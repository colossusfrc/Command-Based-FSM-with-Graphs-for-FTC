package org.firstinspires.ftc.teamcode.rcfeatures;

import org.firstinspires.ftc.teamcode.ufpackages.CommandBased.Command;
import org.firstinspires.ftc.teamcode.commands.intercommands.*;
import org.firstinspires.ftc.teamcode.subsystems.stateportable.Robot;
import org.firstinspires.ftc.teamcode.subsystems.stateportable.StatePortableSubsystem;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Optional;

public enum States {
    //exemplo
    S1,
    S2,
    IDLE;

    private final HashMap<StatePortableSubsystem, Double> dataRelations = new HashMap<>();
    private final HashMap<States, Class<?>> intermediateCommands = new HashMap<>();
    static {
        //exemplo
        S1.intermediateCommands.put(S2, S1ToS2.class);
        S1.intermediateCommands.put(IDLE, S1ToIdle.class);

        S2.intermediateCommands.put(S1, S2ToS1.class);
        S2.intermediateCommands.put(IDLE, S2ToIdle.class);

        IDLE.intermediateCommands.put(S1, IdleToS1.class);
        IDLE.intermediateCommands.put(S2, IdleToS2.class);
    }
    public Optional<Command> intermediateCommand(States hypotheticalState,
                                                 Robot statePortableSubsystem)
            throws NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            InstantiationException {
            Class<?> c = this.intermediateCommands.get(hypotheticalState);
            assert c != null;
            Command intermediateCommand = (Command) c.
                    getConstructor(Robot.class).
                    newInstance(statePortableSubsystem);
            return Optional.of(intermediateCommand.withTimeout(2));
    }
    public Optional<Command> intermediateCommand(States hypotheticalState,
                                                 StatePortableSubsystem statePortableSubsystem)
            throws NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            InstantiationException {
        Class<?> c = this.intermediateCommands.get(hypotheticalState);
        assert c != null;
        Command intermediateCommand = (Command) c.
                getConstructor(StatePortableSubsystem.class).
                newInstance(statePortableSubsystem);
        return Optional.of(intermediateCommand.withTimeout(2));
    }
    private void relate(StatePortableSubsystem subsystemToRelate, Double relation){
        dataRelations.put(subsystemToRelate, relation);
    }
    public Double getRelation(StatePortableSubsystem subsystem){
        return dataRelations.get(subsystem);
    }
    public static void s1(StatePortableSubsystem subsystemToRelate, Double relation){
        S1.relate(subsystemToRelate, relation);
    }
    public static void s2(StatePortableSubsystem subsystemToRelate, Double relation){
        S2.relate(subsystemToRelate, relation);
    }
    public static void idle(StatePortableSubsystem subsystemToRelate, Double relation){
        IDLE.relate(subsystemToRelate, relation);
    }
}
