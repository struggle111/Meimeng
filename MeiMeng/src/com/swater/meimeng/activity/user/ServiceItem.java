package com.swater.meimeng.activity.user;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.meimeng.app.R;
import com.swater.meimeng.commbase.BaseTemplate;

public class ServiceItem extends BaseTemplate {
	TextView txt = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_item);
		TempRun();

	}

	@Override
	public void iniView() {
		showTitle("服务条款");
		showNavgationLeftBar("返回");

	}

	@Override
	public void bindClick() {
		bindNavgationEvent();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_left_btn: {
			onBackPressed();
		}

			break;

		default:
			break;
		}
	}

}
