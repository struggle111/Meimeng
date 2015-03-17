package com.swater.meimeng.mutils.OMdown;

import android.util.Log;

/**
 * android.util.Log的封装
 * 
 * @author kenping.liu
 * 
 */
public class ULogger
{
    public static final int LEVEL_ALL   = 0;
    public static final int LEVEL_INFO  = 3;
    public static final int LEVEL_ERROR = 5;

    public static String    TAG         = "unisky";
    public static int       level       = LEVEL_ALL;

    public static void i(String msg)
    {
        if (level <= LEVEL_INFO)
        {
            Log.i(TAG, msg);
        }
    }

    public static void i(Throwable tr)
    {
        if (level <= LEVEL_INFO)
        {
            Log.i(TAG, "", tr);
        }
    }

    public static void i(String msg, Throwable tr)
    {
        if (level <= LEVEL_INFO)
        {
            Log.i(TAG, msg, tr);
        }
    }

    public static void e(String msg)
    {
        if (level <= LEVEL_ERROR)
        {
            Log.e(TAG, msg);
        }
    }

    public static void e(Throwable tr)
    {
        if (level <= LEVEL_ERROR)
        {
            Log.e(TAG, "", tr);
        }
    }

    public static void e(String msg, Throwable tr)
    {
        if (level <= LEVEL_ERROR)
        {
            Log.e(TAG, msg, tr);
        }
    }
}
