package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp
public class Kdrivecode2 extends LinearOpMode {
    TouchSensor touch;
    
    private Servo limit;
    private DcMotor viper;
    private DcMotor worm;
    private DistanceSensor dis1;
    private DistanceSensor dis2;
    private TouchSensor Touch;
    private DcMotor bl;
    
    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        
        int magvalue;
        double dis1mm;
        double dis2mm;
        
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
        Touch = hardwareMap.get(TouchSensor.class, "Touch");
        
        
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
        claw.setPosition(0);
        magvalue = 0;
        viper.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
       double speed = 1;
        
        telemetry.addData("status", "intialized");
        telemetry.update();
        
        waitForStart();
        
        if (isStopRequested()) return;
        
        while(opModeIsActive()) {
            double y = -gamepad1.left_stick_y; //remember, y stick value is reversed
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x * 0.6;
            double y2 = gamepad2.left_stick_y;
            double x2 = gamepad2.right_stick_y;
            
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
            
            if (Touch.isPressed()) {
                 magvalue = 1;
            } 
        
            else {
                magvalue = 0;
            }
            if (dis1mm <= 100) {
                limit.setPosition(0.35);
            } 
        
            else if (magvalue == 0 && dis2mm <= 100) {
                limit.setPosition(0.35);
            } 
        
            else if (magvalue == 1 && dis2mm <= 100) {
                limit.setPosition(0);
            } 
        
            else {
                magvalue = 0;
            }
            
            // Rotate the movement direction counter to the bot's rotations
            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            
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
            double wormPower = -y2 / 1.2;
            double viperPower = x2;
            
            if (gamepad1.a) {
                speed = 0.5;
            }
            
            if (gamepad1.b) {
                speed = 1;
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
            telemetry.addData("Mag Value", magvalue);
            telemetry.addData("limit", limit.getPosition());
            telemetry.addData("viper", viper.getPower());
            telemetry.addData("Fl", flPower);
            telemetry.addData("Fr", frPower);
            telemetry.addData("Bl", blPower);
            telemetry.addData("Br", brPower);
            
            

            
            
        }  
    
    }
    
}
