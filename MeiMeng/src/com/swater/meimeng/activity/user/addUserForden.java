package com.swater.meimeng.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.meimeng.app.R;
import com.swater.meimeng.commbase.BaseTemplate;

public class addUserForden extends BaseTemplate {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_forbiden_user);
		TempRun();
	}

	@Override
	public void iniView() {
		showNavgationLeftBar("返回");
		view_Hide(findViewById(R.id.home_right_btn));
		showTitle("添加屏蔽");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_left_btn: {
			sysback();

		}

			break;
		case R.id.add_for_btn:
		case R.id.home_right_btn: {
			shieldName = getValueView(findViewById(R.id.add_for_username));
			if (TextUtils.isEmpty(shieldName)) {
				showToast("请先输入要添加的屏幕人");
			} else {

				addUserForbiden();
			}

		}

			break;

		default:
			break;
		}

	}

	@Override
	public void bindClick() {
		bindNavgationEvent();
		setClickEvent(findButton(R.id.add_for_btn));

	}

	String shieldName = "";
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ProDimiss();
			switch (msg.what) {
			case Resp_exception: {
				showToat("服务器异常！");

			}

				break;
			case Resp_action_fail: {

				showToat("服务器异常！" + mg2String(msg));
			}

				break;
			case Resp_action_ok: {
				showToast("添加成功！");
				Intent in = new Intent(CMD_FRESH_SHILED_LIST);
				sendBroadcast(in);
				onBackPressed();

			}

				break;

			default:
				break;
			}
		};
	};

	void addUserForbiden() {
		ProShow("正在添加屏蔽...");

		try {

			poolThread.submit(new Runnable() {

				@Override
				public void run() {
					req_map_param.put("key", key_server);
					req_map_param.put("uid", shareUserInfo().getUserid() + "");
					req_map_param.put("keyword", shieldName);
					vo_resp = sendReq(MURL_PERMISSION_ADD_SHIELD, req_map_param);
					if (null == vo_resp) {
						handler.obtainMessage(Resp_exception).sendToTarget();
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
		}

	}

}
