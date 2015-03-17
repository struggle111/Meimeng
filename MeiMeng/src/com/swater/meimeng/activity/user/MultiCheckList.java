package com.swater.meimeng.activity.user;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.Radio_lay_click;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.User_Ada_Type;
import com.swater.meimeng.activity.adapterGeneral.UserAdapterItem;
import com.swater.meimeng.activity.adapterGeneral.vo.DataVo;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.database.XmlDataOptions.PersonDataCato;

public class MultiCheckList extends BaseTemplate implements Radio_lay_click {
	ListView lsview = null;
	String type = "";
	int pos = -1;
	UserAdapter adapter = null;
	DataVo datavo = null;
	List<UserAdapterItem> arr_adapter = new ArrayList<UserAdapterItem>();
	String data_echo = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multi_lay);
		TempRun();
	}

	@Override
	public void iniView() {
		showTitle("个人兴趣");
		showNavgationLeftBar("返回");
		lsview = findListView(R.id.ls_ck);
		data_echo = this.getIntent().getStringExtra("data");
		type = this.getIntent().getStringExtra("type");
		pos = this.getIntent().getIntExtra("pos", -1);
		inixmlOpts(t_context);
		adapter = new UserAdapter(t_context);
		adapter.setRadio_click_enent(this);
		PersonDataCato pdc = reader_ops.getSingleCato(type);
		String[] arr = pdc.getDataArray();
		LinkedList<UserAdapterItem> ech = adapter.getQuence_selected();
		String[] arr_selected = null;
		if (!TextUtils.isEmpty(data_echo)) {

			if (!"null".equals(data_echo)) {
				if (data_echo.contains(",")) {
					arr_selected = data_echo.split(",");

				} else {
					arr_selected = new String[] { data_echo };

				}

			}

		}
		for (int i = 0; i < arr.length; i++) {
			UserAdapterItem it = new UserAdapterItem();
			it.setLeftStr(arr[i]);
			if (arr_selected!=null) {
				for (int j = 0; j < arr_selected.length; j++) {
					if (arr_selected[j].equals(arr[i])) {
						it.setPos(i);
						ech.add(it);
						
					}
					
				}
				
			}
			it.setId(pdc.getMaps_reverse().get(arr[i]));
			arr_adapter.add(it);

		}
		adapter.setType(User_Ada_Type.type_check_box_lay);
		adapter.setObjs(arr_adapter);
		adapter.setQuence_selected(ech);

		adapter.setListview(lsview);

		lsview.setAdapter(adapter);

	}

	@Override
	public void bindClick() {
		bindNavgationEvent();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_left_btn: {
			Intent intent = new Intent();
			intent.setAction(CMD_HOBBY_SURVEY);
			datavo = adapter.getDataquence_selected();
			if (null != datavo) {
				//datavo.setIds("");
				datavo.setType_date(TextUtils.isEmpty(type) ? "" : type);
				datavo.setPos(pos);
			}else{
				datavo=new DataVo();
				datavo.setType_date(TextUtils.isEmpty(type) ? "" : type);
				datavo.setPos(pos);
				datavo.setIds("");
			}

			intent.putExtra("pda", datavo);
			MultiCheckList.this.sendBroadcast(intent);
			
			finish();

		}

			break;
		case R.id.home_right_btn: {

		}

			break;

		default:
			break;
		}

	}

	@Override
	public void Radio_lay_Click(View v, int pos, ListAdapter adapter) {
		try {
			UserAdapter ad = (UserAdapter) adapter;
			List<?> objs = ad.getObjs();
			CheckBox cb = (CheckBox) ad.getListview().findViewWithTag(pos);
			UserAdapterItem it = (UserAdapterItem) objs.get(pos);

			it.setPos(pos);
			if (cb.isChecked()) {
				cb.setChecked(false);
				ad.getQuence_selected().remove(it);
			} else {
				cb.setChecked(true);
				ad.getQuence_selected().add(it);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (reader_ops != null) {
			reader_ops.ClearAllCache();
			reader_ops = null;
		}

	}

}
