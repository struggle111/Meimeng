package com.swater.meimeng.activity.adapterGeneral.vo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.swater.meimeng.database.XmlDataOptions.CellVo;

public class AnSwerVo  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4888582364661390435L;
	String type = "";
	String answer = "";
	String id_op = "";
	String value = "";
	int type_qe_id=-1;
	LinkedList<SubVo> subs=null;
	
	

	public LinkedList<SubVo> getSubs() {
		return subs;
	}

	public void setSubs(LinkedList<SubVo> subs) {
		this.subs = subs;
	}

	public int getType_qe_id() {
		return type_qe_id;
	}

	public void setType_qe_id(int type_qe_id) {
		this.type_qe_id = type_qe_id;
	}

	List<CellVo> subList = null;

	public List<CellVo> getSubList() {
		return subList;
	}

	public void setSubList(List<CellVo> subList) {
		this.subList = subList;
	}

	public AnSwerVo() {
	}

	public AnSwerVo(String type, String answer, String id_op, String value) {
		super();
		this.type = type;
		this.answer = answer;
		this.id_op = id_op;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getId_op() {
		return id_op;
	}

	public void setId_op(String id_op) {
		this.id_op = id_op;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
