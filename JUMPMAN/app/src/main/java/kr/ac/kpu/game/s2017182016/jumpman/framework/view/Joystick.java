package kr.ac.kpu.game.s2017182016.jumpman.framework.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;

public class Joystick implements GameObject {

    private final Paint inCP;
    private final Paint outCP;
    private final int inCR;
    private final int outCR;
    private int inCCY;
    private int inCCX;
    private final int outCCY;
    private final int outCCX;
    private boolean isPressed;
    private int pointerId = -1;
    private double actuatorX;
    private double actuatorY;
    private boolean enabled = true;

    public Joystick(int centerX, int centerY, int outCR, int inCR) {
        outCCX = centerX;
        outCCY = centerY;
        inCCX = centerX;
        inCCY = centerY;

        this.outCR = outCR;
        this.inCR = inCR;
        outCP = new Paint(Paint.ANTI_ALIAS_FLAG);
        outCP.setColor(Color.argb(120, 90, 90, 100));
        outCP.setStyle(Paint.Style.FILL_AND_STROKE);

        inCP = new Paint(Paint.ANTI_ALIAS_FLAG);
        inCP.setColor(Color.argb(180, 80, 130, 220));
        inCP.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    public void update() {
        updateInCircle();
    }

    private void updateInCircle() {
        inCCX = (int) (outCCX + actuatorX * outCR);
        inCCY = (int) (outCCY + actuatorY * outCR);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            setIsPressed(false);
            resetActuator();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!enabled) {
            return;
        }
        canvas.drawCircle(outCCX, outCCY, outCR, outCP);
        canvas.drawCircle(inCCX, inCCY, inCR, inCP);
    }

    public boolean isPressed(double touchX, double touchY) {
        if (!enabled) {
            return false;
        }
        double distance = Math.sqrt(
                Math.pow(outCCX - touchX, 2) +
                        Math.pow(outCCY - touchY, 2)
        );
        return distance < outCR;
    }

    /** Joystick 주변에서는 점프 입력을 막는다. */
    public boolean blocksJump(double touchX, double touchY) {
        if (!enabled) {
            return false;
        }
        double distance = Math.sqrt(
                Math.pow(outCCX - touchX, 2) +
                        Math.pow(outCCY - touchY, 2)
        );
        return distance < outCR * 2.2f;
    }

    public int getOutCCX() {
        return outCCX;
    }

    public int getOutCCY() {
        return outCCY;
    }

    public int getOutCR() {
        return outCR;
    }

    public void setIsPressed(boolean pressed) {
        this.isPressed = pressed;
        if (!pressed) {
            pointerId = -1;
        }
    }

    public void setIsPressed(boolean pressed, int pointerId) {
        this.isPressed = pressed;
        this.pointerId = pressed ? pointerId : -1;
    }

    public boolean getIsPressed() {
        return isPressed;
    }

    public int getPointerId() {
        return pointerId;
    }

    public void setActuator(double touchX, double touchY) {
        double dx = touchX - outCCX;
        double dy = touchY - outCCY;
        double ddistance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        if (ddistance < outCR) {
            actuatorX = dx / outCR;
            actuatorY = dy / outCR;
        } else if (ddistance > 0) {
            actuatorX = dx / ddistance;
            actuatorY = dy / ddistance;
        }
    }

    public void resetActuator() {
        actuatorX = 0.0;
        actuatorY = 0.0;
    }

    public double getActuatorX() {
        return enabled ? actuatorX : 0.0;
    }

    public double getActuatorY() {
        return enabled ? actuatorY : 0.0;
    }
}
