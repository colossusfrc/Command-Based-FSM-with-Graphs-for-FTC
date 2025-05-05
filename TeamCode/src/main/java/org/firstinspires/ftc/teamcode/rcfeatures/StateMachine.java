package org.firstinspires.ftc.teamcode.rcfeatures;

public class StateMachine {
    private States state;

    public StateMachine() {
        this.state = States.IDLE;
    }

    public void setState(States hypotheticalState) {
        this.state = contracts(hypotheticalState);

    }

    private States contracts(States hypotheticalState) {
        return hypotheticalState;
    }

    public States getCurrentState() {
        return this.state;
    }

}
