package com.swater.meimeng.mutils.sound;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class PlaySound {
	
	private SoundPool pool;
	private Map<Integer, Integer> soundID;
	private Context context;
	private AudioManager am;
	private static PlaySound player;
	private PlaySound(Context context){
		pool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);
		soundID = new HashMap<Integer, Integer>();
		this.context = context;
		//实例化AudioManager对象，控制声音
		am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	}
	public static PlaySound getIntence(Context context){
		if (player == null) {
			player = new PlaySound(context);
		}
		return player;
	}
	
	public void play(int resId){
		if (soundID.get(resId) != null) {
			int soundId = soundID.get(resId);
			//最大音量
		    float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		    //当前音量
		    float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		    float volumnRatio = audioCurrentVolumn/audioMaxVolumn;
		    //播放
			pool.play(soundId, volumnRatio, volumnRatio, 1, 0, 1);
		}
	}
	
	public void pause(int resId) {
		pool.pause(soundID.get(resId));
	}
	
	//设置要播放的声音
	public void addResource(int[] resIds){
		for (int i = 0; i < resIds.length; i++) {
			soundID.put(resIds[i], pool.load(context, resIds[i], 1));
		}
	}
}