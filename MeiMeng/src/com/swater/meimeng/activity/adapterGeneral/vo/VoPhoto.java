package com.swater.meimeng.activity.adapterGeneral.vo;

import java.io.Serializable;

public class VoPhoto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1159058650120062252L;
	String head_url = "";
	int pid = 0;
	String url = "";
	String thumb_url = "";
	int countTotal = 0;
	boolean isDownFinish = false;

	public boolean isDownFinish() {
		return isDownFinish;
	}

	public void setDownFinish(boolean isDownFinish) {
		this.isDownFinish = isDownFinish;
	}

	public String getHead_url() {
		return head_url;
	}

	public void setHead_url(String head_url) {
		this.head_url = head_url;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getThumb_url() {
		return thumb_url;
	}

	public void setThumb_url(String thumb_url) {
		this.thumb_url = thumb_url;
	}

	public int getCountTotal() {
		return countTotal;
	}

	public void setCountTotal(int countTotal) {
		this.countTotal = countTotal;
	}

	@Override
	public String toString() {
		return "VoPhoto [head_url=" + head_url + ", pid=" + pid + ", url="
				+ url + ", thumb_url=" + thumb_url + ", countTotal="
				+ countTotal + "]";
	}

}
