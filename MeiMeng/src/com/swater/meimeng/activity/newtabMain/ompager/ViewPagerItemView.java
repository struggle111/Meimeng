package com.swater.meimeng.activity.newtabMain.ompager;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.meimeng.app.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.swater.meimeng.activity.adapterGeneral.vo.UserSearchVo;
import com.swater.meimeng.activity.newtabMain.IndexImageDownloadTask;
import com.swater.meimeng.commbase.MeiMengApp;
import com.swater.meimeng.fragment.recommend.BarPushActivity;
import com.swater.meimeng.mutils.NSlog.NSLoger;

/**
 * @author frankiewei 相册的ItemView,自定义View.方便复用.
 */
public class ViewPagerItemView extends FrameLayout {
	Context con = null;
LinkedList<IndexImageDownloadTask> down_quences=new LinkedList<IndexImageDownloadTask>();
	/**
	 * 图片的ImageView.
	 */
	private ImageView mAlbumImageView;

	/**
	 * 图片名字的TextView.
	 */
	private TextView mALbumNameTextView;

	/**
	 * 图片的Bitmap.
	 */
	private Bitmap mBitmap;

	/**
	 * 要显示图片的JSONOBject类.
	 */
	// private JSONObject mObject;
	private UserSearchVo mObject;
	public void release(){
		if (mAlbumImageView!=null) {
			BitmapDrawable dr=(BitmapDrawable)mAlbumImageView.getBackground();
			if (dr!=null) {
				
				Bitmap bm=dr.getBitmap();
				if (bm!=null) {
					if (!bm.isRecycled()) {
						bm.recycle();
						bm=null;
						NSLoger.Log("回收！！--========");
						
					}
				}
			}
			
		}
		if (down_quences.size()>0) {
			for (IndexImageDownloadTask ta : down_quences) {
				if (!ta.isCancelled()) {
					ta.cancel(true);
				Log.d("-release-释放首页队列-", "---"+this.getClass().getName());
				}
				
			}
		}
	}

	public ViewPagerItemView(Context context, MeiMengApp app) {
		super(context);
		setupViews();
		iniImgloader();
	}

