package com.swater.meimeng.activity.adapterGeneral.vo;


import java.io.Serializable;

/**
 * @category 多选数据队列
 */
public class DataVo implements Serializable {
	String type_date = "";
	String values = "";
	String ids = "";
	int pos=0;
	

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public String getType_date() {
		return type_date;
	}

	public void setType_date(String type_date) {
		this.type_date = type_date;
	}

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

}
