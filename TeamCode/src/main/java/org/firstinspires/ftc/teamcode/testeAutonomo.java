package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Autonomous(name = "BAIXO - VERMELHO TIROS (Autônomo)")
public class testeAutonomo extends LinearOpMode {

    private DcMotorEx leftFront, leftBack, rightFront, rightBack;
    private DcMotorEx feeder;
    private ShooterController shooterController;

    @Override
    public void runOpMode() {

        initconfigH();

        // Motor do feeder
        feeder = hardwareMap.get(DcMotorEx.class, "feeder");

        // ShooterController
        shooterController = new ShooterController();
        shooterController.init(hardwareMap, feeder);

        waitForStart();

        if (opModeIsActive()) {

            // 1) Anda um pouco pra frente
            andarFrente(0.5);
            sleep(300);
            parar();
            sleep(300);

            // 2) Inicia ciclo (liga shooter e começa a acelerar)
            shooterController.iniciarCiclo3Bolinhas();

            // 3) Loop do tiro (robô parado)
            while (opModeIsActive() && !shooterController.isIdle()) {

                shooterController.update();

                // Quando estiver pronto, começa a atirar
                if (shooterController.isReadyToShoot()) {
                    shooterController.comecarATirar();
                }

                idle();
            }
        }
    }

    private void initconfigH() {
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        leftBack = hardwareMap.get(DcMotorEx.class, "leftBack");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        rightBack = hardwareMap.get(DcMotorEx.class, "rightBack");

        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    private void andarFrente(double power) {
        leftFront.setPower(power);
        leftBack.setPower(power);
        rightFront.setPower(power);
        rightBack.setPower(power);
    }

    private void parar() {
        leftFront.setPower(0);
        leftBack.setPower(0);
        rightFront.setPower(0);
        rightBack.setPower(0);
    }
}