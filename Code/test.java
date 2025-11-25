// package org.firstinspires.ftc.teamcode;
// 
// import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
// import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
// import com.qualcomm.robotcore.hardware.DcMotor;
// import com.qualcomm.robotcore.hardware.DistanceSensor;
// import com.qualcomm.robotcore.hardware.Servo;
// import com.qualcomm.robotcore.hardware.TouchSensor;
// import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
// 
// @TeleOp(name = "test (Blocks to Java)")
// public class test extends LinearOpMode {
// 
//   private Servo limit;
//   private DcMotor viper;
//   private DcMotor worm;
//   private DistanceSensor dis1;
//   private DistanceSensor dis2;
//   private TouchSensor Touch;
//   private DcMotor bl;
// 
//   /**
//    * This sample contains the bare minimum Blocks for any regular OpMode. The 3 blue
//    * Comment Blocks show where to place Initialization code (runs once, after touching the
//    * DS INIT button, and before touching the DS Start arrow), Run code (runs once, after
//    * touching Start), and Loop code (runs repeatedly while the OpMode is active, namely not
//    * Stopped).
//    */
//   @Override
//   public void runOpMode() {
//     int magvalue;
//     double dis1mm;
//     double dis2mm;
// 
//     limit = hardwareMap.get(Servo.class, "limit");
//     viper = hardwareMap.get(DcMotor.class, "viper");
//     worm = hardwareMap.get(DcMotor.class, "worm");
//     dis1 = hardwareMap.get(DistanceSensor.class, "dis1");
//     dis2 = hardwareMap.get(DistanceSensor.class, "dis2");
//     Touch = hardwareMap.get(TouchSensor.class, "Touch");
//     bl = hardwareMap.get(DcMotor.class, "bl");
// 
//     // Put initialization blocks here.
//     waitForStart();
//     limit.setPosition(0);
//     magvalue = 0;
//     viper.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//     if (opModeIsActive()) {
//       while (opModeIsActive()) {
//         worm.setPower(gamepad1.left_stick_y);
//         viper.setPower(gamepad1.right_stick_y);
//         if (viper.getPower() <= 0) {
//           viper.setPower(0.7 * viper.getPower());
//         }
//         dis1mm = dis1.getDistance(DistanceUnit.MM);
//         dis2mm = dis2.getDistance(DistanceUnit.MM);
//         if (Touch.isPressed()) {
//           magvalue = 1;
//         } else {
//           magvalue = 0;
//         }
//         if (dis1mm <= 100) {
//           limit.setPosition(1);
//         } else if (magvalue == 0 && dis2mm <= 100) {
//           limit.setPosition(0.35);
//         } else if (magvalue == 1 && dis2mm <= 100) {
//           limit.setPosition(0);
//         } else {
//           magvalue = 0;
//         }
//         // Put loop blocks here.
//         telemetry.update();
//         telemetry.addData("dis", dis1.getDistance(DistanceUnit.MM));
//         telemetry.addData("dis2", dis2.getDistance(DistanceUnit.MM));
//         telemetry.addData("Mag Value", magvalue);
//         telemetry.addData("limit", limit.getPosition());
//         telemetry.addData("viper", viper.getPower());
//       }
//       // Put run blocks here.
//     }
//   }
// }
// 
