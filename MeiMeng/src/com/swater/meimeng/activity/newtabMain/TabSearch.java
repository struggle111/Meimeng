package com.swater.meimeng.activity.newtabMain;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;
import android.widget.*;
import com.nostra13.universalimageloader.cache.disc.impl.FileCountLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

import com.swater.meimeng.commbase.MeiMengApp;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.AdapterSearch;
import com.swater.meimeng.activity.adapterGeneral.vo.UserSearchVo;
import com.swater.meimeng.activity.newtabMain.downquence.CellMgrThreadPool;
import com.swater.meimeng.activity.newtabMain.downquence.CellTaskMgr;
import com.swater.meimeng.activity.oomimg.ImageCache;
import com.swater.meimeng.activity.oomimg.ImageLoaderAdapter;
import com.swater.meimeng.activity.user.UserSetHome;
import com.swater.meimeng.commbase.TabBaseTemplate;
import com.swater.meimeng.database.Map_Province.CityVo;
import com.swater.meimeng.database.Map_Province.ProvinceVo;
import com.swater.meimeng.database.XmlDataCity;
import com.swater.meimeng.fragment.recommend.BarPushActivity;
import com.swater.meimeng.fragment.search.SearchByCity;
import com.swater.meimeng.mutils.constant.GeneralUtil;
import com.swater.meimeng.mutils.diagutil.Tools;
import com.swater.meimeng.mutils.net.RespVo;
import com.swater.meimeng.mutils.pullxlist.PullXListView.IXListViewListener;
import com.swater.meimeng.mutils.wheelview.ArrayWheelAdapter;
import com.swater.meimeng.mutils.wheelview.OnWheelChangedListener;
import com.swater.meimeng.mutils.wheelview.WheelView;

public class TabSearch extends TabBaseTemplate implements IXListViewListener {

	com.swater.meimeng.mutils.pullxlist.PullXListView ls = null;
	AdapterSearch adapter = null;
	List<UserSearchVo> data_user = new ArrayList<UserSearchVo>();
	ArrayList<UserSearchVo> data_container = new ArrayList<UserSearchVo>();
	Boolean hasNextData = true;;
	int pageSize = 8;
	View lay_sch1, lay_sch2, top_hidden;
	EditText edit_key = null;
	TypeSearch typesearch = TypeSearch.by_city;

	private WheelView country, city;
	List<ProvinceVo> ls_pro = null;
	private static String countries[];
	private String cities[][];
	XmlDataCity reader = null;
	Dialog city_diag = null;
	Button btn_city, btn_id, btn_name;
	View guide_view = null;
	// -------

	CellMgrThreadPool pool_mgr = null;
	CellTaskMgr cell = null;

	void iniQuence() {
		if (pool_mgr == null) {

			pool_mgr = new CellMgrThreadPool();
			new Thread(pool_mgr).start();
		}
		if (cell == null) {

			cell = CellTaskMgr.getInstance();
		}
	}

