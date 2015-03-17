package com.swater.meimeng.mutils.diagutil;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meimeng.app.R;

public class Tools {
	static LinearLayout dialogView = null;
	static ViewGroup vg = null;

	public static Dialog createCustomDialog(Context context,
			List<DialogItem> items, int style) {
		LinearLayout dialogView = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.custom_dialog_layout, null);
		final Dialog customDialog = new Dialog(context, style);
		LinearLayout itemView;
		TextView textView;
		for (DialogItem item : items) {
			itemView = (LinearLayout) LayoutInflater.from(context).inflate(
					item.getViewId(), null);
			textView = (TextView) itemView.findViewById(R.id.popup_text);
			textView.setText(item.getTextId());
			textView.setOnClickListener(new OnItemClick(item, customDialog));
			dialogView.addView(itemView);
		}

		WindowManager.LayoutParams localLayoutParams = customDialog.getWindow()
				.getAttributes();
		localLayoutParams.x = 0;
		localLayoutParams.y = -1000;
		localLayoutParams.gravity = 80;
		dialogView.setMinimumWidth(10000);

		customDialog.onWindowAttributesChanged(localLayoutParams);
		customDialog.setCanceledOnTouchOutside(true);
		customDialog.setCancelable(true);
		customDialog.setContentView(dialogView);

		if (context instanceof Activity) {
			Activity activity = (Activity) context;
			if (!activity.isFinishing()) {
				customDialog.show();
			}
		}
		customDialog.setOnDismissListener(dismisslis);
		customDialog.setOnCancelListener(cancellis);

		return customDialog;
	}

	static OnCancelListener cancellis = new OnCancelListener() {

		@Override
		public void onCancel(DialogInterface dialog) {
			clearView();
		}
	};
	static OnDismissListener dismisslis = new OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {

			clearView();

		}
	};

	public static Dialog pull_Dialog(Context context, View view) {

		vg = (ViewGroup) view;
		dialogView = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.pull_lay, null);
		final Dialog customDialog = new Dialog(context, R.style.CustomDialogOld);

		view.setMinimumWidth(10000);

		dialogView.addView(view);

		// dialogView.removeView(view);
		WindowManager.LayoutParams localLayoutParams = customDialog.getWindow()
				.getAttributes();
		localLayoutParams.x = 0;
		localLayoutParams.y = -400;
		localLayoutParams.gravity = 68;// 80;
		dialogView.setMinimumWidth(10000);

		customDialog.onWindowAttributesChanged(localLayoutParams);
		customDialog.setCanceledOnTouchOutside(true);
		customDialog.setCancelable(true);
		customDialog.setContentView(dialogView);

		if (context instanceof Activity) {
			Activity activity = (Activity) context;
			if (!activity.isFinishing()) {
				customDialog.show();
			}
		}
		return customDialog;
	}
//	public static Dialog pull_Dialog(Context context, View view) {
//		
//		vg = (ViewGroup) view;
//		dialogView = (LinearLayout) LayoutInflater.from(context).inflate(
//				R.layout.pull_lay, null);
//		final Dialog customDialog = new Dialog(context, R.style.CustomDialogOld);
//		
//		view.setMinimumWidth(10000);
//		
//		dialogView.addView(view);
//		
//		// dialogView.removeView(view);
//		WindowManager.LayoutParams localLayoutParams = customDialog.getWindow()
//				.getAttributes();
//		localLayoutParams.x = 0;
//		localLayoutParams.y = -400;
//		localLayoutParams.gravity = 68;// 80;
//		dialogView.setMinimumWidth(10000);
//		
//		customDialog.onWindowAttributesChanged(localLayoutParams);
//		customDialog.setCanceledOnTouchOutside(true);
//		customDialog.setCancelable(true);
//		customDialog.setContentView(dialogView);
//		
//		if (context instanceof Activity) {
//			Activity activity = (Activity) context;
//			if (!activity.isFinishing()) {
//				customDialog.show();
//			}
//		}
//		return customDialog;
//	}
	public static Dialog pull_Dialog_New(Context context, View view) {
		
		vg = (ViewGroup) view;
		dialogView = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.pull_lay, null);
		final Dialog customDialog = new Dialog(context, R.style.CustomDialogOld);
		
		view.setMinimumWidth(10000);
		
		dialogView.addView(view);
		
		// dialogView.removeView(view);
		WindowManager.LayoutParams localLayoutParams = customDialog.getWindow()
				.getAttributes();
		localLayoutParams.x = 0;
		localLayoutParams.y = -400;
		localLayoutParams.gravity = 28;// 80;
		dialogView.setMinimumWidth(10000);
		
		customDialog.onWindowAttributesChanged(localLayoutParams);
		customDialog.setCanceledOnTouchOutside(true);
		customDialog.setCancelable(true);
		customDialog.setContentView(dialogView);
		
		if (context instanceof Activity) {
			Activity activity = (Activity) context;
			if (!activity.isFinishing()) {
				customDialog.show();
			}
		}
		return customDialog;
	}

	public static void clearView() {
		if (dialogView != null) {
			dialogView.removeAllViewsInLayout();
		}
		if (null != vg) {
			
			vg.removeAllViews();
		}
	}
}
