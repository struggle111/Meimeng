package com.swater.meimeng.fragment.party;

import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import com.swater.meimeng.commbase.MeiMengApp;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.meimeng.app.R;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.mutils.remoteview.RemoteImageView;

public class PrivateActDetail extends BaseTemplate {
    TextView txt_time, txt_place, txt_count_persons, p_act_recommend;
    RemoteImageView pic = null;
    RemoteImageView user_pic = null;
    TextView txt_desc = null;
    TextView user_nickname = null;
    TextView user_times = null;
    String aid = "", subject = "";
    String app_user_id = "";
    String LIST_STR = "查看申请";

    //参与活动的按钮
    private Button joinButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_act_detail);
        TempRun();
    }

    @Override
    public void iniView() {

        joinButton = (Button) findViewById(R.id.private_act_detail_join_button);
        joinButton.setOnClickListener(this);


        pic = (RemoteImageView) findViewById(R.id.act_img);
        user_pic = (RemoteImageView) findViewById(R.id.user_photo);
        user_nickname = findText(R.id.user_nickname);

        txt_desc = findText(R.id.p_act_intro);
        txt_place = findText(R.id.p_act_place);
        txt_time = findText(R.id.p_act_time);
        txt_count_persons = findText(R.id.party_detail_count);
        p_act_recommend = findText(R.id.p_act_recommend);

        aid = this.getIntent().getStringExtra("aid");
        subject = this.getIntent().getStringExtra("sub");
        showNavgationLeftBar("返回");
        showNavgationRightBar("申请参加");
        if (!TextUtils.isEmpty(subject)) {

            showTitle(subject);
        }
        Log.e("PrivateActDetail:","到这个了");
        getPrivateDetail();


    }

    // 返回数据
    // aid: 邀请ID,int;
    // subject: 邀请主题,string;
    // big_pic:海报图片地址,string;
    // status: 邀请状态,int;1-未开始;2-进行中;3-已结束;
    // description: 邀请描述,string;
    // time: 邀请时间,string;
    // createtime:发起时间,string;
    // place: 邀请地点,string;
    // remark: 邀请备注,string;
    // user:发起人,JSON对象;
    // ----uid:邀请人ID,int;
    // ----header:邀请人头像地址,string;
    // ----nickname:邀请人昵称,string;
    // ----locked:相册头像上锁状态,int;1-未上锁(默认);2-已上锁;
    // ----opentome: 相册头像对我开放(上锁后生效,如果没对我开放,无法查看相册和头像),int;1-不开放;2-开放;
    // applied:是否已经申请,int;1-是;2-否;
    // apply_count:申请计数,int;

    protected Object parselJson(String res) {

        int isapp = 0;
        String user_header = null, user_name = null;
        String va_time = null, va_place = null, va_count = null, va_desc = null, va_condition = null;
        try {
            JSONObject json = new JSONObject(res).getJSONObject("data");
            pic.setUrl(json.getString("big_pic"));
            va_count = json.getInt("apply_count") + "";
            va_desc = json.getString("description");
            va_place = json.getString("place");
            va_condition = json.getString("remark");
            va_time = json.getString("time");

            JSONObject userobj = json.getJSONObject("user");
            user_header = userobj.getString("header");
            app_user_id = userobj.getInt("uid") + "";
            user_name = userobj.getString("nickname");
            isapp = json.getInt("applied");
            switch (isapp) {
                case 1: // 已参加
                    view_Hide(findViewById(R.id.private_act_detail_join_button));
                    break;
                case 2: //未参加
                    break;

                default:
                    break;
            }

            int state = json.getInt("status");
            Button bu = (Button) findViewById(R.id.home_right_btn);
            //
            bu.setEnabled(false);
//            bu.setFocusable(false);
            switch (state) {
                case 1:
                    bu.setVisibility(View.VISIBLE);
                    bu.setText("报名");
                    view_Show(findViewById(R.id.private_act_detail_join_button));
                    break;
                case 2:
                    view_Hide(findButton(R.id.home_right_btn));
                    view_Hide(findViewById(R.id.private_act_detail_join_button));
                    break;
                case 3:
                    bu.setText("已结束");
                    view_Hide(findViewById(R.id.private_act_detail_join_button));
                    break;
                default:
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showToast("解析数据出错！");
            // throw new RuntimeException("解析异常！");
        } finally {

            if (app_user_id.trim().equals(
                    shareUserInfo().getUserid() + "".trim())) {
                showNavgationRightBar(LIST_STR);
            } else {

                if (isapp == 1) {
                    showNavgationRightBar("已报名");
                    view_Hide(findViewById(R.id.private_act_detail_join_button));
                }
            }

            setValueToView(txt_count_persons, "已有" + va_count + "人报名");
            setValueToView(txt_place, "" + va_place);
            setValueToView(txt_time, "" + va_time);
            setValueToView(txt_desc, "" + va_desc);
            setValueToView(user_nickname, "发起人：" + user_name);
            setValueToView(p_act_recommend, va_condition);

            user_pic.setOpenRect(true);
            user_pic.setRect(true);
            user_pic.setRectSize(120);

            user_pic.setUrl(user_header);
        }

        return null;
    }

    ;

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            ProDimiss();
            switch (msg.what) {
                case Resp_action_fail: {
                    showToast("返回数据出错！" + mg2String(msg));
                }

                break;
                case Resp_action_ok: {
                    parselJson(vo_resp.getResp());

                }

                break;
                case Resp_exception: {
                    showToast("稍等片刻！");

                }

                break;
                case ACTION_TAG1: {
                    showToast("提交成功！");
                    showNavgationRightBar("已提交");
                }

                break;
                case ACTION_TAG2: {
                    showToast("申请失败！" + mg2String(msg));

                }

                break;

                default:
                    break;
            }
        }


    };

    private void getPrivateDetail() {
        Log.e("getPrivateDetail:", "到这个方法里了");

        ProShow("正在获取详情...");

        poolThread.submit(new Runnable() {

            @Override
            public void run() {

                try {
                    req_map_param.put("key", key_server);
                    req_map_param.put("uid", shareUserInfo().getUserid() + "");
                    req_map_param.put("aid", aid);
                    vo_resp = sendReq(MURL_Invite_Detail, req_map_param);

                    Log.e("请求数据的接口：", MURL_Invite_Detail + ";" + req_map_param.toString());
                    if (null == vo_resp) {
                        handler.obtainMessage().sendToTarget();
                    }
                    if (vo_resp.isHasError()) {

                        handler.obtainMessage(Resp_action_fail,
                                vo_resp.getErrorDetail()).sendToTarget();
                    } else {

                        handler.obtainMessage(Resp_action_ok).sendToTarget();
                    }

                } catch (Exception e) {
                    handler.obtainMessage(Resp_exception).sendToTarget();
                }

            }
        });

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
            }

            break;
            case R.id.home_right_btn: {
                if (getValueView(findViewById(R.id.home_right_btn))
                        .equals(LIST_STR)) {
                    Intent in = new Intent();
                    in.putExtra("aid", aid);
                    jumpOtherActivity(JoinUserList.class, in);
                } else {
                    showConfirm();
                    // applyJoin();
                }
            }

            break;

            case R.id.private_act_detail_join_button:
                if (MeiMengApp.isVisitor){
                    MeiMengApp.setVisitorDialogView(PrivateActDetail.this,1);
                }else {
                    joinButtonListener();
                }
                break;

            default:
                break;
        }

    }

    /**
     * 参见活动的按钮的事件监听器
     */
    private void joinButtonListener() {
        //Toast.makeText(PrivateActDetail.this,"我要参与哦",Toast.LENGTH_SHORT).show();
        applyJoin();
        //txt_count_persons.setText("人数+1");
    }


    private void showConfirm() {

        AlertDialog.Builder builder = new AlertDialog.Builder(t_context);
        builder.setTitle("提示");
        builder.setMessage("提交申请后，客服人员会马上与你取得联系，谢谢您的参与");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                applyJoin();

            }
        });
        builder.show();

    }

    void applyJoin() {
        ProShow("正在提交...");

        poolThread.submit(new Runnable() {

            @Override
            public void run() {

                try {
                    req_map_param.put("key", key_server);
                    req_map_param.put("type", "2");
                    req_map_param.put("uid", shareUserInfo().getUserid() + "");
                    req_map_param.put("aid", aid);
                    vo_resp = sendReq(MURL_APPLY_ACT, req_map_param);
                    if (null == vo_resp) {
                        handler.obtainMessage().sendToTarget();
                    }
                    if (vo_resp.isHasError()) {

                        handler.obtainMessage(ACTION_TAG2,
                                vo_resp.getErrorDetail()).sendToTarget();
                    } else {

                        handler.obtainMessage(ACTION_TAG1).sendToTarget();
                    }

                } catch (Exception e) {
                    handler.obtainMessage(Resp_exception).sendToTarget();
                }

            }
        });
    }

}
