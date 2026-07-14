package kr.ac.kpu.game.s2017182016.jumpman.framework.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;

/**
 * 십자 패드 (좌/우/상). 상단 유지 + 점프 = 제자리(수직) 점프.
 */
public class LeftRightPad implements GameObject {
    private final RectF leftRect = new RectF();
    private final RectF rightRect = new RectF();
    private final RectF upRect = new RectF();
    private final RectF bounds = new RectF();

    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pressedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean leftPressed;
    private boolean rightPressed;
    private boolean upPressed;
    private int leftPointerId = -1;
    private int rightPointerId = -1;
    private int upPointerId = -1;
    private boolean enabled = true;

    /**
     * @param originLeft 전체 십자 영역 왼쪽
     * @param originTop  전체 십자 영역 위
     * @param buttonW    한 칸 가로
     * @param buttonH    한 칸 세로
     * @param gap        칸 사이 간격
     */
    public LeftRightPad(float originLeft, float originTop, float buttonW, float buttonH, float gap) {
        float midX = originLeft + buttonW + gap;
        leftRect.set(originLeft, originTop + buttonH + gap, originLeft + buttonW, originTop + buttonH + gap + buttonH);
        rightRect.set(midX + buttonW + gap, originTop + buttonH + gap,
                midX + buttonW + gap + buttonW, originTop + buttonH + gap + buttonH);
        upRect.set(midX, originTop, midX + buttonW, originTop + buttonH);
        bounds.set(originLeft, originTop,
                midX + buttonW + gap + buttonW,
                originTop + buttonH + gap + buttonH);

        bgPaint.setColor(Color.argb(140, 60, 60, 70));
        bgPaint.setStyle(Paint.Style.FILL);
        pressedPaint.setColor(Color.argb(200, 70, 120, 200));
        pressedPaint.setStyle(Paint.Style.FILL);
        borderPaint.setColor(Color.argb(180, 220, 220, 230));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2.5f);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(buttonH * 0.42f);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public float getLeft() {
        return bounds.left;
    }

    public float getRight() {
        return bounds.right;
    }

    public float getTop() {
        return bounds.top;
    }

    public float getBottom() {
        return bounds.bottom;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            reset();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    /** -1 left, 0 none, +1 right (상단만 누르면 0) */
    public int getDirection() {
        if (!enabled) {
            return 0;
        }
        if (leftPressed && !rightPressed) {
            return -1;
        }
        if (rightPressed && !leftPressed) {
            return 1;
        }
        return 0;
    }

    public boolean isHoldingUp() {
        return enabled && upPressed;
    }

    public boolean contains(float x, float y) {
        return enabled && bounds.contains(x, y);
    }

    public boolean blocksJump(float x, float y) {
        if (!enabled) {
            return false;
        }
        float pad = Math.max(leftRect.width(), leftRect.height()) * 0.25f;
        RectF expanded = new RectF(
                bounds.left - pad,
                bounds.top - pad,
                bounds.right + pad,
                bounds.bottom + pad
        );
        return expanded.contains(x, y);
    }

    public boolean onPointerDown(int pointerId, float x, float y) {
        if (!enabled) {
            return false;
        }
        if (upRect.contains(x, y)) {
            upPressed = true;
            upPointerId = pointerId;
            return true;
        }
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
        if (!enabled) {
            return;
        }
        // Release if finger left its button
        if (pointerId == upPointerId && !upRect.contains(x, y)) {
            upPressed = false;
            upPointerId = -1;
        }
        if (pointerId == leftPointerId && !leftRect.contains(x, y)) {
            leftPressed = false;
            leftPointerId = -1;
        }
        if (pointerId == rightPointerId && !rightRect.contains(x, y)) {
            rightPressed = false;
            rightPointerId = -1;
        }
        // Claim new button if free
        if (upRect.contains(x, y) && upPointerId < 0) {
            upPressed = true;
            upPointerId = pointerId;
            clearOtherDirs(pointerId, true, false, false);
        } else if (leftRect.contains(x, y) && leftPointerId < 0) {
            leftPressed = true;
            leftPointerId = pointerId;
            clearOtherDirs(pointerId, false, true, false);
        } else if (rightRect.contains(x, y) && rightPointerId < 0) {
            rightPressed = true;
            rightPointerId = pointerId;
            clearOtherDirs(pointerId, false, false, true);
        }
    }

    private void clearOtherDirs(int pointerId, boolean keepUp, boolean keepLeft, boolean keepRight) {
        if (!keepUp && upPointerId == pointerId) {
            upPressed = false;
            upPointerId = -1;
        }
        if (!keepLeft && leftPointerId == pointerId) {
            leftPressed = false;
            leftPointerId = -1;
        }
        if (!keepRight && rightPointerId == pointerId) {
            rightPressed = false;
            rightPointerId = -1;
        }
    }

    public void onPointerUp(int pointerId) {
        if (pointerId == upPointerId) {
            upPressed = false;
            upPointerId = -1;
        }
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
        upPressed = false;
        leftPointerId = -1;
        rightPointerId = -1;
        upPointerId = -1;
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Canvas canvas) {
        if (!enabled) {
            return;
        }
        drawBtn(canvas, upRect, upPressed, "▲");
        drawBtn(canvas, leftRect, leftPressed, "◀");
        drawBtn(canvas, rightRect, rightPressed, "▶");
    }

    private void drawBtn(Canvas canvas, RectF rect, boolean pressed, String label) {
        float r = Math.min(rect.width(), rect.height()) * 0.18f;
        canvas.drawRoundRect(rect, r, r, pressed ? pressedPaint : bgPaint);
        canvas.drawRoundRect(rect, r, r, borderPaint);
        float ly = rect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2f;
        canvas.drawText(label, rect.centerX(), ly, textPaint);
    }
}
