package com.swater.meimeng.activity.reg;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.meimeng.app.R;
import com.swater.meimeng.activity.user.WaitActive;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.database.Map_Province.CityVo;
import com.swater.meimeng.database.Map_Province.ProvinceVo;
import com.swater.meimeng.database.ShareUserConstant.UserGobal;
import com.swater.meimeng.database.ShareUtil.UserVo;
import com.swater.meimeng.database.XmlDataCity;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.constant.MConstantUser;
import com.swater.meimeng.mutils.diagutil.Tools;
import com.swater.meimeng.mutils.wheelview.ArrayWheelAdapter;
import com.swater.meimeng.mutils.wheelview.OnWheelChangedListener;
import com.swater.meimeng.mutils.wheelview.WheelView;

public class UserManRegActivity extends BaseTemplate {
	List<ProvinceVo> ls_pro = null;
	XmlDataCity reader = null;
	int tag_which = 0;
	Dialog user_diag = null;
	private WheelView country, city;
	private static String countries[];
	private String cities[][];
	int container_size = 3;
	int age_size_begin = 17;
	int assert_begin = 100;
	int assert_begin_up = 150;
	Button btn_city_yes = null;
	Button btn_city_no = null;
	int age_size_end = 60;
	final static int diag_city = 90;
	private static final int diag_age = 10;
	private static final int diag_assert = 110;
	int record_type = 0;
	String arr_times[] = null;
	String arr_asserts[] = null;

	void iniAssertRange() {
		arr_asserts = new String[] { "500W以下", "500W-1000W", "1000W-3000W",
				"3000W-5000W", "5000W-1E", "1E-3E", "5E-10E", "10E以上" };
		;

	}

	void iniTestData() {

		arr_times = new String[43];
		for (int i = 0; i < arr_times.length; i++) {
			age_size_begin++;
			arr_times[i] = age_size_begin + "";

		}
		reader = new XmlDataCity();
		try {
			reader.init(t_context);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
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

	// void showCheer() {
	//
	// country.setAdapter(new ArrayWheelAdapter<String>(countries));
	// country.setCurrentItem(0);
	// city.setAdapter(new ArrayWheelAdapter<String>(cities[0]));
	// city.setCurrentItem(cities[0].length / 2);
	// }

	public View getDialogView() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int mScreenWidth = dm.widthPixels; // 获得屏幕的宽
		View viewMain = LayoutInflater.from(this).inflate(
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
				city.setCurrentItem(0);

			}
		});

		city = (WheelView) viewMain.findViewById(R.id.city);

