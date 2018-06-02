package jelly.xmppclient.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.EditText;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import jelly.xmppclient.activity.LoginActivity;
import jelly.xmppclient.listener.CheckConnectionListener;
import jelly.xmppclient.listener.MsgListener;
import jelly.xmppclient.manager.XmppManager;
import jelly.xmppclient.util.ConstUtil;
import jelly.xmppclient.util.PreferenceUtil;
import jelly.xmppclient.util.ToastUtils;

public class LoginService extends Service {
    private CheckConnectionListener checkConnectionListener;
    private static LoginService mInstance = null;
    private final IBinder binder = new MyBinder();
    private String login_username, login_password;
    private XmppManager xmppmanager;
    private XMPPConnection mXMPPConnection;
//    private MyPacketListener mMyPacketListener;
//    private CheckConnectionListener checkConnectionListener;
    private NotificationManager mNotificationManager;

    public class MyBinder extends Binder {
        public LoginService getService() {
            return LoginService.this;
        }
    }
    public LoginService() {
    }
    public static LoginService getInstance() {
        return mInstance;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        login_username = PreferenceUtil.getSharePreStr(this, ConstUtil.SP_KEY_NAME);
        login_password = PreferenceUtil.getSharePreStr(this, ConstUtil.SP_KEY_PWD);
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE); // 通知
        xmppmanager = XmppManager.getXmppconnectionManager();
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

        if(login_username==null||login_password==null){
            ToastUtils.showLongToast( this,"用户名或密码不能为空" );
        }
        else{
            mXMPPConnection = xmppmanager.initConnection(); // 初始化XMPPConnection
            loginXMPP(); // 登录XMPP
            ChatManager chatmanager = mXMPPConnection.getChatManager();
            chatmanager.addChatListener(new ChatManagerListener() {
                @Override
                public void chatCreated(Chat chat, boolean createdLocally) {
                    chat.addMessageListener(new MsgListener(LoginService.this, mNotificationManager));
                }
            });
        }

    }
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

            mXMPPConnection.login(login_username, login_password);
            if (mXMPPConnection.isAuthenticated()) { // 登录成功
                sendLoginBroadcast(true);
                // 添加xmpp连接监听
                checkConnectionListener = new CheckConnectionListener(this);
                mXMPPConnection.addConnectionListener(checkConnectionListener);
//                // 注册好友状态更新监听
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
//                mXMPPConnection.disconnect();
                mXMPPConnection = null;
            }
            if (xmppmanager != null) {
                xmppmanager = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
