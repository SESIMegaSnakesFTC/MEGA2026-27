package org.firstinspires.ftc.teamcode.limelight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


@Disabled
public class limelightGetDistance extends LinearOpMode {

    private Limelight3A limelight;

    //>>>Valores para a trigonometria <<<

    private final double HeighLimelight = 21; //CM
    private final double AngleLimelight = 0;
    private final double HeightObejct   = 4.9; //CENTRO Pólen


    boolean ArtefatoMode = true;




    public void runOpMode(){

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.start();
        limelight.setPollRateHz(90);
        limelight.pipelineSwitch(9);




        waitForStart();

        boolean lastA = false;
        while(opModeIsActive()){
            boolean currentA = gamepad1.a;

            LLResult result = limelight.getLatestResult();
            double Distancia = GetDistanceObject(result);

            // ==>MODOS DE OPERAÇÃO<==

            ArtefatoMode = !(gamepad1.a && !lastA);

            lastA = currentA;

            telemetry.addData("DISTANCIA-CM", Distancia);
            telemetry.update();

        }
    }
    public double GetDistanceObject(LLResult result){

        if (result == null || !result.isValid()) return 0;

        double getTY = result.getTy();
        double AngleToTarget = Math.toRadians(HeighLimelight);

        double distance = (HeightObejct - HeighLimelight / Math.tan(AngleToTarget));

        return Math.abs(distance);


    }

    @TeleOp(name = "Tracking Lime", group = "TeleOp")
    public static class Limelight_Tracking_aprilTag extends LinearOpMode {

        private DcMotor limeMotor;
        private Limelight3A limelight;
        private enum actions {LEFT, RIGHT, NONE}

        private actions act = actions.NONE;
        private boolean lastAPressed = false;
        private boolean ModoTracking = false;

        private double kP = 0.03;
        private double Speed = 0.09; //VELOCIDADE DE PROCURA

        @Override
        public void runOpMode(){
            limelight = hardwareMap.get(Limelight3A.class, "limelight");
            limeMotor = hardwareMap.get(DcMotor.class, "limeMotor");
            limeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            limelight.setPollRateHz(90);
            limelight.start();
            limelight.pipelineSwitch(9); // APRIL TAG

            waitForStart();

            while (opModeIsActive()){

                boolean currentAPressed = gamepad1.a;

                ModoTracking = (gamepad1.a && !lastAPressed);

                lastAPressed = currentAPressed;

                if (ModoTracking){

                    RunModoTracking();
                }
                else StopSearching();
            }
        }

        private void StopSearching(){

            limeMotor.setPower(0);
            //>>>PARA DE PROCURAR<<<

        }
        private void RunModoTracking(){

            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()){

                double TX = result.getTx();

                act = (TX > 0) ? actions.RIGHT : ( TX < 0 ? actions.LEFT : actions.NONE);

                double correction = -TX *kP;

                correction = Math.max(-0.3987, Math.min(0.3987, correction));

                limeMotor.setPower(correction);
            }
            else{

                //PROCURA POR ONDE ELA FOI VISTA PELA ÚLTIMA VEZ

                switch (act){

                    case LEFT:
                        limeMotor.setPower(Speed);
                        break;

                    case RIGHT:
                        limeMotor.setPower(-Speed);
                        break;

                    case NONE:
                        limeMotor.setPower(Speed);
                        break;
                }
            }

        }
    }
}