		return viewMain;
	}

	public View getDialogView(String name, String[] values) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int mScreenWidth = dm.widthPixels; // 获得屏幕的宽
		View viewMain = LayoutInflater.from(this).inflate(
				R.layout.wheel_activity, null);
		viewMain.setMinimumWidth(mScreenWidth);
		btn_city_no = (Button) viewMain.findViewById(R.id.wh_yes);
		btn_city_yes = (Button) viewMain.findViewById(R.id.wh_no);
		btn_city_no.setOnClickListener(this);
		btn_city_yes.setOnClickListener(this);

		TextView nameshow = (TextView) viewMain.findViewById(R.id.showname);
		nameshow.setText(name);

		city = (WheelView) viewMain.findViewById(R.id.pro_ac);
		ArrayWheelAdapter<String> ada = new ArrayWheelAdapter<String>(values);

		city.setAdapter(ada);
		city.setCurrentItem(0);

		return viewMain;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		record_type = id;
		switch (id) {
		case diag_city: {

			View wheelView = getDialogView();
			country.setAdapter(new ArrayWheelAdapter<String>(countries));
			country.setCurrentItem(0);
			city.setAdapter(new ArrayWheelAdapter<String>(cities[0]));
			// city.setCurrentItem(cities[0].length / 2);
			city.setCurrentItem(0);
			user_diag = Tools.pull_Dialog(t_context, wheelView);
		}

			break;
		case diag_age: {

			View wheelView = getDialogView("选择年龄", arr_times);
			user_diag = Tools.pull_Dialog(t_context, wheelView);

			break;
		}
		case diag_assert: {
			View wheelView = getDialogView("选择资产", arr_asserts);

			user_diag = Tools.pull_Dialog(t_context, wheelView);

			break;
		}
		default:
			break;
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.man_reg_by_phone);
		this.TempRun();

	}

	String city_id = "";

	@Override
	public void onClick(View v) {
		// showToast("click--");
		switch (v.getId()) {

		// 取消
		case R.id.wh_no: {

			if (user_diag != null || user_diag.isShowing()) {
				user_diag.cancel();
			}
		}
			break;
		// 确定
		case R.id.wh_yes: {

			switch (record_type) {
			case diag_age: {
				int index = city.getCurrentItem();
				String t_value = arr_times[index];

				if (tag_which == 1) {
					setValueToView(findViewById(R.id.txt_reg_assert), t_value);
				} else {
					//
					setValueToView(findViewById(R.id.txt_reg_age), t_value);
				}

			}

				break;
			case diag_assert: {
				int index = city.getCurrentItem();
				String t_value = arr_asserts[index];
				index++;
				req_map_param.put(MConstantUser.UserReg.assets_level, index
						+ "");
				setValueToView(findViewById(R.id.txt_reg_assert), t_value);

			}

				break;
			case diag_city: {
				String value_city = cities[country.getCurrentItem()][city
						.getCurrentItem()];

				city_id = reader.getCityIdByName(value_city);

				setValueToView(findViewById(R.id.txt_reg_city), value_city);
			}

				break;

			default:
				break;
			}

			if (user_diag != null) {
				user_diag.cancel();
			}

		}
			break;
		case R.id.home_right_btn: {
			if (ischeckData()) {
				regUser();
			}

			/*
			 * if (ischeckData() && tag_which !=1) { Intent in = new
			 * Intent(t_context, RegUploadImg.class);
			 * t_context.startActivity(in); }else{
			 * 
			 * regUser(); }
			 */
		}
			break;
		case R.id.home_left_btn: {
			onBackPressed();
		}
			break;
		case R.id.lay_reg_age: {

			this.onCreateDialog(diag_age);

		}
			break;
		case R.id.wh_lay_assert: {
			this.onCreateDialog(diag_assert);
		}
			break;
		case R.id.wh_lay_city: {
			this.onCreateDialog(diag_city);
		}
			break;
		default:
			break;
		}
	}

	@Override
	public void iniView() {
		tag_which = getIntent().getIntExtra("tag", 0);
		switch (tag_which) {
		case 1:// 女
		{
			view_Show(findButton(R.id.home_right_btn));
			setValueToView(findViewById(R.id.home_right_btn), "下一步");
			view_Show(findViewById(R.id.home_left_btn));
			setValueToView(findViewById(R.id.home_left_btn), "返回");
		}
			break;
		case 2:// 男
		{
			// setValueToView(findViewById(R.id.txt_assert), "年龄");
			view_Show(findViewById(R.id.home_left_btn));
			setValueToView(findViewById(R.id.home_left_btn), "返回");
			view_Show(findButton(R.id.home_right_btn));
			setValueToView(findViewById(R.id.home_right_btn), "提交");
			// view_Hide(findButton(R.id.home_right_btn));

		}
			break;

		default:
			break;
		}
		this.iniTestData();
		this.iniAssertRange();
		this.getDialogView();

	}

	@Override
	public void bindClick() {
		setClickEvent(findViewById(R.id.home_left_btn),
				findViewById(R.id.wh_lay_city),
				findViewById(R.id.wh_lay_assert),
				findButton(R.id.home_right_btn), findViewById(R.id.lay_reg_age));
		setClickEvent(btn_city_no, btn_city_yes);
	}

	/**
	 * @category 检查参数
	 * @return true--有效 fasle 无效
	 */
	boolean ischeckData() {
		boolean isvalidate = true;

		if (TextUtils.isEmpty(getValueView(findViewById(R.id.input_phone)))) {
			showToat("请先填写您的手机号");
			// findEditText(R.id.user_name).requestFocus();
			isvalidate = false;
			return isvalidate;
		}
		if (!TextUtils.isEmpty(getValueView(findViewById(R.id.input_phone)))) {
			if (!validatePhone(getValueView(findViewById(R.id.input_phone))
					.trim())) {
				showToat("请填写11位手机号");
				// findEditText(R.id.user_name).requestFocus();
			}
		}

		if (TextUtils.isEmpty(getValueView(findViewById(R.id.txt_input_pwd)))) {
			showToat("请填写密码");
			isvalidate = false;
			return isvalidate;
		}
		if (TextUtils.isEmpty(getValueView(findViewById(R.id.txt_input_name)))) {
			showToat("请填写姓名");
			isvalidate = false;
			return isvalidate;
		}
		if (TextUtils.isEmpty(getValueView(findViewById(R.id.txt_reg_assert)))) {
			showToat("请填写资产");
			isvalidate = false;
			return isvalidate;
		}

		if (TextUtils.isEmpty(getValueView(findViewById(R.id.txt_reg_age)))) {
			showToat("请选择年龄");
			isvalidate = false;
			return isvalidate;
		}

		if (TextUtils.isEmpty(getValueView(findViewById(R.id.txt_reg_city)))) {
			showToat("请先选择城市");
			isvalidate = false;
			return isvalidate;
		}
		return isvalidate;
	}

	public boolean validatePhone(String msg) {
		String regex = "^\\d{3}-?\\d{8}|\\d{4}-?\\d{8}$";
		boolean isValidate = msg.matches(regex);

		return isValidate;
	}

	/**
	 * @category 注册用户
	 */
	void regUser() {
		ProShow("注册中...");
		req_map_param.put("key", key_server);
		req_map_param.put(MConstantUser.UserReg.telephone,
				getValueView(findViewById(R.id.input_phone)));
		req_map_param.put(MConstantUser.UserReg.password,
				getValueView(findViewById(R.id.txt_input_pwd)));
		req_map_param.put(MConstantUser.UserReg.city_id, city_id);

		req_map_param.put(MConstantUser.UserReg.realname,
				getValueView(findViewById(R.id.txt_input_name)).trim());
		req_map_param.put(MConstantUser.UserReg.sex, "1");
		req_map_param.put(MConstantUser.UserReg.age,
				getValueView(findViewById(R.id.txt_reg_age)).trim());

		poolThread.submit(new Runnable() {

			@Override
			public void run() {
				try {
					req_map_param.put("key", key_server);
					vo_resp = sendReq(MURL_user_reg, req_map_param);
					Log.d("resp-->", vo_resp.toString());
					if (vo_resp.isHasError()) {
						handler.obtainMessage(Resp_action_fail,
								vo_resp.getErrorDetail()).sendToTarget();
					} else {

						handler.obtainMessage(Resp_action_ok, vo_resp)
								.sendToTarget();
					}
				} catch (Exception e) {

					handler.obtainMessage(Resp_exception).sendToTarget();
				}

			}
		});

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ProDimiss();
			switch (msg.what) {
			case Resp_action_ok:
				switch (tag_which) {
				case 1:
					Intent in = new Intent(t_context, RegUploadImg.class);
					int uid;
					try {
						
						parselJson(vo_resp.getResp());
						regPush();

						uid = new JSONObject(vo_resp.getResp()).getJSONObject(
								"data").getInt(UserGobal.USER_ID);
						in.putExtra("uid", uid);
						in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						t_context.startActivity(in);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;
				case 2:

					showToat("注册成功！");
					Intent ins = new Intent(t_context, WaitActive.class);
					t_context.startActivity(ins);
					UserManRegActivity.this.finish();
					break;

				default:
					break;
				}

				break;
			case Resp_action_fail:
				showToat("注册失败！" + mg2String(msg));
				break;
			case Resp_exception:
				showToat("注册异常！");
				break;

			default:
				break;
			}

		};

	};

	/***
	 * 
	 * 
	 * @category 注册JPUSH 别名
	 */
	void regPush() {

		JPushInterface.setAlias(this, shareUserInfo().getUserid() + "",
				new TagAliasCallback() {

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
	
	

	protected Object parselJson(String res) {

		try {

			JSONObject obj = new JSONObject(res);
			JSONObject baseobj = obj.getJSONObject("data");
			int userid = obj.getJSONObject("data").getInt(UserGobal.USER_ID);
			int sex = obj.getJSONObject("data").getInt(UserGobal.USER_SEX);
			int active = obj.getJSONObject("data").getInt(
					UserGobal.USER_IS_ACTIVE);
			int vip_level = obj.getJSONObject("data").getInt(
					UserGobal.USER_VIP_LEVEL);
			String nickname = obj.getJSONObject("data").getString(
					UserGobal.USER_Nick_Name);
			String header = obj.getJSONObject("data").getString("header");
			Log.d("---userid-->" + userid, "--data---");
			UserVo user = new UserVo();
			user.setUid(userid);
			user.setAudit_type(baseobj.getInt("audit_type"));
			user.setMedal(baseobj.getString("medal"));
			user.setHeader(header);
			user.setActive(active);
			user.setNickname(nickname);
			user.setVip_level(vip_level);
			user.setSex(sex);
			
			shareUserInfo().saveLoginInfo(
					req_map_param.get(MConstantUser.UserReg.telephone),
					req_map_param.get(MConstantUser.UserReg.password));
			
			shareUserInfo().saveUserInfo(user);
			
		} catch (JSONException e) {
			handler.obtainMessage(-9).sendToTarget();
			e.printStackTrace();
		}

		return null;
	};

}
