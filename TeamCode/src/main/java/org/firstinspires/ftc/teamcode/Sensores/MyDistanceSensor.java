package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


@TeleOp(name = "Sensor de distância", group = "TeleOp")
public class MyDistanceSensor extends LinearOpMode {

    private DistanceSensor distanceSensor;

    public void runOpMode(){

        distanceSensor = hardwareMap.get(DistanceSensor.class, "SensorDistance");


        while (opModeIsActive()){

            telemetry.addData("ACTUAL_DISTANCE", GetDistance());
            telemetry.addData("NextToObject", IsClose());

        }

    }



    public double GetDistance(){ return distanceSensor.getDistance(DistanceUnit.CM); }

    public boolean IsClose(){ return GetDistance() <= 7.5; }
}
