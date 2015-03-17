package com.swater.meimeng.activity.newtabMain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;

import com.meimeng.app.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.swater.meimeng.database.ShareUtil;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.constant.MConstantUser;
import com.swater.meimeng.mutils.net.MUrlPostAddr;
import com.swater.meimeng.mutils.net.RequestByPost;

/**
 * @category 主页
 */
@SuppressWarnings("deprecation")
public class IndexActivity extends TabActivity implements OnClickListener,
		MUrlPostAddr {
	TabHost tabhost = null;
	private RadioGroup m_radioGroup;
	
	RadioButton rb_msg = null;
	RadioButton rb_rec = null;
	RadioButton rb_activity = null;
	RadioButton rb_vip = null;
	RadioButton rb_search = null;
	
	private Drawable[] icons_normal = new Drawable[5];
	private Drawable[] icons_checked = new Drawable[5];

	public static final String EXTRA_CURRENT_TAB_ID = "currentTabId";

	//michael 这边是把TabVip改成TabVipMember
	private static final Class<?>[] tabClasses = {
			TabParty.class, TabRecommend.class, TabMessage.class};
	private static final String[] TAGS = { "TabSearch", "TabParty",
			"TabRecommend", "TabMessage", "TabVip" };

	Context context = null;
	
	private int currentTabIndex = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_main);
		context = this;

		iniTab();
		checkVersion();
		receiveNotice();
	}

	void receiveNotice() {
		if (this.getIntent().getIntExtra("type", -1) != -1) {
			clearRest();
			rb_msg.setChecked(true);
			rb_msg.setTextColor(getResources().getColor(R.color.gold));
			rb_msg.setCompoundDrawables(null, icons_checked[3], null, null);
			tabhost.setCurrentTab(3);
		}
	}

	void iniTab() {
		tabhost = getTabHost();

		icons_normal[0] = getResources().getDrawable(R.drawable.icon_tab1);
		icons_normal[1] = getResources().getDrawable(R.drawable.icon_tab2);
		icons_normal[2] = getResources().getDrawable(R.drawable.icon_tab3);
		icons_normal[3] = getResources().getDrawable(R.drawable.icon_tab4);
		icons_normal[4] = getResources().getDrawable(R.drawable.icon_tab5);

		icons_checked[0] = getResources().getDrawable(R.drawable.icon_tab1_p);
		icons_checked[1] = getResources().getDrawable(R.drawable.icon_tab2_p);
		icons_checked[2] = getResources().getDrawable(R.drawable.icon_tab3_p);
		icons_checked[3] = getResources().getDrawable(R.drawable.icon_tab4_p);
		icons_checked[4] = getResources().getDrawable(R.drawable.icon_tab5_p);

		for (int i = 0; i < 5; i++) {
			icons_normal[i].setBounds(0, 0, icons_normal[i].getMinimumWidth(),
					icons_normal[i].getMinimumHeight());
			icons_checked[i].setBounds(0, 0,
					icons_checked[i].getMinimumWidth(),
					icons_checked[i].getMinimumHeight());
		}
		
		m_radioGroup = (RadioGroup) findViewById(R.id.main_radiogroup);
		rb_search = (RadioButton) findViewById(R.id.rb_search);
		rb_activity = (RadioButton) findViewById(R.id.rb_doing);
		rb_rec = (RadioButton) findViewById(R.id.rb_recommend);
		rb_msg = (RadioButton) findViewById(R.id.rb_message);
		rb_vip = (RadioButton) findViewById(R.id.rb_vip);
		
		rb_search.setOnClickListener(this);
		rb_activity.setOnClickListener(this);
		rb_rec.setOnClickListener(this);
		rb_msg.setOnClickListener(this);
		rb_vip.setOnClickListener(this);
		
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			currentTabIndex = bundle.getInt(EXTRA_CURRENT_TAB_ID);
		}
		if (null == tabhost) {
			NSLoger.Log("--nulll--tab--");
		}

		Intent intent = new Intent(this,TabSearch.class);
		Bundle bundle1 = new Bundle();
		bundle1.putInt("vipOrSearch",0);
		intent.putExtras(bundle1);
		getTabHost().addTab(
				getTabHost().newTabSpec(TAGS[0])
						.setIndicator(TabSearch.class.getSimpleName())
						.setContent(intent));
		int i = 0;
		for (Class<?> clazz : tabClasses) {
			getTabHost().addTab(
					getTabHost().newTabSpec(TAGS[i+1])
							.setIndicator(clazz.getSimpleName())
							.setContent(new Intent(this, clazz)));
			i++;
		}

		Intent intent2 = new Intent(this,TabSearch.class);
		Bundle bundle2 = new Bundle();
		bundle2.putInt("vipOrSearch",1);
		intent2.putExtras(bundle2);
		getTabHost().addTab(
				getTabHost().newTabSpec(TAGS[4])
						.setIndicator(TabSearch.class.getSimpleName())
						.setContent(intent2));
		
		rb_rec.setTextColor(getResources().getColor(R.color.gold));
		rb_rec.setCompoundDrawables(null, icons_checked[2], null, null);
		
		tabhost.setCurrentTab(2);
		
	}

	void clearRest() {
		rb_search.setCompoundDrawables(null, icons_normal[0], null, null);
		rb_activity.setCompoundDrawables(null, icons_normal[1], null, null);
		rb_rec.setCompoundDrawables(null, icons_normal[2], null, null);
		rb_msg.setCompoundDrawables(null, icons_normal[3], null, null);
		rb_vip.setCompoundDrawables(null, icons_normal[4], null, null);

		rb_search.setTextColor(getResources().getColor(R.color.tab_normal));
		rb_activity.setTextColor(getResources().getColor(R.color.tab_normal));
		rb_rec.setTextColor(getResources().getColor(R.color.tab_normal));
		rb_msg.setTextColor(getResources().getColor(R.color.tab_normal));
		rb_vip.setTextColor(getResources().getColor(R.color.tab_normal));
	}

	@Override
	public void onClick(View v) {
		clearRest();
		switch (v.getId()) {
		
		case R.id.rb_search: {
			rb_search.setTextColor(getResources().getColor(R.color.gold));
			rb_search.setCompoundDrawables(null, icons_checked[0], null, null);
			tabhost.setCurrentTab(0);

		}break;
		
		case R.id.rb_doing: {
			rb_activity.setTextColor(getResources().getColor(R.color.gold));
			rb_activity
					.setCompoundDrawables(null, icons_checked[1], null, null);
			tabhost.setCurrentTab(1);

		}break;
		
		case R.id.rb_recommend: {
			rb_rec.setTextColor(getResources().getColor(R.color.gold));
			rb_rec.setCompoundDrawables(null, icons_checked[2], null, null);
			tabhost.setCurrentTab(2);
		}break;
		
		case R.id.rb_message: {
			rb_msg.setTextColor(getResources().getColor(R.color.gold));
			rb_msg.setCompoundDrawables(null, icons_checked[3], null, null);
			tabhost.setCurrentTab(3);

		}break;
		
		case R.id.rb_vip: {
			rb_vip.setTextColor(getResources().getColor(R.color.gold));
			rb_vip.setCompoundDrawables(null, icons_checked[4], null, null);
			tabhost.setCurrentTab(4);

		}break;
		
		default:
			break;
		}
	}

	String res_version = "";
	protected ExecutorService poolThread = Executors.newFixedThreadPool(1);
	protected Map<String, String> req_map_param = Collections
			.synchronizedMap(new HashMap<String, String>());
	Handler mhaHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 910:
				parselVersion();
				break;
			case 110: {
				Intent act = new Intent(context, ForceUpdate.class);
				act.putExtra("url", ver_url);
				context.startActivity(act);
				finish();

			}
				break;
			default:
				break;
			}
		};
	};

	public void onBackPressed() {

		super.onBackPressed();
		try {
			
			ImageLoader.getInstance().stop();
		} catch (Exception e) {
		}
	};

	String ver_name = "";
	String ver_desc = "";
	String ver_url = "";
	int newcode = 0;

	void parselVersion() {

		// {"versionname":"1.0.1","versioncode":2,"forceupdatecode":1,
		// "updatetime":"2013-09-23","description":"1.\u66f4\u65b0\u5185\u5bb91\uff0c\r\n2.\u66f4\u65b0\u5185\u5bb92\u3002",
		// "downloadurl":"http:\/\/112.124.18.97\/app\/meimeng_android_beta_0923.apk"}

		try {
			if (!TextUtils.isEmpty(res_version)) {

				JSONObject json = new JSONObject(res_version);
				ver_name = json.getString("versionname");
				ver_desc = json.getString("description");
				ver_url = json.getString("downloadurl");
				newcode = json.getInt("forceupdatecode");
				boolean isf = newcode == 0 ? false : isForceUpdate(newcode);
				if (isf == true) {
					mhaHandler.obtainMessage(110).sendToTarget();
				}
			}

		} catch (Exception e) {

		}

	}

	boolean isForceUpdate(int newwcode) {
		boolean isForce = false;

		try {
			int code = getAppCode(IndexActivity.this);
			if (code <= newcode) {
				isForce = true;
			}

		} catch (Exception e) {
		}
		return isForce;
	}

	boolean isHasNewVersion(String newver, String oldVersion) {

		boolean isHas = false;

		// 判断第一位版本号s是否相同
		if (newver.indexOf(0) == oldVersion.indexOf(0)) {

			if (Float.parseFloat(newver.substring(2, newver.length())) > Float
					.parseFloat(oldVersion.substring(2, oldVersion.length()))) {
				isHas = true;

			} else {
				isHas = false;
			}
		} else if (newver.indexOf(0) > oldVersion.indexOf(0)) {
			isHas = true;

		} else {
			isHas = false;

		}
		return isHas;
	}

	public int getAppCode(Context context) {
		int code = 0;
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			code = pi.versionCode;

		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}
		return code;
	}

	void checkVersion() {

		poolThread.submit(new Runnable() {

			@Override
			public void run() {
				req_map_param.put(MConstantUser.UserProperty.user_key,
						key_server);
				req_map_param.put(MConstantUser.UserProperty.uid, ShareUtil
						.getInstance(context).getUserid() + "");

				res_version = RequestByPost.CkeckNewVersion(url_version);
				if (res_version != null) {
					mhaHandler.obtainMessage(910).sendToTarget();
				}
			}

		});

	};
}
