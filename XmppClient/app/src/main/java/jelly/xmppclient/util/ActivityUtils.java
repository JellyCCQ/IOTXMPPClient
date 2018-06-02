package jelly.xmppclient.util;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by 陈超钦 on 2018/5/27.
 */

public class ActivityUtils {
    /**
     * 打开Activity
     * @param activity
     * @param clazz
     */
    public static void startActivity(Activity activity, Class clazz) {
        startActivity(activity,clazz,false);
    }

    /**
     * 打开Activity，并关闭上一个Activity
     * @param activity
     * @param clazz
     * @param isFinish
     */
    public static void startActivity(Activity activity, Class clazz, boolean isFinish) {
        Intent intent = new Intent(activity, clazz);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
    }
}
