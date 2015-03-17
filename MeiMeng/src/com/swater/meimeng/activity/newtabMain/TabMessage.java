package com.swater.meimeng.activity.newtabMain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.display.DisplayManager;

import android.util.DisplayMetrics;
import android.view.*;
import android.widget.TextView;
import com.swater.meimeng.activity.user.CustomDialog;
import com.swater.meimeng.commbase.MeiMengApp;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ListAdapter;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.MessageAdapter;
import com.swater.meimeng.activity.adapterGeneral.MessageAdapter.Click_AGREE_BUTTON;
import com.swater.meimeng.activity.adapterGeneral.MessageAdapter.MSG_TYPE;
import com.swater.meimeng.activity.adapterGeneral.MessageAdapter.Msg_Cell_Click;
import com.swater.meimeng.activity.adapterGeneral.vo.MsgVo;
import com.swater.meimeng.activity.adapterGeneral.vo.UserSearchVo;
import com.swater.meimeng.activity.user.UserSetHome;
import com.swater.meimeng.commbase.DiagPopView;
import com.swater.meimeng.commbase.DiagPopView.Diag_ok_no;
import com.swater.meimeng.commbase.TabBaseTemplate;
import com.swater.meimeng.fragment.party.JoinUserList;
import com.swater.meimeng.fragment.party.PrivateActDetail;
import com.swater.meimeng.fragment.party.TopActivityDetail;
import com.swater.meimeng.fragment.recommend.BarPushActivity;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.constant.GeneralUtil;
import com.swater.meimeng.mutils.net.RespVo;
import com.swater.meimeng.mutils.pullxlist.PullXListView;
import com.swater.meimeng.mutils.pullxlist.PullXListView.IXListViewListener;

