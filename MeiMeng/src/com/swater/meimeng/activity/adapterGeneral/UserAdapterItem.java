package com.swater.meimeng.activity.adapterGeneral;

import java.io.Serializable;

public class UserAdapterItem  implements Serializable{
	String leftStr = "";
	String rightStr = "";
	String id="";
	int pos=-1;
	

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLeftStr() {
		return leftStr;
	}

	public void setLeftStr(String leftStr) {
		this.leftStr = leftStr;
	}

	public String getRightStr() {
		return rightStr;
	}

	public void setRightStr(String rightStr) {
		this.rightStr = rightStr;
	}

}
