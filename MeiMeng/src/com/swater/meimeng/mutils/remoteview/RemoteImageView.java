package com.swater.meimeng.mutils.remoteview;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.meimeng.app.R;
import com.swater.meimeng.mutils.NSlog.NSLoger;

/**
 * 异步加载远程图片VIEW
 * 
 * 
 * @version 1.0
 */
public class RemoteImageView extends ImageView implements View.OnClickListener {
	private static final String TAG = RemoteImageView.class.getName();
	Bitmap bitmapSrc = null;
	protected ExecutorService executorService = Executors.newFixedThreadPool(5);

	public String getUrl() {
		return url;
	}

	/**
	 * @category 是否开启圆角！
	 */
	boolean isOpenRect = true;

	public void Force_Close() {
		try {
			if (bitmapSrc != null) {
				bitmapSrc = null;
				NSLoger.Log("--释放远程图片占用资源--");
				if (executorService != null) {
					executorService.shutdown();
					NSLoger.Log("--释放远程图片占用线程--");
				}
				System.gc();
			}
		} catch (Exception e) {
		}

	}

	public boolean isOpenRect() {
		return isOpenRect;
	}

	public void setOpenRect(boolean isOpenRect) {
		this.isOpenRect = isOpenRect;
	}

	// 图片数据（通过加载而得）
	private byte[] data;
	// 要加载的图片URL
	private String url;
	// 标识当前图片是否正在加载过程中
	private boolean loading;

	private boolean mymutate = false;

	private int zoomWidth;
	public boolean isRect = false;
	public int rectSize = 0;
	public boolean isScale = false; // 是否重新设置layoutparams
	public int scaleSize = 0;
	public boolean isFixed = false;// 是否保持一个固定值，是的话:<固定值-拉;>固定值-压;
	public int fixedSize = 0;

	public void setRectSize(int rectSize) {
		this.rectSize = rectSize;
	}

	private boolean ableOnClick = false;

