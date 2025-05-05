package org.firstinspires.ftc.teamcode.subsystems.stateportable;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.CommandBased.SubsystemBase;
import org.firstinspires.ftc.teamcode.rcfeatures.StateMachine;
import org.firstinspires.ftc.teamcode.rcfeatures.States;

public abstract class StatePortableSubsystem extends SubsystemBase {
    private final StateMachine stateMachine;
    protected final LinearOpMode linearOpMode;

    protected StatePortableSubsystem(LinearOpMode linearOpMode) {
        this.stateMachine = new StateMachine();
        this.linearOpMode = linearOpMode;
    }
    protected StatePortableSubsystem(){
        this.stateMachine = new StateMachine();
        this.linearOpMode = null;
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
}
