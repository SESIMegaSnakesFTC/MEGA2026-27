package org.firstinspires.ftc.teamcode.AUTONOMO.PID;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class GiroPID {

 //===== VARIÁVEIS DE AJUSTE ;( =====
    private double Kp = 0.02;
    private double Kd = 0.002;
    private double kStatic = 0.05;
    private double alphaFiltro = 0.3;

    private double toleranciaEmGraus = 1.0;
    private double tempoEstabilidade = 0.2;
    private double timeoutSegundos = 3.0;


 //===== ESTADO PARA EXECUÇÃO =====
    private double anguloAlvo;
    private double potenciaMAX;
    private double previousError    = 0;
    private double derivadaFiltrada = 0;
    private boolean firstCall       = true;
    private boolean INtolerancia    = false;
    private boolean finished        = false;

    private ElapsedTime timerDT      = new ElapsedTime();
    private ElapsedTime timerTOTAL   = new ElapsedTime();
    private ElapsedTime timerEstavel = new ElapsedTime();

    /*
    PARA O OUTRO PROGRAMADOR ==> USE A FUNÇÃO NO INÍCIO ANTES DE COMEÇAR
    PARA DEFINIR O ALVO E REDEFINR ESTADOS
     */

    public void iniciarPID(double AnguloAlvo, double potenciaMAX){

       this.anguloAlvo       = anguloAlvo;
       this.potenciaMAX      = potenciaMAX;
       this.previousError    = 0;
       this.derivadaFiltrada = 0;
       this.firstCall        = true;
       this.INtolerancia     = false;
       this.finished         = false;
       this.timerTOTAL.reset();
    }

    public void UPDATE(DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack, IMU imu){

       if (finished) return;
       double yawAtual = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
       double erro     = anguloAlvo - yawAtual;

       while (erro >  180) erro -= 360;
       while (erro < -180) erro += 360;


       double dt = firstCall ? 0 : timerDT.seconds();
       firstCall = false;
       timerDT.reset();

       double derivadaBruta = (dt>0) ? (erro - previousError) / dt : 0;
       previousError = erro;
       derivadaFiltrada = alphaFiltro * derivadaBruta + (1 - alphaFiltro) * derivadaFiltrada;

       double potencia = (Kp*erro) + (Kd*derivadaFiltrada);

       if (Math.abs(potencia) > 0.001){
          potencia += Math.signum(potencia) * kStatic;
       }

       potencia = Math.max(-potenciaMAX, Math.min(potencia, potenciaMAX));

       leftFront.setPower(potencia);
       leftBack.setPower(potencia);
       rightFront.setPower(-potencia);
       rightBack.setPower(-potencia);

       if (Math.abs(erro) <= toleranciaEmGraus){

          if (!INtolerancia){

            timerEstavel.reset();
            INtolerancia = true;

          }else INtolerancia = false;

       }

       boolean IsEstabilizado   = INtolerancia && timerEstavel.seconds() >= tempoEstabilidade;
       boolean ultrapassouTempo = timerTOTAL.seconds() >= timeoutSegundos;

       if (IsEstabilizado || ultrapassouTempo){
          finished = true;
          leftFront.setPower(0.0);leftBack.setPower(0.0);
          rightFront.setPower(0.0);rightBack.setPower(0.0);
       }
    }

    public boolean FINISHED(){ return finished; }
}


