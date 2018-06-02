package jelly.xmppclient.util;

import android.content.Context;

/**
 * Created by 陈超钦 on 2018/5/27.
 */

public class ConstUtil {
    /**
     * 服务器ip
     */
    public static final String SERVER_HOST = "172.31.73.162";
    /**
     * 服务器端口
     */
    public static final int SERVER_PORT = 5222;
    /**
     * 服务器名字
     */
    public static final String SERVER_NAME = "sc-201707181734";
    /**
     * SharedPreferences 的名字
     */
    public static final String SP_NAME = "iotXmppConfig";
    /**
     * 登陆用户名
     */
    public static final String SP_KEY_NAME = "username";
    /**
     * 登陆密码
     */
    public static final String SP_KEY_PWD = "password";

    /**
     * 登录状态广播过滤器
     */
    public static final String LOGIN_STATUS = "jelly.xmppclient.login_is_success";
    public static final String REGISTER_STATUS="jelly.xmppclient.register_is_success";
    /**
     * 在线
     */
    public static final String ON_LINE = "available";
    /**
     * 在线
     */
    public static final String OFF_LINE = "available";

    /**
     * 获取所有者的jid
     *
     * @return
     */
    public static String getOwnerJid(Context context) {
        return PreferenceUtil.getSharePreStr(context, SP_KEY_NAME) + "@xmpp/Smack";
    }

}