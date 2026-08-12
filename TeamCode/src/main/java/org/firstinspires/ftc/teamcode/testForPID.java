package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.AUTONOMO.PID.GiroPID;

public class testForPID extends LinearOpMode {

    private DcMotor leftFront, leftBack, rightFront, rightBack;
    private IMU imu;
    private GiroPID Spin = new GiroPID();

    boolean SpinIsBusy = false;

    public void runOpMode(){


        MOTORCONFIG(); InitIMU();

        waitForStart();

        while (opModeIsActive()){

            if (gamepad1.a && !SpinIsBusy){
                Spin.iniciarPID(90, 0.6);
                SpinIsBusy = true;
            }
            if (SpinIsBusy){
                Spin.UPDATE(leftFront, leftBack, rightFront, rightBack, imu);
                if (Spin.FINISHED()) SpinIsBusy = false;
            }
            else {
                double x  = gamepad1.left_stick_x*1.1;
                double y  = -gamepad1.left_stick_y;
                double rx = gamepad1.right_stick_x;

                rx = Math.copySign(rx*rx, rx);



                double Median = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1 );

                double leftFrontPower  = ( y + x + rx) / Median;
                double leftBackPower   = ( y - x + rx) / Median;
                double rightFrontPower = ( y - x - rx) / Median;
                double rightBackPower  = ( y + x - rx) / Median;


                leftFront.setPower(leftFrontPower);
                leftBack.setPower(leftBackPower);
                rightFront.setPower(rightFrontPower);
                rightBack.setPower(rightBackPower);

            }


        }


    }
    public void MOTORCONFIG(){
        leftFront  = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack   = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack  = hardwareMap.get(DcMotor.class, "rightBack");


        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


    }
    public void InitIMU() {

        RevHubOrientationOnRobot.LogoFacingDirection LogoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection UsbDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;
        RevHubOrientationOnRobot orientationOnRobot =
                new RevHubOrientationOnRobot(LogoDirection, UsbDirection);
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(orientationOnRobot));

    }
}
