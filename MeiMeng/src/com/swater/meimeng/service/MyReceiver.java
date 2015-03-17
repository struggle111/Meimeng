package com.swater.meimeng.service;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.ui.JPushRemoteViews;

import com.meimeng.app.R;
import com.swater.meimeng.activity.newtabMain.IndexActivity;
import com.swater.meimeng.database.ShareUtil;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.constant.GeneralUtil;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "MyReceiver";
	int type = -1;
	/**
	 * 处理推送设置 @chengshiyang
	 * 
	 * @param con
	 * @param from
	 * @param id
	 * @param tag
	 *            是否播放声音
	 */
	int id = 0;

	private void showNotifacation(Context con, String txt) {
		String msg = "";
		id++;

		NotificationManager manager = (NotificationManager) con
				.getSystemService(Context.NOTIFICATION_SERVICE);
		msg = " 发来了新消息。" + txt;
		final Notification notification = new Notification(
				R.drawable.ic_launcher, msg, System.currentTimeMillis());

		notification.flags = Notification.FLAG_AUTO_CANCEL;
		// notification.flags = Notification.FLAG_ONGOING_EVENT;

		RemoteViews contentView = new RemoteViews(con.getPackageName(),
				R.layout.msgnotifa);
		contentView.setTextViewText(R.id.text, msg);
		notification.contentView = contentView;

		Intent notificationIntent = new Intent(con, IndexActivity.class); // 到聊天界面

		notificationIntent.putExtra("leaderUserId", "1");

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(con, 0,
				notificationIntent, 0);
		contentIntent = PendingIntent
				.getActivity(con, 0, notificationIntent, 0);

		notification.contentIntent = contentIntent;
		manager.notify(id, notification);
		Intent in = new Intent(con, IndexActivity.class);
		in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		in.putExtra("type", 2);
		if (isTopActivity(con)) {

			con.startActivity(in);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: "
				+ printBundle(bundle));

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "接收Registration Id : " + regId);
			// send the Registration Id to your server...
		} else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "接收UnRegistration Id : " + regId);
			// send the UnRegistration Id to your server...
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			String txt = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			Log.d(TAG,
					"接收到推送下来的自定义消息: "
							+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
			// showNotifacation(context, txt);

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG, "接收到推送下来的通知");

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			Log.d(TAG, "用户点击打开了通知");
//			Object objects = bundle.get(JPushInterface.EXTRA_EXTRA);
//			String clsName = objects.getClass().getCanonicalName();
//			NSLoger.Log(clsName + "---cls-Name--");
//
//			try {
//				JSONObject ob = new JSONObject((String) objects);
//				type = ob.getInt("msgtype");
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			switch (type) {
//			case 1: {
//			}
//
//				break;
//			case 2: {
//			}
//
//				break;
//			case 3: {
//			}
//
//				break;
//
//			default:
//				break;
//			}
			Intent in = new Intent(context, IndexActivity.class);
			in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			in.putExtra("type", 2);

			context.startActivity(in);

		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			Log.d(TAG,
					"用户收到到RICH PUSH CALLBACK: "
							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..
			// Intent i = new Intent(context, MainActivity.class);
			// i.putExtra("type", type);
			// context.startActivity(i);

		} else {
			Log.d(TAG, "Unhandled intent - " + intent.getAction());
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	/**
	 * 判断应该是否处在激活状态
	 * 
	 * @param context
	 * @return
	 */
	public boolean isTopActivity(Context context) {
		String packageName = "com.swater.meimeng";
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// Log.d(this.getClass().getSimpleName(),
			// "---------------包名-----------"+tasksInfo.get(0).topActivity.getPackageName());
			// 应用程序位于堆栈的顶层
			if (packageName.equals(tasksInfo.get(0).topActivity
					.getPackageName())) {
				Log.d(this.getClass().getSimpleName(), "程序在使用");
				return true;
			}
		}
		// Log.d(this.getClass().getSimpleName(), "程序没有在使用");
		return false;
	}

}
