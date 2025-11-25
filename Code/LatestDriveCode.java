// Packages & Imports
package org.firstinspires.ftc.teamcode.teamCode;
import static java.lang.Math.abs;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class LatestDriveCode extends LinearOpMode {

    // State Variables
    public double ViperPower = 0;
    public Servo leftMandible;
    public Servo rightMandible;
    public Servo elevateServo;
    public Servo elevateServo2;
    public CRServo beltServo;
    public CRServo headServo;

    @Override
    public void runOpMode() throws InterruptedException {
        
        // Classify Variables
        DcMotor frontLeft = hardwareMap.dcMotor.get("frontLeft");
        DcMotor backLeft = hardwareMap.dcMotor.get("backLeft");
        DcMotor frontRight = hardwareMap.dcMotor.get("frontRight");
        DcMotor backRight = hardwareMap.dcMotor.get("backRight");

        DcMotor leftViper1 = hardwareMap.dcMotor.get("leftViper1");
        DcMotor leftViper2 = hardwareMap.dcMotor.get("leftViper2");
        DcMotor rightViper1 = hardwareMap.dcMotor.get("rightViper1");
        DcMotor rightViper2 = hardwareMap.dcMotor.get("rightViper2");

        leftMandible = hardwareMap.get(Servo.class, "leftMandible");
        rightMandible = hardwareMap.get(Servo.class, "rightMandible");

        headServo = hardwareMap.get(CRServo.class, "headServo");
        //headServo.resetDeviceConfigurationForOpMode();
        elevateServo = hardwareMap.get(Servo.class, "elevateServo");
        elevateServo2 = hardwareMap.get(Servo.class, "elevateServo2");
        

        beltServo = hardwareMap.get(CRServo.class, "beltServo");

        double mandPower = 0;
        
        double speed = 1;
        
        rightMandible.setDirection(Servo.Direction.REVERSE);
        
        leftMandible.setPosition(0);
        rightMandible.setPosition(0);
        elevateServo2.setPosition(1);
        



        // Set Variable Direction
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);

        rightViper1.setDirection(DcMotorSimple.Direction.REVERSE);
        rightViper2.setDirection(DcMotorSimple.Direction.REVERSE);
        leftViper1.setDirection(DcMotorSimple.Direction.REVERSE);
        leftViper2.setDirection(DcMotorSimple.Direction.REVERSE);
        
        


        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {

            //Movement Through Sticks
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            
            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double flPower = (y + x + rx) / denominator;
            double blPower = (y - x + rx) / denominator;
            double frPower = (y - x - rx) / denominator;
            double brPower = (y + x - rx) / denominator;



            // Viper Slides (Vertical)
            if (gamepad2.dpad_up){ // Increase Viper Height
                ViperPower = -1;
            }
            else if(gamepad2.dpad_down){ // Decrease Viper Height
                ViperPower = 1;
            }
            else if (!gamepad2.dpad_down && !gamepad2.dpad_up){ // Keep Current Viper Height
                ViperPower = -0.15;
            }
            
            
            //double ViperPower = gamepad2.left_stick_y; // Not Inverted
            //if (abs(ViperPower) <= 0.3){ 
            //    ViperPower = -0.2;
            //}
            
            
            // Head Rotation (Horizontal)
            if(gamepad2.dpad_right){
                headServo.setPower(-0.2); // Rotate Right
            }
            else if(gamepad2.dpad_left){ // Rotate Left
                headServo.setPower(0.2);
            }
            else{ // Stop Rotation
                headServo.setPower(0);
            }



            // Head Rotation (Vertical)
            if (gamepad2.y){ // Rotate Up
                elevateServo2.setPosition(0.7);
            }
            else if (gamepad2.a){ // Rotate Down
                elevateServo2.setPosition(1);
            }
            // Head Extend (beltServo)
            float beltServoPower = gamepad2.right_stick_y; // Not Inverted
            if (abs(beltServoPower) <= 0.3){ 
                beltServoPower = 0;
            }
            beltServo.setPower(beltServoPower);
            
            

            // Claws
            if (gamepad2.right_bumper){ // Close Claw
                rightMandible.setPosition(0.14); // more positive --> more grippy (before 1, now 0.35)
                leftMandible.setPosition(0.14); // more positive --> more grippy (before 1, now 0.68)
            }
            else if(gamepad2.left_bumper){ // Open Claw
                rightMandible.setPosition(0); // more negative --> less grippy
                leftMandible.setPosition(0); // more negative --> less grippy
            }
            
            if (gamepad1.a) {
                 speed = 0.5;
             }
             
             if (gamepad1.b) {
                 speed = 1;
             }



            frontLeft.setPower(flPower * speed);
            backLeft.setPower(blPower * speed);
            frontRight.setPower(frPower * speed);
            backRight.setPower(brPower * speed);
            
            leftViper2.setPower(ViperPower);
            leftViper1.setPower(ViperPower);
            rightViper1.setPower(ViperPower*-1);
            rightViper2.setPower(ViperPower*-1);
            
            // Update Pos of Vr
            double vr1pos = rightViper1.getCurrentPosition();
            double vl1pos = leftViper2.getCurrentPosition();
            
            // Update Pos of Vr
            double vr2pos = rightViper2.getCurrentPosition();
            double vl2pos = leftViper2.getCurrentPosition();

            telemetry.addData("VrPOS1:", vr1pos);
            telemetry.addData("VrPOS2:", vr2pos);
            telemetry.addData("VlPOS:", vl1pos);
            telemetry.addData("VlPOS:", vl2pos);
            telemetry.addData("ViperPower:", ViperPower);
            telemetry.addData("Fl", flPower);
            telemetry.addData("Fr", frPower);
            telemetry.addData("Bl", blPower);
            telemetry.addData("Br", brPower);
            telemetry.update();
        }
    }
}
