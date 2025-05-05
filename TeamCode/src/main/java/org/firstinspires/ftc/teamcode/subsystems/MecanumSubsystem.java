package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RRFeatures.MecanumDrive;
import org.firstinspires.ftc.teamcode.rcfeatures.States;

public class MecanumSubsystem extends MecanumDrive {
    public MecanumSubsystem(HardwareMap hardwareMap, Pose2d pose, LinearOpMode linearOpMode) {
        super(hardwareMap, pose, linearOpMode);
        States.s1(this, 0.1);
        States.s2(this, 0.2);
        States.idle(this, 0.0);
    }
    @Override
    public void actuateSubsystem(States state) {
        double power = state.getRelation(this);
        setPowers(power);

    }

    private void setPowers(double power){
        leftFront.setPower(power);
        leftBack.setPower(power);
        rightBack.setPower(power);
        rightFront.setPower(power);
    }
    @Override
    public void finalAction() {
        setPowers(0);
    }

    @Override
    public void actuateSubsystem(double value) {
        setDrivePowers(
                new PoseVelocity2d(
                        new Vector2d(
                                value, 0.0),
                        0.0));
    }

    @Override
    public void periodic() {
        linearOpMode.telemetry.addData("State:", getState());
    }
}
