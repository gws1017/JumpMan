package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kr.ac.kpu.game.s2017182016.jumpman.framework.util.AssetBitmap;

/**
 * Jump King level.png (780x585) hitbox atlas parser.
 * 13x13 screens of 60x45 hitbox pixels; 1 hitbox pixel = 8 game pixels (480x360).
 * Screen index: top-left first, then down the column (screen = col * 13 + row + 1).
 *
 * Mask colors:
 * - Black ≈ solid collision
 * - Red ≈ slippery slope (AABB stairs + slide)
 * - Workshop palette (ice/snow/sand/teal) also solid when present
 */
public class LevelMaskParser {
    private static final String TAG = LevelMaskParser.class.getSimpleName();

    public static final int GRID = 13;
    public static final int HITBOX_W = 60;
    public static final int HITBOX_H = 45;
    public static final int PIXEL_SCALE = 8;
    public static final int LOGICAL_W = HITBOX_W * PIXEL_SCALE; // 480
    public static final int LOGICAL_H = HITBOX_H * PIXEL_SCALE; // 360

    public static final int START_SCREEN = 1;
    public static final int MIN_SCREEN = 1;
    /**
     * 본편(원작 Jump King)만 구현 — 44는 콜리전 없는 빈 화면, 45부터는 DLC.
     * 43이 진짜 마지막 화면(탑 꼭대기, 엔딩 아트)이라 여기서 끊는다.
     */
    public static final int MAX_SCREEN = 43;

    /** true = climb by increasing screen number (matches midground 1.png → 2.png …) */
    public static final boolean USE_SEQUENTIAL_CLIMB = true;

    private static final int EMPTY = 0;
    private static final int SOLID = 1;
    private static final int SLOPE = 2;
    private static final int ICE = 3;

    public static class MaskPlatform {
        public final Rect rect;
        public final boolean slope;
        /** -1 slide left, +1 slide right */
        public final int slopeDir;
        public final boolean ice;

        public MaskPlatform(Rect rect, boolean slope, int slopeDir, boolean ice) {
            this.rect = rect;
            this.slope = slope;
            this.slopeDir = slopeDir;
            this.ice = ice;
        }
    }

    private static Bitmap levelBitmap;

    public static void ensureLoaded(Context context) {
        if (levelBitmap != null) {
            return;
        }
        levelBitmap = AssetBitmap.load(context, "level.png");
        if (levelBitmap == null) {
            Log.e(TAG, "Failed to load assets/level.png");
            return;
        }
        Log.d(TAG, "Loaded level mask " + levelBitmap.getWidth() + "x" + levelBitmap.getHeight());
    }

    public static int screenToCol(int screen) {
        return (screen - 1) / GRID;
    }

    public static int screenToRow(int screen) {
        return (screen - 1) % GRID;
    }

    public static int toScreen(int col, int row) {
        return col * GRID + row + 1;
    }

    public static boolean screenAssetExists(int screen) {
        if (screen == 101 || screen == 155) {
            return false;
        }
        return screen >= MIN_SCREEN && screen <= MAX_SCREEN;
    }

    public static int screenAbove(int screen) {
        if (USE_SEQUENTIAL_CLIMB) {
            int next = screen + 1;
            while (next <= MAX_SCREEN) {
                if (screenAssetExists(next) && hasCollisionPixels(next)) {
                    return next;
                }
                next++;
            }
            return -1;
        }
        int row = screenToRow(screen);
        if (row <= 0) {
            return -1;
        }
        return screen - 1;
    }

    public static int screenBelow(int screen) {
        if (USE_SEQUENTIAL_CLIMB) {
            int prev = screen - 1;
            while (prev >= MIN_SCREEN) {
                if (screenAssetExists(prev) && hasCollisionPixels(prev)) {
                    return prev;
                }
                prev--;
            }
            return -1;
        }
        int row = screenToRow(screen);
        if (row >= GRID - 1) {
            return -1;
        }
        return screen + 1;
    }

