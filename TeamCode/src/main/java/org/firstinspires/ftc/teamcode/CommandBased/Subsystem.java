package org.firstinspires.ftc.teamcode.CommandBased;


public interface Subsystem {
    default void periodic(){}
    default String getName(){
        return this.getClass().getSimpleName();
    }
    default void setDefaultCommand(org.firstinspires.ftc.teamcode.CommandBased.Command defaultCommand){
        CommandScheduler.getInstance().setDefaultCommand(this, defaultCommand);
    }


}
