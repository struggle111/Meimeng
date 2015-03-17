package com.swater.meimeng.mutils.imgs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;

public class SaveImg {

	/*
	 * @chengshiyang
	 */
	static String path = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/meimeng/save/";

	// "/sdcard/meimeng/save/";

	/**
	 * 设置图片圆角
	 * 
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		if (bitmap == null) {

			return null;
		}

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static String saveMyBitmap(Bitmap mBitmap, File file) {
		// String fullPath = "";
		// getStringfileName();
		// isExist(path);
		// fullPath = path + getStringfileName() + ".png";
		File f = file;// new File(file);
		try {
			f.createNewFile();
		} catch (Exception e) {
			System.out.println("在保存图片时出错：" + e.toString());
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (null!=f) {
			String ph = f.getPath();
			
			System.out.println("在保存图片---->>" + ph);
		}
		return null;
	}

	public static String saveMyBitmap(Bitmap mBitmap) {
		String fullPath = "";
		getStringfileName();
		isExist(path);
		fullPath = path + getStringfileName() + ".png";
		File f = new File(fullPath);
		try {
			f.createNewFile();
		} catch (Exception e) {
			System.out.println("在保存图片时出错：" + e.toString());
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fullPath;
	}

	public static String getStringfileName() {

		int as = (int) (Math.random() * 10000);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		sdf.format(date);
		String rs = sdf.format(date) + "_" + as;
		Log.d("SaveImg", "----------random---filename--" + rs);
		return rs;
	}

	public static void isExist(String path) {
		File file = new File(path);
		// 判断文件夹是否存在,如果不存在则创建文件夹
		if (!file.exists()) {
			file.mkdir();
		}
	}

}
