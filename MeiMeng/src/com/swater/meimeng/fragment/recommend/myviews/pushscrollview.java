package com.swater.meimeng.fragment.recommend.myviews;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ScrollView;

public class pushscrollview extends ScrollView  {
	
//	-----begin-------
	
	private OnScrollListener onScrollListener = null;

	private View viewInScroll,viewOutScroll;
	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);
		if (onScrollListener != null) {
			onScrollListener.onScrollChanged(this, x, y, oldx, oldy);
		}
		computeFloatIfNecessary();
	}

	/**
	 * 监听ScrollView滚动接口
	 * @author reyo
	 *
	 */
	public interface OnScrollListener {

		public void onScrollChanged(pushscrollview scrollView, int x,
				int y, int oldx, int oldy);

	}
	
	/**
	 * 设置需要浮动的View
	 * @param viewInScroll ScollView内的view
	 * @param viewFloat ScollView外的view，真正需要浮动的view
	 */
	public void setFloatView(View viewInScroll,View viewOutScroll){
		this.viewInScroll=viewInScroll;
		this.viewOutScroll=viewOutScroll;
	}
	
	private void computeFloatIfNecessary(){
		if(viewInScroll==null&&viewOutScroll==null){
			return;
		}
		// 获取ScrollView的x,y坐标
		int[] location = new int[2];
		this.getLocationInWindow(location);
		// 获取浮动View的x,y坐标
		int[] loc = new int[2];
		viewInScroll.getLocationOnScreen(loc);
		// 当浮动view的y <= ScrollView的y坐标时，把固定的view显示出来
		if (loc[1] <= location[1]) {
			// 处理一下把原有view设置INVISIBLE，这样显示效果会好点
			viewOutScroll.setVisibility(View.VISIBLE);
			viewInScroll.setVisibility(View.INVISIBLE);
		} else {
			// 记得还原回来
			viewOutScroll.setVisibility(View.GONE);
			viewInScroll.setVisibility(View.VISIBLE);
		}
	}
	
//	------end----------

	public interface onTapScroll {

		public void TapScroll(float y);
	}

	public onTapScroll tapscroll_listener;

	public onTapScroll getTapscroll_listener() {
		return tapscroll_listener;
	}

	public void setTapscroll_listener(onTapScroll tapscroll_listener) {
		this.tapscroll_listener = tapscroll_listener;
	}

	public pushscrollview(Context context) {
		super(context);
		init(context);
	}

	public pushscrollview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	void init(Context context) {
		genster = new GestureDetector(context, new SimpleSwip());
		// setOnTouchListener(this);
		setFadingEdgeLength(0); 
	
	}
  


  @Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.onInterceptTouchEvent(ev) && genster.onTouchEvent(ev);
	}

	public void fling(int velocityY) {
		
           // super.fling(velocityY * 4/3);
        
		// if (tapscroll_listener!=null) {
		// tapscroll_listener.TapScroll(velocityY);
		// }

	};

	GestureDetector genster = null;

	class SimpleSwip extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {

			return Math.abs(distanceY) > Math.abs(distanceX);
		}
	}

//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		if (event.getAction() == MotionEvent.ACTION_MOVE) {
//			if (tapscroll_listener != null) {
//				tapscroll_listener.TapScroll(this.getScrollY());
//			}
//			// 监听到ScrollView的滚动事件
//			Log.i("tapscroll_listener",
//					"tapscroll_listener y=" + this.getScrollY());
//		}
//		return false;
//	}


}
