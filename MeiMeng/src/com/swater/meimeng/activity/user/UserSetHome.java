package com.swater.meimeng.activity.user;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.widget.*;
import com.swater.meimeng.mutils.sound.recordvoice.AudioRecordFunc;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.User_Ada_Type;
import com.swater.meimeng.activity.adapterGeneral.UserAdapter.cell_lay_click;
import com.swater.meimeng.activity.adapterGeneral.UserAdapterItem;
import com.swater.meimeng.activity.adapterGeneral.vo.AnSwerVo;
import com.swater.meimeng.activity.adapterGeneral.vo.HobbyVo;
import com.swater.meimeng.activity.adapterGeneral.vo.SubVo;
import com.swater.meimeng.activity.adapterGeneral.vo.UserInfo;
import com.swater.meimeng.activity.user.swipepage.SurveyPageActivity;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.commbase.VoiceDiag;
import com.swater.meimeng.commbase.VoiceDiag.OnOverRecordClick;
import com.swater.meimeng.database.ShareUtil.UserVo;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.constant.GeneralUtil;
import com.swater.meimeng.mutils.constant.MConstantUser;
import com.swater.meimeng.mutils.constant.MConstantUser.UserAll_hobby_info;
import com.swater.meimeng.mutils.constant.MConstantUser.UserAll_life_info;
import com.swater.meimeng.mutils.constant.MConstantUser.UserAll_work_info;
import com.swater.meimeng.mutils.diagutil.DialogItem;
import com.swater.meimeng.mutils.mygrid.MyListView;
import com.swater.meimeng.mutils.net.RespVo;
import com.swater.meimeng.mutils.net.UploadNew;
import com.swater.meimeng.mutils.remoteview.RemoteImageView;
import com.swater.meimeng.mutils.sound.ZipUtils;
import com.swater.meimeng.mutils.sound.sundButton.AACPlayer;
import com.swater.meimeng.mutils.sound.sundButton.CanNotRecordException;
import com.swater.meimeng.mutils.sound.sundButton.ExtAudioRecorder;
import com.swater.meimeng.mutils.sound.sundButton.MyAudioRecord;
import com.swater.meimeng.mutils.sound.sundButton.MyAudioTrack;
import com.swater.meimeng.mutils.sound.sundButton.SoundButton;
import com.swater.meimeng.mutils.sound.sundButton.WavVoiceTest;

/**
 * @category 个人资料设置
 */
