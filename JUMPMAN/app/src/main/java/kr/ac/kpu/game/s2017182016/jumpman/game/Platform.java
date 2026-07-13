package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Canvas;

import kr.ac.kpu.game.s2017182016.jumpman.framework.object.ImageObject;

public class Platform extends ImageObject {
    public enum Kind {
        NORMAL,
        SLOPE
    }

    public final Kind kind;
    /** -1 slide left, +1 slide right (SLOPE only) */
    public final int slopeDir;

    public Platform(float x, float y, int width, int height) {
        this(x, y, width, height, Kind.NORMAL, 0);
    }

    public Platform(float x, float y, int width, int height, Kind kind, int slopeDir) {
        init(x, y, width, height);
        this.kind = kind;
        this.slopeDir = slopeDir == 0 ? 1 : (slopeDir < 0 ? -1 : 1);
        float w = width;
        float h = height;
        dstRect.set(x, y, x + w, y + h);
    }

    public boolean isSlope() {
        return kind == Kind.SLOPE;
    }

    @Override
    public void draw(Canvas canvas) {
    }

    @Override
    public void update() {
    }
}
