package com.swater.meimeng.activity.newtabMain.downquence;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.OMdown.KMediaUtil;
import com.swater.meimeng.mutils.OMdown.ULogger;
import com.swater.meimeng.mutils.remoteview.CachePoolException;
import com.swater.meimeng.mutils.remoteview.CachePoolManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

public class CellDownTask implements Runnable {
	String url = "";
	ImageView imv;
	Bitmap bit;

	public String getIdName() {
		return url;
	}
	Boolean isFinish=false;
	

	public Boolean getIsFinish() {
		return isFinish;
	}
	public void setIsFinish(Boolean isFinish) {
		this.isFinish = isFinish;
	}
	public CellDownTask(String name, ImageView imv) {

		this.url = name;
		this.imv = imv;
	}

	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 9: {
				
				if (bit!=null&&imv!=null) {
					imv.setImageBitmap(bit);
					setIsFinish(true);
					System.out.println("-下载完毕啦-name哈-->>" + url + "--ok--");
				}

			}

				break;

			default:
				break;
			}
		};
	};

	@Override
	public void run() {
		try {
			if (CachePoolManager.isIndexExistFile(url)) {
				long free_memory = Runtime.getRuntime().freeMemory() / 1024/1204;

				if (free_memory <=1) {
					NSLoger.Log("------下载用户详情--低内存警告！-------");
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
				bit = KMediaUtil.decodeImageFile(CachePoolManager
						.getIndexFileUrl(url).getPath(), true);


			}

			else {

				bit = getBitmap(url, 400*80, true);

			}
			hand.obtainMessage(9).sendToTarget();
			Thread.sleep(100);
		} catch (Exception e) {
			setIsFinish(false);
			e.printStackTrace();
			System.out.println("-下载失败！--fail  ||@######||-->>" + url + "--fail--to -save--");
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
					Log.d("--详情图片已保存--过--", "--url--"+url);
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
			Log.d("--详情图片--保存中ing--ok--", "--url--"+url);
		}

		/**
		 * 数据流转成btyle[]数组
		 * */
		private byte[] getBytes(InputStream is) {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			byte[] b = new byte[2048];
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

}
