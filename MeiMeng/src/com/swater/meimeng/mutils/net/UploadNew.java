package com.swater.meimeng.mutils.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.swater.meimeng.database.ShareUtil;
import com.swater.meimeng.mutils.NSlog.NSLoger;

public class UploadNew implements MUrlPostAddr {
	private static UploadNew instance = new UploadNew();
	static SharedPreferences share = null;

	private UploadNew() {

	}

	public static UploadNew getInstance(Context con) {
		return instance;
	}

	private static HttpURLConnection con = null;
	private static URL url = null;
	private DataOutputStream ds = null;
	private InputStream is = null;


	public void setConnection(String actionUrl) {

		try {

			if (con == null) {
				url = new URL(actionUrl);
				con = (HttpURLConnection) url.openConnection();

				con.setDoInput(true);
				con.setDoOutput(true);
				con.setUseCaches(false);

				con.setRequestMethod("POST");
				con.setRequestProperty("Connection", "Keep-Alive");
				con.setRequestProperty("Charset", "UTF-8");
				con.setRequestProperty("Content-Type",
						"multipart/form-data; boundary=*****");
			}
			con.connect();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @SuppressWarnings("unused")
	public RespVo getInputStream(List<NameValuePair> list,String fullpath) {

		StringBuilder builder = new StringBuilder();
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			if (ds == null) {
				OutputStream outStream = con.getOutputStream();
				ds = new DataOutputStream(outStream);
			}
			list.add(new BasicNameValuePair("key", key_server));
			
//			
//
//			file_in.close();

			for (int i = 0; i < list.size(); i++) {

				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; name=\""
						+ list.get(i).getName() + "\"");
				ds.writeBytes("\r\n\r\n");

				ds.write(list.get(i).getValue().getBytes("UTF-8"));
				ds.writeBytes(end);
			}

			ds.writeBytes(twoHyphens + boundary + end);
			
			
//			
			ds.writeBytes("Content-Disposition: form-data;  name=\"data\"; filename=\""
					+ fullpath.substring(fullpath.lastIndexOf("/") + 1)
					+ "\""
					+ end);
			ds.writeBytes(end);

			byte[] buffer = new byte[8192]; // 8k
			int count = 0;
			// 读取文件
			FileInputStream file_in = new FileInputStream(fullpath);
			while ((count = file_in.read(buffer)) != -1) {
				ds.write(buffer, 0, count);
			}
			file_in.close();

			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			ds.flush();
			int code_resp = con.getResponseCode();
			NSLoger.Log("--code--" + code_resp);
			/* 取得Resp+coonse内容 */
			is = con.getInputStream();
			isr = new InputStreamReader(is, "UTF-8");
			br = new BufferedReader(isr);
			String line = "";

			while ((line = br.readLine()) != null) {
				builder.append(line);
			}

		} catch (Exception e) {
			Log.e("error->", "p:" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				br.close();
				isr.close();
				con.getInputStream().close();
				is.close();
				ds.close();
				con.disconnect();
				con=null; 
				ds=null;
			} catch (Exception e2) {
				
				// TODO: handle exception
				e2.printStackTrace();
			}
		}
		

		return analyseRes(builder.toString(), "--");// builder.toString();
	}
	private static RespVo analyseRes(String res, String sendUrl) {
		RespVo rvo = new RespVo();
		rvo.setSendUrl(sendUrl);
		Log.d("from--server----res" + res, "---");
		String errorDetail = "";
		if (!TextUtils.isEmpty(res) && res.length() > 2) {
			if (res.contains("result")) {
				try {
					// String errorcode = new JSONObject(res).getJSONObject(
					// "error_response").getString("code");
					int code_res = new JSONObject(res).getInt("result");

					if (code_res != 1) {

						rvo.setHasError(true);
						errorDetail = new JSONObject(res).getString("error");
						rvo.setErrorDetail(errorDetail);
						rvo.setErrorCode(code_res + "");
						rvo.setResp(res);
					} else {
						rvo.setHasError(false);
						rvo.setErrorCode(code_res + "");
						rvo.setResp(res);

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				rvo.setHasError(false);
				rvo.setResp(res);

			}

		} else {

			rvo.setHasError(true);
			rvo.setResp(res);
			rvo.setErrorDetail("返回后台数据为空！");
			rvo.setErrorCode("9999");
		}
		return rvo;

	}

}
