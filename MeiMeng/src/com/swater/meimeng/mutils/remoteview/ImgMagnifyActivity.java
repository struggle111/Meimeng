package com.swater.meimeng.mutils.remoteview;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.meimeng.app.R;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.mutils.imgs.SaveImg;

public class ImgMagnifyActivity extends BaseTemplate {

	/**
	 * @chengshiyang
	 * */
	Bitmap bitmapsave = null;
	private Context context;
	String path = "/sdcard/meimeng/index/";
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;

	float x_down = 0;
	float y_down = 0;
	private int mode = NONE;
	private float oldDist;
	private Matrix matrix = new Matrix();
	Matrix matrix1 = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	private PointF mid = new PointF();
	ImageView showimg;
	String url = "";
	float heightScreen;
	float widthScreen;
	ImageView mainbg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imgdiag);
		context = this;
		TempRun();
	}

	@Override
	public void iniView() {

		showTitle("图片");
		DisplayMetrics dm = new DisplayMetrics(); // 获取屏幕宽度高度
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		heightScreen = dm.heightPixels;
		widthScreen = dm.widthPixels;
		showimg = (ImageView) findViewById(R.id.imgshow_);
		showNavgationLeftBar("返回");
		view_Hide(findButton(R.id.home_right_btn));
		parserUrl();
		try {
			downStartImg();

		} catch (Exception e) {
			handler.obtainMessage(12,e.getMessage()).sendToTarget();
		}

	}

	@Override
	public void bindClick() {
		bindNavgationEvent();

	}

	Thread down_thread = null;

	

	void downStartImg() {
		ProShow("正在加载中...");
		poolThread.submit(new Runnable() {
			
			@Override
			public void run() {
				try {
					parserUrl();
					Bitmap bmp = null;
					int _displaypixels = getDisplayScreen().heightPixels
							* getDisplayScreen().widthPixels;
					if (CachePoolManager.isExistFile(url)) {
						byte[] bytes = CachePoolManager
								.readByteByFile(CachePoolManager
										.getFileUrl(url));
						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inJustDecodeBounds = true;
						BitmapFactory.decodeByteArray(bytes, 0,
								bytes.length, opts);
						opts.inSampleSize = computeSampleSize(opts, -1,
								_displaypixels);
						// end
						opts.inJustDecodeBounds = false;
						bmp = BitmapFactory.decodeByteArray(bytes, 0,
								bytes.length, opts);

					} else {

						bmp = getBitmap(url, _displaypixels, true);
						SaveImg.saveMyBitmap(bmp,
								CachePoolManager.getFileUrl(url));
					}
					if (null == bmp) {

						handler.obtainMessage(15).sendToTarget();
					} else {

						handler.obtainMessage(11, bmp).sendToTarget();
					}

				} catch (Exception e) {
					e.printStackTrace();
					handler.obtainMessage(12,e.getMessage()).sendToTarget();
					// TODO: handle exception
				}				
			}
		});
//		if (null == down_thread) {
//			down_thread = new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//
//					try {
//						parserUrl();
//						Bitmap bmp = null;
//						int _displaypixels = getDisplayScreen().heightPixels
//								* getDisplayScreen().widthPixels;
//						if (CachePoolManager.isExistFile(url)) {
//							byte[] bytes = CachePoolManager
//									.readByteByFile(CachePoolManager
//											.getFileUrl(url));
//							BitmapFactory.Options opts = new BitmapFactory.Options();
//							opts.inJustDecodeBounds = true;
//							BitmapFactory.decodeByteArray(bytes, 0,
//									bytes.length, opts);
//							opts.inSampleSize = computeSampleSize(opts, -1,
//									_displaypixels);
//							// end
//							opts.inJustDecodeBounds = false;
//							bmp = BitmapFactory.decodeByteArray(bytes, 0,
//									bytes.length, opts);
//
//						} else {
//
//							bmp = getBitmap(url, _displaypixels, true);
//							SaveImg.saveMyBitmap(bmp,
//									CachePoolManager.getFileUrl(url));
//						}
//						if (null == bmp) {
//
//							handler.obtainMessage(12).sendToTarget();
//						} else {
//
//							handler.obtainMessage(11, bmp).sendToTarget();
//						}
//
//					} catch (Exception e) {
//						handler.obtainMessage(12).sendToTarget();
//						// TODO: handle exception
//					}
//
//				}
//			});
//		}
//		if (!TextUtils.isEmpty(url)) {
//
//			down_thread.start();
//		}

	}

	public Bitmap getBitmap(String url, int displaypixels, Boolean isBig)
			throws MalformedURLException, IOException {
		Bitmap bmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();

		InputStream stream = new URL(url).openStream();
		byte[] bytes = getBytes(stream);
		// 这3句是处理图片溢出的begin( 如果不需要处理溢出直接 opts.inSampleSize=1;)
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		opts.inSampleSize = computeSampleSize(opts, -1, displaypixels);
		// end
		opts.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		return bmp;
	}

	/**
	 * 数据流转成btyle[]数组
	 * */
	private byte[] getBytes(InputStream is) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[2048];
		int len = 0;
		try {
			while ((len = is.read(b, 0, 2048)) != -1) {
				baos.write(b, 0, len);
				baos.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] bytes = baos.toByteArray();
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

	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			ProDimiss();
			switch (msg.what) {
			case 11: {
				if (msg.obj != null) {
					Bitmap bitmapSrc = (Bitmap) msg.obj;
					if (null == bitmapSrc) {
						return;
					}
					bitmapsave = bitmapSrc;
					showimg.setImageBitmap(bitmapSrc);
				
					blindListener();
				} else {
					showToast("图片加载异常！");
				}
			}

				break;
			case 12: {
				showToast("加载图片失败！"+mg2String(msg));

			}

				break;
			case 15: {
				showToast("无法加载原图！！");
				
			}
			
			break;

			default:
				break;
			}

		};
	};

	void parserUrl() {
		if (getIntent() != null) {
			url = getIntent().getStringExtra("url");
		}
	}

	protected void blindListener() {
		if (bitmapsave == null) {
			return;
		}
		showimg.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ImageView view = (ImageView) v;
				// int height = view.getMeasuredHeight();
				// int wid = view.getMeasuredWidth();
				// Log.e("原图大小－－", "--height-->>" + height + "--wid->" +
				// wid);
				switch (event.getAction() & MotionEvent.ACTION_MASK) {

				case MotionEvent.ACTION_DOWN:
					x_down = event.getX();
					y_down = event.getY();
					/*
					 * Object obj_ = context
					 * .getSystemService(Context.WINDOW_SERVICE); WindowManager
					 * wm = (WindowManager) obj_; int envWidth =
					 * wm.getDefaultDisplay().getWidth(); int envHeight =
					 * wm.getDefaultDisplay().getHeight(); float a = (float)
					 * envWidth; float b = (float) envHeight; float x1 =
					 * event.getX(); float y1 = event.getY();
					 */
					matrix.set(view.getImageMatrix());
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					mode = DRAG;
					break;
				case MotionEvent.ACTION_UP:
					/*
					 * Log.v("----ACTION_UP-----", "----"); Object obj_2 =
					 * context .getSystemService(Context.WINDOW_SERVICE);
					 * WindowManager wm2 = (WindowManager) obj_2; int envWidth2
					 * = wm2.getDefaultDisplay().getWidth(); int envHeight2 =
					 * wm2.getDefaultDisplay().getHeight(); float a2 = (float)
					 * envWidth2; float b2 = (float) envHeight2;
					 * savedMatrix.set(matrix); float x12 = event.getX(); float
					 * y12 = event.getY(); start.set(a2 / 2, b2 / 4); mode =
					 * DRAG;
					 */
					break;
				case MotionEvent.ACTION_POINTER_UP:
					// Log.v("----ACTION_POINTER_UP-----", "----");
					mode = NONE;
					break;

				case MotionEvent.ACTION_POINTER_DOWN:
					// Log.v("----ACTION_POINTER_DOWN-----", "----");
					oldDist = spacing(event);
					// Log.v("-oldDist---", oldDist + "---");
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					boolean matrixCheck = false;
					if (mode == ZOOM) {
						matrix1.set(savedMatrix);
						float newDist = spacing(event);
						float scale = newDist / oldDist;
						matrix1.postScale(scale, scale, mid.x, mid.y);// 縮放
						matrixCheck = matrixCheck();
						if (matrixCheck == false) {
							matrix.set(matrix1);
							// invalidate();
						}
					} else if (mode == DRAG) {
						matrix1.set(savedMatrix);
						matrix1.postTranslate(event.getX() - x_down,
								event.getY() - y_down);// 平移
						matrixCheck = matrixCheck();
						if (matrixCheck == false) {
							matrix.set(matrix1);
						}
					}

					break;
				}
				// ／／设置通过矩阵放大
				view.setScaleType(ScaleType.MATRIX);
				view.setImageMatrix(matrix);
				return true;
			}

			private boolean matrixCheck() {
				float[] f = new float[9];
				matrix1.getValues(f);
				// 图片4个顶点的坐标
				float x1 = f[0] * 0 + f[1] * 0 + f[2];
				float y1 = f[3] * 0 + f[4] * 0 + f[5];
				float x2 = f[0] * bitmapsave.getWidth() + f[1] * 0 + f[2];
				float y2 = f[3] * bitmapsave.getWidth() + f[4] * 0 + f[5];
				float x3 = f[0] * 0 + f[1] * bitmapsave.getHeight() + f[2];
				float y3 = f[3] * 0 + f[4] * bitmapsave.getHeight() + f[5];
				float x4 = f[0] * bitmapsave.getWidth() + f[1]
						* bitmapsave.getHeight() + f[2];
				float y4 = f[3] * bitmapsave.getWidth() + f[4]
						* bitmapsave.getHeight() + f[5];
				// 图片现宽度
				double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
						* (y1 - y2));
				// 缩放比率判断
				if (width < widthScreen / 2 || width > widthScreen * 2) {
					return true;
				}
				// 出界判断
				if ((x1 < widthScreen / 3 && x2 < widthScreen / 3
						&& x3 < widthScreen / 3 && x4 < widthScreen / 3)
						|| (x1 > widthScreen * 2 / 3
								&& x2 > widthScreen * 2 / 3
								&& x3 > widthScreen * 2 / 3 && x4 > widthScreen * 2 / 3)
						|| (y1 < heightScreen / 3 && y2 < heightScreen / 3
								&& y3 < heightScreen / 3 && y4 < heightScreen / 3)
						|| (y1 > heightScreen * 2 / 3
								&& y2 > heightScreen * 2 / 3
								&& y3 > heightScreen * 2 / 3 && y4 > heightScreen * 2 / 3)) {
					return true;
				}
				return false;
			}

			private float spacing(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				// Log.v("---x--", x + "--");
				// Log.v("---y--", y + "--");
				return FloatMath.sqrt(x * x + y * y);
			}

			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}
		});

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		judgeOriention();
	}

	void judgeOriention() {
		int mCurrentOrientation = getResources().getConfiguration().orientation;

		if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {

			// showToast("竖立");

		}
		if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			// showToast("横屏幕");

		}
	}

	// -----------
	private class GetSrcImageDataTask extends AsyncTask<Void, Void, byte[]> {

		@Override
		protected byte[] doInBackground(Void... params) {
			parserUrl();
			byte[] data_ = null;
			if (url != null || !"".equals(url)) {
				String dourl = url;// utilPath(url);
				// File tempFile = new File(CachePoolManager.TEMP_DIR,
				// CachePoolManager.parseUrlToTempFile(url));
				data_ = CachePoolManager.loadCacheOrDownload(dourl);
				// try {
				// data_ = CachePoolManager.readByteByFile(tempFile);
				// } catch (CachePoolException e) {
				// e.printStackTrace();
				// }

			} else {
				Log.d("url-->null---", "imgineManify-------");
			}
			return data_;
		}

		@Override
		protected void onPreExecute() {
			ProShow("正在加载原图...");
			// pb.showPb();
		}

		@Override
		protected void onPostExecute(byte[] imageData) {
			// pb.hidePb();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap bitmapSrc = BitmapFactory.decodeByteArray(imageData, 0,
					imageData.length, options);

			Object obj_ = context.getSystemService(Context.WINDOW_SERVICE);
			WindowManager wm = (WindowManager) obj_;
			int envWidth = wm.getDefaultDisplay().getWidth();
			int envHeight = wm.getDefaultDisplay().getHeight();

			options.inJustDecodeBounds = false;
			if (options.outHeight > envHeight) {
				int be = options.outHeight / envHeight;
				options.inSampleSize = be;
			} else {
				int be = options.outWidth / envWidth;
				if (be >= 1)
					be = 1;
				options.inSampleSize = be;
			}

			bitmapSrc = null;

			int retryCount = 0;
			while (retryCount++ < 3) {
				// 如果处理重试状态,每次缩小一半再解析
				if (retryCount > 0)
					options.inSampleSize *= 2;

				try {
					bitmapSrc = BitmapFactory.decodeByteArray(imageData, 0,
							imageData.length, options);
				} catch (Exception e) {
					Log.d("", "解析大图时outofmemory,重试次数:" + retryCount);
				}

				if (bitmapSrc != null) {
					break;
				}
			}

			// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦

			handler.obtainMessage(11, bitmapSrc).sendToTarget();
			// bitmapsave = bitmapSrc;
			// showimg.setImageBitmap(bitmapSrc);
			// blindListener();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_left_btn: {
			sysback();

		}

			break;

		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (handler!=null) {
			handler=null;
			
		}
		closePool();
//		if (down_thread!=null) {
//			down_thread.stop();
//			down_thread=null;
//		}
		
	}

}
