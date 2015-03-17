package com.swater.meimeng.fragment.party;

import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import com.swater.meimeng.commbase.MeiMengApp;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.meimeng.app.R;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.mutils.remoteview.RemoteImageView;

public class TopActivityDetail extends BaseTemplate {
    TextView txt_time, txt_place, txt_condition, txt_count_persons,
            txt_pic_time;
    RemoteImageView pic = null;
    // ImageView pic = null;
    TextView txt_desc = null;
    TextView txt_reccomend = null;
    String aid = "";

    /**
     * 高端派对的参加按钮
     */
    private Button joinButton;

    // DisplayImageOptions options;
    // protected ImageLoader imageLoader = ImageLoader.getInstance();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_act_detail);
        TempRun();
    }

    @Override
    public void iniView() {

        joinButton = (Button) findViewById(R.id.top_act_detail_join_button);
        joinButton.setOnClickListener(this);

        pic = (RemoteImageView) findViewById(R.id.act_img);
        txt_condition = findText(R.id.p_act_condition);
        txt_desc = findText(R.id.p_act_intro);
        txt_place = findText(R.id.p_act_place);
        txt_time = findText(R.id.p_act_time);
        txt_reccomend = findText(R.id.p_act_recommend);

        txt_count_persons = findText(R.id.party_detail_count);
        txt_pic_time = findText(R.id.party_detail_time);

        aid = this.getIntent().getStringExtra("aid");
        String sub = this.getIntent().getStringExtra("sub");
        showNavgationLeftBar("返回");
        showNavgationRightBar("申请参加");
        if (!TextUtils.isEmpty(sub)) {
            if (sub.contains("中国")) {
                sub = sub.replace("中国", "");

            }
            showTitle(sub);
        }
        getDetail();

    }

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
                    showToast("申请失败！");

                }

                break;

                default:
                    break;
            }
        }

        ;
    };

    protected Object parselJson(String res) {
        Log.e("高端排队的数据：",res);
        String va_time = null, remark = null, va_place = null, va_count = null, va_desc = null, va_condition = null;
        int isapp = 0;
        try {
            JSONObject json = new JSONObject(res).getJSONObject("data");
            String pic_url = json.getString("big_pic");
            pic.setUrl(pic_url);
            // options = new DisplayImageOptions.Builder()
            // .showImageOnLoading(0)
            // .showImageForEmptyUri(0)
            // .showImageOnFail(0).cacheInMemory(true)
            //
            // .imageScaleType(ImageScaleType.EXACTLY)
            // .cacheOnDisc(true).displayer(new RoundedBitmapDisplayer(6))
            // .build();
            // imageLoader.displayImage(pic_url, pic,
            // options);
            va_count = json.getInt("apply_count") + "";
            va_desc = json.getString("description");
            va_place = json.getString("place");
            remark = json.getString("remark");
            va_condition = json.getString("requirement");
            va_time = json.getString("time");
            isapp = json.getInt("applied");
            switch (isapp) {
                case 1: // 已参加
                    view_Hide(findViewById(R.id.top_act_detail_join_button));
                    break;
                case 2: //未参加
                    break;

                default:
                    break;
            }

            int state = json.getInt("status");
            Button bu = (Button) findViewById(R.id.home_right_btn);
            bu.setEnabled(false);

            Log.e("此活动的状态：",""+state);
            switch (state) {
                case 1:
                    bu.setVisibility(View.VISIBLE);
                    bu.setText("报名");
                    view_Show(findViewById(R.id.top_act_detail_join_button));
                    break;
                case 2:
                    view_Hide(findButton(R.id.home_right_btn));
                    view_Hide(findViewById(R.id.top_act_detail_join_button));
                    break;
                case 3:
                    bu.setText("已结束");
                    view_Hide(findViewById(R.id.top_act_detail_join_button));
                    break;
                default:
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("解析异常！");
        } finally {
            if (isapp == 1) {
                showNavgationRightBar("已报名");
                view_Hide(findViewById(R.id.top_act_detail_join_button));
            }

            setValueToView(txt_reccomend, remark);
            setValueToView(txt_condition, va_condition);
            setValueToView(txt_count_persons, "已有" + va_count + "人参加");
            setValueToView(txt_place, "" + va_place);
            setValueToView(txt_time, "" + va_time);
            setValueToView(txt_desc, "" + va_desc);

            setValueToView(txt_pic_time, "" + va_time);

        }

        // 返回数据
        // aid:活动ID,int;
        // subject:活动主题,string;
        // big_pic:海报图片地址,string;
        // status:活动状态,int;1-未开始;2-进行中;3-已结束;
        // description:活动介绍,string;
        // time:活动时间,string;
        // place:活动地点,string;
        // requirement:活动参与条件,string;
        // remark:活动备注,string;
        // applied:是否已经申请,int;1-是;2-否;
        // apply_count:申请计数,int;

        return null;
    }

    ;

    void getDetail() {
        ProShow("正在获取详情...");

        poolThread.submit(new Runnable() {

            @Override
            public void run() {

                try {
                    req_map_param.put("key", key_server);
                    req_map_param.put("uid", shareUserInfo().getUserid() + "");
                    req_map_param.put("aid", aid);
                    vo_resp = sendReq(MURL_Party_Detail, req_map_param);

                    Log.e("高端派对的请求接口：", MURL_Party_Detail + ";" + req_map_param.toString() + ";服务器返回的数据：" + vo_resp.getResp());
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

    void applyJoin() {
        ProShow("正在提交...");

        poolThread.submit(new Runnable() {

            @Override
            public void run() {

                try {
                    req_map_param.put("key", key_server);
                    req_map_param.put("type", "1");
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

    @Override
    public void bindClick() {
        bindNavgationEvent();

    }

    // @Override
    // protected void onStart() {
    // super.onStart();
    // if (imageLoader != null) {
    // imageLoader.resume();
    // }
    // }
    //
    // @Override
    // protected void onPause() {
    // super.onPause();
    // if (imageLoader != null) {
    // imageLoader.pause();
    // }
    // }
    //
    // @Override
    // protected void onResume() {
    // super.onResume();
    //
    // if (imageLoader != null) {
    // imageLoader.resume();
    // }
    // }
    // @Override
    // protected void onDestroy() {
    // // TODO Auto-generated method stub
    // super.onDestroy();
    // if (imageLoader != null) {
    // imageLoader.stop();
    // }
    // }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_left_btn: {
                sysback();
            }

            break;
            case R.id.home_right_btn: {
                String text = getValueView(findViewById(R.id.home_right_btn));
                if (text.equals("已提交")) {
                    return;
                }
                applyJoin();
            }

            break;

            case R.id.top_act_detail_join_button:
                if (MeiMengApp.isVisitor){
                    MeiMengApp.setVisitorDialogView(TopActivityDetail.this,1);
                }else {
                    joinButtonListener();
                }
                break;

            default:
                break;
        }

    }

    /**
     * 参加活动按钮的事件
     */
    private void joinButtonListener() {
        //Toast.makeText(TopActivityDetail.this,"我要参加高端派对",Toast.LENGTH_SHORT).show();
        applyJoin();
        //txt_count_persons.setText("人数 +1");
    }

}
