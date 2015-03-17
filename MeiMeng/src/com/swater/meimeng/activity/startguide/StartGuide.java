package com.swater.meimeng.activity.startguide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.meimeng.app.R;
import com.swater.meimeng.activity.newtabMain.IndexActivity;
import com.swater.meimeng.activity.user.UserLogin;
import com.swater.meimeng.database.ShareUtil;

public class StartGuide extends Activity implements OnViewChangeListener,
		OnClickListener {
	boolean isLogin = false;
	private MyScrollLayout mScrollLayout;

	private ImageView[] mImageViews;

	private int mViewCount;

	private int mCurSel;
	Context context = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		isLogin = ShareUtil.getInstance(this).getUserid()>0?true:false;
		setContentView(R.layout.guide_main);

		init();
	}

	void goMain() {
		Intent in = new Intent();
		in.setClass(context, IndexActivity.class);
		context.startActivity(in);
	}

	private void init() {
		mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
		mScrollLayout.findViewById(R.id.st_g_6).setOnClickListener(this);

		mViewCount = mScrollLayout.getChildCount();

		mScrollLayout.SetOnViewChangeListener(this);
	}


	@Override
	public void OnViewChange(int view) {
	}

	@Override
	public void onClick(View v) {
		if (isLogin != true) {
			Intent i = new Intent();
			i.setClass(context, UserLogin.class);
			finish();
			context.startActivity(i);
		} else {

			goMain();
		}
	}
}
