package com.swater.meimeng.activity.user;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.cell_lay_click;
import com.swater.meimeng.activity.adapterGeneral.UserAdapterItem;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.User_Ada_Type;
import com.swater.meimeng.commbase.BaseTemplate;

public class UserSetForbidenUser extends BaseTemplate implements cell_lay_click {
	UserAdapter adapter = null;
	ListView ls = null;
	List<UserAdapterItem> data = new ArrayList<UserAdapterItem>();
	BroadcastReceiver broad = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(CMD_FRESH_SHILED_LIST)) {
				TempRun();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_setting_layout);
		registerReceiver(broad, new IntentFilter(CMD_FRESH_SHILED_LIST));
		
		TempRun();
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broad);
	}

	@Override
	public void iniView() {
		showNavgationLeftBar("返回");
		showNavgationRightBar("添加");
		showTitle("屏蔽名单");
		ls = findListView(R.id.ls_set);
		adapter = new UserAdapter(t_context);
		adapter.setClick_cell_event(this);
		adapter.setType(User_Ada_Type.type_user_set_forbdden_user_cell);
		for (int i = 0; i < 3; i++) {
			UserAdapterItem it = new UserAdapterItem();
			switch (i) {
			case 0: {

				it.setLeftStr("15882399");
			}

				break;
			case 1: {

				it.setLeftStr("xxxx");
			}

				break;
			case 2: {
				it.setLeftStr("yyyyyy");

			}

				break;

			default:
				break;
			}
			// data.add(it);

		}
		adapter.setObjs(data);
		ls.setAdapter(adapter);
		getData();

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ProDimiss();

			switch (msg.what) {
			case Resp_exception: {
				showToat("获取异常！");
			}

				break;
			case Resp_action_fail: {
//				showToast("服务器异常！" + mg2String(msg));
			}

				break;
			case Resp_action_ok: {
				parselList();
			}

				break;
			case Resp_action_Empty: {
				showToast("拉取数据失败!");

			}

				break;

			case ACTION_TAG1: {
				showToast("操作成功！!");

			}

				break;
			case ACTION_TAG3: {
				showToast("操作成功！!");
				TempRun();

			}

				break;

			default:
				break;
			}
		};

	};

	void parselList() {

		if (vo_resp == null) {
			return;
		} else {

			String resp = vo_resp.getResp();
			try {

				JSONObject obj = new JSONObject(resp).getJSONObject("data");
				JSONArray arr = obj.getJSONArray("keywords");
				data.clear();
				for (int i = 0; i < arr.length(); i++) {
					UserAdapterItem it = new UserAdapterItem();
					it.setLeftStr(arr.getJSONObject(i).getString("keyword"));
					data.add(it);
				}
				adapter.notifyDataSetChanged();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	void releaseShield(String releaseName) {

		ProShow("正在解除屏蔽...");
		req_map_param.put("uid", shareUserInfo().getUserid() + "");
		req_map_param.put("key", key_server);
		req_map_param.put("keyword", releaseName);
		poolThread.submit(new Runnable() {

			@Override
			public void run() {
				try {
					vo_resp = sendReq(MURL_PERMISSION_DEL_SHIELD, req_map_param);
					if (null == vo_resp) {
						handler.obtainMessage(Resp_exception).sendToTarget();

					} else {
						if (vo_resp.isHasError()) {
							handler.obtainMessage(Resp_action_fail,
									vo_resp.getErrorDetail()).sendToTarget();

						} else {
							handler.obtainMessage(ACTION_TAG3).sendToTarget();
						}
					}

				} catch (Exception e) {
					handler.obtainMessage(Resp_exception).sendToTarget();

				}

			}
		});
	}

	// key true string 参见规范中的key值
	// uid true int 用户ID
	// permission true int 权限,1-对所有人可见(默认);2-对所有人关闭;3-对想见的人和同意查看的人可见;
	void getData() {
		ProShow("正在获取设置...");
		poolThread.submit(new Runnable() {

			@Override
			public void run() {
				try {
					req_map_param.put("uid", shareUserInfo().getUserid() + "");
					req_map_param.put("key", key_server);
					vo_resp = sendReq(MURL_PERMISSION_SHIELDLIST, req_map_param);
					if (null == vo_resp) {
						handler.obtainMessage(Resp_exception).sendToTarget();

					} else {
						if (vo_resp.isHasError()) {
							handler.obtainMessage(Resp_action_fail,
									vo_resp.getErrorDetail()).sendToTarget();

						} else {
							handler.obtainMessage(Resp_action_ok)
									.sendToTarget();
						}
					}

				} catch (Exception e) {
					handler.obtainMessage(Resp_exception).sendToTarget();

				}

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_left_btn: {
			sysback();

		}

			break;

		case R.id.home_right_btn: {
			jumpOtherActivity(addUserForden.class);

		}

			break;

		default:
			break;
		}

	}

	@Override
	public void bindClick() {
		bindNavgationEvent();

	}

	private void showConfirm(int pos) {
		final int poss = pos;
		AlertDialog.Builder builder = new AlertDialog.Builder(t_context);
		builder.setTitle("提示");
		builder.setMessage("确认对其解除屏蔽吗?");
		builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = data.get(poss).getLeftStr();
				if (!TextUtils.isEmpty(name)) {

					releaseShield(name);
				}

			}
		});
		builder.show();

	}

	@Override
	public void Cell_lay_Click(View v, int pos) {
		showConfirm(pos);

	}

}
