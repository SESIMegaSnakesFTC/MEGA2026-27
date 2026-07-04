package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Teleop,,, group = "TeleOp")
public class Teleop extends LinearOpMode {

    private DcMotor leftBack;
    private DcMotor leftFront;
    private DcMotor rightBack;
    private DcMotor rightFront;
    private DcMotor spindexer;
    private DcMotor feeder;

    @Override
    public void runOpMode() {

        // Hardware Map
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        spindexer = hardwareMap.get(DcMotor.class, "Spindexer");
        feeder = hardwareMap.get(DcMotor.class, "feeder");

        // Inversão dos motores do lado esquerdo
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        leftFront.setDirection(DcMotor.Direction.REVERSE);

        rightBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.FORWARD);

        // Freio quando soltar o joystick
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        spindexer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        feeder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addLine("Pronto!");
        telemetry.update();

        boolean lastY = false;
        boolean lastX = false;
        double spindexerPower = 0;

        waitForStart();

        while (opModeIsActive()) {

            // Movimento
            double y = gamepad1.left_stick_y;    // Frente e trás (Invertido)
            double x = -gamepad1.left_stick_x;   // Strafe (Invertido)
            double rx = gamepad1.right_stick_x;  // Giro

            // Cálculo Mecanum
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

            double frontLeftPower  = (y + x + rx) / denominator;
            double backLeftPower   = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower  = (y + x - rx) / denominator;

            // Aplica potência
            leftFront.setPower(frontLeftPower);
            leftBack.setPower(backLeftPower);
            rightFront.setPower(frontRightPower);
            rightBack.setPower(backRightPower);

            // Controle Spindexer (Gamepad 2) - Toggle
            if (gamepad2.y && !lastY) {
                if (spindexerPower == 0.6) {
                    spindexerPower = 0;
                } else {
                    spindexerPower = 0.6;
                }
            }
            lastY = gamepad2.y;

            if (gamepad2.x && !lastX) {
                if (spindexerPower == -0.6) {
                    spindexerPower = 0;
                } else {
                    spindexerPower = -0.6;
                }
            }
            lastX = gamepad2.x;

            spindexer.setPower(spindexerPower);

            // Controle Feeder (Gamepad 2) - Hold
            if (gamepad2.right_bumper) {
                feeder.setPower(1.0);
            } else if (gamepad2.left_bumper) {
                feeder.setPower(-1.0);
            } else {
                feeder.setPower(0);
            }

            // Telemetria
            telemetry.addData("Left Front", frontLeftPower);
            telemetry.addData("Left Back", backLeftPower);
            telemetry.addData("Right Front", frontRightPower);
            telemetry.addData("Right Back", backRightPower);

            telemetry.addData("Y", y);
            telemetry.addData("X", x);
            telemetry.addData("RX", rx);

            telemetry.update();
        }
    }
}