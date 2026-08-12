package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Código de Teleoperado para a equipe MEGA.
 * Inclui Mecanum com Compensação de Força (Bias) MANUAL no Strafe.
 * Resolvido SEM SENSORES (Sem IMU).
 */
@TeleOp(name = "Teleoperado", group = "TeleOp")
public class Teleop extends LinearOpMode {

    enum STATELime { GIRANDO, ENCONTROU, PARADO }
    STATELime statusLIMELIGHT = STATELime.PARADO;
    boolean ToRight   = true;
    double AnguloAlvo = 0;

    // Chassi
    private DcMotor leftFront, leftBack, rightBack, rightFront;

    //limelight
    private Limelight3A limelight3A;
    boolean SeeingBall, Stop;
    // Mecanismos
    private DcMotor spindexer, feeder, shooter, LimelightMotor;

    // --- CONFIGURAÇÃO DE COMPENSAÇÃO MANUAL (BIAS) ---
    // Ajuste este valor conforme seus testes para o robô andar reto.
    // Ex: 0.1, 0.2, 0.3... quanto maior, mais força o lado oposto ganha.
    private double STRAFE_BIAS_FACTOR = 0.8;


    @Override
    public void runOpMode() {

        // Mapeamento de Hardware
        leftFront      = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack       = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack      = hardwareMap.get(DcMotor.class, "rightBack");
        rightFront     = hardwareMap.get(DcMotor.class, "rightFront");
        spindexer      = hardwareMap.get(DcMotor.class, "Spindexer");
        feeder         = hardwareMap.get(DcMotor.class, "feeder");
        shooter        = hardwareMap.get(DcMotor.class, "shooter");
        LimelightMotor = hardwareMap.get(DcMotor.class, "MotorLimelight");
        limelight3A    = hardwareMap.get(Limelight3A.class, "limelight");


        //limelight
        LimelightMotor.setDirection(DcMotor.Direction.FORWARD);
        LimelightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        limelight3A.setPollRateHz(90);
        limelight3A.start();

        //MODO ATUAL
        limelight3A.pipelineSwitch(9); //Identificar pólen



        // Direção dos motores
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        // Comportamento Zero Power
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        spindexer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        feeder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LimelightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        // Variáveis de Estado para o Shooter/Spindexer
        boolean lastRT            = false;
        boolean lastLT            = false;
        double shooterActivePower = 0;

        telemetry.addLine("Pronto! Pressione START");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // --- CONTROLE DE MOVIMENTAÇÃO (Gamepad 1) ---
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;

            // --- Lógica de Multiplicador de Força MANUAL (Bias) ---
            double leftMultiplier = 1.0;
            double rightMultiplier = 1.0;

            // Se estiver indo para a ESQUERDA (x negativo), aumenta força na DIREITA
            if (x < -0.1) {
                rightMultiplier = 1.0 + (Math.abs(x) * STRAFE_BIAS_FACTOR);
            }
            // Se estiver indo para a DIREITA (x positivo), aumenta força na ESQUERDA
            else if (x > 0.1) {
                leftMultiplier = 1.0 + (Math.abs(x) * STRAFE_BIAS_FACTOR);
            }

            // -- Variáveis da limelight --
            LLResult result = limelight3A.getLatestResult();

            if (result != null && result.isValid()){
                SeeingBall = true;
                while (SeeingBall){
                    Stop = true;
                }
            }
            else{
                SeeingBall = true;

            }


            // Cálculo das potências aplicando os multiplicadores de compensação
            double frontLeft  = (y + x + rx) * leftMultiplier;
            double backLeft   = (y - x + rx) * leftMultiplier;
            double frontRight = (y - x - rx) * rightMultiplier;
            double backRight  = (y + x - rx) * rightMultiplier;

            // Normalização para não ultrapassar 1.0 (100% de força)
            double denominator = Math.max(Math.max(Math.abs(frontLeft), Math.abs(backLeft)),
                    Math.max(Math.abs(frontRight), Math.abs(backRight)));
            denominator = Math.max(denominator, 1.0);

            leftFront.setPower(frontLeft / denominator);
            leftBack.setPower(backLeft / denominator);
            rightFront.setPower(frontRight / denominator);
            rightBack.setPower(backRight / denominator);


            // --- CONTROLE DO SHOOTER E SPINDEXER (Gamepad 2) - Lógica de Toggle ---
            boolean currentRT = gamepad2.right_trigger > 0.5;
            boolean currentLT = gamepad2.left_trigger > 0.5;

            if (currentRT && !lastRT) {
                if (shooterActivePower == 1.0) shooterActivePower = 0;
                else shooterActivePower = 1.0;
            }
            if (currentLT && !lastLT) {
                if (shooterActivePower == -1.0) shooterActivePower = 0;
                else shooterActivePower = -1.0;
            }

            lastRT = currentRT;
            lastLT = currentLT;

            shooter.setPower(shooterActivePower);
            spindexer.setPower(shooterActivePower * 0.8);

            // --- CONTROLE DO FEEDER (Gamepad 2) ---
            if (gamepad2.right_bumper) feeder.setPower(1.0);
            else if (gamepad2.left_bumper) feeder.setPower(-1.0);
            else feeder.setPower(0);




            // Telemetria para ajudar nos testes
            telemetry.addData("Bias Factor", STRAFE_BIAS_FACTOR);
            telemetry.addData("Mult Esq", leftMultiplier);
            telemetry.addData("Mult Dir", rightMultiplier);
            telemetry.addData("Any Ball", SeeingBall);
            telemetry.update();
        }
    }
}