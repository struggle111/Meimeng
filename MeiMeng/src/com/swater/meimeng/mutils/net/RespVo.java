package com.swater.meimeng.mutils.net;

import java.io.IOException;
import java.io.Serializable;

import org.apache.http.client.ClientProtocolException;

/**
 * 返回实体类 sendUrl－－代表发送的url ishasError--true,代表有错误信息返回， resp代表服务器返回的数据
 * error_code代表服务器返回的错误吗 error_ErrorDetail代表服务器返回的错误详情
 * 
 * @return
 * 
 * **/
public class RespVo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1347661808287142281L;
	String sendUrl = "";
	String ErrorDetail = "";
	String resp = "";
	String ErrorCode = "";
	boolean hasError = false;

	public boolean isHasError() {
		return hasError;
	}

	public boolean isInternetFail() {
		return this.getErrorCode().equals("110") ? true : false;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	public String getSendUrl() {
		return sendUrl;
	}

	public void setSendUrl(String sendUrl) {
		this.sendUrl = sendUrl;
	}

	public String getErrorDetail() {
		return ErrorDetail;
	}

	public void setErrorDetail(String errorDetail) {
		ErrorDetail = errorDetail;
	}

	public String getResp() {
		return resp;
	}

	public void setResp(String resp) {
		this.resp = resp;
	}

	public String getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(String errorCode) {
		ErrorCode = errorCode;
	}

}
