package org.cubiit.admanager;

import static mediation.helper.util.Constant.KEY_PRIORITY_INTERSTITIAL_AD;
import static mediation.helper.util.Constant.KEY_PRIORITY_NATIVE_AD;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import mediation.helper.MediationAdHelper;
import mediation.helper.interstitial.MediationAdInterstitial;
import mediation.helper.interstitial.OnInterstitialAdListener;
import mediation.helper.nativead.MediationNativeAd;
import mediation.helper.nativead.OnNativeAdListener;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        try {
            getSupportActionBar().hide();
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        loadNative();
        initAds();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.progressbar).setVisibility(View.GONE);
                findViewById(R.id.getstarted).setVisibility(View.VISIBLE);
            }
        }, 6000);
        findViewById(R.id.getstarted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashScreen.this,MainActivity.class));
                finish();
            }
        });
    }

    public static Integer[] KEY_PRIORITY_INTERSTITIAL_AD_test = new Integer[]{
            MediationAdHelper.AD_CUBI_IT,
            MediationAdHelper.AD_FACEBOOK,
            MediationAdHelper.AD_ADMOB};

    private void showInterstitalAds() {
        try {
            MediationAdInterstitial.showInterstitialAd(false, this,
                    KEY_PRIORITY_INTERSTITIAL_AD_test,
                    new OnInterstitialAdListener() {
                        @Override
                        public void onDismissed(int adType) {


                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.d("de", "onError: " + errorMessage);


                        }

                        @Override
                        public void onLoaded(int adType) {


                        }

                        @Override
                        public void onBeforeAdShow() {
                        }

                        @Override
                        public void onAdClicked(int adType) {

                        }

                        @Override
                        public void onFacebookAdCreated(com.facebook.ads.InterstitialAd facebookFrontAD) {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initAds() {
        MediationAdInterstitial.initInterstitialAd(false, this,
                KEY_PRIORITY_INTERSTITIAL_AD, null);
    }
    public static Integer[] KEY_PRIORITY_NATIVE_AD_TEST = new Integer[]{
            MediationAdHelper.AD_ADMOB};
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
        nativeAd.loadAD(KEY_PRIORITY_NATIVE_AD_TEST, new OnNativeAdListener() {
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