package com.swater.meimeng.activity.newtabMain.ompager;

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.Map;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.vo.UserSearchVo;
import com.swater.meimeng.activity.adapterGeneral.vo.VoPhoto;
import com.swater.meimeng.activity.newtabMain.IndexImageDownloadTask;
import com.swater.meimeng.commbase.MeiMengApp;

/**
 * @author frankiewei 相册的ItemView,自定义View.方便复用.
 */
public class DetailViewPagerItemView extends FrameLayout {
	Context con = null;

	/**
	 * 图片的ImageView.
	 */
	private ImageView mAlbumImageView;

	/**
	 * 图片名字的TextView.
	 */
	private TextView mALbumNameTextView;

	/**
	 * 图片的Bitmap.
	 */
	private Bitmap mBitmap;

	/**
	 * 要显示图片的JSONOBject类.
	 */
	// private JSONObject mObject;
	private VoPhoto mObject;

	public DetailViewPagerItemView(Context context) {
		super(context);
		setupViews();
	}

	public DetailViewPagerItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupViews();
	}

	// 初始化View.
	private void setupViews() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.recommend_cell, null);

		mAlbumImageView = (ImageView) view.findViewById(R.id.recom_photo);
		mALbumNameTextView = (TextView) view.findViewById(R.id.recom_content);
		mALbumNameTextView.setVisibility(View.GONE);
		addView(view);
	}

	protected ImageLoader imageLoader = null;
	DisplayImageOptions options;

	void iniImgloader() {
		if (imageLoader == null) {

			imageLoader = ImageLoader.getInstance();
		}

		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.default_head)
				.showImageOnFail(R.drawable.default_head)
				.resetViewBeforeLoading().cacheOnDisc()
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();
	}

	/**
	 * 填充数据，共外部调用.
	 * 
	 * @param object
	 */
	public void setData(VoPhoto ob, Context con) {
		try {
			this.mObject = ob;
			this.con = con;
			// int resId = object.getInt("resid");
			// String name = object.getString("name");

			// IndexImageDownloadTask task = new IndexImageDownloadTask(con);
			try {
				Thread.sleep(100);
				// task.execute(ob.getUrl(), mAlbumImageView);
				// mALbumNameTextView.setText(ob.getContent());
			} catch (Exception e) {
				// TODO: handle exception
			}
			// downs_quences.add(task);
			// 实战中如果图片耗时应该令其一个线程去拉图片异步,不然把UI线程卡死.
			// mAlbumImageView.setImageResource(resId);
			// mALbumNameTextView.setText(ls);
			final ImageView imageView = mAlbumImageView;

			if (imageLoader == null) {
				imageLoader = ImageLoader.getInstance();
			}
			imageLoader.displayImage(ob.getUrl(), imageView, options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							// if (userinfo_vo.getSex() == 1) {
							// imageView
							// .setImageResource(R.drawable.man_default);
							//
							// } else {
							// imageView
							// .setImageResource(R.drawable.female_default);
							// }
							// spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {

							// spinner.setVisibility(View.GONE);
							// if (ob..getSex() == 1) {
							// imageView
							// .setImageResource(R.drawable.man_default);
							// handler.obtainMessage(991).sendToTarget();
							//
							// } else {
							// imageView
							// .setImageResource(R.drawable.female_default);
							// }
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							// spinner.setVisibility(View.GONE);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 这里内存回收.外部调用.
	 */
	public void recycle() {
		// mAlbumImageView.setImageBitmap(null);
		if ((this.mBitmap == null) || (this.mBitmap.isRecycled()))
			return;
		this.mBitmap.recycle();
		this.mBitmap = null;
	}

	Map<String, SoftReference<Bitmap>> maps_bit = null;

	// Map<String, Bitmap> maps_bit=null;
	/**
	 * 重新加载.外部调用.
	 */
	public boolean reload() {
		try {
			// int resId = mObject.getInt("resid");
			// // 实战中如果图片耗时应该令其一个线程去拉图片异步,不然把UI线程卡死.
			// mAlbumImageView.setImageResource(resId);
			if (true) {

				Log.d("-  detaill page--reload--复用缓存！---", "-->>");
				return true;
			}

			if (this.con != null) {
				if (this.con instanceof Activity) {
					MeiMengApp app = (MeiMengApp) ((Activity) this.con)
							.getApplication();
					maps_bit = app.getMapParams_bit();
					if (maps_bit != null) {
						if (maps_bit.containsKey(this.mObject.getUrl())) {
							SoftReference<Bitmap> bp = maps_bit
									.get(this.mObject.getUrl());
							if (bp != null) {
								mAlbumImageView.setImageBitmap(bp.get());
								Log.d("-  detaill page--成功加载系统应用内部缓存！---",
										"-->>");
								return true;
							}

						}
					}
				}
			}

			IndexImageDownloadTask task = new IndexImageDownloadTask(con);
			try {
				Thread.sleep(100);
				task.execute(this.mObject.getUrl(), mAlbumImageView);
				// mALbumNameTextView.setText(this.mObject.getContent());
			} catch (Exception e) {
				// TODO: handle exception
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
