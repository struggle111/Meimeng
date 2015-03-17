package com.swater.meimeng.fragment.recommend;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meimeng.app.R;

public class MedalView extends RelativeLayout {
	ImageView imv = null;
	TextView txt = null;
	String TAG = "--MedalView---";

	public MedalView(Context context) {
		super(context);
		imv = new ImageView(context);
		txt = new TextView(context);

	}

	public void setMedalName(String name) {

		if (!TextUtils.isEmpty(name) && !"null".equals(name)) {
			if (txt != null) {
				txt.setText(name);
			}

		}
	}

	public MedalView(Context context, int id) {
		super(context);
		imv = new ImageView(context);
		txt = new TextView(context);
		// FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,Gravity.BOTTOM);
		// FrameLayout.LayoutParams txt_layoutParams = new
		// FrameLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.TOP);

		int wid = 150;// LayoutParams.WRAP_CONTENT;//130
		RelativeLayout.LayoutParams im_medal = new RelativeLayout.LayoutParams(
				wid, LayoutParams.WRAP_CONTENT);
		im_medal.bottomMargin = 0;
		im_medal.topMargin = 115;
		im_medal.leftMargin = 0;
		imv.setLayoutParams(im_medal);

		RelativeLayout.LayoutParams txt_medal = new RelativeLayout.LayoutParams(
				wid, LayoutParams.WRAP_CONTENT);
		// RelativeLayout.LayoutParams txt_pa = new RelativeLayout.LayoutParams(
		// 40,40);
		txt_medal.bottomMargin = 60;
		txt.setGravity(Gravity.CENTER);
		txt.setVisibility(View.GONE);
		txt.setBackgroundResource(R.color.halftrans_black);
		txt.setText("美盟勋章");
		txt.setLayoutParams(txt_medal);
		imv.setImageResource(id);
		imv.setScaleType(ScaleType.CENTER_INSIDE);
		// imv.addFocusables(ls);
		// imv.addTouchables(ls);

		imv.setOnTouchListener(new touch());
		addView(imv);

		addView(txt);

	}

	class touch implements OnTouchListener {
		private static final long LOCK_TOUCH_IN_MILLIS = 120;

		private long touchTime = 0l;
		private boolean moving = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (v instanceof ImageView) {
				// return false;
			}

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				moving = false;
				txt.setVisibility(View.VISIBLE);
				touchTime = Calendar.getInstance().getTimeInMillis();
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				long moveTime = Calendar.getInstance().getTimeInMillis();
				if (!moving) {
					Log.d(TAG, "moveTime(" + moveTime + ") - touchTime("
							+ touchTime + ") = " + (moveTime - touchTime));
					if (moveTime - touchTime > LOCK_TOUCH_IN_MILLIS) {

					} else {

						return true;
					}
				}

				moving = true;

			} else if (event.getAction() == MotionEvent.ACTION_UP) {

				moving = false;
				txt.setVisibility(View.GONE);
			}

			return true;
		}

	}

}
