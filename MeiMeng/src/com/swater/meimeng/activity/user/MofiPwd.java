package com.swater.meimeng.activity.user;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.meimeng.app.R;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.mutils.constant.MConstantUser;

public class MofiPwd extends BaseTemplate {
	EditText ed_pwd_old, ed_pwd_new1, ed_pwd_new2;
	String pwd_org = "";
	String pwd_new = "";
	String pwd_new_repeat = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_modiy_pwd);
		TempRun();
	}

	@Override
	public void iniView() {
		showTitle("修改密码");
		showNavgationLeftBar("返回");
		showNavgationRightBar("提交");
		ed_pwd_old = findEditText(R.id.edit_pwd_org);
		ed_pwd_new1 = findEditText(R.id.edit_pwd_new1);
		ed_pwd_new2 = findEditText(R.id.edit_pwd_new2);

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
			pwd_new = getValueView(ed_pwd_new1).trim();
			pwd_new_repeat = getValueView(ed_pwd_new2).trim();
			pwd_org = getValueView(ed_pwd_old).trim();
			if (TextUtils.isEmpty(pwd_org)) {
				showToast("请先输入原密码！");
			}else if(TextUtils.isEmpty(pwd_new)){
				showToast("请输入新密码！");
				
			}else if(TextUtils.isEmpty(pwd_new_repeat)){
				showToast("请再次输入新密码！");
				
			}else if(!pwd_new.equals(pwd_new_repeat)){
				showToast("两次输入密码不一致！");
				
			}else{
				
				saveInfo();
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
				showToast("修改成功！");
				sysback();

			}

				break;
			case Resp_action_fail: {
				showToast("修改失败" + mg2String(msg));

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

	void saveInfo() {
		// if (TextUtils.isEmpty(id_blood)) {
		// showToast("请先选择血型");
		// return;
		// }
		// if (TextUtils.isEmpty(id_ethic)) {
		// showToast("请先选择名族");
		// return;
		// }
		ProShow("正在修改密码...");

		try {
			poolThread.submit(new Runnable() {

				@Override
				public void run() {

					req_map_param.put(MConstantUser.UserProperty.uid,
							shareUserInfo().getUserid() + "");
					req_map_param.put(MConstantUser.UserProperty.user_key,
							key_server);
					req_map_param.put("currentpwd", pwd_org);
					req_map_param.put("newpwd", pwd_new);

					vo_resp = sendReq(MURL_PWD, req_map_param);
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
