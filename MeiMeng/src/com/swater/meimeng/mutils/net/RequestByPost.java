package com.swater.meimeng.mutils.net;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.swater.meimeng.mutils.NSlog.NSLoger;

/**
 * @author chengshiyang E-mail:topdream51095@gmail.com
 * @version time：2012-9-12 上午1:54:02
 * 
 */

public class RequestByPost implements MUrlPostAddr {
	public static final String BOUNDARY = "7cd4a6d158c";
	public static final String MP_BOUNDARY = "--" + BOUNDARY;
	public static final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";
	public static final String MULTIPART_FORM_DATA = "multipart/form-data";

	public static BasicNameValuePair componentFields(String... strings) {
		BasicNameValuePair ap = null;
		StringBuilder sb = new StringBuilder();
		for (String str : strings) {
			sb.append(str + ",");
		}
		// ap = new BasicNameValuePair(MConstantUser.UserReg.USER_PASSWORD,
		// sb.toString());
		return ap;
	}

	public static BasicNameValuePair componentFields1(String... strings) {
		BasicNameValuePair ap = null;
		StringBuilder sb = new StringBuilder();
		for (String str : strings) {
			sb.append(str + ",");
		}
		ap = new BasicNameValuePair("fields1", sb.toString());
		return ap;
	}

	public static BasicNameValuePair componentExtraFields2(String... strings) {
		BasicNameValuePair ap = null;
		StringBuilder sb = new StringBuilder();
		for (String str : strings) {
			sb.append(str + ",");
		}
		// ap = new BasicNameValuePair(MConstantUser.UserReg.USER_GENDER,
		// sb.toString());
		return ap;
	}

	private static List<NameValuePair> conentParam(Map<String, String> keysParam) {
		if (keysParam == null) {
			return null;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (Entry<String, String> keys : keysParam.entrySet()) {
			keys.getKey();
			params.add(new BasicNameValuePair(keys.getKey(), keysParam.get(keys
					.getKey())));
		}
		return params;
	}

	public static byte[] getBytes(InputStream is) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// byte[] b = new byte[2048];
		byte[] b = new byte[1024];
		int len = 0;
		byte[] bytes = null;
		try {
			while ((len = is.read(b, 0, 1024)) != -1) {
				baos.write(b, 0, len);
				baos.flush();
			}
			bytes = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			return null;

		} finally {

			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return bytes;
	}

	/**
	 * @category 请求后台
	 * @param 请求地址
	 * @param 参数
	 * @return
	 */
	public static String CkeckNewVersion(String requrl) {

		try {
			StringBuilder builder = new StringBuilder();

			InputStream is = new URL(requrl).openStream();
			InputStreamReader isr = null;
			BufferedReader br = null;
			isr = new InputStreamReader(is, "UTF-8");
			br = new BufferedReader(isr);
			String line = "";
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}

			String res = builder.toString();
			NSLoger.Log("=====res---" + res);
			if (br != null) {

				br.close();
			}
			if (is != null) {
				is.close();
			}
			return res;

		} catch (Exception e) {
			return null;
		}
	};

	public static RespVo sendPostPhp(String requrl,
			Map<String, String> mapParams) {
		HttpPost httpRequest = new HttpPost(requrl);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		
		params.addAll(conentParam(mapParams));
		RespVo vo = null;
		HttpResponse httpResponse = null;

		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			StringBuilder sb = new StringBuilder();
			sb.append(requrl + "");
			for (NameValuePair nameValuePair : params) {
				sb.append("/");
				sb.append(nameValuePair.toString().replace("=", "/"));
			}
			// api/default/addrecordlog
			// api/default/addrecordlog
			Log.d("-- 发送url-->>toStirng------methodg--->>",
					"sb---" + sb.toString());
			httpResponse = new DefaultHttpClient().execute(httpRequest);
			int code = httpResponse.getStatusLine().getStatusCode();
			NSLoger.Log("--code-->" + code);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				Log.d("--服务器返回数据！---" + strResult, "---->>");
				if (!TextUtils.isEmpty(strResult)) {

					vo = analyseRes(strResult, sb.toString());

				} else {
					vo = new RespVo();
					vo.setErrorCode("999");
					vo.setErrorDetail("服务器连接失败！请稍后重试！！");
					vo.setHasError(true);
					vo.setSendUrl(sb.toString());
				}
			} else {

				Log.e("-----服务器响应出错--resp-->>",
						EntityUtils.toString(httpResponse.getEntity()));
			}

		} catch (Exception e) {
			e.printStackTrace();
			vo = ParselException(e);
		} finally {
		}
		return vo;

	}
	public static RespVo sendAsyncPost(String requrl,
			Map<String, String> mapParams) {
		final RespVo rv=new RespVo();
		final String req_urls=requrl;
		AsyncHttpClient client=new AsyncHttpClient();
		RequestParams rp=new RequestParams(mapParams);
		client.post(requrl, rp, new AsyncHttpResponseHandler(){
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// 
				super.onFailure(arg0, arg1);
			}
			@Override
			public void onFailure(Throwable arg0) {
				// 
				super.onFailure(arg0);
				
			}
			@Override
			public void onSuccess(String res) {
				super.onSuccess(res);
				rv.setResp(res);
				//rv.setSendUrl(sendUrl)
				//rv=analyseRes(res, req_urls);
			}
			
		});
