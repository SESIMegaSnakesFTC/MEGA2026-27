package org.firstinspires.ftc.teamcode.AUTONOMO.pedroPathing;


import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class Modelo extends LinearOpMode {

    private Follower follower;

    public void runOpMode(){

        waitForStart();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose (56, 9, Math.toRadians(0)));

        PathChain mainChain = follower.pathBuilder()
                .addPath(new BezierLine(
                        new Pose (56.000, 9.000),
                        new Pose(39.000, 33.293)
                )
                )
                .setLinearHeadingInterpolation(Math.toRadians(19), Math.toRadians(90))
                // >>>> caso haja mais passos <<<<
                .build();

        waitForStart();

        if (isStopRequested()) return;

      // ==> Isso sempre fora do loop <==
        follower.followPath(mainChain);
    //======================================

        while(opModeIsActive() && follower.isBusy()){

            follower.update();
            telemetry.addData("Position X", follower.getPose().getX());
            telemetry.addData("Position Y", follower.getPose().getY());
            telemetry.update();

        }


    }
}
