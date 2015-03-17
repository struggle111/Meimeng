
package com.swater.meimeng.mutils.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * @author 作者chengshiyang E-mail: topdream51095@gmail.com
 * @version 创建时间：2012-9-17 下午10:04:31
 * 
 */
public class FilterNet {
	public static boolean hasNet(Context con) {
		boolean flag = false;
		ConnectivityManager cm = (ConnectivityManager) con
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (netinfo == null || !netinfo.isAvailable()) {
			if (Thread.currentThread().getId() == 0) {
				// Toast.makeText(con, "网络连接中断！请先设置好网络！", Toast.LENGTH_SHORT)
				// .show();
			}
			flag = false;
		}
		if (netinfo != null) {
			if (netinfo.isConnected()) {
				flag = true;
			}else flag=false;
		}

		return flag;

	}
}
