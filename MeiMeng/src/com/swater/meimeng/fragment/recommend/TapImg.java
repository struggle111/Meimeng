/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.swater.meimeng.fragment.recommend;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.meimeng.app.R;
import com.swater.meimeng.mutils.constant.GeneralUtil;
import com.swater.meimeng.mutils.imagnifytools.PhotoViewAttacher;
import com.swater.meimeng.mutils.imagnifytools.PhotoViewAttacher.OnMatrixChangedListener;
import com.swater.meimeng.mutils.imagnifytools.PhotoViewAttacher.OnPhotoTapListener;

/**
 * @category 图片缩放控件
 */
public class TapImg extends Activity implements OnClickListener {

	private ImageView mImageView;

	private PhotoViewAttacher mAttacher;
	String url = "";

	Context context = null;
	ProgressBar pro = null;
	DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tap_newimgdiag);
		context = this;

		ini();

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

	@Override
	protected void onStart() {
		super.onStart();
		if (imageLoader != null) {
			imageLoader.resume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (imageLoader != null) {
			imageLoader.pause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (imageLoader != null) {
			imageLoader.resume();
		}
	}

	void ini() {
		findViewById(R.id.home_left_btn).setOnClickListener(this);
		mImageView = (ImageView) findViewById(R.id.imgshow_);
		pro = (ProgressBar) findViewById(R.id.loading);
		GeneralUtil.view_Hide(findViewById(R.id.home_right_btn));
		parserUrl();
		GeneralUtil.setValueToView(findViewById(R.id.center_show), "查看原图");
		GeneralUtil.setValueToView(findViewById(R.id.home_left_btn), "返回");
		GeneralUtil.view_Show(findViewById(R.id.home_left_btn));
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.female_default)
				.showImageOnFail(R.drawable.female_default)
				.resetViewBeforeLoading().cacheOnDisc()
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory()
				.displayer(new FadeInBitmapDisplayer(100)).build();
		imageLoader.resume();

		if (!TextUtils.isEmpty(url)) {
			loadImg();

		} else {
			Toast.makeText(context, "图片地址错误！", 2).show();
		}

	}

	void parserUrl() {
		if (getIntent() != null) {
			url = getIntent().getStringExtra("url");
		}
	}

	void loadImg() {

		imageLoader.displayImage(url, mImageView, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						pro.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						String message = null;
						switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
						}
						Toast.makeText(context, message + imageUri,
								Toast.LENGTH_SHORT).show();

						pro.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						pro.setVisibility(View.GONE);

						mAttacher = new PhotoViewAttacher(mImageView);
						//
						mAttacher
								.setOnMatrixChangeListener(new MatrixChangeListener());
						mAttacher.setOnPhotoTapListener(new PhotoTapListener());

					}
				});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mAttacher != null) {
			mAttacher.cleanup();

		}
		if (imageLoader != null) {
			imageLoader.stop();
		}
	}

	private class PhotoTapListener implements OnPhotoTapListener {

		@Override
		public void onPhotoTap(View view, float x, float y) {
			float xPercentage = x * 100f;
			float yPercentage = y * 100f;

			/**
			 * if (null != mCurrentToast) { mCurrentToast.cancel(); }
			 * 
			 * mCurrentToast = Toast.makeText(TapImg.this, String.format(
			 * PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage),
			 * Toast.LENGTH_SHORT); mCurrentToast.show();
			 */
		}
	}

	private class MatrixChangeListener implements OnMatrixChangedListener {

		@Override
		public void onMatrixChanged(RectF rect) {
			// mCurrMatrixTv.setText(rect.toString());
		}
	}

}
