package jelly.xmppclient.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;

import jelly.xmppclient.activity.LoginActivity;
import jelly.xmppclient.activity.RegisterActivity;
import jelly.xmppclient.listener.CheckConnectionListener;
import jelly.xmppclient.listener.MsgListener;
import jelly.xmppclient.manager.XmppManager;
import jelly.xmppclient.util.ActivityUtils;
import jelly.xmppclient.util.ConstUtil;
import jelly.xmppclient.util.PreferenceUtil;
import jelly.xmppclient.util.ToastUtils;

public class RegisterService extends Service {
    private CheckConnectionListener checkConnectionListener;
    private static RegisterService mInstance = null;
    private final IBinder binder = new RegisterService.MyBinder();
    private String reg_username, reg_password, reg_comfirmpassword;
    private XmppManager xmppmanager;
    private XMPPConnection mXMPPConnection;
    //    private MyPacketListener mMyPacketListener;
//    private CheckConnectionListener checkConnectionListener;
    private NotificationManager mNotificationManager;

    public class MyBinder extends Binder {
        public RegisterService getService() {
            return RegisterService.this;
        }
    }
    public RegisterService() {
    }
    public static RegisterService getInstance() {
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
        reg_username = PreferenceUtil.getSharePreStr(this, "reg_username");
        reg_password = PreferenceUtil.getSharePreStr(this, "reg_password");
        reg_comfirmpassword = PreferenceUtil.getSharePreStr( this,"reg_confirmpassword" );
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

        if(reg_username==null||reg_password==null||reg_comfirmpassword==null){
            ToastUtils.showLongToast( this,"用户名或密码不能为空" );
        }
        else if(!reg_password.equals( reg_comfirmpassword )){
            ToastUtils.showLongToast( this,"您两次输入的密码不相等" );
        }
        else{
            mXMPPConnection = xmppmanager.initConnection(); // 初始化XMPPConnection
            register(); // 登录XMPP
        }

    }
    void register() {
        try {
            mXMPPConnection.connect();
            try {
                if (checkConnectionListener != null) {
                    mXMPPConnection.removeConnectionListener(checkConnectionListener);
                    checkConnectionListener = null;
                }
            } catch (Exception e) {
            }

            //设置注册所需要的信息
            Registration registration = new Registration();
            registration.setType( IQ.Type.SET);
            registration.setTo(mXMPPConnection.getServiceName());
            registration.setUsername(reg_username);
            registration.setPassword(reg_password);

            //PacketFilter:包过滤类,过滤一些不用的包
            PacketFilter filter = new AndFilter(new PacketIDFilter(registration.getPacketID()), new PacketTypeFilter(IQ.class));
            PacketCollector collector = mXMPPConnection.createPacketCollector(filter);
            // 向服务器端，发送注册Packet包，注意其中Registration是Packet的子类
            mXMPPConnection.sendPacket(registration);
            IQ result = (IQ) collector.nextResult( SmackConfiguration.getPacketReplyTimeout());
            collector.cancel(); //停止请求result
            if(result==null) {
                sendBroadcast(false );
            }
            else if(result.getType()==IQ.Type.RESULT){
                sendBroadcast(true );
            }else{
                if(result.getError().toString().equalsIgnoreCase("conflict(409)")){
                    sendBroadcast(false );
                }else{
                    sendBroadcast(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendBroadcast(false);
            stopSelf();
        }
    }


    void sendBroadcast(boolean isSuccess) {
        Intent intent = new Intent(ConstUtil.REGISTER_STATUS);
        intent.putExtra("isSuccess", isSuccess);
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
            if (xmppmanager != null) {
                xmppmanager = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
