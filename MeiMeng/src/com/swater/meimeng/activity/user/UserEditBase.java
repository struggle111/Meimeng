package com.swater.meimeng.activity.user;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.UserAdapterItem;
import com.swater.meimeng.activity.adapterGeneral.vo.UserInfo;
import com.swater.meimeng.activity.newtabMain.TabVip;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.commbase.DiagPopView;
import com.swater.meimeng.commbase.BaseTemplate.click_wheel;
import com.swater.meimeng.commbase.DiagPopView.Diag_ok_no;
import com.swater.meimeng.database.Map_Province.CityVo;
import com.swater.meimeng.database.Map_Province.ProvinceVo;
import com.swater.meimeng.database.XmlDataCity;
import com.swater.meimeng.database.XmlDataOptions.PersonDataCato;
import com.swater.meimeng.mutils.constant.MConstantUser;
import com.swater.meimeng.mutils.diagutil.Tools;
import com.swater.meimeng.mutils.wheelview.ArrayWheelAdapter;
import com.swater.meimeng.mutils.wheelview.OnWheelChangedListener;
import com.swater.meimeng.mutils.wheelview.WheelView;

//修改基本信息
public class UserEditBase extends BaseTemplate implements click_wheel, Diag_ok_no{
	String city_id = "";
	private WheelView country, city;
	List<ProvinceVo> ls_pro = null;
	private static String countries[];
	private String cities[][];
	XmlDataCity reader = null;
	Dialog city_diag = null;
	UserInfo vo = null;
	RelativeLayout lay_age;
	RelativeLayout lay_height;
	RelativeLayout lay_marriage;
	RelativeLayout lay_ischild;
	RelativeLayout lay_degree;
	TextView base_age;
	EditText base_height;
	TextView base_marriage;
	TextView base_ischild;
	TextView base_degree;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_edit_base);
		TempRun();
		echoValue();
	}

	private void echoValue() {
		vo = (UserInfo) this.getIntent().getSerializableExtra("vo");

		if (vo.getNickName() == null || "null".equals(vo.getNickName())) {
			findEditText(R.id.edit_honey).setHint("");
		} else {

			setValueToView(
					findViewById(R.id.edit_honey),
					vo.getNickName() == null || "null".equals(vo.getNickName()) ? "还没有昵称哦!"
							: vo.getNickName());
		}

		setValueToView(findViewById(R.id.edit_city_btn), vo.getCity_name());
		setValueToView(findViewById(R.id.base_age), vo.getAge());
		setValueToView(findViewById(R.id.base_grad), vo.getDegree());
		// setValueToView(findViewById(R.id.base_heigt),
		// vo.getHeight()==null||"null".equals(vo.getHeight())?"保密" :
		// vo.getHeight()+"CM");
		
		setValueToView(findViewById(R.id.base_heigt), vo.getHeight() == null
				|| "null".equals(vo.getHeight()) ? "" : vo.getHeight() + "厘米");
		setValueToView(findViewById(R.id.base_son), vo.getChild());
		setValueToView(findViewById(R.id.base_marr), vo.getMarriage());
		if (!isBlank(vo.getHeight())) {
			findViewById(R.id.base_heigt).setFocusable(false);
//			findViewById(R.id.base_heigt).setFocusable(false);
			
		}

		id_age = vo.getAge();
		id_degree = vo.getId_degree();
		id_marriage = vo.getId_marriage();

		id_child = vo.getId_child();
		if (!TextUtils.isEmpty(vo.getJson_base_info())) {
			try {
				city_id = new JSONObject(vo.getJson_base_info()).getJSONObject(
						"city").getInt("city_id")
						+ "";
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void iniView() {
		lay_age = findLayRelative(R.id.lay_1_cell_1);
		lay_height = findLayRelative(R.id.lay_1_cell_2);
		lay_marriage = findLayRelative(R.id.lay_1_cell_3);
		lay_ischild = findLayRelative(R.id.lay_1_cell_4);
		lay_degree = findLayRelative(R.id.lay_1_cell_5);
		base_age = findText(R.id.base_age);
		base_degree = findText(R.id.base_grad);
		base_height = (EditText) findViewById(R.id.base_heigt);
		base_ischild = findText(R.id.base_son);
		base_marriage = findText(R.id.base_marr);
		setClickEvent(lay_age, lay_degree, lay_height, lay_ischild,
				lay_marriage);
		showTitle("基本信息");
		showNavgationLeftBar("返回");
		showNavgationRightBar("保存");
		iniCityData();
		iniAge();
		inixmlOpts(t_context);
		setClick_yes_diag(this);

	}

	void showCityDiag() {
		View wheelView = getCityDialogView();
		country.setAdapter(new ArrayWheelAdapter<String>(countries));
		country.setCurrentItem(0);
		city.setAdapter(new ArrayWheelAdapter<String>(cities[0]));
		city.setCurrentItem(cities[0].length / 2);
		city_diag = Tools.pull_Dialog(t_context, wheelView);
	}

	void iniCityData() {

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

	Button btn_city_yes = null, btn_city_no = null;

	public View getCityDialogView() {
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
				city.setCurrentItem(cities[newValue].length / 2);

			}
		});

		city = (WheelView) viewMain.findViewById(R.id.city);

		return viewMain;
	}

	@Override
	public void bindClick() {
		bindNavgationEvent();
		setClickEvent(findViewById(R.id.edit_city_lay));

	}

	void CancelDiagCity() {
		if (city_diag != null || city_diag.isShowing()) {
			city_diag.cancel();
		}
	}

	String arr_times[] = null;
	int age_size_begin = 17;
	int age_size_end = 60;
	int pos_ted = 0;

	String id_age = "";
	String id_height = "";
	String id_marriage = "";
	String id_degree = "";
	String id_child = "";
	Dialog user_diag = null;
	String type_selected = "";

	void iniAge() {
		arr_times = new String[43];
		// arr_asserts = new String[36];
		for (int i = 0; i < arr_times.length; i++) {
			age_size_begin++;
			arr_times[i] = age_size_begin + "";

		}
	}

	void CancelWheel() {
		if (null != user_diag) {
			user_diag.dismiss();
		}
	}

	boolean isBlank(String s) {

		if (s == null || "null".equals(s) ||"0".equals(s) || TextUtils.isEmpty(s)) {
			return true;

		} else {
			return false;
		}
	}

//	setValueToView(findViewById(R.id.edit_city_btn), vo.getCity_name());
//	setValueToView(findViewById(R.id.base_age), vo.getAge());
//	setValueToView(findViewById(R.id.base_grad), vo.getDegree());
//	// setValueToView(findViewById(R.id.base_heigt),
//	// vo.getHeight()==null||"null".equals(vo.getHeight())?"保密" :
//	// vo.getHeight()+"CM");
//	
//	setValueToView(findViewById(R.id.base_heigt), vo.getHeight() == null
//			|| "null".equals(vo.getHeight()) ? "" : vo.getHeight() + "厘米");
//	setValueToView(findViewById(R.id.base_son), vo.getChild());
//	setValueToView(findViewById(R.id.base_marr), vo.getMarriage());
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

//		case R.id.lay_1_cell_1: {
//			if (!isBlank(id_age)) {
//				popCall();
//				return;
//			}else{
//				
//				type_selected = Person_Type_age;
//				View wheelView = getDialogView("选择年龄", arr_times);
//				user_diag = Tools.pull_Dialog(t_context, wheelView);
//				
//			}
//
//		}
//			break;
		case R.id.lay_1_cell_2: {

		}
			break;
		case R.id.lay_1_cell_3: {
			//if (!isBlank(id_marriage)) {
			if (!isBlank(getValueView(findViewById(R.id.base_marr)))) {
				popCall();
				return;
			}else{
				
				
				type_selected = Person_Type_marrage;
				PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
				dialogView = getDialogView("婚姻状况", pdc.getDataArray());
				if (null != dialogView) {
					pushDiagWindow(dialogView);
					
				}
			}

		}
			break;
		case R.id.lay_1_cell_4: {
			//if (!isBlank(id_child)) {
			
			if (!isBlank(getValueView(findViewById(R.id.base_son)))) {
		//	if (!isBlank(id_child)) {
				popCall();
				return;
			}else{
				
				
				type_selected = Person_Type_child;
				PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
				dialogView = getDialogView("有无子女", pdc.getDataArray());
				if (null != dialogView) {
					pushDiagWindow(dialogView);
					
				}
			}

		}
			break;
		case R.id.lay_1_cell_5: {
			//if (!isBlank(id_degree)) {
			if (!isBlank(getValueView(findViewById(R.id.base_grad)))) {
				popCall();
				return;
			}else{
				
				type_selected = Person_Type_degree;
				PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
				dialogView = getDialogView("学历", pdc.getDataArray());
				if (null != dialogView) {
					pushDiagWindow(dialogView);
					
				}
			}

		}
			break;
		case R.id.edit_city_lay: {
			showCityDiag();

		}
			break;
		// 取消
		case R.id.wh_no: {
			CancelDiagCity();
			if (user_diag != null) {
				user_diag.dismiss();
			}

		}
			break;
		case R.id.wh_yes: {
			CancelDiagCity();

			String value_city = cities[country.getCurrentItem()][city
					.getCurrentItem()];
			city_id = reader.getCityIdByName(value_city);

			setValueToView(findViewById(R.id.edit_city_btn), value_city);
		}
			break;
		case R.id.home_left_btn: {
			sysback();
			

		}

			break;
		case R.id.home_right_btn: {
			submitBasicInfo();
		}

			break;

		default:
			break;
		}

	}
	DiagPopView diagpopview = null;

	void popCall() {
		if (diagpopview == null) {

			diagpopview = new DiagPopView(t_context, "拨打服务热线" + "400-8783-520");
			diagpopview.setLeftMsg("确定");
			diagpopview.setOnHeaderItemClick(this);
		}
		diagpopview.showAsDropDown(findViewById(R.id.topNav));

	}

	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			ProDimiss();
			switch (msg.what) {
			case Resp_action_ok: {
				// showToast("保存成功！");
				sysback();
				sendBroadcast(new Intent(CMD_FRESH_ALL_DATA));
			}

				break;
			case Resp_action_fail: {
				showToast("保存失败！" + mg2String(msg));

			}

				break;
			case Resp_exception: {
				showToast("服务器异常！");

			}

				break;

			default:
				break;
			}
		};
	};

	void submitBasicInfo() {
		try {
			final String honey_value = getValueView(findViewById(R.id.edit_honey));
			if (TextUtils.isEmpty(honey_value)) {
				showToast("请先填写昵称！");
				return;
			}
			if (TextUtils.isEmpty(city_id)) {
				showToast("城市信息不能为空！");
				return;
			}

			ProShow("正在保存信息...");
			poolThread.submit(new Runnable() {

				@Override
				public void run() {
					req_map_param.put(MConstantUser.UserProperty.user_key,
							key_server);
					req_map_param.put(MConstantUser.UserProperty.uid,
							shareUserInfo().getUserid() + "");
					req_map_param.put(MConstantUser.UserProperty.nickname,
							honey_value);
					req_map_param.put(MConstantUser.UserProperty.city_id,
							city_id);
					//
					// age false int 年龄值，仅允许填写一次
					// height false int 身高值，仅允许填写一次
					// marriage false int 婚姻状况，见配置，仅允许填写一次
					// degree false int 学历，见配置，仅允许填写一次
					// child false int 有无子女，见配置，仅允许填写一次

					id_height = getValueView(base_height);
					if (!TextUtils.isEmpty(id_height)) {
						if (id_height.contains("厘米")) {
							id_height.replace("厘米", "");

						}

					}
					req_map_param.put("age", id_age);
					req_map_param.put("height", id_height);
					req_map_param.put("marriage", id_marriage);
					req_map_param.put("degree", id_degree);
					req_map_param.put("child", id_child);

					vo_resp = sendReq(MURL_user_basicinfo, req_map_param);
					if (vo_resp == null) {
						handler.obtainMessage(Resp_action_fail, "无法连接服务器")
								.sendToTarget();
					}
					if (vo_resp.isHasError()) {
						handler.obtainMessage(Resp_action_fail,
								vo_resp.getErrorDetail()).sendToTarget();

					} else {

						handler.obtainMessage(Resp_action_ok).sendToTarget();
					}

				}
			});

		} catch (Exception e) {
			handler.obtainMessage(Resp_exception).sendToTarget();
		}

	}

	@Override
	public void click_yes_or_no(View v, int index, String value) {

		switch (v.getId()) {
		case R.id.wh_no: {
			CallWheelDiagCancel();

		}

			break;
		case R.id.wh_yes: {
			CallWheelDiagCancel();
			// ((UserAdapterItem) adapter.getObjs().get(pos_ted))
			// .setRightStr(value);
			// adapter.notifyDataSetChanged();
			// id_age=data_s.get(2).getRightStr().trim();

			if (type_selected == Person_Type_child) {
				id_child = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);
				setValueToView(base_ischild, value);

			} else if (type_selected == Person_Type_degree) {
				id_degree = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);
				setValueToView(base_degree, value);

			} else if (type_selected == Person_Type_marrage) {
				id_marriage = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);
				setValueToView(base_marriage, value);

			} else if (type_selected == Person_Type_age) {
				// id_marriage = reader_ops.getSingleCato(type_selected)
				// .getMaps_reverse().get(value);
				// showToast("--age-->>>"+value);
				if (!"null".equals(value) && !TextUtils.isEmpty(value)) {
					id_age = value.trim();

				}

				setValueToView(base_age, value);

				if (null != user_diag) {
					user_diag.dismiss();
				}

			}

		}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (reader != null) {
			reader = null;
		}
		if (reader_ops != null) {
			reader_ops.ClearAllCache();
			reader_ops = null;
		}
	}
	void callTel() {

		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ "4008783520"));
		UserEditBase.this.startActivity(intent);// 内部类
	}
	@Override
	public void btn_ok_no(View v) {
		if (diagpopview != null) {
			diagpopview.dismiss();
		}
		if (v.getId() == R.id.app_yes) {
			callTel();

		}		
	}

}
