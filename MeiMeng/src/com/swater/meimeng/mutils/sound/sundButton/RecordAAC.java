package com.swater.meimeng.mutils.sound.sundButton;

import java.io.File;
import java.io.IOException;

import com.swater.meimeng.mutils.NSlog.NSLoger;

import android.media.MediaRecorder;
import android.os.Environment;

public class RecordAAC {
	// String path = "/sdcard/aac/recorder.aac";
	public static final String path = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/aac/AudioRecord/recored.aac";
	MediaRecorder recorder = null;
	Thread record_thread = null;

	public void ini() {
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		File fl = new File(path);
		if (!fl.isFile() || !fl.exists()) {
			File pf = fl.getParentFile();
			pf.mkdirs();
			if (fl.exists()) {
				NSLoger.Log("文件已存在");
				
			}
		}
	}

	public void beginRecord() {

		record_thread = new Thread(new Runnable() {

			@Override
			public void run() {
				test();

			}
		});
		record_thread.start();

	}

	public void stopRecord() {
		
		if (null != recorder) {
			try {

				recorder.stop();
				recorder=null;
//				recorder.reset();
//				recorder.release();

			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("录音异常！"+e.getLocalizedMessage());
			}finally{
				
				if (null != record_thread) {
					record_thread.stop();
				}
				
			}

		}
		

	}

	void test() {

		try {
			recorder.setOutputFile(path);
			recorder.prepare();
			recorder.start();
			try {
				// Runnable.wait(10000);
			} catch (Exception exp) {
				exp.printStackTrace();
			}

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
