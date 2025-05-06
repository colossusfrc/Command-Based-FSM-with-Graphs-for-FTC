package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.rcfeatures.States;
import org.firstinspires.ftc.teamcode.subsystems.stateportable.Robot;
import org.firstinspires.ftc.teamcode.subsystems.stateportable.StatePortableSubsystem;

public class RightChassis extends StatePortableSubsystem {
    private final DcMotor front;
    private final DcMotor back;
    public RightChassis(HardwareMap hardwareMap){
        registerSubsystem(this);
        front = hardwareMap.get(DcMotorEx.class, "frontRight");
        back = hardwareMap.get(DcMotorEx.class, "backRight");
        States.s1(this, 0.1);
        States.s2(this, 0.2);
        States.idle(this, 0.0);
    }

    @Override
    public void actuateSubsystem(States state) {
        actuateSubsystem(state.getRelation(this));
    }

    @Override
    public void actuateSubsystem(double value) {
        front.setPower(value);
        back.setPower(value);
    }

    @Override
    public void finalAction() {
        back.setPower(0.0);
        front.setPower(0.0);
    }
}
