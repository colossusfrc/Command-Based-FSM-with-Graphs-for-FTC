package org.firstinspires.ftc.teamcode.commands.intercommands;

import org.firstinspires.ftc.teamcode.CommandBased.Command;
import org.firstinspires.ftc.teamcode.subsystems.stateportable.StatePortableSubsystem;
//exemplo
public class IdleToS2 extends InterCommands {
    public IdleToS2(StatePortableSubsystem statePortableSubsystem){
        super(statePortableSubsystem);
    }
    @Override
    public void execute() {
        statePortableSubsystem.actuateSubsystem(0.1);
    }

    @Override
    public void end(boolean interrupted) {
        statePortableSubsystem.actuateSubsystem(0.0);
    }
}
