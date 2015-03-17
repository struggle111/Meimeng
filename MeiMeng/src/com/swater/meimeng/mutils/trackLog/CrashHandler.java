package com.swater.meimeng.mutils.trackLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.swater.meimeng.database.ShareUtil;
import com.swater.meimeng.mutils.net.MUrlPostAddr;
import com.swater.meimeng.mutils.net.RequestByPost;
import com.swater.meimeng.mutils.net.RespVo;

/**
 * 末处理异常处理器
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler, MUrlPostAddr {
	static final String TAG = CrashHandler.class.getSimpleName();

	public boolean DEBUG = true;

	/** 系统默认异常处理器 */
	Thread.UncaughtExceptionHandler defaultHandler;

	Context mContext;

	private static final CrashHandler crashHandler = new CrashHandler();

	/** 使用Properties来保存设备的信息和错误堆栈信息 */
	private Properties mDeviceCrashInfo = new Properties();
	private static final String VERSION_NAME = "versionName";
	private static final String VERSION_CODE = "versionCode";
	private static final String STACK_TRACE = "STACK_TRACE";
	/** 错误报告文件的扩展名 */
	private static final String CRASH_REPORTER_EXTENSION = ".cr";

	public CrashHandler() {
	}

	public static CrashHandler getInstance() {
		return crashHandler;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && defaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			defaultHandler.uncaughtException(thread, ex);
		} else {
			// Sleep一会后结束程序
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "Error : ", e);
			}

			android.os.Process.killProcess(android.os.Process.myPid());

			System.exit(10);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		if (DEBUG) {
			ex.printStackTrace();
		}
		final Throwable ext=ex;
		final String msg = ex.getLocalizedMessage();
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "程序出错啦:" + msg, Toast.LENGTH_LONG)
						.show();
				collectCrashDeviceInfo(mContext);
				// 保存错误报告文件
				// String crashFileName =
				saveCrashInfoToFile(ext);
				// 发送错误报告到服务器
				sendCrashReportsToServer(mContext);
				Looper.loop();
			}

		}.start();
		// 收集设备信息
		return true;
	}

	/**
	 * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
	 */
	public void sendPreviousReportsToServer() {
		sendCrashReportsToServer(mContext);
	}

	/**
	 * 把错误报告发送给服务器,包含新产生的和以前没发送的.
	 * 
	 * @param ctx
	 */
	private void sendCrashReportsToServer(Context ctx) {
		String[] crFiles = getCrashReportFiles(ctx);
		if (crFiles != null && crFiles.length > 0) {
			TreeSet<String> sortedFiles = new TreeSet<String>();
			sortedFiles.addAll(Arrays.asList(crFiles));

			for (String fileName : sortedFiles) {
				File cr = new File(ctx.getFilesDir(), fileName);
				postReport(cr);
				cr.delete();// 删除已发送的报告
			}
		}
	}

	private void postReport(File file) {
		String systemVersion = android.os.Build.MODEL + ","
				+ android.os.Build.VERSION.SDK + ","
				+ android.os.Build.VERSION.RELEASE;

		String errorDetail = "";

		try {
			FileInputStream fis = new FileInputStream(file);

			byte[] buff = new byte[fis.available()];

			fis.read(buff);

			fis.close();

			errorDetail = new String(buff);
		} catch (Exception e) {
			errorDetail = "post exception";
		}
		final String str_ver=this.getVersionName();
		final String str_error_detail=errorDetail;
		
		uploadCrashDetail(str_ver, "1.0", str_error_detail,
				sh.getUserid() + "", System.currentTimeMillis());
	}

	ShareUtil sh = null;
	Map<String, String> mapParams = new HashMap<String, String>();

	/**
	 * @category 上传崩溃详情
	 */

	void uploadCrashDetail(String versionName, String releaseVer,
			String detailMsg, String uid, long timestamp) {
		mapParams.put("key", key_server);
		try {
			RequestByPost resp = new RequestByPost();
			mapParams.put("androidVersion", versionName);
			mapParams.put("versionRelease", releaseVer);
			mapParams.put("detailMsg", detailMsg);
			mapParams.put("uid", uid);
			mapParams.put("time", timestamp + "");

//			RespVo vo = resp.sendPostPhp("http://192.168.1.104:112/api/default/addrecordlog", mapParams);
			RespVo vo = resp.sendPostPhp(MURL_LOG, mapParams);
			if (vo == null) {
				Log.d("----uploadCrashDetail--上传出错！--》》-",
						"----uploadCrashDetail--");
			} else {
				if (vo.isHasError()) {
					Log.d("----uploadCrashDetail--上传出错！--》》-" + vo.getErrorDetail(),
							"----uploadCrashDetail--");

				}else{
					Log.d("----uploadCrashDetail》-"+vo.getErrorDetail()+vo.getResp(),
							"----uploadCrashDetail--");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		

	}

	private String getVersionName() {
		PackageManager packageManager = mContext.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		String version = "E";
		try {
			packInfo = packageManager.getPackageInfo(mContext.getPackageName(),
					0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			Log.d(TAG, "" + e);
		}

		return version;
	}

	/**
	 * 获取错误报告文件名
	 * 
	 * @param ctx
	 * @return
	 */
	private String[] getCrashReportFiles(Context ctx) {
		File filesDir = ctx.getFilesDir();
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(CRASH_REPORTER_EXTENSION);
			}
		};
		return filesDir.list(filter);
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return
	 */
	private String saveCrashInfoToFile(Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);

		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}

		String result = info.toString();
		printWriter.close();
		mDeviceCrashInfo.put(STACK_TRACE, result);

		try {
			long timestamp = System.currentTimeMillis();
			String fileName = "crash-" + timestamp + CRASH_REPORTER_EXTENSION;
			FileOutputStream trace = mContext.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			mDeviceCrashInfo.store(trace, "");
			trace.flush();
			trace.close();
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing report file...", e);
		}
		return null;
	}

	/**
	 * 收集程序崩溃的设备信息
	 * 
	 * @param ctx
	 */
	public void collectCrashDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				mDeviceCrashInfo.put(VERSION_NAME,
						pi.versionName == null ? "not set" : pi.versionName);
				mDeviceCrashInfo.put(VERSION_CODE, "" + pi.versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Error while collect package info", e);
		}
		// 使用反射来收集设备信息.在Build类中包含各种设备信息,
		// 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
		// 具体信息请参考后面的截图
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				mDeviceCrashInfo.put(field.getName(), "" + field.get(null));
				if (DEBUG) {
					Log.d(TAG, field.getName() + " : " + field.get(null));
				}
			} catch (Exception e) {
				Log.e(TAG, "Error while collect crash info", e);
			}

		}

	}

	/**
	 * 初始化默认异常处理器
	 * 
	 * @param ctx
	 */
	public void init(Context ctx) {
		mContext = ctx;
//		if (null == resp) {
//
//			resp = new RequestByPost();
//		}
		if (sh == null) {
			sh = ShareUtil.getInstance(mContext);
		}
		defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

		Thread.setDefaultUncaughtExceptionHandler(this);
	}

}
