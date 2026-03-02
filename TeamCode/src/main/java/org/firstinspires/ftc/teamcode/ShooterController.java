package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class ShooterController {

    // Hardware
    private DcMotor shooterDireito;
    private DcMotor shooterEsquerdo;
    private DcMotorEx feeder;
    private Servo alavancaServo;

    // Timer de estado
    private ElapsedTime stateTimer = new ElapsedTime();

    // -------------------------------------------------------
    // ESTADOS DO CICLO DE SHOOT
    // -------------------------------------------------------
    public enum ShootState {
        IDLE,
        ACELERANDO_SHOOTER,   // Shooter ligado, aguardando atingir velocidade
        PRONTO_PARA_ATIRAR,   // Shooter na velocidade — aguarda comecarATirar()
        ATIRANDO_BOLINHA_1,   // Feeder ligado, alimentando bolinha 1
        ESPERANDO_1_2,        // Pausa entre bolinha 1 e 2
        ATIRANDO_BOLINHA_2,   // Alimentando bolinha 2
        ESPERANDO_2_3,        // Pausa entre bolinha 2 e 3
        PREPARANDO_BOLINHA_3, // Aciona servo para subir bolinha 3
        ATIRANDO_BOLINHA_3,   // Alimentando bolinha 3
        FINALIZANDO           // Desliga tudo e volta ao IDLE
    }

    private ShootState currentState = ShootState.IDLE;

    // -------------------------------------------------------
    // CONSTANTES DE TEMPO (ajuste conforme necessário)
    // -------------------------------------------------------
    private static final double TEMPO_ACELERACAO      = 2.0; // segundos até shooter estar na velocidade
    private static final double TEMPO_FEEDER_BOLINHA  = 0.8; // tempo de feeder por bolinha
    private static final double TEMPO_ENTRE_BOLINHAS  = 1.0; // pausa entre bolinhas
    private static final double TEMPO_SERVO_ACIONA    = 0.3; // tempo servo acionado

    // -------------------------------------------------------
    // CONSTANTES DE HARDWARE
    // -------------------------------------------------------
    private static final double POTENCIA_SHOOTER       = -0.8;  // TIRO DE LONGE
    private static final double POTENCIA_FEEDER        = -1.0;
    private static final double POSICAO_SERVO_ATIRAR   = 0.8;
    private static final double POSICAO_SERVO_REPOUSO  = 0.2;

    // -------------------------------------------------------
    // CONTROLE INTERNO
    // -------------------------------------------------------
    private int     bolinhasAtiradas  = 0;
    private boolean shooterLigado     = false;
    private boolean feederLigado      = false;

    // -------------------------------------------------------
    // INIT
    // -------------------------------------------------------
    public void init(HardwareMap hardwareMap, DcMotorEx feederMotor) {
        shooterDireito  = hardwareMap.get(DcMotor.class, "shooterDireito");
        shooterEsquerdo = hardwareMap.get(DcMotor.class, "shooterEsquerdo");

        // Mesma direção do TeleopDEFAULT
        shooterDireito.setDirection(DcMotor.Direction.REVERSE);

        shooterDireito.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterEsquerdo.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        this.feeder = feederMotor;

        alavancaServo = hardwareMap.get(Servo.class, "alavancaServo");
        alavancaServo.setPosition(POSICAO_SERVO_REPOUSO);
    }

    // -------------------------------------------------------
    // HELPERS PRIVADOS
    // -------------------------------------------------------
    private void ligarShooter() {
        if (!shooterLigado) {
            shooterDireito.setPower(POTENCIA_SHOOTER);
            shooterEsquerdo.setPower(POTENCIA_SHOOTER);
            shooterLigado = true;
        }
    }

    private void desligarShooter() {
        if (shooterLigado) {
            shooterDireito.setPower(0);
            shooterEsquerdo.setPower(0);
            shooterLigado = false;
        }
    }

    private void ligarFeeder() {
        if (!feederLigado) {
            feeder.setPower(POTENCIA_FEEDER);
            feederLigado = true;
        }
    }

    private void desligarFeeder() {
        if (feederLigado) {
            feeder.setPower(0);
            feederLigado = false;
        }
    }

    // -------------------------------------------------------
    // API PÚBLICA
    // -------------------------------------------------------

    /**
     * Inicia o pré-carregamento: liga o shooter e começa a acelerar.
     * Deve ser chamado no início do movimento para IDASHOOT1.
     */
    public void iniciarCiclo3Bolinhas() {
        if (currentState == ShootState.IDLE) {
            bolinhasAtiradas = 0;
            ligarShooter();
            currentState = ShootState.ACELERANDO_SHOOTER;
            stateTimer.reset();
        }
    }

    /**
     * Dispara a sequência de tiro. Só tem efeito quando isReadyToShoot() == true.
     * Deve ser chamado pelo vermelhoBaixo no estado ESPERA_SHOOT1.
     */
    public void comecarATirar() {
        if (currentState == ShootState.PRONTO_PARA_ATIRAR) {
            ligarFeeder();
            currentState = ShootState.ATIRANDO_BOLINHA_1;
            stateTimer.reset();
        }
    }

    /**
     * Para de emergência: desliga tudo e volta ao IDLE.
     */
    public void emergencyStop() {
        desligarFeeder();
        desligarShooter();
        if (alavancaServo != null) {
            alavancaServo.setPosition(POSICAO_SERVO_REPOUSO);
        }
        currentState = ShootState.IDLE;
    }

    // -------------------------------------------------------
    // UPDATE — chame em todo loop()
    // -------------------------------------------------------
    public void update() {
        switch (currentState) {

            case ACELERANDO_SHOOTER:
                // Shooter já está ligado; aguarda acelerar
                if (stateTimer.seconds() >= TEMPO_ACELERACAO) {
                    currentState = ShootState.PRONTO_PARA_ATIRAR;
                    stateTimer.reset();
                }
                break;

            case PRONTO_PARA_ATIRAR:
                // Aguarda chamada de comecarATirar()
                break;

            case ATIRANDO_BOLINHA_1:
                // Feeder já está ligado (ligado em comecarATirar)
                if (stateTimer.seconds() >= TEMPO_FEEDER_BOLINHA) {
                    bolinhasAtiradas = 1;
                    currentState = ShootState.ESPERANDO_1_2;
                    stateTimer.reset();
                }
                break;

            case ESPERANDO_1_2:
                // Feeder continua ligado durante a pausa
                if (stateTimer.seconds() >= TEMPO_ENTRE_BOLINHAS) {
                    currentState = ShootState.ATIRANDO_BOLINHA_2;
                    stateTimer.reset();
                }
                break;

            case ATIRANDO_BOLINHA_2:
                // Feeder continua ligado
                if (stateTimer.seconds() >= TEMPO_FEEDER_BOLINHA) {
                    bolinhasAtiradas = 2;
                    currentState = ShootState.ESPERANDO_2_3;
                    stateTimer.reset();
                }
                break;

            case ESPERANDO_2_3:
                // Feeder continua ligado durante a pausa
                if (stateTimer.seconds() >= TEMPO_ENTRE_BOLINHAS) {
                    currentState = ShootState.PREPARANDO_BOLINHA_3;
                    stateTimer.reset();
                }
                break;

            case PREPARANDO_BOLINHA_3:
                // Aciona o servo para subir a bolinha 3
                alavancaServo.setPosition(POSICAO_SERVO_ATIRAR);
                if (stateTimer.seconds() >= TEMPO_SERVO_ACIONA) {
                    currentState = ShootState.ATIRANDO_BOLINHA_3;
                    stateTimer.reset();
                }
                break;

            case ATIRANDO_BOLINHA_3:
                // Feeder continua; aguarda alimentar a última bolinha
                if (stateTimer.seconds() >= TEMPO_FEEDER_BOLINHA) {
                    currentState = ShootState.FINALIZANDO;
                    stateTimer.reset();
                }
                break;

            case FINALIZANDO:
                // Desliga feeder, shooter e volta servo ao repouso
                desligarFeeder();
                desligarShooter();
                alavancaServo.setPosition(POSICAO_SERVO_REPOUSO);
                bolinhasAtiradas = 3;
                currentState = ShootState.IDLE;
                break;

            case IDLE:
            default:
                break;
        }
    }

    // -------------------------------------------------------
    // MÉTODOS DE CONSULTA
    // -------------------------------------------------------

    /** Estado atual da máquina de tiro */
    public ShootState getEstadoAtual() { return currentState; }

    /** true quando shooter está acelerado e aguardando comecarATirar() */
    public boolean isReadyToShoot() { return currentState == ShootState.PRONTO_PARA_ATIRAR; }

    /** true quando o ciclo terminou (ou nunca começou) */
    public boolean isIdle() { return currentState == ShootState.IDLE; }

    /** true enquanto alguma fase do ciclo está em andamento */
    public boolean isShooting() { return currentState != ShootState.IDLE; }

    /** Quantas bolinhas já foram atiradas neste ciclo */
    public int getBolinhasAtiradas() { return bolinhasAtiradas; }

    /** true se o motor shooter está ligado */
    public boolean isShooterLigado() { return shooterLigado; }

    /** true se o feeder está ligado */
    public boolean isFeederLigado() { return feederLigado; }

    /** Tempo decorrido no estado atual */
    public double getTempoEstadoAtual() { return stateTimer.seconds(); }
}