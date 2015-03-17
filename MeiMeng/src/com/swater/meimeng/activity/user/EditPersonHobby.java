package com.swater.meimeng.activity.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.cell_lay_click;
import com.swater.meimeng.activity.adapterGeneral.UserAdapterItem;
import com.swater.meimeng.activity.adapterGeneral.vo.DataVo;
import com.swater.meimeng.activity.adapterGeneral.vo.HobbyVo;
import com.swater.meimeng.activity.adapterGeneral.vo.UserInfo;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.constant.MConstantUser;
import com.swater.meimeng.mutils.constant.MConstantUser.UserAll_hobby_info;

public class EditPersonHobby extends BaseTemplate implements cell_lay_click {
	ListView ls = null;
	UserAdapter adapter = null;
	String type_selected = "";
	int pod_selected = -1;
	String id_like_sports, id_like_foods, id_like_books, id_like_musics,
			id_like_movies, id_focus_pros, id_entainments, id_hobbies,
			id_travels;

	String type = "";
	Map<String, String> keys_Param = new HashMap<String, String>();

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(br);

	};

	BroadcastReceiver br = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Object dv = (Object) intent.getSerializableExtra("pda");
			if (dv == null) {
				return;

			}
			if (dv instanceof DataVo) {
				DataVo data = (DataVo) dv;
				((UserAdapterItem) adapter.getObjs()
						.get(((DataVo) dv).getPos())).setRightStr(((DataVo) dv)
						.getValues());
				adapter.notifyDataSetChanged();
				String dtype = ((DataVo) dv).getType_date();

				if (dtype.equals(Person_Type_like_sports)) {
					id_like_sports = data.getIds();
				}

				else if (dtype.equals(Person_Type_like_food)) {
					id_like_foods = data.getIds();

				}

				else if (dtype.equals(Person_Type_like_book)) {
					id_like_books = data.getIds();

				}

				else if (dtype.equals(Person_Type_like_music)) {

					id_like_musics = data.getIds();
				}

				else if (dtype.equals(Person_Type_like_movie)) {

					id_like_movies = data.getIds();
				}

				else if (dtype.equals(Person_Type_attentionitem)) {
					id_focus_pros = data.getIds();

				}

				else if (dtype.equals(Person_Type_entertainment)) {
					id_entainments = data.getIds();

				}

				else if (dtype.equals(Person_Type_amateur_like)) {
					id_hobbies = data.getIds();

				} else if (dtype.equals(Person_Type_travel)) {
					id_travels = data.getIds();

				}

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
		registerReceiver(br, new IntentFilter(CMD_HOBBY_SURVEY));
	}

	UserInfo info = null;
	List<HobbyVo> ls_data = null;

	@Override
	public void iniView() {
		info = (UserInfo) this.getIntent().getSerializableExtra("vo");
		try {
			ls_data = info.getHobby_data();
			// for (HobbyVo hv : ls_data) {
			// NSLoger.Log(info.getJson_hobby_info());
			// NSLoger.Log(hv.getData());
			//
			// }
		} catch (Exception e) {
			// TODO: handle exception
		}
		showTitle("修改个人爱好");
		showNavgationLeftBar("返回");
		showNavgationRightBar("保存");

		ls = findListView(R.id.user_ls);
		adapter = new UserAdapter(t_context);
		adapter.setClick_cell_event(this);

		List<UserAdapterItem> dataSrc = new ArrayList<UserAdapterItem>();
		UserAdapterItem item = null;

		for (int i = 0; i < 9; i++) {
			item = new UserAdapterItem();
			item.setRightStr(ls_data.get(i).getData());
			switch (i) {
			case 0: {
				item.setLeftStr("喜欢的运动");
				id_like_sports = ls_data.get(i).getIds();

			}
				break;
			case 1: {
				item.setLeftStr("喜欢的食物");
				id_like_foods = ls_data.get(i).getIds();
			}
				break;
			case 2: {
				item.setLeftStr("喜欢的书籍");
				id_like_books = ls_data.get(i).getIds();
			}
				break;
			case 3: {
				item.setLeftStr("喜欢的音乐");
				id_like_musics = ls_data.get(i).getIds();
			}
				break;
			case 4: {
				item.setLeftStr("喜欢的电影");
				id_like_movies = ls_data.get(i).getIds();
			}
				break;
			case 5: {
				item.setLeftStr("关注的节目");
				id_focus_pros = ls_data.get(i).getIds();
			}
				break;
			case 6: {
				item.setLeftStr("娱乐休闲");
				id_entainments = ls_data.get(i).getIds();
			}
				break;
			case 7: {
				item.setLeftStr("业余爱好");
				id_hobbies = ls_data.get(i).getIds();
			}
				break;
			case 8: {
				item.setLeftStr("喜欢的旅游去处");
				id_travels = ls_data.get(i).getIds();
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
			type = Person_Type_like_sports;
			keys_Param.put(Person_Type_like_sports, id_like_sports);
		}

			break;
		case 1: {
			type = Person_Type_like_food;
			keys_Param.put(Person_Type_like_sports, id_like_foods);

		}

			break;
		case 2: {

			type = Person_Type_like_book;
			keys_Param.put(Person_Type_like_sports, id_like_books);
		}

			break;
		case 3: {
			type = Person_Type_like_music;
			keys_Param.put(Person_Type_like_sports, id_like_musics);

		}

			break;
		case 4: {
			type = Person_Type_like_movie;

			keys_Param.put(Person_Type_like_sports, id_like_movies);
		}

			break;
		case 5: {

			type = Person_Type_attentionitem;
			keys_Param.put(Person_Type_like_sports, id_focus_pros);
		}

			break;
		case 6: {
			type = Person_Type_entertainment;
			keys_Param.put(Person_Type_like_sports, id_entainments);

		}

			break;
		case 7: {

			type = Person_Type_amateur_like;
			keys_Param.put(Person_Type_like_sports, id_hobbies);
		}

			break;
		case 8: {

			type = Person_Type_travel;
		}

			break;

		default:
			break;
		}
		Intent in = new Intent();
		in.setClass(t_context, MultiCheckList.class);

		in.putExtra("data", ls_data.get(pos).getData());
		in.putExtra("type", type);
		in.putExtra("pos", pod_selected);
		if (!TextUtils.isEmpty(type)) {
			t_context.startActivity(in);
		}

	}

	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			ProDimiss();
			switch (msg.what) {
			case Resp_action_ok: {
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
					req_map_param.put(MConstantUser.UserProperty.like_sports,
							id_like_sports);
					req_map_param.put(MConstantUser.UserProperty.like_food,
							id_like_foods);
					req_map_param.put(MConstantUser.UserProperty.like_book,
							id_like_books);
					req_map_param.put(MConstantUser.UserProperty.like_music,
							id_like_musics);
					req_map_param.put(MConstantUser.UserProperty.like_movie,
							id_like_movies);
					req_map_param.put(
							MConstantUser.UserProperty.like_attention_item,
							id_focus_pros);
					req_map_param.put(
							MConstantUser.UserProperty.like_entertainment,
							id_entainments);
					req_map_param.put(
							MConstantUser.UserProperty.like_amateur_like,
							id_hobbies);
//					amateur_like
					req_map_param.put(
							MConstantUser.UserProperty.like_travel,
							id_travels);

					vo_resp = sendReq(MURL_user_Hobby, req_map_param);
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
