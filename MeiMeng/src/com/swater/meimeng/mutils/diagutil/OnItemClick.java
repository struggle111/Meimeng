package com.swater.meimeng.mutils.diagutil;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;

public class OnItemClick implements OnClickListener{
	private DialogItem mItem;
	private Dialog mDialog;
	public OnItemClick(DialogItem item,Dialog dialog){
		mItem = item;
		mDialog = dialog;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(mItem != null) 
			mItem.onClick();
		
		if(mDialog != null){
			mDialog.dismiss();
			mDialog = null;
		}
	}

}
