package com.swater.meimeng.commbase;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.AlertDialog;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.meimeng.app.R;
import com.swater.meimeng.activity.user.ExitApplication;
import com.swater.meimeng.activity.user.UserEditBase;
import com.swater.meimeng.commbase.HeaderPopMenu.OnHeaderItemClick;
import com.swater.meimeng.database.ShareUtil;
import com.swater.meimeng.database.XmlDataOptions;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.diagutil.Tools;
import com.swater.meimeng.mutils.net.FilterNet;
import com.swater.meimeng.mutils.net.RequestByPost;
import com.swater.meimeng.mutils.net.RespVo;
import com.swater.meimeng.mutils.wheelview.ArrayWheelAdapter;
import com.swater.meimeng.mutils.wheelview.WheelView;

/**
 * @category 定义模板类 封装公用的方法
 */
public abstract class BaseTemplate extends Activity implements TemplateInter {
    protected Context t_context = BaseTemplate.this;
    RequestByPost reqTool = new RequestByPost();
    public RespVo vo_resp = null;
    public XmlDataOptions reader_ops = null;
    protected Dialog wheel_diag = null;
    protected WheelView singleWheelView = null;
    protected View dialogView = null;
    protected ExecutorService poolThread = Executors.newFixedThreadPool(5);
    protected Map<String, String> req_map_param = Collections
            .synchronizedMap(new HashMap<String, String>());
    protected ProgressDialog mpb;
    protected ExitApplication app_exit = ExitApplication.getInstance();
    protected boolean isChangeData = false;
    protected ProgressDialog pd = null;


    /**
     * get the gobal user info
     */
    protected ShareUtil shareUserInfo() {
        return ShareUtil.getInstance(t_context);

    }

    protected void inixmlOpts(Context con) {
        reader_ops = XmlDataOptions.getSingle();// new XmlDataOptions();
        try {
            reader_ops.init(con);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalAccessError("初始化个人数据xml失败！");
        }

    }

    /**
     * @category 绑定Push窗口点击事件
     */
    public void setClick_yes_diag(click_wheel click_yes_diag) {
        this.click_yes_diag = click_yes_diag;
    }

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

    protected void CallWheelDiagCancel() {
        if (null != wheel_diag) {
            if (wheel_diag.isShowing()) {
                wheel_diag.dismiss();
            }
        }

    }

