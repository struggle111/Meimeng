package com.swater.meimeng.activity.user;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.cell_lay_click;
import com.swater.meimeng.activity.adapterGeneral.UserAdapterItem;
import com.swater.meimeng.activity.adapterGeneral.vo.DataVo;
import com.swater.meimeng.activity.adapterGeneral.vo.UserInfo;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.commbase.BaseTemplate.click_wheel;
import com.swater.meimeng.database.XmlDataOptions.PersonDataCato;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.constant.MConstantUser;

public class EditLifeInfo extends BaseTemplate implements cell_lay_click,
		click_wheel {

	ListView ls = null;
	UserAdapter adapter = null;
	String type_selected = "";
	int pod_selected = -1;
	String id_smoke, id_drink, id_take_execise, id_schedule, id_religion,
			id_family_ranking, id_with_parents, id_want_child,
			id_make_romantic, id_largest, id_pet, id_life_skills;
	BroadcastReceiver brc = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Object dv = (Object) intent.getSerializableExtra("pda");
			if (dv == null) {
				return;

			}
			if (dv instanceof DataVo) {
				DataVo data = (DataVo) dv;
				// ((UserAdapterItem) adapter.getObjs()
				// .get(((DataVo) dv).getPos())).setRightStr(((DataVo) dv)
				// .getValues());
				adapter.notifyDataSetChanged();
				String dtype = ((DataVo) dv).getType_date();

				if (dtype.equals(Person_Type_pet_type)) {
					id_pet = data.getIds();
				} else if (dtype.equals(Person_Type_life_skill)) {
					id_life_skills = data.getIds();
				}
				((UserAdapterItem) adapter.getObjs().get(pod_selected))
						.setRightStr(data.getValues());
				adapter.notifyDataSetChanged();

			} else {
				Log.d("--数据出错！---", "---");
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_general_lay);
		TempRun();
	}

	UserInfo info = null;

	@Override
	public void iniView() {
		info = (UserInfo) this.getIntent().getSerializableExtra("vo");
		try {
			JSONObject obj = new JSONObject(
					info.getJson_lifeinfo() == null ? ""
							: info.getJson_lifeinfo());
			if (obj == null || obj.length() < 2) {
				return;
			}
			NSLoger.Log(obj.toString());
			// {"religion":{"id":1,"value":"无宗教信仰"},"schedule":{"id":1,"value":"早睡早起很规律"}
			// ,"with_parents":{"id":1,"value":"愿意"},"largest_consumer":{"id":1,"value":"美食"},
			// "life_skill":[{"id":1,"value":"实现个人目标"},{"id":2,"value":"维护朋友圈关系"}],
			// "drinking":{"id":1,"value":"不喝"},"make_romantic":{"id":1,"value":"经常"},
			// "pet_value":[{"id":2,"value":"猫"},{"id":3,"value":"狗"},{"id":4,"value":"鸟"}]
			// ,"want_children":{"id":1,"value":"愿意"},
			// "family_ranking":{"id":1,"value":"独生子女"}
			// ,"smoke":{"id":1,"value":"不吸，很反感吸烟"},"take_exercise":{"id":1,"value":"每天锻炼"}}
			id_drink = obj.getJSONObject("drinking").getInt("id") + "";
			id_family_ranking = obj.getJSONObject("family_ranking")
					.getInt("id") + "";
			id_largest = obj.getJSONObject("largest_consumer").getInt("id")
					+ "";
			id_make_romantic = obj.getJSONObject("make_romantic").getInt("id")
					+ "";
			id_religion = obj.getJSONObject("religion").getInt("id") + "";
			id_schedule = obj.getJSONObject("schedule").getInt("id") + "";
			id_smoke = obj.getJSONObject("smoke").getInt("id") + "";
			id_take_execise = obj.getJSONObject("take_exercise").getInt("id")
					+ "";
			id_want_child = obj.getJSONObject("want_children").getInt("id")
					+ "";
			id_with_parents = obj.getJSONObject("with_parents").getInt("id")
					+ "";
			// id_pet=obj.getJSONObject("").getInt("id")+"";
			// id_life_skills=obj.getJSONObject("life_skill").getInt("id")+"";
			JSONArray arr_pet = obj.getJSONArray("life_skill");
			StringBuilder sb1 = new StringBuilder("");
			for (int i = 0; i < arr_pet.length(); i++) {
				sb1.append(arr_pet.getJSONObject(i).getInt("id") + ",");

			}
			id_pet = sb1.toString();
			JSONArray arr_skill = obj.getJSONArray("life_skill");
			StringBuilder sb2 = new StringBuilder("");
			for (int i = 0; i < arr_skill.length(); i++) {
				sb2.append(arr_pet.getJSONObject(i).getInt("id") + ",");

			}
			id_life_skills = sb2.toString();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		registerReceiver(brc, new IntentFilter(CMD_EDIT_MULTI_CHBOX));

		showTitle("修改生活信息");
		showNavgationLeftBar("返回");
		showNavgationRightBar("保存");

		ProShow("初始化信息...");

		ls = findListView(R.id.user_ls);
		adapter = new UserAdapter(t_context);
		adapter.setClick_cell_event(this);

		List<UserAdapterItem> dataSrc = new ArrayList<UserAdapterItem>();
		UserAdapterItem item = null;
		for (int i = 0; i < 12; i++) {
			item = new UserAdapterItem();
			switch (i) {
			case 0: {
				item.setLeftStr("是否吸烟");
				item.setRightStr(info.getVa5_issmoke());
			}
				break;
			case 1: {
				item.setLeftStr("是否饮酒");
				item.setRightStr(info.getVa5_drink());
			}
				break;
			case 2: {
				item.setLeftStr("锻炼习惯");
				item.setRightStr(info.getVa5_execise());
			}
				break;
			case 3: {
				item.setLeftStr("作息习惯");
				item.setRightStr(info.getVa5_rest());
			}
				break;
			case 4: {
				item.setLeftStr("宗教信仰");
				item.setRightStr(info.getVa5_zongjiao());
			}
				break;
			case 5: {
				item.setLeftStr("家中排行");
				item.setRightStr(info.getVa5_rank());
			}
				break;
			case 6: {
				item.setLeftStr("愿意与Ta父母同住");
				item.setRightStr(info.getVa5_livewith());
			}
				break;
			case 7: {
				item.setLeftStr("是否要孩子");
				item.setRightStr(info.getVa5_iswantchild());
			}
				break;
			case 8: {
				item.setLeftStr("制造浪漫");
				item.setRightStr(info.getVa5_makeroma());
			}
				break;
			case 9: {
				item.setLeftStr("最大消费");
				item.setRightStr(info.getVa5_big_lar());
			}
				break;

			case 10: {
				item.setRightStr(info.getVa5_pets());
				item.setLeftStr("宠物类型");
			}
				break;

			case 11: {
				item.setLeftStr("擅长生活技能");
				item.setRightStr(info.getVa5_skills());
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
		ProDimiss();

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
			submitInfo();
		}

			break;

		default:
			break;
		}

	}

	protected void onDestroy() {
		super.onDestroy();

		if (null != brc) {
			unregisterReceiver(brc);

		}
		if (handler != null) {
			handler = null;
		}
		if (reader_ops != null) {
			reader_ops = null;
		}
	};

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

	void submitInfo() {
		try {
			ProShow("正在保存信息...");
			poolThread.submit(new Runnable() {

				@Override
				public void run() {
					req_map_param.put(MConstantUser.UserProperty.user_key,
							key_server);
					req_map_param.put(MConstantUser.UserProperty.uid,
							shareUserInfo().getUserid() + "");
					req_map_param.put(MConstantUser.UserProperty.smoke,
							id_smoke);
					req_map_param.put(MConstantUser.UserProperty.drinking,
							id_drink);
					req_map_param.put(MConstantUser.UserProperty.take_exercise,
							id_take_execise);
					req_map_param.put(MConstantUser.UserProperty.schedule,
							id_schedule);
					req_map_param.put(MConstantUser.UserProperty.religion,
							id_religion);
					req_map_param.put(
							MConstantUser.UserProperty.family_ranking,
							id_family_ranking);
					req_map_param.put(MConstantUser.UserProperty.with_parents,
							id_with_parents);
					req_map_param.put(MConstantUser.UserProperty.want_children,
							id_want_child);
					req_map_param.put(MConstantUser.UserProperty.make_romantic,
							id_make_romantic);
					req_map_param.put(
							MConstantUser.UserProperty.largest_consumer,
							id_largest);
					req_map_param.put(MConstantUser.UserProperty.pet_value,
							id_pet);
					req_map_param.put(MConstantUser.UserProperty.life_skill,
							id_life_skills);
					Log.d("--pets-->" + id_pet, "");
					Log.d("--skills-->" + id_life_skills, "");
					// req_map_param.put(MConstantUser.UserProperty.with_parents,
					// id_with_parents);

					vo_resp = sendReq(MURL_user_LifeInfo, req_map_param);
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

	@Override
	public void Cell_lay_Click(View v, int pos) {
		this.pod_selected = pos;
		adapter.setPos_selected_pos(pos);
		String name = ((UserAdapterItem) adapter.getObjs().get(pos))
				.getLeftStr();
		switch (pos) {
		case 0: {
			type_selected = Person_Type_smoke;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(name, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 1: {
			type_selected = Person_Type_drinking;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(name, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}
		}

			break;

		case 2: {
			type_selected = Person_Type_take_exercise;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(name, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 3: {
			type_selected = Person_Type_schedule;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(name, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 4: {
			type_selected = Person_Type_religion;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(name, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 5: {
			type_selected = Person_Type_family_ranking;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(name, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 6: {
			type_selected = Person_Type_with_parents;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(name, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 7: {
			type_selected = Person_Type_want_children;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(name, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 8: {
			type_selected = Person_Type_make_romantic;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(name, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 9: {
			type_selected = Person_Type_largest_consumer;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView(name, pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 10: {
			type_selected = Person_Type_pet_type;
			Intent in = new Intent(t_context, MultiViewChoice.class);
			in.putExtra("type", type_selected);
			t_context.startActivity(in);

			// PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			// dialogView = getDialogView(name, pdc.getDataArray());
			// if (null != dialogView) {
			// pushDiagWindow(dialogView);
			//
			// }
			//
		}
			break;
		case 11: {
			// PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			// dialogView = getDialogView(name, pdc.getDataArray());
			// if (null != dialogView) {
			// pushDiagWindow(dialogView);
			//
			// }
			// jumpOtherActivity(MultiViewChoice.class);
			type_selected = Person_Type_life_skill;
			Intent in = new Intent(t_context, MultiViewChoice.class);
			in.putExtra("type", type_selected);
			t_context.startActivity(in);
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

			((UserAdapterItem) adapter.getObjs().get(pod_selected))
					.setRightStr(value);
			adapter.notifyDataSetChanged();
			CallWheelDiagCancel();

			if (type_selected == Person_Type_smoke) {
				id_smoke = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_drinking) {

				id_drink = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_take_exercise) {
				id_take_execise = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_schedule) {
				id_schedule = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_religion) {
				id_religion = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_family_ranking) {
				id_family_ranking = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_with_parents) {
				id_with_parents = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_want_children) {
				id_want_child = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_make_romantic) {
				id_make_romantic = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_largest_consumer) {
				id_largest = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_pet_type) {
				// // id_pet = reader_ops.getSingleCato(type_selected)
				// .getMaps_reverse().get(value);

			}
			// else if (type_selected == Person_Type_life_skill) {
			// id_life_skills = reader_ops.getSingleCato(type_selected)
			// .getMaps_reverse().get(value);
			//
			// }

		}

			break;

		default:
			break;
		}

	}
}
