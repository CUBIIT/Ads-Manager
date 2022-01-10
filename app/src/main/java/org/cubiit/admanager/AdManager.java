package org.cubiit.admanager;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import mediation.helper.AdHelperApplication;
import mediation.helper.callbacks.OnFetchRemoteCallback;

public class AdManager extends Application {
    String TAG = "de_admanager";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        //this call is must
        AdHelperApplication.getValuesFromConfig( FirebaseRemoteConfig.getInstance(), AdManager.this, new OnFetchRemoteCallback() {
            @Override
            public void onFetchValuesSuccess() {
                Log.d(TAG, "onFetchValuesSuccess: ");
            }

            @Override
            public void onFetchValuesFailed() {
                Log.d(TAG, "onFetchValuesFailed: ");
            }

            @Override
            public void onUpdateSuccess(String appid) {
                Log.d(TAG, "onUpdateSuccess: ");
                updateManifest(appid);
            }
        });
        AdHelperApplication.initMediation(AdManager.this);
        AdHelperApplication.setFirebaseAnalytics(FirebaseAnalytics.getInstance(this));
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


    }
    private void updateManifest(String app_id) {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
            Log.d(TAG, "Name Found: " + myApiKey);
            ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", app_id);
            String ApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
            Log.d(TAG, "ReNamed Found: " + ApiKey);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
    }
}
