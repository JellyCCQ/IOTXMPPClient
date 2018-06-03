package jelly.xmppclient.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;


import jelly.xmppclient.listener.CheckConnectionListener;
import jelly.xmppclient.manager.XmppConnectionManager;
import jelly.xmppclient.util.ConstUtil;
import jelly.xmppclient.util.PreferenceUtil;


public class RegisterService extends Service {
    private static RegisterService mInstance = null;
    private final IBinder binder = new MyBinder();
    private String reg_username, reg_password;
    private XmppConnectionManager mXmppConnectionManager;
    private XMPPConnection mXMPPConnection;
    private CheckConnectionListener checkConnectionListener;
    private NotificationManager mNotificationManager;

    public class MyBinder extends Binder {
        public RegisterService getService() {
            return RegisterService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public static RegisterService getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        reg_username = PreferenceUtil.getSharePreStr(this, ConstUtil.REG_KEY_NAME);
        reg_password = PreferenceUtil.getSharePreStr(this, ConstUtil.REG_KEY_PWD);
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE); // 通知
        mXmppConnectionManager = XmppConnectionManager.getXmppconnectionManager();
        initXMPPTask();

    }

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
        registerXMPP();
    }


    /**
     * 注册XMPP
     */
    void registerXMPP() {
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
                sendRegisterBroadcast(false );
            }
            else if(result.getType()==IQ.Type.RESULT){
                sendRegisterBroadcast(true );
            }else{
                if(result.getError().toString().equalsIgnoreCase("conflict(409)")){
                    sendRegisterBroadcast(false );
                }else{
                    sendRegisterBroadcast(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendRegisterBroadcast(false);
        }
        stopSelf();
    }

    /**
     * 发送登录状态广播
     *
     * @param isRegisterSuccess
     */
    void sendRegisterBroadcast(boolean isRegisterSuccess) {
        Intent intent = new Intent(ConstUtil.REGISTER_STATUS);
        intent.putExtra("isRegisterSuccess", isRegisterSuccess);
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
