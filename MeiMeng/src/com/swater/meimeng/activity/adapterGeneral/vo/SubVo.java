package com.swater.meimeng.activity.adapterGeneral.vo;

import java.io.Serializable;

public class SubVo  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1822019331466524256L;
	String type_id = "";
	String value = "";
	String op_id = "";
	String ques_id = "";
	String ques_value="";
	

	public String getQues_value() {
		return ques_value;
	}

	public void setQues_value(String ques_value) {
		this.ques_value = ques_value;
	}

	public String getOp_id() {
		return op_id;
	}

	public void setOp_id(String op_id) {
		this.op_id = op_id;
	}

	public String getQues_id() {
		return ques_id;
	}

	public void setQues_id(String ques_id) {
		this.ques_id = ques_id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getType_id() {
		return type_id;
	}

	public void setType_id(String type_id) {
		this.type_id = type_id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
