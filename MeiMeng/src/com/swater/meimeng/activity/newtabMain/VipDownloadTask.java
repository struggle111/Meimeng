package com.swater.meimeng.activity.newtabMain;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.swater.meimeng.commbase.MeiMengApp;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.OMdown.KMediaUtil;
import com.swater.meimeng.mutils.OMdown.ULogger;
import com.swater.meimeng.mutils.remoteview.CachePoolException;
import com.swater.meimeng.mutils.remoteview.CachePoolManager;

public class VipDownloadTask extends AsyncTask<Object, Object, Bitmap> {
	private ImageView imageView = null;
	LinkedList<Bitmap> down_bitmaps = new LinkedList<Bitmap>();

	int img_type = 1;
	String url = null;
	MeiMengApp app = null;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public VipDownloadTask() {
	}

	public VipDownloadTask(Context con) {
		if (con != null) {
			if (con instanceof Activity) {
				Activity cls = (Activity) con;

				app = (MeiMengApp) cls.getApplication();
			}
		}

	}

	public int getImg_type() {
		return img_type;
	}

	public void setImg_type(int img_type) {
		this.img_type = img_type;
	}

	public LinkedList<Bitmap> getDown_bitmaps() {
		return down_bitmaps;
	}

	public void setDown_bitmaps(LinkedList<Bitmap> down_bitmaps) {
		this.down_bitmaps = down_bitmaps;
	}

	/***
	 * 这里获取到手机的分辨率大小
	 * */
	public void setDisplayWidth(int width) {
		_displaywidth = width;
	}

	public int getDisplayWidth() {
		return _displaywidth;
	}

	public void setDisplayHeight(int height) {
		_displayheight = height;
	}

	public int getDisplayHeight() {
		return _displayheight;
	}

	public int getDisplayPixels() {
		return _displaypixels;
	}

	private int _displaywidth = 480;
	private int _displayheight = 800;
	private int _displaypixels = _displaywidth * _displayheight;

	@Override
	protected Bitmap doInBackground(Object... params) {
		Bitmap bmp = null;
		if (params == null && params.length < 2) {
			return null;
		}

		imageView = (ImageView) params[1];
		try {
			this.url = (String) params[0];
			if (null == url) {
				return null;
			}
			setUrl(url);
			if (app != null) {
				if (app.getMapParams_bit().containsKey(this.url)) {
					NSLoger.Log("---预载入-加载内存缓存-----》》》。");
					return app.getMapParams_bit().get(url).get();

				}
			}

			if (CachePoolManager.isIndexExistFile(url)) {
				long free_memory = Runtime.getRuntime().freeMemory() / 1024/1204;

				if (free_memory < 2) {
					NSLoger.Log("--低内存警告！-------");
					app.ReleaseForce();
					Runtime.getRuntime().gc();
				}

				/**
				 * byte[] bytes =
				 * CachePoolManager.readByteByFile(CachePoolManager
				 * .getIndexFileUrl(url)); long lg = bytes.length / 1024;
				 * NSLoger.Log("-1----1000k--->>"); BitmapFactory.Options opts =
				 * new BitmapFactory.Options(); opts.inJustDecodeBounds = true;
				 * BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
				 * opts.inSampleSize = computeSampleSize(opts, -1,
				 * _displaypixels); // end opts.inJustDecodeBounds = false; bmp
				 * = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
				 * opts);
				 */
				bmp = KMediaUtil.decodeImageFile(CachePoolManager
						.getIndexFileUrl(url).getPath(), true);
//				bmp = zoomImg(bmp, 300, 300);    
//				bmp = KMediaUtil.loadImage(CachePoolManager.getIndexFileUrl(url).getPath(), 400, 400);

				return bmp;

			}

			else {

				bmp = getBitmap(url, _displaypixels, true);
//				bmp = zoomImg(bmp, 300, 300);

			}

		} catch (Exception e) {
			return null;
		}
		return bmp;

	}
	// 缩放图片
	public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
	   // 获得图片的宽高
	   int width = bm.getWidth();
	   int height = bm.getHeight();
	   // 计算缩放比例
	   float scaleWidth = ((float) newWidth) / width;
	   float scaleHeight = ((float) newHeight) / height;
	   // 取得想要缩放的matrix参数
	   Matrix matrix = new Matrix();
	   matrix.postScale(scaleWidth, scaleHeight);
	   // 得到新的图片
	   Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
	    return newbm;
	}
	public void ReUser(Bitmap bp) {
		if (bp != null && !bp.isRecycled()) {
			NSLoger.Log("--回收利用---》》");
			// bp.recycle();
			bp = null;
			System.gc();
		}
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		if (imageView != null && result != null) {
			imageView.setImageBitmap(result);
			imageView.setTag(2);
			// down_bitmaps.add(result);

			// ReUser(result);
			cancel(true);
			if (app != null && this.url != null) {
				app.getMapParams_bit().put(this.url, new SoftReference<Bitmap>(result));
				NSLoger.Log("--成功-放入内存缓存-----》》。");

			} else {
				NSLoger.Log("-fail-to-放入内存缓存-----》》。");
			}
		}

	}
