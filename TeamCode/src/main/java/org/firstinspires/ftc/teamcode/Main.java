package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.ufpackages.CommandBased.CommandScheduler;

@TeleOp(name = "MainTeleop")
public class Main extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        new RobotContainer(this);
        waitForStart();
        while (opModeIsActive()){
            CommandScheduler.getInstance().run(this);
        }
    }
}
