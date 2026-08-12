package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

public class TesteForServos extends LinearOpMode {


    // >>>>> Servos de rotação --> VELOCIDADE DE GIRO ---> CRServo <<<<<
    // >>>>> Servos de posição --> POSIÇÃO ATUAL ---> Servo <<<<<
    private Servo servoPos = null;
    boolean PosUM = false;
    int counter = 0;


    public void runOpMode(){


        servoPos = hardwareMap.get(Servo.class, "servoLeft");


        while(opModeIsActive()){


           if (gamepad2.aWasPressed() && !PosUM){

               servoPos.setPosition(1.0);

               counter++;

           }
           else if (gamepad2.aWasPressed() && PosUM){

                servoPos.setPosition(-1.0);
                counter--;

           }
           else{ servoPos.setPosition(0); }

           telemetry.addData("posição em Um", PosUM);
           telemetry.addData("Posição atual", servoPos.getPosition());
           telemetry.update();

        }

    }
}
