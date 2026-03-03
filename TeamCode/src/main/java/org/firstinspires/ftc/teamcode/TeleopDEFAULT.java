package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Arrays;

@TeleOp(name = "TELEOP_OFC")
public class TeleopDEFAULT extends LinearOpMode {

    // ===================== MOTORES DE MOVIMENTO =====================
    private DcMotorEx leftFront = null;
    private DcMotorEx leftBack = null;
    private DcMotorEx rightFront = null;
    private DcMotorEx rightBack = null;

    // ===================== MECANISMOS =====================
    private DcMotor feeder = null;
    private DcMotorEx baseShooter = null;
    private DcMotor shooterDireito = null;
    private DcMotor shooterEsquerdo = null;
    private Servo alavanca = null;
    private ElapsedTime tempoAlavanca = new ElapsedTime();
    private boolean alavancaAtiva = false;

    // ===================== CONTROLE DE VELOCIDADE CHASSI =====================
    private final double[] VELOCIDADES_CHASSI = {0.130, 0.300, 0.500, 0.850};
    private int indiceVelocidade = VELOCIDADES_CHASSI.length - 1;

    public boolean rbPressionadoUltimoEstado = false;
    public boolean lbPressionadoUltimoEstado = false;

    // ===================== SHOOTER =====================
    boolean shooterLigado = false;
    public boolean A_PressUltEst_G2 = false;

    double shooterPower = -0.85;
    double menorShooter = 0.65;
    double maiorShooter = 0.8;

    private boolean yUltimoEstado = false;
    private boolean bUltimoEstado = false;

    // ===================== LIMITES BASE =====================
    private static final int TICKS_PER_OUTPUT_REV = 1120;
    private static final double DEGREES_PER_TICK = 360.0 / TICKS_PER_OUTPUT_REV;

    private static final double MAX_ANGLE = 180.0;
    private static final double MIN_ANGLE = -180.0;

    @Override
    public void runOpMode() {

        telemetry.addData("Status", "Iniciando...");
        telemetry.update();

        initconfigH();

        telemetry.addData("Status", "INICIADO");
        telemetry.update();

        waitForStart();
        resetRuntime();

        Arrays.sort(VELOCIDADES_CHASSI);

        while (opModeIsActive()) {

            gamepad1.setLedColor(0, 128, 0, Gamepad.LED_DURATION_CONTINUOUS);
            gamepad2.setLedColor(0, 128, 0, Gamepad.LED_DURATION_CONTINUOUS);

            driveMecanum();
            feederControl();
            ligarShooter();
            controleBaseShooter();

            telemetry.addData("Velocidade Chassi", "%.3f", VELOCIDADES_CHASSI[indiceVelocidade]);
            telemetry.addData("Shooter Power", shooterPower);
            telemetry.update();
        }
    }

    private void initconfigH() {

        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        leftBack = hardwareMap.get(DcMotorEx.class, "leftBack");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        rightBack = hardwareMap.get(DcMotorEx.class, "rightBack");

        feeder = hardwareMap.get(DcMotor.class, "feeder");
        shooterEsquerdo = hardwareMap.get(DcMotor.class, "shooterEsquerdo");
        shooterDireito = hardwareMap.get(DcMotor.class, "shooterDireito");
        baseShooter = hardwareMap.get(DcMotorEx.class, "baseShooter");

        alavanca = hardwareMap.get(Servo.class, "alavancaServo");
        alavanca.setPosition(0);

        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);
        shooterDireito.setDirection(DcMotor.Direction.REVERSE);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        baseShooter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        baseShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void driveMecanum() {

        double ft = gamepad1.left_stick_y;
        double lateral = -gamepad1.left_stick_x;
        double giro = -gamepad1.right_stick_x;

        double velocidade = VELOCIDADES_CHASSI[indiceVelocidade];

        boolean lb = gamepad1.right_bumper;
        boolean rb = gamepad1.left_bumper;

        if (rb && !rbPressionadoUltimoEstado && indiceVelocidade > 0) indiceVelocidade--;
        if (lb && !lbPressionadoUltimoEstado && indiceVelocidade < VELOCIDADES_CHASSI.length - 1) indiceVelocidade++;

        rbPressionadoUltimoEstado = rb;
        lbPressionadoUltimoEstado = lb;

        leftFront.setPower((ft + lateral + giro) * velocidade);
        rightFront.setPower((ft - lateral - giro) * velocidade);
        leftBack.setPower((ft - lateral + giro) * velocidade);
        rightBack.setPower((ft + lateral - giro) * velocidade);
    }

    private void feederControl() {

        if (gamepad2.right_bumper) feeder.setPower(1);
        else if (gamepad2.left_bumper) feeder.setPower(-1);
        else feeder.setPower(0);
    }

    private void ligarShooter() {

        boolean A = gamepad2.a;

        if (A && !A_PressUltEst_G2) shooterLigado = !shooterLigado;
        A_PressUltEst_G2 = A;

        boolean yPressionado = gamepad2.y;
        boolean bPressionado = gamepad2.b;

        if (yPressionado && !yUltimoEstado) shooterPower = -menorShooter;
        if (bPressionado && !bUltimoEstado) shooterPower = -maiorShooter;

        yUltimoEstado = yPressionado;
        bUltimoEstado = bPressionado;

        if (shooterLigado) {

            shooterDireito.setPower(shooterPower);
            shooterEsquerdo.setPower(shooterPower);

            if (gamepad2.dpad_up && !alavancaAtiva) {
                alavanca.setPosition(0.68);
                tempoAlavanca.reset();
                alavancaAtiva = true;
            }

            if (alavancaAtiva && tempoAlavanca.seconds() >= 1.5) {
                alavanca.setPosition(0);
                alavancaAtiva = false;
            }

        } else {
            shooterDireito.setPower(0);
            shooterEsquerdo.setPower(0);
            alavanca.setPosition(0);
            alavancaAtiva = false;
        }
    }

    private void controleBaseShooter() {

        double stick = gamepad2.left_stick_x;
        double power = stick * 0.5;
        double angle = getAngleDegrees();

        if ((power > 0 && angle >= MAX_ANGLE) ||
                (power < 0 && angle <= MIN_ANGLE)) {
            baseShooter.setPower(0);
            return;
        }

        baseShooter.setPower(power);
    }

    private double getAngleDegrees() {
        return baseShooter.getCurrentPosition() * DEGREES_PER_TICK;
    }
}