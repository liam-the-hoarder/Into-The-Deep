package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp; 
import com.qualcomm.robotcore.hardware.TouchSensor;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode; 
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp

public class Field_Centric extends LinearOpMode {
    @Override 
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor fl = hardwareMap.dcMotor.get("fl");
        DcMotor bl = hardwareMap.dcMotor.get("bl");
        DcMotor fr = hardwareMap.dcMotor.get("fr");
        DcMotor br = hardwareMap.dcMotor.get("br");
        DcMotor worm = hardwareMap.dcMotor.get("worm");
        DcMotor viper = hardwareMap.dcMotor.get("viper");
        

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);
        
        viper.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        worm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        // retrieve the IMU from the hardware map
        IMU imu = hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);
        
        
        TouchSensor touch;
        DcMotor motor;
        
        
        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {// todo: write your code here
            double y = -gamepad1.left_stick_y; // remember, Y stick is reversed!
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            double y2 = gamepad2.left_stick_y;
            double x2 = gamepad2.right_stick_y;
            
            
            //This button choice was made so that it is hard to hit on accident,
            // it can be freely changed based on preference.
            // The equivalent button is start on Xbox-style controllers.
            if (gamepad1.start) {
                imu.resetYaw();
            }
            
            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            
            // Rotate the movement direction counter to the bot's rotation
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);
            
            rotX = rotX * 1.1; // Counteract imperfect strafing

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all powers maintain the same ratio, but only when
            // at least one is out of the range [-1,1]
            
           
            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            double flPower = (rotY + rotX + rx) / denominator ;
            double blPower = (rotY - rotX + rx) / denominator ;
            double frPower = (rotY - rotX - rx) / denominator ;
            double brPower = (rotY + rotX - rx) / denominator ;
            double wormPower = y2;
            double viperPower = x2;
            
            
            
            fl.setPower(flPower);
            bl.setPower(blPower);
            fr.setPower(frPower);
            br.setPower(brPower);
            worm.setPower(wormPower);
            viper.setPower(viperPower);
            
            // Get the touch sensor and motor from hardwareMap
            touch = hardwareMap.get(TouchSensor.class, "Touch");
            motor = hardwareMap.get(DcMotor.class, "Motor");
        
            // Wait for the play button to be pressed
            
            // If the touch sensor is pressed, stop the motor
            if (touch.isPressed()) {
                motor.setPower(1);
                wormPower = y2 * 1; 
            } else { // Otherwise, run the motor
                motor.setPower(0);
            }
        
    

            
            
            telemetry.update();
            telemetry.addData("Fl", flPower);
            telemetry.addData("Fr", frPower);
            telemetry.addData("Bl", blPower);
            telemetry.addData("Br", brPower);
            telemetry.addData("Worm", wormPower);
            telemetry.addData("Viper", viperPower);

            
        }

    }
        
}