	public RemoteImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (attrs != null) {
			url = attrs.getAttributeValue(null, "url");
			isScale = attrs.getAttributeBooleanValue(null, "isScale", false);
			scaleSize = attrs.getAttributeIntValue(null, "scaleSize", 0);
			isFixed = attrs.getAttributeBooleanValue(null, "isFixed", false);
			fixedSize = attrs.getAttributeIntValue(null, "fixedSize", 0);
		}
		if (url == null || url.equals("")) {
			return;
		}
		loadImageData(url, false);
	}

	public RemoteImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RemoteImageView(Context context) {
		super(context);
	}

	/**
	 * 异步加载图片
	 * 
	 * @param url
	 *            加载的图片URL
	 */
	private void loadImageData(final String url, boolean reload) {
		loading = true;
		if (!reload && data != null) {
			loading = false;
			return;
		}
		if (ableOnClick) {
			setOnClickListener(this);
		}
		executorService.submit(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
				postInvalidate();
				data = CachePoolManager.loadCacheOrDownload(url);
				loading = false;
				postInvalidate();
				if (null != data) {
					int time = 0;
					do {
						try {

							bitmapSrc = BitmapFactory.decodeByteArray(data, 0,
									data.length);
							break;
						} catch (OutOfMemoryError e) {
							System.gc();
							time++;
							BitmapFactory.Options opts = new BitmapFactory.Options();
							opts.inJustDecodeBounds = true;
							byte[] bytes = data;
							BitmapFactory.decodeByteArray(bytes, 0,
									bytes.length, opts);
							opts.inSampleSize = computeSampleSize(opts, -1,
									480 * 800) * time;

							// end
							opts.inJustDecodeBounds = false;

							bitmapSrc = BitmapFactory.decodeByteArray(bytes, 0,
									bytes.length, opts);

						}

					} while (time < 4);
					// Message message = handler.obtainMessage(0, data);

					// 这3句是处理图片溢出的begin( 如果不需要处理溢出直接 opts.inSampleSize=1;)
					/**
					 * BitmapFactory.Options opts = new BitmapFactory.Options();
					 * opts.inJustDecodeBounds = true; byte[] bytes = data;
					 * BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
					 * opts); opts.inSampleSize = computeSampleSize(opts, -1,
					 * 480 * 800); // end opts.inJustDecodeBounds = false;
					 * 
					 * bitmapSrc = BitmapFactory.decodeByteArray(bytes, 0,
					 * bytes.length, opts);
					 */

					handler.obtainMessage(0).sendToTarget();
					// handler.sendMessage(message);
				} else {
					handler.obtainMessage(110).sendToTarget();
				}
			}
		});
		// new Thread() {
		// @Override
		// public void run() {
		// try {
		// Thread.sleep(200);
		// } catch (InterruptedException e) {
		// }
		// postInvalidate();
		// data = CachePoolManager.loadCacheOrDownload(url);
		// loading = false;
		// postInvalidate();
		// if (null != data) {
		//
		// Message message = handler.obtainMessage(0, data);
		// handler.sendMessage(message);
		// } else {
		// handler.obtainMessage(110).sendToTarget();
		// }
		// }
		// }.start();
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
		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case 0:

					// BitmapFactory.decodeStream(is);
					// if (null != bitmapSrc) {
					//
					// setImageBitmap(bitmapSrc);
					// }
					newsetBit();
					break;
				case 110:
					Log.d("--读取图片为空！－－》》》》", "-读取图片为空-chengshiyang>>>");
					break;
				default:
					break;
				}

			} catch (Exception e) {
				// Toast.makeText(myContext, "图片加载异常",
				// Toast.LENGTH_SHORT).show();
			}
		}
	};

	void newsetBit() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				if (null != bitmapSrc) {
					setImageBitmap(bitmapSrc);
				}

			}
		});
		// if (null != bitmapSrc) {
		// setImageBitmap(bitmapSrc);
		// }
	}

	// public void setImageResource(int resId) {
	// BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(resId);
	// setImageBitmap(bd.getBitmap());
	// }

	public void setImageBitmap(Bitmap bitmapSrc) {
		// 是否拉伸或压缩
		if (isScale) {
			int width = bitmapSrc.getWidth();
			int height = bitmapSrc.getHeight();
			//
			DisplayMetrics dm = getResources().getDisplayMetrics();
			int w = (int) (dm.widthPixels - (scaleSize * dm.density + 0.5f));
			// 计算缩放率，新尺寸除原始尺寸
			float scaleWidth = ((float) w) / width;
			float scaleHeight = scaleWidth;

			if (isFixed) {
				scaleHeight = (284 * ((float) w) / 564) / height;
			}

			// 创建操作图片用的matrix对象
			Matrix matrix = new Matrix();
			// 缩放图片动作
			matrix.postScale(scaleWidth, scaleHeight);
			// setImageMatrix(matrix);
			//
			try {

				bitmapSrc = Bitmap.createBitmap(bitmapSrc, 0, 0, width, height,
						matrix, true);
			} catch (OutOfMemoryError e) {
				while (bitmapSrc == null) {
					System.gc();
					System.runFinalization();
					bitmapSrc = Bitmap.createBitmap(bitmapSrc, 0, 0, width,
							height, matrix, true);
				}
				// TODO: handle exception
			}
		}

		try {
			if (isRect && isOpenRect == true) {
				bitmapSrc = toRoundCorner(bitmapSrc, rectSize);
			}

			if (null == bitmapSrc) {
				setImageResource(R.drawable.female_default);
			} else {

				super.setImageBitmap(bitmapSrc);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		//
		// 是否有圆角

	}

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
		Bitmap output = null;
		try {
			output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
					Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			final float roundPx = pixels;

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);
		} catch (OutOfMemoryError e) {
			System.gc();
			// System.runFinalization();
			return null;
		}

		return output;
	}

	/**
	 * 设置新的图片
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		if (url == null || url.equals("")) {
			// Log.d(TAG, "the remote url can not be null");
			return;
		}
		if (url.equals(this.url)) {
			// Log.d(TAG, "the same url, dispatch the image");
			loadImageData(this.url, false);
			return;
		}
		this.url = url;
		// Log.d(TAG, "set remote image url : " + url);
		loadImageData(url, true);
	}

	/*
	 * @Override public void draw(Canvas canvas) { if(Utility.isEmpty(url)) {
	 * super.draw(canvas); return; } try { Field mResourceField =
	 * ImageView.class.getDeclaredField("mResource");
	 * mResourceField.setAccessible(true); Integer resource =
	 * (Integer)mResourceField.get(this); if(resource != null && resource != 0)
	 * { super.draw(canvas); return; } //检测setImageDrawable mResourceField =
	 * ImageView.class.getDeclaredField("mDrawable");
	 * mResourceField.setAccessible(true); Drawable draw =
	 * (Drawable)mResourceField.get(this); if(draw != null) {
	 * super.draw(canvas); return; } }catch (Exception e) { Log.d(TAG,
	 * e.getMessage()); } __draw(canvas); }
	 */

	/**
	 * 设置图片是否为灰色(默认不为灰色)
	 * 
	 * @param mymutate
	 *            public void setMymutate(boolean mymutate) { this.mymutate =
	 *            mymutate; }
	 */

	public void setZoomWidth(int width) {
		this.zoomWidth = width;
	}

	/**
	 * 设置是否点击放大
	 * 
	 * @param ableOnClick
	 */
	public void setAbleOnClick(boolean ableOnClick) {
		this.ableOnClick = ableOnClick;
	}

	/**
	 * 设置圆角
	 * 
	 * @param isRect
	 */
	public void setRect(boolean isRect) {
		this.isRect = isRect;
	}

	@SuppressWarnings("unused")
	private void __draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		if (loading) {
			canvas.drawText("Loading ...", 0, 20, paint);
			return;
		}

		if (data == null || data.length == 0) {
			// Log.d(TAG, "data is null");
			canvas.drawText("data is null", 0, 20, paint);
			return;
		}
		try {
			// Log.i("liujiang", "@->" + data.length);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;
			Bitmap bitmapSrc = BitmapFactory.decodeByteArray(data, 0,
					data.length, options);
			// Bitmap bitmapSrc = BitmapFactory.decodeByteArray(data, 0,
			// data.length);

			// setImageBitmap(bitmapSrc);
			// canvas.drawColor(Color.BLACK);
			// *
			Paint cp = new Paint();
			if (mymutate) {
				cp.setAlpha(125);
			}

			int zoomHeight = getHeight();
			if (zoomWidth == 0) {

				zoomHeight = (bitmapSrc.getHeight() * getWidth() * 2)
						/ bitmapSrc.getWidth() * 2;
				canvas.drawBitmap(bitmapSrc, null, new Rect(0, 0, getWidth(),
						zoomHeight), cp);

				// canvas.drawBitmap(bitmap, null, new Rect(0, 0, getWidth(),
				// getHeight()), cp);
			} else {
				zoomHeight = (bitmapSrc.getHeight() * zoomWidth)
						/ bitmapSrc.getWidth();// 调整高度
				canvas.drawBitmap(bitmapSrc, null, new Rect(0, 0, zoomWidth,
						zoomHeight), cp);
			}

			// 绘制图片边框：
			Rect rec;
			if (zoomHeight > getHeight()) {
				rec = canvas.getClipBounds();

			} else {
				rec = new Rect(0, 0, getWidth(), zoomHeight);
			}
			rec.bottom--;
			rec.right--;
			Paint p = new Paint();
			p.setColor(Color.GRAY);
			p.setStyle(Paint.Style.STROKE);
			canvas.drawRect(rec, p);

			// */

		} catch (Exception e) {
			canvas.drawText("E:" + e.getMessage(), 0, 20, paint);
		}
		Log.d(TAG, "draw completed, data len : " + data.length);
	}

	/*
	 * @Override public boolean onTouch(View v, MotionEvent event) {
	 * 
	 * return true; }
	 */
	PopupWindow pw;

	// ---------load-----img diag----s

	@Override
	public void destroyDrawingCache() {

		super.destroyDrawingCache();
		// if (executorService != null) {
		// if (!executorService.isShutdown()) {
		// try {
		// executorService.shutdown();
		// NSLoger.Log("--关闭远程图片下载----》》》。");
		// if (handler!=null) {
		// handler=null;
		// }
		// } catch (Exception e) {
		// }
		//
		//
		//
		// }
		//
		// }
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getContext(), ImgMagnifyActivity.class);
		// 传递URL
		intent.putExtra("url", url);
		getContext().startActivity(intent);

		/*
		 * Object obj = getContext().getSystemService(Context.WINDOW_SERVICE);
		 * WindowManager wm = (WindowManager) obj; int envWidth =
		 * wm.getDefaultDisplay().getWidth(); int envHeight =
		 * wm.getDefaultDisplay().getHeight(); WindowManager.LayoutParams params
		 * = cd.getWindow().getAttributes(); params.x = 0; params.y = 0;
		 * params.width = envWidth; params.height = envHeight ;
		 * cd.getWindow().setAttributes(params);
		 */

	}
	// if (bitmapSrc.isRecycled() == false) {
	//
	// bitmapSrc.recycle();
	// Log.d("--释放图片占用内存－－》》》》", "--释放图片占用内存－chengshiyang>>>");
	// }

	// @SuppressWarnings("unused")
	// private void myPopupWindow(View v) {
	// LayoutInflater factory = LayoutInflater.from(getContext());
	// Bitmap bitmapSrc = BitmapFactory.decodeByteArray(data, 0, data.length);
	// ImageView i = new ImageView(getContext());
	// i.setImageBitmap(bitmapSrc);
	// if (pw == null) {
	// pw = new PopupWindow(i);
	// }
	// pw.setHeight(bitmapSrc.getHeight());
	// pw.setWidth(bitmapSrc.getWidth());
	// pw.setFocusable(true);
	// pw.showAtLocation(v, Gravity.CENTER, 0, 0);
	// i.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// pw.dismiss();
	// }
	// });
	// }

	/**
	 * 处理图片的路径。（去掉width）
	 * 
	 * @param url
	 * @return
	 */
	// private String utilPath(String url) {
	// String str = url;
	// Log.d(this.getClass().getSimpleName(), "utilPath1 ->" + url);
	// if (url.indexOf("&width=") != -1) {
	// str = url.substring(0, url.indexOf("&width="));
	// Object obj_ = getContext().getSystemService(Context.WINDOW_SERVICE);
	// WindowManager wm = (WindowManager) obj_;
	// int envWidth = wm.getDefaultDisplay().getWidth();
	// str += "&width=" + envWidth;
	// Log.d(this.getClass().getSimpleName(), "utilPath2 ->" + str);
	// }
	// return str;
	// }
}
