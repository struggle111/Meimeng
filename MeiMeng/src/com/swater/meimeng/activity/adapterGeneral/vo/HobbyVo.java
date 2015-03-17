package com.swater.meimeng.activity.adapterGeneral.vo;

import java.io.Serializable;
import java.util.List;

import com.swater.meimeng.activity.adapterGeneral.UserAdapterItem;

public class HobbyVo  implements Serializable{
	public HobbyVo() {
	}

	public HobbyVo(List<UserAdapterItem> lsdata, String leftName) {
		super();
		this.lsdata = lsdata;
		this.leftName = leftName;
	}

	List<UserAdapterItem> lsdata = null;
	String leftName = "";
	String data = "";
	String ids="";

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public List<UserAdapterItem> getLsdata() {
		return lsdata;
	}

	public void setLsdata(List<UserAdapterItem> lsdata) {
		this.lsdata = lsdata;
	}

	public String getLeftName() {
		return leftName;
	}

	public void setLeftName(String leftName) {
		this.leftName = leftName;
	}
}
