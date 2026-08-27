package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Teste Dois Servos", group = "Teste")
public class TWOServos extends LinearOpMode {

    private enum SIDE_SERVO {ESQUERDO, DIREITO, DESLIGADO}
    private Servo leftServo, rightServo;
    private double TEMPO_ESPERA = 1.5;
    private double SERVO_ATIVO = 0.48;


    private static final double POS_ZERO_ESQUERDA = 0;
    private static final double POS_ZERO_DIREITA  = 0.1754;

    @Override
    public void runOpMode(){

        leftServo  = hardwareMap.get(Servo.class, "servoLeft");
        rightServo = hardwareMap.get(Servo.class, "servoRight");

        // Manda o zero já de cara, antes do waitForStart, pra garantir estado conhecido
        leftServo.setPosition(POS_ZERO_ESQUERDA);
        rightServo.setPosition(POS_ZERO_DIREITA);

        waitForStart();

        boolean LastRT = false;
        boolean LastLT = false;
        ElapsedTime timer = new ElapsedTime();
        SIDE_SERVO action = SIDE_SERVO.DESLIGADO;

        while(opModeIsActive()){

            boolean RT = gamepad2.right_trigger > 0.5;
            boolean LT = gamepad2.left_trigger  > 0.5;

            if (RT) {
                if (!LastRT) timer.reset();
                if (timer.seconds() >= TEMPO_ESPERA) action = SIDE_SERVO.DIREITO;
                else action = SIDE_SERVO.DESLIGADO;
            } else if (LT) {
                if (!LastLT) timer.reset();
                if (timer.seconds() >= TEMPO_ESPERA) action = SIDE_SERVO.ESQUERDO;
                else action = SIDE_SERVO.DESLIGADO;
            } else action = SIDE_SERVO.DESLIGADO;

            switch (action){
                case DIREITO:
                    rightServo.setPosition(Range.clip(POS_ZERO_DIREITA + SERVO_ATIVO, 0.0, 1.0));
                    leftServo.setPosition(POS_ZERO_ESQUERDA);
                    break;

                case ESQUERDO:
                    leftServo.setPosition(Range.clip(POS_ZERO_ESQUERDA + SERVO_ATIVO, 0.0, 1.0));
                    rightServo.setPosition(POS_ZERO_DIREITA);
                    break;

                case DESLIGADO:
                    leftServo.setPosition(POS_ZERO_ESQUERDA);
                    rightServo.setPosition(POS_ZERO_DIREITA);
                    break;
            }

            telemetry.addData("Ação", action);
            telemetry.addData("Timer", "%.2f s", timer.seconds());
            telemetry.addData("Status", (action == SIDE_SERVO.DESLIGADO && (RT || LT)) ? "AGUARDANDO..." : "OK");
            telemetry.update();

            LastRT = RT;
            LastLT = LT;
        }
    }
}