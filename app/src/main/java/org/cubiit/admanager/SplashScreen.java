package org.cubiit.admanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import mediation.helper.MediationAdHelper;
import mediation.helper.interstitial.MediationAdInterstitial;
import mediation.helper.nativead.MediationNativeAd;
import mediation.helper.nativead.OnNativeAdListener;

import static mediation.helper.util.Constant.KEY_PRIORITY_INTERSTITIAL_AD;
import static mediation.helper.util.Constant.KEY_PRIORITY_NATIVE_AD;

import com.bumptech.glide.Glide;

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
        loadNative();
        initAds();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this,MainActivity.class));
                finish();
            }
        }, 6000);
    }
    private void initAds(){
        MediationAdInterstitial.initInterstitialAd(false, this,
                KEY_PRIORITY_INTERSTITIAL_AD, null);
    }
    private void loadNative() {
        MediationNativeAd nativeAd = new MediationNativeAd(false, (FrameLayout) findViewById(R.id.native_fram), this, getString(R.string.app_name),
                new MediationAdHelper.ImageProvider() {
                    @Override
                    public void onProvideImage(ImageView imageView, String imageUrl) {
                        Glide.with(getApplicationContext())
                                .load(imageUrl)
                                .into(imageView);
                    }
                });
        nativeAd.loadAD(KEY_PRIORITY_NATIVE_AD, new OnNativeAdListener() {
            @Override
            public void onError(String errorMessage) {
                Log.d("de_", "onError: " + errorMessage);
            }

            @Override
            public void onLoaded(int adType) {

            }

            @Override
            public void onAdClicked(int adType) {

            }
        });

    }
}