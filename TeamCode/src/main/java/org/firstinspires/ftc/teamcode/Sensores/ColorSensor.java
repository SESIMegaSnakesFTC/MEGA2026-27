package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class ColorSensor extends LinearOpMode {


    private NormalizedColorSensor SensorCOR;
    private DistanceSensor SensorDist;

    public void runOpMode(){

        SensorCOR      = hardwareMap.get(NormalizedColorSensor.class, "SensorCor");
        SensorDist     = hardwareMap.get(DistanceSensor.class, "SensorCor");



        waitForStart();

        while (opModeIsActive()) {

            String COLOR = GetColor(SensorCOR);

            if (CLOSER(SensorDist)) {
                if (Ball(COLOR)) {

                    PegarBolinha();

                }



            }

            telemetry.addData("COR", COLOR);
            telemetry.addData("Any Ball", Ball(COLOR));
            telemetry.update();


        }
    }


    public String GetColor(NormalizedColorSensor SensorCOR){

        NormalizedRGBA cores = SensorCOR.getNormalizedColors();

        String CorDetectada;

        float[] hsv = new float[3];
        Color.RGBToHSV (
                (int)(cores.red*255),
                (int)(cores.green*255),
                (int)(cores.blue*255),
                hsv
        );
        float matiz = hsv[0];

        if (matiz < 30 || matiz > 330 ){
            CorDetectada = "VERMELHO";
        }
        else if (matiz > 45 && matiz < 75){
            CorDetectada = "AMARELO";
        }
        else if (matiz > 90 && matiz <150){
            CorDetectada = "VERDE";
        }
        else if (matiz > 200 && matiz <260) {
            CorDetectada = "AZUL";
        }
        else{
            CorDetectada = "None";
        }
        return CorDetectada;
    }
    public boolean Ball(String COR){ return COR.equals("AMARELO");}

    public boolean CLOSER(DistanceSensor Sensor){ return Sensor.getDistance(DistanceUnit.CM) < 25; }
}

