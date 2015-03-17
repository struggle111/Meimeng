package com.swater.meimeng.activity.adapterGeneral.vo;

import java.io.Serializable;

public class PartyVo implements Serializable {
	// activities:活动列表,JSON数组;
	// ----aid:活动ID,int;
	// ----subject:活动主题,string;
	// ----show_pic:列表图片地址,string;
	int aid;
	String subject = "";
	String show_pic = "";
	String desc = "";
	int status;
	String place="";

	//活动时间
	String time = "";

	long lTime;

	public long getlTime() {
		return lTime;
	}

	public void setlTime(long lTime) {
		this.lTime = lTime;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getAid() {
		return aid;
	}

	public void setAid(int aid) {
		this.aid = aid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getShow_pic() {
		return show_pic;
	}

	public void setShow_pic(String show_pic) {
		this.show_pic = show_pic;
	}

}
