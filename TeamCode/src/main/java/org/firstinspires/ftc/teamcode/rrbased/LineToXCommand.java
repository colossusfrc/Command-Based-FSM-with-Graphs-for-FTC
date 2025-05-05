package org.firstinspires.ftc.teamcode.rrbased;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.CommandBased.Command;
import org.firstinspires.ftc.teamcode.RRFeatures.MecanumDrive;

public class LineToXCommand extends Command {
    private final MecanumDrive mecanumDrive;
    private boolean inTrajectory;
    private final double x;
    Action trajectoryActionBuilder;
    LinearOpMode opMode;
    public LineToXCommand(LinearOpMode opMode, MecanumDrive mecanumDrive, double x) {
        this.opMode = opMode;
        this.mecanumDrive = mecanumDrive;
        this.x = x;
        addRequirements(this.mecanumDrive);
    }

    @Override
    protected void initialize() {
        inTrajectory = true;
        trajectoryActionBuilder = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose()).lineToX(x).build();
    }

    @Override
    public void execute() {
        TelemetryPacket telemetryPacket = new TelemetryPacket();
        inTrajectory = trajectoryActionBuilder.run(telemetryPacket);
        opMode.telemetry.addData("In trajectory:", inTrajectory);
    }

    @Override
    public void end(boolean interrupted) {
        mecanumDrive.setDrivePowers(new PoseVelocity2d(new Vector2d(0.0, 0.0), 0.0));
    }

    @Override
    protected boolean isFinished() {
        return !inTrajectory;
    }
}