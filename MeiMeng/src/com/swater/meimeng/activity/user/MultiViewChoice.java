package com.swater.meimeng.activity.user;

import java.util.ArrayList;
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

public class MultiViewChoice extends BaseTemplate implements Radio_lay_click {
	ListView lsview = null;
	String type = "";
	int pos = -1;
	UserAdapter adapter = null;
	DataVo datavo = null;
	public static String EDIT_MULTI_CHBOX = "EDIT_MULTI_CHBOX";
	List<UserAdapterItem> arr_adapter = new ArrayList<UserAdapterItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multi_lay);
		TempRun();
	}

	@Override
	public void iniView() {
		showTitle("选择");
		showNavgationLeftBar("返回");
		// showNavgationRightBar("保存");
		lsview = findListView(R.id.ls_ck);
		type = this.getIntent().getStringExtra("type");
		pos = this.getIntent().getIntExtra("pos", -1);
		inixmlOpts(t_context);
		adapter = new UserAdapter(t_context);
		adapter.setRadio_click_enent(this);
		PersonDataCato pdc = reader_ops.getSingleCato(type);
		String[] arr = pdc.getDataArray();
		for (int i = 0; i < arr.length; i++) {
			UserAdapterItem it = new UserAdapterItem();
			it.setLeftStr(arr[i]);
			it.setId(pdc.getMaps_reverse().get(arr[i]));
			arr_adapter.add(it);

		}
		adapter.setType(User_Ada_Type.type_check_box_lay);
		adapter.setObjs(arr_adapter);
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
			intent.setAction(CMD_EDIT_MULTI_CHBOX);
			datavo = adapter.getDataquence_selected();
			if (null != datavo) {
				datavo.setType_date(TextUtils.isEmpty(type) ? "" : type);
				datavo.setPos(pos);
			}

			intent.putExtra("pda", datavo);
			t_context.sendBroadcast(intent);
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

}
