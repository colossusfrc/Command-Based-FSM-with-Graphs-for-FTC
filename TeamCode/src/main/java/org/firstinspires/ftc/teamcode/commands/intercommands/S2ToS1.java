package org.firstinspires.ftc.teamcode.commands.intercommands;

import org.firstinspires.ftc.teamcode.CommandBased.Command;
import org.firstinspires.ftc.teamcode.subsystems.stateportable.StatePortableSubsystem;
//exemplo
public class S2ToS1 extends InterCommands {
    public S2ToS1(StatePortableSubsystem statePortableSubsystem){
        super(statePortableSubsystem);
    }

    @Override
    public void execute() {
       statePortableSubsystem.actuateSubsystem(0.4);
    }

    @Override
    public void end(boolean interrupted) {
        statePortableSubsystem.actuateSubsystem(0.0);
    }

}
