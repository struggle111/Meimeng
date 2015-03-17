package com.swater.meimeng.activity.user.myocus;

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
import com.swater.meimeng.activity.adapterGeneral.AdapterSearch;
import com.swater.meimeng.activity.adapterGeneral.vo.UserSearchVo;
import com.swater.meimeng.activity.oomimg.ImageCache;
import com.swater.meimeng.activity.oomimg.ImageLoaderAdapter;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.fragment.recommend.BarPushActivity;
import com.swater.meimeng.mutils.constant.GeneralUtil;
import com.swater.meimeng.mutils.constant.MConstantUser;
import com.swater.meimeng.mutils.pullxlist.PullXListView.IXListViewListener;

public class MyFocus extends BaseTemplate implements IXListViewListener {
	com.swater.meimeng.mutils.pullxlist.PullXListView lsview = null;
	int type = 0;// Type_FOCUS.focus_type_follow_me;
	List<UserSearchVo> data_user = new ArrayList<UserSearchVo>();
	List<UserSearchVo> data_container = new ArrayList<UserSearchVo>();
	Boolean hasNextData = true;;
	int pageSize = 8;
	AdapterSearch adapter = null;
	TextView txt_no_data = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_focus);
		TempRun();
	}

	@Override
	public void iniView() {
		iniloadImg();
		lsview = (com.swater.meimeng.mutils.pullxlist.PullXListView) findViewById(R.id.ls_focus);
		txt_no_data = findText(R.id.no_data);
		lsview.setXListViewListener(this);
		type = getIntent().getIntExtra("type", 0);
		String title = "";
		// 类型;1-我关注的;2-我想见的;3-想见我的;4-两情相悦;
		switch (type) {
		case focus_type_follow_me: {
			type_value = "1";
			title = "我的关注";
			setValueToView(txt_no_data, "您未添加关注的人哦！");

		}

			break;
		case focus_type_iwant2see: {
			type_value = "2";

			title = "我想见的";
			setValueToView(txt_no_data, "您未添加我想见的人哦！");
		}

			break;
		case focus_type_happ_each: {
			type_value = "4";
			title = "两情相悦";

		}

			break;
		case focus_type_want2see_me: {

			type_value = "3";
			title = "想见我的";
		}

			break;

		default:
			break;
		}
		showTitle(title);
		view_Hide(findButton(R.id.home_right_btn));
		showNavgationLeftBar("返回");
		getMyFocus();

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
		case R.id.home_right_btn: {
		}

			break;

		default:
			break;
		}

	}

	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			ProDimiss();
			switch (msg.what) {
			case Resp_action_ok: {
				parselJson(vo_resp.getResp());
				data_container.addAll(data_user);
				// oomAdapter();
				oomAdpterNew();
				// if (null == adapter) {
				// adapter = new AdapterSearch(t_context);
				// adapter.setType_search(AdapterSearch_Type.type_focus);
				// adapter.setAct(MyFocus.this);
				// adapter.setLs_view(lsview);
				// adapter.setData(data_container);
				// lsview.setAdapter(adapter);
				// lsview.setOnItemClickListener(it);
				//
				// }
				//
				// adapter.notifyDataSetChanged();
				;

				if (!hasNextData) {
					lsview.setPullLoadEnable(false);
					lsview.setPullRefreshEnable(true);
					// lsview.stopLoadMoreWithState(LoadState.STATE_OVER);
					// lsview.setPullLoadEnable(false, LoadState.STATE_OVER);
				} else {
					lsview.setPullLoadEnable(true);
					lsview.setPullRefreshEnable(true);
					// lsview.stopLoadMoreWithState(LoadState.STATE_NORMAL);
					// lsview.setPullLoadEnable(true, LoadState.STATE_OVER);
				}
				lsview.setRefreshTime(GeneralUtil.simpleDateFormat
						.format(new Date()));
				lsview.stopRefresh();
				lsview.stopLoadMore();
			}

				break;
			case Resp_action_fail: {
				showToast("获取数据失败！" + mg2String(msg));

			}

				break;
			case Resp_exception: {
				showToast("服务器异常！");

			}

				break;
			case -8: {
				showToast("连接后台服务失败！！");

			}

				break;

			default:
				break;
			}
		};
	};
	ItemAdapter itadapter = null;

	void oomAdpterNew() {
		if (null == itadapter) {

			itadapter = new ItemAdapter();
			lsview.setAdapter(itadapter);
			lsview.setOnItemClickListener(it);
		}
		itadapter.notifyDataSetChanged();

	}

	OnItemClickListener it = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			position = position - 1;
			if (position < data_container.size()) {

				String uid = data_container.get(position).getUid();
				String uName = data_container.get(position).getNickName();
				Intent in = new Intent(t_context, BarPushActivity.class);
				in.putExtra("data", data_container.get(position));
				if (!TextUtils.isEmpty(uid)) {
					t_context.startActivity(in);
				}
			}

		}
	};

	private ImageCache mCache;
	ImageLoaderAdapter adapter_oom = null;


	int pageIndex = 1;
	String type_value = "";

	private void getMyFocus() {
		ProShow("");
		try {
			poolThread.submit(new Runnable() {

				@Override
				public void run() {

					req_map_param.put(MConstantUser.UserProperty.uid,
							shareUserInfo().getUserid() + "");
					req_map_param.put(MConstantUser.UserProperty.user_key,
							key_server);
					req_map_param.put("page", pageIndex + "");
					req_map_param.put("type", type_value);

					vo_resp = sendReq(MURL_MyFocus_List, req_map_param);
					if (null == vo_resp) {
						handler.obtainMessage(-8).sendToTarget();
					}
					if (vo_resp.isHasError()) {
						handler.obtainMessage(Resp_action_fail).sendToTarget();
					} else {

						handler.obtainMessage(Resp_action_ok).sendToTarget();
					}
				}
			});

		} catch (Exception e) {
			handler.obtainMessage(Resp_exception).sendToTarget();
		}

	}

	void NoticeNo() {
		if (txt_no_data != null) {
			view_Show(txt_no_data);
			if (lsview != null) {
				view_Hide(lsview);
			}
		}
	}

	protected Object parselJson(String res) {
		data_user.clear();
		try {
			JSONObject ar = new JSONObject(res).getJSONObject("data");
			if (res == null || ar == null || ar.length() < 1) {
				hasNextData = false;
				NoticeNo();
				return null;
			}
			JSONArray arr = new JSONObject(res).getJSONObject("data")
					.getJSONArray("users");

			if (arr == null || arr.length() < 1) {
				NoticeNo();
				hasNextData = false;
				return null;
			}
			int ar_length = arr.length();
			// NSLoger.Log(ar_length + "");
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

				usv.setPhotoUrl(arr.getJSONObject(i).getString("photo_url"));
				usv.setNickName(arr.getJSONObject(i).getString("nickname"));
				// usv.setHeight(arr.getJSONObject(i).getString("height"));
				usv.setCity(arr.getJSONObject(i).getString("city"));
				usv.setHeart_desc(arr.getJSONObject(i).getString(
						"heart_description"));
				usv.setUid(arr.getJSONObject(i).getInt("uid") + "");
				// usv.setHead_url(arr.getJSONObject(i).getString("header_url"));
				usv.setNickName(arr.getJSONObject(i).getString("nickname"));
				// usv.setHeight(arr.getJSONObject(i).getString("height"));
				usv.setCity(arr.getJSONObject(i).getString("city"));
				usv.setProvince(arr.getJSONObject(i).getString("province"));
				;
				usv.setOpentome(arr.getJSONObject(i).getInt("opentome") + "");

				usv.setOpentome(arr.getJSONObject(i).getInt("opentome") + "");
				usv.setAge(arr.getJSONObject(i).getInt("age") + "");
				usv.setSex(arr.getJSONObject(i).getInt("sex") + "");
				data_user.add(usv);
			}
		} catch (JSONException e) {
			showToast(" 解析数据出错！");
			e.printStackTrace();
		} finally {

		}

		return null;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onRefresh() {
		pageIndex = 1;
		// ClearFresh();
		data_container.clear();
		// getMyFocus();
		TempRun();
	}

	@Override
	public void onLoadMore() {
		if (hasNextData) {

			pageIndex++;
			hasNextData = false;
			getMyFocus();
		}

	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (handler != null) {
			handler = null;
		}
		closePool();
		if (imageLoader != null) {
			imageLoader.stop();
		}

	}

	protected ImageLoader imageLoader = null;
	DisplayImageOptions options;

	void iniloadImg() {

		if (imageLoader == null) {

			imageLoader = ImageLoader.getInstance();
		}
		imageLoader.resume();

		// options = new DisplayImageOptions.Builder()
		// .showImageOnLoading(R.drawable.female_default)
		// .showImageForEmptyUri(R.drawable.ic_empty)
		// .showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
		// .cacheOnDisc(true).displayer(new RoundedBitmapDisplayer(0))
		// .build();
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(0)
				.showImageOnFail(0)
				.resetViewBeforeLoading().cacheOnDisc()
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();
	}

	class ItemAdapter extends BaseAdapter {

		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		private class ViewHolder {
			public TextView text;
			public ImageView image;
			public TextView user_name, user_age, user_height, user_pos,
					user_heart_desc;
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
				view = getLayoutInflater().inflate(R.layout.vip_cell_autoline, parent,
						false);
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
				GeneralUtil.setValueToView(holder.user_heart_desc,
						"我在美盟，你在哪儿？");
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
//				holder.image.setImageResource(R.drawable.female_default);
				holder.image.setBackgroundResource(R.drawable.female_default);

			} else {
				holder.image.setBackgroundResource(R.drawable.man_default);
			}

			imageLoader.displayImage(usv.getPhotoUrl(), holder.image, options,
					animateFirstListener);

			return view;
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
				((ImageView) view).setImageResource(0);

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
