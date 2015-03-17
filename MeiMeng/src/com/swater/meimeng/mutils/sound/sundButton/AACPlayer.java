package com.swater.meimeng.mutils.sound.sundButton;

import java.io.IOException;

import com.swater.meimeng.mutils.constant.GeneralUtil;
import com.swater.meimeng.mutils.remoteview.CachePoolManager;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

public class AACPlayer {
	MediaPlayer player = null;
	public static AACPlayer inst = new AACPlayer();
	String url_sound = "";
	ImageView left_wave, right_wave;

	public void setWaveBg(ImageView leftwave, ImageView rightwave) {
		this.left_wave = leftwave;
		this.right_wave = rightwave;

	}

	public String getUrl_sound() {
		return url_sound;
	}

	public void setUrl_sound(String url_sound) {
		this.url_sound = url_sound;
	}

	public static AACPlayer getInstance() {

		return inst;
	}

	public AACPlayer() {
		player = new MediaPlayer();
	}

	public void clearData() {
		// player.stop();
		// player.release();
		// player.reset();
		// player.stop();
		// player.release()
		iniData(getUrl_sound());

	}

	public void iniData(String url) {
		try {
//			this.setUrl_sound(url);
			if (TextUtils.isEmpty(url) || url == null || url.equals("null")) {
				return;
			}
			if (null == player) {
				player = new MediaPlayer();
			}
			if (player.isPlaying()) {
				player.reset();

			}
//			String cache_url = CachePoolManager.loadCacheOrDownloadSound(url);
//			if (!TextUtils.isEmpty(cache_url) && cache_url.length() > 5) {
//
//				player.setDataSource(cache_url);
//			} else {
				player.setDataSource(url);
//			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		// player.start()

	}

	public void SateContinue() {
		if (null != player) {
			player.start();
			handler.obtainMessage(1).sendToTarget();
		}
	}

	public void statePlay() throws Exception {
		if (player != null) {

			player.prepare();

			player.start();
			player.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					handler.obtainMessage(1).sendToTarget();

				}
			});
		}
	}

	public void play(String s) throws IOException {

		this.setUrl_sound(s);
		if (null == player) {
			player = new MediaPlayer();
			player.setDataSource(s);
			player.prepare();
			player.start();
			player.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					handler.obtainMessage(1).sendToTarget();

				}
			});
		}
		if (player.isPlaying()) {
			player.reset();

		}

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1: {
				GeneralUtil.view_Show(left_wave);
				GeneralUtil.view_Hide(right_wave);

				AnimationDrawable drawable = (AnimationDrawable) left_wave
						.getDrawable();
				if (getPlayingStateCurrent()&&!drawable.isRunning()) {

					drawable.start();
				}
//				else{
//					drawable.stop();
//				}
			}
				break;
			case 2: {

				AnimationDrawable drawable = (AnimationDrawable) left_wave
						.getDrawable();
				if (drawable.isRunning()) {
					
					drawable.stop();
				}

				GeneralUtil.view_Hide(left_wave);
				GeneralUtil.view_Show(right_wave);

			}
				break;

			default:
				break;
			}
		};
	};

	ImageView left = null;
	ImageView right = null;

	public void play(String s, ImageView left, ImageView right)
			throws IOException {
		this.left = left;
		this.right = right;
		if (null == player) {
			player = new MediaPlayer();
			;
		}
		if (player.isPlaying()) {
			player.reset();

		}
		player.setDataSource(s);
		player.prepare();
		player.start();

	}

	void overDisplay() {
		left.setVisibility(View.GONE);
		right.setVisibility(View.VISIBLE);

	}

	public boolean getPlayingStateCurrent() {

		try {
			if (player == null) {
				return false;
			} else {
				return player.isPlaying();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public void StatePauseOrGoing() {
		if (getPlayingStateCurrent()) {
			if (player != null) {

				player.pause();
				handler.obtainMessage(2).sendToTarget();
			}
		} else {
			/**
			 * if (player!=null) {
			 * 
			 * player.start(); }else{ player=new MediaPlayer(); }
			 */
		}

	}

	public void stop() {

		if (null != player) {
			player.pause();
			overDisplay();

		}
	}

	public void stateRelease() {
		if (null != player) {
			player.stop();
			player.release();
			player = null;
			url_sound=null;
//			inst=null;
		}
	}

	class Eventfinish implements OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer mp) {

		}

	}

	class EventPause implements OnErrorListener {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			return false;
		}

	}

}
