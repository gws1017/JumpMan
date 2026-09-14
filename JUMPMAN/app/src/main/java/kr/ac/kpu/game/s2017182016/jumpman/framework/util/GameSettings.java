package kr.ac.kpu.game.s2017182016.jumpman.framework.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

/**
 * Persisted game options (pad, debug, volumes).
 */
public class GameSettings {
    public enum PadMode {
        JOYSTICK,
        DPAD
    }

    private static final String PREFS = "jumpman_settings";
    private static GameSettings instance;

    private final SharedPreferences prefs;
    private PadMode padMode = PadMode.DPAD;
    private boolean cheatEnabled = false;
    private boolean showCollision = false;
    private boolean assistMode = false;
    private float bgmVolume = 1f;
    private float sfxVolume = 1f;

    private GameSettings(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        load();
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new GameSettings(context);
        }
    }

    public static GameSettings get() {
        return instance;
    }

    private void load() {
        String pad = prefs.getString("padMode", PadMode.DPAD.name());
        try {
            padMode = PadMode.valueOf(pad);
        } catch (IllegalArgumentException e) {
            padMode = PadMode.DPAD;
        }
        cheatEnabled = prefs.getBoolean("cheatEnabled", false);
        showCollision = prefs.getBoolean("showCollision", false);
        assistMode = prefs.getBoolean("assistMode", false);
        bgmVolume = clamp01(prefs.getFloat("bgmVolume", 1f));
        sfxVolume = clamp01(prefs.getFloat("sfxVolume", 1f));
    }

    public void save() {
        prefs.edit()
                .putString("padMode", padMode.name())
                .putBoolean("cheatEnabled", cheatEnabled)
                .putBoolean("showCollision", showCollision)
                .putBoolean("assistMode", assistMode)
                .putFloat("bgmVolume", bgmVolume)
                .putFloat("sfxVolume", sfxVolume)
                .apply();
    }

    private static float clamp01(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }

    public PadMode getPadMode() {
        return padMode;
    }

    public void setPadMode(PadMode padMode) {
        this.padMode = padMode;
        save();
    }

    public boolean isCheatEnabled() {
        return cheatEnabled;
    }

    public void setCheatEnabled(boolean cheatEnabled) {
        this.cheatEnabled = cheatEnabled;
        save();
    }

    public boolean isShowCollision() {
        return showCollision;
    }

    public void setShowCollision(boolean showCollision) {
        this.showCollision = showCollision;
        save();
    }

    public boolean isAssistModeEnabled() {
        return assistMode;
    }

    public void setAssistModeEnabled(boolean assistMode) {
        this.assistMode = assistMode;
        save();
    }

    public float getBgmVolume() {
        return bgmVolume;
    }

    public void setBgmVolume(float bgmVolume) {
        this.bgmVolume = clamp01(bgmVolume);
        save();
    }

    public void adjustBgmVolume(float delta) {
        setBgmVolume(bgmVolume + delta);
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public void setSfxVolume(float sfxVolume) {
        this.sfxVolume = clamp01(sfxVolume);
        save();
    }

    public void adjustSfxVolume(float delta) {
        setSfxVolume(sfxVolume + delta);
    }

    public void applyBgmVolume(MediaPlayer player) {
        if (player == null) {
            return;
        }
        try {
            player.setVolume(bgmVolume, bgmVolume);
        } catch (IllegalStateException ignored) {
        }
    }
}
