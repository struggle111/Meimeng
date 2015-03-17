package com.swater.meimeng.fragment.party;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.AdapterSearch;
import com.swater.meimeng.activity.adapterGeneral.AdapterSearch.AdapterSearch_Type;
import com.swater.meimeng.activity.adapterGeneral.vo.UserSearchVo;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.fragment.recommend.BarPushActivity;
import com.swater.meimeng.mutils.listview.XListView;
import com.swater.meimeng.mutils.net.RespVo;

public class JoinUserList extends BaseTemplate {
	XListView ls = null;
	List<UserSearchVo> data_user = new ArrayList<UserSearchVo>();
	List<UserSearchVo> data_container = new ArrayList<UserSearchVo>();
	Boolean hasNextData = true;;
	int pageSize = 10;
	AdapterSearch adapter = null;
	RespVo vo_resp = null;
	int pageIndex = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join_list);
		TempRun();
	}

	@Override
	public void iniView() {
		ls = (XListView) findViewById(R.id.join_list);
		ls.setOnItemClickListener(cell);
		aid = this.getIntent().getStringExtra("aid");
		showTitle("已参加人数");
		showNavgationLeftBar("返回");
		view_Hide(findViewById(R.id.home_right_btn));
		getUserlist();

	}

	OnItemClickListener cell = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent in = new Intent();
			UserSearchVo vo = data_container.get(position);
			in.putExtra("data", vo);
			jumpOtherActivity(BarPushActivity.class, in);

		}
	};

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
		}

			break;

		default:
			break;
		}

	}

	String aid = "";

	void getUserlist() {
		ProShow("正在获取数据....");

		poolThread.submit(new Runnable() {

			@Override
			public void run() {
				try {
					req_map_param.put("uid", shareUserInfo().getUserid() + "");
					req_map_param.put("key", key_server);

					req_map_param.put("aid", aid);
					req_map_param.put("page", pageIndex + "");
					vo_resp = sendReq(MURL_APPLY_LIST_USERS, req_map_param);

					if (null == vo_resp) {
						handler.obtainMessage(ACTION_FAIL).sendToTarget();
					}
					if (vo_resp.isHasError()) {
						handler.obtainMessage(ACTION_ERROR,
								vo_resp.getErrorDetail()).sendToTarget();

					} else {
						handler.obtainMessage(Resp_action_ok).sendToTarget();

					}

				} catch (Exception e) {
					e.printStackTrace();
					handler.obtainMessage(ACTION_EXCEPTON).sendToTarget();
				}

			}
		});
	}

	protected Object parselJson(String res) {
		data_user.clear();
		try {
			JSONObject ar = new JSONObject(res).getJSONObject("data");
			if (res == null || ar == null) {
				hasNextData = false;
				return null;
			}
			JSONArray arr = new JSONObject(res).getJSONObject("data")
					.getJSONArray("users");
			if (arr.length() < pageSize) {
				hasNextData = false;
			} else {
				hasNextData = true;
			}
			if (arr == null || arr.length() < 1) {
				return null;
			}
			for (int i = 0; i < arr.length(); i++) {

				UserSearchVo usv = new UserSearchVo();
				usv.setUid(arr.getJSONObject(i).getInt("uid") + "");

				usv.setPhotoUrl(arr.getJSONObject(i).getString("photo_url"));
				usv.setNickName(arr.getJSONObject(i).getString("nickname"));
				// usv.setHeight(arr.getJSONObject(i).getString("height"));
				usv.setCity(arr.getJSONObject(i).getString("city"));
				usv.setHeart_desc(arr.getJSONObject(i).getString(
						"heart_description"));
				// usv.setHead_url(arr.getJSONObject(i).getString("header_url"));
				usv.setNickName(arr.getJSONObject(i).getString("nickname"));
				// usv.setHeight(arr.getJSONObject(i).getString("height"));
				usv.setCity(arr.getJSONObject(i).getString("city"));
				usv.setProvince(arr.getJSONObject(i).getString("province"));
				;
				usv.setOpentome(arr.getJSONObject(i).getInt("opentome") + "");

				usv.setOpentome(arr.getJSONObject(i).getInt("opentome") + "");
				usv.setAge(arr.getJSONObject(i).getInt("age") + "");
				data_user.add(usv);
			}
		} catch (JSONException e) {
			showToast(" 解析数据出错！");
			e.printStackTrace();
		} finally {

		}

		return null;
	};

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ProDimiss();
			switch (msg.what) {
			case ACTION_TAG1: {

			}
				break;
			case Resp_DATA_OK: {
			}

				break;
			case ACTION_FAIL: {
				showToast("服务器响应失败！");
			}

				break;
			case ACTION_EXCEPTON:
			case Resp_DATA_EXCEPTION: {
				showToast("获取数据异常！" + mg2String(msg));
			}

				break;
			case Resp_exception: {
				showToast("服务器异常！" + mg2String(msg));

			}

				break;
			case ACTION_ERROR:
			case Resp_DATA_Empty: {
				showToast("获取数据失败！" + mg2String(msg));
			}

				break;
			case Resp_action_fail: {
				showToast("获取数据异常！" + mg2String(msg));
			}

				break;
			case Resp_action_ok: {
				parselJson(vo_resp.getResp());
				data_container.addAll(data_user);
				if (null == adapter) {
					adapter = new AdapterSearch(t_context);
					adapter.setLs_view(ls);
					adapter.setAct(JoinUserList.this);
					adapter.setType_search(AdapterSearch_Type.type_focus);
					adapter.setData(data_container);
					ls.setAdapter(adapter);

				}

				adapter.notifyDataSetChanged();
				ls.stopRefresh();

				if (!hasNextData) {
					ls.stopLoadMoreWithState(LoadState.STATE_OVER);
					ls.setPullLoadEnable(false, LoadState.STATE_OVER);
				} else {
					ls.stopLoadMoreWithState(LoadState.STATE_NORMAL);
					ls.setPullLoadEnable(true, LoadState.STATE_OVER);
				}
			}

				break;

			default:
				break;
			}
		};
	};

}
