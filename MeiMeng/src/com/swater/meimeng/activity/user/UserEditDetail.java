package com.swater.meimeng.activity.user;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.vo.UserInfo;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.commbase.BaseTemplate.click_wheel;
import com.swater.meimeng.database.XmlDataOptions.PersonDataCato;
import com.swater.meimeng.mutils.constant.MConstantUser;

public class UserEditDetail extends BaseTemplate implements click_wheel {
	String type = "";
	UserInfo vo = null;
	// String id_nationlaty="";
	// String id_nationlaty="";
	// nationality false int 国籍ID
	// overseas_experience false int 海外经历ID
	// profession false int 职业ID
	// family false int 家庭背景ID
	// live false int 居住状况ID
	// car false int 购车状况ID
	// salary false int 月薪ID
	// assets_level false int 资产等级ID
	// zodiac false string 生肖
	// constellation false String 星座
	//
	RelativeLayout lay_xingzuo;
	RelativeLayout lay_shengxiao;
	RelativeLayout lay_guoji;
	RelativeLayout lay_oversea;
	RelativeLayout lay_career;
	RelativeLayout lay_family_bg;
	RelativeLayout lay_live;
	RelativeLayout lay_car;
	RelativeLayout lay_salary;
	RelativeLayout lay_assert;
	TextView txt_cd_guoji;
	TextView txt_cd_oversea;
	TextView txt_cd_career;
	TextView txt_cd_fami_bg;
	TextView txt_cd_live;
	TextView txt_cd_car;
	TextView txt_cd_salary;
	TextView txt_cd_assert;
	TextView txt_cd_shengxiao;
	TextView txt_cd_xingzuo;
	String id_guoji = "";
	String id_oversea = "";
	String id_career = "";
	String id_famil_bg = "";
	String id_car = "";
	String id_live = "";
	String id_assert_id = "";
	String id_salary = "";
	String shengxiao = "";
	String xingzuo = "";

