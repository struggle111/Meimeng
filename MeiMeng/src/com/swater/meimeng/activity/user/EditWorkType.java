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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.cell_lay_click;
import com.swater.meimeng.activity.adapterGeneral.vo.DataVo;
import com.swater.meimeng.activity.adapterGeneral.vo.UserInfo;
import com.swater.meimeng.activity.adapterGeneral.UserAdapterItem;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.commbase.BaseTemplate.click_wheel;
import com.swater.meimeng.database.XmlDataOptions.PersonDataCato;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.constant.MConstantUser;

public class EditWorkType extends BaseTemplate implements cell_lay_click,
		click_wheel {
	ListView ls = null;
	UserAdapter adapter = null;
	String type_selected = "";
	int pod_selected = -1;
	UserInfo uv = null;
	String id_trade, id_company_type, id_work_state, id_income, id_grad_year,
			id_profes, id_languages;

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

				if (dtype.equals(Person_Type_language)) {
					id_languages = data.getIds();
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
		registerReceiver(brc, new IntentFilter(CMD_EDIT_MULTI_CHBOX));

	}

	@Override
	public void iniView() {
		// LocalBroadcastManager.getInstance(t_context).registerReceiver(brc,
		// arg1)

		showTitle("修改工作信息");
		showNavgationLeftBar("返回");
		showNavgationRightBar("保存");
		uv = (UserInfo) this.getIntent().getSerializableExtra("vo");
		try {
			JSONObject obj = new JSONObject(uv.getJson_workinfo());
			// {"work_status":{"id":1,"value":"轻松稳定"},"graduation_time":{"id":1,"value":"在读"},"company_trade":{"id":1,"value":"IT\/通信\/电子\/互联网"},"language":[{"id":1,"value":"中文(普通话)
			// "}],"income":{"id":1,"value":"福利优越"},"company_type":{"id":1,"value":"政府机关"},"profession":{"id":1,"value":"计算机类"}}
			id_trade = obj.getJSONObject("company_type").getInt("id") + "";
			id_grad_year = obj.getJSONObject("graduation_time").getInt("id")
					+ "";
			id_income = obj.getJSONObject("income").getInt("id") + "";
			id_profes = obj.getJSONObject("profession").getInt("id") + "";
			id_company_type = obj.getJSONObject("company_trade").getInt("id")
					+ "";
			id_work_state = obj.getJSONObject("work_status").getInt("id") + "";
			if (!obj.isNull("language")) {
				JSONArray arr = obj.getJSONArray("language");
				StringBuilder sb = new StringBuilder("");
				for (int i = 0; i < arr.length(); i++) {
					if (i > 0) {
						sb.append(",");
					}
					sb.append(arr.getJSONObject(i).getInt("id"));

				}
				id_languages = sb.toString();

			}
			NSLoger.Log(obj.toString());
		} catch (Exception e) {
			// showToast(""+e.getMessage());
			e.printStackTrace();
		}

		ls = findListView(R.id.user_ls);
		adapter = new UserAdapter(t_context);
		adapter.setClick_cell_event(this);

		List<UserAdapterItem> dataSrc = new ArrayList<UserAdapterItem>();
		UserAdapterItem item = null;
		for (int i = 0; i < 7; i++) {
			item = new UserAdapterItem();
			switch (i) {
			case 0: {
				item.setLeftStr("公司行业");
				item.setRightStr(uv.getVa4_comp_type());
			}
				break;
			case 1: {
				item.setLeftStr("公司类型");
				item.setRightStr(uv.getVa4_comp_trade());
			}
				break;
			case 2: {
				item.setLeftStr("工作状态");
				item.setRightStr(uv.getVa4_job_state());
			}
				break;
			case 3: {
				item.setLeftStr("收入描述");
				item.setRightStr(uv.getVa4_income_desc());
			}
				break;
			case 4: {
				item.setLeftStr("毕业年份");
				item.setRightStr(uv.getVa4_grad_year());
			}
				break;
			case 5: {
				item.setLeftStr("专业类型");
				item.setRightStr(uv.getVa4_prof_type());
			}
				break;
			case 6: {
				item.setLeftStr("语言能力");
				item.setRightStr(uv.getVa4_lang());
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

	@Override
	public void Cell_lay_Click(View v, int pos) {
		this.pod_selected = pos;
		switch (pos) {
		case 0: {
			type_selected = Person_Type_company_trade;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView("选择行业", pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 1: {
			type_selected = Person_Type_company_type;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView("选择公司类型", pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}
		}

			break;
		case 2: {
			type_selected = Person_Type_work_status;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView("工作状态", pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 3: {
			type_selected = Person_Type_income;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView("选择收入", pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 4: {
			type_selected = Person_Type_graduation_time;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView("毕业年份", pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 5: {
			type_selected = Person_Type_profession;
			PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			dialogView = getDialogView("专业类型", pdc.getDataArray());
			if (null != dialogView) {
				pushDiagWindow(dialogView);

			}

		}

			break;
		case 6: {
			type_selected = Person_Type_language;
			// PersonDataCato pdc = reader_ops.getSingleCato(type_selected);
			// dialogView = getDialogView("语言能力", pdc.getDataArray());
			// if (null != dialogView) {
			// pushDiagWindow(dialogView);
			//
			// }

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
			CallWheelDiagCancel();
			((UserAdapterItem) adapter.getObjs().get(pod_selected))
					.setRightStr(value);
			adapter.notifyDataSetChanged();

			if (type_selected == Person_Type_company_trade) {
				id_trade = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_company_type) {

				id_company_type = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_work_status) {
				id_work_state = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_graduation_time) {
				id_grad_year = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_profession) {
				id_profes = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			} else if (type_selected == Person_Type_language) {
				id_languages = reader_ops.getSingleCato(type_selected)
						.getMaps_reverse().get(value);

			}

			else if (type_selected == Person_Type_income) {
				id_income = reader_ops.getSingleCato(type_selected)
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
					req_map_param.put(MConstantUser.UserProperty.company_trade,
							id_trade);
					req_map_param.put(MConstantUser.UserProperty.company_type,
							id_company_type);
					req_map_param.put(MConstantUser.UserProperty.work_status,
							id_work_state);
					req_map_param.put(MConstantUser.UserProperty.income,
							id_income);
					req_map_param.put(
							MConstantUser.UserProperty.graduation_time,
							id_grad_year);
					req_map_param.put(MConstantUser.UserProperty.profession,
							id_profes);
					req_map_param.put(MConstantUser.UserProperty.language,
							id_languages);
					// req_map_param.put(MConstantUser.UserProperty.uid,
					// shareUserInfo().getUserid() + "");

					vo_resp = sendReq(MURL_user_workinfo, req_map_param);
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (null != brc) {
			unregisterReceiver(brc);

		}
	}

}
