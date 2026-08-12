package org.firstinspires.ftc.teamcode.AUTONOMO;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.ColorSensor;

import dalvik.system.DelegateLastClassLoader;


class Passo {

    AzulDIreita.MoviFunctions type;
    double power;
    int distance; //TICKS

    Passo(AzulDIreita.MoviFunctions Tipo, double Forca, double Distancia) {

        double DiametroRODA = 10.4;
        double ticksPorVolta = 530; //MUDAR DEPOIS
        double Circunference = Math.PI * DiametroRODA;


        type = Tipo;
        power = Forca;
        distance = (int) ((Distancia / Circunference) * ticksPorVolta);
    }

}


public class AzulDIreita extends LinearOpMode {

    public enum MoviFunctions {
        PARA_FRENTE, VIRAR_DIREITA, VIRAR_ESQUERDA, PEGAR,
        SOLTAR, PARAR, OLHAR_TRAS
    }


    private IMU imu;
    private DcMotor LeftFront, LeftBack, RightFront, RightBack;
    private DcMotor Spindexer, Feeder, Shooter;
    private NormalizedColorSensor Colorsensor;
    private DistanceSensor sensordist;


    @Override
    public void runOpMode() {

        // ===> INICIALIZAÇÃO DE FUNÇÕES E INSTÂNCIAS <===


        ColorSensor ClassSensor = new ColorSensor();
        AllMechanismConfig();
        InitIMU();


        //>>>>>PATH<<<<<

        Passo[] Path = {

                new Passo(MoviFunctions.PARA_FRENTE, 0.9, 300),
                new Passo(MoviFunctions.VIRAR_ESQUERDA, 0.9, 0),
                new Passo(MoviFunctions.PARA_FRENTE, 0.9, 300),
                new Passo(MoviFunctions.VIRAR_ESQUERDA, 0.9, 0),
                new Passo(MoviFunctions.PARA_FRENTE, 0.9, 300),
                new Passo(MoviFunctions.OLHAR_TRAS, 0.9, 0),
                new Passo(MoviFunctions.PARA_FRENTE, 0.9, 300),
                new Passo(MoviFunctions.VIRAR_DIREITA, 0.9, 0),
                new Passo(MoviFunctions.PARAR, 0, 0)


        };


        waitForStart();


        String Anyball = ClassSensor.GetColor(Colorsensor);


        // >>>>PASSO A PASSO SENDO FEITO <<<<

        for (Passo passo : Path) {

            if (!opModeIsActive()) break;

            Functions(passo.type, passo.power, passo.distance);


        }


    }


    public void AllMechanismConfig() {

        LeftFront = hardwareMap.get(DcMotor.class, "leftFront");
        LeftBack = hardwareMap.get(DcMotor.class, "leftback");
        RightFront = hardwareMap.get(DcMotor.class, "rightFront");
        RightBack = hardwareMap.get(DcMotor.class, "rightBack");
        LeftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LeftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LeftFront.setDirection(DcMotor.Direction.FORWARD);
        LeftBack.setDirection(DcMotor.Direction.FORWARD);
        RightBack.setDirection(DcMotor.Direction.REVERSE);
        RightFront.setDirection(DcMotor.Direction.REVERSE);
        LeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LeftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        LeftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        sensordist = hardwareMap.get(DistanceSensor.class, "SensorCOR");
        Colorsensor = hardwareMap.get(NormalizedColorSensor.class, "SensorCOR");

        Shooter = hardwareMap.get(DcMotor.class, "shooter");
        Spindexer = hardwareMap.get(DcMotor.class, "spindexer");
        Feeder = hardwareMap.get(DcMotor.class, "Feeder");
        Shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Spindexer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Feeder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        Shooter.setDirection(DcMotor.Direction.FORWARD);

    }

    public void Functions(MoviFunctions ato, double power, int DistanciaTICKS) {

        int ticksAlvo = LeftFront.getCurrentPosition() + DistanciaTICKS;

        switch (ato) {

            case PARA_FRENTE:


                LeftFront.setTargetPosition(ticksAlvo);
                LeftBack.setTargetPosition(ticksAlvo);
                RightFront.setTargetPosition(ticksAlvo);
                RightBack.setTargetPosition(ticksAlvo);

                LeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                LeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                RightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                RightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                LeftFront.setPower(power);
                LeftBack.setPower(power);
                RightFront.setPower(power);
                RightBack.setPower(power);

                while (opModeIsActive() && (LeftFront.isBusy() || RightFront.isBusy())) {

                }

                LeftFront.setPower(0.0);
                LeftBack.setPower(0.0);
                RightFront.setPower(0.0);
                RightBack.setPower(0.0);

                //RESETANDO PAE

                LeftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                LeftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                RightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                RightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


                break;

            case VIRAR_DIREITA:

                double yawDIREITA = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
                GirarParaAngulo(yawDIREITA + 90, power,
                        LeftFront, LeftBack, RightFront, RightBack,
                        imu
                );

                break;

            case VIRAR_ESQUERDA:

                double yawESQUERDA = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
                GirarParaAngulo(yawESQUERDA - 90, power,
                        LeftFront, LeftBack, RightFront, RightBack,
                        imu
                );

                break;


            case OLHAR_TRAS:

                double yawTRAS = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
                GirarParaAngulo(yawTRAS + 180, power,
                        LeftFront, LeftBack, RightFront, RightBack,
                        imu);

                break;

            case PARAR:

                STOP();
                break;
        }


    }

    public void GirarParaAngulo(double anguloAlvo, double PotenciaMax,
                                DcMotor LeftFront, DcMotor LeftBack,
                                DcMotor RightFront, DcMotor RightBack,
                                IMU imu

    ) {

        ElapsedTime Execution_time = new ElapsedTime();

        double Kp = 0.02;
        double Kd = 0.002;
        double erro;
        double previous_error = 0;
        ElapsedTime timer = new ElapsedTime();
        boolean first_ = true;
        double TimeToDo = 3.0;
        double kstatic = 0.05;//PARA QUANDO O ERRO É MUITO PEQUENO


        do {

            double yawAtual = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);

            erro = anguloAlvo - yawAtual;



            while (erro >  180) erro -= 360;
            while (erro < -180) erro += 360;


            double dt = first_ ? 0 : timer.seconds();
            first_ = false;
            timer.reset();



            double derivate = (dt > 0) ? (erro - previous_error) / dt : 0;
            previous_error  = erro;

            double potencia = Kp * erro + Kd * derivate;

            if (Math.abs(erro) > 0.001){
                potencia += Math.signum(potencia) * kstatic;
            }

            potencia = Math.max(-PotenciaMax, Math.min(potencia, PotenciaMax));


            LeftFront.setPower(potencia);
            LeftBack.setPower(potencia);
            RightFront.setPower(-potencia);
            RightBack.setPower(-potencia);


        } while (Math.abs(erro) > 1 && opModeIsActive() && (TimeToDo > Execution_time.seconds()));

        LeftFront.setPower(0.0);
        LeftBack.setPower(0.0);
        RightFront.setPower(0.0);
        RightBack.setPower(0.0);

    }

    public void InitIMU() {

        RevHubOrientationOnRobot.LogoFacingDirection LogoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection UsbDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;
        RevHubOrientationOnRobot orientationOnRobot =
                new RevHubOrientationOnRobot(LogoDirection, UsbDirection);
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(orientationOnRobot));

    }


    public void STOP() {

        LeftFront.setPower(0.0);
        LeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LeftBack.setPower(0.0);
        LeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RightFront.setPower(0.0);
        RightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RightBack.setPower(0.0);
        RightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


    }
}
