package com.swater.meimeng.mutils.net;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.meimeng.app.R;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.mutils.constant.GeneralUtil;
import com.swater.meimeng.mutils.net.CustomMultipartEntity.ProgressListener;


public class HttpMultipartPost extends AsyncTask<String, Integer, String> {
	public interface upload_call{
		void uoload_call(RespVo s);
	}

	public upload_call getCall() {
		return call;
	}
	public void setCall(upload_call call) {
		this.call = call;
	}
	private upload_call call;
	private Context context;
	private String filePath;
	private ProgressDialog pd;
	private long totalSize;

	private String url,uid;
	public HttpMultipartPost(Context context, String filePath,String url,String uid) {
		this.context = context;
		this.filePath = filePath;
		this.url=url;
		this.uid=uid;
	}
	public HttpMultipartPost(Context context, String filePath) {
		this.context = context;
		this.filePath = filePath;
	}
TextView txt_percent;
	@Override
	protected void onPreExecute() {
		
		
		
		View contentView = LayoutInflater.from(context).inflate(
				R.layout.progressdialog, null);
		txt_percent=(TextView)contentView.findViewById(R.id.txt_percent);
//		if (TextUtils.isEmpty(str)) {
//			str = "正在拉取数据...";
//		}
		GeneralUtil.setValueToView(contentView.findViewById(R.id.gress_text_view), "正在上传...");
		GeneralUtil.view_Show(txt_percent);
//		pd.setCancelable(false);
		pd = new ProgressDialog(context);
		pd.show();

		pd.setContentView(contentView);
		
//		pd = new ProgressDialog(context);
//		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		pd.setMessage("正在上传...");
//		pd.setCancelable(false);
//		pd.show();
	}
	//static String ur_server="http://112.124.18.97";
	static String key_server="b91e85501ab94732";

	@Override
	protected String doInBackground(String... params) {
		String serverResponse = null;

		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(this.url);

		try {
			CustomMultipartEntity multipartContent = new CustomMultipartEntity(
					new ProgressListener() {
						@Override
						public void transferred(long num) {
							GeneralUtil.setValueToView(txt_percent, (int) ((num / (float) totalSize) * 100)+"%");
//							publishProgress((int) ((num / (float) totalSize) * 100));
						}
					});

			// We use FileBody to transfer an image
			multipartContent.addPart("data", new FileBody(new File(
					filePath)));
			totalSize = multipartContent.getContentLength();
//			BasicNameValuePair bvp1 = new BasicNameValuePair("uid",
//					shareUserInfo().getUserid() + "");
//			BasicNameValuePair bvp2 = new BasicNameValuePair("data",
//					globalPath);
//			BasicNameValuePair bvp3 = new BasicNameValuePair(
//					"filename", globalPath.substring(globalPath
//							.lastIndexOf("/")));
//			multipartContent.addPart("",new ba);
			multipartContent.addPart("key", new StringBody(key_server));
			multipartContent.addPart("uid", new StringBody(this.uid));
			multipartContent.addPart("fileName", new StringBody(filePath.substring(filePath
					.lastIndexOf("/"))));
			// Send it
			httpPost.setEntity(multipartContent);
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			serverResponse = EntityUtils.toString(response.getEntity());
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return serverResponse;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		pd.setProgress((int) (progress[0]));
	}

	@Override
	protected void onPostExecute(String result) {
		System.out.println("result: " + result);
		RespVo rv=analyseRes(result, url);
		if (this.call!=null) {
			
			this.call.uoload_call(rv);
		}
		
		pd.dismiss();
	}

	@Override
	protected void onCancelled() {
		System.out.println("cancle");
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
