package com.swater.meimeng.fragment.search;

import java.io.IOException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.meimeng.app.R;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.commbase.BaseTemplate.click_wheel;
import com.swater.meimeng.database.Map_Province.CityVo;
import com.swater.meimeng.database.Map_Province.ProvinceVo;
import com.swater.meimeng.database.XmlDataCity;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.diagutil.Tools;
import com.swater.meimeng.mutils.wheelview.ArrayWheelAdapter;
import com.swater.meimeng.mutils.wheelview.OnWheelChangedListener;
import com.swater.meimeng.mutils.wheelview.WheelView;

public class SearchByCity extends BaseTemplate implements click_wheel {
	private WheelView country, city;
	List<ProvinceVo> ls_pro = null;
	private static String countries[];
	private String cities[][];
	XmlDataCity reader = null;
	Dialog city_diag = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_by_city);
		TempRun();
		iniCityData();
		showCityDiag();
	}

	View wheelView = null;

	void showCityDiag() {
		wheelView = getCityDialogView();
		if (wheelView == null) {
//			showToast("111");
			return;
		}
		country.setAdapter(new ArrayWheelAdapter<String>(countries));
		country.setCurrentItem(0);
		city.setAdapter(new ArrayWheelAdapter<String>(cities[0]));
		city.setCurrentItem(0);
		city_diag = Tools.pull_Dialog(t_context, wheelView);
		if (city_diag == null) {
//			showToast("333");
		}
	}

	void iniCityData() {

		reader = new XmlDataCity();
		try {
			reader.init(t_context);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ls_pro = reader.getAllProvinceData();
		countries = new String[ls_pro.size()];
		cities = new String[ls_pro.size()][];

		for (int i = 0; i < ls_pro.size(); i++) {
			countries[i] = ls_pro.get(i).getPro_name();
			String pvnid = ls_pro.get(i).getPro_id();
			List<CityVo> ls_city = reader.getCitiesByProId(pvnid);
			cities[i] = new String[ls_city.size()];
			for (int j = 0; j < ls_city.size(); j++) {
				cities[i][j] = ls_city.get(j).getCity_name();

			}

		}

	}

	Button btn_city_yes = null, btn_city_no = null;

	public View getCityDialogView() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int mScreenWidth = dm.widthPixels; // 获得屏幕的宽
		int mScreenHeight = dm.heightPixels;//获得屏幕的高
		View viewMain = LayoutInflater.from(t_context).inflate(
				R.layout.wheelview_cities, null);
		viewMain.setMinimumWidth(mScreenWidth);
		viewMain.setMinimumHeight(mScreenHeight/2);

		btn_city_yes = (Button) viewMain.findViewById(R.id.wh_yes);

		btn_city_no = (Button) viewMain.findViewById(R.id.wh_no);
		btn_city_no.setOnClickListener(this);
		btn_city_yes.setOnClickListener(this);
		country = (WheelView) viewMain.findViewById(R.id.country);
		country.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				city.setAdapter(new ArrayWheelAdapter<String>(cities[newValue]));
				city.setCurrentItem(0);

			}
		});

		city = (WheelView) viewMain.findViewById(R.id.city);

		return viewMain;
	}

	void CancelDiagCity() {
		if (city_diag != null || city_diag.isShowing()) {
			city_diag.cancel();
		}
	}

	@Override
	public void iniView() {
		setClick_yes_diag(this);

	}

	@Override
	public void bindClick() {
		bindNavgationEvent();
		showNavgationLeftBar("返回");
		setClickEvent(findButton(R.id.btn_action));

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_action: {
			if (null==reader||cities==null) {
				iniCityData();
			}

			showCityDiag();
		}

			break;
		case R.id.wh_no:
		case R.id.home_left_btn: {

			//sysback();
			SearchByCity.this.finishActivity(90);
			onBackPressed();
		}

			break;
		case R.id.wh_yes: {
			String value_city = cities[country.getCurrentItem()][city
					.getCurrentItem()];
			String cityid = reader.getCityIdByName(value_city);
//			 showToast("city-value--" + value_city);
			Intent in = new Intent(t_context, SearchResultActivity.class);
			in.putExtra("city", value_city);
			in.putExtra("cityid", cityid);
			// in.setAction(CMD_SEARCH_CITY);
			t_context.startActivity(in);
			// sendBroadcast(in);

			// jumpOtherActivity(SearchResultActivity.class);
			finish();
			// sysback();
			// jumpOtherActivity(SearchFragment.class);

		}

			break;

		default:
			break;
		}

	}

	@Override
	public void click_yes_or_no(View v, int index, String value) {
		switch (v.getId()) {
		case R.id.wh_yes: {
			showToat(value + "");
		}

			break;

		default:
			break;
		}

	}
	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		lease();
		
	}
	void lease() {
		try {
			if (reader != null) {
				reader.CloseRes();
				reader = null;
				NSLoger.Log("--释放城市搜索数据！--xml0----");
				if (ls_pro != null) {
					ls_pro.clear();
					ls_pro = null;
					countries = null;
					cities = null;
				}
				System.gc();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
