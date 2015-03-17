package com.swater.meimeng.activity.user;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.meimeng.app.R;
import com.swater.meimeng.activity.newtabMain.IndexActivity;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.database.ShareUtil;
import com.swater.meimeng.database.ShareUserConstant.UserGobal;
import com.swater.meimeng.database.ShareUtil.UserVo;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.constant.MConstantUser;
import com.swater.meimeng.mutils.net.RespVo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.drm.DrmStore.Action;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

public class WaitActive extends BaseTemplate {
	RespVo resp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waitfor_active);
		TempRun();
	}

	@Override
	public void iniView() {

		view_Show(findViewById(R.id.home_left_btn));
		setValueToView(findViewById(R.id.home_left_btn), "注销");
		view_Show(findButton(R.id.home_right_btn));
		setValueToView(findViewById(R.id.home_right_btn), "刷新");
		
	}

	@Override
	public void bindClick() {
		setClickEvent(findViewById(R.id.home_left_btn),
				findViewById(R.id.fresh_main),
				findButton(R.id.home_right_btn));
	}

	private void diagActive() {

		AlertDialog.Builder builder = new AlertDialog.Builder(t_context);
		builder.setTitle("帐号审核中");
		builder.setMessage("如有疑问欢迎致电客户专线：400-8783-520；15520829590；");
		builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				callTel();
			}
		});
		builder.show();

	}

	void callTel() {

		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ "4008783520"));
		WaitActive.this.startActivity(intent);// 内部类
	}

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_right_btn: {
			Relogin();

		}

			break;
		case R.id.home_left_btn: {

			jumpOtherActivity(UserLogin.class);

		}

			break;
		case R.id.fresh_main: {
			Intent intent = new Intent();
			if (shareUserInfo().getUserInfo().getActive() == 2) {
				intent.setClass(t_context, IndexActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				t_context.startActivity(intent);
				finish();

			} else {
				// showToast("您的状态尚未激活！请先激活状态！");
				diagActive();
			}

		}

			break;

		default:
			break;
		}

	}

	protected Object parselJson(String res) {
		try {
			// active：是否激活，int；1-未激活；2-激活；
			// audit_type：认证类型，int；1-在线审核；2-面谈审核；
			// vip_level：vip等级，int；0-未付费;1-银牌；2-金牌;3-黑牌；
			// nickname:昵称,string;
			// header:头像,string;
			// medal:勋章ID拼接的字符串，string，如”1,2,3”;
			// {"result":1,"error":"","data":{"uid":55,"sex":2,"active":2,"vip_level":0,"nickname":"55",
			// "header":"http:\/\/112.124.18.97\/attachment\/tmp\/55\/header.jpg","audit_type":1,"medal":"2,3"}}

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
			user.setLoginName(shareUserInfo().getUserInfo().getLoginName());
			user.setLoginPwd(shareUserInfo().getUserInfo().getLoginPwd());
			shareUserInfo().saveUserInfo(user);
		} catch (JSONException e) {
			handler.obtainMessage(-9).sendToTarget();
			e.printStackTrace();
		}

		return null;
	};

	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			ProDimiss();
			switch (msg.what) {
			case Resp_action_fail: {
				showToat("获取激活状态失败" + mg2String(msg));
				break;
			}

			case Resp_action_ok: {
				parselJson(resp.getResp());
				regPush();
				Intent intent = new Intent();
				// int；1-未激活；2-激活； */
				if (ShareUtil.getInstance(t_context).getUserInfo().getActive() == 2) {

					intent.setClass(t_context, IndexActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					t_context.startActivity(intent);
					finish();
				} else {
//					showToast("您的状态尚未激活！请先激活状态！");
					diagActive();
				}
				break;
			}

			case Resp_exception: {
				showToat("获取异常！");
			}

				break;
			case Resp_NET_FAIL: {

			}

				break;
			case -9: {
				showToast("解析数据异常！");

			}

				break;

			default:
				break;
			}
		};
	};

	void Relogin() {
		ProShow("正在获取激活状态...");

		poolThread.submit(new Runnable() {

			@Override
			public void run() {

				try {
					req_map_param.put("key", key_server);
					req_map_param.put(MConstantUser.UserReg.telephone,
							shareUserInfo().getUserInfo().getLoginName());
					req_map_param.put(MConstantUser.UserReg.password,
							shareUserInfo().getUserInfo().getLoginPwd());
					resp = sendReq(MURL_user_login, req_map_param);
					if (resp == null) {
						handler.obtainMessage(Resp_exception).sendToTarget();
					}
					Log.d("user--login--resp--" + resp.getResp(), "---->>");
					if (!resp.isHasError()) {
						handler.obtainMessage(Resp_action_ok).sendToTarget();
					} else {
						handler.obtainMessage(Resp_action_fail,
								resp.getErrorDetail()).sendToTarget();

					}

				} catch (Exception e) {
					handler.obtainMessage(Resp_exception).sendToTarget();

				}

			}
		});

	}

}
