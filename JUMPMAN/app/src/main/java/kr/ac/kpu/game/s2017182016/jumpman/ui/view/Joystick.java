package kr.ac.kpu.game.s2017182016.jumpman.ui.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import kr.ac.kpu.game.s2017182016.jumpman.framework.GameObject;

public class Joystick implements GameObject {

    private Paint inCP;
    private Paint outCP;
    private int inCR;
    private int outCR;
    private int inCCY;
    private int inCCX;
    private int outCCY;
    private int outCCX;
    private double joystickCircle2TouchDistance;
    private boolean isPressed;
    private double actuatorX;
    private double actuatorY;

    public Joystick(int centerX, int centerY, int outCR, int inCR){
        outCCX = centerX;
        outCCY = centerY;
        inCCX = centerX;
        inCCY = centerY;

        this.outCR = outCR;
        this.inCR = inCR;
        outCP = new Paint();
        outCP.setColor(Color.GRAY);
        outCP.setStyle(Paint.Style.FILL_AND_STROKE);

        inCP = new Paint();
        inCP.setColor(Color.BLUE);
        inCP.setStyle(Paint.Style.FILL_AND_STROKE);

    }


    @Override
    public void update() {
        updateInCircle();
    }

    private void updateInCircle() {
        inCCX = (int) (outCCX + actuatorX*outCR);
        inCCY = (int) (outCCY + actuatorY*outCR);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(
                outCCX,
                outCCY,
                outCR,
                outCP
        );
        canvas.drawCircle(
                inCCX,
                inCCY,
                inCR,
                inCP
        );
    }

    public boolean isPressed(double touchX, double touchY) {
        joystickCircle2TouchDistance = Math.sqrt(
                Math.pow(outCCX - touchX, 2) +
                Math.pow(outCCY - touchY, 2)
        );
        return joystickCircle2TouchDistance < outCR;
    }

    public void setIsPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    public boolean getIsPressed() {
        return isPressed;
    }

    public void setActuator(double touchX, double touchY) {
        double dx = touchX - outCCX;
        double dy = touchY - outCCY;
        double ddistance = Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));
        if(ddistance < outCR){
            actuatorX = dx/outCR;
            actuatorY = dy/outCR;
        }else{
            actuatorX = dx/ddistance;
            actuatorY = dy/ddistance;
        }
    }

    public void resetActuator() {
        actuatorX = 0.0;
        actuatorY = 0.0;
    }

    public double getActuatorX() {
        return actuatorX;
    }
}