return null;
		//return analyseRes(res, req_urls);

	}
	static RespVo ParselException(Exception e) {
		RespVo vo = new RespVo();
		if (e instanceof ConnectException) {
			vo.setHasError(true);
			vo.setErrorCode("110");
			vo.setErrorDetail("网络连接错误！未能连接到服务器！");

		}
		return vo;
	}

	public byte[] getData() {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(
				arrayOutputStream);
		try {
			dataOutputStream.writeShort(8);
			dataOutputStream.writeUTF("wangjun");
			dataOutputStream.flush();
			dataOutputStream.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return arrayOutputStream.toByteArray();
	}

	private static RespVo analyseRes(String res, String sendUrl) {
		RespVo rvo = new RespVo();
		rvo.setSendUrl(sendUrl);
		Log.e("接口：",sendUrl);
		Log.d("from--server----res" + res, "---");
		String errorDetail = "";
		if (!TextUtils.isEmpty(res) && res.length() > 2) {
			if (res.contains("result")) {
				try {
					// String errorcode = new JSONObject(res).getJSONObject(
					// "error_response").getString("code");
					int code_res = new JSONObject(res).getInt("result");

					Log.e("活动中返回的数据：",res);
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

	/**
	 * String转MD5
	 * 
	 * @param s
	 * @return
	 */
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;

	}

	/**
	 * 
	 * @category 上传图片文件
	 */
	public static RespVo uploadUserFile(String uploadUrl, String fullpath,
			String userid, String key, String pid) {
		String keys = "b91e85501ab94732";
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******";
		RespVo vo = null;
		try {

			URL url = new URL(uploadUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
			// 允许输入输出流
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			// 使用POST方法
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			FileInputStream fis = new FileInputStream(fullpath);
			InputStream ins = new BufferedInputStream(fis);
			// fis.close();
			int con_lgth = readStream(ins).length;
			httpURLConnection.setRequestProperty("Content-Length", con_lgth
					+ "");

			// 411 Length Required 服务器不能处理请求，除非客户发送一个Content-Length头。（HTTP 1.1新）

			DataOutputStream dos = new DataOutputStream(
					httpURLConnection.getOutputStream());

			// --------------
			String header_split = twoHyphens + boundary + end;
			dos.writeBytes(header_split);
			dos.writeBytes("Content-Disposition: form-data; name=\"uid\"" + end);
			dos.writeBytes(end + URLEncoder.encode(userid, "UTF-8") + end);
			// // --------------
			dos.writeBytes(header_split);
			dos.writeBytes("Content-Disposition: form-data; name=\"key\"" + end);
			dos.writeBytes(end + URLEncoder.encode(keys, "UTF-8") + end);
			// --------------------
			// // --------------
			if (!TextUtils.isEmpty(pid)) {
				dos.writeBytes(header_split);
				dos.writeBytes("Content-Disposition: form-data; name=\"pid\""
						+ end);
				dos.writeBytes(end + URLEncoder.encode(pid, "UTF-8") + end);
				// --------------------
			}

			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data;  name=\"data\"; filename=\""
					+ fullpath.substring(fullpath.lastIndexOf("/") + 1)
					+ "\""
					+ end);
			dos.writeBytes(end);

			byte[] buffer = new byte[8192]; // 8k
			int count = 0;
			// 读取文件
			FileInputStream file_in = new FileInputStream(fullpath);
			while ((count = file_in.read(buffer)) != -1) {
				dos.write(buffer, 0, count);
			}

			file_in.close();

			dos.writeBytes(end);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			dos.flush();
			int code = httpURLConnection.getResponseCode();
			NSLoger.Log("--code--->" + code);

			InputStream is = httpURLConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String result = br.readLine();
			Log.d("resp---from---secerl====_" + result, "--");

			dos.close();
			is.close();
			vo = analyseRes(result, uploadUrl);

			// return vo;

		} catch (Exception e) {
			if (vo == null) {
				vo = new RespVo();

			}
			vo.setErrorDetail(e.getMessage());
			vo.setHasError(true);
			vo.setResp("");
			return vo;
			// e.printStackTrace();
		}
		return vo;
	}

	/**
	 * 
	 * @category 上传用户--头像
	 */
	public static RespVo uploadUserHeader(String uploadUrl, String fullpath,
			String userid, String k) {
		String keys = "b91e85501ab94732";
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******";
		RespVo vo = null;
		try {

			URL url = new URL(uploadUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
			// 允许输入输出流
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			// 使用POST方法
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(
					httpURLConnection.getOutputStream());

			// --------------
			String header_split = twoHyphens + boundary + end;
			// -------11
			dos.writeBytes(header_split);
			dos.writeBytes("Content-Disposition: form-data; name=\"key\"" + end);
			dos.writeBytes(end + URLEncoder.encode(keys, "UTF-8") + end);

			// ------222
			dos.writeBytes(header_split);
			dos.writeBytes("Content-Disposition: form-data; name=\"uid\"" + end);
			dos.writeBytes(end + URLEncoder.encode(userid, "UTF-8") + end);

			// ------333
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data;  name=\"data\"; filename=\""
					+ fullpath.substring(fullpath.lastIndexOf("/") + 1)
					+ "\""
					+ end);
			dos.writeBytes(end);

			FileInputStream fis = new FileInputStream(fullpath);
			byte[] buffer = new byte[8192]; // 8k
			int count = 0;
			// 读取文件
			while ((count = fis.read(buffer)) != -1) {
				dos.write(buffer, 0, count);
			}
			fis.close();

			dos.writeBytes(end);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			dos.flush();

			InputStream is = httpURLConnection.getInputStream();
			if (is == null) {
				Log.d("--InputStream--from --server--" + is, "");
				return null;

			}
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String result = br.readLine();
			Log.d("resp---from---secerl====_" + result, "--");

			dos.close();
			is.close();
			vo = analyseRes(result, uploadUrl);
			// return vo;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	/**
	 * 
	 * @category 上传用户--语音 URL：/api/upload/voice 表单数据，封装为form表单提交：
	 * @param 参数名
	 *            必填 类型 描述 key True 密钥, 参见规范中的key值； uid True 用户ID； data true
	 *            语音内容,长度不超过180s;格式暂定aac，开发中遇到问题，及时提出修改。 示例： --*******
	 *            Content-Disposition: form-data; name=" key " b91e85501ab94732
	 *            --******* Content-Disposition: form-data; name="uid" 123456
	 *            --******* Content-Disposition: form-data; name="data" ;
	 *            filename="1435235.aac" xxxxxxxxxxxxx
	 */
	public static RespVo uploadVoice(String uploadUrl, String fullpath,
			String userid, String k) {
		String keys = "b91e85501ab94732";
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******";
		RespVo vo = null;
		try {

			URL url = new URL(uploadUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
			// 允许输入输出流
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			// 使用POST方法
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(
					httpURLConnection.getOutputStream());

			// --------------
			String header_split = twoHyphens + boundary + end;
			// -------11
			dos.writeBytes(header_split);
			dos.writeBytes("Content-Disposition: form-data; name=\"key\"" + end);
			dos.writeBytes(end + URLEncoder.encode(keys, "UTF-8") + end);

			// ------222
			dos.writeBytes(header_split);
			dos.writeBytes("Content-Disposition: form-data; name=\"uid\"" + end);
			dos.writeBytes(end + URLEncoder.encode(userid, "UTF-8") + end);

			// ------333
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data;  name=\"data\"; filename=\""
					+ fullpath.substring(fullpath.lastIndexOf("/") + 1)
					+ "\""
					+ end);
			dos.writeBytes(end);

			FileInputStream fis = new FileInputStream(fullpath);
			byte[] buffer = new byte[8192]; // 8k
			int count = 0;
			// 读取文件
			while ((count = fis.read(buffer)) != -1) {
				dos.write(buffer, 0, count);
			}
			fis.close();

			dos.writeBytes(end);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			dos.flush();

			InputStream is = httpURLConnection.getInputStream();
			if (is == null) {
				Log.d("--InputStream--from --server--" + is, "");
				return null;

			}
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String result = br.readLine();
			Log.d("resp---from---secerl====_" + result, "--");

			dos.close();
			is.close();
			vo = analyseRes(result, uploadUrl);
			// return vo;

		} catch (Exception e) {
			vo.setErrorDetail(e.getMessage());
			vo.setHasError(true);
			vo.setResp(null);
			e.printStackTrace();
		}
		return vo;
	}

	@Deprecated
	private String componetHeaderUpload(Map<String, String> headerParams) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******";
		StringBuilder sb = new StringBuilder("");

		for (Entry<String, String> keys : headerParams.entrySet()) {
			sb.append(twoHyphens + boundary + end);
			sb.append("Content-Disposition: form-data; name=\"" + keys.getKey()
					+ "\"" + end + keys.getValue() + end);

		}

		return sb.toString();
	}

	private static HttpURLConnection con = null;
	private static URL url = null;
	private DataOutputStream ds = null;
	private InputStream is = null;

	// private static final String actionUrl = "http://api.XXXXXXX.com/";

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
	public String getInputStream(List<NameValuePair> list) {

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
			list.add(new BasicNameValuePair("api_key",
					"8b11c911-25c4-4d05-9cf2-120c72487544"));

			for (int i = 0; i < list.size(); i++) {

				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; name=\""
						+ list.get(i).getName() + "\"");
				ds.writeBytes("\r\n\r\n");

				ds.write(list.get(i).getValue().getBytes("UTF-8"));
				ds.writeBytes(end);
			}

			ds.writeBytes(twoHyphens + boundary + end);
			ds.flush();

			/* 取得Response内容 */
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
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}

		return builder.toString();
	}

}
