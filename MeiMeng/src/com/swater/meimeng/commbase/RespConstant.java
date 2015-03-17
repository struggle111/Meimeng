package com.swater.meimeng.commbase;

/**
 * @category 定义判断后台常量！ 用于线程取数据
 */
public interface RespConstant {
	public static int Resp_exception = 7;
	public static int Resp_NET_OK = 8;
	public static int Resp_NET_FAIL = 9;
	public static int Resp_DATA_OK = 10;
	public static int Resp_DATA_EXCEPTION = 11;
	public static int Resp_DATA_Empty = 12;
	public static int Resp_action_ok = 13;
	public static int Resp_action_fail = 14;
	public static int Resp_action_Empty = 15;
	public static int ACTION_TAG1 = 16;
	public static int ACTION_TAG2 = 17;
	public static int ACTION_TAG3 = 18;
	public static int ACTION_EMPTY = 19;
	public static int ACTION_FAIL = 20;
	public static int ACTION_ERROR = 21;
	public static int ACTION_EXCEPTON = 22;
	public static int focus_type_follow_me = 91;
	public static int focus_type_want2see_me = 92;
	public static int focus_type_iwant2see = 93;
	public static int focus_type_happ_each = 94;
	public static String PAGE = "page";

	public enum LoadState {
		STATE_NORMAL, STATE_READY, STATE_LOADING, STATE_OVER
		
	};
//	current_page":1,"total_page":2,"page_size":8,"total_count":"11"
	public final String PAGE_KEY_current_page="current_page";
	public final String PAGE_KEY_total_page="total_page";
	public final String PAGE_KEY_page_size="page_size";
	public final String PAGE_KEY_total_count="total_count";
	/**
	 * public enum Type_FOCUS { focus_type_follow_me, focus_type_want2see_me,
	 * focus_type_iwant2see, focus_type_happ_each };
	 */

}
