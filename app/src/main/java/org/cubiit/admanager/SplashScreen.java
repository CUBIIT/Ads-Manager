package org.cubiit.admanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import mediation.helper.interstitial.MediationAdInterstitial;

import static mediation.helper.util.Constant.KEY_PRIORITY_INTERSTITIAL_AD;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        try{
            getSupportActionBar().hide();
        }catch (Exception ignore){
            ignore.printStackTrace();
        }
        initAds();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this,MainActivity.class));
                finish();
            }
        }, 2000);
    }
    private void initAds(){
        MediationAdInterstitial.initInterstitialAd(false, this, getString(R.string.fb_interstitial_id),
                getString(R.string.admob_interstitial_id),
                KEY_PRIORITY_INTERSTITIAL_AD, null);
    }
}