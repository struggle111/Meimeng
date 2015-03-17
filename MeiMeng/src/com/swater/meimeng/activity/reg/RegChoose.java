package com.swater.meimeng.activity.reg;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.meimeng.app.R;
import com.swater.meimeng.commbase.BaseTemplate;

public class RegChoose extends BaseTemplate {

	ImageView iv_sex;
	LinearLayout layout_choose;
	Button btn_left, btn_right;
	int chooseValue = 0;
	Animation alphaAnimation;

	SoundPool sp;
	int musicFemale;
	int musicMale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_reg);
		this.TempRun();

	}

	

	@Override
	public void iniView() {
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// showNavgationRightBar("");
		view_Show(findViewById(R.id.home_right_btn));
		view_Show(findViewById(R.id.home_left_btn));
		showNavgationLeftBar("");
		showNavgationRightBar("下一步");

		layout_choose = (LinearLayout) findViewById(R.id.layout_choose);
		iv_sex = (ImageView) findViewById(R.id.iv_sex);
		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);

		alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
		alphaAnimation.setDuration(1000);
		
		sp= new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		musicFemale = sp.load(this, R.raw.genderclickfemale, 1);
		musicMale = sp.load(this, R.raw.genderclickmale, 1); 
	}

	@Override
	public void bindClick() {
		bindNavgationEvent();
	}

	@Override
	public void onClick(View v) {

		// in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if ( v.getId() == R.id.home_left_btn) {
			onBackPressed();
			return;
		}

		switch (v.getId()) {

		case R.id.btn_left:
			sp.play(musicMale, 1, 1, 0, 0, 1);
			iv_sex.setVisibility(View.VISIBLE);
			iv_sex.setImageResource(R.drawable.male_show);
			iv_sex.startAnimation(alphaAnimation);
			layout_choose.setBackgroundResource(R.drawable.male);
			chooseValue = 1;
			break;

		case R.id.btn_right:
			sp.play(musicFemale, 1, 1, 0, 0, 1);
			iv_sex.setVisibility(View.VISIBLE);
			iv_sex.setImageResource(R.drawable.female_show);
			iv_sex.startAnimation(alphaAnimation);
			layout_choose.setBackgroundResource(R.drawable.female);
			chooseValue = 2;
			break;
			
		case R.id.home_right_btn: {
			
			if (chooseValue==2) {
				Intent in = new Intent(t_context, UserRegActivity.class);

				// 女
				in.putExtra("tag", 1);
				t_context.startActivity(in);

				
			}else if(chooseValue==1){
				Intent in = new Intent(t_context, UserManRegActivity.class);
				in.putExtra("tag", 2);
				t_context.startActivity(in);
				
			}else {
				showToast("请先选择性别！");
			}
			
		}

			break;

		default:
			break;
		}

	}
}
