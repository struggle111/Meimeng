package com.swater.meimeng.activity.user.swipepage;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

public class ScrollViewPage extends ViewPager {
	@Override
	public boolean canScrollHorizontally(int direction) {
		return true;
//		return super.canScrollHorizontally(direction);
	}

	/** 是否禁用滑动事件 */
	boolean isForbiddenScroll = true;

	/** 设置是否禁用！true 时禁用滑动事件！-false开启滑动 */
	public void setForbiddenScroll(boolean isForbiddenScroll) {
		this.isForbiddenScroll = isForbiddenScroll;
	}

	public ScrollViewPage(Context context) {
		super(context);
	}

	public ScrollViewPage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void scrollTo(int x, int y) {
	int wid=x;
	int hei=y;
//	Log.d("scrollTo--x---"+wid, "");
//	Log.d("scrollTo----y---"+hei, "");
		
//		if (isForbiddenScroll==true) {
//			Log.d("--关闭滑动--", "");
//			
//		}else{
//			Log.d("--开启滑动---", "");
			super.scrollTo(x, y);
			
//		}

		// if (isForbiddenScroll== true) {
		// Log.d("--ScrollViewPage-->-"+"禁用滑动", "");
		// return;
		//
		// }else{
		// super.scrollTo(x, y);
		// }

	}

}