public class UserSetHome extends BaseTemplate implements OnOverRecordClick,
        cell_lay_click {
    public static final String wav_file_Name = "aa.wav";
    public static final String wav_path = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/meimeng_voice/";
    com.swater.meimeng.mutils.mygrid.MyListView ls_hobby = null;
    String voice_full_path = wav_path + wav_file_Name;
    private File audioFile;
    MyAudioRecord m_recorder;
    /**
     * 封装用户信息
     */
    UserInfo userinfo_vo = null;
    MyAudioTrack m_track;
    private VoiceDiag cd;
    private long time; // 录音时间
    SoundButton btn_sound = null;
    MyListView ls_personlity = null;// 个性描述列表
    UserAdapter adapter_personlity = null;
    UserAdapter adapter_hobby = null;
    List<HobbyVo> arr_hobby = new ArrayList<HobbyVo>();
    List<AnSwerVo> arr_sueveys = new ArrayList<AnSwerVo>();
    RemoteImageView userheader = null;
    protected ArrayList<DialogItem> items_voices = new ArrayList<DialogItem>();

    ImageView imv_voice = null, imv_clear = null;
    boolean isplaying = false;
    AACPlayer player_instance = null;
    // -------------------

    ListView set_ls_baseinfo = null;
    MyListView set_ls_detail_info = null;
    ListView set_ls_appear = null;
    ListView set_ls_workinfo = null;
    ListView set_ls_lifeinfo = null;
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


    private RelativeLayout voiceMonologLayout;

    //表白按钮
    private Button monologButton;

    //录音类
    private AudioRecordFunc audioRecordFunc;

    BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CMD_FRESH_ALL_DATA)) {

                try {
                    if (!UserSetHome.this.isFinishing()) {

                        final Handler handler_refresh = new Handler() {
                            public void handleMessage(Message msg) {
                                if (msg != null) {
                                    if (msg.what == 76) {

                                        player_instance.stateRelease();
                                        TempRun();

                                    }
                                }

                            }

                            ;
                        };
                        handler_refresh.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                arr_hobby.clear();
                                handler_refresh.obtainMessage(76)
                                        .sendToTarget();
                            }
                        }, 200);

                    }
                } catch (Exception e) {
                }

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_set);

        this.TempRun();
        registerReceiver(br, new IntentFilter(CMD_FRESH_ALL_DATA));

    }

    ExtAudioRecorder instance_audio = null;

    void wavBegin() {
        instance_audio = ExtAudioRecorder.getInstanse(true);
        WavVoiceTest.recordChat(instance_audio, wav_path, wav_file_Name);
    }

    void wavEnd() {
        WavVoiceTest.stopRecord(instance_audio);
        voice_upload();
    }

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            ProDimiss();
            switch (msg.what) {
                case 100: {

                    showToast("上传失败!" + mg2String(msg));
                }
                break;
                case Resp_action_ok: {
                    String header_url = shareUserInfo().getUserInfo().getHeader();
                    if (!TextUtils.isEmpty(header_url)
                            && !header_url.equals("null")) {

                        userheader.setUrl(header_url);
                        userheader.setOpenRect(true);
                        userheader.setRect(true);
                        userheader.setRectSize(120);
                    } else {
                        userheader.setBackgroundResource(0);
                        if (shareUserInfo().getUserInfo().getSex() == 2) {
                            userheader.setImageResource(R.drawable.female_head);
                        } else if (shareUserInfo().getUserInfo().getSex() == 1) {

                            userheader.setImageResource(R.drawable.male_head);
                        } else {
                            userheader.setImageResource(R.drawable.default_head);
                        }
                    }
                    parselJson(vo_resp.getResp());
                    setViewUser();

                }

                break;
                case Resp_action_fail: {

                }

                break;
                case -98:
                    try {

                        TempRun();
                    } catch (Exception e) {
                    }
                    break;
                case -13:
                case ACTION_TAG1: {
                    showToast("上传失败!" + mg2String(msg));
                }

                break;
                case ACTION_TAG2: {
                    File file_local = new File(userinfo_vo.getVoice_url_local());
                    if (file_local.isFile() && file_local.exists()) {
                        file_local.delete();

                    }
                    showToast("上传成功！");
                    TempRun();

                }

                break;
                case Resp_exception: {
                    showToast("服务器异常！" + mg2String(msg));

                }

                break;

                default:
                    break;
            }
        }

        ;
    };

    protected Object parselJson(String res) {

        try {

            Log.e("网上获取的数据：",res);

            clearData();
            // iniBaseView();
            // iniDetailView();
            JSONObject obj = new JSONObject(res).getJSONObject("data");

            String voice_url = obj.getJSONObject("voice_description")
                    .getString("url");
            String voice_url_audit = obj.getJSONObject("voice_description")
                    .getString("audit_url");
            if (!TextUtils.isEmpty(voice_url_audit)
                    && !voice_url_audit.equals("null")) {
                userinfo_vo.setVoice_url_audit(voice_url_audit);

            }
            if (!TextUtils.isEmpty(voice_url) && !voice_url.equals("null")) {
                //
                userinfo_vo.setVoice_url(voice_url);
            }
            userinfo_vo.setVoice_url_local(wav_path + wav_file_Name);

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

            userinfo_vo.setHeart_description(desc_self);
            userinfo_vo.setHeight(obj_base_info.getString("height"));
            // "marriage":{"value":"离异","id":2},
            userinfo_vo.setMarriage(obj_base_info.getJSONObject("marriage")
                    .getString("value"));
            userinfo_vo.setId_marriage(obj_base_info.getJSONObject("marriage")
                    .getString("id"));

            String nickName_value = obj_base_info.getJSONObject("nickname")
                    .getString("content");
            String nickName_audit = obj_base_info.getJSONObject("nickname")
                    .getString("audit_content");
            if (!TextUtils.isEmpty(nickName_audit)
                    || !nickName_audit.equals("null")) {
                nickName_value = nickName_audit;

            }
            userinfo_vo.setNickName(nickName_value);
            UserVo uv = shareUserInfo().getUserInfo();
            // .setNickname(nickName_value);
            uv.setNickname(nickName_value);
            shareUserInfo().saveUserInfo(uv);

            // userinfo_vo.setNickName(obj_base_info.getJSONObject("nickname")
            // .getString("content"));
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
            // userinfo_vo.setId_shengxiao(obj_detail.getJSONObject("assets_level").getInt("id")+"");
            // userinfo_vo.setId_xingzuo(obj_detail.getJSONObject("assets_level").getInt("id")+"");

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
                // setValueToView(findViewById(R.id.va4_comp_trade),
                // va_cp_trade);
                // setValueToView(findViewById(R.id.va4_comp_type), va_cp_type);
                // setValueToView(findViewById(R.id.va4_grad_year), grad_year);
                // setValueToView(findViewById(R.id.va4_income_desc), income);
                // setValueToView(findViewById(R.id.va4_job_state),
                // va_workstate);
                // setValueToView(findViewById(R.id.va4_lang), lang);
                // setValueToView(findViewById(R.id.va4_prof_type), va_pro);
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

            JSONObject obj_hobby_info = obj.getJSONObject("hobby_info");
            userinfo_vo.setJson_hobby_info(obj_hobby_info.toString());

            if (obj_hobby_info == null
                    || obj_hobby_info.isNull(UserAll_hobby_info.like_sports)) {
                getHobbyData(null, "喜欢的运动:");

                getHobbyData(null, "喜欢的食物:");
                getHobbyData(null, "喜欢的书籍:");
                getHobbyData(null, "喜欢的音乐:");
                getHobbyData(null, "喜欢的电影:");
                getHobbyData(null, "关注节目:");
                getHobbyData(null, "娱乐休闲:");
                getHobbyData(null, "业余爱好:");
                getHobbyData(null, "喜欢的旅游:");

            } else {

                getHobbyData(
                        obj_hobby_info
                                .getJSONArray(UserAll_hobby_info.like_sports),
                        "喜欢的运动:");

                getHobbyData(
                        obj_hobby_info
                                .getJSONArray(UserAll_hobby_info.like_food),
                        "喜欢的食物:");
                getHobbyData(
                        obj_hobby_info
                                .getJSONArray(UserAll_hobby_info.like_book),
                        "喜欢的书籍:");
                getHobbyData(
                        obj_hobby_info
                                .getJSONArray(UserAll_hobby_info.like_music),
                        "喜欢的音乐:");
                getHobbyData(
                        obj_hobby_info
                                .getJSONArray(UserAll_hobby_info.like_movie),
                        "喜欢的电影:");
                getHobbyData(
                        obj_hobby_info
                                .getJSONArray(UserAll_hobby_info.attention_item),
                        "关注节目:");
                getHobbyData(
                        obj_hobby_info
                                .getJSONArray(UserAll_hobby_info.entertainment),
                        "娱乐休闲:");
                getHobbyData(
                        obj_hobby_info
                                .getJSONArray(UserAll_hobby_info.amateur_like),
                        "业余爱好:");
                getHobbyData(
                        obj_hobby_info.getJSONArray(UserAll_hobby_info.travel),
                        "喜欢的旅游:");
            }

            adapter_hobby.notifyDataSetChanged();
            userinfo_vo.setHobby_data(arr_hobby);
            // -------个性展示
            JSONArray obj_survey = obj.getJSONArray("personality_info");
            arr_sueveys.clear();

            if (GeneralUtil.isNotNullObject(obj_survey)
                    && obj_survey.length() > 0) {
                int length = obj_survey.length();
                NSLoger.Log("--length-->>" + length);
                getAnList(obj_survey);
                adapter_personlity.notifyDataSetChanged();
                String msg = adapter_personlity.getObjs().toString();
                NSLoger.Log(msg + "--msg-->>");
                if (length < 6) {
                    for (int i = length; i < 7; i++) {
                        AnSwerVo asv = new AnSwerVo();

                        asv.setAnswer("暂时保密");

                        switch (i) {
                            case 0: {
                                asv.setType("理想对象");

                            }

                            break;
                            case 1: {
                                asv.setType("婚后生活");

                            }

                            break;
                            case 2: {
                                asv.setType("婚姻期望");

                            }

                            break;
                            case 3: {

                                asv.setType("约会类型");
                            }

                            break;
                            case 4: {
                                asv.setType("生活习惯");

                            }

                            break;
                            case 5: {
                                asv.setType("个性描述");

                            }

                            break;
                            case 6: {

                                asv.setType("爱情观点");
                            }

                            break;

                            default:
                                break;
                        }
                        arr_sueveys.add(asv);
                    }

                }

            } else {

                for (int i = 0; i < 7; i++) {
                    AnSwerVo asv = new AnSwerVo();

                    asv.setAnswer("暂时保密");

                    switch (i) {
                        case 0: {
                            asv.setType("理想对象");

                        }

                        break;
                        case 1: {
                            asv.setType("婚后生活");

                        }

                        break;
                        case 2: {
                            asv.setType("婚姻期望");

                        }

                        break;
                        case 3: {

                            asv.setType("约会类型");
                        }

                        break;
                        case 4: {
                            asv.setType("生活习惯");

                        }

                        break;
                        case 5: {
                            asv.setType("个性描述");

                        }

                        break;
                        case 6: {

                            asv.setType("爱情观点");
                        }

                        break;

                        default:
                            break;
                    }
                    arr_sueveys.add(asv);

                }
                ls_personlity.setAdapter(adapter_personlity);
                adapter_personlity.notifyDataSetChanged();
                // setListViewHeightBasedOnChildren(ls_personlity);

                // reComputerListHeight(ls_personlity, adapter_personlity);
            }
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

    ;

    // [{"type_value":"理想对象","type_id":1,"question":[
    // {"answer":"我希望她是一个理性智慧的人；","option_value":"理性智慧","option_id":2,"question_value":"你希望Ta是一个什么样的人？","type_text":"理想对象","question_id":1,"type_id":1},{"answer":"对方的品质中，谦虚对我来说最重要；","option_value":"谦虚","option_id":2,"question_value":"对方什么品质对你最重要？","type_text":"理想对象","question_id":2,"type_id":1}
    // ,{"answer":"在我寻找另一半时，我最看重容貌身材；","option_value":"容貌身材  ","option_id":1,"question_value":"在你寻找另一半时，对方什么方面对你最重要？","type_text":"理想对象","question_id":3,"type_id":1},
    // {"answer":"假如女朋友太优秀，我也不会有压力，因为优秀的女友带出去很有面子。","option_value":"不会，优秀的女友带出去很有面子","option_id":4,"question_value":"如果女朋友太优秀会感到压力吗？","type_text":"理想对象","question_id":4,"type_id":1}]}
    // ,{"type_value":"婚后生活","type_id":2,"question":[{"answer":"我脑海中的婚后生活是男主外，女主内，男方负责养家，女方负责持家；","option_value":"男主外，女主内，男方负责养家，女方负责持家","option_id":1,"question_value":"你脑海中的婚后生活中是怎样分工的？","type_text":"婚后生活","question_id":1,"type_id":2},{"answer":"我最喜欢开座位很高、四轮驱动的名牌越野车；","option_value":"座位很高、四轮驱动的名牌越野车  ","option_id":2,"question_value":"你最喜欢开哪种类型的小汽车？","type_text":"婚后生活","question_id":2,"type_id":2},
    // {"answer":"对于个人空间的问题，我只要一周有一天属于我自己，其他的时间都和爱人一起；","option_value":"只要一周有一天属于我自己，其他的时间都和爱人一起   ","option_id":3,"question_value":"当你在婚姻中，你希望你的个人空间有多大？","type_text":"婚后生活","question_id":3,"type_id":2},
    // {"answer":"我如果提前退休开始享受生活，我会开着自己的车，游遍全国的名山大川；","option_value":"开着自己的车，游遍全国的名山大川  ","option_id":1,"question_value":"你如果提前退休开始享受生活，你会选择什么样的生活方式？","type_text":"婚后生活","question_id":4,"type_id":2},{"answer":"我理想中的婚礼应该是在大草坪上的西式婚礼，礼服婚纱必不可少，留下永恒记忆。","option_value":"大草坪上的西式婚礼，礼服婚纱必不可少，留下永恒记忆   ","option_id":3,"question_value":"你理想的婚礼是什么样的？","type_text":"婚后生活","question_id":5,"type_id":2}]},{"type_value":"婚姻期望","type_id":3,"question":[{"answer":"有了老婆了，我觉得婚后的家居环境不会比单身汉的时候更差吧；","option_value":"有了老婆了，不会比我单身汉的时候更差吧","option_id":4,"question_value":"你希望婚后的家居环境是怎样的？","type_text":"婚姻期望","question_id":2,"type_id":3}]},
    // {"type_v

    void getAnList(JSONArray arr) {
        try {
            for (int i = 0; i < arr.length(); i++) {
                AnSwerVo asv = new AnSwerVo();
                LinkedList<SubVo> subs = new LinkedList<SubVo>();
                JSONObject cell = arr.getJSONObject(i);
                String name = cell.getString("type_value");
                JSONArray cell_arr = cell.getJSONArray("question");
                StringBuilder sbcell = new StringBuilder("");
                for (int j = 0; j < cell_arr.length(); j++) {
                    SubVo cv = new SubVo();
                    String st = cell_arr.getJSONObject(j).getString("answer");
                    // cv.setOp_id(op_id)
                    cv.setValue(cell_arr.getJSONObject(j).getString(
                            "option_value"));
                    cv.setOp_id(cell_arr.getJSONObject(j)
                            .getString("option_id"));
                    cv.setQues_value(cell_arr.getJSONObject(j).getString(
                            "question_value"));
                    cv.setQues_id(cell_arr.getJSONObject(j).getInt(
                            "question_id")
                            + "");
                    sbcell.append(st + " ");
                    subs.add(cv);

                }
                asv.setType(name);
                asv.setSubs(subs);
                asv.setAnswer(sbcell.toString());
                arr_sueveys.add(asv);

            }
        } catch (Exception e) {
            showToast("读取信息出错！");
        } finally {

        }

    }

    // public class SubVo implements Serializable {
    //
    //
    // }

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
        StringBuilder sb_ids = new StringBuilder("");
        if (arr == null || arr.length() < 1) {
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
                if (i > 0) {
                    sb1.append(",");
                }
                sb1.append(cell.getRightStr());
                sb_ids.append(cell.getId() + ",");
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
                String ids_str = sb_ids.toString();
                if (ids_str.length() > 2) {
                    ids_str = ids_str.substring(0, ids_str.length() - 1);

                }
                hv.setIds(ids_str);
            }

        }
        arr_hobby.add(hv);
        return arr_hobby;

    }

    /**
     * 给view赋值
     */
    private void setViewUser() {
        setValueToView(findViewById(R.id.txt_heart_self),
                userinfo_vo.getHeart_description());
        // clearData();
        // iniBaseView();
        // iniDetailView();
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
                    cell.setRightStr(shareUserInfo().getUserid() + "");
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
        if (userinfo_vo.getSex() == 2) {
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
        for (int i = 0; i < 13; i++) {
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

                        handler.obtainMessage(Resp_action_ok).sendToTarget();
                    }

                }
            });

        } catch (Exception e) {
            handler.obtainMessage(Resp_exception).sendToTarget();

        }
    }

    @Override
    public void iniView() {

        //录音
        audioRecordFunc = AudioRecordFunc.getInstance();

        voiceMonologLayout = (RelativeLayout)findViewById(R.id.user_set_sound_monolog);
        voiceMonologLayout.setOnClickListener(this);

        monologButton = (Button)findViewById(R.id.user_set_monolog_button);
        monologButton.setOnClickListener(this);

        userinfo_vo = new UserInfo();
        showNavgationLeftBar("返回");
        showNavgationRightBar("设置");
        showTitle("个人资料");
        ls_personlity = (MyListView) findViewById(R.id.ls_person);
        userheader = (RemoteImageView) findViewById(R.id.user_head);

        adapter_personlity = new UserAdapter(t_context);
        adapter_personlity.setType(User_Ada_Type.type_personlities);
        adapter_personlity.setClick_cell_event(this);
        btn_sound = (SoundButton) findViewById(R.id.voice_play_btn);
        // ----
        ls_hobby = (com.swater.meimeng.mutils.mygrid.MyListView) findViewById(R.id.ls_hobby);
        adapter_hobby = new UserAdapter(t_context);
        adapter_hobby.setType(User_Ada_Type.type_hobby_cell);
        adapter_hobby.setObjs(arr_hobby);
        adapter_hobby.setClikAble(false);
        ls_hobby.setAdapter(adapter_hobby);
        //

        imv_voice = findImageView(R.id.right_anim);
        imv_clear = findImageView(R.id.cleananim);
        player_instance = AACPlayer.getInstance();
        player_instance.setWaveBg(imv_voice, imv_clear);
        // ----
        // ------
        set_ls_baseinfo = findListView(R.id.set_ls_baseinfo);
        set_ls_lifeinfo = findListView(R.id.set_ls_life_info);
        set_ls_detail_info = (MyListView) findViewById(R.id.set_ls_detailinfo);
        set_ls_appear = findListView(R.id.set_ls_appearance_info);
        set_ls_workinfo = findListView(R.id.set_ls_work_info);

        this.IniDiagShw();
        Inipersonkinds();
        getUserInfo();

    }

    List<UserAdapterItem> arr_personlity = new ArrayList<UserAdapterItem>();

    void Inipersonkinds() {
        for (int i = 0; i < 7; i++) {
            UserAdapterItem cell = new UserAdapterItem();
            switch (i) {
                case 0: {
                    cell.setLeftStr("个性描述");

                }

                break;
                case 1: {
                    cell.setLeftStr("生活习惯");

                }

                break;
                case 2: {
                    cell.setLeftStr("爱情观点");

                }

                break;
                case 3: {
                    cell.setLeftStr("约会类型");

                }

                break;
                case 4: {
                    cell.setLeftStr("婚姻期望");

                }

                break;
                case 5: {

                    cell.setLeftStr("婚后生活");
                }

                break;
                case 6: {
                    cell.setLeftStr("理想对象");

                }

                break;

                default:
                    break;
            }
            arr_personlity.add(cell);

        }
        // adapter_personlity.setObjs(arr_personlity);
        adapter_personlity.setObjs(arr_sueveys);
        //
        ls_personlity.setAdapter(adapter_personlity);
        // // reComputerListHeight(ls_personlity, adapter_personlity);
        //
        // // params.height = totalHeight
        // // + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // // listView.getDividerHeight()获取子项间分隔符占用的高度
        // // params.height最后得到整个ListView完整显示需要的高度
        // reComputerListHeight(ls_personlity, adapter_personlity);
        // ViewGroup.LayoutParams params = ls_personlity.getLayoutParams();
        // // params.height=params.height+1300*4;
        // params.height=params.height;
        // ls_personlity.setLayoutParams(params);
        // setListViewHeightBasedOnChildren(ls_personlity);
    }

    void IniDiagShw() {

        items_voices.add(new DialogItem(R.string.voice_cell_delete,
                R.layout.custom_dialog_normal) {

            @Override
            public void onClick() {
                del_voice();
            }
        });

        items_voices.add(new DialogItem(R.string.voice_cell_record,
                R.layout.custom_dialog_normal) {
            @Override
            public void onClick() {
                super.onClick();

                recordBegin();
                // aacbegin();

            }
        });

        // 取消
        items_voices.add(new DialogItem(R.string.pull_item_cancel,
                R.layout.custom_dialog_cancel));
    }

    void del_voice() {
        ProShow("正在删除语音....");
        poolThread.submit(new Runnable() {

            @Override
            public void run() {

                try {
                    // RequestByPost resp = new RequestByPost();
                    // RespVo vo = resp.uploadVoice(MURL_VOICE_DEL,
                    // voice_full_path, shareUserInfo().getUserid() + "",
                    // key_server);
                    req_map_param.put("key", key_server);
                    req_map_param.put("uid", shareUserInfo().getUserid() + "");
                    RespVo vo = sendReq(MURL_VOICE_DEL, req_map_param);
                    if (vo == null) {
                        handler.obtainMessage(-100).sendToTarget();
                    } else {
                        if (vo.isHasError()) {
                            handler.obtainMessage(-100, vo.getErrorDetail())
                                    .sendToTarget();

                        } else {

                            handler.obtainMessage(-190).sendToTarget();
                        }
                    }
                } catch (Exception e) {
                    handler.obtainMessage(-13).sendToTarget();
                }

            }
        });

    }

    /**
     * RecordAAC recordaac = null;
     * <p/>
     * void aacbegin() { if (recordaac == null) { recordaac = new RecordAAC();
     * recordaac.ini(); recordaac.beginRecord(); // // recordBegin(); }
     * <p/>
     * }
     */

    void recordBegin() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            /**
             * audioFile = new File(MyAudioRecord.AUDIO_RECORD); if
             * (!audioFile.exists()) { File vDirPath =
             * audioFile.getParentFile(); vDirPath.mkdirs(); } m_recorder = new
             * MyAudioRecord(); time = 0; time = System.currentTimeMillis(); try
             * { m_recorder.startRecording(); } catch (CanNotRecordException e)
             * { e.printStackTrace(); showToast("录音异常！"); //
             * Toast.makeText(context, // R.string.device_no_soundmode,
             * 2).show(); }
             */
            if (cd == null) {
                cd = new VoiceDiag(t_context);
                cd.setEvent(UserSetHome.this);
            }
            cd.show();

            //录音开始
            audioRecordFunc.startRecordAndFile();
