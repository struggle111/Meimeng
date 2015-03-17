package com.swater.meimeng.activity.user;

import android.os.Bundle;
import android.view.View;

import com.meimeng.app.R;
import com.swater.meimeng.commbase.BaseTemplate;

public class AboutUs extends BaseTemplate {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutme);
		TempRun();
	}

	@Override
	public void iniView() {
		showNavgationLeftBar("返回");
		view_Hide(findViewById(R.id.home_right_btn));
		showTitle("关于我们");
		// TODO Auto-generated method stub

	}

	@Override
	public void bindClick() {
		bindNavgationEvent();
		// TODO Auto-generated method stub

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

}
