package org.firstinspires.ftc.teamcode.subsystems.stateportable;

import org.firstinspires.ftc.teamcode.ufpackages.CommandBased.SubsystemBase;
import org.firstinspires.ftc.teamcode.rcfeatures.StateMachine;
import org.firstinspires.ftc.teamcode.rcfeatures.States;

public abstract class StatePortableSubsystem extends SubsystemBase {
    private final StateMachine stateMachine;

    protected StatePortableSubsystem() {
        this.stateMachine = new StateMachine();
    }

    public void setState(States state){
        stateMachine.setState(state);
        States calculatedState = stateMachine.getCurrentState();
        actuateSubsystem(calculatedState);
    }
    public States getState(){
        return stateMachine.getCurrentState();
    }
    public abstract void actuateSubsystem(States state);
    public void actuateSubsystem(double value){

    }

    public abstract void finalAction();
    protected void registerSubsystem(StatePortableSubsystem subsystem){
        Robot.getInstance().addSubsystems(subsystem);
    }
}
