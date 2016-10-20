package com.cneop.util;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class VoiceUtil {

	private Context context;
	private SoundPool soundPool;
	private int streamVolume;
	private HashMap<Integer, Integer> soundPoolMap;

	public VoiceUtil(Context context) {
		this.context = context;
		initSounds();
	}

	private void initSounds() {
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 50);
		soundPoolMap = new HashMap<Integer, Integer>();
		AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	public void loadSfx(int raw, int ID) {
		soundPoolMap.put(ID, soundPool.load(context, raw, ID));
	}

	public void play(int sound, int uLoop) {
		try {
			soundPool.play(soundPoolMap.get(sound), streamVolume, streamVolume, 1, uLoop, 1f);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
