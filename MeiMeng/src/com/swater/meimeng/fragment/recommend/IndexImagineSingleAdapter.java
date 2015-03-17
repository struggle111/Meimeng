package com.swater.meimeng.fragment.recommend;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.vo.UserSearchVo;
import com.swater.meimeng.activity.adapterGeneral.vo.VoPhoto;
import com.swater.meimeng.activity.newtabMain.IndexImageDownloadTask;
import com.swater.meimeng.commbase.MeiMengApp;
import com.swater.meimeng.mutils.remoteview.ImgMagnifyActivity;

@Deprecated
public class IndexImagineSingleAdapter extends PagerAdapter {
	private static IndexImagineSingleAdapter instance = new IndexImagineSingleAdapter();
	protected ImageLoader imageLoader = null;
	DisplayImageOptions options = null;
	List<?> data = null;
	int re_time = 0;
	HashMap<String, View> view_keys = null;
	static Context con = null;
	private Map<String, SoftReference<Bitmap>> maps = null;
	int sex = 2;

	class TapClick implements OnClickListener {
		int index = 0;

		public TapClick(int pos) {
			this.index = pos;
		}

		@Override
		public void onClick(View v) {
			Intent in = new Intent();
			if (data.get(0) instanceof UserSearchVo) {

				UserSearchVo usv = (UserSearchVo) data.get(index);
				// in.setClass(t_context, BeautyDetail.class);
				in.setClass(con, BarPushActivity.class);
				in.putExtra("data", usv);
				con.startActivity(in);
			}

		}

	}

	class TapClickImagnify implements OnClickListener {
		int index_p = 0;
		final Intent in = new Intent();

		public TapClickImagnify(int pos) {
			this.index_p = pos;
			// in.setClass(t_context, ImgMagnifyActivity.class);
			// if (this.index_p < photos.size()) {
			//
			// in.putExtra("url", photos.get(i).getUrl());
			//
			// }
		}

		@Override
		public void onClick(View v) {
			if (data.get(0) instanceof VoPhoto) {

				VoPhoto usv = (VoPhoto) data.get(index_p);
				Intent in = new Intent();
				in.setClass(con, ImgMagnifyActivity.class);
				in.putExtra("url", usv.getUrl());
				con.startActivity(in);
			}
		}

	}

	public HashMap<String, View> getView_keys() {
		return view_keys;
	}

	public void clearViews() {
		this.view_keys.clear();
	}

	public List<?> getData() {
		return data;
	}

	public void setData(List<?> data) {
		this.data = data;
	}

	void iniImgloader() {
		if (imageLoader == null) {

			imageLoader = ImageLoader.getInstance();
		}
		if (view_keys == null) {
			view_keys = new HashMap<String, View>();
		}
		if (data == null) {
			data = new ArrayList<Object>();
		}
		if (options == null) {

			options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.default_head)
					.showImageOnFail(R.drawable.default_head)
					.resetViewBeforeLoading().cacheOnDisc()
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.displayer(new FadeInBitmapDisplayer(300)).build();

		}
	}

	private IndexImagineSingleAdapter() {
		iniImgloader();

	}

	private static LayoutInflater inflater;
	static MeiMengApp app = null;

	public static IndexImagineSingleAdapter getInstance(Context context,
			LayoutInflater minflater) {
		inflater = LayoutInflater.from(context);
		con = context;
		if (con instanceof Activity) {
			Activity act = (Activity) con;
			app = (MeiMengApp) act.getApplication();

		}

		return instance;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public void finishUpdate(View container) {
	}

	@Override
	public int getCount() {
		// view_keys.clear();

		return data == null ? 0 : data.size();
	}

	@SuppressWarnings("unused")
	@Override
	public Object instantiateItem(ViewGroup view, final int position) {
		View imageLayout = null;

		Handler cell_hanHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 1) {

				} else {
					Toast.makeText(con, "re-use-", 2).show();
				}
			}
		};
		if (view_keys.containsKey(position + "")) {
			imageLayout = view_keys.get(position + "");
			cell_hanHandler.obtainMessage(2).sendToTarget();
		} else {

			if (data.get(0) instanceof VoPhoto) {
				imageLayout = inflater.inflate(R.layout.beauty_cell, view,
						false);
				imageLayout.setOnClickListener(new TapClickImagnify(position));
			} else if (data.get(0) instanceof UserSearchVo) {
				imageLayout = inflater.inflate(R.layout.recommend_cell, view,
						false);
				imageLayout.setOnClickListener(new TapClick(position));

			}

			view_keys.put(position + "", imageLayout);
			cell_hanHandler.obtainMessage(1).sendToTarget();
		}

		final ImageView imageView = (ImageView) imageLayout
				.findViewById(R.id.recom_photo);
		final ProgressBar spinner = (ProgressBar) imageLayout
				.findViewById(R.id.loading);
		// imageView.setOnClickListener(new TapClick(position));
		if (maps == null) {
			maps = app.getMapParams_bit();
		}
		Log.d("-----可使用内存--KB--》》-" + Runtime.getRuntime().freeMemory() / 1000
				+ "---KB--", "---kb--");
		Log.d("-----可使用内存-MB---》》-" + Runtime.getRuntime().freeMemory() / 1000
				/ 1000 + "MB", "---kb--");
		String final_url = "";
		if (data.size() == 0 || data == null) {

		} else {
			if (data.get(0) instanceof VoPhoto) {

				final_url = ((VoPhoto) data.get(position)).getUrl();
			} else if (data.get(0) instanceof UserSearchVo) {
				final_url = ((UserSearchVo) data.get(position)).getHead_url();
				TextView txt = (TextView) imageLayout
						.findViewById(R.id.recom_content);
				UserSearchVo su = (UserSearchVo) data.get(position);
				txt.setText(su.getContent());

			}

		}
		final String url = final_url;

		final Handler img_handler = new Handler() {

			@Override
			public void handleMessage(Message mg) {
				super.handleMessage(mg);
				switch (mg.what) {

				case 690:
					re_time++;
					if (re_time < 2) {
						try {
							IndexImageDownloadTask task = new IndexImageDownloadTask(
									con);
							task.execute(url, imageView);
							Thread.sleep(100);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					break;

				default:
					break;
				}
			}
		};

		if (maps.containsKey(url) && maps.get(url).get() != null) {
			imageView.setImageBitmap(maps.get(url).get());
			spinner.setVisibility(View.GONE);

		} else {

			imageLoader.displayImage(url, imageView, options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							if (sex == 1) {
								imageView
										.setImageResource(R.drawable.man_default);

							} else {
								imageView
										.setImageResource(R.drawable.female_default);
							}
							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							img_handler.obtainMessage(690).sendToTarget();
							try {
								Thread.sleep(300);

							} catch (Exception e) {
							}
							spinner.setVisibility(View.GONE);
							if (sex == 1) {
								imageView
										.setImageResource(R.drawable.man_default);

							} else {
								imageView
										.setImageResource(R.drawable.female_default);
							}
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							spinner.setVisibility(View.GONE);

							if (loadedImage != null) {
								// showToast("--3--");
								maps.put(url, new SoftReference<Bitmap>(
										loadedImage));
								imageView.setImageBitmap(loadedImage);
							} else {
								// showToast("--2--");
							}
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
							super.onLoadingCancelled(imageUri, view);

						}
					});
		}

		((ViewPager) view).addView(imageLayout, 0);
		return imageLayout;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View container) {
	}

}
