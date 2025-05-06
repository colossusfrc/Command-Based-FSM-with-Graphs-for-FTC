package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.ufpackages.CommandBased.SequentialCommandGroup;
import org.firstinspires.ftc.teamcode.ufpackages.CommandBased.Trigger;
import org.firstinspires.ftc.teamcode.commands.composedcommand.MechanumDriveCommand;
import org.firstinspires.ftc.teamcode.commands.generalcommand.IntermediateCommandFactory;
import org.firstinspires.ftc.teamcode.ufpackages.rrbased.LineToXCommand;
import org.firstinspires.ftc.teamcode.subsystems.LeftChassis;
import org.firstinspires.ftc.teamcode.subsystems.MecanumSubsystem;
import org.firstinspires.ftc.teamcode.rcfeatures.States;
import org.firstinspires.ftc.teamcode.subsystems.RightChassis;

public class RobotContainer {
    private final MecanumSubsystem mecanumDrive;
    private final LinearOpMode linearOpMode;
    private final Gamepad gamepad;
    public RobotContainer(LinearOpMode linearOpMode){
        this.linearOpMode = linearOpMode;
        this.gamepad = linearOpMode.gamepad1;
        new LeftChassis(linearOpMode.hardwareMap);
        new RightChassis(linearOpMode.hardwareMap);
        this.mecanumDrive = new MecanumSubsystem(linearOpMode.hardwareMap, new Pose2d(0, 0, 0), linearOpMode);
        configureBindings();
    }
    public void configureBindingsTestMultipleSubsystems(){
        /*new Trigger(()->gamepad.y).toggleOnTrue(
                IntermediateCommandFactory
                        .intermediateSelector(States.S1, robot)
        );
        new Trigger(()->gamepad.a).toggleOnTrue(
                IntermediateCommandFactory
                        .intermediateSelector(States.S2, robot)
        );
        new Trigger(()->gamepad.b).toggleOnTrue(
                IntermediateCommandFactory
                        .intermediateSelector(States.IDLE, robot)
        );*/
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
                IntermediateCommandFactory
                        .intermediateSelector(States.S1)
        );
        new Trigger(()->gamepad.a).toggleOnTrue(
                IntermediateCommandFactory
                        .intermediateSelector(States.S2)
        );
        new Trigger(()->gamepad.b).toggleOnTrue(
                IntermediateCommandFactory
                        .intermediateSelector(States.IDLE)
        );
    }

}