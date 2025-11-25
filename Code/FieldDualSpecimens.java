package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp
public class FieldDualSpecimens extends LinearOpMode {
    DcMotorEx worm;
    
    private enum LeftTriggerState {
        IDLE,
        PICK_POWER,
        VIPER_DOWN,
        FLIP_START,
        DFLIP_DOWN,
        VIPER_UP,
        FLIP_FINAL,
        CLAW_ADJUST,
        PCLAW_CLOSE,
        FLIP_RESET,
        DFLIP_RESET,
        COMPLETE
    }

    private LeftTriggerState leftTriggerState = LeftTriggerState.IDLE;
    private long leftTriggerStartTime = 0;



    private enum RightTriggerState {
        IDLE,
        START_POSITION,
        FLIP_POSITION,
        FINAL_POSITION
}

    private RightTriggerState rightTriggerState = RightTriggerState.IDLE;
    private long rightTriggerStartTime = 0;
    
    private enum RightBumperState {
    IDLE,
    FLIP_POSITION,
    CLAW_CLOSE
    }

    private RightBumperState rightBumperState = RightBumperState.IDLE;
    private long rightBumperStartTime = 0;


    
    
    private DcMotor viper1;
    private DcMotor viper2;
    
    private DcMotor bl;
    
    @Override
    public void runOpMode() throws InterruptedException {
        
        worm = hardwareMap.get(DcMotorEx.class, "worm");
        
        // Reset the encoder during initialization
        worm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        
        DcMotor fl = hardwareMap.dcMotor.get("fl");
        DcMotor bl = hardwareMap.dcMotor.get("bl");
        DcMotor fr = hardwareMap.dcMotor.get("fr");
        DcMotor br = hardwareMap.dcMotor.get("br");
        worm = hardwareMap.get(DcMotorEx.class, "worm");
        DcMotor viper1 = hardwareMap.dcMotor.get("viper1");
        DcMotor viper2 = hardwareMap.dcMotor.get("viper2");
        DcMotor pick = hardwareMap.dcMotor.get("pick");
        Servo pClaw = hardwareMap.servo.get("pClaw");
        Servo flip = hardwareMap.servo.get("flip");
        Servo dflip = hardwareMap.servo.get("dflip");
        Servo dclaw = hardwareMap.servo.get("dclaw");
        
        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // see the note about this erlier on this page.
        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);
        viper1.setDirection(DcMotorSimple.Direction.REVERSE);
        viper2.setDirection(DcMotorSimple.Direction.REVERSE);

        // Retrieve the IMU from the hardware map
        IMU imuExpan = hardwareMap.get(IMU.class, "imuExpan");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imuExpan.initialize(parameters);
        
        worm.setTargetPosition(0);
        
        // Switch to RUN_TO_POSITION mode
        worm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        //worm.setTargetPosition(800);
        //worm.setVelocity(1000);
        
        
        pClaw.setPosition(0.15);
        flip.setPosition(0);
        dclaw.setPosition(0);
        dflip.setPosition(0.95);
        
        
        viper1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        viper2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
       double speed = 1;
       
       boolean slpPause = false;
     
        telemetry.addData("status", "intialized");
        telemetry.update();
        
        
        waitForStart();
    
        
        if (isStopRequested()) {
            worm.setTargetPosition(0);
            worm.setVelocity(1000);
            sleep(1500);
            return;
        }
        
        while(opModeIsActive()) {
            
            
              
            if (gamepad1.start) {
                //imuExpan.initialize(parameters);
                imuExpan.resetYaw();
            }
           
            
            
            double y = -gamepad1.left_stick_y; //remember, y stick value is reversed
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x * 0.6;
            double y2 = gamepad2.left_stick_y * 0.7;
            double x2 = gamepad2.right_stick_y;
           
            
            // Rotate the movement direction counter to the bot's rotations
            double botHeading = imuExpan.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            
            // Rotate the movement direction counter to the bot's rotation
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);
            
