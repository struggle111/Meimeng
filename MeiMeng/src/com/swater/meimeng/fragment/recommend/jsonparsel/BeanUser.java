package com.swater.meimeng.fragment.recommend.jsonparsel;

public class BeanUser {
	int result;
	String error;

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	InnerUserInfo data;
	String realname;
	String vip_level;
	InnerBaseInfo base_info;

	static class InnerBaseInfo {
		String uid = "";
		String height = "";

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getHeight() {
			return height;
		}

		public void setHeight(String height) {
			this.height = height;
		}

	}

	static class InnerUserInfo {
		String sex = "";

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

	}

	static class InnerHeadInfo {
		String url = "";
		String audit_url = "";

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getAudit_url() {
			return audit_url;
		}

		public void setAudit_url(String audit_url) {
			this.audit_url = audit_url;
		}

	}

	static class InnerheartInfo {
		String content = "";
		String audit_content = "";

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getAudit_content() {
			return audit_content;
		}

		public void setAudit_content(String audit_content) {
			this.audit_content = audit_content;
		}

	}

	public InnerUserInfo getData() {
		return data;
	}

	public void setData(InnerUserInfo data) {
		this.data = data;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getVip_level() {
		return vip_level;
	}

	public void setVip_level(String vip_level) {
		this.vip_level = vip_level;
	}

	public InnerBaseInfo getBase_info() {
		return base_info;
	}

	public void setBase_info(InnerBaseInfo base_info) {
		this.base_info = base_info;
	}

}
