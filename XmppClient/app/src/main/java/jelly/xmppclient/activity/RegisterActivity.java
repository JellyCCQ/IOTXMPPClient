package jelly.xmppclient.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import jelly.xmppclient.R;
import jelly.xmppclient.service.LoginService;
import jelly.xmppclient.service.RegisterService;
import jelly.xmppclient.util.ActivityUtils;
import jelly.xmppclient.util.ConstUtil;
import jelly.xmppclient.util.PreferenceUtil;
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
    private boolean submitForm() {
        if (!validateName()) {
            return false;
        }
        if (!validatePassword()) {
            return false;
        }
        if (!validateConfrimPassword()) {
            return false;
        }
        return true;
    }
    private boolean validatePassword() {
        if (inputpassword.getText().toString().trim().isEmpty()) {
            requestFocus(inputpassword);
            return false;
        }
        return true;
    }
    private boolean validateConfrimPassword() {
        if (confirmpassword.getText().toString().trim().isEmpty()) {
            requestFocus(confirmpassword);
            return false;
        }
        return true;
    }
    private boolean validateName() {
        if (inputname.getText().toString().trim().isEmpty()) {
            requestFocus(inputname);
            return false;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirm:
                if (!submitForm()) {
                    return;
                }
                final String reg_username = inputname.getText().toString().trim();
                final String reg_password = inputpassword.getText().toString().trim();
                final String reg_confirmpassword = confirmpassword.getText().toString().trim();
                PreferenceUtil.putSharePre(this, ConstUtil.REG_KEY_NAME, reg_username);
                PreferenceUtil.putSharePre(this, ConstUtil.REG_KEY_PWD, reg_password);
                PreferenceUtil.putSharePre(this, ConstUtil.REGCONFIRM_KEY_PWD, reg_confirmpassword);
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
                    boolean isSuccess = intent.getBooleanExtra("isRegisterSuccess", false);
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