            rotX = rotX * 1.1; // Counteract imperfect strafing
            
            // Denominator is the largest motor power (absolute Value) or 1 
            // This  ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            
            
            
            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            double flPower = (rotY + rotX + rx) / denominator ;
            double blPower = (rotY - rotX + rx) / denominator ;
            double frPower = (rotY - rotX - rx) / denominator ;
            double brPower = (rotY + rotX - rx) / denominator ;
            
            
            double pickPower = y2 ;
            double viper1Power = x2;
            double viper2Power = x2;
            
            
               
            
            if (gamepad2.x) {
                slpPause = true;
                flPower = 0;
                blPower = 0;
                frPower = 0;
                brPower = 0;
            }
            
            if (gamepad2.dpad_left) {
                slpPause = false;
            } 
            
            if (gamepad2.right_bumper && rightBumperState == RightBumperState.IDLE) {
                rightBumperState = RightBumperState.FLIP_POSITION;
                rightBumperStartTime = System.currentTimeMillis();
            }

            switch (rightBumperState) {
                case FLIP_POSITION:
                    flip.setPosition(0.205);
                    if (System.currentTimeMillis() - rightBumperStartTime > 100) {
                        rightBumperState = RightBumperState.CLAW_CLOSE;
                        rightBumperStartTime = System.currentTimeMillis();
                    }
                    break;

                case CLAW_CLOSE:
                    pClaw.setPosition(0.2);
                    rightBumperState = RightBumperState.IDLE; // Sequence is complete
                    break;

                case IDLE:
                default:
                break;
            }

            
            
                     

                if (gamepad2.right_trigger == 1 && rightTriggerState == RightTriggerState.IDLE) {
                    rightTriggerState = RightTriggerState.START_POSITION;
                    rightTriggerStartTime = System.currentTimeMillis();
                }

            switch (rightTriggerState) {
                case START_POSITION:
                    pClaw.setPosition(0.15);
                    if (System.currentTimeMillis() - rightTriggerStartTime > 40) {
                        rightTriggerState = RightTriggerState.FLIP_POSITION;
                        rightTriggerStartTime = System.currentTimeMillis();
                    }
                    break;

                case FLIP_POSITION:
                    flip.setPosition(0.19);
                    if (System.currentTimeMillis() - rightTriggerStartTime > 550) {
                        rightTriggerState = RightTriggerState.FINAL_POSITION;
                        rightTriggerStartTime = System.currentTimeMillis();
                    }
                    break;

                case FINAL_POSITION:
                    pClaw.setPosition(0);
                    flip.setPosition(0.19); // Adjust this if needed for your logic
                    rightTriggerState = RightTriggerState.IDLE;
                    slpPause = true; // This matches your existing logic
                    break;

                case IDLE:
                default:
                break;
            }
            
            
            
            if (gamepad2.left_trigger == 1 && leftTriggerState == LeftTriggerState.IDLE) {
                leftTriggerState = LeftTriggerState.PICK_POWER;
                leftTriggerStartTime = System.currentTimeMillis();
            }

