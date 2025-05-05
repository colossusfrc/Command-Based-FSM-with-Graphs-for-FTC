package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.CommandBased.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.CommandBased.Trigger;
import org.firstinspires.ftc.teamcode.commands.composedcommand.MechanumDriveCommand;
import org.firstinspires.ftc.teamcode.commands.generalcommand.IntermediateManagementCommand;
import org.firstinspires.ftc.teamcode.rrbased.LineToXCommand;
import org.firstinspires.ftc.teamcode.subsystems.MecanumSubsystem;
import org.firstinspires.ftc.teamcode.rcfeatures.States;

public class RobotContainer {
    private final MecanumSubsystem mecanumDrive;
    private final LinearOpMode linearOpMode;
    private final Gamepad gamepad;
    public RobotContainer(LinearOpMode linearOpMode){
        this.linearOpMode = linearOpMode;
        this.gamepad = linearOpMode.gamepad1;
        this.mecanumDrive = new MecanumSubsystem(linearOpMode.hardwareMap, new Pose2d(0.0, 0.0, 0.0), linearOpMode);
        configureBindings();
    }
    public void configureBindings(){
        mecanumDrive.setDefaultCommand(
                new MechanumDriveCommand(mecanumDrive,
                        ()->gamepad.left_stick_x,
                        ()->gamepad.left_stick_y,
                        ()->gamepad.right_stick_x)
        );
        new Trigger(()->gamepad.x).toggleOnTrue(
                new SequentialCommandGroup(
                        new LineToXCommand(linearOpMode, mecanumDrive, 10),
                            new LineToXCommand(linearOpMode, mecanumDrive, 20),
                                new LineToXCommand(linearOpMode, mecanumDrive, -10)
                )
        );
        new Trigger(()->gamepad.y).toggleOnTrue(
                IntermediateManagementCommand
                        .intermediateSelector(States.S1, mecanumDrive)
        );
        new Trigger(()->gamepad.a).toggleOnTrue(
                IntermediateManagementCommand
                        .intermediateSelector(States.S2, mecanumDrive)
        );
        new Trigger(()->gamepad.b).toggleOnTrue(
                IntermediateManagementCommand
                        .intermediateSelector(States.IDLE, mecanumDrive)
        );
    }

}