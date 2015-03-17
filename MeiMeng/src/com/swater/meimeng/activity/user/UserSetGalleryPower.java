package com.swater.meimeng.activity.user;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.Radio_lay_click;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.User_Ada_Type;
import com.swater.meimeng.activity.adapterGeneral.UserAdapterItem;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.mutils.NSlog.NSLoger;

/**
 * @category 查看权限
 */
public class UserSetGalleryPower extends BaseTemplate implements
		Radio_lay_click {
	UserAdapter adapter = null;
	ListView ls = null;
	List<UserAdapterItem> data = new ArrayList<UserAdapterItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_setting_layout);
		TempRun();
	}

	@Override
	public void iniView() {
		showNavgationLeftBar("返回");
		// view_Hide(findViewById(R.id.home_right_btn));
		showNavgationRightBar("保存");
		showTitle("相册查看权限");
		ls = findListView(R.id.ls_set);
		adapter = new UserAdapter(t_context);
		adapter.setType(User_Ada_Type.type_user_gallery_power);
		adapter.setRadio_click_enent(this);
		int type = this.getIntent().getIntExtra("type", 0);
		// 1-对所有人开放(默认);
		// 2-对所有人关闭;
		// 3-对部分人开放;个人设置中也会用到这个状态;
		for (int i = 0; i < 3; i++) {
			UserAdapterItem it = new UserAdapterItem();
			switch (i) {
			case 0: {
				
				it.setLeftStr("对所有人开放");
			}

				break;
			case 1: {

				it.setLeftStr("对所有人关闭");
			}

				break;
			case 2: {
				it.setLeftStr("只对你想见的人开放");

			}

				break;

			default:
				break;
			}
			data.add(it);

		}
		switch (type) {
		case 1:
			adapter.setPos_selected_pos(0);
			break;
		case 2:
			adapter.setPos_selected_pos(1);
			break;
		case 3:
			adapter.setPos_selected_pos(2);
			break;

		default:
			break;
		}
		adapter.setObjs(data);
		ls.setAdapter(adapter);

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
//			if (isChangeData) {
//				showToast("刷新数据！");
				sendBroadcast(new Intent(CMD_FRESH_ALL_DATA));
//			}

		}

			break;
		case R.id.home_right_btn: {
			saveState();
			
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
			case Resp_exception: {
				showToat("获取异常！");
			}

				break;
			case Resp_action_fail: {
				showToast("服务器异常！" + mg2String(msg));
			}

				break;
			case Resp_action_ok: {
				onBackPressed();
			}

				break;
			case Resp_action_Empty: {
				showToast("拉取数据失败!");

			}

				break;

			case ACTION_TAG1: {
				isChangeData=true;
				showToast("操作成功！!");

			}

				break;

			default:
				break;
			}
		};

	};

	// key true string 参见规范中的key值
	// uid true int 用户ID
	// permission true int 权限,1-对所有人可见(默认);2-对所有人关闭;3-对想见的人和同意查看的人可见;
	void saveState() {
		ProShow("正在保存设置...");
		poolThread.submit(new Runnable() {

			@Override
			public void run() {
				try {
					req_map_param.put("uid", shareUserInfo().getUserid() + "");
					req_map_param.put("key", key_server);
					String per = "1";
					switch (adapter.getPos_selected_pos()) {
					case 0: {
						per = "1";
					}

						break;
					case 1: {
						per = "2";

					}

						break;
					case 2: {
						per = "3";

					}

						break;

					default:
						break;
					}

					// 08-17 17:49:16.275: D/--
					// 发送url-->>toStirng------methodg--->>(24766):
					// sb---http://192.168.20.1:9090//api/personal/albumpermission/uid/55/permission/2/key/b91e85501ab94732

					req_map_param.put("permission", per);
					vo_resp = sendReq(MURL_PERMISSION_GALLERY_SET,
							req_map_param);
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
	public void Radio_lay_Click(View v, int pos, ListAdapter ad) {
		adapter.setPos_selected_pos(pos);
		adapter.notifyDataSetChanged();

	}

}
