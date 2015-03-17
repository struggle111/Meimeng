package com.swater.meimeng.mutils.sound.sundButton;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.swater.meimeng.mutils.remoteview.CachePoolManager;
import com.swater.meimeng.mutils.sound.sundButton.MyAudioTrack.MyAudioTrackBackCall;

public class SoundButton extends Button implements OnLongClickListener,
		Runnable {
	private static final String TAG = SoundButton.class.getName();
	public static int SOUNDFLAG = 0; // -1：声音加载中 -2：出现异常

	private String soundUrl;
	private String soundTmpUrl;// 本地缓存路径
	// private MediaPlayer mediaPlayer;
	private boolean loading;
	private MyAudioTrack m_track;
	private TempUrl tempUrl;
	private MyAudioTrackBackCall backCall;
	private Context myContext;

	public String getSoundUrl() {
		return soundUrl;
	}

	public static int voiceTime = 0; // 语音的时间长度，如果为0的话(没有传过来)，那么不执行语音暂停功能
	int playVoiceTime = 0; // 语音播放的时间
	private Handler handler = new Handler();;

	public SoundButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// mediaPlayer = new MediaPlayer();
		myContext = context;

		if (attrs != null) {
			soundUrl = attrs.getAttributeValue(null, "soundUrl");
		}
		if (soundUrl == null || soundUrl.equals("")) {
			return;
		}
		loadSoundData(soundUrl, false);

	}

	public SoundButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SoundButton(Context context) {
		this(context, null);
	}

	/**
	 * 异步加载
	 * 
	 * @param url
	 *            加载的声音URL
	 * 
	 */
	private void loadSoundData(final String url, boolean reload) {
		loading = true;
		if (!reload && soundTmpUrl != null) {
			loading = false;
			return;
		}
		new Thread() {
			@Override
			public void run() {
				/*
				 * try { Thread.sleep(200); } catch (InterruptedException e) { }
				 */
				postInvalidate();
				soundTmpUrl = CachePoolManager.loadCacheOrDownloadSound(url);
				loading = false;
				postInvalidate();
			}
		}.start();
	}

	/**
	 * 设置新的
	 * 
	 * @param url
	 */
	public void setSoundUrl(String soundUrl) {
		if (soundUrl == null || soundUrl.equals("")) {
			Log.d(TAG, "the remote url can not be null");
			return;
		}
		if (soundUrl.equals(this.soundUrl)) {
			Log.d(TAG, "the same url, dispatch the sound");
			loadSoundData(this.soundUrl, false);
			return;
		}
		this.soundUrl = soundUrl;
		// Log.d(TAG, "set remote sound url : " + soundUrl);
		loadSoundData(soundUrl, true);
		setTextSize(16);
	}

	public void setSoundTmpUrl(String soundTmpUrl, int position, int duration) {
		if (soundTmpUrl != null && !"".equals(soundTmpUrl)) {
			this.soundTmpUrl = soundTmpUrl;
			tempUrl.setTempUrl(soundTmpUrl, position, duration);
		}
		setTextSize(16);
	}

	public void setTempUrl(TempUrl tempUrl) {
		this.tempUrl = tempUrl;
	}

	/**
	 * 播放声音
	 */
	public void playSound() {
		SOUNDFLAG = 0;
		if (loading) {
			SOUNDFLAG = -1;
			Toast.makeText(myContext, "加载中", 2).show();
			return;
		}
		if (soundTmpUrl == null || soundTmpUrl.equals("")) {
			SOUNDFLAG = -2;
			Toast.makeText(myContext, "未知错误", 2).show();
			return;
		}
		/*
		 * // 为播放器设置数据文件 try { Log.i(TAG, this.soundTmpUrl); if (mediaPlayer ==
		 * null) { mediaPlayer = new MediaPlayer(); } Log.i("liujiang",
		 * this.soundTmpUrl); File file = new File(this.soundTmpUrl);
		 * FileInputStream fis = new FileInputStream(file);
		 * mediaPlayer.setDataSource(fis.getFD());
		 * //mediaPlayer.setDataSource(this.soundTmpUrl); // 准备并且启动播放器
		 * mediaPlayer.prepare(); } catch (IllegalArgumentException e) {
		 * e.printStackTrace(); } catch (IllegalStateException e) {
		 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
		 * 
		 * mediaPlayer.start(); mediaPlayer.setOnCompletionListener(new
		 * OnCompletionListener() {
		 * 
		 * @Override public void onCompletion(MediaPlayer mp) { //
		 * setTitle("录音播放完毕.");
		 * 
		 * } }); //
		 */
		// *
		m_track = new MyAudioTrack(myContext);
		m_track.init(soundTmpUrl);
		m_track.setBackCall(backCall);

		// 舒永超
		// m_track.setBackCall(call);
		// SoundButton.this.setOnClickListener(click);

		playVoiceTime = 0;
		m_track.start();

		handler.postDelayed(this, 0);

		// */
	}

	@Override
	/*
	 * 线程运行 判断是否要播放结束声音
	 * (如果语音没有播放完，用户就暂停了语音，那么就不播放暂停声音，如果语音播放完成，播放语音播放完成声音com.golf91
	 * .R.raw.playend)
	 */
	public void run() {
		playVoiceTime++;
		boolean isOver = false; // 语音是否播放完

		// 语音播放的时间大于了语音本身的时间，那么说明语音播放完成
		if (playVoiceTime >= voiceTime) {
			isOver = true;
			handler.removeCallbacks(this); // 结束线程
			MyAudioTrack.isPlaySuccess = true;
		} else {
			MyAudioTrack.isPlaySuccess = false;
		}
		System.out.println(playVoiceTime + "   " + voiceTime + "  " + isOver);
		// 如果没有播放完，就继续
		if (isOver == false) {
			handler.postDelayed(this, 1000);
		}

	}

	public void stopSound() {

		if (m_track != null) {
			handler.removeCallbacks(this);
		}
		// if (m_track != null) {
		// >>>>>>> .r8078
		// m_track.stopSound();
		// }
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
	}

	public interface TempUrl {
		public void setTempUrl(String tempUrl, int position, int duration);
	}

	public void setBackCall(MyAudioTrackBackCall backCall) {
		this.backCall = backCall;
	}

	@Override
	public boolean onLongClick(View v) {
		// Toast.makeText(getContext(), "long-voice---click-->", 2).show();
		return false;
	}

}
