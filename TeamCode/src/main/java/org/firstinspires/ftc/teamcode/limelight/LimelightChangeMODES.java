package org.firstinspires.ftc.teamcode.limelight;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class LimelightChangeMODES extends LinearOpMode {

    // 1 --> APRIL TAG
    // 2 --> ARTEFATO

    //limelight3A.getPipelineIndex() --> retorna o modo em uso

    int limelightSTATE = 1;

    private Limelight3A limelight3A;

    public void runOpMode(){

        limelight3A = hardwareMap.get(Limelight3A.class, "limelight");
        limelight3A.setPollRateHz(90);
        limelight3A.start();

        waitForStart();

        while (opModeIsActive()){

            switch (limelightSTATE){

                case 1:
                    limelight3A.pipelineSwitch(8);

                case 2:
                    limelight3A.pipelineSwitch(9);
            }

            if (limelightSTATE == 1 && gamepad1.a) limelightSTATE = 2;

            else if (limelightSTATE == 2 && gamepad1.a) limelightSTATE = 1;

        }

    }
}