public class TabMessage extends TabBaseTemplate implements Click_AGREE_BUTTON,
        Diag_ok_no, Msg_Cell_Click, IXListViewListener {

    int pageIndex = 1;
    int page_current = -1;
    int page_size = -1;
    int page_total_count = -1;
    int page_total_page = -1;
    boolean istopData = true;
    List<MsgVo> data_ls = new ArrayList<MsgVo>();
    List<MsgVo> data_ls_app = Collections
            .synchronizedList(new ArrayList<MsgVo>());
    List<MsgVo> data_ls_user = Collections
            .synchronizedList(new ArrayList<MsgVo>());
    PullXListView pullList = null, pullList_app = null, pullList_user = null;
    MessageAdapter adapter_msg = null, adapter_msg_app = null,
            adapter_msg_user = null;
    final int type_sys = 1;
    final int type_user = 2;
    final int type_apply = 3;
    Button btn_sys, btn_user, btn_apply;

    public final static int ACTION_SYS = 716;
    public final static int ACTION_APP = 816;
    public final static int ACTION_USER = 916;
    Map<String, Integer> action_map = Collections
            .synchronizedMap(new HashMap<String, Integer>());
    final String key_action = "key_action";
    final String key_type = "key_type";
    boolean isUser = false;

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {

            closePool();
        } catch (Exception e) {
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);
        t_context = this;
        TempRun();
    }

    @Override
    public void iniView() {

        setValueToView(findViewById(R.id.center_show), "美盟传情");
        btn_apply = (Button) findViewById(R.id.msg_btn_apply);
        btn_user = (Button) findViewById(R.id.msg_btn_user);
        btn_sys = (Button) findViewById(R.id.msg_btn_sys);
        pullList = (PullXListView) findViewById(R.id.msg_list);
        pullList_app = (PullXListView) findViewById(R.id.msg_list_sys);
        pullList_user = (PullXListView) findViewById(R.id.msg_list_user);

        pullList.setPullLoadEnable(true);
        pullList.setPullRefreshEnable(true);
        pullList_user.setPullLoadEnable(true);
        pullList_user.setPullRefreshEnable(true);
        btn_apply.setOnClickListener(this);
        btn_user.setOnClickListener(this);
        btn_sys.setOnClickListener(this);

        pullList.setXListViewListener(this);
        pullList_app.setXListViewListener(this);
        pullList_user.setXListViewListener(this);
        iniViewData();
        bindMenuClick();

    }

    @Override
    public void bindClick() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_img_btn:
            case R.id.home_left_btn: {
                this.LeftClickPop(findViewById(R.id.topNav));
            }

            break;
            case R.id.msg_btn_apply: {
                if (MeiMengApp.isVisitor){
                    MeiMengApp.setVisitorDialogView(TabMessage.this,1);
                }else {
                    isUser = false;
                    pageIndex = 1;
                    data_ls_app.clear();
                    view_Hide(pullList_user, pullList);
                    view_Show(pullList_app);
                    if (null == adapter_msg_app) {

                        adapter_msg_app = new MessageAdapter(t_context);
                        adapter_msg_app.setData(data_ls_app);
                        adapter_msg_app.setCell_click(this);
                        adapter_msg_app.setLisview(pullList_app);
                        pullList_app.setAdapter(adapter_msg_app);
                    }
                    adapter_msg_app.setCell_lay_click(this);

                    // if (pullList_app.getVisibility()==View.VISIBLE) {
                    // showToast("显示申请！");
                    //
                    // }
                    adapter_msg_app.setTYPE(MSG_TYPE.MSG_TYPE_APPLY);
                    btn_apply.setBackgroundResource(R.drawable.right_p);
                    btn_sys.setBackgroundResource(R.drawable.title_left);
                    btn_user.setBackgroundResource(R.drawable.title_center);

                    btn_apply.setTextColor(this.getResources().getColor(R.color.white));
                    btn_sys.setTextColor(this.getResources().getColor(R.color.blank));
                    btn_user.setTextColor(this.getResources().getColor(R.color.blank));

                    adapter_msg_app.setCell_lay_click(TabMessage.this);
                    getMsgList(type_apply, ACTION_APP);
                }

            }

            break;
            case R.id.msg_btn_user: {
                if (MeiMengApp.isVisitor) {
//                CustomDialog dialog = new CustomDialog(TabMessage.this,R.style.VisitorCustomDialog);
//                dialog.show();
////                WindowManager manager = getWindowManager();
////                Display display = manager.getDefaultDisplay();
//                DisplayMetrics metrics = getResources().getDisplayMetrics();
//
//                int width = metrics.widthPixels *5 /6;
//                int height = metrics.heightPixels/4;
//                Log.e("对话框的宽高：","width = " + width+";height = "+height);
//                dialog.getWindow().setLayout(width,height);
                    MeiMengApp.setVisitorDialogView(TabMessage.this,1);
                } else {
                    isUser = true;
                    pageIndex = 1;
                    data_ls_user.clear();
                    view_Hide(pullList, pullList_app);
                    view_Show(pullList_user);
                    // if (pullList_user.getVisibility()==View.VISIBLE) {
                    // showToast("显示用户！");
                    //
                    // }
                    btn_apply.setBackgroundResource(R.drawable.title_right);
                    btn_sys.setBackgroundResource(R.drawable.title_left);
                    btn_user.setBackgroundResource(R.drawable.middle_p);

                    btn_apply.setTextColor(this.getResources().getColor(R.color.blank));
                    btn_sys.setTextColor(this.getResources().getColor(R.color.blank));
                    btn_user.setTextColor(this.getResources().getColor(R.color.white));

                    if (null == adapter_msg_user) {
                        adapter_msg_user = new MessageAdapter(t_context);
                        adapter_msg_user.setCell_click(this);
                        adapter_msg_user.setData(data_ls_user);
                        adapter_msg_user.setLisview(pullList_user);
                        pullList_user.setAdapter(adapter_msg_user);
                        adapter_msg_user.setTYPE(MSG_TYPE.MSG_TYPE_USER);
                    }
                    getMsgList(type_user, ACTION_USER);
                }

            }

            break;
            case R.id.msg_btn_sys: {
                isUser = false;
                pageIndex = 1;
                data_ls.clear();
                view_Hide(pullList_user, pullList_app);
                view_Show(pullList);
                // if (pullList.getVisibility()==View.VISIBLE) {
                // showToast("显示系统！");
                //
                // }
                btn_apply.setBackgroundResource(R.drawable.title_right);
                btn_sys.setBackgroundResource(R.drawable.left_p);
                btn_user.setBackgroundResource(R.drawable.title_center);

                btn_apply.setTextColor(this.getResources().getColor(R.color.blank));
                btn_sys.setTextColor(this.getResources().getColor(R.color.white));
                btn_user.setTextColor(this.getResources().getColor(R.color.blank));

                if (null == adapter_msg) {
                    adapter_msg = new MessageAdapter(t_context);
                    adapter_msg.setCell_click(this);
                    adapter_msg.setData(data_ls);
                    adapter_msg.setLisview(pullList);
                    adapter_msg.setTYPE(MSG_TYPE.MSG_TYPE_SYS);
                    pullList.setAdapter(adapter_msg);
                }
                getMsgList(type_sys, ACTION_SYS);
            }

            break;

            default:
                break;
        }

    }

    @Override
    public void onHeader_ItemClick(View v) {
        judgeTypeMyfocus(v, TabMessage.this);
        switch (v.getId()) {
            case R.id.gallery_imv:
            case R.id.lay_person: {
                if (MeiMengApp.isVisitor){
                    MeiMengApp.setVisitorDialogView(TabMessage.this,1);
                }else {
                    jumpOtherActivity(UserSetHome.class);
                }


            }
            // case R.id.gallery_imv: {
            // jumpOtherActivity(UserGallery.class);
            //
            // }
            // break;

            default:
                break;
        }

    }

    private void showConfirm(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(t_context);
        builder.setTitle("提示");
        Object obj = adapter_msg.getData().get(pos);
        String user_Name = "";
        String app_userid = "";
        if (obj instanceof MsgVo) {
            user_Name = ((MsgVo) obj).getUser_title();
            app_userid = ((MsgVo) obj).getUid();
        }
        builder.setMessage("同意" + user_Name + "查看你的相册吗?");
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final String appuid = app_userid;
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                agreeSee(appuid);

            }
        });
        builder.show();

    }


    void agreeSee(String uid) {
        try {
            ProShow("请求数据中...");
            req_map_param.put("uid", shareUserInfo().getUserid() + "");
            req_map_param.put("key", key_server);
            req_map_param.put("applyid", uid);
            poolThread.submit(new Runnable() {

                @Override
                public void run() {
                    // applyid
                    RespVo rv = sendReq(MURL_MSG_AGREE_APPLY, req_map_param);
                    if (rv == null) {
                        handler.obtainMessage(-11).sendToTarget();
                    } else {

                        if (rv.isHasError()) {
                            handler.obtainMessage(-12, rv.getErrorDetail())
                                    .sendToTarget();
                        } else {
                            handler.obtainMessage(-13).sendToTarget();
                        }
                    }

                }
            });
        } catch (Exception e) {
            handler.obtainMessage(Resp_exception).sendToTarget();
        }
    }

    /**
     * URL：/api/message/agree 请求参数： 参数名 必填 类型 描述 key true string 参见规范中的key值 uid
     * true int 用户自己的ID applyid true int 申请人ID
     */
    int pos = 0;

    // 同意按钮
    @Override
    public void Click_AGREE_BUTTON(View v, int pos) {
        // pos = pos;
        try {
            // showToast("11");
            popCall(data_ls_app.get(pos).getUser_title());
        } catch (Exception e) {
            e.printStackTrace();
            showToast("" + e.getMessage());
            // TODO: handle exception
        }

    }

    @Override
    public void btn_ok_no(View v) {
        // showToast("agree11");
        switch (v.getId()) {
            case R.id.app_yes: {
                // showToast("agree");
                agreeSee(data_ls_app.get(pos).getUid() + "");

            }

            break;

            default:
                break;
        }
        if (diagpopview != null) {
            diagpopview.dismiss();
        }
    }

    void iniViewData() {
        if (pullList != null) {

            view_Show(pullList);
        }
        if (pullList_app != null) {
            view_Hide(pullList_app);

        }
        if (pullList_user != null) {
            view_Hide(pullList_user);

        }
        // view_Hide(pullList_user, pullList_app);
        btn_apply.setBackgroundResource(R.drawable.title_right);
        btn_sys.setBackgroundResource(R.drawable.left_p);
        btn_user.setBackgroundResource(R.drawable.title_center);
        if (null == adapter_msg) {
            adapter_msg = new MessageAdapter(t_context);
            adapter_msg.setCell_click(this);
            adapter_msg.setData(data_ls);
            adapter_msg.setLisview(pullList);
            pullList.setAdapter(adapter_msg);
        }
        adapter_msg.setTYPE(MSG_TYPE.MSG_TYPE_SYS);
        getMsgList(type_sys, ACTION_SYS);

    }

    // cell点击事件

    @Override
    public void Msg_Cell_Click(View v, int pos, ListAdapter ada, MSG_TYPE type,
                               Object ob) {

        MsgVo mv = null;
        if (null != ob) {
            if (ob instanceof MsgVo) {
                mv = (MsgVo) ob;

            }
        }

        if (null != ada) {
            switch (type) {
                case MSG_TYPE_APPLY: {
                    // showToast("申请消息" + mv.getTargrt_name());

                    Intent in = new Intent(t_context, BarPushActivity.class);
                    UserSearchVo data = new UserSearchVo();
                    data.setUid(data_ls_app.get(pos).getUid() + "");
                    in.putExtra("data", data);
                    TabMessage.this.startActivity(in);

                }

                break;
                case MSG_TYPE_USER: {
                    // showToast("用户消息" + mv.getTargrt_name());
                    // ---type:消息类型,int;1-问候(跳转用户详情);2-我想见TA(跳转用户详情界面);3-活动申请(跳转到对应活动的申请列表);
                    // ----aid:活动ID,int;(类型为活动申请时有效)

                    switch (mv.getType()) {
                        case 2:
                        case 1: {

                            Intent in = new Intent(t_context, BarPushActivity.class);
                            UserSearchVo data = new UserSearchVo();
                            data.setUid(mv.getTarget_id() + "");
                            in.putExtra("data", data);
                            TabMessage.this.startActivity(in);

                        }

                        break;
                        case 3: {
                            Intent in = new Intent(t_context, JoinUserList.class);
                            in.putExtra("aid", mv.getTarget_id() + "");
                            TabMessage.this.startActivity(in);

                        }

                        break;
                    }

                }

                break;
                case MSG_TYPE_SYS: {
                    // ----type:消息类型,int;1-资料审核未通过(跳转个人设置);2-文字推送(无操作);3-推荐对象(跳转到用户详情);4-派对通知(跳转到派对详情);5-活动创建成功(跳转到私人活动详情)

                    // showToast("系统消息" + mv.getTargrt_name());

                    switch (mv.getType()) {
                        case 1: {
                            // showToast("跳转个人设置");
                            Intent in = new Intent(t_context, UserSetHome.class);
                            // UserSearchVo data = new UserSearchVo();
                            // data.setUid(mv.getTarget_id() + "");
                            // in.putExtra("data", data);
                            TabMessage.this.startActivity(in);
                        }

                        break;
                        case 2: {

                        }

                        break;
                        case 3: {
                            // showToast("跳转到用户详情");
                            Intent in = new Intent(t_context, BarPushActivity.class);
                            UserSearchVo data = new UserSearchVo();
                            data.setUid(mv.getTarget_id() + "");
                            in.putExtra("data", data);
                            TabMessage.this.startActivity(in);

                        }

                        break;
                        case 4: {
                            // showToast("跳转到派对详情");
                            Intent in = new Intent(t_context, TopActivityDetail.class);
                            in.putExtra("aid", mv.getTarget_id() + "");
                            in.putExtra("sub", mv.getTargrt_name() + "");
                            TabMessage.this.startActivity(in);

                        }

                        break;
                        case 5: {
                            // showToast("私人活动详情");
                            Intent in = new Intent(t_context, PrivateActDetail.class);
                            in.putExtra("aid", mv.getTarget_id() + "");
                            in.putExtra("sub", mv.getTargrt_name());
                            TabMessage.this.startActivity(in);

                        }

                        break;

                        default:
                            break;
                    }

                }

                break;

                default:
                    break;
            }

        }

    }

    void getPage(String res) {
        try {
            /**
             * current_page":1,"total_page":2,"page_size":8,"total_count":"11"
             * public final String PAGE_KEY_current_page="current_page"; public
             * final String PAGE_KEY_total_page="total_page"; public final
             * String PAGE_KEY_page_size="page_size"; public final String
             * PAGE_KEY_total_count="total_count";
             */

            JSONObject obj = new JSONObject(res).getJSONObject("data");
            page_current = obj.getInt("current_page");
            page_size = obj.getInt("page_size");
            page_total_count = Integer.parseInt(obj.getString("total_count"));
            page_total_page = obj.getInt("total_page");
            NSLoger.Log(page_current + page_total_page + "" + page_total_count);

        } catch (Exception e) {
            // TODO: handle exception
            showToast("解析分页数据出错！" + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /**
         * adapter_msg=null; pageIndex=1;
         */
        // data_cache.put("msg_data", data_ls);
    }

    private void loadPage(PullXListView ls) {
        ls.stopRefresh();
        ls.stopLoadMore();
        ls.setRefreshTime(GeneralUtil.simpleDateFormat.format(new Date()));
    }

    private void loadOver(PullXListView ls) {
        ls.setPullLoadEnable(false);
        ls.stopRefresh();
        ls.stopLoadMore();

        ls.setRefreshTime(GeneralUtil.simpleDateFormat.format(new Date()));
    }

    private void freshTime(PullXListView ls) {
        ls.setPullLoadEnable(true);
        ls.stopRefresh();
        ls.stopLoadMore();

        ls.setRefreshTime(GeneralUtil.simpleDateFormat.format(new Date()));
    }

    // void clearData() {
    // this.data_ls.clear();
    // this.data_ls_app.clear();
    // this.data_ls_user.clear();
    // }

    @Override
    public void onRefresh() {
        if (action_map.containsKey(key_action)
                && action_map.containsKey(key_type)) {
            // clearData();

            int action_action = action_map.get(key_action);
            int action_sys = action_map.get(key_type);
            pageIndex = 1;
            switch (action_action) {
                case ACTION_APP: {
                    // data_ls_app.clear();
                }

                break;
                case ACTION_SYS: {
                    // data_ls.clear();

                }

                break;
                case ACTION_USER: {
                    // data_ls_user.clear();

                }

                break;

                default:
                    break;
            }
            getMsgList(action_action, action_sys);
            switch (action_action) {
                case ACTION_APP: {
                    freshTime(pullList_app);
                }

                break;
                case ACTION_SYS: {
                    freshTime(pullList);
                }

                break;
                case ACTION_USER: {
                    freshTime(pullList_user);

                }

                break;

                default:
                    break;
            }

        }

    }

    @Override
    public void onLoadMore() {

        if (action_map.containsKey(key_action)
                && action_map.containsKey(key_type)) {

            int action_action = action_map.get(key_action);
            int action_sys = action_map.get(key_type);

            if (pageIndex < page_total_page) {
                pageIndex++;
                getMsgList(action_action, action_sys);
                switch (action_action) {
                    case ACTION_APP: {
                        freshTime(pullList_app);
                    }

                    break;
                    case ACTION_SYS: {
                        freshTime(pullList);

                    }

                    break;
                    case ACTION_USER: {
                        freshTime(pullList_user);

                    }

                    break;

                    default:
                        break;
                }

            } else {
                switch (action_action) {
                    case ACTION_APP: {
                        freshTime(pullList_app);
                        loadOver(pullList_app);
                    }

                    break;
                    case ACTION_SYS: {
                        freshTime(pullList);

                        loadOver(pullList);
                    }

                    break;
                    case ACTION_USER: {
                        freshTime(pullList_user);
                        loadOver(pullList_user);
                    }

                    break;

                    default:
                        break;
                }

            }

        }
        // if (isUser) {
        // if (pageIndex < page_total_page) {
        // pageIndex++;
        // getMsgList(type_user, ACTION_USER);
        // }
        // }

    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ProDimiss();
            switch (msg.what) {

                case ACTION_USER: {

                    // showToast("用户消息");
                    parselMsg_User(vo_resp.getResp());
                    adapter_msg_user.notifyDataSetChanged();
                    loadPage(pullList_user);
                    getPage(vo_resp.getResp());

                }
                break;
                case ACTION_APP: {

                    // showToast("申请消息");
                    parselMsg_App(vo_resp.getResp());
                    adapter_msg_app.notifyDataSetChanged();
                    loadPage(pullList_app);
                    getPage(vo_resp.getResp());

                }
                break;
                case ACTION_SYS: {

                    // showToast("系统消息");
                    parselMsg_Sys(vo_resp.getResp());
                    if (null == adapter_msg) {

                        adapter_msg = new MessageAdapter(t_context);
                        adapter_msg.setCell_click(TabMessage.this);
                        adapter_msg.setData(data_ls);
                        adapter_msg.setLisview(pullList);
                        adapter_msg.setTYPE(MSG_TYPE.MSG_TYPE_SYS);
                        pullList.setAdapter(adapter_msg);

                        // adapter_msg = new MessageAdapter(t_context);
                        // adapter_msg.setData(data_ls);
                        // pullList.setAdapter(adapter_msg);
                    }
                    adapter_msg.notifyDataSetChanged();
                    getPage(vo_resp.getResp());
                    loadPage(pullList);

                }
                break;
                // case ACTION_TAG1: {
                //
                // NSLoger.Log(vo_resp.getResp());
                // parselJson(vo_resp.getResp());
                // if (null == adapter_msg) {
                //
                // adapter_msg = new MessageAdapter(t_context);
                // adapter_msg.setData(data_ls);
                // pullList.setAdapter(adapter_msg);
                // }
                //
                // adapter_msg.notifyDataSetChanged();
                //
                // }
                //
                // break;
                case ACTION_ERROR: {
                    showToast("访问出错！" + mg2String(msg));

                }

                break;
                case ACTION_EXCEPTON: {
                    showToast("服务异常！");

                }

                break;
                case -11: {
                    showToast("服务异常！");

                }

                break;
                case -12: {
                    showToast("服务异常！" + mg2String(msg));

                }

                break;
                case -13: {
                    showToast("操作成功！！");
                    TabMessage.this.onRefresh();

                }

                break;

                default:
                    break;
            }
        }

        ;
    };

    void parselMsg_Sys(String res) {
        try {
            if (res == null) {
                return;
            }
            if (pageIndex == 1) {
                data_ls.clear();
            }
            JSONArray arr = new JSONObject(res).getJSONObject("data")
                    .getJSONArray("items");
            if (arr.length() < 1) {
                showToast(" 暂时还没有数据！");
            }
            for (int i = 0; i < arr.length(); i++) {
                MsgVo mv = new MsgVo();
                // status:状态,int;1-未读;2-已读;
                // ---targetid:由类型决定的目标ID,int;(推荐对象、派对通知、活动创建有效)
                mv.setTarget_id(arr.getJSONObject(i).getInt("targetid"));
                mv.setContent(arr.getJSONObject(i).getString("content"));
                mv.setTime(arr.getJSONObject(i).getString("time"));
                mv.setUser_title(arr.getJSONObject(i).getString("targetname"));
                mv.setTargrt_name(arr.getJSONObject(i).getString("targetname"));
                mv.setType(arr.getJSONObject(i).getInt("type"));
                mv.setStatus(arr.getJSONObject(i).getInt("status"));
                data_ls.add(mv);

            }
        } catch (Exception e) {
            showToast("解析系统错误！");
            e.printStackTrace();
        }

    }

    void parselMsg_User(String res) {
        try {
            if (res == null) {
                return;
            }
            if (pageIndex == 1) {
                data_ls_user.clear();
            }
            JSONArray arr = new JSONObject(res).getJSONObject("data")
                    .getJSONArray("items");
            if (arr.length() < 1) {
                showToast(" 暂时还没有数据！");
            }
            // -opentome: 相册头像对我开放(上锁后生效,如果没对我开放,无法查看相册和头像),int;1-不开放;2-开放;
            for (int i = 0; i < arr.length(); i++) {
                MsgVo mv = new MsgVo();
                mv.setTarget_id(arr.getJSONObject(i).getInt("uid"));
                mv.setContent(arr.getJSONObject(i).getString("content"));
                mv.setTime(arr.getJSONObject(i).getString("time"));
                mv.setUser_title(arr.getJSONObject(i).getString("nickname"));
                mv.setPic_url(arr.getJSONObject(i).getString("header_url"));
                mv.setType(arr.getJSONObject(i).getInt("type"));
                mv.setStatus(arr.getJSONObject(i).getInt("status"));
                mv.setSex(arr.getJSONObject(i).getInt("sex"));
                mv.setOpen_to_me(arr.getJSONObject(i).getInt("opentome"));
                data_ls_user.add(mv);

            }
        } catch (Exception e) {
            showToast("解析用户错误！");
            e.printStackTrace();
        }

    }

    void parselMsg_App(String res) {
        try {
            if (res == null) {
                return;
            }
            if (pageIndex == 1) {
                data_ls_app.clear();
            }
            JSONArray arr = new JSONObject(res).getJSONObject("data")
                    .getJSONArray("items");
            if (arr.length() < 1) {
                showToast(" 暂时还没有数据！");
            }
            for (int i = 0; i < arr.length(); i++) {
                MsgVo mv = new MsgVo();

                mv.setContent("申请查看您的相册");
                mv.setUid(arr.getJSONObject(i).getInt("uid") + "");
                mv.setTime(arr.getJSONObject(i).getString("time"));
                mv.setUser_title(arr.getJSONObject(i).getString("nickname"));
                mv.setPic_url(arr.getJSONObject(i).getString("header_url"));
                mv.setIsagreed(arr.getJSONObject(i).optInt("agreed", 1));
                mv.setStatus(arr.getJSONObject(i).getInt("status"));
                mv.setOpen_to_me(arr.getJSONObject(i).getInt("opentome"));
                data_ls_app.add(mv);

                // -agreed:是否已同意;1-未同意;2-已同意;

            }
        } catch (Exception e) {
            showToast("解析申请错误！" + e.getMessage());
            e.printStackTrace();
        }

    }

    DiagPopView diagpopview = null;

    void popCall(String name) {
        if (diagpopview == null) {

            diagpopview = new DiagPopView(TabMessage.this, "同意" + name
                    + "查看你的相册？");
            diagpopview.setLeftMsg("确定");
            diagpopview.setOnHeaderItemClick(TabMessage.this);
        }
        diagpopview.showAsDropDown(findViewById(R.id.topNav));

    }

    void getMsgList(int type, int act) {
        action_map.put(key_type, type);
        action_map.put(key_action, act);
        final int action = act;
        ProShow("正在拉取数据...");
        req_map_param.put("uid", shareUserInfo().getUserid() + "");
        req_map_param.put("key", key_server);
        req_map_param.put(PAGE, pageIndex + "");

        String URL_TYPE = "";

        switch (type) {
            case type_user: {
                URL_TYPE = MURL_MSG_USER;
            }

            break;
            case type_sys: {
                URL_TYPE = MURL_MSG_SYS;
            }

            break;
            case type_apply: {
                URL_TYPE = MURL_MSG_APPLY;
            }

            break;

            default:
                break;
        }
        final String url = URL_TYPE;

        poolThread.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    vo_resp = sendReq(url, req_map_param);

                    if (null == vo_resp) {
                        handler.obtainMessage(ACTION_FAIL).sendToTarget();

                    }
                    if (vo_resp.isHasError()) {
                        handler.obtainMessage(ACTION_ERROR).sendToTarget();

                    } else {

                        handler.obtainMessage(action).sendToTarget();

                    }

                } catch (Exception e) {
                    handler.obtainMessage(ACTION_EXCEPTON).sendToTarget();
                }

            }
        });

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        try {
            // adapter_msg_app.loadPause();
            if (adapter_msg_user != null) {

                adapter_msg_user.loadPause();
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            // adapter_msg_app.loadPause();
            if (adapter_msg_user != null) {

                adapter_msg_user.loadResume();
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            // adapter_msg_app.loadResume();
            if (adapter_msg_user != null) {

                adapter_msg_user.loadResume();
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
