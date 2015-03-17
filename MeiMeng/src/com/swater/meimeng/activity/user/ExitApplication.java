package com.swater.meimeng.activity.user;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;


/**
 * 完全退出程序辅助类
 * 
 * @author sych
 * 
 */
public class ExitApplication {

	// 存储已打开的Activity集合
	private List<Activity> listActivity = new ArrayList<Activity>();
	private List<Activity> spelistActivity = new ArrayList<Activity>();

	// 单例的ExitApplication,目的是在任何的Activity中用的都是同一个集合
	private static ExitApplication exitApplication;

	private ExitApplication() {
	}

	public static ExitApplication getInstance() {
		if (null == exitApplication) {
			exitApplication = new ExitApplication();
		}
		return exitApplication;
	}

	/**
	 * 添加Activity到集合中(在每个Acitivity的onCreate方法中调用)
	 */
	public void addActivity(Activity activity) {
		listActivity.add(activity);
	}

	public void addSpecialActivity(Activity activity) {
		spelistActivity.add(activity);
	}

	/**
	 * 完全退出程序(在准备退出系统的时候调用)
	 */
	public void exitToShow(final Context context) {

		Dialog dialog = new AlertDialog.Builder(context)
				.setTitle("确认退出")
				.setMessage("确定退出该系统?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Intent intent = new Intent(Intent.ACTION_MAIN);
						intent.addCategory(Intent.CATEGORY_HOME);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						context.startActivity(intent);
						android.os.Process.killProcess(android.os.Process
								.myPid());

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}

				}).create();
		dialog.show();
	}

	public void exit(Context context) {
		removewActivity();
		System.exit(0);
	}
	public void exitSpecial(Context context) {
		removewSpecialActivity();
		System.exit(0);
	}

	public void removewActivity() {
		for (Activity activity : listActivity) {
			activity.finish();
		}
	}

	public void removewSpecialActivity() {
		for (Activity activity : spelistActivity) {
			activity.finish();
		}
	}

	void exitApplication(Context context) {
		// MainMenuActivity.closeAllBelowActivities(MainMenuActivity.this);
		// finish();
		try {

			PackageManager localPackageManager = context.getPackageManager();
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);

			List<ResolveInfo> resolveInfos = localPackageManager
					.queryIntentActivities(intent,
							PackageManager.MATCH_DEFAULT_ONLY);
			for (int i = 0; i < resolveInfos.size(); i++) {
				ResolveInfo resolveInfo = resolveInfos.get(i);
				ActivityInfo activityInfo = resolveInfo.activityInfo;
				if (!activityInfo.name.endsWith("DummyActivity")) {
					ComponentName componentName = new ComponentName(
							activityInfo.packageName, activityInfo.name);

					Intent intent1 = new Intent();
					intent1.setComponent(componentName);

					context.startActivity(intent1);
					System.exit(0);
					break;
				}

			}

		} catch (Exception e) {
			// mLoger.info("default home screen not found " + e.getMessage());
		}

	}
}