    public static boolean hasCollisionPixels(int screenNumber) {
        if (levelBitmap == null) {
            return true;
        }
        if (screenNumber < 1 || screenNumber > GRID * GRID) {
            return false;
        }
        int col = screenToCol(screenNumber);
        int row = screenToRow(screenNumber);
        int originX = col * HITBOX_W;
        int originY = row * HITBOX_H;
        for (int y = 0; y < HITBOX_H; y++) {
            for (int x = 0; x < HITBOX_W; x++) {
                if (classifyPixel(levelBitmap.getPixel(originX + x, originY + y)) != EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<MaskPlatform> parseScreen(int screenNumber) {
        ArrayList<MaskPlatform> result = new ArrayList<>();
        if (levelBitmap == null) {
            Log.e(TAG, "levelBitmap not loaded");
            return result;
        }
        if (screenNumber < 1 || screenNumber > GRID * GRID) {
            Log.e(TAG, "Invalid screen: " + screenNumber);
            return result;
        }

        int col = screenToCol(screenNumber);
        int row = screenToRow(screenNumber);
        int originX = col * HITBOX_W;
        int originY = row * HITBOX_H;

        int[][] kind = new int[HITBOX_W][HITBOX_H];
        for (int y = 0; y < HITBOX_H; y++) {
            for (int x = 0; x < HITBOX_W; x++) {
                kind[x][y] = classifyPixel(levelBitmap.getPixel(originX + x, originY + y));
            }
        }

        mergeRects(kind, SOLID, false, result);
        mergeRects(kind, SLOPE, true, result);
        mergeRects(kind, ICE, false, result);

        Log.d(TAG, "Screen " + screenNumber + " → " + result.size() + " platforms");
        return result;
    }

    private static void mergeRects(int[][] kind, int target, boolean slope, List<MaskPlatform> out) {
        boolean[][] visited = new boolean[HITBOX_W][HITBOX_H];
        for (int y = 0; y < HITBOX_H; y++) {
            for (int x = 0; x < HITBOX_W; x++) {
                if (kind[x][y] != target || visited[x][y]) {
                    continue;
                }
                int x2 = x;
                while (x2 + 1 < HITBOX_W && kind[x2 + 1][y] == target && !visited[x2 + 1][y]) {
                    x2++;
                }
                int y2 = y;
                boolean canGrow = true;
                while (canGrow && y2 + 1 < HITBOX_H) {
                    for (int tx = x; tx <= x2; tx++) {
                        if (kind[tx][y2 + 1] != target || visited[tx][y2 + 1]) {
                            canGrow = false;
                            break;
                        }
                    }
                    if (canGrow) {
                        y2++;
                    }
                }
                for (int ty = y; ty <= y2; ty++) {
                    for (int tx = x; tx <= x2; tx++) {
                        visited[tx][ty] = true;
                    }
                }
                Rect rect = new Rect(
                        x * PIXEL_SCALE,
                        y * PIXEL_SCALE,
                        (x2 + 1) * PIXEL_SCALE,
                        (y2 + 1) * PIXEL_SCALE
                );
                int dir = slope ? inferSlopeDir(kind, x, y, x2, y2) : 0;
                out.add(new MaskPlatform(rect, slope, dir, target == ICE));
            }
        }
    }

    /** Solid on left → slide right; solid on right → slide left. */
    private static int inferSlopeDir(int[][] kind, int x1, int y1, int x2, int y2) {
        int voteRight = 0;
        int voteLeft = 0;
        for (int y = y1; y <= y2; y++) {
            for (int x = x1; x <= x2; x++) {
                if (x > 0 && kind[x - 1][y] == SOLID) {
                    voteRight++;
                }
                if (x + 1 < HITBOX_W && kind[x + 1][y] == SOLID) {
                    voteLeft++;
                }
                if (y + 1 < HITBOX_H && kind[x][y + 1] == SOLID) {
                    if (x > 0 && kind[x - 1][y + 1] == SOLID) {
                        voteRight++;
                    }
                    if (x + 1 < HITBOX_W && kind[x + 1][y + 1] == SOLID) {
                        voteLeft++;
                    }
                }
            }
        }
        if (voteLeft > voteRight) {
            return -1;
        }
        if (voteRight > voteLeft) {
            return 1;
        }
        return 1;
    }

    private static int classifyPixel(int pixel) {
        int a = Color.alpha(pixel);
        if (a < 128) {
            return EMPTY;
        }
        int r = Color.red(pixel);
        int g = Color.green(pixel);
        int b = Color.blue(pixel);

        // Red = slippery slope
        if (r >= 200 && g <= 80 && b <= 80) {
            return SLOPE;
        }
        // Black = solid collision
        if (r <= 40 && g <= 40 && b <= 40) {
            return SOLID;
        }
        // Cyan = ice (slippery, no directional bias)
        if (r == 0 && g == 255 && b == 255) return ICE;
        // Workshop palette solids (snow / sand / teal)
        if (r == 0 && g == 170 && b == 170) return SOLID;
        if (r == 255 && g == 255 && b == 0) return SOLID;
        if (r == 255 && g == 255 && b == 255) return SOLID;
        if (r == 255 && g == 106 && b == 0) return SOLID;

        return EMPTY;
    }
}
