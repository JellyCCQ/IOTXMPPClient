package jelly.xmppclient.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;

import jelly.xmppclient.listener.CheckConnectionListener;
import jelly.xmppclient.listener.MsgListener;
import jelly.xmppclient.manager.XmppConnectionManager;
import jelly.xmppclient.util.ConstUtil;
import jelly.xmppclient.util.PreferenceUtil;
import jelly.xmppclient.util.ToastUtils;

public class LoginService extends Service {
    private static LoginService mInstance = null;
    private final IBinder binder = new MyBinder();
    private String username, password;
    private XmppConnectionManager mXmppConnectionManager;
    private XMPPConnection mXMPPConnection;
//    private MyPacketListener mMyPacketListener;
    private CheckConnectionListener checkConnectionListener;
    private NotificationManager mNotificationManager;

    public class MyBinder extends Binder {
        public LoginService getService() {
            return LoginService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public static LoginService getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        username = PreferenceUtil.getSharePreStr(this, ConstUtil.SP_KEY_NAME);
        password = PreferenceUtil.getSharePreStr(this, ConstUtil.SP_KEY_PWD);
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE); // 通知
        mXmppConnectionManager = XmppConnectionManager.getXmppconnectionManager();
        initXMPPTask();

    }

    /**
     * 初始化xmpp和完成后台登录
     */
    private void initXMPPTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    initXMPP();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化XMPP
     */
    void initXMPP() {

        mXMPPConnection = mXmppConnectionManager.initConnection(); // 初始化XMPPConnection
        loginXMPP(); // 登录XMPP
        ChatManager chatmanager = mXMPPConnection.getChatManager();
        chatmanager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                chat.addMessageListener(new MsgListener(LoginService.this, mNotificationManager));
            }
        });
    }


    /**
     * 登录XMPP
     */
    void loginXMPP() {
        try {
            mXMPPConnection.connect();
            try {
                if (checkConnectionListener != null) {
                    mXMPPConnection.removeConnectionListener(checkConnectionListener);
                    checkConnectionListener = null;
                }
            } catch (Exception e) {
            }

            mXMPPConnection.login(username, password);
            if (mXMPPConnection.isAuthenticated()) { // 登录成功
                sendLoginBroadcast(true);
                // 添加xmpp连接监听
                checkConnectionListener = new CheckConnectionListener(this);
                mXMPPConnection.addConnectionListener(checkConnectionListener);
                // 注册好友状态更新监听
//                mMyPacketListener = new MyPacketListener(this);
//                mXMPPConnection.addPacketListener(mMyPacketListener, null);
            } else {
                sendLoginBroadcast(false);
                stopSelf(); // 如果登录失败，自动销毁Service
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendLoginBroadcast(false);
            stopSelf();
        }
    }

    /**
     * 发送登录状态广播
     *
     * @param isLoginSuccess
     */
    void sendLoginBroadcast(boolean isLoginSuccess) {
        Intent intent = new Intent(ConstUtil.LOGIN_STATUS);
        intent.putExtra("isLoginSuccess", isLoginSuccess);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        if (mNotificationManager != null) {
            mNotificationManager = null;
        }
        try {
            if (mXMPPConnection != null) {
                mXMPPConnection.disconnect();
                mXMPPConnection = null;
            }
            if (mXmppConnectionManager != null) {
                mXmppConnectionManager = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
