package com.swater.meimeng.mutils.imgs;

import com.swater.meimeng.mutils.OMdown.KMediaUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.Display;
import android.view.WindowManager;

public class PhotoUtils {
	static PhotoUtils inst = new PhotoUtils();

	private PhotoUtils() {

	}

	public static PhotoUtils getSingle() {
		return inst;
	}

	public Bitmap parselUriImg(Uri uri, Activity act) {
		Bitmap bitmap = null;
		try {
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeStream(act.getContentResolver()
					.openInputStream(uri), null, options);
			int picWidth = options.outWidth;
			int picHeight = options.outHeight;
			WindowManager windowManager = act.getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			int screenWidth = display.getWidth();
			int screenHeight = display.getHeight();
			options.inSampleSize = 1;
			if (picWidth > picHeight) {
				if (picWidth > screenWidth)
					options.inSampleSize = picWidth / screenWidth;
			} else {
				if (picHeight > screenHeight)
					options.inSampleSize = picHeight / screenHeight;
			}
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeStream(act.getContentResolver()
					.openInputStream(uri), null, options);
			// img_touxiang.setImageBitmap(bitmap);
			/*
			 * if (bitmap.isRecycled() == false) { bitmap.recycle(); }
			 */
			System.gc();
		} catch (Exception e1) {
		}
		return bitmap;
	}

	public  Bitmap scalePicture(String filename, int maxWidth,
			int maxHeight) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			BitmapFactory.decodeFile(filename, opts);
			int srcWidth = opts.outWidth;
			int srcHeight = opts.outHeight;
			int desWidth = 0;
			int desHeight = 0;
			// 缩放比例
			double ratio = 0.0;
			if (srcWidth > srcHeight) {
				ratio = srcWidth / maxWidth;
				desWidth = maxWidth;
				desHeight = (int) (srcHeight / ratio);
			} else {
				ratio = srcHeight / maxHeight;
				desHeight = maxHeight;
				desWidth = (int) (srcWidth / ratio);
			}
			// 设置输出宽度、高度
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inSampleSize = (int) (ratio) + 1;
			newOpts.inJustDecodeBounds = false;
			newOpts.outWidth = desWidth;
			newOpts.outHeight = desHeight;
			bitmap = BitmapFactory.decodeFile(filename, newOpts);

		} catch (Exception e) {
			// TODO: handle exception
		}
		return bitmap;
	}

}
