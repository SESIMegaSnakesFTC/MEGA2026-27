package org.firstinspires.ftc.teamcode;

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

    // Declaração dos motores do chassi
    private DcMotor leftBack;
    private DcMotor leftFront;
    private DcMotor rightBack;
    private DcMotor rightFront;

    // Declaração dos motores de mecanismos
    private DcMotor spindexer;
    private DcMotor feeder;

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

        // Avisa que a inicialização foi concluída
        telemetry.addLine("Pronto!");
        telemetry.update();

        // Variáveis auxiliares para a lógica de "Toggle" do Spindexer
        boolean lastY = false;      // Armazena o estado anterior do botão Y
        boolean lastX = false;      // Armazena o estado anterior do botão X
        double spindexerPower = 0;  // Potência atual que será enviada ao motor Spindexer

        // Aguarda o botão Start ser pressionado no Driver Station
        waitForStart();

        // Loop principal do TeleOp
        while (opModeIsActive()) {

            // --- Controle de Movimentação (Gamepad 1) ---
            // y: Frente/Trás | x: Esquerda/Direita (Strafe) | rx: Rotação
            double y = gamepad1.left_stick_y;    
            double x = -gamepad1.left_stick_x;   
            double rx = gamepad1.right_stick_x;  

            // Denominador para garantir que a potência total não ultrapasse 1.0 (100%)
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

            // Fórmulas matemáticas para tração Mecanum
            double frontLeftPower  = (y + x + rx) / denominator;
            double backLeftPower   = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower  = (y + x - rx) / denominator;

            // Envia as potências calculadas para os motores do chassi
            leftFront.setPower(frontLeftPower);
            leftBack.setPower(backLeftPower);
            rightFront.setPower(frontRightPower);
            rightBack.setPower(backRightPower);

            // --- Controle do Spindexer (Gamepad 2) - Lógica de Toggle ---
            
            // Lógica para o botão Y (Girar para frente ou parar)
            if (gamepad2.y && !lastY) { // Detecta apenas o momento do clique (quando pressiona)
                if (spindexerPower == 0.6) {
                    spindexerPower = 0;     // Se já estava ligado, desliga
                } else {
                    spindexerPower = 0.6;   // Se estava parado ou em reverso, liga com 0.6
                }
            }
            lastY = gamepad2.y; // Atualiza o estado anterior do botão

            // Lógica para o botão X (Girar para trás ou parar)
            if (gamepad2.x && !lastX) {
                if (spindexerPower == -0.6) {
                    spindexerPower = 0;     // Se já estava em reverso, desliga
                } else {
                    spindexerPower = -0.6;  // Se estava parado ou em frente, liga com -0.6
                }
            }
            lastX = gamepad2.x; // Atualiza o estado anterior do botão

            // Aplica a potência final ao motor do Spindexer
            spindexer.setPower(spindexerPower);

            // --- Controle do Feeder (Gamepad 2) - Lógica de Hold (Segurar) ---
            if (gamepad2.right_bumper) {
                feeder.setPower(1.0);  // Gira enquanto segura RB
            } else if (gamepad2.left_bumper) {
                feeder.setPower(-1.0); // Gira invertido enquanto segura LB
            } else {
                feeder.setPower(0);    // Para quando solta ambos
            }

            // --- Telemetria ---
            // Exibe dados no Driver Station para diagnóstico em tempo real
            telemetry.addData("Status", "Rodando");
            telemetry.addData("Spindexer Power", spindexerPower);
            telemetry.addData("Joystick Y", y);
            telemetry.addData("Joystick X", x);
            telemetry.addData("Joystick RX", rx);
            telemetry.update();
        }
    }
}

/**
 * --- LEGENDA DE PROGRAMAÇÃO ---
 * 
 * 1. public class Teleop extends LinearOpMode: Define o nome do programa e diz que ele segue o modelo padrão da FTC.
 * 2. private DcMotor: Declara uma variável que representa um motor elétrico. "private" significa que só este arquivo a enxerga.
 * 3. double: Tipo de dado para números com casas decimais. Usado para potências (ex: 0.6) e valores dos joysticks.
 * 4. boolean: Tipo de dado lógico que só pode ser Verdadeiro (true) ou Falso (false). Usado para o sistema de Toggle.
 * 5. void runOpMode(): O método principal onde tudo acontece. "void" significa que ele executa uma ação mas não devolve um valor.
 * 6. hardwareMap: O "dicionário" do robô que conecta os nomes escritos no Driver Station com as variáveis do código.
 * 7. setPower(double): Comando que define a velocidade do motor, variando de -1.0 (trás) a 1.0 (frente).
 * 8. while(opModeIsActive()): Um laço de repetição que mantém o código rodando enquanto o botão "Stop" não for pressionado.
 * 9. if / else: Estruturas de decisão. "Se" tal condição for real, faça isso, "senão", faça aquilo.
 * 10. !lastY: O símbolo "!" significa "NÃO". Usado aqui para verificar se o botão NÃO estava pressionado no ciclo anterior (detecção de clique).
 * 11. telemetry: Sistema usado para enviar textos e números para a tela do celular do piloto.
 */
