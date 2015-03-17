package com.swater.meimeng.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @category 封装公用的共享数据
 */
public class ShareUtil implements ShareUserConstant {
	//final static String PAGE_MAIN="PAGE_MAIN";
	//final static String PAGE_SEARCH="PAGE_SEARCH";
	//final static String FIRST_OPEN="FIRST_OPEN";
	
	static String VersionName = "1.0.0";
	
	public static class UserVo {
		String header = "";
		String loginName = "";
		String loginPwd = "";
		int audit_type = -1;
		String medal = "";

		public String getMedal() {
			return medal;
		}

		public void setMedal(String medal) {
			this.medal = medal;
		}

		/** 认证类型，int；1-在线审核；2-面谈审核； */
		public int getAudit_type() {
			return audit_type;
		}

		public void setAudit_type(int audit_type) {
			this.audit_type = audit_type;
		}

		public String getLoginName() {
			return loginName;
		}

		public void setLoginName(String loginName) {
			this.loginName = loginName;
		}

		public String getLoginPwd() {
			return loginPwd;
		}

		public void setLoginPwd(String loginPwd) {
			this.loginPwd = loginPwd;
		}

		public String getHeader() {
			return header;
		}

		public void setHeader(String header) {
			this.header = header;
		}

		int uid = -1;
		String pwd = "";
		/** active：是否激活 1-未激活；2-激活； */
		int active = 1;
		/** vip_level 0-未付费;1…N - VIP1…VIPN; */
		int vip_level = -1;
		String nickname = "";
		int sex = 0;

		public int getSex() {
			return sex;
		}

		public void setSex(int sex) {
			this.sex = sex;
		}

		public int getUid() {
			return uid;
		}

		public void setUid(int uid) {
			this.uid = uid;
		}

		public String getPwd() {
			return pwd;
		}

		public void setPwd(String pwd) {
			this.pwd = pwd;
		}

		public int getActive() {
			return active;
		}

		/** active：是否激活，int；1-未激活；2-激活； */
		public void setActive(int active) {
			this.active = active;
		}

		/** vip等级，int；0-未付费;1-银牌；2-金牌;3-黑牌； */
		public int getVip_level() {
			return vip_level;
		}

		public void setVip_level(int vip_level) {
			this.vip_level = vip_level;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		@Override
		public String toString() {
			return "UserVo 用户所有 属性--[uid=" + uid + ", pwd=" + pwd + ", active="
					+ active + ", vip_level=" + vip_level + ", nickname="
					+ nickname + "]";
		}

	}

	private static ShareUtil instance = new ShareUtil();
	static SharedPreferences share = null;

	private ShareUtil() {

	}

	public static ShareUtil getInstance(Context con) {
		PackageManager pm = con.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(con.getPackageName(), 0);
			VersionName = pi.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		share = con.getSharedPreferences(USER_SH_INFO, Context.MODE_PRIVATE);
		return instance;
	}

	public static void Release() {

		if (share != null) {
			share = null;
		}
	}

	/**
	 * @category 保存用户信息
	 * @param UserVo
	 */
	public void saveUserInfo(UserVo user) {
		share.edit().putInt(UserGobal.USER_ID, user.getUid()).commit();
		share.edit().putInt(UserGobal.USER_IS_ACTIVE, user.getActive())
				.commit();
		share.edit().putInt(UserGobal.USER_VIP_LEVEL, user.getVip_level())
				.commit();
		share.edit().putString(UserGobal.USER_Nick_Name, user.getNickname())
				.commit();
		share.edit().putString(UserGobal.USER_HEADER, user.getHeader())
				.commit();
		share.edit().putInt("sex", user.getSex()).commit();

	}

	/**
	 * @category 保存登陆密码信息
	 * @param UserVo
	 */
	public void saveLoginInfo(String userName, String pwd) {
		share.edit().putString(UserGobal.USER_LOGIN_NAME, userName).commit();
		share.edit().putString(UserGobal.USER_LOGIN_PWD, pwd).commit();
	}
	
	public void SetisFistEnter_MainPage() {
		share.edit().putBoolean(VersionName+"PAGE_MAIN", false).commit();
	}
	
	public void SetisFistUser_SearPage() {
		share.edit().putBoolean(VersionName+"PAGE_SEARCH", false).commit();		
	}
	
	public void SetisFistOpen() {
		share.edit().putBoolean(VersionName+"FIRST_OPEN", false).commit();		
	}
	/**
	 * @category 判断是否是第一次使用
	 * @param 
	 */
	public Boolean getisFistEnter_MainPage() {
		return share.getBoolean(VersionName+"PAGE_MAIN", true);
		
		
	}
	/**
	 * @category 判断是否是第一次使用
	 * @param 
	 */
	public Boolean getisFistEnter_SearPage() {
	return 	share.getBoolean(VersionName+"PAGE_SEARCH", true);
		
		
	}
	public Boolean getisFirstOpen() {
		return 	share.getBoolean(VersionName+"FIRST_OPEN", true);
		
		
	}

	/**
	 * @param 清除登陆信息 
	 */
	public void clearLoginInfo() {
		// share.edit().putString(UserGobal.USER_LOGIN_NAME, "").commit();
		share.edit().putString(UserGobal.USER_LOGIN_PWD, "").commit();
		share.edit().putInt(UserGobal.USER_ID, -1).commit();
		share.edit().putInt(UserGobal.USER_IS_ACTIVE, -1).commit();
		share.edit().putInt(UserGobal.USER_VIP_LEVEL, -1).commit();
		share.edit().putString(UserGobal.USER_Nick_Name, "").commit();
		share.edit().putString(UserGobal.USER_HEADER, "").commit();
		share.edit().putInt("sex", -1).commit();
		Release();

	}

	/**
	 * @category 得到用户ID
	 * @return USERID
	 */
	public int getUserid() {

		return share.getInt(UserGobal.USER_ID, -1);

	}

	/**
	 * @category 得到用户信息
	 * @return uservo
	 */
	public UserVo getUserInfo() {
		UserVo uservo = new UserVo();
		uservo.setActive(share.getInt(UserGobal.USER_IS_ACTIVE, -1));
		uservo.setUid(share.getInt(UserGobal.USER_ID, -1));
		uservo.setVip_level(share.getInt(UserGobal.USER_VIP_LEVEL, -1));
		uservo.setNickname(share.getString(UserGobal.USER_Nick_Name, ""));
		uservo.setHeader(share.getString(UserGobal.USER_HEADER, ""));
		uservo.setLoginName(share.getString(UserGobal.USER_LOGIN_NAME, ""));
		uservo.setLoginPwd(share.getString(UserGobal.USER_LOGIN_PWD, ""));
		uservo.setSex(share.getInt(UserGobal.USER_SEX, 0));
		return uservo;

	}
	

}
