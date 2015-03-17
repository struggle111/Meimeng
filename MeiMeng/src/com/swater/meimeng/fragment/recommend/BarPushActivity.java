package com.swater.meimeng.fragment.recommend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.User_Ada_Type;
import com.swater.meimeng.activity.adapterGeneral.UserAdapterItem;
import com.swater.meimeng.activity.adapterGeneral.vo.AnSwerVo;
import com.swater.meimeng.activity.adapterGeneral.vo.HobbyVo;
import com.swater.meimeng.activity.adapterGeneral.vo.UserInfo;
import com.swater.meimeng.activity.adapterGeneral.vo.UserSearchVo;
import com.swater.meimeng.activity.adapterGeneral.vo.VoPhoto;
import com.swater.meimeng.commbase.DiagPopView;
import com.swater.meimeng.commbase.DiagPopView.Diag_ok_no;
import com.swater.meimeng.commbase.MeiMengApp;
import com.swater.meimeng.commbase.RespConstant;
import com.swater.meimeng.database.ShareUserConstant;
import com.swater.meimeng.database.ShareUtil;
import com.swater.meimeng.database.XmlDataGreeting;
import com.swater.meimeng.database.XmlMedalData;
import com.swater.meimeng.fragment.NewViewPagerAdapter;
import com.swater.meimeng.fragment.recommend.myviews.pushscrollview;
import com.swater.meimeng.fragment.recommend.myviews.pushscrollview.onTapScroll;
import com.swater.meimeng.mutils.MedalUtil;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.constant.GeneralUtil;
import com.swater.meimeng.mutils.constant.MConstantUser;
import com.swater.meimeng.mutils.constant.MConstantUser.UserAll_life_info;
import com.swater.meimeng.mutils.constant.MConstantUser.UserAll_work_info;
import com.swater.meimeng.mutils.diagutil.Tools;
import com.swater.meimeng.mutils.mygrid.MyListView;
import com.swater.meimeng.mutils.net.MUrlPostAddr;
import com.swater.meimeng.mutils.net.RequestByPost;
import com.swater.meimeng.mutils.net.RespVo;
import com.swater.meimeng.mutils.sound.sundButton.AACPlayer;
import com.swater.meimeng.mutils.wheelview.WheelView;

