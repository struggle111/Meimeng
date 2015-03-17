package com.swater.meimeng.mutils.remoteview;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.util.ExceptionUtils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 
 * @author chengshiyang
 */
public class CircleImgView extends ImageView {
	protected ExecutorService executorService = Executors.newFixedThreadPool(5);

	public CircleImgView(Context context) {
		super(context);
	}

	public CircleImgView(Context context, AttributeSet attrs, int def) {
		super(context, attrs, def);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		ParselUrl();
	}

	public CircleImgView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	String url = "";
	private byte[] data;
	Bitmap btmap = null;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 100:
				if (btmap != null) {
					try {
						setImageBitmap(btmap);

					} catch (OutOfMemoryError e) {
						while (btmap == null) {
							System.gc();
							System.runFinalization();
							setImageBitmap(getRoundedCornerBitmap(bitsrc));
						}
					}
				}
				break;
			case 10:
				break;
			default:
				break;
			}
		};
	};
	Bitmap bitsrc = null;

	void ParselUrl() {
		executorService.submit(new Runnable() {
			
			@Override
			public void run() {
				try {
					if (!TextUtils.isEmpty(url) && url.indexOf("http:") > -1) {
						data = CachePoolManager.loadCacheOrDownload(url);
						bitsrc = BitmapFactory.decodeByteArray(data, 0,
								data.length);
						
						
						btmap = getRoundedCornerBitmap(bitsrc);
						handler.obtainMessage(100).sendToTarget();
					}

				} catch (Exception e) {
					handler.obtainMessage(10).sendToTarget();
					// TODO: handle exception
				}
			}
		});
		
	}

	private Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		// TODO Auto-generated method stub
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = bitmap.getWidth() / 2;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

}
