package com.swater.meimeng.mutils.NSlog;

public final class NSLoger {

	private static Boolean isopen = true;

	public static void Log(String s) {
		LogPrint(s);
	}

	private static void LogPrint(String s) {
		if (isopen) {
			android.util.Log.d("---->", s);
		}

	}

}