	// String id_guoji="";
	// String id_guoji="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_edit_detail);
		TempRun();
		echoValue();
	}

	void iniLay() {

		// lay_1_cell_1
		iniShengxiao();
		iniXingzuo();
		lay_xingzuo = findLayRelative(R.id.lay_3_cell_3);
		lay_shengxiao = findLayRelative(R.id.lay_2_cell_3);
		lay_guoji = findLayRelative(R.id.lay_1_cell_1);
		lay_oversea = findLayRelative(R.id.lay_1_cell_2);
		lay_career = findLayRelative(R.id.lay_1_cell_3);
		lay_family_bg = findLayRelative(R.id.lay_1_cell_4);
		lay_live = findLayRelative(R.id.lay_1_cell_5);
		lay_car = findLayRelative(R.id.lay_1_cell_6);
		lay_salary = findLayRelative(R.id.lay_1_cell_7);
		lay_assert = findLayRelative(R.id.lay_1_cell_8);
		setClickEvent(lay_assert, lay_car, lay_career, lay_family_bg,
				lay_guoji, lay_live, lay_oversea, lay_salary, lay_shengxiao,
				lay_xingzuo);
		txt_cd_assert = findText(R.id.c_d_asset);
		txt_cd_car = findText(R.id.c_d_car);
		txt_cd_career = findText(R.id.c_d_prof);
		txt_cd_fami_bg = findText(R.id.c_d_fami_bg);
		txt_cd_guoji = findText(R.id.c_d_guoji);
		txt_cd_live = findText(R.id.c_d_live);
		txt_cd_oversea = findText(R.id.c_d_oversea);
		txt_cd_salary = findText(R.id.c_d_salary);
		txt_cd_shengxiao = findText(R.id.c_d_shengxiao);
		txt_cd_xingzuo = findText(R.id.c_d_xingzuo);
		if (shareUserInfo().getUserInfo().getSex() == 2) {
			view_Hide(lay_assert);
			view_Show(lay_salary);

		} else {
			view_Show(lay_assert);
			view_Hide(lay_salary);

		}
	}

	@Override
	public void iniView() {
		showTitle("修改详细信息");
		showNavgationLeftBar("返回");
		showNavgationRightBar("保存");
		inixmlOpts(t_context);
		iniLay();

	}

	private void echoValue() {
		vo = (UserInfo) this.getIntent().getSerializableExtra("vo");
		setValueToView(findViewById(R.id.c_d_asset), vo.getAssets_level());
		setValueToView(findViewById(R.id.c_d_car), vo.getCar());
		setValueToView(findViewById(R.id.c_d_fami_bg), vo.getFamily());
		setValueToView(findViewById(R.id.c_d_guoji), vo.getNationality());
		setValueToView(findViewById(R.id.c_d_live), vo.getLive());
		setValueToView(findViewById(R.id.c_d_oversea),
				vo.getOverseas_experience());
		setValueToView(findViewById(R.id.c_d_prof), vo.getProfession());
		setValueToView(findViewById(R.id.c_d_salary), vo.getSalary());
		setValueToView(findViewById(R.id.edit_blood_btn), vo.getBlood());
		setValueToView(findViewById(R.id.edit_ethinc_btn), vo.getMingzu());
		setValueToView(findViewById(R.id.c_d_shengxiao), vo.getShengxiao());
		setValueToView(findViewById(R.id.c_d_xingzuo), vo.getXingzuo());
		try {
			// ,"blood_type":{"value":"","id":0},"assets_level":"500W以下","ehtnic":{"value":"汉族","id":1},
			id_blood = new JSONObject(vo.getJson_detailinfo()).getJSONObject(
					"blood_type").getInt("id")
					+ "";
			id_ethic = new JSONObject(vo.getJson_detailinfo()).getJSONObject(
					"ehtnic").getInt("id")
					+ "";
			id_assert_id = vo.getId_assert();
			id_live = vo.getId_live();
			id_car = vo.getId_car();
			id_career = vo.getId_career();
			id_famil_bg = vo.getId_family_bg();
			id_guoji = vo.getId_nation();
			id_oversea = vo.getId_oversea();
			shengxiao = vo.getShengxiao();
			xingzuo = vo.getXingzuo();
			id_salary = vo.getId_salery();
		} catch (Exception e) {
			showToat(e.getMessage());
			// TODO: handle exception
		}
	}

	@Override
	public void bindClick() {
		setClick_yes_diag(this);
		bindNavgationEvent();
		setClickEvent(findViewById(R.id.edit_lay_blood),
				findViewById(R.id.edit_lay_ethnic));

	}

	PersonDataCato pdc_blood = null, pdc_ethinc = null;
	String[] shengxiao_arr = new String[12];
	String[] xingzuo_arr = new String[12];

	void iniShengxiao() {
		shengxiao_arr[0] = "鼠";
		shengxiao_arr[1] = "牛";
		shengxiao_arr[2] = "虎";
		shengxiao_arr[3] = "兔";
		shengxiao_arr[4] = "龙";
		shengxiao_arr[5] = "蛇";
		shengxiao_arr[6] = "马";
		shengxiao_arr[7] = "羊";
		shengxiao_arr[8] = "猴";
		shengxiao_arr[9] = "鸡";
		shengxiao_arr[10] = "狗";
		shengxiao_arr[11] = "猪";

	}

	void iniXingzuo() {
		xingzuo_arr[0] = "白羊座";
		xingzuo_arr[1] = "金牛座";
		xingzuo_arr[2] = "双子座";
		xingzuo_arr[3] = "巨蟹座";
		xingzuo_arr[4] = "狮子座";
		xingzuo_arr[5] = "处女座";
		xingzuo_arr[6] = "天秤座";
		xingzuo_arr[7] = "天蝎座";
		xingzuo_arr[8] = "射手座";
		xingzuo_arr[9] = "摩羯座";
		xingzuo_arr[10] = "水瓶座";
		xingzuo_arr[11] = "双鱼座";

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.lay_3_cell_3: {
			type = Person_Type_xingzuo;
			// pdc_blood = reader_ops.getSingleCato(type);
			View view = getDialogView("星座", xingzuo_arr);
			pushDiagWindow(view);

		}
			break;
		case R.id.lay_2_cell_3: {
			type = Person_Type_shengxiao;
			// pdc_blood = reader_ops.getSingleCato(type);
			View view = getDialogView("生肖", shengxiao_arr);
			pushDiagWindow(view);

		}
			break;
		case R.id.lay_1_cell_1: {
			type = Person_Type_nationality;
			pdc_blood = reader_ops.getSingleCato(type);
			View view = getDialogView("选择国籍", pdc_blood.getDataArray());
			pushDiagWindow(view);

		}
			break;
		case R.id.lay_1_cell_2: {
			type = Person_Type_overseas_experience;
			pdc_blood = reader_ops.getSingleCato(type);
			View view = getDialogView("海外经历", pdc_blood.getDataArray());
			pushDiagWindow(view);

		}
			break;
		case R.id.lay_1_cell_3: {
			type = Person_Type_profession;
			pdc_blood = reader_ops.getSingleCato(type);
			View view = getDialogView("职业", pdc_blood.getDataArray());
			pushDiagWindow(view);

		}
			break;
		case R.id.lay_1_cell_4: {
			type = Person_Type_family;
			pdc_blood = reader_ops.getSingleCato(type);
			View view = getDialogView("家庭背景", pdc_blood.getDataArray());
			pushDiagWindow(view);

		}
			break;
		case R.id.lay_1_cell_5: {
			type = Person_Type_live;
			pdc_blood = reader_ops.getSingleCato(type);
			View view = getDialogView("居住状况", pdc_blood.getDataArray());
			pushDiagWindow(view);

		}
			break;
		case R.id.lay_1_cell_6: {
			type = Person_Type_car;
			pdc_blood = reader_ops.getSingleCato(type);
			View view = getDialogView("购车状况", pdc_blood.getDataArray());
			pushDiagWindow(view);

		}
			break;
		case R.id.lay_1_cell_7: {
			type = Person_Type_salary;
			pdc_blood = reader_ops.getSingleCato(type);
			View view = getDialogView("月薪", pdc_blood.getDataArray());
			pushDiagWindow(view);
		}
			break;
		case R.id.lay_1_cell_8: {
			type = Person_Type_assets_level;
			pdc_blood = reader_ops.getSingleCato(type);
			View view = getDialogView("资产", pdc_blood.getDataArray());
			pushDiagWindow(view);

		}
			break;
		case R.id.home_left_btn: {
			sysback();

		}

			break;
		case R.id.home_right_btn: {

			saveInfo();

		}

			break;
		case R.id.edit_lay_blood: {
			type = Person_Type_blood_type;
			pdc_blood = reader_ops.getSingleCato(type);
			View view = getDialogView("选择血型", pdc_blood.getDataArray());
			pushDiagWindow(view);

		}

			break;
		case R.id.edit_lay_ethnic: {
			type = Person_Type_ehtnic;
			pdc_ethinc = reader_ops.getSingleCato(type);
			View view = getDialogView("选择名族", pdc_ethinc.getDataArray());
			pushDiagWindow(view);

		}

			break;

		default:
			break;
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
			if (type == Person_Type_ehtnic) {
				setValueToView(findViewById(R.id.edit_ethinc_btn), value);
				id_ethic = pdc_ethinc.getMaps_reverse().get(value);
			} else if (type == Person_Type_blood_type) {
				setValueToView(findViewById(R.id.edit_blood_btn), value);
				id_blood = pdc_blood.getMaps_reverse().get(value);
			}
			// -----
			else if (type == Person_Type_nationality) {
				setValueToView(txt_cd_guoji, value);
				id_guoji = pdc_blood.getMaps_reverse().get(value);
			} else if (type == Person_Type_profession) {
				setValueToView(txt_cd_career, value);
				id_career = pdc_blood.getMaps_reverse().get(value);
			} else if (type == Person_Type_car) {
				setValueToView(txt_cd_car, value);
				id_car = pdc_blood.getMaps_reverse().get(value);
			} else if (type == Person_Type_family) {
				setValueToView(txt_cd_fami_bg, value);
				id_famil_bg = pdc_blood.getMaps_reverse().get(value);
			} else if (type == Person_Type_salary) {
				setValueToView(txt_cd_salary, value);
				id_salary = pdc_blood.getMaps_reverse().get(value);
			} else if (type == Person_Type_overseas_experience) {
				setValueToView(txt_cd_oversea, value);
				id_oversea = pdc_blood.getMaps_reverse().get(value);
			} else if (type == Person_Type_assets_level) {
				setValueToView(txt_cd_assert, value);
				id_assert_id = pdc_blood.getMaps_reverse().get(value);
			} else if (type == Person_Type_live) {
				setValueToView(txt_cd_live, value);
				id_live = pdc_blood.getMaps_reverse().get(value);
			} else if (type == Person_Type_shengxiao) {
				setValueToView(txt_cd_shengxiao, value);
				shengxiao = value;

			} else if (type == Person_Type_xingzuo) {
				setValueToView(txt_cd_xingzuo, value);
				xingzuo = value;

			}

			CallWheelDiagCancel();
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
				showToast("保存成功！");
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

	String id_blood = "", id_ethic = "";

	void saveInfo() {
		if (TextUtils.isEmpty(id_blood)) {
			showToast("请先选择血型");
			return;
		}
		if (TextUtils.isEmpty(id_ethic)) {
			showToast("请先选择名族");
			return;
		}
		ProShow("正在保存信息...");

		try {
			poolThread.submit(new Runnable() {

				@Override
				public void run() {
					req_map_param.put(MConstantUser.UserProperty.uid,
							shareUserInfo().getUserid() + "");
					req_map_param.put(MConstantUser.UserProperty.user_key,
							key_server);
					req_map_param.put(MConstantUser.UserProperty.blood_type,
							id_blood);
					req_map_param.put(MConstantUser.UserProperty.ethnic_id,
							id_ethic);

					// ------

					// nationality false int 国籍ID
					// overseas_experience false int 海外经历ID
					// profession false int 职业ID
					// family false int 家庭背景ID
					// live false int 居住状况ID
					// car false int 购车状况ID
					// salary false int 月薪ID
					// assets_level false int 资产等级ID
					// zodiac false string 生肖
					// constellation false String 星座
					req_map_param.put("nationality", id_guoji);
					req_map_param.put("overseas_experience", id_oversea);
					req_map_param.put("profession", id_career);
					req_map_param.put("family", id_famil_bg);
					req_map_param.put("live", id_live);
					req_map_param.put("car", id_car);
					req_map_param.put("salary", id_salary);
					req_map_param.put("assets_level", id_assert_id);
					req_map_param.put("zodiac", shengxiao);
					req_map_param.put("constellation", xingzuo);
					req_map_param.put("salary", id_salary);
					vo_resp = sendReq(MURL_user_detailInfo, req_map_param);
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
	protected void onDestroy() {
		super.onDestroy();

		if (reader_ops != null) {
			reader_ops = null;
		}
	}
}
