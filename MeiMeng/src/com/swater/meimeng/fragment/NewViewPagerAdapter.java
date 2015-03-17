package com.swater.meimeng.fragment;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class NewViewPagerAdapter extends PagerAdapter {

	private List<View> listViews;

	public List<View> getListViews() {
		return listViews;
	}

	public void setListViews(List<View> listViews) {
		this.listViews = listViews;
	}

	public OnInitItemListener getListener() {
		return listener;
	}

	public void setListener(OnInitItemListener listener) {
		this.listener = listener;
	}

	private OnInitItemListener listener;

	public NewViewPagerAdapter(List<View> listViews) {
		this.listViews = listViews;
	}

	public NewViewPagerAdapter(List<View> listViews, OnInitItemListener listener) {
		this.listViews = listViews;
		this.listener = listener;
	}

	

	@Override
	public int getCount() {
		if (listViews != null) {
			return listViews.size();
		}
		return 0;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		container.addView(listViews.get(position));
		if (listener != null) {
			listener.initItem(container, position);
		}

		return listViews.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(listViews.get(position));
	}

	public interface OnInitItemListener {
		void initItem(ViewGroup container, int position);
	}
	public interface OnloadNextPage {
		void initItem(ViewGroup container, int position);
	}

}