            switch (leftTriggerState) {
                case PICK_POWER:
                    // Keep motor power on for 600ms
                    while (System.currentTimeMillis() - leftTriggerStartTime <= 600) {
                        pick.setPower(1);
                    }
                    pick.setPower(0); // Stop motor after loop ends
                    leftTriggerState = LeftTriggerState.VIPER_DOWN;
                    leftTriggerStartTime = System.currentTimeMillis();
                    break;

                case VIPER_DOWN:
                    // Keep motor power on for 250ms
                    while (System.currentTimeMillis() - leftTriggerStartTime <= 350) {
                        viper1.setPower(-1);
                        viper2.setPower(-1);
                    }
                    viper1.setPower(0); // Stop motors after loop ends
                    viper2.setPower(0);
                    dclaw.setPosition(0);
                    flip.setPosition(0);
                    leftTriggerState = LeftTriggerState.FLIP_START;
                    leftTriggerStartTime = System.currentTimeMillis();
                    break;

                case FLIP_START:
                    if (System.currentTimeMillis() - leftTriggerStartTime > 2600) {
                        dflip.setPosition(0.95);
                        leftTriggerState = LeftTriggerState.DFLIP_DOWN;
                        leftTriggerStartTime = System.currentTimeMillis();
                    }
                    break;

                case DFLIP_DOWN:
                    dflip.setPosition(0.68);
                    if (System.currentTimeMillis() - leftTriggerStartTime > 200) {
                        leftTriggerState = LeftTriggerState.VIPER_UP;
                        leftTriggerStartTime = System.currentTimeMillis();
                    }
                    break;

                case VIPER_UP:
                    // Keep motor power on for 170ms
                    while (System.currentTimeMillis() - leftTriggerStartTime <= 170) {
                        viper1.setPower(0.8);
                        viper2.setPower(0.8);
                    }
                    viper1.setPower(0); // Stop motors after loop ends
                    viper2.setPower(0);
                    leftTriggerState = LeftTriggerState.FLIP_FINAL;
                    leftTriggerStartTime = System.currentTimeMillis();
                    break;

                case FLIP_FINAL:
                    flip.setPosition(0.04);
                    if (System.currentTimeMillis() - leftTriggerStartTime > 180) {
                        dclaw.setPosition(0.23);
                        leftTriggerState = LeftTriggerState.CLAW_ADJUST;
                        leftTriggerStartTime = System.currentTimeMillis();
                    }
                    break;

                case CLAW_ADJUST:
                    pClaw.setPosition(0);
                    if (System.currentTimeMillis() - leftTriggerStartTime > 110) {
                        flip.setPosition(0);
                        leftTriggerState = LeftTriggerState.FLIP_RESET;
                        leftTriggerStartTime = System.currentTimeMillis();
                    }
                    break;

                case FLIP_RESET:
                    dflip.setPosition(0.95);
                    if (System.currentTimeMillis() - leftTriggerStartTime > 80) {
                        leftTriggerState = LeftTriggerState.PCLAW_CLOSE;
                        leftTriggerStartTime = System.currentTimeMillis();
                    }
                    break;

                case PCLAW_CLOSE:
                    pClaw.setPosition(0.15);
                    leftTriggerState = LeftTriggerState.IDLE; // End sequence
                    break;

                case IDLE:
                default:
                    break;
            }






    
            
            if (gamepad2.left_bumper) {
                dclaw.setPosition(0);
            }
            
            //if (gamepad2.left_trigger == 1) {
               // viper.setPower(-1);
                //sleep(150);
                //viper.setPower(0);
                //sleep(40);
                //flip.setPosition(0.24);
                //sleep(550);
                //pClaw.setPosition(0);
    
            //}
            
            if (gamepad2.dpad_up) {


                pClaw.setPosition(0);
                sleep(200);
                pClaw.setPosition(0.15);
            
            }
            
           if (gamepad2.a) {
  
  
                pick.setPower(0.7);
                Thread.sleep(600);
                viper1.setPower(-1);
                viper2.setPower(-1);
                Thread.sleep(450);
                viper1.setPower(0);
                viper2.setPower(0);
                dclaw.setPosition(0);
                flip.setPosition(0);
                Thread.sleep(2600);
                dflip.setPosition(0.95);
                Thread.sleep(100);
                dflip.setPosition(0.7);
                Thread.sleep(200);
                viper1.setPower(1);
                viper2.setPower(1);
                Thread.sleep(450);
                viper1.setPower(0);
                viper2.setPower(0);
                Thread.sleep(250);
                flip.setPosition(0.045);
                Thread.sleep(250);
                dclaw.setPosition(0.28);
                Thread.sleep(40);
                pClaw.setPosition(0);
                Thread.sleep(110);
                flip.setPosition(0);
                Thread.sleep(100);
                dflip.setPosition(0.95);
                Thread.sleep(80);
                pClaw.setPosition(0.15);
                slpPause = true; 
               
           }
            
            
            
