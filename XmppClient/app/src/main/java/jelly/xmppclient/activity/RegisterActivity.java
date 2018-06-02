package jelly.xmppclient.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;

import jelly.xmppclient.R;
import jelly.xmppclient.listener.CheckConnectionListener;
import jelly.xmppclient.manager.XmppManager;
import jelly.xmppclient.service.LoginService;
import jelly.xmppclient.service.RegisterService;
import jelly.xmppclient.util.ActivityUtils;
import jelly.xmppclient.util.ConstUtil;
import jelly.xmppclient.util.ToastUtils;

/**
 * Created by 陈超钦 on 2018/5/29.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText inputname,inputpassword,confirmpassword;
    private TextView cancle,confirm;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );
        inputname = (EditText) findViewById(R.id.reg_username);
        inputpassword = (EditText) findViewById(R.id.reg_password);
        confirmpassword = (EditText) findViewById(R.id.reg_confirmpassword);

        cancle = (TextView) findViewById(R.id.cancel);
        confirm = (TextView) findViewById(R.id.confirm);

        cancle.setOnClickListener( this );
        confirm.setOnClickListener( this );

        initRegisterReceiver();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirm:
                Intent intent = new Intent(this, RegisterService.class);
                startService(intent);
                break;
            case R.id.cancel:
                ActivityUtils.startActivity( this,LoginActivity.class,true );
                break;
        }
    }
    /**
     * 初始化登录的广播
     */
    private void initRegisterReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals( ConstUtil.REGISTER_STATUS)) {

                    boolean isSuccess = intent.getBooleanExtra("isSuccess", false);
                    if (isSuccess) {//登录成功
                        ToastUtils.showShortToast(context, "注册成功");
                        ActivityUtils.startActivity(RegisterActivity.this, LoginActivity.class, true);
                    } else {
                        ToastUtils.showShortToast(context, "注册失败");
                    }
                }
            }
        };

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConstUtil.REGISTER_STATUS);
        registerReceiver(receiver, mFilter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


}
