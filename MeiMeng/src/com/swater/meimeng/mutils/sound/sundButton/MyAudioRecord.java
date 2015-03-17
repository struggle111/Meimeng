package com.swater.meimeng.mutils.sound.sundButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;


/**
 * 音频采集
 * 
 * @author <a href="mailto:mrjeye@hotmail.com">Mr.J</a> <br />
 *
 * Jun 12, 2012
 */
public class MyAudioRecord {
	public static final String AUDIO_RECORD = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/meimeng/AudioRecord/recored.aif";
//	public static final String AUDIO_RECORD = Environment
//			.getExternalStorageDirectory().getAbsolutePath()
//			+ "/meimeng/AudioRecord/recored.aac";
	private static final String AUDIO_RECORDER_TEMP_FILE = Environment.getExternalStorageDirectory().getAbsolutePath()+"/meimeng/AudioRecord/recored_.aif";
//	private static final String AUDIO_RECORDER_TEMP_FILE = Environment.getExternalStorageDirectory().getAbsolutePath()+"/meimeng/AudioRecord/recored_.aac";

	private static final int RECORDER_BPP = 16;
	int channels = 1;
	int frequency = 8000;
	private static int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private static int EncodingBitRate = AudioFormat.ENCODING_PCM_16BIT;

	boolean isRecording = false;

	protected AudioRecord audioRecord;
	Thread recordingThread;
	protected int recBufSize;
	protected byte[] m_in_bytes;
	protected boolean m_keep_running;
	protected Socket s;
	protected FileOutputStream dout;
	protected LinkedList<byte[]> m_in_q;

	/**
	 * 创建AudioRecord
	 * 
	 * @throws CanNotRecordException　任何创建失败的情况
	 */
	public void createAudioRecord() throws CanNotRecordException {
		recBufSize = AudioRecord.getMinBufferSize(frequency,
				channelConfiguration, EncodingBitRate);

		if (recBufSize <= 0) {
			throw new CanNotRecordException("您的设置暂时不能很好的支持音频采集");
		}
		
		try {
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, EncodingBitRate, recBufSize);
		} catch (Exception e) {
			throw new CanNotRecordException("您的设置暂时不能很好的支持音频采集:" + e.getMessage(), e);
		}
				
	}

	/**
	 * 开始采集音频数据
	 * 
	 * @throws CanNotRecordException	任何可能导致音频采集发生错误的情况
	 */
	public void startRecording() throws CanNotRecordException {
		createAudioRecord();
		
		try {
			
			audioRecord.startRecording();
		} catch (Exception e) {
			throw new CanNotRecordException(e.getMessage(), e);
		}
		
		isRecording = true;
		
		recordingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				writeAudioDataToFile();
			}

		});

		recordingThread.start();
	}

	private void writeAudioDataToFile() {
		byte data[] = new byte[recBufSize];
		String filename = getTempFilename();
		FileOutputStream os = null;

		try {
			os = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		int read = 0;

		if (null != os) {
			while (isRecording) {
				read = audioRecord.read(data, 0, recBufSize);

				if (AudioRecord.ERROR_INVALID_OPERATION != read) {
					try {
						os.write(data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopRecording() throws Exception{
		if (null != audioRecord) {
			isRecording = false;

			audioRecord.stop();
			audioRecord.release();

			audioRecord = null;
			recordingThread = null;
		}

		copyWaveFile(getTempFilename(), getFilename());
		deleteTempFile();
	}

	private void deleteTempFile() {
		File file = new File(getTempFilename());

		file.delete();
	}

	private String getFilename() {
		return AUDIO_RECORD;
	}

	private String getTempFilename() {
		return AUDIO_RECORDER_TEMP_FILE;
	}

	private void copyWaveFile(String inFilename, String outFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = frequency;
		// int channels = 2;
		long byteRate = RECORDER_BPP * frequency * channels / 8;

		byte[] data = new byte[recBufSize];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;

			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);

			while (in.read(data) != -1) {
				out.write(data);
			}

			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels, long byteRate)
			throws IOException {

		byte[] header = new byte[44];

		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = RECORDER_BPP; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}

	// ////
	public static byte[] revers(byte[] tmp) {
		byte[] reversed = new byte[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			reversed[i] = tmp[tmp.length - i - 1];

		}
		return reversed;
	}

	public static byte[] intToBytes(int num) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (num >> 24);
		bytes[1] = (byte) ((num >> 16) & 0x000000FF);
		bytes[2] = (byte) ((num >> 8) & 0x000000FF);
		bytes[3] = (byte) (num & 0x000000FF);
		return bytes;
	}

}