//            wavBegin();
//            aacbegin();

        } else {
            Toast.makeText(getApplicationContext(), "请插入SD卡后,再使用语音功能!", 1)
                    .show();
        }
    }

    void recordEnd() {
        if (cd != null) {
            if (cd.isShowing()) {
                cd.dismiss();
            }
        }

        int recorderTime = (int) (System.currentTimeMillis() - time) / 1000;

        if (recorderTime >= 2) {
            // 松开事件发生后执行代码的区域
            if (audioFile != null) {
                try {
                    new Thread() {
                        public void run() {
                            try {
                                m_recorder.stopRecording();
                                m_recorder = null;

                                sendVoice();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }

                    }.start();
                } catch (Exception e) {
                }

            }

        } else {
            showToat("录音太短");

        }
    }

    @Override
    public void bindClick() {
        setClickEvent(findButton(R.id.btn_md_heart_word),
//                findButton(R.id.user_set_sound_monolog),
                findButton(R.id.user_edit_work_btn),
                findButton(R.id.user_base_edit), findViewById(R.id.user_head),
                findButton(R.id.user_edit_detail_btn),
                findButton(R.id.user_life_edit),
//                findButton(R.id.voice_play_btn),
                findButton(R.id.edit_hobby_btn),
                findButton(R.id.user_edit_body_btn),
                findViewById(R.id.right_voice_linear));
        bindNavgationEvent();

        //隐藏标题中右边的按钮
        findViewById(R.id.home_right_btn).setVisibility(View.GONE);
    }

    int count_time = 1;

    Intent src_data = new Intent();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_hobby_btn: {
                src_data.putExtra("vo", userinfo_vo);
                jumpOtherActivity(EditPersonHobby.class, src_data);

            }

            break;
            case R.id.home_left_btn: {
                sysback();

            }

            break;

            //播放语音独白
            case R.id.right_voice_linear: {

                // {"result":1,"error":"","data":{"url":"http:\/\/112.124.18.97\/attachment\/albumpic\/278\/voice\/audit.wav.mp3"}}
                // String
                // tu="http://112.124.18.97/attachment/albumpic/278/voice/audit.wav.mp3";
                // File file_local = new File(userinfo_vo.getVoice_url_local());
                // if (file_local.exists()) {
                // player_instance.iniData(userinfo_vo.getVoice_url_local());
                // } else

                try {
                    Log.e("播放语音独白了：","Voice_url_audit="+userinfo_vo.getVoice_url_audit()+"; Voice_url="+userinfo_vo.getVoice_url());

                    if (!TextUtils.isEmpty(userinfo_vo.getVoice_url_audit())) {
                        player_instance.iniData(userinfo_vo.getVoice_url_audit());
                    } else if (TextUtils.isEmpty(userinfo_vo.getVoice_url())
                            || userinfo_vo.getVoice_url().length() < 5
                            || userinfo_vo.getVoice_url().equals("null")) {
                        showToast("该用户暂未上传语音！");

                        return;
                    } else {
                        player_instance.iniData(userinfo_vo.getVoice_url());
                    }

                    // player_instance.iniData(tu);
                    count_time++;

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
                    showToast("读取失败！");
                    Log.e("error", e.getMessage());
                    e.printStackTrace();
                }

            }

            break;
            case R.id.user_edit_detail_btn: {
                src_data.putExtra("vo", userinfo_vo);

                jumpOtherActivity(UserEditDetail.class, src_data);

            }

            break;
            case R.id.user_edit_work_btn: {
                src_data.putExtra("vo", userinfo_vo);

                jumpOtherActivity(EditWorkType.class, src_data);

            }

            break;
            case R.id.user_edit_body_btn: {
                src_data.putExtra("vo", userinfo_vo);

                // jumpOtherActivity(UserEditBodyStyle.class);
                jumpOtherActivity(EditBodyStyle.class, src_data);

            }

            break;
//		case R.id.home_right_btn: {
//			src_data.putExtra("vo", userinfo_vo);
//
//			jumpOtherActivity(UserSettingActivity.class, src_data);
//
//		}
//
//			break;
            case R.id.user_head: {
                jumpOtherActivity(UserGallery.class);
            }

            break;
            case R.id.user_base_edit: {
                src_data.putExtra("vo", userinfo_vo);
                jumpOtherActivity(UserEditBase.class, src_data);
                // jumpOtherActivity(NewUserEditBase.class, src_data);

            }

            break;
//            case R.id.user_set_sound_monolog:
            case R.id.user_set_monolog_button:
            {
                items_voices.clear();
                IniDiagShw();

                this.pullDiag();
            }
            break;
            case R.id.btn_md_heart_word: {
                src_data.putExtra("vo", userinfo_vo);

                jumpOtherActivity(EditUserHeartWord.class, src_data);

            }
            break;
            case R.id.user_life_edit: {
                src_data.putExtra("vo", userinfo_vo);

                jumpOtherActivity(EditLifeInfo.class, src_data);

            }
            break;

            default:
                break;
        }

    }

    Button isrecording = null;

    void iniComment(LinearLayout layout) {
        // voice_btn = (Button) layout.findViewById(R.id.match_voice_button);
        isrecording.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (Environment.getExternalStorageState().equals(
                                Environment.MEDIA_MOUNTED)) {
                            audioFile = new File(MyAudioRecord.AUDIO_RECORD);
                            if (!audioFile.exists()) {
                                File vDirPath = audioFile.getParentFile();
                                vDirPath.mkdirs();
                            }
                            m_recorder = new MyAudioRecord();
                            time = 0;
                            time = System.currentTimeMillis();
                            try {
                                m_recorder.startRecording();
                            } catch (CanNotRecordException e) {
                                e.printStackTrace();
                                showToast("录音异常！");
                                // Toast.makeText(context,
                                // R.string.device_no_soundmode, 2).show();
                            }
                            if (cd == null) {
                                cd = new VoiceDiag(t_context);
                            }
                            cd.show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "请插入SD卡后,再使用语音功能!", 1).show();
                        }
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        // 移动事件发生后执行代码的区域
                        // voice_btn.setBackgroundResource(R.drawable.group_chat_record_on);
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        cd.dismiss();
                        // voice_btn.setBackgroundResource(R.drawable.group_chat_record_off);
                        int recorderTime = (int) (System.currentTimeMillis() - time) / 1000;

                        if (recorderTime >= 2) {
                            // 松开事件发生后执行代码的区域
                            if (audioFile != null) {
                                try {
                                    new Thread() {
                                        public void run() {
                                            try {
                                                m_recorder.stopRecording();
                                                m_recorder = null;

                                                sendVoice();
                                            } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }

                                        }

                                    }.start();
                                } catch (Exception e) {
                                }

                            }

                        } else {
                            showToat("录音太短");

                            break;
                        }
                        break;
                    }
                    default:
                        break;
                }
                return false;
            }
        });
    }

    void sendVoice() {
        byte[] voiceData = null;
        try {
            voiceData = ZipUtils.gZip(ZipUtils.getByte(audioFile
                    .getAbsoluteFile()));
            Log.d("path-->>" + audioFile.getAbsolutePath(), "----->>");
            if (voiceData != null) {
                showToast("success!---");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @category 上传头像
     */
    private void uploadHeader() {
        try {
            // RequestByPost.uploadUserHeader(uploadUrl, fullpath, userid, key)

        } catch (Exception e) {
        }

    }

    void pullDiag() {
        com.swater.meimeng.mutils.diagutil.Tools.createCustomDialog(t_context,
                this.items_voices, R.style.CustomDialogOld);

    }

    /**
     * @category 录音结束点击事件
     */
    @Override
    public void RecordOver(View v) {
        // recordEnd();
        /** recordaac.stopRecord(); */

        //停止录音
//        wavEnd();
        audioRecordFunc.stopRecordAndFile();

        //上传语音
        voice_upload();

        if (cd != null) {
            if (cd.isShowing()) {
                cd.dismiss();
            }
        }
    }

    @Override
    public void Cell_lay_Click(View v, int pos) {

        if (adapter_personlity.getObjs().get(0) instanceof AnSwerVo) {
            // 跳到多选问题调查----理想对象
            AnSwerVo ans = ((AnSwerVo) adapter_personlity.getObjs().get(pos));
            String keyName = ans.getType();
            Intent in = new Intent();
            in.putExtra("name", keyName);
            // in.putExtra("data", null);
            in.putExtra("data", ans);
            in.setClass(t_context, SurveyPageActivity.class);
            t_context.startActivity(in);
        } else if (adapter_personlity.getObjs().get(0) instanceof UserAdapterItem) {
            String keyName = ((UserAdapterItem) adapter_personlity.getObjs()
                    .get(pos)).getLeftStr();
            Intent in = new Intent();
            in.putExtra("name", keyName);
            in.setClass(t_context, SurveyPageActivity.class);
            t_context.startActivity(in);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (br != null) {
            unregisterReceiver(br);
        }
        if (src_data != null) {
            src_data = null;
        }
        if (userinfo_vo != null) {
            userinfo_vo = null;

        }
        if (handler != null) {
            handler = null;
            // handler.removeCallbacks(null);
        }
        count_time = 1;
        player_instance.stateRelease();
        closePool();
    }

    RespVo vo_voice = null;

    void voice_upload() {
        ProShow(" 上传中....");
        poolThread.submit(new Runnable() {

            @Override
            public void run() {

                try {
                    // RequestByPost resp = new RequestByPost();
                    // RespVo vo = resp.uploadVoice(MURL_VOICE_UPLOAD,
                    // voice_full_path, shareUserInfo().getUserid() + "",
                    // key_server);

                    UploadNew instance = UploadNew.getInstance(t_context);
                    instance.setConnection(MURL_VOICE_UPLOAD);
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    BasicNameValuePair bvp1 = new BasicNameValuePair("uid",
                            shareUserInfo().getUserid() + "");
                    // BasicNameValuePair bvp2 = new BasicNameValuePair("data",
                    // voice_full_path);
                    BasicNameValuePair bvp3 = new BasicNameValuePair(
                            "filename",
                            voice_full_path.substring(voice_full_path
                                    .lastIndexOf("/")));

                    Log.e("音频路径：",voice_full_path);

                    params.add(bvp1);
                    // params.add(bvp2);
                    params.add(bvp3);

                    vo_voice = instance.getInputStream(params, voice_full_path);

                    if (vo_voice == null) {
                        handler.obtainMessage(Resp_DATA_Empty).sendToTarget();
                    } else {
                        if (vo_voice.isHasError()) {
                            handler.obtainMessage(ACTION_TAG1,
                                    vo_voice.getErrorDetail()).sendToTarget();

                        } else {
                            // {"result":1,"error":"","data":{"url":"http:\/\/112.124.18.97\/attachment\/albumpic\/278\/voice\/audit.wav.mp3"}}

                            handler.obtainMessage(ACTION_TAG2).sendToTarget();
                        }
                    }
                } catch (Exception e) {
                    handler.obtainMessage(-13).sendToTarget();
                }

            }
        });

    }

}
