package org.firstinspires.ftc.teamcode.limelight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


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
}
