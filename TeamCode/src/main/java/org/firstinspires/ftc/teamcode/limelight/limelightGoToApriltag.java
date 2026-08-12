package org.firstinspires.ftc.teamcode.limelight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


public class limelightGoToApriltag extends LinearOpMode {


    private Limelight3A limelight3A;

    public void runOpMode(){

        limelight3A = hardwareMap.get(Limelight3A.class, "limelight");


        waitForStart();

        while(opModeIsActive()){

            LLResult result = limelight3A.getLatestResult();

            if (result != null && result.isValid()){

                linharAprilTag();
            }


        }
    }
    public void linharAprilTag(){

    }
}
