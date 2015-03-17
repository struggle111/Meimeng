package com.swater.meimeng.commbase;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.meimeng.app.R;
import com.swater.meimeng.database.ShareUtil;
import com.swater.meimeng.mutils.constant.GeneralUtil;

public class DiagPopView implements OnClickListener {
	public interface Diag_ok_no {
		public void btn_ok_no(View v);
	}

	private Context context;
	private PopupWindow popupWindow;
	private Diag_ok_no listener;
	private LayoutInflater inflater;
	public View view = null;
	ShareUtil sh = null;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public DiagPopView(Context context) {
		this.context = context;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.lock_release, null, true);
		view.findViewById(R.id.app_no).setOnClickListener(this);
		view.findViewById(R.id.app_yes).setOnClickListener(this);
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
		popupWindow.setOutsideTouchable(true);

	}

	public DiagPopView(Context context, View contentView) {
		this.context = context;

		/**
		 * inflater = (LayoutInflater) context
		 * .getSystemService(Context.LAYOUT_INFLATER_SERVICE); view =
		 * inflater.inflate(R.layout.lock_release, null, true);
		 */
		/**
		 * view.findViewById(R.id.app_no).setOnClickListener(this);
		 * view.findViewById(R.id.app_yes).setOnClickListener(this);
		 */
		this.view = contentView;
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
		popupWindow.setOutsideTouchable(true);

	}

	public void setLeftMsg(String left) {
		if (view != null) {
			GeneralUtil.setValueToView(view.findViewById(R.id.app_yes), left);
		}

	}

	public DiagPopView(Context context, String msg) {
		this.context = context;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.lock_release, null, true);
		if (!TextUtils.isEmpty(msg)) {
			GeneralUtil.setValueToView(view.findViewById(R.id.show_txt), msg);

		}
		view.findViewById(R.id.app_no).setOnClickListener(this);
		view.findViewById(R.id.app_yes).setOnClickListener(this);
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
		popupWindow.setOutsideTouchable(true);

	}

	protected View getOverView() {
		return view == null ? new View(context) : view;

	}

	public void setOnHeaderItemClick(Diag_ok_no listener) {
		this.listener = listener;
	}

	public void showAsDropDown(View parent) {
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAsDropDown(parent, 0, 0);

		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.update();
	}

	public void showPush(View parent) {
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// popupWindow.showAsDropDown(parent, 10, 400);
		popupWindow.showAtLocation(parent, Gravity.BOTTOM, 10, 700);

		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.update();
	}

	public void dismiss() {
		popupWindow.dismiss();
	}

	@Override
	public void onClick(View v) {
		this.listener.btn_ok_no(v);
	}
}
