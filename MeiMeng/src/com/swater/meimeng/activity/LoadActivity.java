package com.swater.meimeng.activity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.meimeng.app.R;
import com.swater.meimeng.activity.newtabMain.IndexActivity;
import com.swater.meimeng.activity.startguide.StartGuide;
import com.swater.meimeng.activity.user.UserLogin;
import com.swater.meimeng.activity.user.WaitActive;
import com.swater.meimeng.database.ShareUtil;
import com.swater.meimeng.mutils.constant.MConstantUser;
import com.swater.meimeng.mutils.net.RequestByPost;

public class LoadActivity extends Activity {

	boolean CANCELED = false;
	ImageView iv_logo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load);
		
		
			iv_logo = (ImageView) findViewById(R.id.iv_logo);

		//其中1表示完全不透明，0表示完全透明,这个的意思是从透明度0.1变成1.0
			Animation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
			alphaAnimation.setDuration(2000);
			iv_logo.startAnimation(alphaAnimation);

			new Handler().postDelayed(new Runnable() {
				public void run() {

					if (!CANCELED) {
						//如果得到的登录密码不是空的或者长度不为0
						if (!TextUtils.isEmpty(ShareUtil.getInstance(getBaseContext()).getUserInfo().getLoginPwd())) {

							//该用户已激活，1代表未激活
							if (ShareUtil.getInstance(getBaseContext()).getUserInfo().getActive()==2) {
								
								try {
									Intent it = new Intent(LoadActivity.this, IndexActivity.class);
									LoadActivity.this.startActivity(it);
									LoadActivity.this.finish();
									
								} catch (Exception e) {
									
								}finally{
									android.os.Process.killProcess(android.os.Process.myPid());
									System.exit(0);
								}
							}else{  //如果未激活，进入一个等待界面，等待后台审核账号
								Intent it = new Intent(LoadActivity.this, WaitActive.class);
								LoadActivity.this.startActivity(it);
								LoadActivity.this.finish();
							}
							
						}else{
						boolean isfirst=ShareUtil.getInstance(getBaseContext()).getisFirstOpen();

						//如果是第一次进入该应用
						if (isfirst) {
							Intent it = new Intent(LoadActivity.this, StartGuide.class);
							LoadActivity.this.startActivity(it);
							LoadActivity.this.finish();
						}else{

							Intent it = new Intent(LoadActivity.this, UserLogin.class);
							LoadActivity.this.startActivity(it);
							LoadActivity.this.finish();
						}
							
						}
					}
				}
			}, 2900);
	}
//	static String url_version="http://112.124.18.97/app/update.json";
//	String res_version = "";
//	protected ExecutorService poolThread = Executors.newFixedThreadPool(1);
//	protected Map<String, String> req_map_param = Collections
//			.synchronizedMap(new HashMap<String, String>());
//	static String key_server="b91e85501ab94732";
//	void checkVersion() {
////		ProShow("正在检测新版本...");
//		poolThread.submit(new Runnable() {
//
//			@Override
//			public void run() {
//				req_map_param.put(MConstantUser.UserProperty.user_key,
//						key_server);
//				req_map_param.put(MConstantUser.UserProperty.uid,
//						s.getUserid() + "");
//
//				res_version = RequestByPost.CkeckNewVersion(url_version);
//				if (res_version != null) {
//					handler.obtainMessage(Resp_action_ok).sendToTarget();
//				} else {
//					handler.obtainMessage(Resp_action_fail, "连接服务器失败！")
//							.sendToTarget();
//
//				}
//			}
//
//		});
//
//	};

	@Override
	protected void onDestroy() {
		CANCELED = true;
		super.onDestroy();
	}
}
