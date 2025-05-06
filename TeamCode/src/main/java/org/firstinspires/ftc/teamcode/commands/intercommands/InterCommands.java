package org.firstinspires.ftc.teamcode.commands.intercommands;

import org.firstinspires.ftc.teamcode.ufpackages.CommandBased.Command;
import org.firstinspires.ftc.teamcode.subsystems.stateportable.Robot;

public class InterCommands extends Command {

    protected final Robot statePortableSubsystem;
    public InterCommands(Robot statePortableSubsystem){
        this.statePortableSubsystem = statePortableSubsystem;
        addRequirements(statePortableSubsystem);
    }

}
