package com.swater.meimeng.activity.adapterGeneral.vo;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class UserSearchVo implements Serializable {
	String age = "";

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	String uid = "";
	String photoUrl = "";
	String locked = "";
	String content = "";
	String heart_desc = "";
	int vip_level = -1;
	int audi_type = 0;
	public String getHeart_desc() {
		return heart_desc;
	}

	public void setHeart_desc(String heart_desc) {
		this.heart_desc = heart_desc;
	}

	String medals = "";

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public int getVip_level() {
		return vip_level;
	}

	public void setVip_level(int vip_level) {
		this.vip_level = vip_level;
	}

	public int getAudi_type() {
		return audi_type;
	}

	public void setAudi_type(int audi_type) {
		this.audi_type = audi_type;
	}

	public String getMedals() {
		return medals;
	}

	public void setMedals(String medals) {
		this.medals = medals;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLocked() {
		return locked;
	}

	public void setLocked(String locked) {
		this.locked = locked;
	}

	String sex = "";
	String head_url = "";
	String nickName = "";
	String height = "";
	String province = "";
	String city = "";
	String opentome = "";

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getHead_url() {
		return head_url;
	}

	public void setHead_url(String head_url) {
		this.head_url = head_url;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getOpentome() {
		return opentome;
	}

	public void setOpentome(String opentome) {
		this.opentome = opentome;
	}

}
