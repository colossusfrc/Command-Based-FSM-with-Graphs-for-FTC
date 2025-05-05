package org.firstinspires.ftc.teamcode.commands.generalcommand;

import org.firstinspires.ftc.teamcode.CommandBased.Command;
import org.firstinspires.ftc.teamcode.CommandBased.CommandScheduler;
import org.firstinspires.ftc.teamcode.CommandBased.SelectCommand;
import org.firstinspires.ftc.teamcode.CommandBased.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.commands.composedcommand.MechanumDriveCommand;
import org.firstinspires.ftc.teamcode.subsystems.stateportable.StatePortableSubsystem;
import org.firstinspires.ftc.teamcode.rcfeatures.States;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class IntermediateManagementCommand{
    private static SequentialCommandGroup intermediateManagementCommandFactory(States state, StatePortableSubsystem subsystem){
        try {
            Optional<Command> commandOptional = subsystem.getState().intermediateCommand(state, subsystem);
            if(!commandOptional.isPresent())return new SequentialCommandGroup();
            return new SubsystemCommand(state, subsystem).beforeStarting(commandOptional.get());
        } catch (AssertionError | NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                 InstantiationException e) {
            return new SequentialCommandGroup(
                    new SubsystemCommand(state, subsystem));
        }
    }
    public static Command intermediateSelector(States state, StatePortableSubsystem subsystem){
        return new SelectCommand(
                ()->intermediateManagementCommandFactory(state, subsystem)
        );
    }
}