	public enum TypeSearch {
		by_id, by_nickname, by_city

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_search);
		t_context = this;
		// isini();
		TempRun();
		iniloadImg();
//		iniData();
		judgeVipOrSearch();
	}

	/**
	 * 判断是搜索的界面还是会员的界面
	 *
	 * judge: 0 是搜索
	 *        1 是会员
	 */
	private void judgeVipOrSearch(){
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		int judge = bundle.getInt("vipOrSearch");
		Log.e("TabSearch:","判断是vip还是搜索的值："+judge);

		//搜索
		if (judge == 0){
			changeAllSearch();
		}

		//会员
		else {
			findViewById(R.id.home_right_btn).setVisibility(View.GONE);
			getUserList();
		}
	}
	void isini() {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				t_context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2000))
				.discCache(
						new FileCountLimitedDiscCache(
								StorageUtils.getOwnCacheDirectory(t_context,
										"/loader_max/"), 100))
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public void iniView() {
		bindMenuClick();
		ls = (com.swater.meimeng.mutils.pullxlist.PullXListView) findViewById(R.id.ls_search_res);
		ls.setXListViewListener(TabSearch.this);
		guide_view = findViewById(R.id.mask_img);
		guide_view.setOnTouchListener(touch);
		lay_sch2 = findViewById(R.id.sear_lay_edit);
		lay_sch1 = findViewById(R.id.search_lay);
		top_hidden = findViewById(R.id.la3);
		edit_key = (EditText) findViewById(R.id.edit_key);
		setClickEvent(findViewById(R.id.edit_btn_seach),
				findViewById(R.id.btn_sch_city),
				findViewById(R.id.btn_sch_nickname),
				findViewById(R.id.btn_sch_id));
		btn_city = (Button) findViewById(R.id.btn_sch_city);
		btn_id = (Button) findViewById(R.id.btn_sch_id);
		btn_name = (Button) findViewById(R.id.btn_sch_nickname);
		setValueToView(findViewById(R.id.center_show), "最新会员");

		typesearch = TypeSearch.by_id;
		showNavgationRightBar("搜索");
		bindNavgationEvent();
		view_Hide(lay_sch1, lay_sch2);
		// iniQuence();
	}

	// }
	@Override
	public void bindClick() {
		// bindNavgationEvent();

	}

	View wheelView = null;

	void showCityDiag() {
		wheelView = getCityDialogView();
		if (wheelView == null) {
			// showToast("111");
			return;
		}
		country.setAdapter(new ArrayWheelAdapter<String>(countries));
		country.setCurrentItem(0);
		city.setAdapter(new ArrayWheelAdapter<String>(cities[0]));
		city.setCurrentItem(cities[0].length / 2);
		city_diag = Tools.pull_Dialog(t_context, wheelView);
		if (city_diag == null) {
			// showToast("333");
		}
	}

	OnItemClickListener it = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			position = position - 1;
			if (position < data_container.size()) {
				String uid = data_container.get(position).getUid();
				String uName = data_container.get(position).getNickName();
				// Intent in = new Intent(t_context, BeautyDetail.class);
				Intent in = new Intent(t_context, BarPushActivity.class);
				// Intent in = new Intent(t_context, VipBeautyDetail.class);
				in.putExtra("ta_id", uid);
				in.putExtra("data", data_container.get(position));
				in.putExtra("ta_name", uName);
				if (!TextUtils.isEmpty(uid)) {
					TabSearch.this.startActivity(in);
				}
			}

		}
	};

	void iniCityData() {

		reader = new XmlDataCity();
		try {
			reader.init(t_context);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ls_pro = reader.getAllProvinceData();
		countries = new String[ls_pro.size()];
		cities = new String[ls_pro.size()][];
		;
		for (int i = 0; i < ls_pro.size(); i++) {
			countries[i] = ls_pro.get(i).getPro_name();
			String pvnid = ls_pro.get(i).getPro_id();
			List<CityVo> ls_city = reader.getCitiesByProId(pvnid);
			cities[i] = new String[ls_city.size()];
			for (int j = 0; j < ls_city.size(); j++) {
				cities[i][j] = ls_city.get(j).getCity_name();

			}

		}

	}

	void lease() {
		try {
			if (reader != null) {
				reader.CloseRes();
				reader = null;
				if (ls_pro != null) {
					ls_pro.clear();
					ls_pro = null;
					countries = null;
					cities = null;
				}
			}
			System.gc();
			System.runFinalization();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	Button btn_city_yes = null, btn_city_no = null;

	public View getCityDialogView() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int mScreenWidth = dm.widthPixels; // 获得屏幕的宽
		View viewMain = LayoutInflater.from(t_context).inflate(
				R.layout.wheelview_cities, null);
		viewMain.setMinimumWidth(mScreenWidth);

		btn_city_yes = (Button) viewMain.findViewById(R.id.wh_yes);

		btn_city_no = (Button) viewMain.findViewById(R.id.wh_no);
		btn_city_no.setOnClickListener(this);
		btn_city_yes.setOnClickListener(this);
		country = (WheelView) viewMain.findViewById(R.id.country);
		country.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				city.setAdapter(new ArrayWheelAdapter<String>(cities[newValue]));
				city.setCurrentItem(cities[newValue].length / 2);

			}
		});

		city = (WheelView) viewMain.findViewById(R.id.city);

		return viewMain;
	}

	void CancelDiagCity() {
		if (city_diag != null || city_diag.isShowing()) {
			city_diag.cancel();
		}
	}

	private void changeAllSearch(){

		findViewById(R.id.home_right_btn).setVisibility(View.GONE);

		showTitle("会员搜索");
		view_Hide(guide_view);
		view_Hide(ls, lay_sch2);
		view_Show(lay_sch1, top_hidden, lay_sch2);

		/**
		 * edit_key.setClickable(false);
		 *
		 * edit_key.setHint("");
		 *
		 * typesearch = TypeSearch.by_city;
		 * btn_city.setBackgroundResource(R.drawable.left_p);
		 * btn_id.setBackgroundResource(R.drawable.title_center);
		 * btn_name.setBackgroundResource(R.drawable.title_right);
		 *
		 * btn_city.setTextColor(this.getResources().getColor(R.color.
		 * white));
		 * btn_id.setTextColor(this.getResources().getColor(R.color
		 * .blank));
		 * btn_name.setTextColor(this.getResources().getColor(R
		 * .color.blank));
		 */

		setValueToView(findViewById(R.id.home_right_btn), "列表");

		edit_key.setClickable(true);
		// etNumber.setInputType(InputType.TYPE_CLASS_NUMBER); //调用数字键盘
		// rlEditText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);//设置输入类型和键盘为英文
		edit_key.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

		btn_city.setBackgroundResource(R.drawable.title_left);
		btn_id.setBackgroundResource(R.drawable.middle_p);
		btn_name.setBackgroundResource(R.drawable.title_right);

		btn_city.setTextColor(this.getResources().getColor(
				R.color.blank));
		btn_id.setTextColor(this.getResources().getColor(R.color.white));
		btn_name.setTextColor(this.getResources().getColor(
				R.color.blank));

		edit_key.setHint("请输入搜索ID");
		typesearch = TypeSearch.by_id;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.wh_no: {
			CancelDiagCity();

		}
			break;
		case R.id.wh_yes: {
			CancelDiagCity();

			String value_city = cities[country.getCurrentItem()][city
					.getCurrentItem()];

		}
			break;
		case R.id.left_img_btn:
		case R.id.home_left_btn: {
			this.LeftClickPop(findViewById(R.id.topNav));
		}

			break;
		case R.id.btn_sch_city: {
			view_Hide(lay_sch2);
			edit_key.setClickable(false);

			edit_key.setHint("");
			// edit_key.setInputType(EditorInfo.TYPE_CLASS_TEXT);
			typesearch = TypeSearch.by_city;
			btn_city.setBackgroundResource(R.drawable.left_p);
			btn_id.setBackgroundResource(R.drawable.title_center);
			btn_name.setBackgroundResource(R.drawable.title_right);

			btn_city.setTextColor(this.getResources().getColor(R.color.white));
			btn_id.setTextColor(this.getResources().getColor(R.color.blank));
			btn_name.setTextColor(this.getResources().getColor(R.color.blank));
			// jumpOtherActivity(SearchByCity.class);
			Intent ins = new Intent(TabSearch.this, SearchByCity.class);
			TabSearch.this.startActivityForResult(ins, 90);

			// goOtherActivity(SearchFragment.this, SearchByCity.class);
		}

			break;
		case R.id.btn_sch_id: {
			view_Show(lay_sch2);
			edit_key.setClickable(true);
			// etNumber.setInputType(InputType.TYPE_CLASS_NUMBER); //调用数字键盘
			// rlEditText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);//设置输入类型和键盘为英文
			edit_key.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

			btn_city.setBackgroundResource(R.drawable.title_left);
			btn_id.setBackgroundResource(R.drawable.middle_p);
			btn_name.setBackgroundResource(R.drawable.title_right);

			btn_city.setTextColor(this.getResources().getColor(R.color.blank));
			btn_id.setTextColor(this.getResources().getColor(R.color.white));
			btn_name.setTextColor(this.getResources().getColor(R.color.blank));

			edit_key.setHint("请输入搜索ID");
			typesearch = TypeSearch.by_id;
		}

			break;
		case R.id.btn_sch_nickname: {
			view_Show(lay_sch2);
			edit_key.setClickable(true);
			edit_key.setInputType(EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
			btn_city.setBackgroundResource(R.drawable.title_left);
			btn_id.setBackgroundResource(R.drawable.title_center);
			btn_name.setBackgroundResource(R.drawable.right_p);

			btn_city.setTextColor(this.getResources().getColor(R.color.blank));
			btn_id.setTextColor(this.getResources().getColor(R.color.blank));
			btn_name.setTextColor(this.getResources().getColor(R.color.white));

			edit_key.setHint("请输入搜索昵称");
			typesearch = TypeSearch.by_nickname;
		}

			break;
		case R.id.edit_btn_seach: {
			// view_Show( lay_sch2);

			if (TextUtils.isEmpty(getValueView(findViewById(R.id.edit_key)))) {
				showToast("请先输入搜索关键字！");

			} else {

				getSearch(getValueView(edit_key));
			}
		}

			break;
		case R.id.home_right_btn: {

			if (getValueView(findViewById(R.id.home_right_btn)).equals("搜索")) {
				showTitle("会员搜索");
				view_Hide(guide_view);
				view_Hide(ls, lay_sch2);
				view_Show(lay_sch1, top_hidden, lay_sch2);

				/**
				 * edit_key.setClickable(false);
				 * 
				 * edit_key.setHint("");
				 * 
				 * typesearch = TypeSearch.by_city;
				 * btn_city.setBackgroundResource(R.drawable.left_p);
				 * btn_id.setBackgroundResource(R.drawable.title_center);
				 * btn_name.setBackgroundResource(R.drawable.title_right);
				 * 
				 * btn_city.setTextColor(this.getResources().getColor(R.color.
				 * white));
				 * btn_id.setTextColor(this.getResources().getColor(R.color
				 * .blank));
				 * btn_name.setTextColor(this.getResources().getColor(R
				 * .color.blank));
				 */

				setValueToView(findViewById(R.id.home_right_btn), "列表");

				edit_key.setClickable(true);
				// etNumber.setInputType(InputType.TYPE_CLASS_NUMBER); //调用数字键盘
				// rlEditText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);//设置输入类型和键盘为英文
				edit_key.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

				btn_city.setBackgroundResource(R.drawable.title_left);
				btn_id.setBackgroundResource(R.drawable.middle_p);
				btn_name.setBackgroundResource(R.drawable.title_right);

				btn_city.setTextColor(this.getResources().getColor(
						R.color.blank));
				btn_id.setTextColor(this.getResources().getColor(R.color.white));
				btn_name.setTextColor(this.getResources().getColor(
						R.color.blank));

				edit_key.setHint("请输入搜索ID");
				typesearch = TypeSearch.by_id;

			} else if (getValueView(findViewById(R.id.home_right_btn)).equals(
					"列表")) {
				showTitle("最新会员");
				view_Show(ls);
				view_Hide(lay_sch1, lay_sch2, top_hidden);
				// view_Hide( top_hidden);
				setValueToView(findViewById(R.id.home_right_btn), "搜索");

			}
		}

			break;
		default:
			break;
		}

		v.setSelected(true);

	}

	void getSearch(String key) {
		ProShow("正在获取数据....");
		final String keys = key.trim();

		poolThread.submit(new Runnable() {

			@Override
			public void run() {
				try {
					req_map_param.put("uid", shareUserInfo().getUserid() + "");
					req_map_param.put("key", key_server);
					switch (typesearch) {
					case by_city: {

					}

						break;
					case by_id: {
						req_map_param.put("search_uid", keys);
						vo_search = sendReq(MURL_search_id, req_map_param);
					}

						break;
					case by_nickname: {
						req_map_param.put("nickname", keys);
						vo_search = sendReq(MURL_search_nickName, req_map_param);

					}

						break;

					default:
						break;
					}

					if (null == vo_search) {
						handler.obtainMessage(ACTION_FAIL).sendToTarget();
					}
					if (vo_search.isHasError()) {
						handler.obtainMessage(ACTION_ERROR,
								vo_search.getErrorDetail()).sendToTarget();

					} else {
						handler.obtainMessage(ACTION_TAG1).sendToTarget();

					}

				} catch (Exception e) {
					e.printStackTrace();
					handler.obtainMessage(ACTION_EXCEPTON).sendToTarget();
				}

			}
		});
	}

	int pageIndex = 1;
	boolean ishide = false;
	RespVo rv = null;
	RespVo vo_search = null;

	void getUserList() {
		if (!ishide) {
			ProShow("");
		}

		poolThread.submit(new Runnable() {

			@Override
			public void run() {

				try {
					req_map_param.put("uid", shareUserInfo().getUserid() + "");
					req_map_param.put("key", key_server);
					req_map_param.put("page", pageIndex + "");
					rv = sendReq(MURL_search_vip_list, req_map_param);
					if (rv == null) {
						handler.obtainMessage(Resp_DATA_EXCEPTION)
								.sendToTarget();
					}
					if (rv.isHasError()) {
						handler.obtainMessage(Resp_action_fail,
								rv.getErrorDetail()).sendToTarget();

					} else {
						handler.obtainMessage(Resp_action_ok).sendToTarget();

					}

				} catch (Exception e) {
					e.printStackTrace();
					handler.obtainMessage(Resp_exception).sendToTarget();
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

			// {"result":1,"error":"","data":{"users":[{"uid":157,"sex":2,"age":19,
			// "photo_url":"http:\/\/112.124.18.97\/attachment\/tmp\/157\/1.jpg",
			// "heart_description":"\u6211\u662f\u4e00\u4e2a\u7231\u5531\u7231\u8df3\u7684\u5973\u5b69\uff01\u6211\u559c\u6b22\u901b\u8857\u3001\u770b\u7535\u5f71\u3001\u770b\u4e66\uff01"
			// ,"nickname":"\u5c0f\u5c0f","province":"\u56db\u5ddd","city":"\u5357\u5145","locked":1,"opentome":2}
			// ,{"uid":158,"sex":2,"age":20,"photo_url":"http:\/\/112.124.18.97\/attachment\/tmp\/158\/1.jpg",
			// "heart_description":"\u6211\u53ef\u4ee5\u9a7e\u9a6d\u4e0d\u540c\u7684\u7a7f\u8863\u98ce\u683c\uff0c\u6211\u662f\u4e00\u4e2a\u611f\u6027\u7684\u5973\u5b69","nickname":"Carry","province":"\u56db\u5ddd",
			// "city":"\u4e50\u5c71","locked":1,"opentome":2},{"uid":159,"sex":2,"age":21,"photo_url":"http:\/\/112.124.18.97\/attachment\/tmp\/159\/1.jpg","heart_description":"\u6211\u662f\u4e00\u4e2a\u6d3b\u6ce2\u5f00\u6717\uff0c\u5927\u65b9\u5f97\u4f53\u7684\u5973\u5b69\u3002\u6211\u5e0c\u671b\u6211\u7684\u53e6\u4e00\u534a\u8ddf\u6211\u6709\u7740\u540c\u6837\u7684\u751f\u6d3b\u4ef7\u503c\u89c2","nickname":"\u666e\u7f57\u65fa\u65af","province":"\u56db\u5ddd","city":"\u51c9\u5c71","locked":1,"opentome":2},{"uid":160,"sex":2,"age":22,"photo_url":"http:\/\/112.124.18.97\/attachment\/tmp\/160\/1.jpg","heart_description":"\u6211\u662f\u4e00\u4e2a\u7406\u6027\u667a\u6167\u3001\u4e50\u89c2\u79ef\u6781\u3001\u4ed4\u7ec6\u8ba4\u771f\u3001\u6e29\u67d4\u8d24\u60e0\u7684\u5973\u5b69\uff0c\u6211\u559c\u6b22\u6444\u5f71\u3001\u97f3\u4e50\u3001\u821e\u8e48\u3001\u8bfb\u4e66\u548c\u65c5\u6e38\u3002","nickname":"\u4e91\u4e91","province":"\u56db\u5ddd","city":"\u6210\u90fd","locked":1,"opentome":2},{"uid":161,"sex":2,"age":23,"photo_url":"http:\/\/112.124.18.97\/attachment\/tmp\/161\/1.jpg","heart_description":"\u6211\u662f\u4e00\u4e2a\u5f88\u6e29\u67d4\u8d24\u60e0\u7684\u5973\u751f\uff0c\u6211\u559c\u6b22\u4eba\u54c1\u597d
			// \u5b5d\u987a
			// \u8d23\u4efb\u5fc3\u5f3a\u7684\u7537\u58eb","nickname":"Aimee","province":"\u56db\u5ddd","city":"\u5e7f\u5b89","locked":1,"opentome":2},{"uid":141,"sex":2,"age":22,"photo_url":"http:\/\/112.124.18.97\/attachment\/tmp\/141\/1.jpg","heart_description":"\u6211\u662f\u4e00\u4e2a\u559c\u7231\u8df3\u821e\u7684\u5973\u5b69\uff0c\u73b0\u5728\u662f\u4e00\u540d\u821e\u8e48\u8001\u5e08\u3002\u6bcf\u5929\u9664\u4e86\u4e0a\u8bfe\uff0c\u5c31\u662f\u5b85\u5728\u5bb6\u91cc\uff0c\u7a7a\u4e86\u4f1a\u53bb\u505a\u505aSPA\u3001\u65c5\u65c5\u6e38\uff0c\u6216\u8005\u6e38\u6e38\u6cf3~\u5bf9\u4e8e\u6211\uff0c\u53ef\u4ee5\u7528\u201c\u9759\u5982\u5904\u5b50\uff0c\u52a8\u5982\u8131\u5154\u201d\u6765\u5f62\u5bb9","nickname":"MUSE","province":"\u56db\u5ddd","city":"\u5185\u6c5f","locked":2,"opentome":1},{"uid":142,"sex":2,"age":23,"photo_url":"http:\/\/112.124.18.97\/attachment\/tmp\/142\/1.jpg","heart_description":"\u6211\u662f\u6f14\u5458\uff0c\u4f46\u6211\u66f4\u60f3\u8bf4\u6211\u662f\u4e00\u4e2a\u559c\u6b22\u8868\u6f14\u7684\u4eba\u3002\u4ece\u5c0f\u5bf9\u827a\u672f\u3001\u6587\u5316\u7684\u559c\u7231\uff0c\u4ee5\u53ca\u6253\u5c0f\u5f00\u59cb\u63a5\u62cd\u7684\u8868\u6f14\uff0c\u8ba9\u6211\u81ea\u7136\u800c\u7136\u7684\u8d70\u8fdb\u4e86\u73b0\u5728\u7684\u884c\u4e1a\u3002\u6253\u7403\u3001\u6e38\u6cf3\u662f\u6211\u6700\u5927\u7684\u7231\u597d\uff01","nickname":"ROSE","province":"\u56db\u5ddd","city":"\u8d44\u9633","locked":2,"opentome":1},{"uid":143,"sex":2,"age":24,"photo_url":"http:\/\/112.124.18.97\/attachment\/tmp\/143\/1.jpg","heart_description":"\u6211\u662f\u4e00\u4e2a\u7231\u597d\u5e7f\u6cdb\u7684\u5973\u5b69\uff0c\u559c\u6b22\u770b\u4e66\u3001\u7535\u5f71\uff0cK\u6b4c\uff0c\u770b\u7535\u89c6\uff01","nickname":"Selina","province":"\u56db\u5ddd","city":"\u96c5\u5b89","locked":1,"opentome":2}],
			// "current_page":1,"total_page":3,"page_size":8,"total_count":"22"}}
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

				usv.setOpentome(arr.getJSONObject(i).getInt("opentome") + "");
				usv.setAge(arr.getJSONObject(i).getInt("age") + "");
				usv.setSex(arr.getJSONObject(i).getInt("sex") + "");
				data_user.add(usv);
			}
		} catch (JSONException e) {
			showToast(" 解析数据出错！" + e.getMessage());
			e.printStackTrace();
		} finally {

		}

		return null;
	}

	UserSearchVo user_res = new UserSearchVo();
	OnTouchListener touch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_MOVE
					|| event.getAction() == MotionEvent.ACTION_UP
					|| event.getAction() == MotionEvent.ACTION_DOWN) {

				// view_Hide(guide_view);
				// sh.SetisFistUser_SearPage();
			}
			return false;
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		// showToast("fin-sear"+requestCode);

		if (requestCode == 90) {

			// showToast("fin-sear");
			showTitle("会员搜索");
			view_Hide(guide_view);
			view_Hide(ls, lay_sch2);
			view_Show(lay_sch1, top_hidden, lay_sch2);

			/**
			 * edit_key.setClickable(false);
			 * 
			 * edit_key.setHint("");
			 * 
			 * typesearch = TypeSearch.by_city;
			 * btn_city.setBackgroundResource(R.drawable.left_p);
			 * btn_id.setBackgroundResource(R.drawable.title_center);
			 * btn_name.setBackgroundResource(R.drawable.title_right);
			 * 
			 * btn_city.setTextColor(this.getResources().getColor(R.color.white)
			 * );
			 * btn_id.setTextColor(this.getResources().getColor(R.color.blank));
			 * btn_name
			 * .setTextColor(this.getResources().getColor(R.color.blank));
			 */

			setValueToView(findViewById(R.id.home_right_btn), "列表");

			edit_key.setClickable(true);
			// etNumber.setInputType(InputType.TYPE_CLASS_NUMBER); //调用数字键盘
			// rlEditText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);//设置输入类型和键盘为英文
			edit_key.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

			btn_city.setBackgroundResource(R.drawable.title_left);
			btn_id.setBackgroundResource(R.drawable.middle_p);
			btn_name.setBackgroundResource(R.drawable.title_right);

			btn_city.setTextColor(this.getResources().getColor(R.color.blank));
			btn_id.setTextColor(this.getResources().getColor(R.color.white));
			btn_name.setTextColor(this.getResources().getColor(R.color.blank));

			edit_key.setHint("请输入搜索ID");
			typesearch = TypeSearch.by_id;

		}
	};

	void readSearchRes() {
		try {

			JSONObject obj = new JSONObject(vo_search.getResp())
					.getJSONObject("data");

			user_res.setHead_url(obj.getString("header_url"));
			user_res.setHeight(obj.getString("height"));
			user_res.setNickName(obj.getString("nickname"));
			user_res.setProvince(obj.getString("province"));
			user_res.setCity(obj.getString("city"));
			user_res.setUid(obj.getInt("uid") + "");
			user_res.setOpentome(obj.getInt("opentome") + "");
			user_res.setLocked(obj.getInt("locked") + "");
			user_res.setSex(obj.getInt("sex") + "");
			if (null != adapter) {

				adapter.clearDownQuence();
			}
			lease();
			if (obj != null) {
				obj = null;
			}

			Intent in = new Intent(t_context, BarPushActivity.class);
			in.putExtra("data", user_res);
			TabSearch.this.startActivity(in);

		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException("解析搜索数据出错！");
		}

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ProDimiss();
			switch (msg.what) {
			case ACTION_TAG1: {

				readSearchRes();

			}
				break;
			case Resp_DATA_OK: {
			}

				break;
			case ACTION_FAIL: {
				showToast("服务器响应失败！" + "");
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
				parselJson(rv.getResp());
				data_container.addAll(data_user);

				// oomAdapter();

				//这边有点问题啊，oomAdterNew()方法里也有此判断
				if (null == itadapter) {
					// adapter = new AdapterSearch(t_context);
					// adapter.setType_search(AdapterSearch_Type.search_type_default);
					// adapter.setData(data_container);
					// // adapter.setMgr_task(cell);
					// adapter.setAct(TabSearch.this);
					// adapter.setLs_view(ls);
					oomAdpterNew();
					if (null == ls) {
						return;
					}
					// ls.setAdapter(adapter);
					// ls.setOnItemClickListener(it);
					ls.setOnScrollListener(new OnScrollListener() {

						@Override
						public void onScrollStateChanged(AbsListView view,
								int scrollState) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onScroll(AbsListView view,
								int firstVisibleItem, int visibleItemCount,
								int totalItemCount) {
							if (firstVisibleItem > 0) {
								view_Hide(guide_view);
								shareUserInfo().SetisFistUser_SearPage();
							}

						}
					});

				}

				// adapter.notifyDataSetChanged();
				ls.stopRefresh();

				if (shareUserInfo().getisFistEnter_SearPage()) {
					view_Show(guide_view);
				} else {
					view_Hide(guide_view);

				}

				if (!hasNextData) {
					ls.setPullLoadEnable(false);
					ls.setFooterText("数据已加载完啦！");
					ls.setRefreshTime(GeneralUtil.simpleDateFormat
							.format(new Date()));
					// ls.stopLoadMoreWithState(LoadState.STATE_OVER);
					// ls.setPullLoadEnable(false, LoadState.STATE_OVER);
				} else {
					ls.setPullLoadEnable(true);
					// ls.stopLoadMoreWithState(LoadState.STATE_NORMAL);
					// ls.setPullLoadEnable(true, LoadState.STATE_OVER);
				}
			}

				break;

			default:
				break;
			}
		};
	};

	void reDefineScrollBar(ListView ls) {
		try {
			Field f = AbsListView.class.getDeclaredField("mFastScroller");
			f.setAccessible(true);
			Object o = f.get(ls);
			f = f.getType().getDeclaredField("mThumbDrawable");
			f.setAccessible(true);
			Drawable drawable = (Drawable) f.get(o);
			drawable = getResources().getDrawable(R.drawable.meimeng);
			f.set(o, drawable);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private ImageCache mCache;
	ImageLoaderAdapter adapter_oom = null;

	ItemAdapter itadapter = null;

	void oomAdpterNew() {
		if (null == itadapter) {

			itadapter = new ItemAdapter();
			ls.setAdapter(itadapter);
			ls.setOnItemClickListener(it);
		}
		// reDefineScrollBar(ls);
		itadapter.notifyDataSetChanged();

	}

	@Override
	public void onLoadMore() {
		if (hasNextData) {
			pageIndex++;
			hasNextData = false;
			ishide = true;
			getUserList();
		}

	}

	public void iniData() {
//		getUserList();
		// iniCityData();
		changeAllSearch();

	}

	@Override
	public void onRefresh() {
		pageIndex = 1;
		data_container.clear();
		// ClearFresh();
		getUserList();

	}

	@Override
	public void onHeader_ItemClick(View v) {
		judgeTypeMyfocus(v, TabSearch.this);
		switch (v.getId()) {
		case R.id.gallery_imv:
		case R.id.lay_person: {
            if (MeiMengApp.isVisitor){
                MeiMengApp.setVisitorDialogView(TabSearch.this,1);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();

		try {

			closePool();
			lease();
		} catch (Exception e) {
			e.printStackTrace();
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

	protected ImageLoader imageLoader = null;
	DisplayImageOptions options;

	void iniloadImg() {

		if (imageLoader == null) {

			imageLoader = ImageLoader.getInstance();
		}

		// options = new DisplayImageOptions.Builder()
		// .showImageOnLoading(R.drawable.female_default)
		// .showImageForEmptyUri(R.drawable.ic_empty)
		// .showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
		// .cacheOnDisc(true).displayer(new RoundedBitmapDisplayer(0))
		// .build();

		if (shareUserInfo().getUserInfo().getSex() == 1) {
			options = new DisplayImageOptions.Builder()
					// .showImageForEmptyUri(R.drawable.female_default)
					// .showImageOnFail(R.drawable.female_default)
					.showImageForEmptyUri(0).showImageOnFail(0)
					.resetViewBeforeLoading().cacheOnDisc()
					.cacheInMemory().imageScaleType(ImageScaleType.EXACTLY)
					// .cacheInMemory(true).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
					.bitmapConfig(Bitmap.Config.RGB_565)
					// .displayer(new RoundedBitmapDisplayer(2)).build();
					.displayer(new FadeInBitmapDisplayer(200)).build();
		} else {
			options = new DisplayImageOptions.Builder()
					// .showImageForEmptyUri(R.drawable.man_default)
					// .showImageOnFail(R.drawable.man_default)
					// .displayer(new RoundedBitmapDisplayer(2)).build()
					.showImageForEmptyUri(0).showImageOnFail(0)
					.resetViewBeforeLoading().cacheOnDisc()
					.cacheInMemory().imageScaleType(ImageScaleType.EXACTLY)
					// .cacheInMemory(true).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
					.bitmapConfig(Bitmap.Config.RGB_565)
					// .displayer(new RoundedBitmapDisplayer(2)).build();
					.displayer(new FadeInBitmapDisplayer(200)).build();
		}

	}

	private class ViewHolder {
		public TextView text;
		public ImageView image;
		public ImageView Sempty;
		com.swater.meimeng.mutils.MyTextView2 define_txt;
		public TextView user_name, user_age, user_pos;
		TextView user_heart_desc;
		// user_heart_desc;
	}

	class ItemAdapter extends BaseAdapter {

		private AnimateFirstDisplayListener animateFirstListener = new AnimateFirstDisplayListener();

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
				// holder.empty = (ImageView) view
				// .findViewById(R.id.cell_header_empty);
				holder.user_name = (TextView) view
						.findViewById(R.id.cell_username);
				holder.user_age = (TextView) view
						.findViewById(R.id.cell_userage);
				holder.user_pos = (TextView) view
						.findViewById(R.id.cell_user_cityand_province);
				holder.user_heart_desc = (TextView) view
						.findViewById(R.id.cell_desc);

				// holder.user_heart_desc = (MyTextView2) view
				// .findViewById(R.id.cell_desc);
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
				// holder.user_heart_desc.setContent("这是一个神秘的人物，还没有描述哦！");
				// holder.define_txt.setText("这是一个神秘的人物，还没有描述哦！");
			} else {

				GeneralUtil.setValueToView(holder.user_heart_desc,
						usv.getHeart_desc());
				// holder.user_heart_desc.setContent(usv.getHeart_desc());
				// holder.define_txt.setText(usv.getHeart_desc());
				// holder.define_txt.setMovementMethod(ScrollingMovementMethod.getInstance());
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
			// holder.image.setScaleType(ScaleType.FIT_XY);
			if (usv.getSex().trim().equals("2")) {
				holder.image.setBackgroundResource(R.drawable.female_default);

			} else {
				holder.image.setBackgroundResource(R.drawable.man_default);
			}
			// Log.d("-----可使用内存--KB--》》-" + Runtime.getRuntime().freeMemory()
			// / 1000 + "---KB--", "---kb--");
			// Log.d("-----可使用内存-MB---》》-" + Runtime.getRuntime().freeMemory()
			// / 1000 / 1000 + "MB", "---kb--");
			animateFirstListener.setVh(holder);
			// holder.empty.setVisibility(View.GONE);
			// holder.image.setVisibility(View.VISIBLE);
			imageLoader.displayImage( usv.getPhotoUrl(), holder.image,
					options, animateFirstListener);
			return view;
		}
	}

	private class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());
		ViewHolder vh = null;

		public ViewHolder getVh() {
			return vh;
		}

		public void setVh(ViewHolder vh) {
			this.vh = vh;
		}

		public AnimateFirstDisplayListener(ViewHolder holder) {
			this.vh = holder;
		}

		public AnimateFirstDisplayListener() {
		}

		@Override
		public void onLoadingStarted(String imageUri, View view) {
			if (view instanceof ImageView) {
				ImageView iv = (ImageView) view;
				iv.setImageBitmap(null);
				// iv.setImageResource(0);

			}

			super.onLoadingStarted(imageUri, view);

		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			// TODO Auto-generated method stub
			super.onLoadingFailed(imageUri, view, failReason);
			// this.vh.empty.setVisibility(View.VISIBLE);
			// this.vh.image.setVisibility(View.GONE);
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			// public static Bitmap createBitmap (Bitmap source, int x, int y,
			// int width, int height, Matrix m, boolean filter)
			// 从原始位图剪切图像，这是一种高级的方式。可以用Matrix(矩阵)来实现旋转等高级方式截图
			// 参数说明：
			// 　　Bitmap source：要从中截图的原始位图
			// 　　int x:起始x坐标
			// 　　int y：起始y坐标
			// int width：要截的图的宽度
			// int height：要截的图的宽度
			// Bitmap.Config config：一个枚举类型的配置，可以定义截到的新位图的质量
			// Matrix ma = new Matrix();
			// vh.empty.setVisibility(View.GONE);
			// vh.image.setVisibility(View.VISIBLE);

			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				// imageView.setScaleType(ScaleType.CENTER_CROP);
				// loadedImage=Bitmap.createScaledBitmap(loadedImage,
				// loadedImage.getWidth(), loadedImage.getHeight(), true);
				if (loadedImage != null) {
					imageView.setImageBitmap(loadedImage);
				}

				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
