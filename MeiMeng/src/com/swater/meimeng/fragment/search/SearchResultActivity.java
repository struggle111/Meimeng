package com.swater.meimeng.fragment.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.vo.UserSearchVo;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.fragment.recommend.BarPushActivity;
import com.swater.meimeng.mutils.constant.GeneralUtil;
import com.swater.meimeng.mutils.net.RespVo;
import com.swater.meimeng.mutils.pullxlist.PullXListView;
import com.swater.meimeng.mutils.pullxlist.PullXListView.IXListViewListener;

public class SearchResultActivity extends BaseTemplate implements
		IXListViewListener {
	PullXListView ls = null;
	List<UserSearchVo> data_user = new ArrayList<UserSearchVo>();
	List<UserSearchVo> data_container = new ArrayList<UserSearchVo>();
	Boolean hasNextData = true;;
	int pageSize = 8;
	// AdapterSearch adapter = null;
	RespVo vo_search = null;
	int pageIndex = 1;
	ImageLoader imageLoader = ImageLoader.getInstance();

	void getSearch(String key) {
		ProShow("正在获取数据....");
		final String keys = key.trim();

		poolThread.submit(new Runnable() {

			@Override
			public void run() {
				try {
					req_map_param.put("uid", shareUserInfo().getUserid() + "");
					req_map_param.put("key", key_server);

					req_map_param.put("city_id", keys);
					req_map_param.put("page", pageIndex + "");
					vo_search = sendReq(MURL_search_city, req_map_param);

					if (null == vo_search) {
						handler.obtainMessage(ACTION_FAIL).sendToTarget();
					}
					if (vo_search.isHasError()) {
						handler.obtainMessage(ACTION_ERROR,
								vo_search.getErrorDetail()).sendToTarget();

					} else {
						handler.obtainMessage(Resp_action_ok).sendToTarget();
					}

				} catch (Exception e) {
					e.printStackTrace();
					handler.obtainMessage(ACTION_EXCEPTON).sendToTarget();
				}

			}
		});
	}

	protected Object parselJson(String res) {
		data_user.clear();
		try {
			JSONObject ar = new JSONObject(res).getJSONObject("data");
			if (res == null || ar == null) {
				hasNextData = false;
				return null;
			}
			JSONArray arr = new JSONObject(res).getJSONObject("data")
					.getJSONArray("users");
			if (arr.length() < pageSize) {
				hasNextData = false;
			} else {
				hasNextData = true;
			}
			if (arr == null || arr.length() < 1) {
				return null;
			}
			for (int i = 0; i < arr.length(); i++) {

				UserSearchVo usv = new UserSearchVo();
				usv.setUid(arr.getJSONObject(i).getInt("uid") + "");
				// usv.setPhotoUrl(arr.getJSONObject(i).getString("header_url"));
				usv.setPhotoUrl(arr.getJSONObject(i).getString("photo_url"));
				usv.setNickName(arr.getJSONObject(i).getString("nickname"));
				// usv.setHeight(arr.getJSONObject(i).getString("height"));
				usv.setCity(arr.getJSONObject(i).getString("city"));
				usv.setHeart_desc(arr.getJSONObject(i).getString(
						"heart_description"));
				usv.setProvince(arr.getJSONObject(i).getString("province"));
				;
				usv.setOpentome(arr.getJSONObject(i).getInt("opentome") + "");

				usv.setAge(arr.getJSONObject(i).getInt("age") + "");
				usv.setSex(arr.getJSONObject(i).getInt("sex") + "");
				data_user.add(usv);

				// UserSearchVo usv = new UserSearchVo();
				// usv.setUid(arr.getJSONObject(i).getInt("uid") + "");
				// usv.setHead_url(arr.getJSONObject(i).getString("header_url"));
				// usv.setNickName(arr.getJSONObject(i).getString("nickname"));
				// usv.setHeight(arr.getJSONObject(i).getString("height"));
				// usv.setCity(arr.getJSONObject(i).getString("city"));
				// usv.setProvince(arr.getJSONObject(i).getString("province"));
				// ;
				// usv.setOpentome(arr.getJSONObject(i).getInt("opentome") +
				// "");
				//
				// usv.setOpentome(arr.getJSONObject(i).getInt("opentome") +
				// "");
				// usv.setAge(arr.getJSONObject(i).getInt("age") + "");
				// data_user.add(usv);
			}
		} catch (JSONException e) {
			showToast(" 解析数据出错！");
			e.printStackTrace();
		} finally {

		}

		return null;
	};

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ProDimiss();
			switch (msg.what) {
			case ACTION_TAG1: {

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
			case Resp_action_ok: {
				parselJson(vo_search.getResp());
				data_container.addAll(data_user);
				// oomAdpterNew();
				if (null == itadapter) {
					itadapter = new ItemAdapter();
					ls.setAdapter(itadapter);
					ls.setOnItemClickListener(it);
					if (null == ls) {
						return;
					}

				}

				itadapter.notifyDataSetChanged();
				ls.stopRefresh();

				if (!hasNextData) {
					ls.setPullLoadEnable(false);
					ls.setFooterText("数据已加载完啦！");
					ls.setRefreshTime(GeneralUtil.simpleDateFormat
							.format(new Date()));
				} else {
					ls.setPullLoadEnable(true);
				}

			}

				break;

			default:
				break;
			}
		};
	};

	OnItemClickListener it = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			position--;
			if (position < data_container.size()) {

				String uid = data_container.get(position).getUid();
				Intent in = new Intent(t_context, BarPushActivity.class);
				in.putExtra("data", data_container.get(position));
				if (!TextUtils.isEmpty(uid)) {
					t_context.startActivity(in);
				}
			}
		}
	};
	ItemAdapter itadapter = null;

	void oomAdpterNew() {
		if (null == itadapter) {

			itadapter = new ItemAdapter();
			ls.setAdapter(itadapter);
			ls.setOnItemClickListener(it);
		}
		itadapter.notifyDataSetChanged();

	}

	// protected ImageLoader imageLoader = null;
	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(0).showImageOnFail(0)
			.resetViewBeforeLoading().cacheOnDisc()
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new FadeInBitmapDisplayer(300)).build();

	// void iniloadImg() {
	//
	// if (imageLoader == null) {
	//
	// imageLoader = ImageLoader.getInstance();
	// }
	//
	// // options = new DisplayImageOptions.Builder()
	// // .showImageOnLoading(R.drawable.female_default)
	// // .showImageForEmptyUri(R.drawable.ic_empty)
	// // .showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
	// // .cacheOnDisc(true).displayer(new RoundedBitmapDisplayer(0))
	// // .build();
	// options = new DisplayImageOptions.Builder()
	// .showImageForEmptyUri(R.drawable.female_default)
	// .showImageOnFail(R.drawable.female_default)
	// .resetViewBeforeLoading(true).cacheOnDisc(true)
	// .imageScaleType(ImageScaleType.EXACTLY)
	// .bitmapConfig(Bitmap.Config.RGB_565)
	// .displayer(new FadeInBitmapDisplayer(300)).build();
	// }
	class ItemAdapter extends BaseAdapter {

		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		private class ViewHolder {
			public TextView text;
			public ImageView image;
			public TextView user_name, user_age, user_pos, user_heart_desc;
		}

		@Override
		public int getCount() {
			return data_container.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.vip_cell_autoline,
						parent, false);
				holder = new ViewHolder();
				// holder.text = (TextView) view.findViewById(R.id.text);
				holder.image = (ImageView) view.findViewById(R.id.cell_header);
				holder.user_name = (TextView) view
						.findViewById(R.id.cell_username);
				holder.user_age = (TextView) view
						.findViewById(R.id.cell_userage);
				holder.user_pos = (TextView) view
						.findViewById(R.id.cell_user_cityand_province);
				holder.user_heart_desc = (TextView) view
						.findViewById(R.id.cell_desc);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			UserSearchVo usv = data_container.get(position);

			String pos_Name = usv.getProvince() + usv.getCity();

			if (TextUtils.isEmpty(pos_Name) || "null".equals(pos_Name)) {
				GeneralUtil.setValueToView(holder.user_pos, "四川成都");
			} else {

				GeneralUtil.setValueToView(holder.user_pos, pos_Name);
			}
			if (TextUtils.isEmpty(usv.getHeart_desc())
					|| "null".equals(usv.getHeart_desc())) {
				GeneralUtil
						.setValueToView(holder.user_heart_desc, "我在美盟，你在哪儿？");
			} else {

				GeneralUtil.setValueToView(holder.user_heart_desc,
						usv.getHeart_desc());
			}
			if (TextUtils.isEmpty(usv.getNickName())
					|| "null".equals(usv.getNickName())) {
				GeneralUtil.setValueToView(holder.user_name, "小西");
			} else {
				if (usv.getNickName().length() > 6) {
					holder.user_name.setTextSize(15);
				}

				GeneralUtil.setValueToView(holder.user_name, usv.getNickName());
			}
			GeneralUtil.setValueToView(holder.user_age, usv.getAge() + "岁");
			if (usv.getSex().trim().equals("2")) {
				holder.image.setBackgroundResource(R.drawable.female_default);

			} else {
				holder.image.setBackgroundResource(R.drawable.man_default);
			}

			imageLoader.displayImage(usv.getPhotoUrl(), holder.image, options,
					animateFirstListener);

			return view;
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
		// TODO Auto-generated method stub
		super.onPause();
		// showToast("onPause");
		if (imageLoader != null) {
			imageLoader.pause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// showToast("onResume");
		if (imageLoader != null) {
			imageLoader.resume();
		}
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingStarted(String imageUri, View view) {
			if (view instanceof ImageView) {
				((ImageView) view).setImageBitmap(null);

			}

			super.onLoadingStarted(imageUri, view);

		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);
		TempRun();
	}

	@Override
	public void iniView() {
		ls = (PullXListView) findViewById(R.id.ls_search_res);
		ls.setXListViewListener(SearchResultActivity.this);
		ls.setPullRefreshEnable(false);
		showTitle("搜索结果");
		showNavgationLeftBar("返回");
		view_Hide(findButton(R.id.home_right_btn));
		// iniloadImg();
		getParams();
	}

	/** 搜索城市 */
	void getParams() {
		String name = this.getIntent().getExtras().getString("city");
		String cityid = this.getIntent().getExtras().getString("cityid");

		// showToast("aa" + name + cityid);
		if (!TextUtils.isEmpty(cityid)) {
			getSearch(cityid);
		}
	}

	@Override
	public void bindClick() {
		bindNavgationEvent();

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
		closePool();
		if (handler != null) {
			handler = null;
			// handler.removeCallbacks(null);
		}
		if (imageLoader != null) {
			imageLoader.stop();
		}
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

		if (hasNextData) {
			pageIndex++;
			hasNextData = false;
			getParams();
		}

	}

}
