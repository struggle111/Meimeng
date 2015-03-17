package com.swater.meimeng.activity.adapterGeneral.vo;

public class MsgVo {
	int sex=0;
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	String user_title="";
	/**-opentome: 相册头像对我开放(上锁后生效,如果没对我开放,无法查看相册和头像),int;1-不开放;2-开放;*/
	int open_to_me=0;
	
	public int getOpen_to_me() {
		return open_to_me;
	}
	public void setOpen_to_me(int open_to_me) {
		this.open_to_me = open_to_me;
	}
	String pic_url="";
	int isagreed=1;
	int msg_type_Sys=0;
	int msg_type_User=0;
	int msg_type_App=0;
	int mid=0;
	String targrt_name="";
	String time="";
	/**1未读，2以读取*/
	int status=0;
	
	/**type:消息类型,int;1-资料审核未通过(跳转个人设置);
	 * 2-文字推送(无操作);
	 * 3-推荐对象(跳转到用户详情);
	 * 4-派对通知(跳转到派对详情);
	 * 5-活动创建成功(跳转到私人活动详情)*/
	int type;
	String content="";
	int target_id=0;
	String uid="";
	
	
	public int getMsg_type_Sys() {
		return msg_type_Sys;
	}
	public void setMsg_type_Sys(int msg_type_Sys) {
		this.msg_type_Sys = msg_type_Sys;
	}
	public int getMsg_type_User() {
		return msg_type_User;
	}
	public void setMsg_type_User(int msg_type_User) {
		this.msg_type_User = msg_type_User;
	}
	public int getMsg_type_App() {
		return msg_type_App;
	}
	public void setMsg_type_App(int msg_type_App) {
		this.msg_type_App = msg_type_App;
	}
	public int getIsagreed() {
		return isagreed;
	}
	public void setIsagreed(int isagreed) {
		this.isagreed = isagreed;
	}
	public String getPic_url() {
		return pic_url;
	}
	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}
	public String getUser_title() {
		return user_title;
	}
	public void setUser_title(String user_title) {
		this.user_title = user_title;
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public int getMid() {
		return mid;
	}
	public void setMid(int mid) {
		this.mid = mid;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getTarget_id() {
		return target_id;
	}
	public void setTarget_id(int target_id) {
		this.target_id = target_id;
	}
	public String getTargrt_name() {
		return targrt_name;
	}
	public void setTargrt_name(String targrt_name) {
		this.targrt_name = targrt_name;
	}
	
	

}
