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
public class Robo_Centric extends LinearOpMode {
    
    
    private Servo limit;
    private DcMotor viper;
    private DcMotor worm;
    private DistanceSensor dis1;
    private DistanceSensor dis2;
    private DistanceSensor dis3;
    private DcMotor bl;
    
    @Override
    public void runOpMode() throws InterruptedException {
        
        
       
        
       
        
        int magvalue;
        double dis1mm;
        double dis2mm;
        double dis3mm;
        
        DcMotor fl = hardwareMap.dcMotor.get("fl");
        DcMotor bl = hardwareMap.dcMotor.get("bl");
        DcMotor fr = hardwareMap.dcMotor.get("fr");
        DcMotor br = hardwareMap.dcMotor.get("br");
        DcMotor worm = hardwareMap.dcMotor.get("worm");
        DcMotor viper = hardwareMap.dcMotor.get("viper");
        Servo claw = hardwareMap.servo.get("claw");
        limit = hardwareMap.get(Servo.class, "limit");
        dis1 = hardwareMap.get(DistanceSensor.class, "dis1");
        dis2 = hardwareMap.get(DistanceSensor.class, "dis2");
        dis3 = hardwareMap.get(DistanceSensor.class, "dis3");
        
        
        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // see the note about this erlier on this page.
        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);
        
        viper.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        
        // Retrieve the IMU from the hardware map
        IMU imu = hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);
        
        limit.setPosition(0);
        claw.setPosition(0.35);
        magvalue = 0;
        
        viper.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
       double speed = 1;
        
        telemetry.addData("status", "intialized");
        telemetry.update();
        
        waitForStart();
    
        
        
        
        if (isStopRequested()) return;
        
        while(opModeIsActive()) {
            if (gamepad2.right_bumper) {
                claw.setPosition(0.3);
            }
            
            if (gamepad2.left_bumper) {
                claw.setPosition(0);
            }
            
            
            if (gamepad1.start) {
                imu.resetYaw();
            }
            
            dis1mm = dis1.getDistance(DistanceUnit.MM);
            dis2mm = dis2.getDistance(DistanceUnit.MM);
            dis3mm = dis2.getDistance(DistanceUnit.MM);
            
            if (dis2mm <= 100) {
                limit.setPosition(0.35);
            } 
        
            if (dis1mm <= 100) {
                limit.setPosition(0.35);
            } 
        
            else if (dis3mm >= 100 && dis2mm <= 100) {
               limit.setPosition(0.35);
            } 
        
            else if (dis3mm <= 100 && dis2mm <= 280) {
                limit.setPosition(0.68);
            }
                
            else {
                limit.setPosition(0.35);
            }
            
           
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            double y2 = gamepad2.left_stick_y;
            double x2 = gamepad2.right_stick_y;

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double flPower = (y + x + rx) / denominator;
            double blPower = (y - x + rx) / denominator;
            double frPower = (y - x - rx) / denominator;
            double brPower = (y + x - rx) / denominator;
            double wormPower = -y2 / 1.15;
            double viperPower = x2;
            
            if (gamepad1.a) {
                speed = 0.5;
            }
            
            if (gamepad1.b) {
                speed = 0.8;
            }
            
            
            fl.setPower(flPower * speed);
            bl.setPower(blPower * speed);
            fr.setPower(frPower * speed);
            br.setPower(brPower * speed);
            worm.setPower(wormPower);
            viper.setPower(viperPower);
            
            
            
            telemetry.update();
            telemetry.addData("dis", dis1.getDistance(DistanceUnit.MM));
            telemetry.addData("dis2", dis2.getDistance(DistanceUnit.MM));
            telemetry.addData("dis3", dis3.getDistance(DistanceUnit.MM));
            telemetry.addData("limit", limit.getPosition());
            telemetry.addData("viper", viper.getPower());
            telemetry.addData("Fl", flPower);
            telemetry.addData("Fr", frPower);
            telemetry.addData("Bl", blPower);
            telemetry.addData("Br", brPower);
            
            

            
            
        }  
    
    }
    
}
