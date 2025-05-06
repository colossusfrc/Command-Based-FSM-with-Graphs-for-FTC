package org.firstinspires.ftc.teamcode.commands.generalcommand;

import org.firstinspires.ftc.teamcode.ufpackages.CommandBased.Command;
import org.firstinspires.ftc.teamcode.ufpackages.CommandBased.SelectCommand;
import org.firstinspires.ftc.teamcode.ufpackages.CommandBased.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.subsystems.stateportable.Robot;
import org.firstinspires.ftc.teamcode.rcfeatures.States;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class IntermediateCommandFactory {
    private static SequentialCommandGroup intermediateCommandSelector(States state, Robot subsystem){
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
    public static Command intermediateSelector(States state){
        return new SelectCommand(
                ()->intermediateCommandSelector(state, Robot.getInstance())
        );
    }
}
