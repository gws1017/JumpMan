package kr.ac.kpu.game.s2017182016.jumpman.framework.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;

/**
 * 좌우만 있는 십자키. 이 게임에서는 조이스틱보다 직관적이다.
 */
public class LeftRightPad implements GameObject {
    private final RectF leftRect = new RectF();
    private final RectF rightRect = new RectF();
    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pressedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean leftPressed;
    private boolean rightPressed;
    private int leftPointerId = -1;
    private int rightPointerId = -1;

    public LeftRightPad(float left, float top, float buttonW, float buttonH, float gap) {
        leftRect.set(left, top, left + buttonW, top + buttonH);
        rightRect.set(left + buttonW + gap, top, left + buttonW + gap + buttonW, top + buttonH);

        bgPaint.setColor(Color.argb(140, 60, 60, 70));
        bgPaint.setStyle(Paint.Style.FILL);
        pressedPaint.setColor(Color.argb(200, 70, 120, 200));
        pressedPaint.setStyle(Paint.Style.FILL);
        borderPaint.setColor(Color.argb(180, 220, 220, 230));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3f);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(buttonH * 0.45f);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public float getLeft() {
        return leftRect.left;
    }

    public float getRight() {
        return rightRect.right;
    }

    public float getTop() {
        return leftRect.top;
    }

    public float getBottom() {
        return leftRect.bottom;
    }

    /** -1 left, 0 none, +1 right */
    public int getDirection() {
        if (leftPressed && !rightPressed) {
            return -1;
        }
        if (rightPressed && !leftPressed) {
            return 1;
        }
        return 0;
    }

    public boolean contains(float x, float y) {
        return leftRect.contains(x, y) || rightRect.contains(x, y);
    }

    /** 패드 주변은 점프 제스처로 치지 않음 */
    public boolean blocksJump(float x, float y) {
        float pad = Math.max(leftRect.width(), leftRect.height()) * 0.35f;
        RectF expanded = new RectF(
                leftRect.left - pad,
                leftRect.top - pad,
                rightRect.right + pad,
                rightRect.bottom + pad
        );
        return expanded.contains(x, y);
    }

    public boolean onPointerDown(int pointerId, float x, float y) {
        if (leftRect.contains(x, y)) {
            leftPressed = true;
            leftPointerId = pointerId;
            return true;
        }
        if (rightRect.contains(x, y)) {
            rightPressed = true;
            rightPointerId = pointerId;
            return true;
        }
        return false;
    }

    public void onPointerMove(int pointerId, float x, float y) {
        if (pointerId == leftPointerId) {
            if (!leftRect.contains(x, y)) {
                leftPressed = false;
                leftPointerId = -1;
            }
        } else if (pointerId == rightPointerId) {
            if (!rightRect.contains(x, y)) {
                rightPressed = false;
                rightPointerId = -1;
            }
        } else if (leftRect.contains(x, y) && leftPointerId < 0) {
            leftPressed = true;
            leftPointerId = pointerId;
            if (pointerId == rightPointerId) {
                rightPressed = false;
                rightPointerId = -1;
            }
        } else if (rightRect.contains(x, y) && rightPointerId < 0) {
            rightPressed = true;
            rightPointerId = pointerId;
            if (pointerId == leftPointerId) {
                leftPressed = false;
                leftPointerId = -1;
            }
        }
    }

    public void onPointerUp(int pointerId) {
        if (pointerId == leftPointerId) {
            leftPressed = false;
            leftPointerId = -1;
        }
        if (pointerId == rightPointerId) {
            rightPressed = false;
            rightPointerId = -1;
        }
    }

    public void reset() {
        leftPressed = false;
        rightPressed = false;
        leftPointerId = -1;
        rightPointerId = -1;
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Canvas canvas) {
        float r = Math.min(leftRect.width(), leftRect.height()) * 0.18f;
        canvas.drawRoundRect(leftRect, r, r, leftPressed ? pressedPaint : bgPaint);
        canvas.drawRoundRect(rightRect, r, r, rightPressed ? pressedPaint : bgPaint);
        canvas.drawRoundRect(leftRect, r, r, borderPaint);
        canvas.drawRoundRect(rightRect, r, r, borderPaint);

        float ly = leftRect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2f;
        canvas.drawText("◀", leftRect.centerX(), ly, textPaint);
        canvas.drawText("▶", rightRect.centerX(), ly, textPaint);
    }
}
