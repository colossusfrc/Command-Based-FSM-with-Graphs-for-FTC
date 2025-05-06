package org.firstinspires.ftc.teamcode.commands.intercommands;

import org.firstinspires.ftc.teamcode.subsystems.LeftChassis;
import org.firstinspires.ftc.teamcode.subsystems.RightChassis;
import org.firstinspires.ftc.teamcode.subsystems.stateportable.Robot;

//exemplo
public class S1ToIdle extends InterCommands {
    public S1ToIdle(Robot statePortableSubsystem){
        super(statePortableSubsystem);
    }

    @Override
    public void execute() {

        statePortableSubsystem.getByClass(LeftChassis.class).actuateSubsystem(0.1);
        statePortableSubsystem.getByClass(RightChassis.class).actuateSubsystem(0.1);

    }

    @Override
    public void end(boolean interrupted) {
        statePortableSubsystem.actuateSubsystem(0.0);
    }
}
