package com.swater.meimeng.activity.user;

import com.meimeng.app.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class tempImgActivity  extends Activity{
	ImageView upload=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg_upload);
		upload=(ImageView)findViewById(R.id.upload_bg);
	Bitmap bmp=(Bitmap)	this.getIntent().getExtras().get("bmp");
	if (bmp!=null) {
		upload.setImageBitmap(bmp);
	}
	}

}
