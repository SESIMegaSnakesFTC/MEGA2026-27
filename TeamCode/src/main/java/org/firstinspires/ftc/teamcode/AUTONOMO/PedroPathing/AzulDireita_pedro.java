package org.firstinspires.ftc.teamcode.AUTONOMO.PedroPathing;


import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;



@Autonomous(name = "AzulDireita_Pedro")
public class AzulDireita_pedro extends LinearOpMode {



    private Follower follower;

    private final Pose init_Position = new Pose(10, 137, Math.toRadians(90));//BASE DIREITA
    private final Pose ponto2 = new Pose(40, 72, Math.toRadians(0));//MEIO TERMO CENTRO
    private final Pose centro = new Pose( 72, 72, Math.toRadians(0));//IR PARA O CENTRO
    private final Pose ponto4 = new Pose(10, 13, Math.toRadians(90));//BASE ESQUERDA
    private DcMotor LeftFront, LeftBack, RightFront, RightBack;

    private PathChain caminho1, caminho2, caminho3;


    @Override
    public void runOpMode(){

        PathChain[] Path = {
            caminho1,
            caminho2,
            caminho3
        };

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(init_Position);


        ConstruirPATH();
        waitForStart();


        for(PathChain path : Path){

            while(opModeIsActive() && follower.isBusy()) {

                follower.followPath(path);
                follower.update();
                telemetry.addData("X", follower.getPose().getX());
                telemetry.addData("Y", follower.getPose().getY());
                telemetry.update();
            }


        }

    }

    public void ConstruirPATH(){

        caminho1 = follower.pathBuilder().addPath(new BezierLine(
                init_Position, ponto4))
                .setLinearHeadingInterpolation(init_Position.getHeading(), ponto4.getHeading()).build();

        caminho2 = follower.pathBuilder().addPath(new BezierLine(
                ponto4, ponto2))
                .setLinearHeadingInterpolation(ponto4.getHeading(), ponto2.getHeading()).build();

        caminho3 = follower.pathBuilder().addPath(new BezierLine(
                ponto2, centro))
                .setLinearHeadingInterpolation(centro.getHeading(), ponto2.getHeading()).build();




    }

}
