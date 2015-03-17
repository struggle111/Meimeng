package com.swater.meimeng.mutils.sound.sundButton;

import java.io.File;

public class WavVoiceTest {
	

	// 获取类的实例
	ExtAudioRecorder extAudioRecorder = ExtAudioRecorder.getInstanse(false); // 未压缩的录音（WAV）

	/**
	 * 录制wav格式文件
	 * 
	 * @param path
	 *            : 文件路径
	 */
	public static File recordChat(ExtAudioRecorder extAudioRecorder,
			String savePath, String fileName) {
		File dir = new File(savePath);
		// 如果该目录没有存在，则新建目录
		if (dir.list() == null) {
			dir.mkdirs();
		}
		// 获取录音文件
		File file = new File(savePath + fileName);
		// 设置输出文件
		extAudioRecorder.setOutputFile(savePath + fileName);
		extAudioRecorder.prepare();
		// 开始录音
		extAudioRecorder.start();
		return file;
	}

	/**
	 * 停止录音
	 * 
	 * @param mediaRecorder
	 *            待停止的录音机
	 * @return 返回
	 */
	public static void stopRecord(final ExtAudioRecorder extAudioRecorder) {
		extAudioRecorder.stop();
		extAudioRecorder.release();
	}
}
