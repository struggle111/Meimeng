package com.swater.meimeng.activity.newtabMain;

import java.util.LinkedList;
import java.util.Set;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import com.swater.meimeng.commbase.MeiMengApp;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.vo.UserSearchVo;
import com.swater.meimeng.activity.user.UserSetHome;
import com.swater.meimeng.activity.user.swipepage.ScrollViewPage;
import com.swater.meimeng.commbase.TabBaseTemplate;
import com.swater.meimeng.database.ShareUtil;
import com.swater.meimeng.fragment.recommend.BarPushActivity;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.constant.GeneralUtil;

public class TabRecommend extends TabBaseTemplate implements
		OnPageChangeListener {

	private ScrollViewPage vp;
	// private NewViewPagerAdapter vpAdapter_new;
	View guide_view = null;
	LinkedList<UserSearchVo> ls_vo = new LinkedList<UserSearchVo>();
	DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_recommend);
		t_context = this;
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.female_default)
				.showImageOnFail(R.drawable.female_default)
				.resetViewBeforeLoading().cacheOnDisc()
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory()
				.displayer(new FadeInBitmapDisplayer(300)).build();
		imageLoader.resume();
		TempRun();
		getRecomndList();
	}

	@Override
	public void iniView() {
		regPush();
		// iniImgloader();
		showTitle("美盟推荐");
		bindMenuClick();
		view_Hide(findViewById(R.id.home_left_btn));
		vp = (ScrollViewPage) findViewById(R.id.viewpager);
		guide_view = findViewById(R.id.mask_img);
		vp.setOnPageChangeListener(this);
		// vpAdapter_new = new NewViewPagerAdapter(views);

	}

	/***
	 * 
	 * 
	 * @category 注册JPUSH 别名
	 */
	void regPush() {
		int uid = ShareUtil.getInstance(TabRecommend.this).getUserid();
		JPushInterface.setAlias(this, uid + "", new TagAliasCallback() {

			@Override
			public void gotResult(int code, String msg, Set<String> arg2) {
				NSLoger.Log("--res---code--" + code + msg);
				if (code == 0) {
					NSLoger.Log("--注册通知别名成功！！！---》");

				} else {
					NSLoger.Log("--注册通知别名失败！！！---》");
				}

			}
		});
	}

	class TapClick implements OnClickListener {
		int index = 0;

		public TapClick(int pos) {
			this.index = pos;
		}

		@Override
		public void onClick(View v) {
			Intent in = new Intent();
			UserSearchVo usv = ls_vo.get(index);
			in.setClass(t_context, BarPushActivity.class);
			// in.setClass(t_context, PushJsonSimpleActivity.class);
			in.putExtra("data", usv);
			t_context.startActivity(in);

		}

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ProDimiss();
			switch (msg.what) {
			case 456:
				view_Hide(guide_view);
				break;
			case ACTION_TAG1: {
				parselJson(vo_resp.getResp());

				/**
				 * ViewPagerAdapter vpad = new ViewPagerAdapter( (MeiMengApp)
				 * TabRecommend.this.getApplication(), TabRecommend.this,
				 * ls_vo);
				 */
				// vp.setAdapter(vpad);
				vp.setAdapter(new ImagePagerAdapter());

				if (shareUserInfo().getisFistEnter_MainPage()) {
					view_Show(guide_view);
				}

			}
				break;
			case Resp_DATA_OK: {
			}

				break;
			case ACTION_FAIL: {
				showToast("服务器响应失败！");
			}

				break;
			case ACTION_EXCEPTON:
			case Resp_DATA_EXCEPTION: {
				showToast("获取数据异常！" + mg2String(msg));
			}

				break;
			case Resp_exception: {
				showToast("服务器异常！" + mg2String(msg));

			}

				break;
			case ACTION_ERROR:
			case Resp_DATA_Empty: {
				showToast("获取数据失败！" + mg2String(msg));
			}

				break;
			case Resp_action_fail: {
				showToast("获取数据异常！" + mg2String(msg));
			}

				break;

			default:
				break;
			}
		};
	};

	@Override
	protected Object parselJson(String res) {
		try {
			JSONArray data_arr = new JSONObject(res).getJSONObject("data")
					.getJSONArray("users");
			for (int i = 0; i < data_arr.length(); i++) {
				UserSearchVo uv = new UserSearchVo();
				JSONObject cell = data_arr.getJSONObject(i);
				uv.setUid(cell.getInt("uid") + "");
				uv.setContent(cell.getString("comment"));
				uv.setHead_url(cell.getString("photo_url"));
				uv.setNickName(cell.getString("nickname"));
				ls_vo.add(uv);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return super.parselJson(res);

	}

	void getRecomndList() {
		ProShow("");
		poolThread.submit(new Runnable() {

			@Override
			public void run() {
				try {
					req_map_param.put("uid", shareUserInfo().getUserid() + "");
					req_map_param.put("key", key_server);
					vo_resp = sendReq(MURL_Recommend, req_map_param);
					if (null == vo_resp) {
						handler.obtainMessage(ACTION_FAIL).sendToTarget();

					}
					if (vo_resp.isHasError()) {
						handler.obtainMessage(ACTION_ERROR).sendToTarget();

					} else {
						handler.obtainMessage(ACTION_TAG1).sendToTarget();

					}

				} catch (Exception e) {
					handler.obtainMessage(ACTION_EXCEPTON).sendToTarget();
				}

			}
		});

	}

	@Override
	public void bindClick() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_img_btn:
		case R.id.home_left_btn: {
			this.LeftClickPop(findViewById(R.id.topNav));
		}

			break;

		default:
			break;
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		if (guide_view.getVisibility() == View.VISIBLE) {
			view_Hide(guide_view);
			shareUserInfo().SetisFistEnter_MainPage();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		// imageLoader.stop();
	}

	@Override
	public void onHeader_ItemClick(View v) {
		judgeTypeMyfocus(v, TabRecommend.this);
		switch (v.getId()) {
		case R.id.gallery_imv:
		case R.id.lay_person: {
            if (MeiMengApp.isVisitor){
                MeiMengApp.setVisitorDialogView(TabRecommend.this,1);
            }else {
                jumpOtherActivity(UserSetHome.class);
            }


		}
		// case R.id.gallery_imv: {
		// jumpOtherActivity(UserGallery.class);
		//
		// }
		// break;

		default:
			break;
		}

	}

	// HashMap<String, View> view_keys = null;
	// private class ImagePagerAdapter extends PagerAdapter {
	//
	// // private String[] images;
	// private LayoutInflater inflater;
	//
	// ImagePagerAdapter(List<VoPhoto> images) {
	// // this.images = images;
	// inflater = getLayoutInflater();
	// view_keys=new HashMap<String, View>();
	// }
	//
	// @Override
	// public void destroyItem(ViewGroup container, int position, Object object)
	// {
	// ((ViewPager) container).removeView((View) object);
	// }
	//
	// @Override
	// public void finishUpdate(View container) {
	// }
	//
	// @Override
	// public int getCount() {
	// return ls_vo.size();
	// }
	//
	// @Override
	// public Object instantiateItem(ViewGroup view, int position) {
	// View imageLayout=null;
	// if (view_keys.containsKey(position)) {
	// imageLayout=view_keys.get(position+"");
	//
	// }else{
	//
	// imageLayout = inflater.inflate(R.layout.recommend_cell, view,
	// false);
	// view_keys.put(position+"", imageLayout);
	// }
	// final ImageView imageView = (ImageView) imageLayout
	// .findViewById(R.id.recom_photo);
	// final TextView txt = (TextView) imageLayout
	// .findViewById(R.id.recom_content);
	// // final ProgressBar spinner = (ProgressBar) imageLayout
	// // .findViewById(R.id.loading);
	// imageView
	// .setImageResource(R.drawable.female_default);
	//
	// imageView.setOnClickListener(new TapClick(position));
	// String content = ls_vo.get(position).getContent();
	// NSLoger.Log("--content--" + content);
	// setValueToView(
	// txt,
	// ls_vo.get(position).getContent() == null
	// || TextUtils.isEmpty(ls_vo.get(position).getContent()
	// .trim()) ? "这个美女很神秘！什么也没留下！" : ls_vo.get(position)
	// .getContent());
	//
	// imageLoader.displayImage(ls_vo.get(position).getHead_url(), imageView,
	// options, new SimpleImageLoadingListener() {
	// @Override
	// public void onLoadingStarted(String imageUri, View view) {
	// imageView
	// .setImageResource(R.drawable.female_default);
	//
	// // spinner.setVisibility(View.VISIBLE);
	// }
	//
	// @Override
	// public void onLoadingFailed(String imageUri, View view,
	// FailReason failReason) {
	//
	// // spinner.setVisibility(View.GONE);
	// // if (userinfo_vo.getSex() == 1) {
	// // imageView
	// // .setImageResource(R.drawable.man_default);
	// //
	// // } else {
	// imageView
	// .setImageResource(R.drawable.female_default);
	// // }
	// }
	//
	// @Override
	// public void onLoadingComplete(String imageUri,
	// View view, Bitmap loadedImage) {
	// // spinner.setVisibility(View.GONE);
	// }
	// });
	//
	// ((ViewPager) view).addView(imageLayout, 0);
	// return imageLayout;
	// }
	//
	// @Override
	// public boolean isViewFromObject(View view, Object object) {
	// return view.equals(object);
	// }
	//
	// @Override
	// public void restoreState(Parcelable state, ClassLoader loader) {
	// }
	//
	// @Override
	// public Parcelable saveState() {
	// return null;
	// }
	//
	// @Override
	// public void startUpdate(View container) {
	// }
	// }
	private class ImagePagerAdapter extends PagerAdapter {

		private LayoutInflater inflater;

		ImagePagerAdapter() {
			inflater = getLayoutInflater();
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
			return ls_vo.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {

			View imageLayout = inflater.inflate(R.layout.recommend_cell, view,
					false);
			final ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.recom_photo);
			TextView txt = (TextView) imageLayout
					.findViewById(R.id.recom_content);
			final ProgressBar spinner = (ProgressBar) imageLayout
					.findViewById(R.id.loading);
			imageLayout.setOnClickListener(new TapClick(position));
			GeneralUtil.setValueToView(txt, ls_vo.get(position).getContent());
			imageLoader.displayImage(ls_vo.get(position).getHead_url(),
					imageView, options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							spinner.setVisibility(View.VISIBLE);
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
							// Toast.makeText(TabRecommend.this,
							// message + imageUri, Toast.LENGTH_SHORT)
							// .show();
							imageView
									.setImageResource(R.drawable.female_default);

							spinner.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							spinner.setVisibility(View.GONE);

							// int targetWidth = headerView.getWidth();
							// int targetHeight = headerView.getHeight();
							//
							// if (targetWidth > 0 && targetHeight > 0) {
							//
							// Bitmap targetBitmap = Bitmap.createBitmap(
							// targetWidth, targetHeight,
							// Bitmap.Config.ARGB_8888);
							//
							// Canvas canvas = new Canvas(targetBitmap);
							//
							// Path path = new Path();
							//
							// path.addCircle(((float) targetWidth - 1) / 2,
							// ((float) targetHeight - 1) / 2,
							// (Math.min(((float) targetWidth),
							// ((float) targetHeight)) / 2),
							// Path.Direction.CCW);
							//
							// canvas.clipPath(path);
							//
							// int lwidth = Math.min(loadedImage.getWidth(),
							// loadedImage.getHeight());
							//
							// canvas.drawBitmap(loadedImage, new Rect(0, 0,
							// lwidth, lwidth), new Rect(0, 0,
							// targetWidth, targetHeight), null);
							//
							// headerView.setImageBitmap(targetBitmap);
							// }
						}
					});

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

}
