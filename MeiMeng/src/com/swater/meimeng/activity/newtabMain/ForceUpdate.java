package com.swater.meimeng.activity.newtabMain;

import com.meimeng.app.R;
import com.swater.meimeng.activity.user.UserSettingActivity;
import com.swater.meimeng.service.CheckVersionService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ForceUpdate extends Activity implements OnClickListener {
	Button btn_down = null;
	String ver_url = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.force_update);
		findViewById(R.id.home_left_btn).setVisibility(View.GONE);
		btn_down = (Button) findViewById(R.id.btn_donwload);
		btn_down.setOnClickListener(this);
		TextView txt=(TextView)findViewById(R.id.center_show);
		txt.setText("版本更新");
		ver_url = this.getIntent().getStringExtra("url");
	}

	@Override
	public void onClick(View v) {
		downAPK();
	}

	protected void downAPK() {
		Intent updateIntent = new Intent(ForceUpdate.this,
				CheckVersionService.class);
		if (!TextUtils.isEmpty(ver_url)) {
			updateIntent.putExtra("apkUrl", ver_url);
			startService(updateIntent);
		}
	}

}
