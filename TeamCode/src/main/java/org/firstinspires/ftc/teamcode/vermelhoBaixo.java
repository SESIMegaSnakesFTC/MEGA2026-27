package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name = "VERMELHO BAIXO")
public class vermelhoBaixo extends OpMode {

    // MOTORES/XERIFE
    private DcMotorEx feeder;
    private Follower follower;

    // CONTROLES
    private Paths paths;
    private Timer pathTimer;

    // IMPORTAÇÃO DE OUTRA CLASSE
    private ShooterController shooterController;

    // TIMEOUT DE SEGURANÇA PARA SHOOT
    private static final double ESPERA_SHOOT = 8.0;

    // -------------------------------------------------------
    // MÁQUINA DE ESTADOS
    // -------------------------------------------------------
    public enum PathState {
        IDASHOOT1,       // Move até a posição de tiro e inicia pré-carregamento do shooter
        ESPERA_SHOOT1,   // Aguarda o ciclo de tiro completar
        POSICAOFINAL,    // Move para a posição final
        DONE
    }

    private PathState pathState;

    // -------------------------------------------------------
    // CAMINHOS
    // -------------------------------------------------------
    public static class Paths {
        public PathChain IDASHOOT1;
        public PathChain POSICAOFINAL;

        public Paths(Follower follower) {

            IDASHOOT1 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(56.000, 8.000),
                            new Pose(64.000, 25.000)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(54))
                    .build();

            POSICAOFINAL = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(64.000, 25.000),
                            new Pose(23.474, 8.737)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))
                    .build();
        }
    }

    // -------------------------------------------------------
    // INIT
    // -------------------------------------------------------
    @Override
    public void init() {

        // CONFIGURAÇÃO FEEDER
        feeder = hardwareMap.get(DcMotorEx.class, "feeder");
        feeder.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        feeder.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        // INICIALIZA CLASSE LÓGICA SHOOTER
        shooterController = new ShooterController();
        shooterController.init(hardwareMap, feeder);

        // INICIALIZA PEDRO PATHING
        follower = Constants.createFollower(hardwareMap);
        paths = new Paths(follower);

        pathTimer = new Timer();
        pathState = PathState.IDASHOOT1;

        follower.setPose(new Pose(56.000, 8.000, Math.toRadians(90)));

        telemetry.addData("STATUS", "HARDWARE OK. AGUARDANDO INICIO.");
        telemetry.update();
    }

    // -------------------------------------------------------
    // START
    // -------------------------------------------------------
    @Override
    public void start() {
        pathTimer.resetTimer();
    }

    // -------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------
    private void setPathState(PathState newState) {
        pathState = newState;
        pathTimer.resetTimer();
    }

    // -------------------------------------------------------
    // LÓGICA DE ESTADOS
    // -------------------------------------------------------
    private void statePathUpdate() {
        switch (pathState) {

            // ---------------------------------------------------
            // IDASHOOT1 — Inicia o pré-carregamento do shooter e move até a posição de tiro
            // ---------------------------------------------------
            case IDASHOOT1:
                // Inicia ciclo de 3 bolinhas imediatamente (shooter acelera durante o trajeto)
                shooterController.iniciarCiclo3Bolinhas();

                follower.setMaxPower(1.0);
                follower.followPath(paths.IDASHOOT1, true);
                setPathState(PathState.ESPERA_SHOOT1);
                break;

            // ---------------------------------------------------
            // ESPERA_SHOOT1 — Aguarda chegada e executa o ciclo de tiro
            // ---------------------------------------------------
            case ESPERA_SHOOT1:
                if (shooterController.isReadyToShoot()) {
                    // Shooter acelerado e robô posicionado: começa a atirar
                    shooterController.comecarATirar();
                    telemetry.addLine("Atirando bolinhas...");
                } else if (shooterController.isIdle() && pathTimer.getElapsedTimeSeconds() > 1.0) {
                    // Ciclo concluído normalmente
                    setPathState(PathState.POSICAOFINAL);
                } else if (pathTimer.getElapsedTimeSeconds() >= ESPERA_SHOOT) {
                    // Timeout de segurança
                    shooterController.emergencyStop();
                    setPathState(PathState.POSICAOFINAL);
                }
                break;

            // ---------------------------------------------------
            // POSICAOFINAL — Move para posição de estacionamento
            // ---------------------------------------------------
            case POSICAOFINAL:
                if (!follower.isBusy()) {
                    follower.setMaxPower(1.0);
                    follower.followPath(paths.POSICAOFINAL, true);
                    setPathState(PathState.DONE);
                }
                break;

            // ---------------------------------------------------
            // DONE — Para tudo
            // ---------------------------------------------------
            case DONE:
                shooterController.emergencyStop();
                break;
        }
    }

    // -------------------------------------------------------
    // LOOP
    // -------------------------------------------------------
    @Override
    public void loop() {
        // ATUALIZA SEGUIDOR
        follower.update();

        // ATUALIZA LÓGICA DO SHOOTER
        shooterController.update();

        // ATUALIZA MÁQUINA DE ESTADOS
        statePathUpdate();

        // TELEMETRIA DE DEPURAÇÃO
        telemetry.addData("Estado Caminho",  pathState);
        telemetry.addData("Estado Shooter",  shooterController.getEstadoAtual());
        telemetry.addData("Bolinhas Atiradas", shooterController.getBolinhasAtiradas());
        telemetry.addData("Feeder Ligado",   shooterController.isFeederLigado());
        telemetry.addData("Shooter Ligado",  shooterController.isShooterLigado());
        telemetry.addData("X",               follower.getPose().getX());
        telemetry.addData("Y",               follower.getPose().getY());
        telemetry.addData("Heading (graus)", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.addData("Tempo Estado",    pathTimer.getElapsedTimeSeconds());
        telemetry.addData("Follower Busy",   follower.isBusy());

        telemetry.update();
    }
}