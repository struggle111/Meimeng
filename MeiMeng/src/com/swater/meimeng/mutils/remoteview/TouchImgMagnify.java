package com.swater.meimeng.mutils.remoteview;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.meimeng.app.R;
import com.swater.meimeng.mutils.constant.GeneralUtil;

public class TouchImgMagnify extends Activity implements OnClickListener {

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
	ProgressBar pro = null;
	DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imgdiag);
		context = this;
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.default_head)
				.showImageOnFail(R.drawable.default_head)
				.resetViewBeforeLoading().cacheOnDisc()
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory()
				.displayer(new FadeInBitmapDisplayer(100)).build();
		imageLoader.resume();
		iniView();
	}

	public void iniView() {

		GeneralUtil.setValueToView(findViewById(R.id.center_show), "查看原图");
		GeneralUtil.setValueToView(findViewById(R.id.home_left_btn), "返回");
		DisplayMetrics dm = new DisplayMetrics(); // 获取屏幕宽度高度
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		heightScreen = dm.heightPixels;
		widthScreen = dm.widthPixels;
		showimg = (ImageView) findViewById(R.id.imgshow_);
		pro = (ProgressBar) findViewById(R.id.loading);
		GeneralUtil.view_Hide(findViewById(R.id.home_right_btn));
		GeneralUtil.view_Show(findViewById(R.id.home_left_btn));
		bindClick();
		parserUrl();
		if (!TextUtils.isEmpty(url)) {
			loadImg();

		} else {
			Toast.makeText(context, "图片地址错误！", 2).show();
		}

	}

	void loadImg() {

		imageLoader.displayImage(url, showimg, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						pro.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						String message = null;
						switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
						}
						Toast.makeText(TouchImgMagnify.this,
								message + imageUri, Toast.LENGTH_SHORT).show();

						pro.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						pro.setVisibility(View.GONE);
						if (loadedImage != null) {
							bitmapsave = loadedImage;
							binldTouch();
						}
					}
				});
	}

	public void bindClick() {
		// bindNavgationEvent();
		findViewById(R.id.home_left_btn).setOnClickListener(this);

	}

	void parserUrl() {
		if (getIntent() != null) {
			url = getIntent().getStringExtra("url");
		}
	}

	protected void binldTouch() {
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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_left_btn: {
			onBackPressed();
		}

			break;

		default:
			break;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (imageLoader != null) {
			imageLoader.resume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (imageLoader != null) {
			imageLoader.pause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (imageLoader != null) {
			imageLoader.resume();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (imageLoader != null) {
			imageLoader.stop();
		}
	}

}
