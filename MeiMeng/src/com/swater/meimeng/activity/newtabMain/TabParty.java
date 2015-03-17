package com.swater.meimeng.activity.newtabMain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;
import com.swater.meimeng.commbase.MeiMengApp;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.AdapterSearch;
import com.swater.meimeng.activity.adapterGeneral.AdapterSearch.AdapterSearch_Type;
import com.swater.meimeng.activity.adapterGeneral.AdapterSearch.Cell_Act_click;
import com.swater.meimeng.activity.adapterGeneral.vo.PartyVo;
import com.swater.meimeng.activity.user.UserGallery;
import com.swater.meimeng.activity.user.UserSetHome;
import com.swater.meimeng.commbase.TabBaseTemplate;
import com.swater.meimeng.fragment.party.PrivateActDetail;
import com.swater.meimeng.fragment.party.TopActivityDetail;
import com.swater.meimeng.mutils.constant.GeneralUtil;
import com.swater.meimeng.mutils.pullxlist.PullXListView;
import com.swater.meimeng.mutils.pullxlist.PullXListView.IXListViewListener;

public class TabParty extends TabBaseTemplate implements Cell_Act_click,
        IXListViewListener {

    PullXListView listview = null;
    List<PartyVo> data = new ArrayList<PartyVo>();
    List<PartyVo> data_privates = new ArrayList<PartyVo>();
    final String KEY_PARTY = "PARTY";
    final String KEY_PARTY_PRIVATE = "PARTY_PRIVATE";
    int type = 1;
    int type_top_activity = 2;
    int type_private_invite = 3;
    AdapterSearch adapter = null;
    AdapterSearch adapter_private = null;
    int pageIndex = 1;
    // int pageSize = 10;
    int page_current = 1;
    int page_size = 1;
    int page_total_count = 1;
    int page_total_page = 1;
    boolean istopData = true;
    Button btn_top, btn_private;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_doing);
        t_context = this;
        TempRun();
        getTopActivity();
    }

    @Override
    public void iniView() {
        bindMenuClick();

        type = type_top_activity;
        pageIndex = 1;
        showTitle("美盟活动");
        listview = (PullXListView) findViewById(R.id.data_ls);
        listview.setOnItemClickListener(cellclick);
        listview.setPullLoadEnable(true);
        listview.setXListViewListener(this);
        bindMenuClick();
        btn_private = (Button) findViewById(R.id.act_btn_private);
        btn_top = (Button) findViewById(R.id.act_btn_top);
        setClickEvent(findViewById(R.id.act_btn_private),
                findViewById(R.id.act_btn_top), findViewById(R.id.left_img_btn));
        // TODO Auto-generated method stub

    }

    OnItemClickListener cellclick = new OnItemClickListener() {


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Log.e("TabParty:", "listView被点击了");

            Intent intent = new Intent(t_context, PrivateActDetail.class);
            position = position - 1;
            if (position < data_privates.size()) {
                intent.putExtra("aid", data_privates.get(position).getAid()
                        + "");
                intent.putExtra("sub", data_privates.get(position).getSubject());
                TabParty.this.startActivity(intent);
            }

        }
    };

    @Override
    public void bindClick() {
        // TODO Auto-generated method stub

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
                    type = (Integer) msg.obj;
                    if (type != type_private_invite) {
                        parselJson(vo_resp.getResp());
                    } else {
                        parselPrivate(vo_resp.getResp());
                    }

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
    boolean isshow = true;

    void getTopActivity() {
        if (pageIndex < 2 && isshow) {

            ProShow("正在获取派对信息...");
        }

        poolThread.submit(new Runnable(){

            @Override
            public void run() {

                try {
                    req_map_param.put("key", key_server);
                    req_map_param.put("uid", shareUserInfo()
                            .getUserid() + "");
                    req_map_param.put("page", "" + pageIndex);
                    vo_resp = sendReq(MURL_PARTY, req_map_param);
                    if (null == vo_resp) {
                        handler.obtainMessage().sendToTarget();
                    }
                    if (vo_resp.isHasError()) {

                        handler.obtainMessage(Resp_action_fail,
                                vo_resp.getErrorDetail()).sendToTarget();
                    } else {
                        type = type_top_activity;

                        handler.obtainMessage(Resp_action_ok, type_top_activity)
                                .sendToTarget();
                    }

                } catch (Exception e) {
                    handler.obtainMessage(Resp_exception).sendToTarget();
                }
            }
        });
    }

    void getPrivatesInvites() {
        if (pageIndex < 2) {

            ProShow("正在获取邀请信息...");
        }

        poolThread.submit(new Runnable() {

            @Override
            public void run() {

                try {
                    req_map_param.put("key", key_server);
                    req_map_param.put("uid", shareUserInfo()
                            .getUserid() + "");
                    req_map_param.put("page", "" + pageIndex);
                    vo_resp = sendReq(MURL_Private_invite, req_map_param);
                    if (null == vo_resp) {
                        handler.obtainMessage().sendToTarget();
                    }
                    if (vo_resp.isHasError()) {

                        handler.obtainMessage(Resp_action_fail,
                                vo_resp.getErrorDetail()).sendToTarget();
                    } else {
                        type = type_private_invite;

                        handler.obtainMessage(Resp_action_ok,
                                type_private_invite).sendToTarget();
                    }

                } catch (Exception e) {
                    handler.obtainMessage(Resp_exception).sendToTarget();
                }

            }
        });
    }

    @Override
    protected Object parselJson(String res) {
        // {"result":1,"error":"","data":{"activities":[{"aid":5,"subject":"\u6d4b\u8bd5\u9ad8\u7aef\u6d3b\u52a85","show_pic":"http:\/\/112.124.18.97\/attachment\/albumpic\/55\/header\/20130731071708.jpg","status":"1"},{"aid":6,"subject":"\u6d4b\u8bd5\u9ad8\u7aef\u6d3b\u52a86","show_pic":"http:\/\/112.124.18.97\/attachment\/albumpic\/55\/header\/20130731071708.jpg","status":"1"},{"aid":8,"subject":"\u6d4b\u8bd5\u9ad8\u7aef\u6d3b\u52a88","show_pic":"http:\/\/112.124.18.97\/attachment\/albumpic\/55\/header\/20130731071708.jpg","status":"1"},{"aid":10,"subject":"\u6d4b\u8bd5\u9ad8\u7aef\u6d3b\u52a810","show_pic":"http:\/\/112.124.18.97\/attachment\/albumpic\/55\/header\/20130731071708.jpg","status":"1"},{"aid":11,"subject":"\u6d4b\u8bd5\u9ad8\u7aef\u6d3b\u52a811","show_pic":"http:\/\/112.124.18.97\/attachment\/albumpic\/55\/header\/20130731071708.jpg","status":"1"},{"aid":13,"subject":"\u6d4b\u8bd5\u9ad8\u7aef\u6d3b\u52a813","show_pic":"http:\/\/112.124.18.97\/attachment\/albumpic\/55\/header\/20130731071708.jpg","status":"1"},{"aid":2,"subject":"\u6d4b\u8bd5\u9ad8\u7aef\u6d3b\u52a82","show_pic":"http:\/\/112.124.18.97\/attachment\/albumpic\/55\/header\/20130731071708.jpg","status":"2"},{"aid":14,"subject":"\u6d4b\u8bd5\u9ad8\u7aef\u6d3b\u52a814","show_pic":"http:\/\/112.124.18.97\/attachment\/albumpic\/55\/header\/20130731071708.jpg","status":"2"}],"current_page":1,"total_page":3,"page_size":8,"total_count":"19"}}

        // activities:活动列表,JSON数组;
        // ----aid:活动ID,int;
        // ----subject:活动主题,string;
        // ----show_pic:列表图片地址,string;

        Log.e("----------TabParty:", res);

        try {
            List<PartyVo> ls_p = new ArrayList<PartyVo>();
            ls_p.clear();
            Log.e("------1--ls_p.size=",""+ls_p.size());
            JSONArray arr = new JSONObject(res).getJSONObject("data")
                    .getJSONArray("activities");
            for (int i = 0; i < arr.length(); i++) {
                PartyVo pv = new PartyVo();
                pv.setShow_pic(arr.getJSONObject(i).getString("show_pic"));
                pv.setSubject(arr.getJSONObject(i).getString("subject"));
                pv.setAid(arr.getJSONObject(i).getInt("aid"));
                pv.setStatus(arr.getJSONObject(i).getInt("status"));

                //得到活动时间
                String time = arr.getJSONObject(i).getString("time");
                pv.setTime(time);

//                long lTime = getSecond(time);
//                pv.setlTime(lTime);
//                Log.e("----------2---ls_p.size=",""+ls_p.size());
//                int jude = ls_p.size();
//                if (jude<1){
//                    ls_p.add(pv);
//                    jude++;
//                    Log.e("TabParty:","ls_p中加了一个数据"+ls_p.size()+";  jude="+jude);
//                }else {
//                    for (int k = 0;k<jude;k++){
//                        if (ls_p.get(k).getlTime()<lTime){
//                            Log.e("TabParty:","k="+k+";ls_p.size="+ls_p.size());
//                            ls_p.add(k,pv);
//                        }else {
//                            if (k == ls_p.size()-1){
//                                ls_p.add(pv);
//                            }
//                        }
//                    }
//                }

                ls_p.add(pv);

            }
            data.addAll(ls_p);

            setOrder(data);

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
            showToast("解析异常！！");
        } finally {
            getPage(res);
            setListAdapter();

        }

        return super.parselJson(res);
    }

    private void setOrder(List<PartyVo> ls_p){
        /**
         * 直接插入排序法
         */
        int total = 1;
        for (int i = 1; i < ls_p.size(); i++) {
            PartyVo party = ls_p.get(i);
            String t1 = party.getTime();
            long a = getSecond(t1);
//                int a = numList.get(i);
            for (int j = total - 1; j >= 0; j--) {
                PartyVo party2 = ls_p.get(j);
                String t2 = party2.getTime();
                long b = getSecond(t2);
                if (a > b) {
                    ls_p.set(j,party);
                    ls_p.set(j+1,party2);
//                        numList.set(j, a);
//                        numList.set(j + 1, b);
                } else {
                    break;
                }
            }
            total = total + 1;
        }
    }
    private long getSecond(String time) {

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dt2 = sdf.parse(time);
            //继续转换得到秒数的long型
            long lTime = dt2.getTime() / 1000;
            return lTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    void getPage(String res) {
        try {
            JSONObject obj = new JSONObject(res).getJSONObject("data");
            page_current = obj.getInt(PAGE_KEY_current_page);
            page_size = obj.getInt(PAGE_KEY_current_page);
            page_total_count = obj.getInt(PAGE_KEY_current_page);
            page_current = obj.getInt(PAGE_KEY_current_page);
            page_total_page = obj.getInt(PAGE_KEY_total_page);

        } catch (Exception e) {
            // TODO: handle exception
            showToast("解析数据异常！");
            e.printStackTrace();
        }

    }


    void parselPrivate(String res) {

        Log.e("私人定制：",res);
        try {
            List<PartyVo> ls_p = new ArrayList<PartyVo>();
            JSONArray arr = new JSONObject(res).getJSONObject("data")
                    .getJSONArray("activities");
            for (int i = 0; i < arr.length(); i++) {
                PartyVo pv = new PartyVo();
                pv.setShow_pic(arr.getJSONObject(i).getString("show_pic"));
                pv.setSubject(arr.getJSONObject(i).getString("subject"));
                pv.setDesc(arr.getJSONObject(i).getString("description"));
                pv.setPlace(arr.getJSONObject(i).getString("place"));
                pv.setStatus(arr.getJSONObject(i).getInt("status"));
                pv.setAid(arr.getJSONObject(i).getInt("aid"));

                //获得时间
                pv.setTime(arr.getJSONObject(i).getString("time"));

                ls_p.add(pv);

            }
            data_privates.addAll(ls_p);

            setOrder(data_privates);

        } catch (Exception e) {
            e.printStackTrace();

            showToast("解析异常！！");
        } finally {
            getPage(res);
            setPrivateAdapter();

        }

    }

    void setListAdapter() {
        Log.e("TabParty", "到这儿了setListAdapter");
        if (null == adapter) {
            adapter = new AdapterSearch(t_context);
            adapter.setType_search(AdapterSearch_Type.type_party);
            adapter.setData(data);
            adapter.setCell_lay_click(this);
            listview.setOnItemClickListener(cellclick);

            Log.e("TabParty", "到这儿了setListAdapter-setOnItemClickListener");

            listview.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
        loadPage();

    }

    void CachesetListAdapter() {
//		if (null == adapter) {
        adapter = new AdapterSearch(t_context);
        adapter.setType_search(AdapterSearch_Type.type_party);
        adapter.setData(data);
        adapter.setCell_lay_click(this);
        listview.setOnItemClickListener(cellclick);

        listview.setAdapter(adapter);
//		}
        adapter.notifyDataSetChanged();
        loadPage();

    }

    private void loadPage() {
        listview.stopRefresh();
        listview.stopLoadMore();

        listview.setRefreshTime(GeneralUtil.simpleDateFormat.format(new Date()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if (handler != null) {
                handler = null;
            }
            closePool();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    void setPrivateAdapter() {
        if (null == adapter_private) {
            adapter_private = new AdapterSearch(t_context);
            adapter_private
                    .setType_search(AdapterSearch_Type.type_private_invite);
            adapter_private.setData(data_privates);

            listview.setAdapter(adapter_private);
        }
        adapter_private.notifyDataSetChanged();
        loadPage();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.left_img_btn:
            case R.id.home_left_btn: {
                this.LeftClickPop(findViewById(R.id.topNav));
            }

            break;
            case R.id.act_btn_private: {
                adapter_private = null;
                data_privates.clear();
                pageIndex = 1;
                isshow = true;
                istopData = false;
                btn_private.setBackgroundResource(R.drawable.right_p);
                btn_top.setBackgroundResource(R.drawable.title_left);
                btn_top.setTextColor(this.getResources().getColor(R.color.blank));
                btn_private.setTextColor(this.getResources()
                        .getColor(R.color.white));
                getPrivatesInvites();
            }

            break;
            case R.id.act_btn_top: {
                btn_private.setBackgroundResource(R.drawable.title_right);
                btn_top.setBackgroundResource(R.drawable.left_p);
                btn_top.setTextColor(this.getResources().getColor(R.color.white));
                btn_private.setTextColor(this.getResources()
                        .getColor(R.color.blank));
                adapter = null;
                data.clear();
                pageIndex = 1;
                isshow = true;
                istopData = true;
                getTopActivity();
            }

            break;

            default:
                break;
        }

    }

    @Override
    public void onHeader_ItemClick(View v) {
        judgeTypeMyfocus(v, TabParty.this);
        switch (v.getId()) {
            case R.id.gallery_imv:
            case R.id.lay_person: {
                if (MeiMengApp.isVisitor){
                    MeiMengApp.setVisitorDialogView(TabParty.this,1);
                }else {
                    jumpOtherActivity(UserSetHome.class);
                }
            }
//		case R.id.gallery_imv: {
//			jumpOtherActivity(UserGallery.class);
//
//		}
//			break;

            default:
                break;
        }

    }

    /**
     * 高端派对的listView的点击事件
     *
     * @param v
     * @param pos
     */
    @Override
    public void Cell_lay_Click(View v, int pos) {
        // showToast("act-click--");

        Log.e("TabParty:", "高端派对的listView的点击事件");
        Intent intent = new Intent(t_context, TopActivityDetail.class);
        intent.putExtra("aid", data.get(pos).getAid() + "");
        intent.putExtra("sub", data.get(pos).getSubject() + "");
        TabParty.this.startActivity(intent);
    }

    @Override
    public void onRefresh() {
        pageIndex = 1;
        isshow = false;
        if (istopData) {
            data.clear();
            // data.clear();
            getTopActivity();

        } else {
            data_privates.clear();
            // data_privates.clear();
            getPrivatesInvites();
        }

    }

    @Override
    public void onLoadMore() {
        if (page_current < page_total_page) {
            listview.setPullLoadEnable(true);
            pageIndex++;
            if (istopData) {
                getTopActivity();

            } else {
                getPrivatesInvites();
            }
        } else {
            listview.setPullLoadEnable(false);

        }

    }


}
