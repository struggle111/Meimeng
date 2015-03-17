package com.swater.meimeng.activity.user;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.cell_lay_click;
import com.swater.meimeng.activity.adapterGeneral.vo.UserInfo;
import com.swater.meimeng.activity.adapterGeneral.UserAdapterItem;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.commbase.BaseTemplate.click_wheel;
import com.swater.meimeng.database.XmlDataOptions.PersonDataCato;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.constant.MConstantUser;

public class EditBodyStyle extends BaseTemplate implements cell_lay_click,
		click_wheel {
	ListView ls = null;
	UserAdapter adapter = null;
	String type_selected = "";
	int pod_selected = -1;
	String id_weight = "", id_face = "", id_body = "", id_hair, id_hair_color,
			id_eye_color, id_attract_part, id_self_score;
	UserInfo vo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_general_lay);
		TempRun();
	}

	@Override
	public void iniView() {
		vo = (UserInfo) this.getIntent().getSerializableExtra("vo");

		showTitle("修改外貌体型");
		showNavgationLeftBar("返回");
		showNavgationRightBar("保存");

		ls = findListView(R.id.user_ls);
		adapter = new UserAdapter(t_context);
		adapter.setClick_cell_event(this);
		
		List<UserAdapterItem> dataSrc = new ArrayList<UserAdapterItem>();
		UserAdapterItem item = null;
		for (int i = 0; i < 8; i++) {
			item = new UserAdapterItem();
			switch (i) {
			case 0: {
				item.setLeftStr("体重");
				item.setRightStr(vo.getVa3_weight());
			}
				break;
			case 1: {
				item.setLeftStr("脸型");
				item.setRightStr(vo.getVa3_face_style());
			}
				break;
			case 2: {
				item.setLeftStr("体型");
				item.setRightStr(vo.getVa3_body_style());
			}
				break;
			case 3: {
				item.setLeftStr("发型");
				item.setRightStr(vo.getVa3_hair_style());

			}
				break;
			case 4: {
				item.setLeftStr("发色");
				item.setRightStr(vo.getVa3_hair_color());
			}
				break;
			case 5: {
				item.setLeftStr("眼睛颜色");
				item.setRightStr(vo.getVa3_eye_color());
			}
				break;
			case 6: {
				item.setLeftStr("魅力部位");
				item.setRightStr(vo.getVa3_attract_place());
			}
				break;
			case 7: {
				item.setLeftStr("相貌自评");
				item.setRightStr(vo.getVa3_self_assert());
			}
				break;

			default:
				break;
			}
			dataSrc.add(item);

		}
		adapter.setObjs(dataSrc);
		ls.setAdapter(adapter);

		adapter.notifyDataSetChanged();
		setClick_yes_diag(this);

		inixmlOpts(t_context);
		JSONObject obj=null;
		try {
			if (null==vo.getJson_bodystyle()) {
				return;
			}
			obj = new JSONObject(vo.getJson_bodystyle()==null?"":vo.getJson_bodystyle());
			if (obj==null||obj.length()<2) {
				return;
			}
//			{"weight":{"id":1,"value":"40KG以下"},"body_type":{"id":1,"value":"苗条"},
//			"face_description":{"id":0,"value":""},"haircolor":{"id":1,"value":"黑色"},
//			"hairstyle":{"id":1,"value":"顺直长发"},"eye_color":{"id":1,"value":"黑色"},"" +
//					"face_type":{"id":1,"value":"圆形脸"},"good_position":{"id":1,"value":"笑容"}}
			id_attract_part=obj.getJSONObject("good_position").getInt("id")+"";
			id_body=obj.getJSONObject("body_type").getInt("id")+"";
			id_eye_color=obj.getJSONObject("eye_color").getInt("id")+"";
			id_face=obj.getJSONObject("face_type").getInt("id")+"";
			id_hair=obj.getJSONObject("hairstyle").getInt("id")+"";
			id_hair_color=obj.getJSONObject("haircolor").getInt("id")+"";
			id_self_score=obj.getJSONObject("face_description").getInt("id")+"";
			id_weight=obj.getJSONObject("weight").getInt("id")+"";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NSLoger.Log(obj.toString());

		

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
			submitBodyStyleInfo();
		}

			break;

		default:
			break;
		}

	}

	@Override
	public void Cell_lay_Click(View v, int pos) {
		this.pod_selected = pos;
		String leftName = ((UserAdapterItem) adapter.getObjs().get(pos))
				.getLeftStr();
		switch (pos) {
		case 0: {
			type_selected = Person_Type_height;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(leftName, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 1: {
			type_selected = Person_Type_face_type;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(leftName, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}
		}

			break;
		case 2: {
			type_selected = Person_Type_body_type;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(leftName, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 3: {
			type_selected = Person_Type_hairstyle;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(leftName, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 4: {
			type_selected = Person_Type_haircolor;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(leftName, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 5: {
			type_selected = Person_Type_eye_color;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(leftName, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;

		case 6: {
			type_selected = Person_Type_good_position;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(leftName, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 7: {
			type_selected = shareUserInfo().getUserInfo().getSex() == 1 ? Person_face_descp_man
					: Person_face_descp_woman;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			if (pdc == null) {
				pdc = reader_ops.getSingleCato(shareUserInfo().getUserInfo()
						.getSex() != 1 ? Person_face_descp_man
						: Person_face_descp_woman);
			}
			dialogView = getDialogView(leftName, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

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
			// showToast(value);
			((UserAdapterItem) adapter.getObjs().get(pod_selected))
					.setRightStr(value);
			adapter.notifyDataSetChanged();
			CallWheelDiagCancel();
			if (type_selected == Person_Type_attentionitem) {
				id_self_score = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_height) {

				id_weight = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_face_type) {
				id_face = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_body_type) {
				id_body = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_hairstyle) {
				id_hair = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_haircolor) {
				id_hair_color = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_eye_color) {

				id_eye_color = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);
			} else if (type_selected == Person_Type_good_position) {
				id_attract_part = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			}else if(type_selected==Person_face_descp_man||type_selected==Person_face_descp_woman){
				
				id_self_score=reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);
			}

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
//				showToast("保存成功！");
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

	void submitBodyStyleInfo() {
		try {

			ProShow("正在保存信息...");
			poolThread.submit(new Runnable() {

				@Override
				public void run() {
					req_map_param.put(MConstantUser.UserProperty.user_key,
							key_server);
					req_map_param.put(MConstantUser.UserProperty.uid,
							shareUserInfo().getUserid() + "");
					req_map_param.put(MConstantUser.UserProperty.weight,
							id_weight);
					req_map_param.put(MConstantUser.UserProperty.face_type,
							id_face);
					req_map_param.put(MConstantUser.UserProperty.body_type,
							id_body);
					req_map_param.put(MConstantUser.UserProperty.hairstyle,
							id_hair);
					req_map_param.put(MConstantUser.UserProperty.haircolor,
							id_hair_color);
					req_map_param.put(MConstantUser.UserProperty.eye_color,
							id_eye_color);
					req_map_param.put(MConstantUser.UserProperty.good_position,
							id_attract_part);
					req_map_param.put(MConstantUser.UserProperty.face_description,
							id_self_score);
					// req_map_param.put(MConstantUser.UserProperty.uid,
					// shareUserInfo().getUserid() + "");

					vo_resp = sendReq(MURL_user_apperanceinfo, req_map_param);
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

	};

}