            if (gamepad2.b) {
                dclaw.setPosition(0.23);
            }
            
            
            
            //if (gamepad2.y) {
              //flip.setPosition(0.25);
              //sleep(200);
              //pClaw.setPosition(0);
              
              
            //}
            
            
            
            //if (gamepad2.dpad_left) {

                //worm.setTargetPosition(700);
                //worm.setVelocity(700);
                //Thread.sleep(1500);
                //worm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                //Thread.sleep(100);
                //worm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                 
              
            //}
            
            //if (gamepad2.dpad_up) {
  
                //worm.setTargetPosition(-800);
                //worm.setVelocity(-700);
                //Thread.sleep(1500);
                //worm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                //Thread.sleep(100);
                //worm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                 
            //}
           
            
            
            
            if (gamepad1.a) {
                speed = 0.5;
            }
            
            if (gamepad1.b) {
                speed = 1;
            }
            
            if (gamepad1.back) {
                worm.setVelocity(700);
                worm.setTargetPosition(800);
                Thread.sleep(700);
                viper1.setPower(-1);
                viper2.setPower(-1);
                flip.setPosition(0.13);
                Thread.sleep(850);
                viper1.setPower(0);
                viper2.setPower(0);
                worm.setVelocity(700);
                worm.setTargetPosition(400);
                Thread.sleep(1200);
                viper1.setPower(1);
                viper2.setPower(1);
                sleep(100);
                worm.setVelocity(600);
                worm.setTargetPosition(600);
                sleep(400);
                pick.setPower(0.5);
                sleep(1000);
                flip.setPosition(1);
                pClaw.setPosition(0.15);
                sleep(3500);
                flip.setPosition(0.13);
                Thread.sleep(20000);
                worm.setTargetPosition(0);
                slpPause = true; 
     
            }
            
            if (gamepad2.dpad_right) {
            worm.setVelocity(700);
            worm.setTargetPosition(300);
            }
          
            if (gamepad2.dpad_down) {
            worm.setTargetPosition(0);
            worm.setVelocity(700);
            }
            
            if (gamepad2.back) {
                pClaw.setPosition(0.15);
                flip.setPosition(0.15);
            }
            
            
            while (gamepad1.dpad_left){
                worm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                worm.setPower(0.6);
                worm.setPower(0);
                
            }
            
            while (gamepad1.dpad_right){
                worm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                worm.setPower(-1);
                worm.setPower(0);
            }
            
            
            while (gamepad1.dpad_up){
                viper1.setPower(-1);
                viper2.setPower(-1);
                viper1.setPower(0);
                viper2.setPower(0);
            }
            
            while (gamepad1.dpad_down){
                viper1.setPower(1);
                viper2.setPower(1);
                viper1.setPower(0);
                viper2.setPower(0);
                
            }
            
            
            
            fl.setPower(flPower * speed);
            bl.setPower(blPower * speed);
            fr.setPower(frPower * speed);
            br.setPower(brPower * speed);
            pick.setPower(pickPower);
            viper1.setPower(viper1Power);
            viper2.setPower(viper2Power);
            
            
            
            telemetry.update();
           
            telemetry.addData("velocity", worm.getVelocity());
            telemetry.addData("position", worm.getCurrentPosition());
            telemetry.addData("is at target", !worm.isBusy());
            telemetry.addData("Viper1", viper1.getPower());
            telemetry.addData("Viper2", viper2.getPower());
            telemetry.addData("Pick", pick.getPower());
            telemetry.addData("Fl", flPower);
            telemetry.addData("Fr", frPower);
            telemetry.addData("Bl", blPower);
            telemetry.addData("Br", brPower);
            telemetry.addData("Speed", speed);
            telemetry.addData("slpPause", slpPause);
            telemetry.addData("imuYAW", imuExpan.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));
            
            
            

            
            
        }  
    
    }
    
}
