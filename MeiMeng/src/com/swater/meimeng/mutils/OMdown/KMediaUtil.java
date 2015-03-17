package com.swater.meimeng.mutils.OMdown;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.swater.meimeng.mutils.NSlog.NSLoger;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

public class KMediaUtil {

	/**
	 * 保存文件
	 * 
	 * @param bm
	 * @param fileName
	 * @throws IOException
	 */
	public static void saveFile(Bitmap bm, File fl) {
		// File dirFile = new File(ALBUM_PATH);
		// if(!dirFile.exists()){
		// dirFile.mkdir();
		// }
		// File myCaptureFile = new File(ALBUM_PATH + fileName);
		if (bm == null || fl == null) {
			return;
		}
		if (fl.exists() && fl.getTotalSpace() > 2) {
			NSLoger.Log("--图片已经存在不需要缓存！----》》");
			return;
		}

		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(fl));
			bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			NSLoger.Log("--保存图片到本体----save-file--->>");

			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 压缩图片到 480*480
	 * 
	 */
	public static boolean compressImage(String src, String dst, int length) {
		if (!src.equals(dst)) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(src, options);
			// int imageHeight = options.outHeight;
			// int imageWidth = options.outWidth;
			// String imageType = options.outMimeType;
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, 480, 480);
			options.inJustDecodeBounds = false;
			Bitmap bmp = BitmapFactory.decodeFile(src, options);
			try {
				FileOutputStream stream = new FileOutputStream(dst);
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				stream.close();
			} catch (Exception ex) {
				ULogger.e(ex);
			}
		}
		return true;
	}

	/**
	 * 根据缩放计算采样值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		int height = options.outHeight;
		int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			// Calculate ratios of height and width to requested height and
			// width
			int heightRatio = Math.round((float) height / (float) reqHeight);
			int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	/****
	 * 处理图片bitmap size exceeds VM budget （Out Of Memory 内存溢出）
	 */
	private int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 加载图片文件
	 * 
	 * @param path
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap loadImage(String path, int reqWidth, int reqHeight) {
		if (reqWidth < 1 || reqHeight < 1) {
			return BitmapFactory.decodeFile(path);
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	/**
	 * 从流加载图片
	 * 
	 * @param ins
	 * @return
	 */
	public static Bitmap decodeImageStream(InputStream ins) {
		return decodeImageStream(ins, true);
	}

	/**
	 * 从流加载图片
	 * 
	 * @param ins
	 * @param fixSreen
	 *            是否适应屏幕大小
	 * @return
	 */
	public static Bitmap decodeImageStream(InputStream ins, boolean fixSreen) {
		Bitmap bmp = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			BitmapFactory.decodeStream(ins, null, options);
			// options.inSampleSize = fixSreen ? calculateInSampleSize(options,
			// KScreen.screenSize.x, KScreen.screenSize.x) : 1;
			options.inSampleSize = fixSreen ? calculateInSampleSize(options,
					-1, 480 * 800) : 1;
			options.inJustDecodeBounds = false;
			int times = 0;
			do {
				try {
					bmp = BitmapFactory.decodeStream(ins, null, options);
					break;
				} catch (OutOfMemoryError err) {
					ULogger.e(err);
					System.gc();
					times++;
					options.inSampleSize = options.inSampleSize * 2;
				}
			} while (times < 3);
		} catch (Exception ex) {
			ULogger.e(ex);
		}
		return bmp;
	}

	/**
	 * 从文件加载图片
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap decodeImageFile(String path) {
		return decodeImageFile(path, true);
	}

	/**
	 * 从文件加载图片
	 * 
	 * @param path
	 * @param fixSreen
	 *            是否适应屏幕大小
	 * @return
	 */
	public static Bitmap decodeImageFile(String path, boolean fixSreen) {
		Log.d("-推荐首页--读取本地缓存文件0----》》》", path);
		Bitmap bmp = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			int times = 0;
			do {
				try {
					options.inJustDecodeBounds = true;
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					options.inPurgeable = true;
					options.inInputShareable = true;
					BitmapFactory.decodeFile(path, options);
					options.inSampleSize = fixSreen ? calculateInSampleSize(
							options, KScreen.screenSize.x, KScreen.screenSize.x)
							: 1;
					options.inJustDecodeBounds = false;

					bmp = BitmapFactory.decodeFile(path, options);
					break;
				} catch (OutOfMemoryError err) {
					Log.d("-推荐首页-OutOfMemoryError---读取本地缓存文件0-正在压缩---》》》", path);
					ULogger.e(err);
					System.gc();
					options.inSampleSize = options.inSampleSize * 4;
					times++;
					// if (times>2) {
					// return null;
					// }

				}
			} while (times < 3);

			// bmp = rotateImage(getPhotoDegree(path), bmp);
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();
			// Log.d("---推荐首页--imgs-", ex.getMessage());
		}
		return bmp;
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int getPhotoDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (Exception ex) {
			ULogger.e(ex);
		}
		return degree;
	}

	/**
	 * 旋转图片
	 * 
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotateImage(int angle, Bitmap bmp) {
		if (angle != 0 && null != bmp) {
			// 旋转图片 动作
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			// 创建新的图片
			bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
					bmp.getHeight(), matrix, true);
		}
		return bmp;
	}

	/**
	 * 旋转图片文件
	 * 
	 * @param angle
	 * @param src
	 * @param dst
	 */
	public static boolean copyImageFile(String src, String dst) {
		Bitmap bmp = decodeImageFile(src, false);
		// 已经在加载时实现了旋转
		// bmp = rotateImage(angle, bmp);
		try {
			FileOutputStream stream = new FileOutputStream(dst);
			bmp.compress(Bitmap.CompressFormat.JPEG, 90, stream);
			stream.close();
		} catch (Exception ex) {
			ULogger.e(ex);
		}
		return new File(dst).exists();
	}

	/**
	 * 彩色->黑白灰
	 * 
	 * @param bmpOriginal
	 * @return
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width = bmpOriginal.getWidth();
		int height = bmpOriginal.getHeight();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	/**
	 * 设置透明度
	 * 
	 * @param bitmap
	 * @param alpha
	 * @return
	 */
	public static Bitmap resetAlpha(Bitmap bitmap, int alpha) {
		int[] argb = new int[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(argb, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
				bitmap.getHeight());// 获得图片的ARGB值
		alpha = (alpha & 0x00FF) << 24;
		for (int i = 0; i < argb.length; i++) {
			argb[i] = alpha | (argb[i] & 0x00FFFFFF);
		}
		return Bitmap.createBitmap(argb, bitmap.getWidth(), bitmap.getHeight(),
				Bitmap.Config.ARGB_8888);
	}

	/**
	 * 添加水印
	 * 
	 * @param src
	 * @param text
	 * @return
	 */
	public static Bitmap watermark(Bitmap src, String text) {
		int w = src.getWidth();
		int h = src.getHeight();

		Bitmap bmp = Bitmap.createBitmap(w, h, src.getConfig());
		Canvas canvas = new Canvas(bmp);

		Paint paint = new Paint();
		paint.setTextSize(15);
		paint.setColor(Color.rgb(255, 0, 0));
		paint.setAntiAlias(true);
		canvas.drawText(text, 0, 0, paint);

		return bmp;
	}

	/**
	 * 检测 Intent 是否有效
	 * 
	 * @param context
	 * @param action
	 * @return
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		PackageManager packageManager = context.getPackageManager();
		Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	/**
	 * 播放音频
	 * 
	 * @param context
	 * @param mrl
	 * @param name
	 * @return
	 */
	public static boolean playAudio(Context context, String mrl, String name) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse(mrl), "audio/*");
			context.startActivity(intent);
			return true;
		} catch (android.content.ActivityNotFoundException ex) {
		}
		return false;
	}

	/**
	 * 播放视频
	 * 
	 * @param context
	 * @param mrl
	 * @return
	 */
	public static boolean playVideo(Context context, String mrl) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse(mrl), "video/*");
			context.startActivity(intent);
			return true;
		} catch (android.content.ActivityNotFoundException ex) {
			// VideoPlayerActivity.play(context, mrl);
		}
		return false;
	}

	/**
	 * Making the camera give you an already rotated image:
	 * 
	 * http://stackoverflow.com/questions/5372620/android-large-image-rotation
	 * 
	 * @param context
	 * @param cameraId
	 * @param camera
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static void setCameraDisplayOrientation(Context context,
			int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		// set the right preview orientation
		camera.setDisplayOrientation(result);
		// make the camera output a rotated image
		android.hardware.Camera.Parameters cameraParameters = camera
				.getParameters();
		cameraParameters.setRotation(result);
		camera.setParameters(cameraParameters);
	}
}
