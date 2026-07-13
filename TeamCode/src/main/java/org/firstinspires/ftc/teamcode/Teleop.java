package org.firstinspires.ftc.teamcode.Rogensk_codes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Código de Teleoperado para a equipe MEGA.
 * Este OpMode controla um chassi Mecanum, um motor Spindexer com sistema de toggle (Y/X)
 * e um motor Feeder com sistema de hold (bumpers).
 */
@TeleOp(name = "Teleoperado", group = "TeleOp")
public class Teleop extends LinearOpMode {

    // Zona morta dos joysticks: valores abaixo disso são tratados como zero
    private static final double DEADZONE = 0.05;

    // Declaração dos motores do chassi
    private DcMotor leftBack;
    private DcMotor leftFront;
    private DcMotor rightBack;
    private DcMotor rightFront;

    // Declaração dos motores de mecanismos
    private DcMotor spindexer;
    private DcMotor feeder;
    private DcMotor shooter;

    /**
     * Aplica a zona morta a um valor de joystick.
     * Se o valor absoluto estiver dentro da DEADZONE, retorna 0.
     * Caso contrário, "reescala" o valor para que a resposta continue suave
     * logo após sair da zona morta (sem salto brusco de 0 para 0.06, por exemplo).
     */
    private double applyDeadzone(double value) {
        if (Math.abs(value) < DEADZONE) {
            return 0.0;
        }
        double sign = Math.signum(value);
        return sign * ((Math.abs(value) - DEADZONE) / (1.0 - DEADZONE));
    }

    @Override
    public void runOpMode() {

        // --- Mapeamento de Hardware ---
        // Associa as variáveis aos nomes configurados no Driver Station
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        spindexer = hardwareMap.get(DcMotor.class, "Spindexer");
        feeder = hardwareMap.get(DcMotor.class, "feeder");
        shooter = hardwareMap.get(DcMotor.class, "shooter");

        // --- Configuração de Direção ---
        // Motores da esquerda costumam ser invertidos para que valores positivos movam o robô para frente
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        leftFront.setDirection(DcMotor.Direction.REVERSE);

        rightBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.FORWARD);

        // --- Comportamento em Zero Power ---
        // Configura os motores para "frear" (BRAKE) quando a potência for zero, em vez de deslizar (FLOAT)
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        spindexer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        feeder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addLine("Pronto!");
        telemetry.update();

        // Variáveis para a lógica de Toggle (Clique para ligar/desligar)
        boolean lastRT = false;
        boolean lastLT = false;
        double shooterActivePower = 0;

        // Aguarda o botão Start ser pressionado no Driver Station
        waitForStart();

        // Loop principal do TeleOp
        while (opModeIsActive()) {

            // --- Controle de Movimentação (Gamepad 1) ---
            // y: Frente/Trás | x: Esquerda/Direita (Strafe) | rx: Rotação
            double y = applyDeadzone(-gamepad1.left_stick_y);
            double x = applyDeadzone(gamepad1.left_stick_x);
            double rx = applyDeadzone(gamepad1.right_stick_x);

            // Fórmulas matemáticas para tração Mecanum
            // Configuração "X" padrão: leftFront/rightBack com rolete de um tipo,
            // leftBack/rightFront com rolete do outro tipo (cantos opostos iguais).
            double frontLeftPower  = y + x + rx;
            double backLeftPower   = y - x + rx;
            double frontRightPower = y - x - rx;
            double backRightPower  = y + x - rx;

            double denominator = Math.max(
                    Math.max(Math.abs(frontLeftPower), Math.abs(backLeftPower)),
                    Math.max(Math.abs(frontRightPower), Math.abs(backRightPower))
            );

            if (denominator > 1.0){
                frontLeftPower  /= denominator;
                backLeftPower   /= denominator;
                frontRightPower /= denominator;
                backRightPower  /= denominator;
            }

            // Envia as potências calculadas para os motores do chassi
            leftFront.setPower(frontLeftPower);
            leftBack.setPower(backLeftPower);
            rightFront.setPower(frontRightPower);
            rightBack.setPower(backRightPower);

            // --- Controle do Shooter e Spindexer (Gamepad 2) - Lógica de Toggle (RT/LT) ---
            // RT liga/desliga para frente, LT liga/desliga para trás
            boolean currentRT = gamepad2.right_trigger > 0.5;
            boolean currentLT = gamepad2.left_trigger > 0.5;

            if (currentRT && !lastRT) {
                if (shooterActivePower == 1.0) shooterActivePower = 0;
                else shooterActivePower = 1.0;
            }
            lastRT = currentRT;

            if (currentLT && !lastLT) {
                if (shooterActivePower == -1.0) shooterActivePower = 0;
                else shooterActivePower = -1.0;
            }
            lastLT = currentLT;

            shooter.setPower(shooterActivePower);
            spindexer.setPower(shooterActivePower * 0.8); // Spindexer acompanha o shooter (80% da força)

            // --- Controle do Feeder (Gamepad 2) - Bumpers (RB/LB) ---
            if (gamepad2.right_bumper) {
                feeder.setPower(1.0);
            } else if (gamepad2.left_bumper) {
                feeder.setPower(-1.0);
            } else {
                feeder.setPower(0);
            }

            // --- Telemetria ---
            // Exibe dados no Driver Station para diagnóstico em tempo real
            telemetry.addData("Status", "Rodando");
            telemetry.addData("Joystick Y", y);
            telemetry.addData("Joystick X", x);
            telemetry.addData("Joystick RX", rx);
            telemetry.addData("FeederRT",gamepad2.right_bumper );
            telemetry.addData("FeederLT", gamepad2.left_bumper);
            telemetry.update();
        }
    }
}