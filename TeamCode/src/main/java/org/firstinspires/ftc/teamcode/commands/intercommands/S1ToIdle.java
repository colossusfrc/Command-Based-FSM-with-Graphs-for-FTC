package org.firstinspires.ftc.teamcode.commands.intercommands;

import org.firstinspires.ftc.teamcode.CommandBased.Command;
import org.firstinspires.ftc.teamcode.subsystems.stateportable.StatePortableSubsystem;
//exemplo
public class S1ToIdle extends InterCommands {
    public S1ToIdle(StatePortableSubsystem statePortableSubsystem){
        super(statePortableSubsystem);
    }

    @Override
    public void execute() {
        statePortableSubsystem.actuateSubsystem(0.3);
    }

    @Override
    public void end(boolean interrupted) {
        statePortableSubsystem.actuateSubsystem(0.0);
    }
}
