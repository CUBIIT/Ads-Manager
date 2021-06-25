package org.cubiit.admanager;

import android.app.Application;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import mediation.helper.AdHelperApplication;

public class AdManager extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //this call is must
        AdHelperApplication.getValuesFromConfig(FirebaseRemoteConfig.getInstance(), AdManager.this);
        AdHelperApplication.initMediation(AdManager.this);
    }
}
