package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Autonomous(name = "BAIXO - Azul (Autônomo)")
public class azulDown extends LinearOpMode {

    private DcMotorEx leftFront, leftBack, rightFront, rightBack;

    @Override
    public void runOpMode() {
        initconfigH();

        // Aguarda o botão de Play ser pressionado
        waitForStart();

        if (opModeIsActive()) {
            // Andar para frente (Sair da Launch Zone)
            andarFrente(0.5);
            sleep(400); // SE QUISER QUE ANDE MAIS TEMPO, MUDA AQUI LAURA E DANI
            parar();
            sleep(300);

            // Lateral Esquerda (Strafe Left)
            lateralDireita(0.5);
            sleep(300); // SE QUISER QUE ANDE MAIS TEMPO, MUDA AQUI LAURA E DANI
            parar();
        }
    }

    private void initconfigH() {
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        leftBack = hardwareMap.get(DcMotorEx.class, "leftBack");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        rightBack = hardwareMap.get(DcMotorEx.class, "rightBack");

        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        // Freio motor
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

    private void lateralDireita(double power) {
        leftFront.setPower(power);
        leftBack.setPower(-power);
        rightFront.setPower(-power);
        rightBack.setPower(power);
    }

    private void parar() {
        leftFront.setPower(0);
        leftBack.setPower(0);
        rightFront.setPower(0);
        rightBack.setPower(0);
    }
}