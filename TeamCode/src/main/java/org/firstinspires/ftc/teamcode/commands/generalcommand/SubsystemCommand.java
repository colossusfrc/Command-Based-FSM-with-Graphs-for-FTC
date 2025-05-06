package org.firstinspires.ftc.teamcode.commands.generalcommand;

import org.firstinspires.ftc.teamcode.ufpackages.CommandBased.Command;
import org.firstinspires.ftc.teamcode.rcfeatures.States;
import org.firstinspires.ftc.teamcode.subsystems.stateportable.StatePortableSubsystem;

public class SubsystemCommand extends Command {

    private final StatePortableSubsystem subsystem;
    private final States state;
    public SubsystemCommand(States state, StatePortableSubsystem subsystems) {
        this.subsystem = subsystems;
        this.state = state;
        addRequirements(this.subsystem);
    }

    @Override
    public void execute() {
        subsystem.setState(state);
    }

    @Override
    public void end(boolean interrupted) {
        subsystem.finalAction();
    }
}
