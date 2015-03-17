package com.swater.meimeng.mutils;

import android.content.Context;
import android.util.Log;

import com.meimeng.app.R;

public class MedalUtil {
	private static MedalUtil instance = new MedalUtil();

	private MedalUtil() {

	}

	public static MedalUtil getInstance(Context con) {
		return instance;
	}

	public static int getMedalIdByName(Context context, String key) {
		int medal_id = 0;
		try {
			medal_id = R.drawable.class.getDeclaredField(key).getInt(context);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			Log.e("----解析获取勋章异常----", "---");

		}
		return medal_id;
	}
}
