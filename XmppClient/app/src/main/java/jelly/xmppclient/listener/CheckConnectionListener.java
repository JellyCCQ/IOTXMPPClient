package jelly.xmppclient.listener;

import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;

import jelly.xmppclient.service.LoginService;

/**
 * Created by 陈超钦 on 2018/5/30.
 */

public class CheckConnectionListener implements ConnectionListener {
    private LoginService context;

    public CheckConnectionListener(LoginService context){
        this.context=context;
    }
    @Override
    public void connectionClosed() {
        Log.e("tt", "connectionClosed");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.e("tt", "connectionClosedOnError = " + e.toString());
    }

    @Override
    public void reconnectingIn(int i) {
        Log.e("tt", "reconnectingIn = "+i);
    }

    @Override
    public void reconnectionSuccessful() {

        Log.e("tt", "reconnectionSuccessful");

    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.e("tt", "reconnectionFailed");
    }
}
