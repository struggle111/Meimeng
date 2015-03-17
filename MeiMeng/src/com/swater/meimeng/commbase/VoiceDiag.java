package com.swater.meimeng.commbase;

import com.meimeng.app.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * @category 自定义录音对话框
 */
public class VoiceDiag extends Dialog implements
		android.view.View.OnClickListener {
	OnOverRecordClick event = null;
	View layview = null;

	public void setEvent(OnOverRecordClick event) {
		this.event = event;
	}

	/**
	 * @category 定义录音结束点击事件
	 */
	public interface OnOverRecordClick {

		public void RecordOver(View v);
	}

	public VoiceDiag(Context context) {
		super(context);
		layview = LayoutInflater.from(context).inflate(
				R.layout.record_diag_view, null);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		Object obj = getContext().getSystemService(Context.WINDOW_SERVICE);
//		WindowManager wm = (WindowManager) obj;
//		int envWidth = wm.getDefaultDisplay().getWidth();
//		int envHeight = wm.getDefaultDisplay().getHeight();

		if (null!=layview) {
			setContentView(layview);
			layview.findViewById(R.id.record_over_btn).setOnClickListener(this);
		}
        setCanceledOnTouchOutside(false);
	}
	@Override
	public void onClick(View v) {
		event.RecordOver(v);
	}

}
