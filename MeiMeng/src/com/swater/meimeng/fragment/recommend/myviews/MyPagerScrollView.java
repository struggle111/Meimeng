package com.swater.meimeng.fragment.recommend.myviews;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.meimeng.app.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.swater.meimeng.activity.adapterGeneral.vo.VoPhoto;

/**
 * 
 * @author chengshiyang
 * 
 */
public class MyPagerScrollView extends HorizontalScrollView {
	private int subChildCount = 0;
	private ViewGroup firstChild = null;
	private int downX = 0;
	private int currentPage = 0;
	private ArrayList<Integer> pointList = new ArrayList<Integer>();
	public LayoutInflater inflater;
	public DisplayImageOptions options;
	public ImageLoader imageLoader = null;
	public int wid, height_img;
	ScrollPageIndexInterface pageIndexListener = null;

	public ScrollPageIndexInterface getPageIndexListener() {
		return pageIndexListener;
	}

	public void setPageIndexListener(ScrollPageIndexInterface pageIndexListener) {
		this.pageIndexListener = pageIndexListener;
	}

	public int getWid() {
		return wid;
	}

	public void setWid(int wid) {
		this.wid = wid;
	}

	public int getHeight_img() {
		return height_img;
	}

	public void setHeight_img(int height_img) {
		this.height_img = height_img;
	}

	public DisplayImageOptions getOptions() {
		return options;
	}

	public void setOptions(DisplayImageOptions options) {
		this.options = options;
	}

	List<VoPhoto> photos = new ArrayList<VoPhoto>();

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public LayoutInflater getInflaterRoot() {
		return inflater;
	}

	public void setInflater(LayoutInflater inflater) {
		this.inflater = inflater;
	}

	@Deprecated
	public void addSubViews(List<VoPhoto> datas, Context con, ViewGroup vggp) {
		photos.addAll(datas);
		LayoutParams params = new LayoutParams(getWid(), getHeight_img());
		for (VoPhoto voPhoto : datas) {
			ImageView cellImg = new ImageView(con);
			cellImg.setLayoutParams(params);
			cellImg.setImageResource(R.drawable.female_default);
			cellImg.setScaleType(ScaleType.CENTER);
			cellImg.setBackgroundColor(Color.WHITE);
			// mContainer.addView(imageView5);
			// View cell = this.getCellView(voPhoto.getUrl());
			vggp.addView(cellImg);
		}

	}

	/**
	 * private View getCellView(String url) {
	 * 
	 * View imageLayout = getInflaterRoot().inflate(R.layout.recommend_cell,
	 * null); final ImageView imageView = (ImageView) imageLayout
	 * .findViewById(R.id.recom_photo);
	 * imageLayout.findViewById(R.id.recom_content).setVisibility(View.GONE);
	 * ProgressBar spinner = (ProgressBar) imageLayout
	 * 
	 * return imageLayout; }
	 */

	public MyPagerScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MyPagerScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MyPagerScrollView(Context context) {
		super(context);
		init();
	}

	private void init() {
		setHorizontalScrollBarEnabled(false);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		receiveChildInfo();
	}

	public void receiveChildInfo() {

		firstChild = (ViewGroup) getChildAt(0);
		if (firstChild != null) {
			subChildCount = firstChild.getChildCount();
			for (int i = 0; i < subChildCount; i++) {
				if (((View) firstChild.getChildAt(i)).getWidth() > 0) {
					pointList.add(((View) firstChild.getChildAt(i)).getLeft());
				}
			}
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = (int) ev.getX();
			break;
		case MotionEvent.ACTION_MOVE: {

		}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL: {
			if (Math.abs((ev.getX() - downX)) > getWidth() / 4) {
				if (ev.getX() - downX > 0) {
					smoothScrollToPrePage();
					if (pageIndexListener != null) {
						pageIndexListener.ScrollPageIndex(currentPage);
					}
				} else {
					smoothScrollToNextPage();
					if (pageIndexListener != null) {
						pageIndexListener.ScrollPageIndex(currentPage);
					}
				}
			} else {
				smoothScrollToCurrent();
				if (pageIndexListener != null) {
					pageIndexListener.ScrollPageIndex(currentPage);
				}
			}
			return true;
		}
		}
		return super.onTouchEvent(ev);
	}

	private void smoothScrollToCurrent() {
		smoothScrollTo(pointList.get(currentPage), 0);
	}

	private void smoothScrollToNextPage() {
		if (currentPage < subChildCount - 1) {
			currentPage++;
			smoothScrollTo(pointList.get(currentPage), 0);
		}
	}

	private void smoothScrollToPrePage() {
		if (currentPage > 0) {
			currentPage--;
			smoothScrollTo(pointList.get(currentPage), 0);
		}
	}

	/**
	 * ��һҳ
	 */
	public void nextPage() {
		smoothScrollToNextPage();
	}

	/**
	 * ��һҳ
	 */
	public void prePage() {
		smoothScrollToPrePage();
	}

	/**
	 * ��ת��ָ����ҳ��
	 * 
	 * @param page
	 * @return
	 */
	public boolean gotoPage(int page) {
		if (page > 0 && page < subChildCount - 1) {
			smoothScrollTo(pointList.get(page), 0);
			currentPage = page;
			return true;
		}
		return false;
	}

	public interface ScrollPageIndexInterface {

		public void ScrollPageIndex(int pageCurrent);
	}
}