public class BarPushActivity extends Activity implements OnClickListener,
        RespConstant, MUrlPostAddr, OnPageChangeListener, ShareUserConstant,
        Diag_ok_no, onTapScroll {
    UserSearchVo vo = null;
    pushscrollview pu_scroll;
    ViewPager svp = null;
    List<VoPhoto> photos = new ArrayList<VoPhoto>();
    Context t_context = null;
    ShareUtil sh = null;
    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    ;
    // ------
    ImageView header_user = null;
    RadioButton rb_wantsee = null, rb_wantsee_out = null;
    Map<String, String> cache_maps = new HashMap<String, String>();
    TextView user_age = null;
    TextView user_area = null;
    TextView txtpro = null;
    ImageView imv_focus = null;
    LinearLayout layLock = null;
    // --
    ImageView imv_voice = null, imv_clear = null;
    boolean isplaying = false;
    AACPlayer player_instance = null;
    int count_time = 1;
    // ------constant--
    final static int DETAIL_INFO = 15412;
    final static int DETAIL_FAIL = 6410;
    // --medal--info

    ImageView medal_1 = null, medal_2 = null, medal_3 = null, medal_4 = null,
            medal_5 = null;
    ImageView medal_renzheng, medal_vip = null;
    LinearLayout medal_lay, medal_lay_renzheng;
    XmlMedalData medal_data = XmlMedalData.getSingle();

    // ---------half-bottom---reference----
    com.swater.meimeng.mutils.mygrid.MyListView ls_hobby = null;
    UserInfo userinfo_vo = null;
    UserAdapter adapter_personlity = null;
    UserAdapter adapter_hobby = null;
    List<HobbyVo> arr_hobby = new ArrayList<HobbyVo>();
    List<AnSwerVo> arr_sueveys = new ArrayList<AnSwerVo>();
    MyListView ls_personlity = null;// 个性描述列表

    MyListView set_ls_baseinfo = null;
    MyListView set_ls_detail_info = null;
    MyListView set_ls_appear = null;
    MyListView set_ls_workinfo = null;
    MyListView set_ls_lifeinfo = null;
    UserAdapter adapter_baseinfo = null;
    UserAdapter adapter_detail_info = null;
    UserAdapter adapter_appearace = null;
    UserAdapter adapter_work = null;
    UserAdapter adapter_life = null;
    List<UserAdapterItem> data_baseinfo = new ArrayList<UserAdapterItem>();
    List<UserAdapterItem> data_detail_info = new ArrayList<UserAdapterItem>();
    List<UserAdapterItem> data_appearance_info = new ArrayList<UserAdapterItem>();
    List<UserAdapterItem> data_work_info = new ArrayList<UserAdapterItem>();
    List<UserAdapterItem> data_life_info = new ArrayList<UserAdapterItem>();

    // ---end-half---

    //一开始看到的简短的信息的布局
    private RelativeLayout messageLayout;
    private ViewGroup.MarginLayoutParams messageParams;

    //详细资料等三个按钮的布局
    private RelativeLayout threeButtonLayout;
    private ViewGroup.MarginLayoutParams threeButtonParams;

    //顶部的布局
    private LinearLayout topLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.push_bar_detail);

        messageLayout = (RelativeLayout) findViewById(R.id.lay_one);

        //详细资料等三个按钮的布局
        threeButtonLayout = (RelativeLayout) findViewById(R.id.c_b);

        topLayout = (LinearLayout) findViewById(R.id.top);

        pu_scroll = (pushscrollview) findViewById(R.id.container_scrol);
        pu_scroll.setTapscroll_listener(this);
        pu_scroll.setFloatView(findViewById(R.id.c_b),
                findViewById(R.id.viewOutScroll));
        pu_scroll.setOnScrollListener(new pushscrollview.OnScrollListener() {

            @Override
            public void onScrollChanged(pushscrollview scrollView, int x,
                                        int y, int oldx, int oldy) {
                Log.i("tag", "y=" + y + ";oldy=" + oldy);
            }
        });
        HalfIniView();
        if (userinfo_vo == null) {
            userinfo_vo = new UserInfo();
        }
        t_context = BarPushActivity.this;
        sh = ShareUtil.getInstance(BarPushActivity.this);

        imageLoader.resume();
        initialView();
        setClick();
    }

    BottomAdapter btadAdapter = null;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        Log.e("屏幕的宽高：", "width=" + width + ";height = " + height);

        int messageWidth = messageLayout.getWidth();
        int messageHeight = messageLayout.getHeight();

        Log.e("简短信息的宽高：", "messageWidth=" + messageWidth + ";messageHeight = " + messageHeight);

        int threeButtonWidth = threeButtonLayout.getWidth();
        int threeButtonHeight = threeButtonLayout.getHeight();

        Log.e("三个按钮布局的宽高：", "threeButtonWidth=" + threeButtonWidth + ";threeButtonHeight = " + threeButtonHeight);

        int topHeight = topLayout.getHeight();
        Log.e("顶部局部的宽度：", "topHeight = " + topHeight);

        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.e("标题栏的高度：", "top=" + statusBarHeight);

        //重新设置简短信息的上边
        messageParams = (ViewGroup.MarginLayoutParams) messageLayout.getLayoutParams();
        messageParams.topMargin = height - statusBarHeight - topHeight - messageHeight - threeButtonHeight;
        messageLayout.setLayoutParams(messageParams);
        messageLayout.setVisibility(View.VISIBLE);

        //重新设置三个按钮布局的上边距
        threeButtonParams = (ViewGroup.MarginLayoutParams) threeButtonLayout.getLayoutParams();
        threeButtonParams.topMargin = height - statusBarHeight - topHeight - threeButtonHeight;
        threeButtonLayout.setLayoutParams(threeButtonParams);

    }

    void HalfIniView() {

        ls_personlity = (MyListView) findViewById(R.id.ls_person);

        adapter_personlity = new UserAdapter(t_context);
        adapter_personlity.setType(User_Ada_Type.type_personlities);
        // ----
        ls_hobby = (com.swater.meimeng.mutils.mygrid.MyListView) findViewById(R.id.ls_hobby);
        adapter_hobby = new UserAdapter(t_context);
        adapter_hobby.setType(User_Ada_Type.type_hobby_cell);
        // -----**--
        // for (int i = 0; i < 4; i++) {
        // HobbyVo hv=new HobbyVo();
        // hv.setLeftName("aae");
        // hv.setData("88888");
        // arr_hobby.add(hv);
        //
        // }
        // -----**--
        adapter_hobby.setObjs(arr_hobby);
        adapter_hobby.setClikAble(false);
        adapter_personlity.setClikAble(false);

        btadAdapter = new BottomAdapter(BarPushActivity.this);

        ls_hobby.setAdapter(btadAdapter);
        // -------------
        set_ls_baseinfo = (MyListView) findViewById(R.id.set_ls_baseinfo);
        set_ls_lifeinfo = (MyListView) findViewById(R.id.set_ls_life_info);
        set_ls_detail_info = (MyListView) findViewById(R.id.set_ls_detailinfo);
        set_ls_appear = (MyListView) findViewById(R.id.set_ls_appearance_info);
        set_ls_workinfo = (MyListView) findViewById(R.id.set_ls_work_info);

        // ----
        ls_personlity.setFocusableInTouchMode(false);
        ls_hobby.setFocusableInTouchMode(false);
        set_ls_baseinfo.setFocusableInTouchMode(false);
        set_ls_lifeinfo.setFocusableInTouchMode(false);
        set_ls_workinfo.setFocusableInTouchMode(false);
        set_ls_detail_info.setFocusableInTouchMode(false);

        ls_personlity.setFocusable(false);
        ls_hobby.setFocusable(false);
        set_ls_baseinfo.setFocusable(false);
        set_ls_lifeinfo.setFocusable(false);
        set_ls_workinfo.setFocusable(false);
        set_ls_detail_info.setFocusable(false);

    }

    // List<UserAdapterItem> arr_personlity = new ArrayList<UserAdapterItem>();
    //
    // void Inipersonkinds() {
    // for (int i = 0; i < 7; i++) {
    // UserAdapterItem cell = new UserAdapterItem();
    // switch (i) {
    // case 0: {
    // cell.setLeftStr("个性描述");
    //
    // }
    //
    // break;
    // case 1: {
    // cell.setLeftStr("生活习惯");
    //
    // }
    //
    // break;
    // case 2: {
    // cell.setLeftStr("爱情观点");
    //
    // }
    //
    // break;
    // case 3: {
    // cell.setLeftStr("约会类型");
    //
    // }
    //
    // break;
    // case 4: {
    // cell.setLeftStr("婚姻期望");
    //
    // }
    //
    // break;
    // case 5: {
    //
    // cell.setLeftStr("婚后生活");
    // }
    //
    // break;
    // case 6: {
    // cell.setLeftStr("理想对象");
    //
    // }
    //
    // break;
    //
    // default:
    // break;
    // }
    // arr_personlity.add(cell);
    //
    // }
    // // adapter_personlity.setObjs(arr_sueveys);
    // ls_personlity.setAdapter(adapter_personlity);
    // adapter_personlity.notifyDataSetChanged();
    //
    // };

    /**
     * 给view赋值
     */
    private void setViewUser() {

        setValueToView(findViewById(R.id.txt_heart_self),
                userinfo_vo.getHeart_description());
        iniAppearlView();
        iniWorkView();
        iniLifeView();
    }

    void clearData() {
        data_appearance_info.clear();
        data_baseinfo.clear();
        data_detail_info.clear();
        data_life_info.clear();
        data_work_info.clear();

    }

    void iniBaseView() {
        adapter_baseinfo = new UserAdapter(t_context);
        adapter_baseinfo.setType(User_Ada_Type.type_new_user_cell);
        for (int i = 0; i < 8; i++) {
            UserAdapterItem cell = new UserAdapterItem();
            switch (i) {
                case 0: {
                    cell.setLeftStr("用户ID");
                    cell.setRightStr(vo.getUid() + "");
                }

                break;
                case 1: {
                    cell.setLeftStr("昵称");
                    cell.setRightStr(userinfo_vo.getNickName());

                }

                break;
                case 2: {
                    cell.setLeftStr("年龄");
                    cell.setRightStr(userinfo_vo.getAge());

                }

                break;
                case 3: {
                    cell.setLeftStr("所在城市");
                    cell.setRightStr(userinfo_vo.getCity_name());
                }

                break;
                case 4: {
                    cell.setLeftStr("学历");
                    cell.setRightStr(userinfo_vo.getDegree());
                }

                break;
                case 5: {
                    cell.setLeftStr("身高");
                    cell.setRightStr(userinfo_vo.getHeight() + "cm");
                }

                break;
                case 6: {
                    cell.setLeftStr("婚姻状况");
                    cell.setRightStr(userinfo_vo.getMarriage());
                }

                break;
                case 7: {
                    cell.setLeftStr("有无子女");
                    cell.setRightStr(userinfo_vo.getChild());

                }

                break;

                default:
                    break;
            }
            data_baseinfo.add(cell);
        }
        adapter_baseinfo.setObjs(data_baseinfo);
        if (set_ls_baseinfo != null) {
            if (adapter_baseinfo != null) {

                set_ls_baseinfo.setAdapter(adapter_baseinfo);
            }
        }

    }

    void iniDetailView() {
        adapter_detail_info = new UserAdapter(t_context);
        adapter_detail_info.setType(User_Ada_Type.type_new_user_cell);
        adapter_detail_info.setObjs(data_detail_info);

        if (vo.getSex().equals("2")) {
            for (int i = 0; i < 11; i++) {
                UserAdapterItem cell = new UserAdapterItem();
                switch (i) {
                    case 0: {
                        cell.setLeftStr("国籍");
                        cell.setRightStr(userinfo_vo.getNationality());
                    }

                    break;
                    case 1: {
                        cell.setLeftStr("海外经历");
                        cell.setRightStr(userinfo_vo.getOverseas_experience());

                    }

                    break;
                    case 2: {
                        cell.setLeftStr("民族");
                        cell.setRightStr(userinfo_vo.getMingzu());

                    }

                    break;
                    case 3: {
                        cell.setLeftStr("职业");
                        cell.setRightStr(userinfo_vo.getProfession());
                    }

                    break;
                    case 4: {
                        cell.setLeftStr("生肖");
                        cell.setRightStr(userinfo_vo.getShengxiao());
                    }

                    break;
                    case 5: {
                        cell.setLeftStr("家庭背景");
                        cell.setRightStr(userinfo_vo.getFamily());
                    }

                    break;
                    case 6: {
                        cell.setLeftStr("星座");
                        cell.setRightStr(userinfo_vo.getXingzuo());
                    }

                    break;
                    case 7: {
                        cell.setLeftStr("居住状况");
                        cell.setRightStr(userinfo_vo.getLive());

                    }

                    break;
                    case 8: {
                        cell.setLeftStr("血型");
                        cell.setRightStr(userinfo_vo.getBlood());

                    }

                    break;
                    case 9: {
                        cell.setLeftStr("购车状况");
                        cell.setRightStr(userinfo_vo.getCar());

                    }

                    break;
                    case 10: {
                        cell.setLeftStr("月薪");
                        cell.setRightStr(userinfo_vo.getSalary());

                    }

                    break;

                    default:
                        break;
                }
                data_detail_info.add(cell);
            }
        } else {
            for (int i = 0; i < 11; i++) {
                UserAdapterItem cell = new UserAdapterItem();
                switch (i) {
                    case 0: {
                        cell.setLeftStr("国籍");
                        cell.setRightStr(userinfo_vo.getNationality());
                    }

                    break;
                    case 1: {
                        cell.setLeftStr("海外经历");
                        cell.setRightStr(userinfo_vo.getOverseas_experience());

                    }

                    break;
                    case 2: {
                        cell.setLeftStr("民族");
                        cell.setRightStr(userinfo_vo.getMingzu());

                    }

                    break;
                    case 3: {
                        cell.setLeftStr("职业");
                        cell.setRightStr(userinfo_vo.getProfession());
                    }

                    break;
                    case 4: {
                        cell.setLeftStr("生肖");
                        cell.setRightStr(userinfo_vo.getShengxiao());
                    }

                    break;
                    case 5: {
                        cell.setLeftStr("家庭背景");
                        cell.setRightStr(userinfo_vo.getFamily());
                    }

                    break;
                    case 6: {
                        cell.setLeftStr("星座");
                        cell.setRightStr(userinfo_vo.getXingzuo());
                    }

                    break;
                    case 7: {
                        cell.setLeftStr("居住状况");
                        cell.setRightStr(userinfo_vo.getLive());

                    }

                    break;
                    case 8: {
                        cell.setLeftStr("血型");
                        cell.setRightStr(userinfo_vo.getBlood());

                    }

                    break;
                    case 9: {
                        cell.setLeftStr("购车状况");
                        cell.setRightStr(userinfo_vo.getCar());

                    }

                    break;

                    case 10: {
                        cell.setLeftStr("资产");
                        cell.setRightStr(userinfo_vo.getAssets_level());

                    }

                    break;

                    default:
                        break;
                }
                data_detail_info.add(cell);
            }
        }

        set_ls_detail_info.setAdapter(adapter_detail_info);
        adapter_detail_info.notifyDataSetChanged();
    }

    void iniLifeView() {
        adapter_life = new UserAdapter(t_context);
        adapter_life.setType(User_Ada_Type.type_new_user_cell);
        adapter_life.setObjs(data_life_info);
        for (int i = 0; i < 12; i++) {
            UserAdapterItem cell = new UserAdapterItem();
            switch (i) {
                case 0: {
                    cell.setLeftStr("是否吸烟");
                    cell.setRightStr(userinfo_vo.getVa5_issmoke());
                }

                break;
                case 1: {
                    cell.setLeftStr("是否饮酒");
                    cell.setRightStr(userinfo_vo.getVa5_drink());

                }

                break;
                case 2: {
                    cell.setLeftStr("锻炼习惯");
                    cell.setRightStr(userinfo_vo.getVa5_execise());

                }

                break;
                case 3: {
                    cell.setLeftStr("作息习惯");
                    cell.setRightStr(userinfo_vo.getVa5_rest());
                }

                break;
                case 4: {
                    cell.setLeftStr("宗教信仰");
                    cell.setRightStr(userinfo_vo.getVa5_zongjiao());
                }

                break;
                case 5: {
                    cell.setLeftStr("家中排行");
                    cell.setRightStr(userinfo_vo.getVa5_rank());
                }

                break;
                case 6: {
                    cell.setLeftStr("愿意与TA父母同住");
                    cell.setRightStr(userinfo_vo.getVa5_livewith());
                }

                break;
                case 7: {
                    cell.setLeftStr("是否要孩子");
                    cell.setRightStr(userinfo_vo.getVa5_iswantchild());

                }

                break;
                case 8: {
                    cell.setLeftStr("制造浪漫");
                    cell.setRightStr(userinfo_vo.getVa5_makeroma());

                }

                break;
                case 9: {
                    cell.setLeftStr("最大消费");
                    cell.setRightStr(userinfo_vo.getVa5_big_lar());

                }

                break;
                case 10: {
                    cell.setLeftStr("宠物类型");
                    cell.setRightStr(userinfo_vo.getVa5_pets());

                }

                break;
                case 11: {
                    cell.setLeftStr("擅长生活技能");
                    cell.setRightStr(userinfo_vo.getVa5_skills());

                }

                break;

                default:
                    break;
            }
            data_life_info.add(cell);
        }
        set_ls_lifeinfo.setAdapter(adapter_life);
        adapter_life.notifyDataSetChanged();
    }

    void iniWorkView() {
        adapter_work = new UserAdapter(t_context);
        adapter_work.setType(User_Ada_Type.type_new_user_cell);
        adapter_work.setObjs(data_work_info);
        for (int i = 0; i < 7; i++) {
            UserAdapterItem cell = new UserAdapterItem();
            switch (i) {
                case 0: {
                    cell.setLeftStr("公司行业");
                    cell.setRightStr(userinfo_vo.getVa4_comp_type());
                }

                break;
                case 1: {
                    cell.setLeftStr("公司类型");
                    cell.setRightStr(userinfo_vo.getVa4_comp_trade());

                }

                break;
                case 2: {
                    cell.setLeftStr("工作状态");
                    cell.setRightStr(userinfo_vo.getVa4_job_state());

                }

                break;
                case 3: {
                    cell.setLeftStr("收入描述");
                    cell.setRightStr(userinfo_vo.getVa4_income_desc());
                }

                break;
                case 4: {
                    cell.setLeftStr("毕业年份");
                    cell.setRightStr(userinfo_vo.getVa4_grad_year());
                }

                break;
                case 5: {
                    cell.setLeftStr("专业类型");
                    cell.setRightStr(userinfo_vo.getVa4_prof_type());
                }

                break;
                case 6: {
                    cell.setLeftStr("语言能力");
                    cell.setRightStr(userinfo_vo.getVa4_lang());
                }

                break;

                default:
                    break;
            }
            data_work_info.add(cell);
        }
        set_ls_workinfo.setAdapter(adapter_work);
        adapter_work.notifyDataSetChanged();
    }

    void iniAppearlView() {
        adapter_appearace = new UserAdapter(t_context);
        adapter_appearace.setType(User_Ada_Type.type_new_user_cell);
        adapter_appearace.setObjs(data_appearance_info);
        for (int i = 0; i < 8; i++) {
            UserAdapterItem cell = new UserAdapterItem();
            switch (i) {
                case 0: {
                    cell.setLeftStr("体重");
                    cell.setRightStr(userinfo_vo.getVa3_weight());
                }

                break;
                case 1: {
                    cell.setLeftStr("发型");
                    cell.setRightStr(userinfo_vo.getVa3_hair_style());

                }

                break;
                case 2: {
                    cell.setLeftStr("脸型");
                    cell.setRightStr(userinfo_vo.getVa3_face_style());

                }

                break;
                case 3: {
                    cell.setLeftStr("发色");
                    cell.setRightStr(userinfo_vo.getVa3_hair_color());
                }

                break;
                case 4: {
                    cell.setLeftStr("体型");
                    cell.setRightStr(userinfo_vo.getVa3_body_style());
                }

                break;
                case 5: {
                    cell.setLeftStr("眼睛颜色");
                    cell.setRightStr(userinfo_vo.getVa3_eye_color());
                }

                break;
                case 6: {
                    cell.setLeftStr("魅力部位");
                    cell.setRightStr(userinfo_vo.getVa3_attract_place());
                }

                break;
                case 7: {
                    cell.setLeftStr("相貌自评");
                    cell.setRightStr(userinfo_vo.getVa3_self_assert());

                }

                break;

                default:
                    break;
            }
            data_appearance_info.add(cell);
        }
        set_ls_appear.setAdapter(adapter_appearace);
        adapter_appearace.notifyDataSetChanged();
    }

    void setClick() {
        setClickEvent(findViewById(R.id.bottom_btn1),
                findViewById(R.id.bottom_btn2), layLock,
                findViewById(R.id.bottom_btn3), imv_focus);
        setClickEvent(findViewById(R.id.out_bottom_btn1),
                findViewById(R.id.out_bottom_btn2),
                findViewById(R.id.out_bottom_btn3));

    }

    protected void setClickEvent(View... views) {
        for (View param : views) {
            if (param != null) {

                param.setOnClickListener(BarPushActivity.this);
            } else {
                Log.d("--setClickEvent==", "--setClickEvent--view-null--");
            }
        }

    }

    void getMedalInfo() {

        medal_lay_renzheng.removeAllViewsInLayout();
        medal_lay.removeAllViewsInLayout();
        // audit_type:认证类型,int;1-在线审核;2-面谈审核;
        // vip_level:vip等级,int;0-未付费;1-银牌；2-金牌;3-黑牌;
        // medal:勋章ID拼接的字符串，string，如”1,2,3”;
        MedalView vip_medal = null;
        MedalView audit_medal = null;
        // --女性用户认证
        if (vo.getAudi_type() == 1 && vo.getSex().equals("2")) {
            audit_medal = new MedalView(t_context, R.drawable.check_online);
            audit_medal.setMedalName("在线审核");
            medal_lay_renzheng.addView(audit_medal);

        } else if (vo.getAudi_type() == 2 && vo.getSex().equals("2")) {
            audit_medal = new MedalView(t_context, R.drawable.check_face);
            audit_medal.setMedalName("面审勋章");
            medal_lay_renzheng.addView(audit_medal);

        } else {
            medal_renzheng.setVisibility(View.GONE);
        }
        // ----男性用户认证---

        if (vo.getSex().equals("1")) {
            switch (vo.getVip_level()) {
                case 1:
                    vip_medal = new MedalView(t_context, R.drawable.vip_silver);
                    vip_medal.setMedalName("银牌会员");
                    medal_lay_renzheng.addView(vip_medal);
                    break;
                case 2:
                    vip_medal = new MedalView(t_context, R.drawable.vip_golden);
                    vip_medal.setMedalName("金牌会员");
                    medal_lay_renzheng.addView(vip_medal);
                    break;
                case 3:
                    vip_medal = new MedalView(t_context, R.drawable.vip_black);
                    vip_medal.setMedalName("黑金会员");
                    medal_lay_renzheng.addView(vip_medal);
                    break;

                default:
                    break;
            }

        }

        // medal_lay.addView(new MedalView(t_context));
        // medal_lay.addView(new MedalView(t_context));
        // ------------------
        if (!TextUtils.isEmpty(vo.getMedals())
                && !vo.getMedals().equals("null")) {
            iniMedalData();
            if (vo.getMedals().contains(",")) {

                String[] medal_arr = vo.getMedals().split(",");
                if (medal_arr != null && medal_arr.length > 0) {

                    for (int i = 0; i < medal_arr.length; i++) {
                        String medal_Name = "a" + medal_arr[i];
                        int medal_id = MedalUtil.getMedalIdByName(t_context,
                                medal_Name);
                        MedalView medal_cell = new MedalView(t_context,
                                medal_id);
                        String medalName = medal_data
                                .getMedalNameById(medal_arr[i]);
                        medal_cell.setMedalName(medalName);
                        medal_lay.addView(medal_cell);

                    }
                }
            } else {
                String medal_Name = "a" + vo.getMedals();
                int medal_id = MedalUtil
                        .getMedalIdByName(t_context, medal_Name);
                MedalView medal_cell = new MedalView(t_context, medal_id);
                String medalName = medal_data.getMedalNameById(vo.getMedals());
                medal_cell.setMedalName(medalName);
                medal_lay.addView(medal_cell);
                // medal_1.setImageResource(medal_id);
                // view_Show(medal_1);

            }

        }

    }

    void halfParsel(String res) {
        try {
            clearData();

            JSONObject obj = new JSONObject(res).getJSONObject("data");

            int sh_count = obj.getInt("shield_count");
            userinfo_vo.setShield_count(sh_count);
            int locked = obj.getInt("locked");
            userinfo_vo.setLocked(locked);
            // -------base info---
            JSONObject obj_base_info = obj.getJSONObject("base_info");
            userinfo_vo.setJson_base_info(obj_base_info.toString());
            String desc_self = obj.getJSONObject("heart_description")
                    .getString("content");
            // {"child":"","uid":"55","degree":"","height":"171","nickname":{"audit_content":"小乖乖","content":"小乖乖"},"age":"26岁","marriage":"","city":{"city_id":1907,"province_id":19,"city_name":"盐城","province_name":"江苏"}}

            if (TextUtils.isEmpty(desc_self) || desc_self.equals("null")) {
                desc_self = "我在美盟，你在哪儿？";
            }
            userinfo_vo.setHeart_description(desc_self);
            userinfo_vo.setHeight(obj_base_info.getString("height"));
            userinfo_vo.setNickName(obj_base_info.getJSONObject("nickname")
                    .getString("content"));
            userinfo_vo.setAge(obj_base_info.getJSONObject("age").getString(
                    "value"));
            userinfo_vo.setDegree(obj_base_info.getJSONObject("degree")
                    .getString("value"));
            userinfo_vo.setId_degree(obj_base_info.getJSONObject("degree")
                    .getString("id"));

            // "city":{"id":2701,"value":{"city_id":2701,"city_name":"\u6210\u90fd","province_id":27,"province_name":"\u56db\u5ddd"}}}

            // userinfo_vo.setProvince_name(obj_base_info.getJSONObject("city")
            // .getString("province_name"));
            userinfo_vo.setCity_name(obj_base_info.getJSONObject("city")
                    .getString("city_name"));
            userinfo_vo.setCity_id(obj_base_info.getJSONObject("city").getInt(
                    "city_id")
                    + "");
            userinfo_vo.setChild(obj_base_info.getJSONObject("child")
                    .getString("value"));
            userinfo_vo.setId_child(obj_base_info.getJSONObject("child")
                    .getString("id"));
            userinfo_vo.setMarriage(obj_base_info.getJSONObject("marriage")
                    .getString("value"));
            userinfo_vo.setId_marriage(obj_base_info.getJSONObject("marriage")
                    .getString("id"));
            // clearData();
            iniBaseView();

            // ----------------detail--info---
            // {"car":"A","constellation":null,"overseas_experience":"","profession":"","blood_type":{"value":"","id":0},"assets_level":"500W以下","ehtnic":{"value":"汉族","id":1},"nationality":"","audit_type":"1","zodiac":null,"family":"","salary":"","medal":null,"live":""}
            JSONObject obj_detail = obj.getJSONObject("detail_info");

            // ar":"A","constellation":"金牛座","overseas_experience":"","profession":"计算机类","blood_type":{"value":"AB","id":3},"assets_level":"500W以下","ehtnic":{"value":"汉族","id":1},"nationality":"中国","audit_type":"1","zodiac":"兔"
            userinfo_vo.setJson_detailinfo(obj_detail.toString());
            userinfo_vo.setXingzuo(obj_detail.getString("constellation"));
            userinfo_vo.setShengxiao(obj_detail.getString("zodiac"));
            userinfo_vo.setSalary(obj_detail.getJSONObject("salary").getString(
                    "value"));

            // userinfo_vo.setMarriage(obj_base_info.getJSONObject("marriage").getString("value"));
            // userinfo_vo.setNationality(obj_detail.getJSONObject("nationality").getString("value"));

            userinfo_vo.setOverseas_experience(obj_detail.getJSONObject(
                    "overseas_experience").getString("value"));
            userinfo_vo.setNationality(obj_detail.getJSONObject("nationality")
                    .getString("value"));
            userinfo_vo.setProfession(obj_detail.getJSONObject("profession")
                    .getString("value"));
            userinfo_vo.setAssets_level(obj_detail
                    .getJSONObject("assets_level").getString("value"));
            userinfo_vo.setFamily(obj_detail.getJSONObject("family").getString(
                    "value"));
            userinfo_vo.setCar(obj_detail.getJSONObject("car").getString(
                    "value"));
            userinfo_vo.setLive(obj_detail.getJSONObject("live").getString(
                    "value"));
            // ----------id
            userinfo_vo.setId_assert(obj_detail.getJSONObject("assets_level")
                    .getInt("id") + "");
            userinfo_vo.setId_car(obj_detail.getJSONObject("car").getInt("id")
                    + "");
            userinfo_vo.setId_career(obj_detail.getJSONObject("profession")
                    .getInt("id") + "");
            userinfo_vo.setId_family_bg(obj_detail.getJSONObject("family")
                    .getInt("id") + "");
            userinfo_vo.setId_live(obj_detail.getJSONObject("live")
                    .getInt("id") + "");
            userinfo_vo.setId_mingzu(obj_detail.getJSONObject("assets_level")
                    .getInt("id") + "");
            userinfo_vo.setId_nation(obj_detail.getJSONObject("nationality")
                    .getInt("id") + "");
            userinfo_vo.setId_oversea(obj_detail.getJSONObject(
                    "overseas_experience").getInt("id")
                    + "");
            userinfo_vo.setId_salery(obj_detail.getJSONObject("salary").getInt(
                    "id")
                    + "");

            // -------id

            userinfo_vo.setBlood(obj_detail.getJSONObject("blood_type")
                    .getString("value"));
            userinfo_vo.setMingzu(obj_detail.getJSONObject("ehtnic").getString(
                    "value"));
            ;
            iniDetailView();
            // ----------------apearance--info---
            JSONObject obj_appearance = obj.getJSONObject("appearance_info");

            String key_j = "value";
            if (obj_appearance == null || obj_appearance.isNull("weight")) {

            } else {
                userinfo_vo.setJson_bodystyle(obj_appearance.toString());
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
                userinfo_vo.setVa3_attract_place(va_goodpos);
                userinfo_vo.setVa3_body_style(va_bs);
                userinfo_vo.setVa3_eye_color(va_ec);
                userinfo_vo.setVa3_face_style(va_fs);
                userinfo_vo.setVa3_hair_color(va_hc);
                userinfo_vo.setVa3_hair_style(va_hs);
                userinfo_vo.setVa3_self_assert(va_face_self);
                userinfo_vo.setVa3_weight(va_weight);

            }

            // ----------------work--info---
            JSONObject obj_work = obj.getJSONObject("work_info");
            userinfo_vo.setJson_workinfo(obj_work.toString());

            String va_cp_type, va_cp_trade = null, va_workstate, income, grad_year, va_pro, lang;
            if (obj_work == null
                    || obj_work.isNull(UserAll_work_info.company_type)) {

            } else {

                va_cp_trade = obj_work.getJSONObject(
                        MConstantUser.UserAll_work_info.company_trade)
                        .getString(key_j);
                va_cp_type = obj_work.getJSONObject(
                        UserAll_work_info.company_type).getString(key_j);
                va_workstate = obj_work.getJSONObject(
                        UserAll_work_info.work_status).getString(key_j);
                income = obj_work.getJSONObject(UserAll_work_info.income)
                        .getString(key_j);
                grad_year = obj_work.getJSONObject(
                        UserAll_work_info.graduation_time).getString(key_j);
                va_pro = obj_work.getJSONObject(UserAll_work_info.profession)
                        .getString(key_j);
                JSONArray arr = obj_work
                        .getJSONArray(UserAll_work_info.language);
                StringBuilder sb = new StringBuilder("");

                for (int i = 0; i < arr.length(); i++) {
                    String va = arr.getJSONObject(i).getString(key_j);
                    sb.append(va);

                }
                lang = sb.toString();
                userinfo_vo.setVa4_comp_trade(va_cp_type);
                userinfo_vo.setVa4_comp_type(va_cp_trade);
                userinfo_vo.setVa4_grad_year(grad_year);
                userinfo_vo.setVa4_income_desc(income);
                userinfo_vo.setVa4_job_state(va_workstate);
                userinfo_vo.setVa4_lang(lang);
                userinfo_vo.setVa4_prof_type(va_pro);

            }

            // ----------------life--info---
            JSONObject obj_life = obj.getJSONObject("life_info");
            if (obj_life == null
                    || obj_life.isNull(UserAll_life_info.largest_consumer)) {

            } else {

                userinfo_vo.setJson_lifeinfo(obj_life.toString());
                userinfo_vo.setVa5_big_lar(getStrFrJson(obj_life
                        .getJSONObject(UserAll_life_info.largest_consumer)));
                userinfo_vo.setVa5_drink(getStrFrJson(obj_life
                        .getJSONObject(UserAll_life_info.drinking)));

                userinfo_vo.setVa5_execise(getStrFrJson(obj_life
                        .getJSONObject(UserAll_life_info.take_exercise)));
                userinfo_vo.setVa5_issmoke(getStrFrJson(obj_life
                        .getJSONObject(UserAll_life_info.smoke)));
                userinfo_vo.setVa5_iswantchild(getStrFrJson(obj_life
                        .getJSONObject(UserAll_life_info.want_children)));
                userinfo_vo.setVa5_livewith(getStrFrJson(obj_life
                        .getJSONObject(UserAll_life_info.with_parents)));
                userinfo_vo.setVa5_makeroma(getStrFrJson(obj_life
                        .getJSONObject(UserAll_life_info.make_romantic)));
                userinfo_vo.setVa5_pets(getStrArr(obj_life
                        .getJSONArray(UserAll_life_info.pet_value)));
                userinfo_vo.setVa5_rank(getStrFrJson(obj_life
                        .getJSONObject(UserAll_life_info.family_ranking)));
                userinfo_vo.setVa5_rest(getStrFrJson(obj_life
                        .getJSONObject(UserAll_life_info.schedule)));
                userinfo_vo.setVa5_skills(getStrArr(obj_life
                        .getJSONArray(UserAll_life_info.life_skill)));
                userinfo_vo.setVa5_zongjiao(getStrFrJson(obj_life
                        .getJSONObject(UserAll_life_info.religion)));

            }

            // ----------------hobby--info----
            // Inipersonkinds()

            // ----------------hobby--info----
            JSONObject obj_hobby_info = obj.getJSONObject("hobby_info");
            btadAdapter.setDatajson(obj_hobby_info.toString());
            btadAdapter.parselData(obj_hobby_info.toString());
            btadAdapter.notifyDataSetChanged();

            JSONArray obj_survey = obj.getJSONArray("personality_info");
            PersonalAdapter personla_btaAdapter = new PersonalAdapter(
                    BarPushActivity.this);
            personla_btaAdapter.setDatajson(obj_survey.toString());
            personla_btaAdapter.parselData(obj_survey.toString());
            ls_personlity.setAdapter(personla_btaAdapter);
            personla_btaAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            showToast("解析数据出错！！" + e == null ? "" : e.getMessage());
            e.printStackTrace();
        }
    }

    void getAnList(JSONArray arr) {
        try {
            for (int i = 0; i < arr.length(); i++) {
                AnSwerVo asv = new AnSwerVo();
                JSONObject cell = arr.getJSONObject(i);
                String name = cell.getString("type_value");
                JSONArray cell_arr = cell.getJSONArray("question");
                StringBuilder sbcell = new StringBuilder("");
                for (int j = 0; j < cell_arr.length(); j++) {
                    SubVo cv = new SubVo();
                    String st = cell_arr.getJSONObject(j).getString("answer");
                    cv.setValue(cell_arr.getJSONObject(j).getString(
                            "option_value"));
                    cv.setType_id(cell_arr.getJSONObject(j).getString(
                            "option_id"));
                    sbcell.append(st + " ");

                }
                asv.setType(name);
                asv.setAnswer(sbcell.toString());
                arr_sueveys.add(asv);

            }
        } catch (Exception e) {
            showToast("读取信息出错！");
            e.printStackTrace();
        } finally {

        }

    }

    public class SubVo {
        String type_id = "";
        String value = "";

        public String getType_id() {
            return type_id;
        }

        public void setType_id(String type_id) {
            this.type_id = type_id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

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

    List<HobbyVo> getHobbyData(JSONArray arr, String lefttypeName) {
        HobbyVo hv = new HobbyVo();
        List<UserAdapterItem> lscell = new ArrayList<UserAdapterItem>();
        String id = "id";
        String value = "value";
        StringBuilder sb1 = new StringBuilder("");
        if (arr == null) {
            hv.setLeftName(lefttypeName);
            hv.setData("暂时保密");
            arr_hobby.add(hv);
            return arr_hobby;
        }

        for (int i = 0; i < arr.length(); i++) {
            UserAdapterItem cell = new UserAdapterItem();
            try {
                cell.setLeftStr(lefttypeName);
                cell.setId(arr.getJSONObject(i).getString(id));
                cell.setRightStr(arr.getJSONObject(i).getString(value));
                sb1.append(cell.getRightStr() + ",");

            } catch (JSONException e) {
                showToast("解析爱好数据异常！");
                e.printStackTrace();
            } finally {

                lscell.add(cell);
                hv.setLeftName(lefttypeName);
                hv.setLsdata(lscell);
                String data = sb1.toString();
                if (data.length() > 2) {
                    data = data.substring(0, data.length() - 1);

                }
                hv.setData(sb1.toString());
            }

        }
        arr_hobby.add(hv);
        return arr_hobby;

    }

    RespVo vo_resp = null;
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ProDimiss();

            switch (msg.what) {
                case -123:
                    if (adapter_greet != null) {
                        adapter_greet.notifyDataSetChanged();
                    }
                    break;
                case DETAIL_FAIL: {
                    showToast(mg2String(msg));
                    getUserImgs();

                }
                break;
                case 123:
                    halfParsel(msg.obj.toString());
                    setViewUser();
                    break;
                case DETAIL_INFO: {
                    String detail_info = detail_resp.getResp();

                    getUserImgs();

                    parselHeaderInfo(detail_info);

                    // halfParsel(detail_resp.getResp());
                    // setViewUser();
                    // getMedalInfo();
                    // try {
                    // userinfo_vo.setHobby_data(arr_hobby);
                    // adapter_personlity.setHidden(true);
                    // adapter_personlity.setObjs(arr_sueveys);
                    // } catch (Exception e) {
                    // e.printStackTrace();
                    //
                    // // TODO: handle exception
                    // }

                }
                break;

                case Resp_exception: {
                    showToast("" + mg2String(msg));
                }

                break;
                case Resp_action_fail: {
                    showToast("" + mg2String(msg));
                }

                break;
                case Resp_action_ok: {

                    String re = vo_resp.getResp();

                    parselJson(re);

                    showTitle(vo.getNickName() == null
                            || "null".equals(vo.getNickName())
                            || TextUtils.isEmpty(vo.getNickName()) ? "用户详情" : vo
                            .getNickName());
                    if ((Integer) layLock.getTag() == 2) {
                        // 上锁状态
                        showLock();
                    } else {

                        if (photos == null || photos.size() < 1) {
                            setValueToView(txtpro, "0" + "张");
                            View lay = LayoutInflater.from(
                                    BarPushActivity.this.getApplicationContext())
                                    .inflate(R.layout.imgs_cell, null);
                            header = (ImageView) lay.findViewById(R.id.photo_cell);

                            header.setClickable(false);

                            if (vo.getSex().equals("2")) {
                                header.setImageResource(R.drawable.female_default);

                            } else {
                                header.setImageResource(R.drawable.man_default);
                            }

                            views.add(lay);
                            NewViewPagerAdapter vpAdapter_new = new NewViewPagerAdapter(
                                    views);
                            svp.setAdapter(vpAdapter_new);

                            return;
                        }

                        setValueToView(txtpro, "1/" + photos.size() + "张");

                        ImagePagerAdapter adapter = new ImagePagerAdapter();
                        svp.setAdapter(adapter);
                        svp.setOnPageChangeListener(BarPushActivity.this);

                    }

                    halfParsel(detail_resp.getResp());

                    setViewUser();

                    // getMedalInfo();//勋章

                }

                break;

                case Resp_action_Empty: {
                    showToast("拉取数据失败!");

                }

                break;

                case ACTION_TAG2: {
                    showToast("操作成功！!");
                    photos.clear();
                    getUserInfo();
                }
                break;

                case ACTION_TAG1: {
                    photos.clear();
                    showToast("操作成功！!");
                    rb_wantsee.setText("已申请见面");
                    rb_wantsee.setTag("3");
                    // rb_wantsee.setClickable(false);
                    rb_wantsee
                            .setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    t_context.getResources().getDrawable(
                                            R.drawable.selector_detail_applied),
                                    null, null);
                    // ---
                    rb_wantsee_out.setText("已申请见面");
                    rb_wantsee_out.setTag("3");
                    // ------
                    rb_wantsee_out
                            .setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    t_context.getResources().getDrawable(
                                            R.drawable.selector_detail_applied),
                                    null, null);

                    getUserInfo();

                }

                break;

                default:
                    break;
            }
        }

        ;

    };
    String url_voice = null;

    void showLock() {
        if ((Integer) layLock.getTag() == 2) {

            View lay = LayoutInflater.from(
                    BarPushActivity.this.getApplicationContext()).inflate(
                    R.layout.imgs_cell, null);

            header = (ImageView) lay.findViewById(R.id.photo_cell);

            header.setClickable(false);

            if (vo.getSex().equals("2")) {
                header.setImageResource(R.drawable.female_default);
            } else {
                header.setImageResource(R.drawable.man_default);
            }

            views.add(lay);

            GeneralUtil.view_Show(findViewById(R.id.lay_lock));

            NewViewPagerAdapter vpAdapter_new = new NewViewPagerAdapter(views);

            svp.setAdapter(vpAdapter_new);
        }
    }

    void iniMedalView() {
        medal_lay = (LinearLayout) findViewById(R.id.medal_lay_gp);
        medal_lay_renzheng = (LinearLayout) findViewById(R.id.medal_lay_renzheng);
        medal_1 = findImageView(R.id.medal_1);
        medal_2 = findImageView(R.id.medal_2);
        medal_3 = findImageView(R.id.medal_3);
        medal_4 = findImageView(R.id.medal_4);
        medal_5 = findImageView(R.id.medal_5);
        medal_renzheng = findImageView(R.id.medal_renzheng);
        medal_vip = findImageView(R.id.medal_vip);
    }

    protected void parselHeaderInfo(String res) {
        try {

            JSONObject obj = new JSONObject(res).getJSONObject("data");
            // followed:是否已经关注,int;1-是;2-否;
            vo.setSex(obj.getString("sex"));
            int vip_type = obj.getInt("vip_level");
            String medals = obj.getString("medal");
            String aud_type = obj.getString("audit_type");
            vo.setAudi_type(Integer.parseInt(aud_type));
            vo.setMedals(medals);
            vo.setVip_level(vip_type);

            url_voice = obj.getJSONObject("voice_description").getString("url");

            JSONObject obj_base = obj.getJSONObject("base_info");
            // userinfo_vo.setNickName(obj_base_info.getJSONObject("nickname")
            // .getString("content"));
            // -------base info---
            userinfo_vo.setJson_base_info(obj_base.toString());
            String desc_self = obj.getJSONObject("heart_description")
                    .getString("content");

            if (TextUtils.isEmpty(desc_self) || desc_self.equals("null")) {
                desc_self = "我在美盟，你在哪儿？";
            }
            userinfo_vo.setHeart_description(desc_self);
            vo.setNickName(obj_base.getJSONObject("nickname").getString(
                    "content"));
            String age = obj_base.getJSONObject("age").getString("value");

            String cityName = "";
            String proName = "";

            if (obj_base.optJSONObject("city") != null) {

                cityName = obj_base.optJSONObject("city")
                        .optString("city_name");

                proName = obj_base.optJSONObject("city").optString(
                        "province_name");
            }

            String url = "";
            if (obj.optJSONObject("header") != null) {
                url = obj.optJSONObject("header").optString("url");
            }

            header_user.setImageBitmap(null);
            DisplayImageOptions options = null;

            if (userinfo_vo.getSex() == 2) {

                header_user.setImageResource(R.drawable.female_head);
                options = new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(R.drawable.female_head)
                        .showImageOnFail(R.drawable.female_head)
                        .resetViewBeforeLoading().cacheOnDisc()
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .displayer(new RoundedBitmapDisplayer(120)).build();
            } else if (userinfo_vo.getSex() == 1) {

                header_user.setImageResource(R.drawable.male_head);
                options = new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(R.drawable.male_head)
                        .showImageOnFail(R.drawable.male_head)
                        .resetViewBeforeLoading().cacheOnDisc()
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .displayer(new RoundedBitmapDisplayer(120)).build();
            } else {
                header_user.setImageResource(R.drawable.default_head);
                options = new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(R.drawable.default_head)
                        .showImageOnFail(R.drawable.default_head)
                        .resetViewBeforeLoading().cacheOnDisc()
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .displayer(new RoundedBitmapDisplayer(120)).build();
            }

            if (!TextUtils.isEmpty(url) && !url.equals("null")) {
                imageLoader.resume();
                imageLoader.displayImage(url, header_user, options);
            }

            player_instance.iniData(url_voice);
            setValueToView(user_age, age);
            setValueToView(user_area, proName + "" + cityName);

            // NSLoger.Log("detial-info===" + res);
            // locked:相册头像上锁状态,int;1-对所有人开放(默认);2-对所有人关闭;3-对部分人开放;个人设置中也会用到这个状态;
            int islocked = obj.getInt("locked");

            switch (islocked) {
                case 1: {

                    layLock.setTag(1);

                    // showToast("对所有人开放");
                }

                break;
                case 2: {
                    layLock.setTag(2);
                    // showToast("对所有人关闭");
                }

                break;
                case 3: {
                    // opentome: 相册头像对我开放(上锁后生效,如果没对我开放,无法查看相册和头像),int;1-不开放;2-开放;
                    layLock.setTag(3);
                    // showToast("对部分人开放");
                    int isopen = obj.optInt("isopentome");
                    if (isopen == 2) {

                        layLock.setTag(1);
                    } else {
                        layLock.setTag(2);
                    }

                }

                break;

                default:
                    break;
            }
            int isfowlled = obj.getInt("followed");
            if (isfowlled == 1) {

                imv_focus.setImageResource(R.drawable.interest_p);
                imv_focus.setTag(1);
            } else {
                imv_focus.setTag(2);
                imv_focus.setImageResource(R.drawable.interest);

            }
            // wantsee:是否已经想见,int; 1-是;2-否;
            int issend = obj.getInt("wantsee");
            if (issend == 1) {
                rb_wantsee.setText("已申请见面");
                rb_wantsee.setTag("3");
                rb_wantsee
                        .setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                t_context.getResources().getDrawable(
                                        R.drawable.selector_detail_applied),
                                null, null);

                // -------
                rb_wantsee_out.setText("已申请见面");
                rb_wantsee_out.setTag("3");
                rb_wantsee_out
                        .setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                t_context.getResources().getDrawable(
                                        R.drawable.selector_detail_applied),
                                null, null);
            } else {
                rb_wantsee.setTag("1");
                rb_wantsee
                        .setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                t_context.getResources().getDrawable(
                                        R.drawable.selector_detail_wantsee),
                                null, null);
                rb_wantsee.setText("我想见TA");
                rb_wantsee.setClickable(true);
                // ---------
                rb_wantsee_out.setTag("1");
                rb_wantsee_out
                        .setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                t_context.getResources().getDrawable(
                                        R.drawable.selector_detail_wantsee),
                                null, null);
                rb_wantsee_out.setText("我想见TA");
                rb_wantsee_out.setClickable(true);

            }

        } catch (Exception e) {
            showToast("解析数据异常" + e.getMessage());
            e.printStackTrace();
        }
        // data":{"sex":"2"
        // vo.setSex(obj.gets)
    }

    protected void showTitle(String str) {
        setValueToView(findViewById(R.id.center_show), str);

    }

    Dialog diag_expa = null;
    DiagPopView diagpopview = null;
    View greet_view = null;
    ExpandableListAdapter adapter = null;
    greetAdapter adapter_greet = null;
    XmlDataGreeting datagreet = null;

    View createViewGreet() {
        View v_p = null;
        // if (v_p==null) {

        v_p = LayoutInflater.from(t_context).inflate(
                R.layout.pop_expanable_view, null);
        v_p.findViewById(R.id.ok).setOnClickListener(this);
        v_p.findViewById(R.id.no).setOnClickListener(this);
        // }

        ExpandableListView expandableListView = (ExpandableListView) v_p
                .findViewById(R.id.list_expa);
        expandableListView.setGroupIndicator(null);//
        // if (null == adapter_greet) {

        adapter_greet = new greetAdapter();
        adapter_greet.setSex("2");
        expandableListView.setAdapter(adapter_greet);
        adapter_greet.notifyDataSetChanged();

        expandableListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {

                return false;
            }
        });

        // 设置item点击的监听器
        expandableListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                ImageView iv = (ImageView) v.findViewById(R.id.child_imv);
                if (Integer.parseInt((String) iv.getTag()) == 1) {

                    iv.setBackgroundResource(R.drawable.ico_single_p);
                    iv.setTag("2");
                } else {

                    iv.setBackgroundResource(R.drawable.ico_single_normal);

                }
                adapter_greet.setPos_group_ted(groupPosition);
                adapter_greet.setPos_son_ted(childPosition);
                adapter_greet.setPos_value(adapter_greet.getChild(
                        groupPosition, childPosition) + "");

                handler.obtainMessage(-123).sendToTarget();

                return false;
            }
        });
        return v_p;

    }

    void iniGreetData() {

        datagreet = XmlDataGreeting.getSingle();
        if (t_context == null) {
            t_context = this.getApplicationContext();
        }
        try {
            datagreet.init(t_context);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {

            System.gc();
            MeiMengApp app = (MeiMengApp) this.getApplication();
            app.ReleaseForce();
        }

    }

    void popLock() {
        if (diagpopview == null) {

            diagpopview = new DiagPopView(t_context);
            diagpopview.setOnHeaderItemClick(this);
        }
        diagpopview.showAsDropDown(findViewById(R.id.top));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_right_btn: {

                //游客
                if (MeiMengApp.isVisitor) {
                    MeiMengApp.setVisitorDialogView(BarPushActivity.this,1);
                }

                //有账号的用户
                else {
                    if (vo.getSex().equals((sh.getUserInfo().getSex() + "").trim())) {
                        return;

                    } else {

                        // if (!TextUtils.isEmpty(vo.getUid())) {
                        //
                        // addApplyLook(vo.getUid());
                        // }else{
                        if ((Integer) imv_focus.getTag() == 1) {
                            imv_focus.setImageResource(R.drawable.interest_p);
                            addFocus(vo.getUid(), false);

                        } else {
                            imv_focus.setImageResource(R.drawable.interest);
                            addFocus(vo.getUid(), true);
                        }

                        // }

                    }
                }


            }

            break;
            case R.id.lay_isopen:
            case R.id.lay_lock: {
                int tag = (Integer) layLock.getTag();
                NSLoger.Log("tag--" + tag);

                if (vo.getSex().equals((sh.getUserInfo().getSex() + "").trim())) {
                    return;

                } else {
                    if ((Integer) layLock.getTag() == 2) {

                        popLock();
                    }

                }

            }
            break;
            case R.id.home_left_btn: {
                onBackPressed();
            }

            break;
            case R.id.out_bottom_btn1: {

                pu_scroll.smoothScrollTo(0, -1100);
                findViewById(R.id.bottom_btn1).setTag(1);
            }
            break;
            case R.id.bottom_btn1: {
                // Intent arg = new Intent(t_context, TaDetail.class);
                // if (vo != null) {
                // if (!TextUtils.isEmpty(vo.getUid())
                // && !TextUtils.isEmpty(vo.getNickName())) {
                // arg.putExtra("ta_id", vo.getUid());
                // arg.putExtra("ta_name", vo.getNickName());
                // t_context.startActivity(arg);
                // }
                // }
                Integer tag = (Integer) findViewById(R.id.bottom_btn1).getTag();
                if (tag == 1) {
                    pu_scroll.smoothScrollTo(0, 1030);

                    findViewById(R.id.bottom_btn1).setTag(2);
                } else {
                    pu_scroll.smoothScrollTo(0, -1100);
                    findViewById(R.id.bottom_btn1).setTag(1);
                }

            }

            break;
            case R.id.voice_lay: {
                count_time++;

                try {
                    if (url_voice.equals("null") || TextUtils.isEmpty(url_voice)) {
                        showToast("该用户暂未上传语音！");
                        return;
                    }
                    if (!player_instance.getPlayingStateCurrent()) {

                        if (count_time > 2) {
                            player_instance.SateContinue();

                        } else {

                            player_instance.statePlay();
                        }

                    } else {
                        player_instance.StatePauseOrGoing();
                        player_instance.clearData();

                    }

                } catch (Exception e) {
                    showToast("读取语音失败！");
                    e.printStackTrace();
                    e.printStackTrace();
                }

            }
            break;
            case R.id.ok: {
                // showToast("send");

                if (diag_expa != null) {
                    if (diag_expa.isShowing()) {
                        diag_expa.dismiss();
                    }

                }

                if (TextUtils.isEmpty(adapter_greet.getPos_value())
                        || null == adapter_greet.getPos_value()
                        || "null".equals(adapter_greet.getPos_value())) {
                    return;
                } else {

                    addGreet(adapter_greet.getPos_value());
                }

            }
            ;
            break;
            case R.id.no: {
                if (diag_expa != null) {
                    if (diag_expa.isShowing()) {
                        diag_expa.dismiss();
                    }
                }

            }
            ;
            break;
            case R.id.out_bottom_btn2:
            case R.id.bottom_btn2: {

                //如果是游客
                if (MeiMengApp.isVisitor) {
                    MeiMengApp.setVisitorDialogView(BarPushActivity.this,1);
                }

                //有账号的用户
                else {
                    // 登陆和查看用户同为女-禁用
                    if (vo.getSex().equals((sh.getUserInfo().getSex() + "").trim())) {
                        return;

                    } else {
                        if (null == datagreet) {
                            iniGreetData();
                        }
                        if (greet_view == null) {
                            greet_view = createViewGreet();
                        }

                        if (diag_expa == null || !diag_expa.isShowing()) {
                            diag_expa = pushDiagWindow(greet_view);

                        }
                        greet_view = null;
                        datagreet = null;
                        ;
                    }
                }


            }

            break;
            case R.id.bottom_btn3:
            case R.id.out_bottom_btn3: {

                // 登陆和查看用户同为女-禁用
                // if (vo.getSex().equals("2")
                // && shareUserInfo().getUserInfo().getSex() == 2) {
                // showToat("此功能只限异性用户使用！");
                // return;
                //
                // } else {

                //如果是游客
                if (MeiMengApp.isVisitor) {
                    MeiMengApp.setVisitorDialogView(BarPushActivity.this,1);
                }

                //有账号的用户
                else {
                    if (vo.getSex().equals((sh.getUserInfo().getSex() + "").trim())) {
                        return;

                    } else {
                        String tag = rb_wantsee.getTag().toString();
                        if (TextUtils.isEmpty(tag) || !tag.equals("3")) {
                            if (!TextUtils.isEmpty(vo.getUid())) {

                                addApplyLook(vo.getUid());
                            }
                        } else {
                            return;
                        }

                    }
                }


                // }
            }

            break;

        }
    }

    /**
     * @param type true int 类型;1-关注;2-取消关注;
     * @category 添加问候
     */
    void addGreet(String msg) {
        ProShow("");
        try {
            req_map_param.put("message", msg);
            req_map_param.put("target_uid", userid_target);
            req_map_param.put("uid", sh.getUserid() + "");
            req_map_param.put("key", key_server);

            // URL：/api/operate/greetings
            // 请求参数：
            // 参数名 必填 类型 描述
            // key true string 参见规范中的key值
            // uid true int 用户ID
            // target_uid true int 目标用户ID
            // message true string 固定的问候短语,见配置
            // 返回结果：
            // 键 类型 描述
            // result int 1-成功；2-失败；
            // error string 错误信息
            poolThread.submit(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    RespVo rv = sendReq(MURL_GREET, req_map_param);

                    if (rv.isHasError()) {
                        handler.obtainMessage(Resp_action_fail,
                                rv.getErrorDetail()).sendToTarget();
                    } else {
                        handler.obtainMessage().sendToTarget();
                    }
                }
            });

        } catch (Exception e) {
            handler.obtainMessage(Resp_DATA_EXCEPTION).sendToTarget();
        }

    }

    /**
     * @param target_uid true int 目标用户ID
     * @param type       true int 类型;1-关注;2-取消关注;
     * @category isadd true代表添加关注！ false取消关注
     */
    void addFocus(String tagid, boolean isadd) {
        ProShow("");
        req_map_param.put("type", isadd == true ? "1" : "2");
        req_map_param.put("target_uid", tagid);
        req_map_param.put("uid", sh.getUserid() + "");
        req_map_param.put("key", key_server);
        poolThread.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    RespVo rv = sendReq(MURL_ADD_FOCUS, req_map_param);
                    if (null == rv) {
                        handler.obtainMessage(Resp_NET_FAIL).sendToTarget();
                    }
                    if (rv.isHasError()) {
                        handler.obtainMessage(Resp_action_fail,
                                rv.getErrorDetail()).sendToTarget();

                    } else {
                        handler.obtainMessage(ACTION_TAG2).sendToTarget();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    handler.obtainMessage(Resp_exception, e.getMessage())
                            .sendToTarget();

                }

            }
        });

    }

    /**
     * @category 申请see
     */
    void addApplyLook(String tagid) {

        ProShow("正在发送申请...");
        req_map_param.put("target_uid", tagid);
        req_map_param.put("uid", sh.getUserid() + "");
        req_map_param.put("key", key_server);
        poolThread.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    RespVo rv = sendReq(MURL_ADD_WANT_2_SEE, req_map_param);
                    if (null == rv) {
                        handler.obtainMessage(Resp_NET_FAIL).sendToTarget();
                    }
                    if (rv.isHasError()) {
                        handler.obtainMessage(Resp_action_fail,
                                rv.getErrorDetail()).sendToTarget();

                    } else {
                        handler.obtainMessage(ACTION_TAG1).sendToTarget();

                    }

                } catch (Exception e) {
                    handler.obtainMessage(Resp_exception).sendToTarget();

                }

            }
        });

    }

    /**
     * @category 申请see
     */
    void addApplyLookGallery(String tagid) {

        ProShow("");
        req_map_param.put("target_uid", tagid);
        req_map_param.put("uid", sh.getUserid() + "");
        req_map_param.put("key", key_server);
        poolThread.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    RespVo rv = sendReq(MURL_ADD_LOOK_APPLY, req_map_param);
                    if (null == rv) {
                        handler.obtainMessage(Resp_NET_FAIL).sendToTarget();
                    }
                    if (rv.isHasError()) {
                        handler.obtainMessage(Resp_action_fail,
                                rv.getErrorDetail()).sendToTarget();

                    } else {
                        handler.obtainMessage(ACTION_TAG1).sendToTarget();

                    }

                } catch (Exception e) {
                    handler.obtainMessage(Resp_exception, e.getMessage())
                            .sendToTarget();

                }

            }
        });

    }

    protected void setValueToView(View v, String value) {
        if (null == value) {
            value = "暂时保密";
        }
        if (null == v) {
            // warnNilView();
        }
        if (v instanceof Button) {
            ((Button) v).setText(value);
        } else if (v instanceof EditText) {
            ((EditText) v).setText(value);
        } else if (v instanceof TextView) {
            ((TextView) v).setText(value);
        } else {
            // warnIllageParam();
        }
    }

    protected ProgressDialog mpb;

    protected void ProShow(String str) {

        try {
            View contentView = LayoutInflater.from(t_context).inflate(
                    R.layout.progressdialog, null);
            if (TextUtils.isEmpty(str)) {
                str = "正在加载数据...";
            }
            setValueToView(contentView.findViewById(R.id.gress_text_view), str);

            mpb = new ProgressDialog(BarPushActivity.this);
            mpb.show();

            mpb.setContentView(contentView);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected Dialog wheel_diag = null;
    protected WheelView singleWheelView = null;
    protected click_wheel click_yes_diag = null;

    /**
     * @category 自定义Push窗口点击事件
     */
    public interface click_wheel extends OnClickListener {

        /**
         * @category Push窗口 确定和消失点击事件
         */
        void click_yes_or_no(View v, int index, String value);

    }

    /**
     * @category push窗口
     */
    protected Dialog pushDiagWindow(View v) {
        return wheel_diag = Tools.pull_Dialog(t_context, v);

    }

    public class DiagBtn_yes_no implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (null == singleWheelView) {
                throw new IllegalArgumentException("push窗口异常！");
                // break;
            }
            String value = singleWheelView.getAdapter().getItem(
                    singleWheelView.getCurrentItem());
            click_yes_diag.click_yes_or_no(v, singleWheelView == null ? 0
                    : singleWheelView.getCurrentItem(), value);

        }

    }

    protected void ProDimiss() {

        if (mpb != null) {
            if (mpb.isShowing()) {
                try {
                    // Thread.sleep(3000);

                } catch (Exception e) {
                    // TODO: handle exception
                }
                mpb.dismiss();
            }

        }
    }

    protected String mg2String(Message msg) {
        return msg.obj == null ? "" : (String) msg.obj;

    }

    protected void showToast(CharSequence s) {
        if (TextUtils.isEmpty(s)) {
            return;

        }

        Toast.makeText(BarPushActivity.this, s, 2).show();

    }

    void initialView() {

        showNavgationLeftBar("返回");
        showTitle("用户详情");
        findViewById(R.id.home_left_btn).setOnClickListener(this);
        findViewById(R.id.home_right_btn).setOnClickListener(this);
        vo = (UserSearchVo) this.getIntent().getSerializableExtra("data");
        svp = (ViewPager) findViewById(R.id.viewpager);
        // --------
        player_instance = AACPlayer.getInstance();
        player_instance.setWaveBg(imv_voice, imv_clear);
        // -----
        findViewById(R.id.lay_isopen).setOnClickListener(this);
        findViewById(R.id.voice_lay).setOnClickListener(this);

        layLock = (LinearLayout) findViewById(R.id.lay_lock);
        layLock.setTag(0);

        imv_voice = findImageView(R.id.right_anim);
        imv_clear = findImageView(R.id.cleananim);
        imv_focus = findImageView(R.id.home_right_btn);
        imv_focus.setTag(2);
        txtpro = (TextView) findViewById(R.id.txtpro);
        user_age = (TextView) findViewById(R.id.age_value);
        user_area = (TextView) findViewById(R.id.area_value);
        header_user = (ImageView) findViewById(R.id.beauty_user);

        rb_wantsee = (RadioButton) findViewById(R.id.bottom_btn3);
        rb_wantsee.setTag("1");
        rb_wantsee_out = (RadioButton) findViewById(R.id.out_bottom_btn3);
        rb_wantsee_out.setTag("1");

        findViewById(R.id.bottom_btn1).setTag(1);

        if (vo.getSex().equals("2")) {
            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.female_default)
                    .showImageOnFail(R.drawable.female_default)
                    .resetViewBeforeLoading().cacheOnDisc()
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory()
                    .displayer(new FadeInBitmapDisplayer(300)).build();

        } else {
            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.man_default)
                    .showImageOnFail(R.drawable.man_default)
                    .resetViewBeforeLoading().cacheOnDisc()
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory()
                    .displayer(new FadeInBitmapDisplayer(300)).build();
        }
        iniMedalView();

        // getUserImgs();
        getUserInfo();
        // new getDataAsyc().execute();
    }

    void iniMedalData() {
        medal_data = XmlMedalData.getSingle();
        try {
            medal_data.init(t_context);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void getUserInfo() {

        ProShow("正在获取详情...");

        try {

            poolThread.submit(new Runnable() {

                @Override
                public void run() {
                    req_map_param.put(MConstantUser.UserProperty.uid,
                            sh.getUserid() + "");
                    req_map_param.put(MConstantUser.UserProperty.user_key,
                            key_server);
                    req_map_param.put("target_uid", vo.getUid());
                    detail_resp = sendReq(MURL_user_all_info, req_map_param);
                    if (null == detail_resp) {
                        handler.obtainMessage(DETAIL_FAIL).sendToTarget();
                    }
                    if (detail_resp.isHasError()) {
                        handler.obtainMessage(DETAIL_FAIL,
                                detail_resp.getErrorDetail()).sendToTarget();

                    } else {

                        handler.obtainMessage(DETAIL_INFO).sendToTarget();
                    }

                }
            });

        } catch (Exception e) {
            handler.obtainMessage(Resp_exception).sendToTarget();

        }
    }

    protected void showNavgationLeftBar(String str) {
        if (!TextUtils.isEmpty(str)) {
            // setValueToView(findViewById(R.id.home_left_btn), str);
            // if ("返回".equals(str)) {
            // setValueToView(findViewById(R.id.home_left_btn), "");
            // findButton(R.id.home_left_btn).setBackgroundResource(R.drawable.back_normal);
            //
            // }
        }
        setValueToView(findViewById(R.id.home_left_btn), "返回");
        findViewById(R.id.home_left_btn).setVisibility(View.VISIBLE);
        /**
         * view_Show(findViewById(R.id.home_left_btn));
         * view_Hide(findViewById(R.id.left_img_btn));
         */

        // view_Show(findViewById(R.id.back_sys_btn_img));

    }

    protected ImageView findImageView(int id) {
        return (ImageView) findViewById(id);

    }

    protected Object parselJson(String res) {
        try {
            JSONObject obj = new JSONObject(res).getJSONObject("data");

            JSONArray arr = obj.getJSONArray("photos");
            String head_url = obj.getString("header");

            int total = obj.getInt("count");
            String url = "", thumb_url = "";
            int pid = 0;
            for (int i = 0; i < arr.length(); i++) {
                VoPhoto vo = new VoPhoto();
                vo.setHead_url(head_url);
                vo.setCountTotal(total);
                pid = arr.getJSONObject(i).getInt("pid");
                url = arr.getJSONObject(i).getString("url");
                thumb_url = arr.getJSONObject(i).getString("thumb_url");
                vo.setPid(pid);
                vo.setThumb_url(thumb_url);
                vo.setUrl(url);
                photos.add(vo);
            }
            if (obj != null) {
                obj = null;
            }
        } catch (Exception e) {
            showToast("获取相册数据失败！");
            e.printStackTrace();
        }

        return null;
    }

    String userid_target = "";

    void getUserImgs() {
        ProShow("");
        poolThread.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    userid_target = vo.getUid();
                    req_map_param.put("key", key_server);
                    req_map_param.put("uid", sh.getUserid() + "");
                    req_map_param.put("target_uid", userid_target);

                    vo_resp = sendReq(MURL_user_gallery_list, req_map_param);
                    if (null == vo_resp) {
                        handler.obtainMessage(Resp_DATA_Empty).sendToTarget();
                    }

                    if (vo_resp.isHasError()) {
                        handler.obtainMessage(Resp_action_fail,
                                vo_resp.getErrorDetail()).sendToTarget();

                    } else {
                        handler.obtainMessage(Resp_action_ok).sendToTarget();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    handler.obtainMessage(Resp_exception).sendToTarget();
                }

            }
        });

    }

    RespVo detail_resp = null;

    protected ExecutorService poolThread = Executors.newFixedThreadPool(2);
    protected Map<String, String> req_map_param = Collections
            .synchronizedMap(new HashMap<String, String>());

    ImageView header = null;
    List<View> views = new ArrayList<View>();
    RequestByPost reqTool = null;

    public RespVo sendReq(String url, Map<String, String> req_map_param) {
        if (reqTool == null) {
            reqTool = new RequestByPost();
        }
        return reqTool.sendPostPhp(url, req_map_param);

    }

    private static final long LOCK_TOUCH_IN_MILLIS = 0;// 120;

    class TouchIsScroll implements OnTouchListener {

        int index_p = 0;
        final Intent in = new Intent();

        public TouchIsScroll(int pos) {
            this.index_p = pos;

        }

        // Lock scroll 100ms

        // Start touch time
        private long touchTime = 0l;
        // Is moving?
        private boolean moving = false;
        String TAG = "TOUCHI--LISTENER";

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(TAG, "down");
                pu_scroll.requestDisallowInterceptTouchEvent(false);
                moving = false;
                touchTime = Calendar.getInstance().getTimeInMillis();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                long moveTime = Calendar.getInstance().getTimeInMillis();
                if (!moving) {
                    Log.d(TAG, "moveTime(" + moveTime + ") - touchTime("
                            + touchTime + ") = " + (moveTime - touchTime));
                    if (moveTime - touchTime > LOCK_TOUCH_IN_MILLIS) {

                    } else {

                        return true;
                    }
                }
                pu_scroll.requestDisallowInterceptTouchEvent(false);
                moving = true;

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(TAG, "up");

                if (moving) {
                    pu_scroll.requestDisallowInterceptTouchEvent(false);
                } else {
                    pu_scroll.requestDisallowInterceptTouchEvent(true);
                    // 判定为点击事件
                    Intent in = new Intent();
                    in.setClass(t_context, TapImg.class);
                    in.putExtra("url", photos.get(index_p).getUrl());
                    BarPushActivity.this.startActivity(in);
                }
                moving = false;
            }
            return true;
        }

    }

    final String data = "data";

    int i = 0;

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int po) {
        po++;
        setValueToView(txtpro, po + "/" + photos.size() + "张");
        if (po < photos.size()) {

        }

    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        ImagePagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.recommend_cell, view,
                    false);
            final ImageView imageView = (ImageView) imageLayout
                    .findViewById(R.id.recom_photo);
            imageLayout.findViewById(R.id.recom_content).setVisibility(
                    View.GONE);
            final ProgressBar spinner = (ProgressBar) imageLayout
                    .findViewById(R.id.loading);
            imageLayout.setOnTouchListener(new TouchIsScroll(position));
            imageLoader.displayImage(photos.get(position).getUrl(), imageView,
                    options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            spinner.setVisibility(View.VISIBLE);
                            if (vo.getSex().equals("2")) {

                                imageView
                                        .setImageResource(R.drawable.female_default);
                            } else {
                                imageView
                                        .setImageResource(R.drawable.man_default);
                            }
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                                    FailReason failReason) {
                            String message = null;
                            switch (failReason.getType()) {
                                case IO_ERROR:
                                    message = "Input/Output error";
                                    break;
                                case DECODING_ERROR:
                                    message = "Image can't be decoded";
                                    break;
                                case NETWORK_DENIED:
                                    message = "Downloads are denied";
                                    break;
                                case OUT_OF_MEMORY:
                                    message = "Out Of Memory error";
                                    break;
                                case UNKNOWN:
                                    message = "Unknown error";
                                    break;
                            }
                            // Toast.makeText(BeautyActivity.this,
                            // message + imageUri, Toast.LENGTH_SHORT)
                            // .show();
                            if (vo.getSex().equals("2")) {

                                imageView
                                        .setImageResource(R.drawable.female_default);
                            } else {
                                imageView
                                        .setImageResource(R.drawable.man_default);
                            }

                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            spinner.setVisibility(View.GONE);
                        }
                    });

            ((ViewPager) view).addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View container) {
        }
    }

    /**
     * @category 问候适配器
     */
    class greetAdapter extends BaseExpandableListAdapter {

        // [倾心暖语, 礼貌回答, 节日祝福, 初次印象, 深度了解, 日常问候]

        String sex = "2";// 2为女，1为男
        int user_sex = 1;
        String[] node_parent = datagreet.getcateKindsToArrayStr();
        String[] node_parent_man = datagreet.getcateKindsToArrayStr();
        String[] node_parent_woman = new String[]{"初次印象", "礼貌回答", "节日祝福",
                "倾心暖语", "日常问候", "深度了解"};

        String[][] node_sons = new String[][]{

                datagreet.getSexSingleDataToString(sex, node_parent[0]),
                datagreet.getSexSingleDataToString(sex, node_parent[1]),
                datagreet.getSexSingleDataToString(sex, node_parent[2]),
                datagreet.getSexSingleDataToString(sex, node_parent[3]),
                datagreet.getSexSingleDataToString(sex, node_parent[4]),
                datagreet.getSexSingleDataToString(sex, node_parent[5])

        };

        public greetAdapter() {
            int userid_sex = ShareUtil.getInstance(t_context).getUserInfo()
                    .getSex();
            NSLoger.Log(userid_sex + "");
            // 性别取反-
            if (ShareUtil.getInstance(t_context).getUserInfo().getSex() == 1) {// 取反
                sex = "1";

                node_parent = node_parent_woman;
                node_sons = new String[][]{

                        datagreet.getSexSingleDataToString(sex,
                                node_parent_woman[0]),
                        datagreet.getSexSingleDataToString(sex,
                                node_parent_woman[1]),
                        datagreet.getSexSingleDataToString(sex,
                                node_parent_woman[2]),
                        datagreet.getSexSingleDataToString(sex,
                                node_parent_woman[3]),
                        datagreet.getSexSingleDataToString(sex,
                                node_parent_woman[4]),
                        datagreet.getSexSingleDataToString(sex,
                                node_parent_woman[5])
                        // datagreet.getSexSingleDataToString(sex, node_parent[5])

                };

                generalsTypes = node_parent;
                generals = node_sons;
            } else {
                sex = "2";
                node_parent = datagreet.getcateKindsToArrayStr();
                node_sons = new String[][]{

                        datagreet.getSexSingleDataToString(sex, node_parent[0]),
                        datagreet.getSexSingleDataToString(sex, node_parent[1]),
                        datagreet.getSexSingleDataToString(sex, node_parent[2]),
                        datagreet.getSexSingleDataToString(sex, node_parent[3]),
                        datagreet.getSexSingleDataToString("1", node_parent[4]),
                        datagreet.getSexSingleDataToString(sex, node_parent[5])

                };

                generalsTypes = node_parent;
                generals = node_sons;
            }
        }

        private String[] generalsTypes = node_parent;// datagreet.getcateKindsToArrayStr()

        public int pos_son_ted = 0;
        public int pos_group_ted = 0;
        public String pos_value = "";

        public String getPos_value() {
            return pos_value;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public void setPos_value(String pos_value) {
            this.pos_value = pos_value;
        }

        public int getPos_group_ted() {
            return pos_group_ted;
        }

        public void setPos_group_ted(int pos_group_ted) {
            this.pos_group_ted = pos_group_ted;
        }

        public int getPos_son_ted() {
            return pos_son_ted;
        }

        public void setPos_son_ted(int pos_son_ted) {
            this.pos_son_ted = pos_son_ted;
        }

        private String[][] generals = node_sons;

        TextView getTextView() {
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, 64);
            TextView textView = new TextView(BarPushActivity.this);
            textView.setLayoutParams(lp);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setPadding(36, 0, 0, 0);
            textView.setTextSize(20);
            textView.setTextColor(Color.BLACK);
            return textView;
        }

        // 重写ExpandableListAdapter中的各个方法
        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return generalsTypes.length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return generalsTypes[groupPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return generals[groupPosition].length;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return generals[groupPosition][childPosition];
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {

            /**
             * ImageView logo = new ImageView(BeautyDetail.this);
             * logo.setImageResource(logos[groupPosition]); logo.setPadding(50,
             * 0, 0, 0); ll.addView(logo);
             */

            LinearLayout ll = new LinearLayout(BarPushActivity.this);
            ll.setOrientation(0);
            TextView textView = getTextView();
            textView.setTextColor(Color.BLACK);
            textView.setText(getGroup(groupPosition).toString());
            ll.addView(textView);

            View parent_cell = LayoutInflater.from(t_context).inflate(
                    R.layout.parent_cell, null);
            TextView txt_ch = (TextView) parent_cell
                    .findViewById(R.id.parent_txt);
            ImageView cell_p = (ImageView) parent_cell
                    .findViewById(R.id.parent_imv);

            if (groupPosition % 2 == 1) {
                parent_cell
                        .setBackgroundResource(R.drawable.selector_item_white);
            } else {
                parent_cell
                        .setBackgroundResource(R.drawable.selector_item_gray);
            }

            if (groupPosition == 0 || groupPosition == 1) {
                cell_p.setBackgroundResource(R.drawable.icon_nj2);
            }
            if (isExpanded) {
                cell_p.setBackgroundResource(R.drawable.icon_nj2);
            } else {

                cell_p.setBackgroundResource(R.drawable.icon_nj1);
            }

            txt_ch.setText(getGroup(groupPosition).toString());

            return parent_cell;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            LinearLayout ll = new LinearLayout(BarPushActivity.this);
            ll.setOrientation(0);
            /**
             * ImageView generallogo = new ImageView( BeautyDetail.this);
             * generallogo
             * .setImageResource(generallogos[groupPosition][childPosition ]);
             * ll.addView(generallogo);
             */
            TextView textView = getTextView();
            textView.setText(getChild(groupPosition, childPosition).toString());
            ll.addView(textView);

            View ch = LayoutInflater.from(t_context).inflate(
                    R.layout.child_cell, null);

            TextView txt_ch = (TextView) ch.findViewById(R.id.child_txt);
            txt_ch.setText(getChild(groupPosition, childPosition).toString());
            ImageView iv = (ImageView) ch.findViewById(R.id.child_imv);
            if (pos_son_ted == childPosition && pos_group_ted == groupPosition) {
                iv.setBackgroundResource(R.drawable.ico_single_p);
            } else {
                iv.setBackgroundResource(R.drawable.ico_single_normal);
            }
            return ch;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (imageLoader != null) {
            imageLoader.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (imageLoader != null) {
            imageLoader.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // pu_scroll.setOnTouchListener(new OnTouchListener() {
        //
        // @Override
        // public boolean onTouch(View v, MotionEvent event) {
        // // TODO Auto-generated method stub
        // return false;
        // }
        // });

        if (imageLoader != null) {
            imageLoader.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {

            // if (medal_data != null) {
            // medal_data = null;
            // }
            if (datagreet != null) {
                NSLoger.Log("--释放XML--数据！--");
                datagreet.Force_Release();
                datagreet = null;
            }
            if (player_instance != null) {

                player_instance.stateRelease();
            }
            if (imageLoader != null) {
                imageLoader.stop();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void btn_ok_no(View v) {
        switch (v.getId()) {
            case R.id.app_no: {
                if (diagpopview != null) {
                    diagpopview.dismiss();
                }

            }

            break;
            case R.id.app_yes: {
                addApplyLookGallery(userid_target);
                if (diagpopview != null) {
                    diagpopview.dismiss();
                }

            }

            break;

            default:
                break;
        }

    }

    // private Toast mCurrentToast;
    @Override
    public void TapScroll(float y) {

        // if (null != mCurrentToast) {
        // mCurrentToast.cancel();
        // }
        //
        // mCurrentToast = Toast.makeText(BarPushActivity.this,
        // "Y--CONPONET"+y, Toast.LENGTH_SHORT);
        // mCurrentToast.show();

    }

    class getDataAsyc extends AsyncTask {
        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (result != null) {

            }
            String re = vo_resp.getResp();
            parselJson(re);

            showTitle(vo.getNickName() == null
                    || "null".equals(vo.getNickName())
                    || TextUtils.isEmpty(vo.getNickName()) ? "用户详情" : vo
                    .getNickName());
            if ((Integer) layLock.getTag() == 2) {
                // 上锁状态
                showLock();
            } else {

                if (photos == null || photos.size() < 1) {
                    setValueToView(txtpro, "0" + "张");
                    View lay = LayoutInflater.from(
                            BarPushActivity.this.getApplicationContext())
                            .inflate(R.layout.imgs_cell, null);
                    header = (ImageView) lay.findViewById(R.id.photo_cell);

                    header.setClickable(false);

                    if (vo.getSex().equals("2")) {
                        header.setImageResource(R.drawable.female_default);

                    } else {
                        header.setImageResource(R.drawable.man_default);
                    }

                    views.add(lay);
                    NewViewPagerAdapter vpAdapter_new = new NewViewPagerAdapter(
                            views);
                    svp.setAdapter(vpAdapter_new);

                    return;
                }

                setValueToView(txtpro, "1/" + photos.size() + "张");

                ImagePagerAdapter adapter = new ImagePagerAdapter();
                svp.setAdapter(adapter);
                svp.setOnPageChangeListener(BarPushActivity.this);

            }

            halfParsel(detail_resp.getResp());
            setViewUser();
            // getMedalInfo();//勋章！！！
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                userid_target = vo.getUid();
                req_map_param.put("key", key_server);
                req_map_param.put("uid", sh.getUserid() + "");
                req_map_param.put("target_uid", userid_target);

                vo_resp = sendReq(MURL_user_gallery_list, req_map_param);
                // if (null == vo_resp) {
                // handler.obtainMessage(Resp_DATA_Empty).sendToTarget();
                // }
                //
                // if (vo_resp.isHasError()) {
                // handler.obtainMessage(Resp_action_fail,
                // vo_resp.getErrorDetail()).sendToTarget();
                //
                // } else {
                // handler.obtainMessage(Resp_action_ok).sendToTarget();
                //
                // }
                return vo_resp.getResp();

            } catch (Exception e) {
                e.printStackTrace();
                // handler.obtainMessage(Resp_exception).sendToTarget();
            }
            return vo_resp.getErrorDetail();
        }

    }

}
