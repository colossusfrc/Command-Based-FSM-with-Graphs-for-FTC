package org.firstinspires.ftc.teamcode.commands.composedcommand;

import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;

import org.firstinspires.ftc.teamcode.CommandBased.Command;
import org.firstinspires.ftc.teamcode.RRFeatures.MecanumDrive;
import org.firstinspires.ftc.teamcode.rcfeatures.States;

import java.util.function.DoubleSupplier;

public class MechanumDriveCommand extends Command {
    private final MecanumDrive mecanumDrive;
    private final DoubleSupplier x;
    private final DoubleSupplier y;
    private final DoubleSupplier z;
    public MechanumDriveCommand(MecanumDrive mecanumDrive, DoubleSupplier x, DoubleSupplier y, DoubleSupplier z){
        this.mecanumDrive = mecanumDrive;
        this.x = x;
        this.y = y;
        this.z = z;
        addRequirements(this.mecanumDrive);
    }
    public MechanumDriveCommand(MecanumDrive mecanumDrive, States state){
        this.mecanumDrive = mecanumDrive;
        x = ()->state.getRelation(mecanumDrive);
        y = ()->0.0;
        z = ()->0.0;
    }

    @Override
    public void execute() {
        double xVelocity = x.getAsDouble()*MecanumDrive.PARAMS.maxWheelVel;
        double yVelocity = y.getAsDouble()*MecanumDrive.PARAMS.maxWheelVel;
        double zVelocity = z.getAsDouble()*MecanumDrive.PARAMS.maxAngVel;

        xVelocity*=mecanumDrive.wheelLimiter;
        yVelocity*=mecanumDrive.wheelLimiter;
        zVelocity*=mecanumDrive.headingLimiter;

        Vector2d translation = new Vector2d(xVelocity, yVelocity);
        PoseVelocity2d robotVelocity = new PoseVelocity2d(translation, zVelocity);

        mecanumDrive.setDrivePowers(robotVelocity);
    }

    @Override
    public void end(boolean interrupted) {
        Vector2d translation = new Vector2d(0.0, 0.0);
        PoseVelocity2d robotVelocity = new PoseVelocity2d(translation, 0.0);
        mecanumDrive.setDrivePowers(robotVelocity);
    }
}
