package org.firstinspires.ftc.teamcode.AUTONOMO.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class VermelhoEsquerda extends LinearOpMode {

    private Follower follower;
    private Pose CurrentPose = new Pose(97.140, 8.5712);

    public void runOpMode(){

        follower = Constants.createFollower(hardwareMap);

        follower.setStartingPose(new Pose(124, 35));

        if (isStopRequested()) return;

        PedroPath2 path = new PedroPath2(follower, CurrentPose);
        follower.followPath(path.MainChain);


        while (opModeIsActive() && follower.isBusy()){

            follower.update();
            CurrentPose  = follower.getPose();

            telemetry.addData("X", follower.getPose().getX());
            telemetry.addData("Y", follower.getPose().getY());

        }
    }


}
class PedroPath2 {
    public PathChain MainChain;
    public PathChain MainChainArtefatDown;
    public PathChain MainChainArtefactUP;
    public PathChain MainChainArtefactNone;
    public Pose PositionCatraca = new Pose(128.0381, 70.58554729011688);


    public PedroPath2(Follower follower, Pose CurrentPose) {
        MainChain = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(104.208, 33.683),
                                new Pose(112.878, 35.932),
                                new Pose(128.481, 34.872)
                        )
                )
                .setTangentHeadingInterpolation()
                .addPath(
                        new BezierCurve(
                                new Pose(128.481, 34.872),
                                new Pose(65.065, 84.621),
                                new Pose(77.336, 72.103),
                                new Pose(89.321, 87.667)
                        )
                )
                .setTangentHeadingInterpolation()
                .addPath(
                        new BezierCurve(
                                new Pose(89.321, 87.667),
                                new Pose(90.826, 82.564),
                                new Pose(97.307, 82.844)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();


        MainChainArtefatDown = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(96.149, 74.013),
                                new Pose(98.363, 72.083)
                        )
                )
                .setTangentHeadingInterpolation()
                .addPath(
                        new BezierCurve(
                                new Pose(98.363, 72.083),
                                new Pose(106.116, 57.224),
                                new Pose(125.282, 58.139)
                        )
                )
                .setTangentHeadingInterpolation()
                .addPath(
                        new BezierCurve(
                                new Pose(125.282, 58.139),
                                new Pose(80.307, 82.673),
                                new Pose(80.449, 83.456),
                                new Pose(95.661, 96.982)
                        )
                )
                .setTangentHeadingInterpolation()
                .addPath(
                        new BezierCurve(
                                new Pose(95.661, 96.982),
                                new Pose(104.208, 67.517),
                                new Pose(128.622, 68.753)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();


        MainChainArtefactUP = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(100.298, 74.584),
                                new Pose(107.876, 83.810),
                                new Pose(125.484, 82.494)
                        )
                )
                .setTangentHeadingInterpolation()
                .addPath(
                        new BezierCurve(
                                new Pose(125.484, 82.494),
                                new Pose(105.775, 90.020),
                                new Pose(102.704, 101.802)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(43))
                .addPath(
                        new BezierCurve(
                                new Pose(102.704, 101.802),
                                new Pose(103.061, 66.866),
                                new Pose(129.371, 68.892)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();
    }
}