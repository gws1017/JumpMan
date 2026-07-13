package kr.ac.kpu.game.s2017182016.jumpman.framework.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class AssetBitmap {
    private static final String TAG = AssetBitmap.class.getSimpleName();
    private static final HashMap<String, Bitmap> cache = new HashMap<>();

    public static Bitmap load(Context context, String assetPath) {
        Bitmap cached = cache.get(assetPath);
        if (cached != null) {
            return cached;
        }
        try (InputStream is = context.getAssets().open(assetPath)) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inScaled = false;
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);
            if (bitmap != null) {
                cache.put(assetPath, bitmap);
            }
            return bitmap;
        } catch (IOException e) {
            Log.w(TAG, "Asset not found: " + assetPath);
            return null;
        }
    }

    public static boolean exists(Context context, String assetPath) {
        try (InputStream ignored = context.getAssets().open(assetPath)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
