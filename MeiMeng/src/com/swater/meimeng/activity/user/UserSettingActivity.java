package com.swater.meimeng.activity.user;

import java.util.*;

import android.os.AsyncTask;
import android.os.Environment;
import com.swater.meimeng.activity.adapterGeneral.vo.AnSwerVo;
import com.swater.meimeng.commbase.MeiMengApp;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.constant.GeneralUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import cn.jpush.android.api.JPushInterface;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.User_Ada_Type;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.cell_lay_click;
import com.swater.meimeng.activity.adapterGeneral.UserAdapterItem;
import com.swater.meimeng.activity.adapterGeneral.vo.UserInfo;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.database.ShareUtil;
import com.swater.meimeng.mutils.constant.MConstantUser;
import com.swater.meimeng.mutils.net.RequestByPost;
import com.swater.meimeng.service.CheckVersionService;

/**
 * 系统个人设置相关
 */
public class UserSettingActivity extends BaseTemplate implements cell_lay_click {
    ListView ls_set = null;
    UserAdapter adapter_set = null;
    List<UserAdapterItem> data = null;

    UserInfo vo = null;


    public static final String wav_file_Name = "aa.wav";
    public static final String wav_path = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/meimeng_voice/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting_layout);


        //个人信息
        vo = new UserInfo();
//        getUserInfo();

