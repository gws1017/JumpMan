package kr.ac.kpu.game.s2017182016.jumpman.framework.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

import kr.ac.kpu.game.s2017182016.jumpman.R;

public class Sound {
    private static SoundPool soundPool;
    private static final int[] SOUND_IDS = {
            R.raw.king_bump, R.raw.king_jump, R.raw.king_land,
            R.raw.menu_intro
    };
    private static HashMap<Integer, Integer> soundIdMap = new HashMap<>();

    public static void init(Context context) {
        AudioAttributes audioAttributes;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            Sound.soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(3)
                    .build();
        } else {
            Sound.soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        }

        for (int resId : SOUND_IDS) {
            int soundId = soundPool.load(context, resId, 1);
            soundIdMap.put(resId, soundId);
        }
    }

    public static int play(int resId) {
        Integer soundId = soundIdMap.get(resId);
        if (soundId == null || soundPool == null) {
            return 0;
        }
        float vol = 1f;
        if (GameSettings.get() != null) {
            vol = GameSettings.get().getSfxVolume();
        }
        return soundPool.play(soundId, vol, vol, 1, 0, 1f);
    }

    public static int play(int resId, float vol, int loop) {
        Integer soundId = soundIdMap.get(resId);
        if (soundId == null || soundPool == null) {
            return 0;
        }
        float volume = vol / 100.f;
        if (GameSettings.get() != null) {
            volume *= GameSettings.get().getSfxVolume();
        }
        return soundPool.play(soundId, volume, volume, 1, loop, 1f);
    }
}
