package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "MyTeleOP", group = "Linear")
public class TeleOperado_ extends LinearOpMode {


    private boolean RTpressedBefore = false;
    private boolean LTpressedBefore = false;


        //>>> DcMotors <<<


    private DcMotor leftFront, leftBack, rightFront, rightBack  = null;

        // >>> Mechanisms <<<
    private DcMotor spindexer, shooter, feeder = null;


    public void runOpMode(){

        //>>> Initial configuration of DcMotors <<<

        leftFront = hardwareMap.get(DcMotor.class, "leftFront" );
        leftBack = hardwareMap.get(DcMotor.class, "leftback");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


            // >>> MECHANISMS <<<

        spindexer = hardwareMap.get(DcMotor.class, "Spindexer");
        shooter   = hardwareMap.get(DcMotor.class, "Shooter");
        feeder    = hardwareMap.get(DcMotor.class, "Feeder");


        spindexer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        feeder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);


        waitForStart();

        while (opModeIsActive()){

            // >>> Variables for robots moviments <<

            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x*1.1;
            double rx = gamepad1.right_stick_x;



            boolean RT = gamepad2.right_bumper;
            boolean LT = gamepad2.left_bumper;

            float LastRB = gamepad2.right_trigger;
            float LastLB = gamepad2.left_trigger;



            // >>>> Control RX <<<<

            rx = Math.copySign(rx * rx, rx);


            // >>>> Mathematics formula for Motor powers <<<<

            double Median = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

            /*

            leftFront e rightBack reagem igual ao "x" (lateral) com o mesmo sinal.
            leftBack e rightFront reagem com sinal invertido.
            O giro (rx) soma nas rodas da esquerda e subtrai nas da direita (ou vice-versa,
            dependendo de como você define "girar horário")

            */

            double leftFrontPower  = ( y + x + rx ) / Median;
            double leftBackPower   = ( y - x + rx ) / Median;
            double rightFrontPower = ( y - x - rx ) / Median;
            double rightBackPower  = ( y + x - rx ) / Median;



            // >>> Putting the Motor power on Dcmotors <<<

            leftFront.setPower(leftFrontPower);
            leftBack.setPower(leftBackPower);
            rightFront.setPower(rightFrontPower);
            rightBack.setPower(rightBackPower);



        // >>>>> SPINDEXER --> SHOOTER <<<<<

            if ( LT && !RT ) {

                if (LTpressedBefore) {

                    spindexer.setPower(0.0);
                    shooter.setPower(0.0);

                    LTpressedBefore = false;


                } else {

                    spindexer.setDirection(DcMotor.Direction.REVERSE);
                    shooter.setDirection(DcMotor.Direction.REVERSE);

                    spindexer.setPower(0.9);
                    shooter.setPower(1.0);


                    LTpressedBefore = true;


                }
                RTpressedBefore = false;
            }


            else if (!LT && RT){

                if ( RTpressedBefore ){

                    spindexer.setPower(0.0);
                    shooter.setPower(0.0);

                    RTpressedBefore = false;

                }

                else{

                    spindexer.setDirection(DcMotor.Direction.FORWARD);
                    shooter.setDirection(DcMotor.Direction.FORWARD);
                    spindexer.setPower(0.9);
                    shooter.setPower(1);

                    RTpressedBefore = true;

                }

                LTpressedBefore = false;
            }

                // >>>>>> FEEDER <<<<<<<

            if (LastLB >= 0.09 && LastRB < 0.1){

                feeder.setDirection(DcMotor.Direction.REVERSE);
                feeder.setPower(1.0);

            }
            else if(LastRB >= 0.09 && LastLB < 0.1 ){

                feeder.setDirection(DcMotor.Direction.FORWARD);
                feeder.setPower(1.0);

            }

            else{ feeder.setPower(0); }



            // >>> Statistics for inspection <<<

            telemetry.addData("y", y);
            telemetry.addData("x", x);
            telemetry.addData("rx", rx);
            telemetry.update();

        }

    }

}