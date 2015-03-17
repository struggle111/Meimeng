package com.swater.meimeng.activity.user.swipepage;

import java.util.LinkedList;
import java.util.List;

import com.swater.meimeng.activity.adapterGeneral.AdapterSearch.Cell_Act_click;
import com.swater.meimeng.activity.adapterGeneral.vo.UserSearchVo;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.OMdown.ImageDownloadTask;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ViewPagerAdapter extends PagerAdapter {
	LinkedList<UserSearchVo> ls_vo = new LinkedList<UserSearchVo>();
	public scroll_callback call_scroll = null;

	public scroll_callback getCall_scroll() {
		return call_scroll;
	}
	public ViewPagerAdapter() {
		// TODO Auto-generated constructor stub
	}

	public void setCall_scroll(scroll_callback call_scroll) {
		this.call_scroll = call_scroll;
	}

	public LinkedList<UserSearchVo> getLs_vo() {
		return ls_vo;
	}

	public void setLs_vo(LinkedList<UserSearchVo> ls_vo) {
		this.ls_vo = ls_vo;
	}

	public interface scroll_callback {
		void scroll_callback_img(View v, int pos);

	}

	// 界面列表
	private List<View> views;

	public ViewPagerAdapter(List<View> views) {
		this.views = views;
	}

	public List<View> getViews() {
		return views;
	}

	public void setViews(List<View> views) {
		this.views = views;
	}

	// 销毁arg1位置的界面
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		System.out.println(views.size() + "     " + arg1);
		if (arg1 > views.size()) {
			return;
		}
		if (views.size() == arg1)
			arg1 = arg1 - 1;
		if (views.size() == 0) {
			return;
		}

		((ViewPager) arg0).removeView(views.get(arg1));

	}

	@Override
	public void finishUpdate(View arg0) {
		NSLoger.Log("-- finishUpdate--->");
	}

	// 获得当前界面数
	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	// 初始化arg1位置的界面
	@Override
	public Object instantiateItem(View arg0, int index) {
		NSLoger.Log("-- 初始化arg1位置的界面-instantiateItem--->" + index);
		int pageNo = 1;
		((ViewPager) arg0).addView(views.get(index), 0);

		return views.get(index);
	}

	// 判断是否由对象生成界面
	@Override
	public boolean isViewFromObject(View cell_make, Object arg1) {
		NSLoger.Log("-- 生成--isViewFromObject--->");

		return (cell_make == arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		NSLoger.Log("--restoreState--->");

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View cellupdate) {

		ViewGroup vg = (ViewGroup) cellupdate;
		Integer pos = (Integer) vg.getTag();
		if (vg != null) {
			for (int i = 0; i < vg.getChildCount(); i++) {
				if (vg.getChildAt(i) instanceof ImageView) {
					ImageView ph = (ImageView) vg.getChildAt(i);
					if (ph.getTag().equals("1")) {
						// ImageDownloadTask task = new ImageDownloadTask();
						String url = "";
						if (pos < ls_vo.size()) {
							NSLoger.Log("--更新--startUpdate--->");
							url = ls_vo.get(pos).getPhotoUrl();
							// task.execute(ph, url);
						}

					}
					break;

				}

			}

		}

	}
	// or (int i = 0; i < viewRight.getChildCount(); i++) {
	// if (!(viewRight.getChildAt(i) instanceof MateButtonGroupView)) {
	// continue;
	// }

}
