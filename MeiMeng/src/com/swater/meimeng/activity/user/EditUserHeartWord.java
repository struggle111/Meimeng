package com.swater.meimeng.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.vo.UserInfo;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.mutils.constant.MConstantUser;

//内心独白
public class EditUserHeartWord extends BaseTemplate {
	UserInfo info = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_word);
		this.TempRun();

	}

	@Override
	public void iniView() {
		info = (UserInfo) this.getIntent().getSerializableExtra("vo");
		showTitle("内心独白");
		showNavgationRightBar("保存");
		showNavgationLeftBar("返回");
		if (null == info || TextUtils.isEmpty(info.getHeart_description())) {
			return;
		} else {
			setValueToView(findViewById(R.id.user_heart_content),
					info.getHeart_description());
		}

	}

	@Override
	public void bindClick() {
		bindNavgationEvent();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.home_right_btn: {
			this.submitSelfHeart();

		}

			break;
		case R.id.home_left_btn: {

			sysback();
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
				// showToast("");
				sysback();
				sendBroadcast(new Intent(CMD_FRESH_ALL_DATA));
			}

				break;
			case Resp_action_fail: {
				showToast("提交失败！" + mg2String(msg));

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

	private void submitSelfHeart() {
		try {
			ProShow("正在提交....");
			if (TextUtils
					.isEmpty(getValueView(findViewById(R.id.user_heart_content)))) {
				showToast("请先填写内容！");
				ProDimiss();
				return;
			}
			poolThread.submit(new Runnable() {

				@Override
				public void run() {

					req_map_param.put(MConstantUser.UserProperty.user_key,
							key_server);
					req_map_param.put(MConstantUser.UserProperty.uid,
							shareUserInfo().getUserid() + "");
					req_map_param
							.put(MConstantUser.UserProperty.content,
									getValueView(findViewById(R.id.user_heart_content)));

					vo_resp = sendReq(MURL_user_heart_description,
							req_map_param);
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

}
