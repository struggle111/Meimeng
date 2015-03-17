package com.swater.meimeng.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.meimeng.app.R;
import com.swater.meimeng.activity.newtabMain.IndexActivity;


public class CheckVersionService extends Service {

	private String apkUrl = null;

	// 文件存储
	private File updateDir = null;
	private File updateFile = null;

	private String downloadDir = "meimeng/update";
	Context context;
	private NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;

	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;

	public IBinder onBind(Intent intent) {
		return null;
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			
			return super.onStartCommand(intent, flags, startId);
		}
		
		context = this.getBaseContext();
		// 获取传值
		apkUrl = intent.getStringExtra("apkUrl");

		// 创建文件
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
			updateDir = new File(Environment.getExternalStorageDirectory(), downloadDir);
			updateFile = new File(updateDir.getPath(), "meimengupdate.apk");
		}

		this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.updateNotification = new Notification();

		// 设置下载过程中，点击通知栏，回到主界面
		updateIntent = new Intent(this, IndexActivity.class);
		updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
		// 设置通知栏显示内容
		updateNotification.icon = R.drawable.meimeng;
		updateNotification.tickerText = "开始下载";
		updateNotification.flags = Notification.FLAG_ONGOING_EVENT;
		RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.download_notification_layout);
		contentView.setTextViewText(R.id.fileName, "美盟");
		// 指定视图
		updateNotification.contentView = contentView;
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 指定内容意图
		updateNotification.contentIntent = contentIntent;

		// 发出通知
		updateNotificationManager.notify(0, updateNotification);

		// 开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
		new Thread(new updateRunnable()).start();// 这个是下载的重点，是下载的过程

		return super.onStartCommand(intent, flags, startId);
	}

	class updateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();

		public void run() {
			try {
				// 增加权限<uses-permission
				// android:name="android.permission.WRITE_EXTERNAL_STORAGE">;
				if (!updateDir.exists()) {
					updateDir.mkdirs();
				}
				if (!updateFile.exists()) {
					updateFile.createNewFile();
				}
				// 下载函数
				long downloadSize = downloadUpdateFile(apkUrl, updateFile);
				if (downloadSize > 0) {
					// 下载成功
					message.what = DOWNLOAD_COMPLETE;
					updateHandler.sendMessage(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				// 下载失败
				updateHandler.sendMessage(message);
			}
		}
	}

	private Handler updateHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_COMPLETE:
				// 点击安装PendingIntent
				Uri uri = Uri.fromFile(updateFile);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
				updatePendingIntent = PendingIntent.getActivity(CheckVersionService.this, 0, installIntent, 0);
				updateNotification.flags = Notification.FLAG_AUTO_CANCEL;
				;
				updateNotification.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
				updateNotification.setLatestEventInfo(CheckVersionService.this, "美盟", "下载完成,点击安装。", updatePendingIntent);
				updateNotificationManager.notify(0, updateNotification);

				// 停止服务
				stopService(updateIntent);
				break;
			case DOWNLOAD_FAIL:

				Intent failIntent = new Intent(context, CheckVersionService.class);
				failIntent.putExtra("apkUrl", apkUrl);
				PendingIntent fail = PendingIntent.getActivity(context, 0, failIntent, 0);

				updateNotification.flags = Notification.FLAG_AUTO_CANCEL;
				updateNotification.contentView = null;
				updateNotification.setLatestEventInfo(CheckVersionService.this, "美盟", "下载失败，请重新下载。", fail);

				updateNotificationManager.notify(0, updateNotification);
				// context.startService(failIntent);
				break;

			default:
				stopService(updateIntent);
			}
		}
	};

	public long downloadUpdateFile(String downloadUrl, File saveFile) throws Exception {

		Log.d("url-->", downloadUrl);
		int downloadCount = 0;
		int currentSize = 0;
		long totalSize = 0;
		int updateTotalSize = 0;

		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;
		String org = downloadUrl;

		try {
			Log.d("enter----url---org", downloadUrl);
			// TODO Validate the url whether is contains the ref
			if (downloadUrl.indexOf("\"") != -1) {
				downloadUrl = downloadUrl.substring(1, org.length() - 1);
			}
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte buffer[] = new byte[512 * 3];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;
				if ((downloadCount == 0) || (int) (totalSize * 100 / updateTotalSize) - 10 > downloadCount) {
					downloadCount += 10;
					int pro = (int) totalSize * 100 / updateTotalSize;
					RemoteViews contentView = updateNotification.contentView;
					contentView.setTextViewText(R.id.rate, "已下载" + pro + "%");
					contentView.setProgressBar(R.id.progress, 100, pro, false);
					updateNotificationManager.notify(0, updateNotification);
				}
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return totalSize;
	}

}
