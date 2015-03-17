package com.swater.meimeng.activity.adapterGeneral.dowmImg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import com.swater.meimeng.activity.newtabMain.downquence.CellDownTask;
import com.swater.meimeng.activity.newtabMain.downquence.CellTaskMgr;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.OMdown.KMediaUtil;
import com.swater.meimeng.mutils.remoteview.CachePoolException;
import com.swater.meimeng.mutils.remoteview.CachePoolManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ImageDownloader {
	private static final String TAG = "ImageDownloader";
	private HashMap<String, MyAsyncTask> map = new HashMap<String, MyAsyncTask>();
	LinkedList<MyAsyncTask> list_down = null;
	private Map<String, SoftReference<Bitmap>> imageCaches = new HashMap<String, SoftReference<Bitmap>>();
	static ImageDownloader idl;

	private ImageDownloader() {

		list_down = new LinkedList<ImageDownloader.MyAsyncTask>();
	}

	public void clearMemory() {

		if (imageCaches != null) {
			Log.d("-clearMemory 清除三方ImageDownloader内存----", "--->>"
					+ imageCaches.size());
			imageCaches.clear();
		}
	}

	public static synchronized ImageDownloader getInstance() {

		if (idl == null) {
			idl = new ImageDownloader();

		}
		return idl;
	}

	public void imageDownload(String url, ImageView mImageView, String path,
			Activity mActivity, OnImageDownload download) {
		SoftReference<Bitmap> currBitmap = imageCaches.get(url);
		Bitmap softRefBitmap = null;
		if (currBitmap != null) {
			softRefBitmap = currBitmap.get();
		}
		String imageName = "";
		if (url != null) {
			imageName = Util.getInstance().getImageName(url);
		}
		if (currBitmap != null && mImageView != null && softRefBitmap != null
				&& url.equals(mImageView.getTag())) {
			mImageView.setImageBitmap(softRefBitmap);
		} else if (mImageView != null && url.equals(mImageView.getTag())) {
			Bitmap bitmap = getBitmapFromFile(mActivity, imageName, path);
			if (bitmap != null) {
				mImageView.setImageBitmap(bitmap);
				imageCaches.put(url, new SoftReference<Bitmap>(bitmap));
			} else if (url != null && needCreateNewTask(mImageView)) {
				MyAsyncTask task = new MyAsyncTask(url, mImageView, path,
						mActivity, download);
				if (mImageView != null) {
					Util.flag++;
					task.execute();
					map.put(url, task);
					list_down.add(task);
				}
			}
		}
	}

	/**
	 * 
	 * @param url
	 * @param mImageView
	 * @return
	 */
	private boolean needCreateNewTask(ImageView mImageView) {
		boolean b = true;
		if (mImageView != null) {
			String curr_task_url = (String) mImageView.getTag();
			if (isTasksContains(curr_task_url)) {
				b = false;
			}
		}
		return b;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	private boolean isTasksContains(String url) {
		boolean b = false;
		if (map != null && map.get(url) != null) {
			b = true;
		}
		return b;
	}

	/**
	 * 
	 * @param url
	 */
	private void removeTaskFormMap(String url) {
		if (url != null && map != null && map.get(url) != null) {
			map.remove(url);
			System.out.println("removeTaskFormMap=" + map.size());
		}
	}

	private Bitmap getBitmapFromFile(Activity mActivity, String imageName,
			String path) {
		Bitmap bitmap = null;
		if (imageName != null) {
			File file = null;
			String real_path = "";
			try {
				if (Util.getInstance().hasSDCard()) {
					real_path = Util.getInstance().getExtPath()
							+ (path != null && path.startsWith("/") ? path
									: "/" + path);
				} else {
					real_path = Util.getInstance().getPackagePath(mActivity)
							+ (path != null && path.startsWith("/") ? path
									: "/" + path);
				}
				file = new File(real_path, imageName);
				if (file.exists()) {
					bitmap = KMediaUtil.decodeImageFile(file.getPath(), false);
				}
				// bitmap = BitmapFactory.decodeStream(new FileInputStream(
				// file));
			} catch (Exception e) {
				e.printStackTrace();
				bitmap = null;
			}
		}
		return bitmap;
	}

	/**
	 * @return
	 */
	private boolean setBitmapToFile(String path, Activity mActivity,
			String imageName, Bitmap bitmap) {
		File file = null;
		String real_path = "";
		try {
			if (Util.getInstance().hasSDCard()) {
				real_path = Util.getInstance().getExtPath()
						+ (path != null && path.startsWith("/") ? path : "/"
								+ path);
			} else {
				real_path = Util.getInstance().getPackagePath(mActivity)
						+ (path != null && path.startsWith("/") ? path : "/"
								+ path);
			}
			file = new File(real_path, imageName);
			if (!file.exists()) {
				File file2 = new File(real_path + "/");
				file2.mkdirs();
			}
			file.createNewFile();
			FileOutputStream fos = null;
			if (Util.getInstance().hasSDCard()) {
				fos = new FileOutputStream(file);
			} else {
				fos = mActivity.openFileOutput(imageName, Context.MODE_PRIVATE);
			}

			if (imageName != null
					&& (imageName.contains(".png") || imageName
							.contains(".PNG"))) {
				// bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			} else {
				// bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			}
			fos.flush();
			if (fos != null) {
				fos.close();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * @param path
	 * @param mActivity
	 * @param imageName
	 */
	private void removeBitmapFromFile(String path, Activity mActivity,
			String imageName) {
		File file = null;
		String real_path = "";
		try {
			if (Util.getInstance().hasSDCard()) {
				real_path = Util.getInstance().getExtPath()
						+ (path != null && path.startsWith("/") ? path : "/"
								+ path);
			} else {
				real_path = Util.getInstance().getPackagePath(mActivity)
						+ (path != null && path.startsWith("/") ? path : "/"
								+ path);
			}
			file = new File(real_path, imageName);
			if (file != null)
				file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {
		private ImageView mImageView;
		private String url;
		private OnImageDownload download;
		private String path;
		private Activity mActivity;

		public MyAsyncTask(String url, ImageView mImageView, String path,
				Activity mActivity, OnImageDownload download) {
			this.mImageView = mImageView;
			this.url = url;
			this.path = path;
			this.mActivity = mActivity;
			this.download = download;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap data = null;

			if (CachePoolManager.isIndexExistFile(url)) {
				long free_memory = Runtime.getRuntime().freeMemory() / 1024 / 1204;

				if (free_memory < 1) {
					Runtime.getRuntime().gc();
				}

				data = KMediaUtil.decodeImageFile(CachePoolManager
						.getIndexFileUrl(url).getPath(), false);
				String imageName = Util.getInstance().getImageName(url);
				if (!setBitmapToFile(path, mActivity, imageName, data)) {
					removeBitmapFromFile(path, mActivity, imageName);
				}

				if (null == data) {
					try {
						data = getBitmap(url, 480 * 400, false);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				imageCaches.put(url, new SoftReference<Bitmap>(data));

			}

			else {
				try {
					data = getBitmap(url, 400 * 480, false);
					String imageName = Util.getInstance().getImageName(url);
					if (!setBitmapToFile(path, mActivity, imageName, data)) {
						removeBitmapFromFile(path, mActivity, imageName);
					}
					imageCaches.put(url, new SoftReference<Bitmap>(data));
					// imageCaches.put(
					// url,
					// new SoftReference<Bitmap>(data.createScaledBitmap(
					// data, 100, 100, true)));

				} catch (Exception e) {
					// TODO: handle exception
				}

			}
			return data;
			// ------------

			/**
			 * if (url != null) { try { URL c_url = new URL(url);
			 * 
			 * try { InputStream in = c_url.openStream();
			 * data=KMediaUtil.decodeImageStream(in); // data =
			 * BitmapFactory.decodeStream(in); if (in!=null) { in.close(); } }
			 * catch (OutOfMemoryError e) {
			 * 
			 * 
			 * } String imageName = Util.getInstance().getImageName(url); if
			 * (!setBitmapToFile(path, mActivity, imageName, data)) {
			 * removeBitmapFromFile(path, mActivity, imageName); }
			 * imageCaches.put( url, new
			 * SoftReference<Bitmap>(data.createScaledBitmap( data, 100, 100,
			 * true)));
			 * 
			 * 
			 * } catch (Exception e) { e.printStackTrace(); } } return data;
			 */
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (download != null) {
				download.onDownloadSucc(result, url, mImageView);
				removeTaskFormMap(url);
			}
			super.onPostExecute(result);
		}

	}

	// new paresel url--img
	public Bitmap getBitmap(String url, int displaypixels, Boolean isBig)
			throws MalformedURLException, IOException {

		Bitmap bmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		InputStream stream = new URL(url).openStream();
		/**
		 * try {
		 * 
		 * InputStream stream = new URL(url).openStream();
		 * 
		 * 
		 * bmp=KMediaUtil.decodeImageStream(stream, true); if (null!=stream) {
		 * stream.close(); NSLoger.Log("--关闭读取网络图片--"); } File temFile =
		 * CachePoolManager.getIndexFileUrl(url); KMediaUtil.saveFile(bmp,
		 * temFile); } catch (Exception e) { // TODO: handle exception }
		 */

		byte[] bytes = getBytes(stream);
		if (null == bytes) {
			return null;
		}
		if (null != stream) {
			stream.close();
		}
		// 这3句是处理图片溢出的begin( 如果不需要处理溢出直接 opts.inSampleSize=1;)
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		opts.inSampleSize = computeSampleSize(opts, -1, displaypixels);
		// end
		opts.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		try {

			saveCache(url, bytes);
		} catch (Exception e) {
			return bmp;
			// TODO: handle exception
		}
		return bmp;
	}

	/**
	 * 数据流转成btyle[]数组
	 * */
	private byte[] getBytes(InputStream is) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// byte[] b = new byte[2048];
		byte[] b = new byte[1024];
		int len = 0;
		byte[] bytes = null;
		try {
			while ((len = is.read(b, 0, 1024)) != -1) {
				baos.write(b, 0, len);
				baos.flush();
			}
			bytes = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			System.gc();
			System.runFinalization();
			// app.ReleaseForce();
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
			if (temFile.exists() && temFile.isFile()) {
				return;
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

	public void Force_Lease() {

		try {
			if (list_down != null) {
				if (list_down.size() > 0) {
					for (MyAsyncTask ta : list_down) {
						if (!ta.isCancelled()) {
							ta.cancel(true);
							map.clear();
							Log.d("--清楚vip---", "----vip--");

						}

					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