	public ViewPagerItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupViews();
	}

	// 初始化View.
	private void setupViews() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.recommend_cell, null);

		mAlbumImageView = (ImageView) view.findViewById(R.id.recom_photo);
		mALbumNameTextView = (TextView) view.findViewById(R.id.recom_content);

		addView(view);
	}

	OnClickListener im = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stubj
			jumpPage();

		}
	};
	protected ImageLoader imageLoader = null;
	DisplayImageOptions options = null;
	private Map<String, SoftReference<Bitmap>> maps = null;

	void iniImgloader() {
		if (imageLoader == null) {

			imageLoader = ImageLoader.getInstance();
		}
		if (options == null) {

			// options = new DisplayImageOptions.Builder()
			// .showImageForEmptyUri(R.drawable.ic_empty)
			// .showImageOnFail(R.drawable.ic_error)
			// .resetViewBeforeLoading(true).cacheOnDisc(true)
			// .imageScaleType(ImageScaleType.)
			// .bitmapConfig(Bitmap.Config.RGB_565)
			// .displayer(new FadeInBitmapDisplayer(300)).build();

		}
		if (con instanceof Activity) {
			Activity act = (Activity) con;
			maps = ((MeiMengApp) act.getApplication()).getMapParams_bit();
		}
		if (maps == null) {

			maps = new HashMap<String, SoftReference<Bitmap>>();
		}
	}

	/**
	 * 填充数据，共外部调用.
	 * 
	 * @param object
	 */
	/**
	 * public void setData(UserSearchVo ob, Context con) { try { this.mObject =
	 * ob; this.con = con; mAlbumImageView.setOnClickListener(im);
	 * mALbumNameTextView.setText(ob.getContent());
	 * 
	 * // // IndexImageDownloadTask task = new IndexImageDownloadTask(con); //
	 * try { // Thread.sleep(100); // task.execute(ob.getHead_url(),
	 * mAlbumImageView); // } catch (Exception e) { // } final ImageView
	 * imageView=mAlbumImageView; final String url=ob.getHead_url();
	 * 
	 * if (maps.containsKey(url) && maps.get(url).get() != null) {
	 * imageView.setImageBitmap(maps.get(url).get()); //
	 * spinner.setVisibility(View.GONE);
	 * 
	 * } else {
	 * 
	 * imageLoader.displayImage(url, imageView, options, new
	 * SimpleImageLoadingListener() {
	 * 
	 * @Override public void onLoadingStarted(String imageUri, View view) { //
	 *           if (userinfo_vo.getSex() == 1) { // imageView //
	 *           .setImageResource(R.drawable.man_default); // // } else {
	 *           imageView .setImageResource(R.drawable.female_default); // } //
	 *           spinner.setVisibility(View.VISIBLE); }
	 * @Override public void onLoadingFailed(String imageUri, View view,
	 *           FailReason failReason) { //
	 *           img_handler.obtainMessage(690).sendToTarget(); try {
	 *           Thread.sleep(300);
	 * 
	 *           } catch (Exception e) { } // spinner.setVisibility(View.GONE);
	 *           // if (userinfo_vo.getSex() == 1) { // imageView //
	 *           .setImageResource(R.drawable.man_default); // // } else {
	 *           imageView .setImageResource(R.drawable.female_default); // } }
	 * @Override public void onLoadingComplete(String imageUri, View view,
	 *           Bitmap loadedImage) { // spinner.setVisibility(View.GONE);
	 * 
	 *           if (loadedImage != null) { // showToast("--3--"); maps.put(url,
	 *           new SoftReference<Bitmap>( loadedImage));
	 *           imageView.setImageBitmap(loadedImage); } else { //
	 *           showToast("--2--"); //
	 *           handler.obtainMessage(991).sendToTarget(); } }
	 * @Override public void onLoadingCancelled(String imageUri, View view) {
	 *           super.onLoadingCancelled(imageUri, view);
	 * 
	 *           } }); } } catch (Exception e) { e.printStackTrace(); }
	 * 
	 *           }
	 */

	

	public void setData(UserSearchVo ob, Context con) {
		try {
			this.mObject = ob;
			this.con = con; // int resId = object.getInt("resid"); //
			// String name = object.getString("name");
			mAlbumImageView.setOnClickListener(im);

			try {
				// imageFetcher.loadFormCache(ob.getHead_url(),
				// mAlbumImageView);
				// imageLoader.displayImage(ob.getHead_url(), mAlbumImageView);
//				if (this.maps!=null&&this.maps.containsKey(ob.getHead_url())) {
//
//					mAlbumImageView.setImageBitmap(this.maps.get(
//							ob.getHead_url()).get());
//				} else {
				IndexImageDownloadTask task = new IndexImageDownloadTask(
							con);
					Thread.sleep(100);
					task.execute(ob.getHead_url(), mAlbumImageView);
					down_quences.add(task);

//				}
				mALbumNameTextView.setText(ob.getContent());
			} catch (Exception e) {
				
				e.printStackTrace();
				//
				// TODO: handle exception } // downs_quences.add(task); //
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Deprecated
	public void setData11(UserSearchVo ob, Context con) {
		try {
			this.mObject = ob;
			this.con = con; // int resId = object.getInt("resid"); //
			// String name = object.getString("name");
			mAlbumImageView.setOnClickListener(im);
			IndexImageDownloadTask task = new IndexImageDownloadTask(con);
			
			try {
				Thread.sleep(100);
				task.execute(ob.getHead_url(), mAlbumImageView);
				// imageLoader.displayImage(ob.getHead_url(), mAlbumImageView);
				mALbumNameTextView.setText(ob.getContent());
			} catch (Exception e) { //
				// TODO: handle exception } // downs_quences.add(task); //
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 这里内存回收.外部调用.
	 */
	public void recycle() {
		mAlbumImageView.setImageBitmap(null);
		if ((this.mBitmap == null) || (this.mBitmap.isRecycled()))
			return;
		this.mBitmap.recycle();
		this.mBitmap = null;
	}

	void jumpPage() {
		Intent in = new Intent();
		// in.setClass(this.con, PersonalSwitchImgs.class);
//		in.setClass(this.con, BeautyDetail.class);
//		in.setClass(this.con, BeautyActivity.class);
		in.setClass(this.con, BarPushActivity.class);
		// in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		in.putExtra("data", this.mObject);
		con.startActivity(in);
	}

//	Map<String, SoftReference<Bitmap>> maps_bit = null;

	/**
	 * 重新加载.外部调用.
	 */
	/**
	 * public boolean reload() { try {
	 * 
	 * if (true) { Log.d(" --推荐首页复用试图--reload缓存！---", "-->>"); return true; } //
	 * int resId = mObject.getInt("resid"); // //
	 * 实战中如果图片耗时应该令其一个线程去拉图片异步,不然把UI线程卡死. //
	 * mAlbumImageView.setImageResource(resId); if (this.con != null) { if
	 * (this.con instanceof Activity) { MeiMengApp app = (MeiMengApp)
	 * ((Activity) this.con) .getApplication(); maps_bit =
	 * app.getMapParams_bit(); if (maps_bit != null) { if
	 * (maps_bit.containsKey(this.mObject.getHead_url())) { Bitmap bp = maps_bit
	 * .get(this.mObject.getHead_url()).get(); if (bp != null) {
	 * mAlbumImageView.setImageBitmap(bp); Log.d("--成功加载系统应用内部缓存！---", "-->>");
	 * return true; }
	 * 
	 * } } } } IndexImageDownloadTask task = new IndexImageDownloadTask(con);
	 * try { Thread.sleep(100); task.execute(this.mObject.getHead_url(),
	 * mAlbumImageView); mALbumNameTextView.setText(this.mObject.getContent());
	 * } catch (Exception e) { // TODO: handle exception } } catch (Exception e)
	 * { e.printStackTrace(); } return false; }
	 */

}
