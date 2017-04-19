package com.edu.hlju.video;

import android.content.Context;
import android.content.res.Resources;

/**
 * 像素转化工具
 * Created by ZXQ20 on 2017/4/19 0019.
 */

public class PixelUtil {
    public static void initContext(Context context){
        mContext=context;
    }
    /**
     * The context
     */
    private static Context mContext;

    /**
     * dp转px
     * @param value the value
     * @return the int
     */
    public static int dp2px(float value){
        final float scale=mContext.getResources().getDisplayMetrics().densityDpi;
        return (int)(value*(scale/160)+0.5f);
    }

    /**
     * px转dp
     * @param value the value
     * @param context the context
     * @return
     */
    public static int dp2px(float value,Context context){
        final float scale=context.getResources().getDisplayMetrics().densityDpi;
        return (int)(value*(scale/160)+0.5f);
    }

    /**
     *sp转px
     * @param value the value
     * @return the int
     */
    public static int sp2px(float value){
        Resources r;
        if(mContext==null){
            r=Resources.getSystem();
        }else{
           r=mContext.getResources();
        }
        float spvalue=value*r.getDisplayMetrics().scaledDensity;
        return (int)(spvalue+0.5f);
    }
    public static int px2sp(float value,Context context){
        final float scale=context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(value/scale+0.5f);
    }
}
