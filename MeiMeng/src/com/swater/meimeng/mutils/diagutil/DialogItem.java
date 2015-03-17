package com.swater.meimeng.mutils.diagutil;


public class DialogItem {
	private  boolean mIsSpecial;
	private int mTextId;
	private int mViewId;
	
	public  DialogItem(int textId, int viewId){
		mTextId = textId;
		mViewId = viewId;
	}
	//点击事件
	public void onClick(){
	}

	public boolean isSpecial() {
		return mIsSpecial;
	}

	public void setSpecial(boolean mIsSpecial) {
		this.mIsSpecial = mIsSpecial;
	}

	public int getTextId() {
		return mTextId;
	}

	public void setTextId(int mTextId) {
		this.mTextId = mTextId;
	}

	public int getViewId() {
		return mViewId;
	}

	public void setViewId(int mViewId) {
		this.mViewId = mViewId;
	}

	
	
		
	
}

