package com.swater.meimeng.activity.user.swipepage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.Radio_lay_click;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.User_Ada_Type;
import com.swater.meimeng.activity.adapterGeneral.vo.AnSwerVo;
import com.swater.meimeng.activity.adapterGeneral.vo.SubVo;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.database.XmlSurveyData;
import com.swater.meimeng.database.XmlSurveyData.SurveyData;
import com.swater.meimeng.database.XmlSurveyData.SurveyVo;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.constant.MConstantUser;

public class SurveyPageActivity extends BaseTemplate implements
		OnPageChangeListener, Radio_lay_click {

	private com.swater.meimeng.activity.user.swipepage.ScrollViewPage vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views = new ArrayList<View>();
	LinkedList<UserAdapter> quence_adapter = new LinkedList<UserAdapter>();
	int pos_scroll = 0;
	SurveyData data = null;
	XmlSurveyData reader_suy = null;
	String keyName = "";
	AnSwerVo ans = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.survey_page);
		TempRun();

	}

	void iniSuyData() {

		reader_suy = XmlSurveyData.getSingle();
		try {
			reader_suy.init(t_context);
			data = reader_suy.getSingleQuests(shareUserInfo().getUserInfo()
					.getSex() + "", keyName);
			
			
			if (this.getIntent().getSerializableExtra("data") != null) {
				if (this.getIntent().getSerializableExtra("data") instanceof AnSwerVo) {
					ans = (AnSwerVo) this.getIntent().getSerializableExtra(
							"data");

				}

			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	void iniData() {
		views.clear();
		int size = data.getKinds_ls().size();
		NSLoger.Log("--size--" + size);
		for (int i = 0; i < data.getKinds_ls().size(); i++) {
			View lay = LayoutInflater.from(t_context).inflate(
					R.layout.survery_cell, null);
			TextView txt = (TextView) lay.findViewById(R.id.ques_title);
			ListView ls = (ListView) lay.findViewById(R.id.ls_radio);
			String node_top = data.getKinds_ls().get(i);
			setValueToView(txt, node_top);

			UserAdapter ud = new UserAdapter(t_context);
			LinkedList<SurveyVo> linklist = data.getSets_question().get(
					data.getKinds_ls().get(i));
			
			ud.setObjs(linklist);
			ud.setRadio_click_enent(this);
//
			if (ans != null) {
				if (ans.getSubs() != null) {
					int j=0;
					for (SubVo sub : ans.getSubs()) {
						if (sub.getQues_value().trim().equals(node_top.trim())) {
							
							
							SurveyVo sv=(SurveyVo)ud.getObjs().get(j);
							id_type=sv.getId_type();
							componnentParams(sv);
							j++;
						}
						for (int k = 0; k < linklist.size(); k++) {
							SurveyVo surveyVo=linklist.get(k);
							if (surveyVo.getText_option().trim().equals(sub.getValue().trim())) {
								ud.setPos_selected_pos(k);
							}
							
						}

					}
				}
			}
			
			ud.setListview(ls);
			ud.setType(User_Ada_Type.type_radio_listview);
			ls.setAdapter(ud);
			quence_adapter.add(ud);
			views.add(lay);

		}
		vpAdapter = new ViewPagerAdapter(views);
		vp.setAdapter(vpAdapter);
		iniGuideDots(null);
	}

	int currentIndex = 0;
	ImageView[] dots = null;

	void iniGuideDots(View v) {
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
		ll.removeAllViews();
		ll.setVisibility(View.VISIBLE);
		dots = new ImageView[views.size()];
		if (dots.length == 0)
			ll.setVisibility(View.INVISIBLE);
		else
			ll.setVisibility(View.VISIBLE);
		// 循环取得小点图片
		for (int i = 0; i < dots.length; i++) {
			ImageView image = new ImageView(t_context);
			image.setLayoutParams(new ViewGroup.LayoutParams(25, 25));
			image.setImageResource(R.drawable.indicator_normal);
			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(25, 25);
			p.setMargins(10, 0, 0, 0);
			dots[i] = image;
			// dots[i].setEnabled(true);// 都设为灰色
			dots[i].setOnClickListener(this);
			dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
			ll.addView(image, p);
		}
		currentIndex = 0;
		// dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态
		dots[currentIndex].setImageResource(R.drawable.indicator_selected);
	}

	@Override
	public void iniView() {
		keyName = this.getIntent().getStringExtra("name");
		showTitle("个性展示");
		showNavgationLeftBar("返回");
		showNavgationRightBar("提交");
		// findButton(R.id.home_right_btn).setBackgroundColor(R.color.gray_deep);
		findButton(R.id.home_right_btn).setClickable(false);
		vp = (ScrollViewPage) findViewById(R.id.viewpager);
		vp.setOnPageChangeListener(this);
		iniSuyData();
		iniData();

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
			submitPersonalInfo();
		}

			break;

		default:
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// pos_scroll=arg0;

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// pos_scroll=arg0;

	}

	@Override
	public void onPageSelected(int p) {
		Log.d("onPageSelected-->>+p", p + "--");
		if (p < 0 || p >= dots.length) {
			return;
		}

		// if (quence_adapter.get(p).getPos_selected_pos() == -1) {
		// showToast("请先选择答案！");
		//
		//
		// vp.setCurrentItem(p);
		// vp.setForbiddenScroll(true);
		//
		// return;
		//
		// } else {
		vp.setForbiddenScroll(false);
		vp.setCurrentItem(p);
		setCurDot(p);

		// }

	}

	private void setCurView(int position) {
		if (position < 0 || position >= dots.length) {
			return;
		}
		if (quence_adapter.get(position).getPos_selected_pos() == -1) {

			return;

		} else {
		}

	}

	/**
	 * 这只当前引导小点的选中
	 */
	private void setCurDot(int positon) {
		// if (positon < 0 || positon > dots.length - 1 || currentIndex ==
		// positon) {
		// return;
		// }

		// dots[positon].setEnabled(false);
		// dots[currentIndex].setEnabled(true);

		for (ImageView cell : dots) {
			// dots[positon].setImageResource(R.drawable.indicator_normal);
			cell.setImageResource(R.drawable.indicator_normal);

		}
		dots[positon].setImageResource(R.drawable.indicator_selected);

		currentIndex = positon;
	}

	String id_type = "";

	@Override
	public void Radio_lay_Click(View v, int pos, ListAdapter adapter) {
		UserAdapter ada_pram = (UserAdapter) adapter;
		vp.setForbiddenScroll(false);
		vp.isForbiddenScroll = false;
		ada_pram.setPos_selected_pos(pos);
		ada_pram.notifyDataSetChanged();
		// ((UserAdapter) adapter).setPos_selected_pos(pos);
		// ((UserAdapter) adapter).notifyDataSetChanged();
		SurveyVo sv = (SurveyVo) ada_pram.getObjs().get(pos);
		id_type = sv.getId_type();
		componnentParams(sv);

	}

	StringBuilder param_arr = new StringBuilder("[");

	String componnentParams(SurveyVo sv) {
		param_arr.append("{" + "\"" + "question_id" + "\"" + ":"
				+ sv.getId_ques() + ",");
		param_arr.append("\"" + "option_id" + "\"" + ":" + sv.getId_option()
				+ "},");

		return param_arr.toString();
	}

	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			ProDimiss();
			switch (msg.what) {
			case Resp_action_ok: {
				// LocalBroadcastManager.getInstance(t_context).sendBroadcast(
				// new Intent(CMD_FRESH_ALL_DATA));
				sendBroadcast(new Intent(CMD_FRESH_ALL_DATA));
				sysback();
				// showToast("保存成功！");
				// sysback();

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

	void submitPersonalInfo() {
		try {

			ProShow("正在保存信息...");
			poolThread.submit(new Runnable() {

				@Override
				public void run() {
					req_map_param.put(MConstantUser.UserProperty.user_key,
							key_server);
					req_map_param.put(MConstantUser.UserProperty.uid,
							shareUserInfo().getUserid() + "");
					req_map_param.put(MConstantUser.UserProperty.type_id,
							id_type);
					String final_param = param_arr.append("]").toString();
					int index = final_param.lastIndexOf(",");
					CharSequence s = final_param.subSequence(0, index);
					String half_s = s.toString() + "]";

					req_map_param
							.put(MConstantUser.UserProperty.answer, half_s);

					vo_resp = sendReq(MURL_user_Personal_show, req_map_param);
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
			handler.obtainMessage(Resp_exception).sendToTarget();
		}

	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (reader_suy != null) {
			reader_suy.ReleaseClear();
			reader_suy = null;
		}
		if (t_context != null) {
			t_context = null;
		}
		if (data != null) {
			data = null;
		}
		if (quence_adapter != null) {
			quence_adapter.clear();
		}
		closePool();
	}

}
