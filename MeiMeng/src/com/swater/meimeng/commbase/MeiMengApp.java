package com.swater.meimeng.commbase;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import com.meimeng.app.R;
import com.nostra13.universalimageloader.cache.disc.impl.FileCountLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.StrictMode;
import cn.jpush.android.api.JPushInterface;

import com.swater.meimeng.activity.adapterGeneral.dowmImg.ImageDownloader;
import com.swater.meimeng.activity.user.CustomDialog;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.net.MUrlPostAddr;
import com.swater.meimeng.mutils.trackLog.CrashHandler;

public class MeiMengApp extends Application implements MUrlPostAddr {
	Map<String, String> mapParams = new HashMap<String, String>();
	private Map<String, SoftReference<Bitmap>> mapParams_bit = new HashMap<String, SoftReference<Bitmap>>();
	ImageDownloader load_mgr = null;

    /**
     *  判断是否是游客
     */
    public static volatile boolean isVisitor = false;

    public Context context;

	// UserVo uv = null;
	/** ThinkAndroid 文件缓存 */
	// private TAFileCache mFileCache;
	// private static MeiMengApp application;
	// private static final String SYSTEMCACHE = "thinkandroid";

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}

	public ImageDownloader getLoad_mgr() {
		return load_mgr;
	}

	public Map<String, SoftReference<Bitmap>> getMapParams_bit() {
		return mapParams_bit;
	}

	public void setMapParams_bit(
			Map<String, SoftReference<Bitmap>> mapParams_bit) {
		this.mapParams_bit = mapParams_bit;
	}

	public void Release() {
		if (null == mapParams_bit) {
			return;
		}
		if (mapParams_bit.size() >= 40) {
			NSLoger.Log("--清除内存缓存---" + mapParams.size());
			mapParams_bit.clear();

		}
	}

	public void ReleaseForce() {
		if (null == mapParams_bit) {
			return;
		}
		NSLoger.Log("--清除内存缓存---" + mapParams.size());
		mapParams_bit.clear();
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onCreate() {
		super.onCreate();
		// Thread.setDefaultUncaughtExceptionHandler(new
		// Thread.UncaughtExceptionHandler() {
		// public void uncaughtException(Thread thread, Throwable ex) {
		// NSLoger.Log("======--*********--.exit---detail--！！！！*********===="
		// + ex.getMessage());
		// android.os.Process.killProcess(android.os.Process.myPid());
		// System.exit(10);
		// }
		//
		// });

        context = getApplicationContext();
		try {
			JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
			JPushInterface.init(this); // 初始化 JPush
			if (load_mgr == null) {
				load_mgr = ImageDownloader.getInstance();
			}
			iniLoaderImg();
		} catch (OutOfMemoryError e) {
			// android.os.Process.killProcess(android.os.Process.myPid());
			// System.exit(0);
			// TODO: handle exception
		} finally {
		}

	}

	void iniRecord() {
		CrashHandler crashHandler = new CrashHandler();
		crashHandler.init(getApplicationContext());

		// CrashHandler.getInstance().init(getApplicationContext());

		// uploadCrashDetail(null, null, null, null, 0l);
	}

	@SuppressWarnings("unused")
	void iniLoaderImg() {
		/**
		 * if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >=
		 * Build.VERSION_CODES.GINGERBREAD) { StrictMode.setThreadPolicy(new
		 * StrictMode.ThreadPolicy.Builder()
		 * .detectAll().penaltyDialog().build()); StrictMode.setVmPolicy(new
		 * StrictMode.VmPolicy.Builder() .detectAll().penaltyDeath().build()); }
		 */
		if (Config.DEVELOPER_MODE
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyDeath().build());
		}
		// initImageLoader(getApplicationContext());
		newinitImageLoader(getApplicationContext());
	}

	public static void newinitImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	class ClearBrc extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			load_mgr.clearMemory();
			mapParams_bit.clear();

			if (load_mgr != null) {
				load_mgr.Force_Lease();
				load_mgr = null;
			}
		}

	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		// ClearBrc br = null;
		// context.registerReceiver(new BroadcastReceiver() {
		//
		// @Override
		// public void onReceive(Context context, Intent intent) {
		//
		// };
		// }, new Intent(BroadCmd.CMD_CLEAR_ALL_CACHE));
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()

				// .memoryCacheSize(8 * 1024 * 1024)
				.memoryCache(new LargestLimitedMemoryCache(2000))
				// .memoryCache(new UsingFreqLimitedMemoryCache(20000))

				.discCache(
						new FileCountLimitedDiscCache(StorageUtils
								.getOwnCacheDirectory(context, "/loader_max/"),
								100))
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		// ImageLoader.getInstance().setCon(context);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		try {

			NSLoger.Log("--------->>>app 应用低内存警告--释放资源占用！---》》》");
			load_mgr.clearMemory();
			mapParams_bit.clear();

			if (load_mgr != null) {
				load_mgr.Force_Lease();
				load_mgr = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);

	}
	// public TAFileCache getFileCache()
	// {
	// if (mFileCache == null)
	// {
	// TACacheParams cacheParams = new TACacheParams(this, SYSTEMCACHE);
	// TAFileCache fileCache = new TAFileCache(cacheParams);
	// application.setFileCache(fileCache);
	//
	// }
	// return mFileCache;
	// }
	//
	// public void setFileCache(TAFileCache fileCache)
	// {
	// this.mFileCache = fileCache;
	// }

    public static void setVisitorDialogView(Context context,int id){
        CustomDialog dialog = new CustomDialog(context,R.style.VisitorCustomDialog,id);
        dialog.show();
//                WindowManager manager = getWindowManager();
//                Display display = manager.getDefaultDisplay();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        int width = metrics.widthPixels *5 /6;
        int height = metrics.heightPixels/4;
        Log.e("对话框的宽高：", "width = " + width + ";height = " + height);
        dialog.getWindow().setLayout(width, height);
    }
}
