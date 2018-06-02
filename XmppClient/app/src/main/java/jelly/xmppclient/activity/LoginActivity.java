package jelly.xmppclient.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import jelly.xmppclient.R;
import jelly.xmppclient.manager.XmppManager;
import jelly.xmppclient.service.LoginService;
import jelly.xmppclient.util.ActivityUtils;
import jelly.xmppclient.util.ConstUtil;
import jelly.xmppclient.util.ToastUtils;

/**
 * Created by 陈超钦 on 2018/5/27.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login;
    private Button register;
    private BroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        login=(Button)findViewById( R.id.but_login );
        register=(Button)findViewById(R.id.but_register);

        login.setOnClickListener( this );
        register.setOnClickListener( this );
        initLonginReceiver();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.but_login:
//                Intent intent = new Intent(this, LoginService.class);
//                startService(intent);
                ActivityUtils.startActivity( this,MainActivity.class,true );
                break;
            case R.id.but_register:
                ActivityUtils.startActivity( this,RegisterActivity.class,true );
                break;
        }
    }

    /**
     * 初始化登录的广播
     */
    private void initLonginReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals( ConstUtil.LOGIN_STATUS)) {

//                    if (catLoadingView.isResumed()) {
//                        catLoadingView.dismiss();
//                    }

                    boolean isLoginSuccess = intent.getBooleanExtra("isLoginSuccess", false);
                    if (isLoginSuccess) {//登录成功
                        ActivityUtils.startActivity(LoginActivity.this, MainActivity.class, true);
                    } else {
                        ToastUtils.showShortToast(context, "登录失败，请检您的网络是否正常以及用户名和密码是否正确");
                    }
                }
            }
        };

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConstUtil.LOGIN_STATUS);
        registerReceiver(receiver, mFilter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