//        vo = (UserInfo) this.getIntent().getSerializableExtra("personalInfo");
//
//
//            TempRun();
//        new getInfoTask().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        new getInfoTask().execute();
    }

    private class getInfoTask extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {

            req_map_param.put(MConstantUser.UserProperty.uid,
                    shareUserInfo().getUserid() + "");
            req_map_param.put(MConstantUser.UserProperty.user_key,
                    key_server);
            req_map_param.put("target_uid", shareUserInfo().getUserid()
                    + "");
            vo_resp = sendReq(MURL_user_all_info, req_map_param);
            if (null == vo_resp) {
                handler.obtainMessage(Resp_exception).sendToTarget();
            }
            if (vo_resp.isHasError()) {
                handler.obtainMessage(Resp_action_fail,
                        vo_resp.getErrorDetail()).sendToTarget();

            } else {
                handler.obtainMessage(1111).sendToTarget();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean a) {
            TempRun();
        }
    }
    @Override
    public void iniView() {
        ls_set = findListView(R.id.ls_set);
        showTitle("个人设置");
        adapter_set = new UserAdapter(t_context);
        adapter_set.setClick_cell_event(this);

        showNavgationLeftBar("返回");
        view_Hide(findViewById(R.id.home_right_btn));
        data = new ArrayList<UserAdapterItem>();
        for (int i = 0; i < 9; i++) {
            UserAdapterItem celldata = new UserAdapterItem();
            switch (i) {
                case 0:

                    celldata.setLeftStr("相册权限");
                    // :相册头像上锁状态,int;
                    // 1-对所有人开放(默认);
                    // 2-对所有人关闭;
                    // 3-对部分人开放;个人设置中也会用到这个状态;
//                    String state = "对所有人开放";
                    String state = "";
                    Log.e("权限状态：",""+vo.getLocked());
                    switch (vo.getLocked()) {

                        case 1: {
                            state = "对所有人开放";
                        }

                        break;
                        case 2: {

                            state = "对所有人关闭";
                        }

                        break;
                        case 3: {
//                            state = "对部分人开放";
                            state = "只对你想见的人开放";

                        }

                        break;

                        default:
                            break;
                    }
                    celldata.setRightStr(state);
                    break;
                case 1:

                    celldata.setRightStr("" + vo.getShield_count() + "人");
                    celldata.setLeftStr("屏蔽名单");
                    break;
                case 2:
                    celldata.setLeftStr("服务协议");
                    celldata.setRightStr("点击查看");

                    break;
                case 3:
                    celldata.setLeftStr("关于软件");
                    celldata.setRightStr("Version" + getAppVersionName(UserSettingActivity.this));

                    break;
                case 4:
                    celldata.setRightStr("开启推送");

                    celldata.setLeftStr("推送设置");
                    break;
                case 5:

                    celldata.setRightStr("400-8783-520");
                    celldata.setLeftStr("服务热线");
                    break;

                case 6:

                    celldata.setLeftStr("密码管理");
                    celldata.setRightStr("修改密码");
                    break;
                case 7:

                    celldata.setLeftStr("版本管理");
                    celldata.setRightStr("版本升级");
                    break;
                case 8:

                    if (MeiMengApp.isVisitor){
                        celldata.setLeftStr("");
                        celldata.setRightStr("退出");
                    }
                    else {

                    }
                    break;
                default:
                    break;
            }
            data.add(celldata);

        }

        adapter_set.setObjs(data);
        adapter_set.setType(User_Ada_Type.type_user_set_sys);

        ls_set.setAdapter(adapter_set);

    }

    @Override
    public void bindClick() {
        bindNavgationEvent();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_left_btn: {
                sysback();
//			sendBroadcast(new Intent(CMD_FRESH_ALL_DATA));

            }

            break;
            case R.id.home_right_btn: {

            }

            break;

            default:
                break;
        }

    }

    private void showConfirm() {

        AlertDialog.Builder builder = new AlertDialog.Builder(t_context);
        builder.setTitle("提示");
        builder.setMessage("是否要注销账户?");
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                app_exit.removewActivity();
                //JPushInterface.stopPush(UserSettingActivity.this);
                Intent i = new Intent();
                ShareUtil.getInstance(t_context).clearLoginInfo();
                i.setClass(UserSettingActivity.this, UserLogin.class);
                startActivity(i);
                UserSettingActivity.this.finish();

            }
        });
        builder.show();

    }

    private void callPhoneDiag() {

        AlertDialog.Builder builder = new AlertDialog.Builder(t_context);
        builder.setTitle("提示");
        builder.setMessage("确定拨打服务热线? " + "400-8783-520");
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                callTel();
            }
        });
        builder.show();

    }

    void callTel() {

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                + "4008783520"));
        UserSettingActivity.this.startActivity(intent);// 内部类
    }

    void unRegLogin() {

    }

    @Override
    public void Cell_lay_Click(View v, int pos) {
        switch (pos) {
            case 0: {

                //游客
                if (MeiMengApp.isVisitor){
                    MeiMengApp.setVisitorDialogView(UserSettingActivity.this,1);
                }

                //有账号的用户
                else {
                    Intent src = new Intent();
                    src.putExtra("type", vo.getLocked());
                    jumpOtherActivity(UserSetGalleryPower.class, src);
                }
            }
            break;
            case 1: {

                //如果是游客
                if (MeiMengApp.isVisitor){
                    MeiMengApp.setVisitorDialogView(UserSettingActivity.this,1);
                }

                //有账号的用户
                else {
                    jumpOtherActivity(UserSetForbidenUser.class);
                }


            }
            break;
            case 2: {
                jumpOtherActivity(ServiceItem.class);

            }
            break;
            case 3: {
                // jumpOtherActivity(AboutUs.class);

            }
            break;

            // case 5:
            //
            // celldata.setRightStr("400-8783-520");
            // celldata.setLeftStr("服务热线");
            // break;
            //
            // case 6:
            //
            // celldata.setLeftStr("密码管理");
            // celldata.setRightStr("修改密码");
            // break;
            // case 7:
            //
            // celldata.setLeftStr("版本管理");
            // celldata.setRightStr("版本升级");
            // break;
            // case 8:
            //
            // celldata.setLeftStr("");
            // celldata.setRightStr("注销账号");
            // break;
            case 5:
                callPhoneDiag();
                break;

            case 8:
                if (MeiMengApp.isVisitor){
                    Intent i = new Intent();
                    ShareUtil.getInstance(t_context).clearLoginInfo();
                    i.setClass(UserSettingActivity.this, UserLogin.class);
                    startActivity(i);
                    UserSettingActivity.this.finish();
                }else {
                    showConfirm();
                }

                break;
            case 6:

                //游客
                if (MeiMengApp.isVisitor){
                    MeiMengApp.setVisitorDialogView(UserSettingActivity.this,1);
                }

                //有账号的用户
                else {
                    jumpOtherActivity(MofiPwd.class);
                }
                break;
            case 7: {
                checkVersion();

            }
            // jumpOtherActivity(MofiPwd.class);
            break;
            default:
                break;
        }
    }

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            ProDimiss();
            switch (msg.what) {
                case 1111:
                    parselJson(vo_resp.getResp());
                    break;
                case Resp_action_ok: {
                    parselVersion();
                    // sysback();
                    // sendBroadcast(new Intent(CMD_FRESH_ALL_DATA));
                }

                break;
                case Resp_action_fail: {
                    showToast("保存失败！" + mg2String(msg));

                }

                break;
                case Resp_exception: {
                    showToast("服务器异常！");

                }

                break;

                default:
                    break;
            }
        }

        ;
    };
    String ver_name = "";
    String ver_desc = "";
    String ver_url = "";

    void parselVersion() {

        // {"versionname":"1.0.1","versioncode":2,"forceupdatecode":1,
        // "updatetime":"2013-09-23","description":"1.\u66f4\u65b0\u5185\u5bb91\uff0c\r\n2.\u66f4\u65b0\u5185\u5bb92\u3002",
        // "downloadurl":"http:\/\/112.124.18.97\/app\/meimeng_android_beta_0923.apk"}

        try {
            if (!TextUtils.isEmpty(res_version)) {

                JSONObject json = new JSONObject(res_version);
                ver_name = json.getString("versionname");
                ver_desc = json.getString("description");
                ver_url = json.getString("downloadurl");

                String current_version = getAppVersionName(UserSettingActivity.this);
                // Float fl_old = Float.valueOf(current_version);
                // Float fl_new = Float.valueOf(ver_name);
                // showToat("current_version--"+current_version);
                // showToat("ver_name--"+ver_name);
                Boolean ishas_new = isHasNewVersion(ver_name, current_version);
                if (ishas_new) {
                    View eulaLayout = LayoutInflater.from(
                            UserSettingActivity.this).inflate(
                            R.layout.version_check, null);
                    CheckBox box = (CheckBox) eulaLayout
                            .findViewById(R.id.skip);
                    box.setVisibility(View.GONE);
                    new AlertDialog.Builder(UserSettingActivity.this)
                            .setTitle("版本更新")
                            .setMessage(
                                    "检测到最新版本" + ver_name + "请及时更新！\r\n"
                                            + ver_desc)
                            .setView(eulaLayout)
                            .setPositiveButton("更新",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.dismiss();
                                            downAPK();
                                        }
                                    })
                            .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.dismiss();
                                            // comeInMain(null);
                                        }
                                    }).create().show();
                } else {
                    View eulaLayout = LayoutInflater.from(
                            UserSettingActivity.this).inflate(
                            R.layout.version_check, null);
                    CheckBox box = (CheckBox) eulaLayout
                            .findViewById(R.id.skip);
                    box.setVisibility(View.GONE);
                    new AlertDialog.Builder(UserSettingActivity.this)
                            .setTitle("版本").setMessage("当前版本已是最新版本！")
                            .setView(eulaLayout)
                                    // .setPositiveButton("更新",
                                    // new DialogInterface.OnClickListener() {
                                    // public void onClick(DialogInterface dialog,
                                    // int which) {
                                    // dialog.dismiss();
                                    // downAPK();
                                    // }
                                    // })
                            .setNegativeButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.dismiss();
                                            // comeInMain(null);
                                        }
                                    }).create().show();

                }
            }

        } catch (Exception e) {
            showToat(e.getMessage());
            // TODO: handle exception
        }

    }

    boolean isHasNewVersion(String newver, String oldVersion) {

        boolean isHas = false;

        // 判断第一位版本号s是否相同
        if (newver.indexOf(0) == oldVersion.indexOf(0)) {

            if (Float.parseFloat(newver.substring(2, newver.length())) > Float
                    .parseFloat(oldVersion.substring(2, oldVersion.length()))) {
                isHas = true;

            } else {
                isHas = false;
            }
        } else if (newver.indexOf(0) > oldVersion.indexOf(0)) {
            isHas = true;

        } else {
            isHas = false;

        }
        return isHas;
    }

    public String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public int getAppCode(Context context) {
        int code = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            code = pi.versionCode;

        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return code;
    }

    protected void downAPK() {
        Intent updateIntent = new Intent(UserSettingActivity.this,
                CheckVersionService.class);
        if (!TextUtils.isEmpty(ver_url)) {
            updateIntent.putExtra("apkUrl", ver_url);
            startService(updateIntent);
        }
    }

    String res_version = "";

    void checkVersion() {
        ProShow("正在检测新版本...");
        poolThread.submit(new Runnable() {

            @Override
            public void run() {
                req_map_param.put(MConstantUser.UserProperty.user_key,
                        key_server);
                req_map_param.put(MConstantUser.UserProperty.uid,
                        shareUserInfo().getUserid() + "");

                res_version = RequestByPost.CkeckNewVersion(url_version);
                if (res_version != null) {
                    handler.obtainMessage(Resp_action_ok).sendToTarget();
                } else {
                    handler.obtainMessage(Resp_action_fail, "连接服务器失败！")
                            .sendToTarget();

                }
            }

        });

    }

    ;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closePool();
    }


    /**
     * 获取个人信息
     */
    void getUserInfo() {

        ProShow("正在拉取信息...");

        try {
            poolThread.submit(new Runnable() {

                @Override
                public void run() {
                    req_map_param.put(MConstantUser.UserProperty.uid,
                            shareUserInfo().getUserid() + "");
                    req_map_param.put(MConstantUser.UserProperty.user_key,
                            key_server);
                    req_map_param.put("target_uid", shareUserInfo().getUserid()
                            + "");
                    vo_resp = sendReq(MURL_user_all_info, req_map_param);
                    if (null == vo_resp) {
                        handler.obtainMessage(Resp_exception).sendToTarget();
                    }
                    if (vo_resp.isHasError()) {
                        handler.obtainMessage(Resp_action_fail,
                                vo_resp.getErrorDetail()).sendToTarget();

                    } else {
                        handler.obtainMessage(1111).sendToTarget();
                    }

                }
            });

        } catch (Exception e) {
            handler.obtainMessage(Resp_exception).sendToTarget();
        }
    }


    protected Object parselJson(String res) {

        Log.e("从服务器获取到的个人信息：",res);
        try {
            // iniBaseView();
            // iniDetailView();
            JSONObject obj = new JSONObject(res).getJSONObject("data");

            String voice_url = obj.getJSONObject("voice_description")
                    .getString("url");
            String voice_url_audit = obj.getJSONObject("voice_description")
                    .getString("audit_url");
            if (!TextUtils.isEmpty(voice_url_audit)
                    && !voice_url_audit.equals("null")) {
                vo.setVoice_url_audit(voice_url_audit);

            }
            if (!TextUtils.isEmpty(voice_url) && !voice_url.equals("null")) {
                //
                vo.setVoice_url(voice_url);
            }
            vo.setVoice_url_local(wav_path + wav_file_Name);

            int sh_count = obj.getInt("shield_count");
            vo.setShield_count(sh_count);
            int locked = obj.getInt("locked");
            vo.setLocked(locked);

            Log.e("权限管理：",""+locked);
            // -------base info---
            JSONObject obj_base_info = obj.getJSONObject("base_info");
            vo.setJson_base_info(obj_base_info.toString());
            String desc_self = obj.getJSONObject("heart_description")
                    .getString("content");
            // {"child":"","uid":"55","degree":"","height":"171","nickname":{"audit_content":"小乖乖","content":"小乖乖"},"age":"26岁","marriage":"","city":{"city_id":1907,"province_id":19,"city_name":"盐城","province_name":"江苏"}}

            if (TextUtils.isEmpty(obj.getJSONObject("heart_description")
                    .getString("audit_content"))
                    || obj.getJSONObject("heart_description")
                    .getString("audit_content").equals("null")) {
                if (TextUtils.isEmpty(desc_self) || desc_self.equals("null")) {
                    desc_self = "我在美盟，你在哪儿？";
                }

            } else {

                desc_self = obj.getJSONObject("heart_description").getString(
                        "audit_content");
            }

            vo.setHeart_description(desc_self);
            vo.setHeight(obj_base_info.getString("height"));
            // "marriage":{"value":"离异","id":2},
            vo.setMarriage(obj_base_info.getJSONObject("marriage")
                    .getString("value"));
            vo.setId_marriage(obj_base_info.getJSONObject("marriage")
                    .getString("id"));

            String nickName_value = obj_base_info.getJSONObject("nickname")
                    .getString("content");
            String nickName_audit = obj_base_info.getJSONObject("nickname")
                    .getString("audit_content");
            if (!TextUtils.isEmpty(nickName_audit)
                    || !nickName_audit.equals("null")) {
                nickName_value = nickName_audit;

            }
            vo.setNickName(nickName_value);
            ShareUtil.UserVo uv = shareUserInfo().getUserInfo();
            // .setNickname(nickName_value);
            uv.setNickname(nickName_value);
            shareUserInfo().saveUserInfo(uv);

            // vo.setNickName(obj_base_info.getJSONObject("nickname")
            // .getString("content"));
            vo.setAge(obj_base_info.getJSONObject("age").getString(
                    "value"));
            vo.setDegree(obj_base_info.getJSONObject("degree")
                    .getString("value"));
            vo.setId_degree(obj_base_info.getJSONObject("degree")
                    .getString("id"));

            // "city":{"id":2701,"value":{"city_id":2701,"city_name":"\u6210\u90fd","province_id":27,"province_name":"\u56db\u5ddd"}}}

            // vo.setProvince_name(obj_base_info.getJSONObject("city")
            // .getString("province_name"));
            vo.setCity_name(obj_base_info.getJSONObject("city")
                    .getString("city_name"));
            vo.setCity_id(obj_base_info.getJSONObject("city").getInt(
                    "city_id")
                    + "");
            vo.setChild(obj_base_info.getJSONObject("child")
                    .getString("value"));
            vo.setId_child(obj_base_info.getJSONObject("child")
                    .getString("id"));
//            iniBaseView();
            // ----------------detail--info---
            // {"car":"A","constellation":null,"overseas_experience":"","profession":"","blood_type":{"value":"","id":0},"assets_level":"500W以下","ehtnic":{"value":"汉族","id":1},"nationality":"","audit_type":"1","zodiac":null,"family":"","salary":"","medal":null,"live":""}
            JSONObject obj_detail = obj.getJSONObject("detail_info");

            // ar":"A","constellation":"金牛座","overseas_experience":"","profession":"计算机类","blood_type":{"value":"AB","id":3},"assets_level":"500W以下","ehtnic":{"value":"汉族","id":1},"nationality":"中国","audit_type":"1","zodiac":"兔"
            vo.setJson_detailinfo(obj_detail.toString());

            vo.setXingzuo(obj_detail.getString("constellation"));
            vo.setShengxiao(obj_detail.getString("zodiac"));
            vo.setSalary(obj_detail.getJSONObject("salary").getString(
                    "value"));

            vo.setOverseas_experience(obj_detail.getJSONObject(
                    "overseas_experience").getString("value"));
            vo.setNationality(obj_detail.getJSONObject("nationality")
                    .getString("value"));
            vo.setProfession(obj_detail.getJSONObject("profession")
                    .getString("value"));
            vo.setAssets_level(obj_detail
                    .getJSONObject("assets_level").getString("value"));
            vo.setFamily(obj_detail.getJSONObject("family").getString(
                    "value"));
            vo.setCar(obj_detail.getJSONObject("car").getString(
                    "value"));
            vo.setLive(obj_detail.getJSONObject("live").getString(
                    "value"));
            // ----------id
            vo.setId_assert(obj_detail.getJSONObject("assets_level")
                    .getInt("id") + "");
            vo.setId_car(obj_detail.getJSONObject("car").getInt("id")
                    + "");
            vo.setId_career(obj_detail.getJSONObject("profession")
                    .getInt("id") + "");
            vo.setId_family_bg(obj_detail.getJSONObject("family")
                    .getInt("id") + "");
            vo.setId_live(obj_detail.getJSONObject("live")
                    .getInt("id") + "");
            vo.setId_mingzu(obj_detail.getJSONObject("assets_level")
                    .getInt("id") + "");
            vo.setId_nation(obj_detail.getJSONObject("nationality")
                    .getInt("id") + "");
            vo.setId_oversea(obj_detail.getJSONObject(
                    "overseas_experience").getInt("id")
                    + "");
            vo.setId_salery(obj_detail.getJSONObject("salary").getInt(
                    "id")
                    + "");
            // vo.setId_shengxiao(obj_detail.getJSONObject("assets_level").getInt("id")+"");
            // vo.setId_xingzuo(obj_detail.getJSONObject("assets_level").getInt("id")+"");

            // -------id

            vo.setBlood(obj_detail.getJSONObject("blood_type")
                    .getString("value"));
            vo.setMingzu(obj_detail.getJSONObject("ehtnic").getString(
                    "value"));
            ;
//            iniDetailView();
            // ----------------apearance--info---
            JSONObject obj_appearance = obj.getJSONObject("appearance_info");

            String key_j = "value";
            if (obj_appearance == null || obj_appearance.isNull("weight")) {

            } else {
                vo.setJson_bodystyle(obj_appearance.toString());
                String va_weight, va_hs = null, va_fs, va_hc, va_bs, va_ec, va_goodpos, va_face_self;
                va_weight = obj_appearance.getJSONObject("weight").getString(
                        key_j);
                va_bs = obj_appearance.getJSONObject("body_type").getString(
                        key_j);
                va_hc = obj_appearance.getJSONObject("haircolor").getString(
                        key_j);
                va_hs = obj_appearance.getJSONObject("hairstyle").getString(
                        key_j);
                va_fs = obj_appearance.getJSONObject("face_type").getString(
                        key_j);
                va_ec = obj_appearance.getJSONObject("eye_color").getString(
                        key_j);

                va_goodpos = obj_appearance.getJSONObject("good_position")
                        .getString(key_j);
                va_face_self = obj_appearance.getJSONObject("face_description")
                        .getString(key_j);
                vo.setVa3_attract_place(va_goodpos);
                vo.setVa3_body_style(va_bs);
                vo.setVa3_eye_color(va_ec);
                vo.setVa3_face_style(va_fs);
                vo.setVa3_hair_color(va_hc);
                vo.setVa3_hair_style(va_hs);
                vo.setVa3_self_assert(va_face_self);
                vo.setVa3_weight(va_weight);

            }

            // ----------------work--info---
            JSONObject obj_work = obj.getJSONObject("work_info");
            vo.setJson_workinfo(obj_work.toString());

            String va_cp_type, va_cp_trade = null, va_workstate, income, grad_year, va_pro, lang;
            if (obj_work == null
                    || obj_work.isNull(MConstantUser.UserAll_work_info.company_type)) {

            } else {

                va_cp_trade = obj_work.getJSONObject(
                        MConstantUser.UserAll_work_info.company_trade)
                        .getString(key_j);
                va_cp_type = obj_work.getJSONObject(
                        MConstantUser.UserAll_work_info.company_type).getString(key_j);
                va_workstate = obj_work.getJSONObject(
                        MConstantUser.UserAll_work_info.work_status).getString(key_j);
                income = obj_work.getJSONObject(MConstantUser.UserAll_work_info.income)
                        .getString(key_j);
                grad_year = obj_work.getJSONObject(
                        MConstantUser.UserAll_work_info.graduation_time).getString(key_j);
                va_pro = obj_work.getJSONObject(MConstantUser.UserAll_work_info.profession)
                        .getString(key_j);
                JSONArray arr = obj_work
                        .getJSONArray(MConstantUser.UserAll_work_info.language);
                StringBuilder sb = new StringBuilder("");

                for (int i = 0; i < arr.length(); i++) {
                    String va = arr.getJSONObject(i).getString(key_j);
                    sb.append(va);

                }
                lang = sb.toString();
                // setValueToView(findViewById(R.id.va4_comp_trade),
                // va_cp_trade);
                // setValueToView(findViewById(R.id.va4_comp_type), va_cp_type);
                // setValueToView(findViewById(R.id.va4_grad_year), grad_year);
                // setValueToView(findViewById(R.id.va4_income_desc), income);
                // setValueToView(findViewById(R.id.va4_job_state),
                // va_workstate);
                // setValueToView(findViewById(R.id.va4_lang), lang);
                // setValueToView(findViewById(R.id.va4_prof_type), va_pro);
                vo.setVa4_comp_trade(va_cp_type);
                vo.setVa4_comp_type(va_cp_trade);
                vo.setVa4_grad_year(grad_year);
                vo.setVa4_income_desc(income);
                vo.setVa4_job_state(va_workstate);
                vo.setVa4_lang(lang);
                vo.setVa4_prof_type(va_pro);

            }

            // ----------------life--info---
            JSONObject obj_life = obj.getJSONObject("life_info");
            if (obj_life == null
                    || obj_life.isNull(MConstantUser.UserAll_life_info.largest_consumer)) {

            } else {

                vo.setJson_lifeinfo(obj_life.toString());
                vo.setVa5_big_lar(getStrFrJson(obj_life
                        .getJSONObject(MConstantUser.UserAll_life_info.largest_consumer)));
                vo.setVa5_drink(getStrFrJson(obj_life
                        .getJSONObject(MConstantUser.UserAll_life_info.drinking)));

                vo.setVa5_execise(getStrFrJson(obj_life
                        .getJSONObject(MConstantUser.UserAll_life_info.take_exercise)));
                vo.setVa5_issmoke(getStrFrJson(obj_life
                        .getJSONObject(MConstantUser.UserAll_life_info.smoke)));
                vo.setVa5_iswantchild(getStrFrJson(obj_life
                        .getJSONObject(MConstantUser.UserAll_life_info.want_children)));
                vo.setVa5_livewith(getStrFrJson(obj_life
                        .getJSONObject(MConstantUser.UserAll_life_info.with_parents)));
                vo.setVa5_makeroma(getStrFrJson(obj_life
                        .getJSONObject(MConstantUser.UserAll_life_info.make_romantic)));
                vo.setVa5_pets(getStrArr(obj_life
                        .getJSONArray(MConstantUser.UserAll_life_info.pet_value)));
                vo.setVa5_rank(getStrFrJson(obj_life
                        .getJSONObject(MConstantUser.UserAll_life_info.family_ranking)));
                vo.setVa5_rest(getStrFrJson(obj_life
                        .getJSONObject(MConstantUser.UserAll_life_info.schedule)));
                vo.setVa5_skills(getStrArr(obj_life
                        .getJSONArray(MConstantUser.UserAll_life_info.life_skill)));
                vo.setVa5_zongjiao(getStrFrJson(obj_life
                        .getJSONObject(MConstantUser.UserAll_life_info.religion)));

            }

            // ----------------hobby--info----

            JSONObject obj_hobby_info = obj.getJSONObject("hobby_info");
            vo.setJson_hobby_info(obj_hobby_info.toString());

//            if (obj_hobby_info == null
//                    || obj_hobby_info.isNull(MConstantUser.UserAll_hobby_info.like_sports)) {
//                getHobbyData(null, "喜欢的运动:");
//
//                getHobbyData(null, "喜欢的食物:");
//                getHobbyData(null, "喜欢的书籍:");
//                getHobbyData(null, "喜欢的音乐:");
//                getHobbyData(null, "喜欢的电影:");
//                getHobbyData(null, "关注节目:");
//                getHobbyData(null, "娱乐休闲:");
//                getHobbyData(null, "业余爱好:");
//                getHobbyData(null, "喜欢的旅游:");
//
//            } else {
//
//                getHobbyData(
//                        obj_hobby_info
//                                .getJSONArray(MConstantUser.UserAll_hobby_info.like_sports),
//                        "喜欢的运动:");
//
//                getHobbyData(
//                        obj_hobby_info
//                                .getJSONArray(MConstantUser.UserAll_hobby_info.like_food),
//                        "喜欢的食物:");
//                getHobbyData(
//                        obj_hobby_info
//                                .getJSONArray(MConstantUser.UserAll_hobby_info.like_book),
//                        "喜欢的书籍:");
//                getHobbyData(
//                        obj_hobby_info
//                                .getJSONArray(MConstantUser.UserAll_hobby_info.like_music),
//                        "喜欢的音乐:");
//                getHobbyData(
//                        obj_hobby_info
//                                .getJSONArray(MConstantUser.UserAll_hobby_info.like_movie),
//                        "喜欢的电影:");
//                getHobbyData(
//                        obj_hobby_info
//                                .getJSONArray(MConstantUser.UserAll_hobby_info.attention_item),
//                        "关注节目:");
//                getHobbyData(
//                        obj_hobby_info
//                                .getJSONArray(MConstantUser.UserAll_hobby_info.entertainment),
//                        "娱乐休闲:");
//                getHobbyData(
//                        obj_hobby_info
//                                .getJSONArray(MConstantUser.UserAll_hobby_info.amateur_like),
//                        "业余爱好:");
//                getHobbyData(
//                        obj_hobby_info.getJSONArray(MConstantUser.UserAll_hobby_info.travel),
//                        "喜欢的旅游:");
//            }

//            adapter_hobby.notifyDataSetChanged();
//            vo.setHobby_data(arr_hobby);
            // -------个性展示
//            JSONArray obj_survey = obj.getJSONArray("personality_info");
//            arr_sueveys.clear();

//            if (GeneralUtil.isNotNullObject(obj_survey)
//                    && obj_survey.length() > 0) {
//                int length = obj_survey.length();
//                NSLoger.Log("--length-->>" + length);
//                getAnList(obj_survey);
//                adapter_personlity.notifyDataSetChanged();
//                String msg = adapter_personlity.getObjs().toString();
//                NSLoger.Log(msg + "--msg-->>");
//                if (length < 6) {
//                    for (int i = length; i < 7; i++) {
//                        AnSwerVo asv = new AnSwerVo();
//
//                        asv.setAnswer("暂时保密");
//
//                        switch (i) {
//                            case 0: {
//                                asv.setType("理想对象");
//
//                            }
//
//                            break;
//                            case 1: {
//                                asv.setType("婚后生活");
//
//                            }
//
//                            break;
//                            case 2: {
//                                asv.setType("婚姻期望");
//
//                            }
//
//                            break;
//                            case 3: {
//
//                                asv.setType("约会类型");
//                            }
//
//                            break;
//                            case 4: {
//                                asv.setType("生活习惯");
//
//                            }
//
//                            break;
//                            case 5: {
//                                asv.setType("个性描述");
//
//                            }
//
//                            break;
//                            case 6: {
//
//                                asv.setType("爱情观点");
//                            }
//
//                            break;
//
//                            default:
//                                break;
//                        }
////                        arr_sueveys.add(asv);
//                    }
//
//                }
//
//            } else {
//
//                for (int i = 0; i < 7; i++) {
//                    AnSwerVo asv = new AnSwerVo();
//
//                    asv.setAnswer("暂时保密");
//
//                    switch (i) {
//                        case 0: {
//                            asv.setType("理想对象");
//
//                        }
//
//                        break;
//                        case 1: {
//                            asv.setType("婚后生活");
//
//                        }
//
//                        break;
//                        case 2: {
//                            asv.setType("婚姻期望");
//
//                        }
//
//                        break;
//                        case 3: {
//
//                            asv.setType("约会类型");
//                        }
//
//                        break;
//                        case 4: {
//                            asv.setType("生活习惯");
//
//                        }
//
//                        break;
//                        case 5: {
//                            asv.setType("个性描述");
//
//                        }
//
//                        break;
//                        case 6: {
//
//                            asv.setType("爱情观点");
//                        }
//
//                        break;
//
//                        default:
//                            break;
//                    }
////                    arr_sueveys.add(asv);
//
//                }
////                ls_personlity.setAdapter(adapter_personlity);
////                adapter_personlity.notifyDataSetChanged();
//                // setListViewHeightBasedOnChildren(ls_personlity);
//
//                // reComputerListHeight(ls_personlity, adapter_personlity);
//            }
            // personality_info=[{"type_value":"理想对象","type_id":"1",
            // "question":[{"answer":"我希望她是一个乐观积极的人；","option_value":"乐观积极","option_id":"3"
            // ,"question_value":"你希望Ta是一个什么样的人？","type_text":"理想对象","question_id":"1","type_id":"1"},
            // {"answer":"我觉得觉得男人会不会做饭很重要，因为我不会，所以他必须会。","option_value":"重要，因为我不会，所以他必须会  "
            // ,"option_id":"1","question_value":"你觉得男人会不会做饭重要吗?","type_text":"理想对象","question_id":"4","type_id":"1"}]},
            // {"type_value":"婚后生活","type_id":"2","question":[{"answer":"我脑海中的婚后生活是男女平等分工，共同承担家庭生活的一切；",
            // "option_value":"男女平等分工，共同承担家庭生活的一切    ","option_id":"2","question_value":"你脑海中的婚后生活中是怎样分工的？"
            // +
            // "","type_text":"婚后生活","question_id":"1","type_id":"2"},{"answer":"我最喜欢开座位很高、四轮驱动的名牌越野车；",
            // "option_value":"座位很高、四轮驱动的名牌越野车  ","option_id":"2","question_value":"你最喜欢开哪种类型的小汽车？",
            // "type_text":"婚后生活","question_id":"2","type_id":"2"},{"answer":"对于个人空间的问题，我不需要太多的个人空间，"
            // +
            // "我喜欢两人常在一起；","option_value":"我不需要太多的个人空间，我喜欢两人常在一起  ","option_id":"1",
            // "question_value":"当你在婚姻中，你希望你的个人空间有多大？","type_text":"婚后生活","question_id":"3","type_id":"2"}
            // ,{"answer":"我如果提前退休开始享受生活，我会买一屋子的书，开始好好看书；","option_value":"买一屋子的书，终于可以好好看书了   ",
            // "option_id":"3","question_value":"你如果提前退休开始享受生活，你会选择什么样的生活方式？","type_text":"婚后生活",
            // "question_id":"4","type_id":"2"},{"answer":"我理想中的婚礼应该是在大草坪上的西式婚礼，礼服婚纱必不可少，留下永恒记忆。",
            // "option_value":"大草坪上的西式婚礼，礼服婚纱必不可少，留下永恒记忆   ","option_id":"3","question_value":"你理想的婚礼是什么样的？","type_text":"婚后生活",
            // "question_id":"5","type_id":"2"}]},{"type_value":"婚姻期望","type_id":"3","question":[{"answer":"婚后的家居环境，我觉得舒适是第一位的，家里不太乱就行；",
            // "option_value":"舒适是第一位的，家里不太乱就行","option_id":"1","question_value":"你希望婚后的家居环境是怎样的？",
            // "type_text":"婚姻期望","question_id":"2","type_id":"3"}]},
            // {"type_value":"约会类型","type_id":"4","question":[{"answer":"约会时，我希望采用AA制，这样相处没有负担，我更看重男士的品质和综合性能力；"
            // ,"option_value":"我希望采用AA制，这样相处没有负担，我更看重男士的品质和综合性能力","option_id":"1","question_value":"对于约会礼仪，哪一种方式会让你感觉很舒服？","type_text":"约会类型","question_id":"3","type_id":"4"}]},{"type_value":"生活习惯","type_id":"5","question":[{"answer":"我平时喜欢美食，是半个美食家，踏遍全城饭店，有时也愿意自己尝试当大厨；","option_value":"我是半个美食家，踏遍全城饭店，有时也愿意自己尝试当大厨","option_id":"1","question_value":"你平时的兴趣爱好是什么？","type_text":"生活习惯","question_id":"1","type_id":"5"},{"answer":"在金钱方面，我不管有没有钱，都很节约，从不乱花钱；","option_value":"不管有没有钱，我都很节约，从不乱花钱   ","option_id":"1","question_value":"你在花钱方面是个什么样的人？","type_text":"生活习惯","question_id":"2","type_id":"5"},{"answer":"我擅长把家里人照顾的健健康康的，做好卫生和饮食；","option_value":"我会把家里人照顾的健健康康的，做好卫生和饮食","option_id":"2","question_value":"你擅长的生活技能？","type_text":"生活习惯","question_id":"3","type_id":"5"},{"answer":"关于锻炼，我会在工作告一段落后，抽出一段时间集中锻炼；","option_value":"我会在工作告一段落后，抽出一段时间集中锻炼   ","option_id":"3","question_value":"你平时的锻炼习惯？","type_text":"生活习惯","question_id":"4","type_id":"5"},{"answer":"关于作息习惯，我经常熬夜，喜欢享受夜生活。","option_value":"我经常熬夜，喜欢享受夜生活  ","option_id":"2","question_value":"你平时的作息习惯？","type_text":"生活习惯","question_id":"5","type_id":"5"}]},{"type_value":"爱情观点","type_id":"7","question":[{"answer":"对于感觉，我觉得来电很重要，我相信一见钟情；","option_value":"很重要，我相信一见钟情  ","option_id":"1","question_value":"对一个人的来电的感觉（chemistry）对你有多重要？","type_text":"爱情观点","question_id":"1","type_id":"7"},{"answer":"关于异地恋，我认为只能在双方都接受的一段时间内成立；","option_value":"只能在双方都接受的一段时间内成立  ","option_id":"2","question_value":"你怎样看待异地的恋情？","type_text":"爱情观点","question_id":"2","type_id":"7"},{"answer":"如果发现对方有令我很反感的地方，不会轻易放弃，努力适应，尽量多看对方的优点；","option_value":"不愿意轻易放弃，努力适应，尽量多看对方的优点  ","option_id":"2","question_value":"如果发现对方有令你很反感的地方,你…","type_text":"爱情观点","question_id":"3","type_id":"7"},{"answer":"我认为爱人有异性朋友是很正常的事情；","option_value":"我觉得爱人有异性朋友很正常","option_id":"2","question_value":"你对你的爱人有异性朋友怎么看？","type_text":"爱情观点","question_id":"4","type_id":"7"},{"answer":"恋人被家庭和朋友接受对我来说非常重要， 我不可能与不被家人和朋友接受的人相处。","option_value":"非常重要， 我不可能与不被家人和朋友接受的人相处  ","option_id":"1","question_value":"你的恋人被你的家庭和朋友接受重要吗？","type_text":"爱情观点","question_id":"5","type_id":"7"}]}]
            // NSLoger.Log(obj)
        } catch (JSONException e) {
            showToast("解析数据出错！！" + e == null ? "" : e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    String getStrFrJson(JSONObject sn) {
        String str = "";
        try {
            str = sn.getString("value");
        } catch (JSONException e) {
            showToast("解析生活信息数据出错！！" + e == null ? "" : e.getMessage());
            e.printStackTrace();
        }
        return str;
    }

    String getStrArr(JSONArray arr) throws JSONException {
        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < arr.length(); i++) {
            String va = arr.getJSONObject(i).getString("value");
            sb.append(va);

        }
        return sb.toString();
    }

}
