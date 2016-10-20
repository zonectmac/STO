package com.cneop.util;

import android.content.Context;

import com.cneop.stoExpress.cneops.R;
import com.cneop.stoExpress.common.Enums.EVoiceType;

public class SoundUtil {

	private VoiceUtil voiceUtil;

	private SoundUtil(Context context) {
		voiceUtil = new VoiceUtil(context);
		// initSound();
	}

	private static SoundUtil soundUtil;

	public static SoundUtil getIntance(Context context) {
		if (soundUtil == null) {
			soundUtil = new SoundUtil(context);
		}
		return soundUtil;
	}

	public static SoundUtil getIntance() {
		return soundUtil;
	}

	/**
	 * 初始化声音
	 */
	public void initSound() {
		voiceUtil.loadSfx(R.raw.ok, EVoiceType.ok.value());
		voiceUtil.loadSfx(R.raw.fail, EVoiceType.fail.value());
		voiceUtil.loadSfx(R.raw.neworder, EVoiceType.neworder.value());
		voiceUtil
				.loadSfx(R.raw.unfinishorder, EVoiceType.unfinishorder.value());
	}

	/**
	 * 播放指定声音
	 * 
	 * @param voiceType
	 */
	public void PlayVoice(EVoiceType voiceType) {
		if (voiceType == EVoiceType.other) {
			return;
		}
		if (voiceUtil != null) {
			voiceUtil.play(voiceType.value(), 0);
		}
	}

	/**
	 * 扫描成功声音
	 */
	public void playOkVoice() {
		PlayVoice(EVoiceType.ok);
	}

	/**
	 * 错误声音
	 */
	public void playFailVoice() {
		PlayVoice(EVoiceType.fail);
	}

	/**
	 * 正确操作声音
	 */
	public void playNormalVoice() {
		PlayVoice(EVoiceType.normal);
	}
}