    /**
     * @param view (Button.textview,editview)
     * @return String
     * @category 赋值
     */
    protected void setValueToView(View v, String value) {
        if (null == value) {
            value = "下次告诉你";
        }
        if (null == v) {
            warnNilView();
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

    protected HeaderPopMenu pop_menu = null;

    protected void call_DissMenu() {
        if (pop_menu == null) {
            return;
        } else {
            pop_menu.dismiss();

        }

    }

    protected void ProShow(String str) {

        try {
            View contentView = LayoutInflater.from(t_context).inflate(
                    R.layout.progressdialog, null);
            if (TextUtils.isEmpty(str)) {
                str = "正在拉取数据...";
            }
            setValueToView(contentView.findViewById(R.id.gress_text_view), str);

            mpb = new ProgressDialog(BaseTemplate.this);
            mpb.show();

            mpb.setContentView(contentView);


        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

    }

    protected void ProShow(String str, boolean isnotCancel) {

        try {
            View contentView = LayoutInflater.from(t_context).inflate(
                    R.layout.progressdialog, null);
            if (TextUtils.isEmpty(str)) {
                str = "正在拉取数据...";
            }
            setValueToView(contentView.findViewById(R.id.gress_text_view), str);

            mpb = new ProgressDialog(BaseTemplate.this);
            mpb.show();

            mpb.setContentView(contentView);
            mpb.setCancelable(isnotCancel);


        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

    }

    protected void ProShow(String str, Activity activity) {
        if (TextUtils.isEmpty(str)) {
            str = "正在拉取数据...";
        }
        mpb = new ProgressDialog(activity);
        mpb.show();
        mpb.setContentView(R.layout.progressdialog);

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

    /**
     * @param view (Button.textview,editview)
     * @return String
     * @category 取得文本信息
     */
    protected String getValueView(View v) {
        String value = "";
        if (null == v) {
            warnNilView();

        }
        if (v instanceof Button) {
            value = ((Button) v).getText().toString();
        } else if (v instanceof EditText) {
            value = ((EditText) v).getText().toString();
        } else if (v instanceof TextView) {
            value = ((TextView) v).getText().toString();
        } else {
            warnIllageParam();
        }
        return value == null ? "" : value;
    }

    protected void view_Hide(View... views) {
        for (View v : views) {
            if (judgeView(v) == false) {

                v.setVisibility(View.GONE);
            }
        }
    }

    public RespVo sendReq(String url, Map<String, String> req_map_param) {
        return reqTool.sendPostPhp(url, req_map_param);

    }

    /**
     * @param view view
     * @author by chengshiyang
     * @category 绑定点击事件
     */
    protected void setClickEvent(View... views) {
        for (View param : views) {
            if (param != null) {
                param.setOnClickListener(BaseTemplate.this);
            } else {
                throw new IllegalStateException(BaseTemplate.class.getName()
                        + "setClickEvent--view-null");
            }
        }

    }

    protected void TempRun(String... strings) {
        if (this instanceof Activity) {
            app_exit.addActivity(this);
        }

        req_map_param.clear();
        this.iniView();
        this.bindClick();

    }

    protected boolean isConnectedInternet(Context con) {
        return FilterNet.hasNet(con);

    }

    protected String mg2String(Message msg) {
        return msg.obj == null ? "" : (String) msg.obj;

    }

    protected LinearLayout findLayLinear(int id) {
        return (LinearLayout) findViewById(id);

    }

    protected ImageButton findImagineButton(int id) {
        return (ImageButton) findViewById(id);

    }

    protected ImageView findImageView(int id) {
        return (ImageView) findViewById(id);

    }

    protected void showToat(String msg) {
        Toast.makeText(BaseTemplate.this, msg, 2).show();

    }

    protected RelativeLayout findLayRelative(int id) {
        return (RelativeLayout) findViewById(id);

    }

    protected ListView findListView(int id) {
        ListView t_listview = (ListView) findViewById(id);
        t_listview.setCacheColorHint(0);
        return t_listview;

    }

    protected TextView findText(int id) {
        return (TextView) findViewById(id);

    }

    protected EditText findEditText(int id) {
        return (EditText) findViewById(id);

    }

    protected ScrollView findScroll(int id) {
        return (ScrollView) findViewById(id);

    }

    protected GridView findGridView(int id) {
        GridView t_gridview = (GridView) findViewById(id);
        t_gridview.setCacheColorHint(0);
        return t_gridview;
    }

    protected Button findButton(int id) {
        return (Button) findViewById(id);

    }

    protected boolean judgeView(View v) {
        boolean tag = false;
        if (v == null) {
            tag = true;
            throw new IllegalStateException("VIEW--chengshiyang--传入空对象！");
        }
        return tag;
    }

    protected void view_Show(View... vs) {
        for (View v : vs) {
            if (judgeView(v) == false) {
                v.setVisibility(View.VISIBLE);
            }
        }
    }

    void warnNilView() {
        throw new IllegalStateException("--chengshiyang-->传入空对象"
                + this.getClass().getSimpleName());
    }

    void warnIllageParam() {
        throw new IllegalArgumentException("--chengshiyang--传入参数错误！"
                + this.getClass().getSimpleName());
    }

    protected void showToast(CharSequence s) {
        if (TextUtils.isEmpty(s)) {
            return;

        }

        Toast.makeText(BaseTemplate.this, s, 2).show();

    }

    /**
     * 判断存储卡是否存在
     *
     * @return
     */
    public boolean existSDcard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    protected Object parselJson(String res) {
        return res;

    }

    protected void sysback() {
        onBackPressed();

    }

    protected void showTitle(String str) {
        setValueToView(findViewById(R.id.center_show), str);

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
        view_Show(findViewById(R.id.home_left_btn));
        /**
         * view_Show(findViewById(R.id.home_left_btn));
         * view_Hide(findViewById(R.id.left_img_btn));
         */

        // view_Show(findViewById(R.id.back_sys_btn_img));

    }

    protected void showNavgationRightBar(String str) {
        if (!TextUtils.isEmpty(str)) {
            setValueToView(findViewById(R.id.home_right_btn), str);
        }
        view_Show(findButton(R.id.home_right_btn));

    }

    protected void bindNavgationEvent() {
        setClickEvent(findViewById(R.id.home_left_btn), findViewById(R.id.home_right_btn));
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    protected void jumpOtherActivity(Class<?> cls) {
        Intent in = new Intent(t_context, cls);
        // in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        t_context.startActivity(in);

    }

    protected void jumpOtherActivity(Class<?> cls, Intent src) {
        Intent in = new Intent(t_context, cls);
        in.putExtras(src);
        // in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        t_context.startActivity(in);

    }

    public DisplayMetrics getDisplayScreen() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        // int mScreenWidth = dm.widthPixels;
        return dm;
    }

    /**
     * @category 得到滚轮控件
     */
    public View getDialogView(String name, String[] values) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int mScreenWidth = dm.widthPixels; // 获得屏幕的宽
        View viewMain = LayoutInflater.from(this).inflate(
                R.layout.wheel_activity, null);
        viewMain.setMinimumWidth(mScreenWidth);
        Button btn_city_no = (Button) viewMain.findViewById(R.id.wh_yes);
        Button btn_city_yes = (Button) viewMain.findViewById(R.id.wh_no);
        btn_city_no.setOnClickListener(new DiagBtn_yes_no());
        btn_city_yes.setOnClickListener(new DiagBtn_yes_no());

        TextView nameshow = (TextView) viewMain.findViewById(R.id.showname);
        nameshow.setText(name);

        singleWheelView = (WheelView) viewMain.findViewById(R.id.pro_ac);
        ArrayWheelAdapter<String> ada = new ArrayWheelAdapter<String>(values);

        singleWheelView.setAdapter(ada);
        singleWheelView.setCurrentItem(0);

        return viewMain;
    }

    public void closePool() {
        if (poolThread != null) {
            if (!poolThread.isShutdown()) {
                poolThread.shutdown();
                NSLoger.Log("--关闭线程---》》》");
                System.gc();
            }
        }
        if (t_context != null) {
            t_context = null;
        }
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

    /**
     * @param listview Baseadapter
     * @category 重置list高度
     */
    protected void reComputerListHeight(ListView ls, BaseAdapter ada) {
        int totalHeight = 0;
        if (ls == null || null == ada) {
            warnNilView();
            return;
        }
        for (int i = 0; i < ada.getCount(); i++) {
            View listItem = ada.getView(i, null, ls);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        totalHeight = 80 + totalHeight;
        ViewGroup.LayoutParams params = ls.getLayoutParams();
        params.height = totalHeight
                + (ls.getDividerHeight() * (ada.getCount() - 1));
        ls.setLayoutParams(params);

    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // params.height=params.height+4500;
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

}
