package org.firstinspires.ftc.teamcode.ufpackages.CommandBased;


import com.qualcomm.robotcore.eventloop.EventLoop;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class CommandScheduler implements Sendable, AutoCloseable {
    /** The Singleton Instance. */
    private static CommandScheduler instance;

    /**
     * Returns the Scheduler instance.
     *
     * @return the instance
     */
    public static synchronized CommandScheduler getInstance() {
        if (instance == null) {
            instance = new CommandScheduler();
        }
        return instance;
    }

    private static final Optional<Command> kNoInterruptor = Optional.empty();

    private final Map<Command, Exception> m_composedCommands = new WeakHashMap<>();

    // A set of the currently-running commands.
    public final Set<Command> m_scheduledCommands = new LinkedHashSet<>();

    // A map from required subsystems to their requiring commands. Also used as a set of the
    // currently-required subsystems.
    private final Map<Subsystem, Command> m_requirements = new LinkedHashMap<>();

    // A map from subsystems registered with the scheduler to their default commands.  Also used
    // as a list of currently-registered subsystems.
    private final Map<Subsystem, Command> m_subsystems = new LinkedHashMap<>();

    private final Event m_defaultButtonLoop = new Event();
    // The set of currently-registered buttons that will be polled every iteration.
    private Event m_activeButtonLoop = m_defaultButtonLoop;

    private boolean m_disabled;

    // Lists of user-supplied actions to be executed on scheduling events for every command.
    private final List<Consumer<Command>> m_initActions = new ArrayList<>();
    private final List<Consumer<Command>> m_executeActions = new ArrayList<>();
    private final List<BiConsumer<Command, Optional<Command>>> m_interruptActions = new ArrayList<>();
    private final List<Consumer<Command>> m_finishActions = new ArrayList<>();

    // Flag and queues for avoiding ConcurrentModificationException if commands are
    // scheduled/canceled during run
    private boolean m_inRunLoop;
    private final Set<Command> m_toSchedule = new LinkedHashSet<>();
    private final List<Command> m_toCancelCommands = new ArrayList<>();
    private final List<Optional<Command>> m_toCancelInterruptors = new ArrayList<>();
    private final Set<Command> m_endingCommands = new LinkedHashSet<>();


    CommandScheduler() {}

    /**
     * Changes the period of the loop overrun watchdog. This should be kept in sync with the
     * TimedRobot period.
     *
     * @param period Period in seconds.
     */
    public void setPeriod(double period) {

    }

    @Override
    public void close() {
    }

    /**
     * Get the default button poll.
     *
     * @return a reference to the default {@link EventLoop} object polling buttons.
     */
    public Event getDefaultButtonLoop() {
        return m_defaultButtonLoop;
    }

    /**
     * Get the active button poll.
     *
     * @return a reference to the current {@link EventLoop} object polling buttons.
     */
    public Event getActiveButtonLoop() {
        return m_activeButtonLoop;
    }

    /**
     * Replace the button poll with another one.
     *
     * @param loop the new button polling loop object.
     */
    public void setActiveButtonLoop(Event loop) {
        m_activeButtonLoop =
                loop;
    }

    /**
     * Initializes a given command, adds its requirements to the list, and performs the init actions.
     *
     * @param command The command to initialize
     * @param requirements The command requirements
     */
    private void initCommand(Command command, Set<Subsystem> requirements) {
        m_scheduledCommands.add(command);
        for (Subsystem requirement : requirements) {
            m_requirements.put(requirement, command);
        }
        command.initialize();
        for (Consumer<Command> action : m_initActions) {
            action.accept(command);
        }
    }

    /**
     * Schedules a command for execution. Does nothing if the command is already scheduled. If a
     * command's requirements are not available, it will only be started if all the commands currently
     * using those requirements have been scheduled as interruptible. If this is the case, they will
     * be interrupted and the command will be scheduled.
     *
     * <p>WARNING: using this function directly can often lead to unexpected behavior and should be
     * avoided. Instead Triggers should be used to schedule Commands.
     *
     * @param command the command to schedule. If null, no-op.
     */
    private void schedule(Command command) {
        if (command == null) {
            return;
        }
        if (m_inRunLoop) {
            m_toSchedule.add(command);
            return;
        }

        // Do nothing if the scheduler is disabled, the robot is disabled and the command doesn't
        // run when disabled, or the command is already scheduled.
        if (m_disabled
                || isScheduled(command)) {
            return;
        }

        Set<Subsystem> requirements = command.getRequirements();

        // Schedule the command if the requirements are not currently in-use.
        if (Collections.disjoint(m_requirements.keySet(), requirements)) {
            initCommand(command, requirements);
        } else {
            // Else check if the requirements that are in use have all have interruptible commands,
            // and if so, interrupt those commands and schedule the new command.
            for (Subsystem requirement : requirements) {
                Command requiring = requiring(requirement);
                if (requiring != null
                        && requiring.getInterruptionBehavior() == Command.InterruptBehavior.cancelIncoming) {
                    return;
                }
            }
            for (Subsystem requirement : requirements) {
                Command requiring = requiring(requirement);
                if (requiring != null) {
                    cancel(requiring);
                }
            }
            initCommand(command, requirements);
        }
    }

    /**
     * Schedules multiple commands for execution. Does nothing for commands already scheduled.
     *
     * <p>WARNING: using this function directly can often lead to unexpected behavior and should be
     * avoided. Instead Triggers should be used to schedule Commands.
     *
     * @param commands the commands to schedule. No-op on null.
     */
    public void schedule(Command... commands) {
        for (Command command : commands) {
            schedule(command);
        }
    }

    /**
     * Runs a single iteration of the scheduler. The execution occurs in the following order:
     *
     * <p>Subsystem periodic methods are called.
     *
     * <p>Button bindings are polled, and new commands are scheduled from them.
     *
     * <p>Currently-scheduled commands are executed.
     *
     * <p>End conditions are checked on currently-scheduled commands, and commands that are finished
     * have their end methods called and are removed.
     *
     * <p>Any subsystems not being used as requirements have their default methods started.
     */
    public void run(LinearOpMode opMode) {
        if (m_disabled) {
            return;
        }

        // Run the periodic method of all registered subsystems.
        for (Subsystem subsystem : m_subsystems.keySet()) {
            subsystem.periodic();
        }

        // Cache the active instance to avoid concurrency problems if setActiveLoop() is called from
        // inside the button bindings.
        Event loopCache = m_activeButtonLoop;
        // Poll buttons for new commands to add.
        loopCache.poll();

        m_inRunLoop = true;
        boolean isDisabled = false;
        // Run scheduled commands, remove finished commands.
        for (Iterator<Command> iterator = m_scheduledCommands.iterator(); iterator.hasNext(); ) {
            Command command = iterator.next();
            opMode.telemetry.addData("command", command.getClass().getSimpleName());
            if (isDisabled && !command.runsWhenDisabled()) {
                cancel(command);
                continue;
            }

            command.execute();
            for (Consumer<Command> action : m_executeActions) {
                action.accept(command);
            }
            if (command.isFinished()) {
                m_endingCommands.add(command);
                command.end(false);
                for (Consumer<Command> action : m_finishActions) {
                    action.accept(command);
                }
                m_endingCommands.remove(command);
                iterator.remove();

                m_requirements.keySet().removeAll(command.getRequirements());
            }
        }
        m_inRunLoop = false;

        // Schedule/cancel commands from queues populated during loop
        for (Command command : m_toSchedule) {
            schedule(command);
        }

        for (int i = 0; i < m_toCancelCommands.size(); i++) {
            cancel(m_toCancelCommands.get(i));
        }

        m_toSchedule.clear();
        m_toCancelCommands.clear();
        m_toCancelInterruptors.clear();

        // Add default commands for un-required registered subsystems.
        for (Map.Entry<Subsystem, Command> subsystemCommand : m_subsystems.entrySet()) {
            if (!m_requirements.containsKey(subsystemCommand.getKey())
                    && subsystemCommand.getValue() != null) {
                schedule(subsystemCommand.getValue());
            }
        }
    }

    /**
     * Registers subsystems with the scheduler. This must be called for the subsystem's periodic block
     * to run when the scheduler is run, and for the subsystem's default command to be scheduled. It
     * is recommended to call this from the constructor of your subsystem implementations.
     *
     * @param subsystems the subsystem to register
     */
    public void registerSubsystem(Subsystem... subsystems) {
        for (Subsystem subsystem : subsystems) {
            if (subsystem == null) {
                continue;
            }
            if (m_subsystems.containsKey(subsystem)) {
                continue;
            }
            m_subsystems.put(subsystem, null);
        }
    }

    /**
     * Un-registers subsystems with the scheduler. The subsystem will no longer have its periodic
     * block called, and will not have its default command scheduled.
     *
     * @param subsystems the subsystem to un-register
     */
    public void unregisterSubsystem(Subsystem... subsystems) {
        m_subsystems.keySet().removeAll(Set.of(subsystems));
    }

    /**
     * Un-registers all registered Subsystems with the scheduler. All currently registered subsystems
     * will no longer have their periodic block called, and will not have their default command
     * scheduled.
     */
    public void unregisterAllSubsystems() {
        m_subsystems.clear();
    }

    /**
     * Sets the default command for a subsystem. Registers that subsystem if it is not already
     * registered. Default commands will run whenever there is no other command currently scheduled
     * that requires the subsystem. Default commands should be written to never end (i.e. their {@link
     * Command#isFinished()} method should return false), as they would simply be re-scheduled if they
     * do. Default commands must also require their subsystem.
     *
     * @param subsystem the subsystem whose default command will be set
     * @param defaultCommand the default command to associate with the subsystem
     */
    public void setDefaultCommand(Subsystem subsystem, Command defaultCommand) {
        if (subsystem == null) {
            return;
        }
        if (defaultCommand == null) {
            return;
        }

        if (!defaultCommand.getRequirements().contains(subsystem)) {
            throw new IllegalArgumentException("Default commands must require their subsystem!");
        }

        m_subsystems.put(subsystem, defaultCommand);
    }

    /**
     * Removes the default command for a subsystem. The current default command will run until another
     * command is scheduled that requires the subsystem, at which point the current default command
     * will not be re-scheduled.
     *
     * @param subsystem the subsystem whose default command will be removed
     */
    public void removeDefaultCommand(Subsystem subsystem) {
        if (subsystem == null) {
            return;
        }

        m_subsystems.put(subsystem, null);
    }

    /**
     * Gets the default command associated with this subsystem. Null if this subsystem has no default
     * command associated with it.
     *
     * @param subsystem the subsystem to inquire about
     * @return the default command associated with the subsystem
     */
    public Command getDefaultCommand(Subsystem subsystem) {
        return m_subsystems.get(subsystem);
    }

    public void cancel(Command... commands) {
        for (Command command : commands) {
            cancel(command);
        }
    }

    private void cancel(Command command) {
        if (command == null) {
            return;
        }
        if (m_endingCommands.contains(command)) {
            return;
        }
        if (m_inRunLoop) {
            m_toCancelCommands.add(command);
            return;
        }
        if (!isScheduled(command)) {
            return;
        }

        m_endingCommands.add(command);
        command.end(true);
        m_endingCommands.remove(command);
        m_scheduledCommands.remove(command);
        m_requirements.keySet().removeAll(command.getRequirements());
    }

    /** Cancels all commands that are currently scheduled. */
    public void cancelAll() {
        // Copy to array to avoid concurrent modification.
        cancel(m_scheduledCommands.toArray(new Command[0]));
    }

    /**
     * Whether the given commands are running. Note that this only works on commands that are directly
     * scheduled by the scheduler; it will not work on commands inside compositions, as the scheduler
     * does not see them.
     *
     * @param commands multiple commands to check
     * @return whether all of the commands are currently scheduled
     */
    public boolean isScheduled(Command... commands) {
        for (Command cmd : commands) {
            if (!isScheduled(cmd)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Whether the given commands are running. Note that this only works on commands that are directly
     * scheduled by the scheduler; it will not work on commands inside compositions, as the scheduler
     * does not see them.
     *
     * @param command a single command to check
     * @return whether all of the commands are currently scheduled
     */
    public boolean isScheduled(Command command) {
        return m_scheduledCommands.contains(command);
    }

    /**
     * Returns the command currently requiring a given subsystem. Null if no command is currently
     * requiring the subsystem
     *
     * @param subsystem the subsystem to be inquired about
     * @return the command currently requiring the subsystem, or null if no command is currently
     *     scheduled
     */
    public Command requiring(Subsystem subsystem) {
        return m_requirements.get(subsystem);
    }

    /** Disables the command scheduler. */
    public void disable() {
        m_disabled = true;
    }

    /** Enables the command scheduler. */
    public void enable() {
        m_disabled = false;
    }

    /** Prints list of epochs added so far and their times. */
    public void printWatchdogEpochs() {
    }

    /**
     * Adds an action to perform on the initialization of any command by the scheduler.
     *
     * @param action the action to perform
     */
    public void onCommandInitialize(Consumer<Command> action) {
        m_initActions.add(action);
    }

    /**
     * Adds an action to perform on the execution of any command by the scheduler.
     *
     * @param action the action to perform
     */
    public void onCommandExecute(Consumer<Command> action) {
        m_executeActions.add(action);
    }

    /**
     * Adds an action to perform on the interruption of any command by the scheduler.
     *
     * @param action the action to perform
     */
    public void onCommandInterrupt(Consumer<Command> action) {
        m_interruptActions.add((command, interruptor) -> action.accept(command));
    }

    /**
     * Adds an action to perform on the interruption of any command by the scheduler. The action
     * receives the interrupted command and an Optional containing the interrupting command, or
     * Optional.empty() if it was not canceled by a command (e.g., by {@link
     * CommandScheduler#cancel}).
     *
     * @param action the action to perform
     */
    public void onCommandInterrupt(BiConsumer<Command, Optional<Command>> action) {
        m_interruptActions.add(action);
    }

    /**
     * Adds an action to perform on the finishing of any command by the scheduler.
     *
     * @param action the action to perform
     */
    public void onCommandFinish(Consumer<Command> action) {
        m_finishActions.add(action);
    }

    /**
     * Register commands as composed. An exception will be thrown if these commands are scheduled
     * directly or added to a composition.
     *
     * @param commands the commands to register
     * @throws IllegalArgumentException if the given commands have already been composed, or the array
     *     of commands has duplicates.
     */
    public void registerComposedCommands(Command... commands) {
        Set<Command> commandSet;
        try {
            commandSet = Set.of(commands);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Cannot compose a command twice in the same composition! (Original exception: "
                            + e
                            + ")");
        }
        Exception exception = new Exception("Originally composed at:");
        exception.fillInStackTrace();
        for (Command command : commands) {
            m_composedCommands.put(command, exception);
        }
    }

    /**
     * Clears the list of composed commands, allowing all commands to be freely used again.
     *
     * <p>WARNING: Using this haphazardly can result in unexpected/undesirable behavior. Do not use
     * this unless you fully understand what you are doing.
     */
    public void clearComposedCommands() {
        m_composedCommands.clear();
    }

    /**
     * Removes a single command from the list of composed commands, allowing it to be freely used
     * again.
     *
     * <p>WARNING: Using this haphazardly can result in unexpected/undesirable behavior. Do not use
     * this unless you fully understand what you are doing.
     *
     * @param command the command to remove from the list of grouped commands
     */
    public void removeComposedCommand(Command command) {
        m_composedCommands.remove(command);
    }

    /**
     * Strip additional leading stack trace elements that are in the framework package.
     *
     * @param stacktrace the original stacktrace
     * @return the stacktrace stripped of leading elements so there is at max one leading element from
     *     the edu.wpi.first.wpilibj2.command package.
     */
    private StackTraceElement[] stripFrameworkStackElements(StackTraceElement[] stacktrace) {
        int i = stacktrace.length - 1;
        for (; i > 0; i--) {
            if (stacktrace[i].getClassName().startsWith("edu.wpi.first.wpilibj2.command.")) {
                break;
            }
        }
        return Arrays.copyOfRange(stacktrace, i, stacktrace.length);
    }


    /**
     * Requires that the specified command hasn't already been added to a composition.
     *
     * @param commands The commands to check
     * @throws IllegalArgumentException if the given commands have already been composed.
     */
    public void requireNotComposed(Command... commands) throws Exception {
        for (Command command : commands) {
            Exception exception = m_composedCommands.getOrDefault(command, null);
            if (exception != null) {
                exception.setStackTrace(stripFrameworkStackElements(exception.getStackTrace()));
                StringWriter buffer = new StringWriter();
                PrintWriter writer = new PrintWriter(buffer);
                writer.println(
                        "Commands that have been composed may not be added to another composition or scheduled "
                                + "individually!");
                exception.printStackTrace(writer);
                Exception thrownException = new IllegalArgumentException(buffer.toString());
                thrownException.setStackTrace(stripFrameworkStackElements(thrownException.getStackTrace()));
                throw thrownException;
            }
        }
    }

    /**
     * Requires that the specified commands have not already been added to a composition.
     *
     * @param commands The commands to check
     * @throws IllegalArgumentException if the given commands have already been composed.
     */

    /**
     * Requires that the specified command hasn't already been added to a composition, and is not
     * currently scheduled.
     *
     * @param command The command to check
     * @throws IllegalArgumentException if the given command has already been composed or scheduled.
     */

    /**
     * Requires that the specified commands have not already been added to a composition, and are not
     * currently scheduled.
     *
     * @param commands The commands to check
     * @throws IllegalArgumentException if the given commands have already been composed or scheduled.
     */

    /**
     * Check if the given command has been composed.
     *
     * @param command The command to check
     * @return true if composed
     */
    public boolean isComposed(Command command) {
        return getComposedCommands().contains(command);
    }

    Set<Command> getComposedCommands() {
        return m_composedCommands.keySet();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
    }
}
