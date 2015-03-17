package com.swater.meimeng.database.sercurity;

public class SecurityLoad {

	public enum SecurityLoaderState {
		/**
		 * 准备就绪
		 */
		SECURITY_LOADER_READY,
		/**
		 * 数据错误
		 */
		SECURITY_LOADER_ERROR_SOURCE_DATA,
		/**
		 * 未知状态
		 */
		SECURITY_LOADER_UNKNOWN
	}

	private static final native String loadSecurityPwdkey();

	public static final native String loadServerIP();

	public String key_security = "b91e85501ab94732";
	static {
		System.loadLibrary("security");
	}

	public String loadSecurity() {

		return "";
	}
}
