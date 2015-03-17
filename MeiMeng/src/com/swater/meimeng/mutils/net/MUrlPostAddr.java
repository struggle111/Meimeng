package com.swater.meimeng.mutils.net;

/**
 * @category 后台请求接口
 */

public interface MUrlPostAddr {
	static String url_version = "http://112.124.18.97/app/update.json";
	static String key_server = "b91e85501ab94732";
	// http://192.168.20.1:9090
	// static String
	// server_ip="http://192.168.31.203:112";//"http://192.168.20.1:9090";//"http://192.168.31.4:112";
	// static String server_ip="http://192.168.32.27/meimeng";
	// 192.168.1.150:112
	// static String
	// server_ip="http://192.168.1.116:112";;///"http://112.124.18.97";
	static String server_ip = "http://112.124.18.97";

	// static String server_ip="http://192.168.1.113:112";

	// static String server_ip="http://192.168.1.107:112";

	// "http://192.168.20.1:9090";
	// /api/default/getrecordloglist/key/b91e85501ab94732
	public static String MURL_user_reg = server_ip + "/api/user/register";;
	public static String MURL_user_login = server_ip + "/api/user/login";;
	public static String MURL_user_uploadImg = server_ip + "/api/upload/pic";;
	public static String MURL_user_header_upload = server_ip
			+ "/api/upload/header";
	public static String MURL_user_gallery_list = server_ip
			+ "/api/personal/albums";

	// public static String MURL_user_gallery_list =
	// "http://192.168.32.27/meimeng/api/personal/albums";;
	public static String MURL_user_gallery_delete_pic = server_ip
			+ "/api/personal/delpic";;
	public static String MURL_user_heart_description = server_ip
			+ "/api/personal/heartdescription";;
	public static String MURL_user_basicinfo = server_ip
			+ "/api/personal/basicinfo";;
	public static String MURL_user_detailInfo = server_ip
			+ "/api/personal/detailinfo";;
	public static String MURL_user_apperanceinfo = server_ip
			+ "/api/personal/appearanceinfo";;
	public static String MURL_user_workinfo = server_ip
			+ "/api/personal/workinfo";;
	public static String MURL_user_LifeInfo = server_ip
			+ "/api/personal/lifeinfo";;
	public static String MURL_user_Hobby = server_ip
			+ "/api/personal/hobbyinfo";;
	public static String MURL_user_Personal_show = server_ip
			+ "/api/personal/personalityinfo";;
	public static String MURL_user_all_info = server_ip + "/api/personal/all";;

	// -----search--city--
	// /api/search/nickname
	public static String MURL_search_nickName = server_ip
			+ "/api/search/nickname";;
	public static String MURL_search_id = server_ip + "/api/search/id";;
	public static String MURL_search_city = server_ip + "/api/search/city";;

	public static String MURL_search_vip_list = server_ip + "/api/search/list";;
	public static String MURL_Recommend = server_ip + "/api/search/recommended";;
	public static String MURL_MyFocus_List = server_ip + "/api/personal/mylist";;
	public static String MURL_ADD_FOCUS = server_ip + "/api/operate/follow";;
	public static String MURL_ADD_WANT_2_SEE = server_ip
			+ "/api/operate/wantsee";;
	public static String MURL_ADD_LOOK_APPLY = server_ip
			+ "/api/operate/applylook";;
	// ------------
	public static String MURL_PARTY = server_ip + "/api/activity/party";;
	public static String MURL_Private_invite = server_ip
			+ "/api/activity/invite";;
	public static String MURL_Party_Detail = server_ip
			+ "/api/activity/partydetail";;
	public static String MURL_Invite_Detail = server_ip
			+ "/api/activity/invitedetail";;
	// ----------------------
	public static String MURL_APPLY_ACT = server_ip + "/api/activity/apply";;
	public static String MURL_APPLY_LIST_USERS = server_ip
			+ "/api/activity/users";;
	// ----
	public static String MURL_PERMISSION_GALLERY_SET = server_ip
			+ "/api/personal/albumpermission";;
	public static String MURL_PERMISSION_SHIELDLIST = server_ip
			+ "/api/personal/shieldlist";;
	public static String MURL_PERMISSION_ADD_SHIELD = server_ip
			+ "/api/personal/addshield";;
	public static String MURL_PERMISSION_DEL_SHIELD = server_ip
			+ "/api/personal/delshield";;
	// --------
	public static String MURL_MSG_SYS = server_ip + "/api/message/sysmsg";;
	public static String MURL_MSG_USER = server_ip + "/api/message/usermsg";;
	public static String MURL_MSG_APPLY = server_ip + "/api/message/applymsg";;
	public static String MURL_MSG_AGREE_APPLY = server_ip
			+ "/api/message/agree";;
	public static String MURL_LOG = server_ip + "/api/default/addrecordlog";;
	public static String MURL_VOICE_UPLOAD = server_ip + "/api/upload/voice";;
	public static String MURL_VOICE_DEL = server_ip + "/api/personal/delvoice";;
	public static String MURL_GREET = server_ip + "/api/operate/greetings";;
	public static String MURL_PWD = server_ip + "/api/personal/password";;

}
