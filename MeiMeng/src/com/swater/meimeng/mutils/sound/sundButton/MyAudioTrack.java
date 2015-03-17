package com.swater.meimeng.mutils.sound.sundButton;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.swater.meimeng.mutils.sound.PlaySound;

public class MyAudioTrack extends Thread {
	protected AudioTrack m_out_trk;
	protected int m_out_buf_size;
	protected byte[] m_out_bytes;
	protected boolean m_keep_running;
	static public boolean isPlaySuccess = true;
	// private Socket s;
	private FileInputStream din;
	DataInputStream dis;

	private MyAudioTrackBackCall backCall;

	private Context context;

	public MyAudioTrack(Context context) {
		this.context = context;
	}

	int musicLength;
	short[] music;

	public void init(String path) {
		try {
			// s = new Socket("192.168.1.100", 4331);
			din = new FileInputStream(path);
			dis = new DataInputStream(din);
			m_keep_running = true;

			int sampleRateInHz = 4001;

			byte[] prefix = new byte[4];

			int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;

			try {
				din.read(prefix);
				if ("RIFF".equals(new String(prefix))) {
					sampleRateInHz = 8000;
					channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
				}
			} catch (Exception e) {
			}

			m_out_buf_size = AudioTrack.getMinBufferSize(sampleRateInHz,
					channelConfig, AudioFormat.ENCODING_PCM_16BIT);

			m_out_trk = new AudioTrack(AudioManager.STREAM_MUSIC,
					sampleRateInHz, channelConfig,
					AudioFormat.ENCODING_PCM_16BIT, m_out_buf_size,
					AudioTrack.MODE_STREAM);

			m_out_bytes = new byte[m_out_buf_size];
			File file = new File(path);
			musicLength = (int) (file.length() / 2);
			music = new short[musicLength];
			// new Thread(R1).start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void free() {
		m_keep_running = false;
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			Log.d("sleep exceptions.../n", "");
		}
	}

	public void run() {
		/*
		 * int i = 0; try { while (dis.available() > 0) { music[musicLength-1-i]
		 * = dis.readShort(); i++; }
		 * 
		 * dis.close(); } catch (IOException e2) { e2.printStackTrace(); }
		 * 
		 * m_out_trk.play(); // Write the music buffer to the AudioTrack object
		 * m_out_trk.write(music, 0, musicLength);
		 */
		// *
		byte[] bytes_pkg = null;
		m_out_trk.play();
		try {
			while (din.available() > 0) {
				try {
					din.read(m_out_bytes);
					bytes_pkg = m_out_bytes.clone();
					m_out_trk.write(bytes_pkg, 0, bytes_pkg.length);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// */

		m_out_trk.stop();
		if (backCall != null) {
			backCall.audioTrackBackCall();
		}

		if (isPlaySuccess) {
			//PlaySound.getIntence(context).play(R.raw.playend);
		}

		m_out_trk = null;
		try {
			din.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopSound() {
		if (m_out_trk != null) {
			m_out_trk.stop();
		}
	}

	public void setBackCall(MyAudioTrackBackCall backCall) {
		this.backCall = backCall;
	}

	public interface MyAudioTrackBackCall {
		public void audioTrackBackCall();
	}
}
