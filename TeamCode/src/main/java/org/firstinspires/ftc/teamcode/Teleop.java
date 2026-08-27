package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Código de Teleoperado Completo para a equipe MEGA.
 * Chassi Mecanum, Shooter, Spindexer, Feeder e Servos.
 * Logica de Timer para acionamento de alguns mecanismos
 * ==> Uso de servos como "catracas" <==
 *   Talvez seja necessário redefinir valores iniciais dos servos
 *   após os próximos testes
 */
@TeleOp(name = "Teleoperado/Servos", group = "TeleOp")
public class Teleop extends LinearOpMode {

    // Chassi
    private DcMotor leftFront, leftBack, rightBack, rightFront;

    // Mecanismos
    private DcMotor spindexer, feeder, shooter;
    private Servo servoLeft, servoRight;

    // --- CONFIGURAÇÃO DE COMPENSAÇÃO MANUAL (BIAS) ---
    private double FATOR_COMPENSACAO_STRAFE = 0.8;

    // --- CONFIGURAÇÃO DOS SERVOS ---
    private double posZeroEsquerda = 0.0;
    private double posZeroDireita = 0.1754;
    private double SERVO_ATIVO = 0.48; // Aproximadamente 70 graus
    private double TEMPO_ESPERA = 1.92; // 2 segundos conforme pedido

    @Override
    public void runOpMode() {

        // Hardware Map
        leftFront  = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack   = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack  = hardwareMap.get(DcMotor.class, "rightBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        spindexer  = hardwareMap.get(DcMotor.class, "Spindexer");
        feeder     = hardwareMap.get(DcMotor.class, "feeder");
        shooter    = hardwareMap.get(DcMotor.class, "shooter");
        servoLeft  = hardwareMap.get(Servo.class, "servoLeft");
        servoRight = hardwareMap.get(Servo.class, "servoRight");

        // Direção dos Motores
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        // Direção dos Servos
        servoLeft.setDirection(Servo.Direction.FORWARD);
        servoRight.setDirection(Servo.Direction.FORWARD);

        // Zero Power Behavior
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        spindexer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        feeder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



        waitForStart();

        // Salvamos a posição atual como o "Zero"
        posZeroEsquerda = servoLeft.getPosition();
        posZeroDireita = servoRight.getPosition();

        // Variáveis de Estado
        boolean lastRT = false;
        boolean lastLT = false;
        double shooterPower = 0;
        ElapsedTime timer = new ElapsedTime();

        while (opModeIsActive()) {

            // --- MOVIMENTAÇÃO (Gamepad 1) ---
            double eixoY = -gamepad1.left_stick_y;
            double eixoX = gamepad1.left_stick_x;
            double rotacao = gamepad1.right_stick_x;

            double multEsq = 1.0;
            double multDir = 1.0;
            if (eixoX < -0.1) multDir = 1.0 + (Math.abs(eixoX) * FATOR_COMPENSACAO_STRAFE);
            else if (eixoX > 0.1) multEsq = 1.0 + (Math.abs(eixoX) * FATOR_COMPENSACAO_STRAFE);

            double fl = (eixoY + eixoX + rotacao) * multEsq;
            double bl = (eixoY - eixoX + rotacao) * multEsq;
            double fr = (eixoY - eixoX - rotacao) * multDir;
            double br = (eixoY + eixoX - rotacao) * multDir;

            double max = Math.max(Math.abs(fl), Math.max(Math.abs(bl), Math.max(Math.abs(fr), Math.abs(br))));
            if (max > 1.0) {
                fl /= max; bl /= max; fr /= max; br /= max;
            }

            leftFront.setPower(fl);
            leftBack.setPower(bl);
            rightFront.setPower(fr);
            rightBack.setPower(br);


            // --- MECANISMOS (Gamepad 2) ---
            boolean currentRT = gamepad2.right_trigger > 0.5;
            boolean currentLT = gamepad2.left_trigger > 0.5;

            // Toggle RT (Lado Direito / Negativo)
            if (currentRT && !lastRT) {
                if (shooterPower == -1.0) {
                    shooterPower = 0;
                } else {
                    shooterPower = -1.0;
                    timer.reset();
                }
            }
            // Toggle LT (Lado Esquerdo / Positivo)
            if (currentLT && !lastLT) {
                if (shooterPower == 1.0) {
                    shooterPower = 0;
                } else {
                    shooterPower = 1.0;
                    timer.reset();
                }
            }
            lastRT = currentRT;
            lastLT = currentLT;

            // O Shooter liga imediatamente
            shooter.setPower(shooterPower);

            // --- LÓGICA DE SINCRONIZAÇÃO (Spindexer + Servo) ---
            if (shooterPower != 0) {
                // Só liga Spindexer e Servo SE o timer passar de 2.0s
                if (timer.seconds() >= TEMPO_ESPERA) {
                    spindexer.setPower(shooterPower * 0.8);
                    if (shooterPower == 1.0) { // Lado LT
                        servoLeft.setPosition(Range.clip(posZeroEsquerda + SERVO_ATIVO, 0.0, 1.0));
                        servoRight.setPosition(posZeroDireita);
                    } else { // Lado RT
                        // Invertendo o movimento através da subtração para girar ao contrário
                        servoRight.setPosition(Range.clip(posZeroDireita + SERVO_ATIVO, 0.0, 1.0));
                        servoLeft.setPosition(posZeroEsquerda);
                    }
                } else {
                    // Durante a aceleração (0 a 2.0s), mantém spindexer e servos no reset
                    spindexer.setPower(0);
                    servoLeft.setPosition(posZeroEsquerda);
                    servoRight.setPosition(posZeroDireita);
                }
            } else {
                // Desligado -> Reset imediato para o Zero Manual
                spindexer.setPower(0);
                servoLeft.setPosition(posZeroEsquerda);
                servoRight.setPosition(posZeroDireita);
            }

            // FEEDER
            if (gamepad2.right_bumper) feeder.setPower(1.0);
            else if (gamepad2.left_bumper) feeder.setPower(-1.0);
            else feeder.setPower(0);

            telemetry.addData("Shooter", shooterPower);
            telemetry.addData("Timer", "%.2f s", timer.seconds());
            telemetry.addData("Status", (timer.seconds() < TEMPO_ESPERA && shooterPower != 0) ? "ACELERANDO..." : "PRONTO");
            telemetry.addData("Servo Esq Pos", servoLeft.getPosition());
            telemetry.addData("Servo Dir Pos", servoRight.getPosition());
            telemetry.update();
        }
        //Evitar conflito quando mudar para o Autônomo
        servoLeft.setPosition(posZeroEsquerda);
        servoRight.setPosition(posZeroDireita);
    }
}
