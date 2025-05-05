package org.firstinspires.ftc.teamcode.commands.intercommands;

import org.firstinspires.ftc.teamcode.CommandBased.Command;
import org.firstinspires.ftc.teamcode.subsystems.stateportable.StatePortableSubsystem;

public class InterCommands extends Command {

    protected final StatePortableSubsystem statePortableSubsystem;
    public InterCommands(StatePortableSubsystem statePortableSubsystem){
        this.statePortableSubsystem = statePortableSubsystem;
        addRequirements(statePortableSubsystem);
    }

}
