package jelly.xmppclient.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import jelly.xmppclient.R;
import jelly.xmppclient.service.LoginService;
import jelly.xmppclient.util.ActivityUtils;
import jelly.xmppclient.util.ConstUtil;
import jelly.xmppclient.util.PreferenceUtil;
import jelly.xmppclient.util.ToastUtils;

/**
 * Created by 陈超钦 on 2018/5/27.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login;
    private Button register;
    private EditText inputUsername, inputPassword;
    private BroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        login=(Button)findViewById( R.id.but_login );
        register=(Button)findViewById(R.id.but_register);
        inputPassword=(EditText)findViewById( R.id.password );
        inputUsername=(EditText)findViewById( R.id.username );
        login.setOnClickListener( this );
        register.setOnClickListener( this );

        String name = PreferenceUtil.getSharePreStr(this, ConstUtil.SP_KEY_NAME);
        String pwd = PreferenceUtil.getSharePreStr(this, ConstUtil.SP_KEY_PWD);
        inputUsername.setText( TextUtils.isEmpty(name) ? "" : name);
        inputPassword.setText(TextUtils.isEmpty(pwd) ? "" : pwd);

        initLonginReceiver();
    }
    private boolean submitForm() {
        if (!validateName()) {
            return false;
        }
        if (!validatePassword()) {
            return false;
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            requestFocus(inputPassword);
            return false;
        }
        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private boolean validateName() {
        if (inputUsername.getText().toString().trim().isEmpty()) {
            requestFocus(inputUsername);
            return false;
        }
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.but_login:
                if (!submitForm()) {
                    return;
                }
                final String username = inputUsername.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                PreferenceUtil.putSharePre(this, ConstUtil.SP_KEY_NAME, username);
                PreferenceUtil.putSharePre(this, ConstUtil.SP_KEY_PWD, password);
                Intent intent = new Intent(this, LoginService.class);
                startService(intent);
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
