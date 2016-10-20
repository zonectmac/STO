package com.cneop.util;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * ≤•∑≈…˘“Ù
 * @author wm
 *
 */
public class MediaPlayerUtil {

	private MediaPlayer mmediaplayer = null;
	private Context context;
	private static MediaPlayerUtil  instance=null;
	private static MediaPlayerUtil getInstance(){
		if(instance==null){
			instance=new MediaPlayerUtil();
		}
		return instance;
	}
	private MediaPlayerUtil(){
		
	}

	public void init(Context context) {
		mmediaplayer = new MediaPlayer();
		this.context = context;
	}

	public void play(int iResId, boolean isLoop) {
		if(mmediaplayer.isPlaying()){
			mmediaplayer.stop();
		}
		mmediaplayer = MediaPlayer.create(this.context, iResId);
		mmediaplayer.setLooping(isLoop);
		mmediaplayer.start();
	}

	public void stop() {
		if (mmediaplayer == null) {
			return;
		}
		mmediaplayer.stop();
		mmediaplayer.release();
	}

}