//new paresel url--img
	public Bitmap getBitmap(String url, int displaypixels, Boolean isBig)
			throws MalformedURLException, IOException {
		
		Bitmap bmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		InputStream stream = new URL(url).openStream();
		/**try {

			InputStream stream = new URL(url).openStream();
			
			
			bmp=KMediaUtil.decodeImageStream(stream, true);
			if (null!=stream) {
				stream.close();
				NSLoger.Log("--关闭读取网络图片--");
			}
			File temFile = CachePoolManager.getIndexFileUrl(url);
			KMediaUtil.saveFile(bmp, temFile);
		} catch (Exception e) {
			// TODO: handle exception
		}*/
		
		
		
		byte[] bytes = getBytes(stream);
		if (null==bytes) {
			return null;
		}
		if (null != stream) {
			stream.close();
		}
		saveCache(url, bytes);
		// 这3句是处理图片溢出的begin( 如果不需要处理溢出直接 opts.inSampleSize=1;)
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		opts.inSampleSize = computeSampleSize(opts, -1, displaypixels);
		// end
		opts.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		return bmp;
	}

	public Bitmap getBitmapByInStream_New(String url, int displaypixels,
			Boolean isBig) throws MalformedURLException, IOException {
		Bitmap bmp = null;

		try {
			
			InputStream stream = new URL(url).openStream();
			bmp = KMediaUtil.decodeImageStream(stream, true);
		} catch (Exception e) {
			// TODO: handle exception
		}
		 return bmp;
		/**
		 * byte[] bytes = getBytes(stream); if (null != stream) {
		 * stream.close(); } saveCache(getUrl(), bytes); // 这3句是处理图片溢出的begin(
		 * 如果不需要处理溢出直接 opts.inSampleSize=1;) opts.inJustDecodeBounds = true;
		 * BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		 * opts.inSampleSize = computeSampleSize(opts, -1, displaypixels); //
		 * end opts.inJustDecodeBounds = false;
		 * 
		 * int times = 0; do { try { bmp = BitmapFactory.decodeByteArray(bytes,
		 * 0, bytes.length, opts); break; } catch (OutOfMemoryError err) {
		 * ULogger.e(err); System.gc(); times++; opts.inSampleSize =
		 * opts.inSampleSize * 2; } } while (times < 3); return bmp;
		 */
	}

	public Bitmap getBitmapByInstream(String url, int displaypixels,
			Boolean isBig) {
		Bitmap bmp = null;
		try {
			InputStream stream = new URL(url).openStream();

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(stream, null, options);
			options.inSampleSize = computeSampleSize(options, -1, displaypixels);
			options.inJustDecodeBounds = false;
			int times = 0;
			do {
				try {
					bmp = BitmapFactory.decodeStream(stream, null, options);
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
	 * 保存缓存文件并返回数据
	 * 
	 * @param url
	 * @return
	 * @throws CachePoolException
	 */
	public static void saveCache(String url, byte[] buffers) {
		OutputStream tos = null;
		// 检查能否创建缓存文件
		try {
			File temFile = CachePoolManager.getIndexFileUrl(url);
			if (temFile.exists()&&temFile.isFile()) {
				return ;
			}
			// Log.d("--saveCache---->>temFile--" + temFile.getAbsolutePath(),
			// "--saveCache--");
			tos = new FileOutputStream(temFile);
		} catch (FileNotFoundException e1) {
			NSLoger.Log("can not create cache file :" + e1.getMessage());
			return;
		}
		// 写入流数据到缓存文件
		try {
			tos.write(buffers);
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		} finally {
			try {
				tos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 数据流转成btyle[]数组
	 * */
	private byte[] getBytes(InputStream is) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		byte[] b = new byte[2048];
		byte[] b = new byte[1024];
		int len = 0;
		byte[] bytes=null;
		try {
			while ((len = is.read(b, 0, 1024)) != -1) {
				baos.write(b, 0, len);
				baos.flush();
			}
			 bytes = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}catch(OutOfMemoryError e){
			System.gc();System.runFinalization();
			app.ReleaseForce();
			return null;
			
		}
		if (null != is) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (baos != null) {
			try {
				baos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bytes;
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

	void test() {
		VipDownloadTask imgtask = new VipDownloadTask();
		/** 这里是获取手机屏幕的分辨率用来处理 图片 溢出问题的。begin */
		// DisplayMetrics dm = new DisplayMetrics();
		// getWindowManager().getDefaultDisplay().getMetrics(dm);
		// imgtask.setDisplayWidth(dm.widthPixels);
		// imgtask.setDisplayHeight(dm.heightPixels);
		// ImageView imageView_test=
		// (ImageView)findViewById(R.id.imageView_test);
		// imgtask.execute("http://pic.test.com/big/7515/201201031116491.jpg",imageView_test);

	}

}
