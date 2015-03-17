package com.swater.meimeng.activity.newtabMain;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import com.meimeng.app.R;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.swater.meimeng.activity.user.UserGallery;
import com.swater.meimeng.activity.user.UserSetHome;
import com.swater.meimeng.commbase.DiagPopView;
import com.swater.meimeng.commbase.MeiMengApp;
import com.swater.meimeng.commbase.TabBaseTemplate;
import com.swater.meimeng.commbase.DiagPopView.Diag_ok_no;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;

public class TabVip extends TabBaseTemplate implements Diag_ok_no {

    private ImageView imageView1;

    private ImageView imageView2;

    private Button button;

    //屏幕宽度
    private int width;

    //屏幕高度
    private int height;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        t_context = this;
        setContentView(R.layout.fragment_vip);

        int size = (int)Runtime.getRuntime().maxMemory();
        Log.e("可用的最大内存：",""+size);
        TempRun();
    }

    @Override
    public void iniView() {
        bindMenuClick();
        showTitle("尊贵服务");
        findViewById(R.id.btn_vip_call)
                .setOnClickListener(this);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;

        Log.e("屏幕宽度：", "width=" + width);
        Log.e("屏幕高度：", "height=" + height);

        imageView1 = (ImageView) findViewById(R.id.fragment_vip_image_view1);

        imageView2 = (ImageView) findViewById(R.id.fragment_vip_image_view2);

//        Bitmap bitmap =  convertToBitmap();
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.meimeng_service);
        Bitmap bitmap = null;
        bitmap = readBitMap(TabVip.this,R.drawable.meimeng_service2);
        bitmap = zoomBitmap(bitmap);

        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();

        Bitmap bitmap1 = CutBitmap(bitmap, 0, bitmapHeight / 2, bitmapWidth);
        imageView1.setImageBitmap(bitmap1);

        Bitmap bitmap2 = CutBitmap(bitmap, bitmapHeight / 2, bitmapHeight / 2, bitmapWidth);
        imageView2.setImageBitmap(bitmap2);

    }

    /**
     * 缩放图片
     *
     * @return
     */
    public Bitmap zoomBitmap(Bitmap bitmap) {

//        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.meimeng_service2);

        //获得Bitmap的高和宽
        int bmpWidth = bitmap.getWidth();
        Log.e("图片的宽度：bitmapWidth=", "" + bmpWidth);

        int bmpHeight = bitmap.getHeight();
        //设置缩小比例
        double scale = (double) width / bmpWidth;
        Log.e("缩放比例：", "scale = " + scale);
        //计算出这次要缩小的比例
        float scaleWidth = (float) (1 * scale);
        float scaleHeight = (float) (1 * scale);
        //产生resize后的Bitmap对象
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBmp = null;
        resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);

        Log.e("缩放后的图片的宽度：bitmapWidth=", "" + resizeBmp.getWidth());
        return resizeBmp;
    }

    public Bitmap convertToBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        options.inSampleSize = 1 * 1024 * 1024;

        options.inJustDecodeBounds = false;

        options.inDither = false;

        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.meimeng_service, options);

        return bitmap;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * 剪切图片
     *
     * @param bitmap
     * @param startY
     * @param numY
     * @param width
     * @return
     */
    private Bitmap CutBitmap(Bitmap bitmap, int startY, int numY, int width) {
        if (bitmap == null) {
            return null;
        }

        Bitmap b = null;
        b = Bitmap.createBitmap(bitmap, 0, startY, width, numY, null, true);

        return b;
    }

    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

        @Override
    public void bindClick() {

    }

    @Override
    public void onHeader_ItemClick(View v) {
        judgeTypeMyfocus(v, TabVip.this);
        switch (v.getId()) {

            case R.id.gallery_imv:
            case R.id.lay_person: {
                if (MeiMengApp.isVisitor) {
                    MeiMengApp.setVisitorDialogView(TabVip.this, 1);
                } else {
                    jumpOtherActivity(UserSetHome.class);
                }
            }

            break;
            // case R.id.gallery_imv: {
            // jumpOtherActivity(UserGallery.class);
            //
            // }
            // break;

            default:
                break;
        }
    }

    DiagPopView diagpopview = null;

    void popCall() {
        if (diagpopview == null) {
            diagpopview = new DiagPopView(t_context, "拨打服务热线" + "400-8783-520");
            diagpopview.setLeftMsg("确定");
            diagpopview.setOnHeaderItemClick(this);
        }
        diagpopview.showAsDropDown(findViewById(R.id.top));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_img_btn:
            case R.id.home_left_btn: {
//                recycleBitmap();
//                recycleImageView(imageView1);
//                recycleImageView(imageView2);
                this.LeftClickPop(findViewById(R.id.topNav));
            }

            break;
            case R.id.btn_vip_call: {
                popCall();
            }
            break;

            default:
                break;
        }

    }

    void callTel() {

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                + "4008783520"));
        TabVip.this.startActivity(intent);// 内部类
    }

    @Override
    public void btn_ok_no(View v) {
        if (diagpopview != null) {
            diagpopview.dismiss();
        }
        if (v.getId() == R.id.app_yes) {
            callTel();

        }

    }

    /**
     * imageView的回收
     *
     * @param imageView
     */
    private void recycleImageView(ImageView imageView) {
        if (imageView != null) {
            BitmapDrawable bd = (BitmapDrawable) imageView.getDrawable();
            if (bd != null) {
                Bitmap bitmap = bd.getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap = null;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
//        recycleBitmap();
//        recycleImageView(imageView1);
//        recycleImageView(imageView2);
        super.onBackPressed();
    }

//    /**
//     * 回收图片
//     */
//    private void recycleBitmap() {
//        if (bitmap != null && !bitmap.isRecycled()) {
//            bitmap.recycle();
//            bitmap = null;
//        }
//    }
}
