package com.swater.meimeng.activity.user;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.widget.Button;
import com.swater.meimeng.commbase.MeiMengApp;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.meimeng.app.R;
import com.swater.meimeng.activity.newtabMain.IndexActivity;
import com.swater.meimeng.activity.reg.RegChoose;
import com.swater.meimeng.activity.reg.RegUploadImg;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.database.Map_Province.CityVo;
import com.swater.meimeng.database.ShareUtil;
import com.swater.meimeng.database.ShareUtil.UserVo;
import com.swater.meimeng.database.XmlDataCity;
import com.swater.meimeng.database.XmlDataCity.ItemVo;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.constant.MConstantUser;
import com.swater.meimeng.mutils.net.RequestByPost;
import com.swater.meimeng.mutils.net.RespVo;

public class UserLogin extends BaseTemplate {
    RespVo resp = null;

    String userName = "";

    String userPassword = "";

    private Button manButton;

    private Button womanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);
        this.TempRun();
    }

    @Override
    public void iniView() {

        manButton = (Button)findViewById(R.id.man_button);
        manButton.setOnClickListener(this);

        womanButton = (Button)findViewById(R.id.woman_button);
        womanButton.setOnClickListener(this);

        view_Hide(findViewById(R.id.home_left_btn));
        showNavgationRightBar("申请");
        findEditText(R.id.user_name).requestFocus();
        // showNavgationLeftBar("返回");
        if (ShareUtil.getInstance(t_context).getUserInfo() != null) {
            if (!TextUtils.isEmpty(ShareUtil.getInstance(t_context)
                    .getUserInfo().getLoginName())) {
                setValueToView(findViewById(R.id.user_name), ShareUtil
                        .getInstance(t_context).getUserInfo().getLoginName());
            }
            ;
        }
    }

    @Override
    public void bindClick() {
        setClickEvent(findViewById(R.id.user_login_btn),
                findViewById(R.id.home_left_btn),
                findViewById(R.id.home_right_btn));
    }

    void loginUser(int visitor) {


        //有账号的用户
        if (visitor == 0) {

            MeiMengApp.isVisitor = false;

            ProShow("正在登陆...", UserLogin.this);

            userName = getValueView(findEditText(R.id.user_name)).trim();

            userPassword = getValueView(findEditText(R.id.user_pwd)).trim();
        }
        //男游客
        else if (visitor == 1) {
            userName = "66666666666";

            MeiMengApp.isVisitor=true;

            userPassword = "666666";
        }
        //女游客
        else {

            MeiMengApp.isVisitor=true;

            userName = "33333333333";

            userPassword = "666666";
        }

        if (TextUtils.isEmpty(userName)) {
            showToat("用户名不能为空！");
            ProDimiss();
            return;

        } else if (TextUtils.isEmpty(userPassword)) {
            showToat("密码不能为空！");
            ProDimiss();
            return;

        } else {

            poolThread.submit(new Runnable() {

                @Override
                public void run() {

                    try {
                        req_map_param.put("key", key_server);
                        req_map_param.put(MConstantUser.UserReg.telephone,
                                userName);
                        req_map_param.put(MConstantUser.UserReg.password,
                                userPassword);
                        resp = sendReq(MURL_user_login, req_map_param);
                        Log.d("user--login--resp--" + resp.getResp(), "---->>");
                        if (!resp.isHasError()) {
                            handler.obtainMessage(Resp_action_ok)
                                    .sendToTarget();
                        } else {
                            handler.obtainMessage(Resp_action_fail,
                                    resp.getErrorDetail()).sendToTarget();

                        }

                    } catch (Exception e) {
                        handler.obtainMessage(Resp_exception).sendToTarget();

                    }

                }
            });

        }
    }

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            ProDimiss();
            switch (msg.what) {
                case Resp_action_fail: {
                    showToat("登录失败" + mg2String(msg));
                    break;
                }

                case Resp_action_ok: {
                    // showToast("登录成功！");
                    // // int；1-未激活；2-激活； */
                    // intent.setClass(t_context, RegUploadImg.class);
                    // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // t_context.startActivity(intent);
                    parselJson(resp.getResp());
                    regPush();
                    Intent intent = new Intent();

                    if (ShareUtil.getInstance(t_context).getUserInfo().getActive() == 2) {

                        intent.setClass(t_context, IndexActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        t_context.startActivity(intent);
                        finish();
                    } else {
                        intent.setClass(t_context, WaitActive.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        t_context.startActivity(intent);
                        finish();
                    }
                    break;
                }

                case Resp_exception: {
                    showToat("登陆异常！");
                }

                break;
                case Resp_NET_FAIL: {

                }

                break;
                case -9: {
                    showToast("解析数据异常！");

                }

                break;

                default:
                    break;
            }
        }

        ;
    };

    /**
     * @category 注册JPUSH 别名
     */
    void regPush() {

        JPushInterface.setAlias(this, shareUserInfo().getUserid() + "",
                new TagAliasCallback() {

                    @Override
                    public void gotResult(int code, String msg, Set<String> arg2) {
                        NSLoger.Log("--res---code--" + code + msg);
                        if (code == 0) {
                            NSLoger.Log("--注册通知别名成功！！！---》");

                        } else {
                            NSLoger.Log("--注册通知别名失败！！！---》");
                        }

                    }
                });
    }

    protected Object parselJson(String res) {

        try {
            // active：是否激活，int；1-未激活；2-激活；
            // audit_type：认证类型，int；1-在线审核；2-面谈审核；
            // vip_level：vip等级，int；0-未付费;1-银牌；2-金牌;3-黑牌；
            // nickname:昵称,string;
            // header:头像,string;
            // medal:勋章ID拼接的字符串，string，如”1,2,3”;
            // {"result":1,"error":"","data":{"uid":55,"sex":2,"active":2,"vip_level":0,"nickname":"55",
            // "header":"http:\/\/112.124.18.97\/attachment\/tmp\/55\/header.jpg","audit_type":1,"medal":"2,3"}}

            JSONObject obj = new JSONObject(res);
            JSONObject baseobj = obj.getJSONObject("data");
            int userid = obj.getJSONObject("data").getInt(UserGobal.USER_ID);
            int sex = obj.getJSONObject("data").getInt(UserGobal.USER_SEX);
            int active = obj.getJSONObject("data").getInt(
                    UserGobal.USER_IS_ACTIVE);
            int vip_level = obj.getJSONObject("data").getInt(
                    UserGobal.USER_VIP_LEVEL);
            String nickname = obj.getJSONObject("data").getString(
                    UserGobal.USER_Nick_Name);
            String header = obj.getJSONObject("data").getString("header");
            Log.d("---userid-->" + userid, "--data---");
            UserVo user = new UserVo();
            user.setUid(userid);
            user.setAudit_type(baseobj.getInt("audit_type"));
            user.setMedal(baseobj.getString("medal"));
            user.setHeader(header);
            user.setActive(active);
            user.setNickname(nickname);
            user.setVip_level(vip_level);
            user.setSex(sex);

            shareUserInfo().saveLoginInfo(
                    req_map_param.get(MConstantUser.UserReg.telephone),
                    req_map_param.get(MConstantUser.UserReg.password));

            shareUserInfo().saveUserInfo(user);
        } catch (JSONException e) {
            handler.obtainMessage(-9).sendToTarget();
            e.printStackTrace();
        }

        return null;
    }

    ;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //有账号的用户
            case R.id.user_login_btn: {
                this.loginUser(0);
                break;

            }
            //男游客
            case R.id.man_button:
                loginUser(1);
                break;
            //女游客
            case R.id.woman_button:
                loginUser(2);
                break;
            case R.id.home_left_btn: {
                onBackPressed();

            }

            break;
            case R.id.home_right_btn: {
                // onBackPressed();
                jumpOtherActivity(RegChoose.class);

            }

            break;

            default:
                break;
        }

    }

    /**
     * Map<String, String> maps_log=new HashMap<String, String>(); void
     * uploadCrashDetail(String versionName, String releaseVer, String
     * detailMsg, String uid, long timestamp) { maps_log.put("key", key_server);
     * maps_log.put("androidVersion", "2.2"); maps_log.put("versionRelease",
     * "1.0"); maps_log.put("detailMsg", "ceshadkandandakdnadnsa");
     * maps_log.put("uid", 55+""); maps_log.put("time",
     * System.currentTimeMillis() + "");
     * <p/>
     * RespVo vo = RequestByPost.sendPostPhp(MURL_LOG, maps_log); if (vo ==
     * null) { Log.d("----uploadCrashDetail--上传出错！--》》-",
     * "----uploadCrashDetail--"); } else { if (vo.isHasError()) {
     * Log.d("----uploadCrashDetail--上传出错！--》》-" + vo.getErrorDetail(),
     * "----uploadCrashDetail--");
     * <p/>
     * } }
     */
    // }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        closePool();
    }
}
