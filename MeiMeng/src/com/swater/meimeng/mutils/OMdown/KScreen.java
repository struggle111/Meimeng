package com.swater.meimeng.mutils.OMdown;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * @author kenping.liu
 * 
 */
public class KScreen
{
    public static float density    = 2.0f;
    public static Point screenSize = new Point(480, 800);

    @SuppressWarnings("deprecation")
    public static void initialize(Context context)
    {
        density = context.getResources().getDisplayMetrics().density;
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        // display.getSize(size);
        screenSize.x = display.getWidth();
        screenSize.y = display.getHeight();
    }

//    @SuppressLint("NewApi")
//    public static boolean hasMenuKey(Context context)
//    {
//        if (KConst.ANDROID_VERSION < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//        {
//            return true;
//        }
//        try
//        {
//            return ViewConfiguration.get(context).hasPermanentMenuKey();
//        }
//        catch (Exception ex)
//        {
//        }
//        return true;
//    }

    /**
     * 获取当前分辨率下指定单位对应的像素大小（根据设备信息）<br>
     * px,dip,sp -> px<br>
     * Paint.setTextSize()单位为px <br>
     * 代码摘自：TextView.setTextSize()
     * 
     * @param unit
     *            TypedValue.COMPLEX_UNIT_*
     * @param size
     * @return
     */
    public static float getRawSize(Context context, int unit, float size)
    {
        // Context c = getContext();
        Resources res = null == context ? Resources.getSystem() : context.getResources();
        return TypedValue.applyDimension(unit, size, res.getDisplayMetrics());
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue)
    {
        return (int) (pxValue / density + 0.5f);
    }

    public static void measureView(View child)
    {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null)
        {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0)
        {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        }
        else
        {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }
}
