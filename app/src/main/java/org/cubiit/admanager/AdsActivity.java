package org.cubiit.admanager;

import static org.cubiit.admanager.SplashScreen.KEY_PRIORITY_NATIVE_AD_TEST;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

import mediation.helper.MediationAdHelper;
import mediation.helper.banner.MediationAdBanner;
import mediation.helper.banner.OnBannerAdListener;
import mediation.helper.bannerNativeAd.MediationNativeBanner;
import mediation.helper.bannerNativeAd.OnNativeBannerListener;
import mediation.helper.config.PLACEHOLDER;
import mediation.helper.nativead.MediationNativeAd;
import mediation.helper.nativead.OnNativeAdListener;

import static mediation.helper.util.Constant.KEY_PRIORITY_BANNER_AD;
import static mediation.helper.util.Constant.KEY_PRIORITY_NATIVE_AD;

import org.cubiit.admanager.dev.R;

public class AdsActivity extends AppCompatActivity {
    ConstraintLayout ad_container;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        String id = bundle.getString("id");
        try {
            getSupportActionBar().setTitle(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ad_container = findViewById(R.id.ad_container);
        loadAds(id);

    }

    private void loadAds(String id) {
        showDialog();
        if (id.equals(MainActivity.banner)) {
            loadBannerAds();
        } else if (id.equals(MainActivity.nativeAds)) {
            loadNativeAds();
        } else {
            loadNativeBanner();
        }

    }
    public static Integer[] KEY_PRIORITY_BANNER_AD_TEST = new Integer[]{

            MediationAdHelper.AD_CUBI_IT};
    public static Integer[] KEY_PRIORITY_NATIVE_AD_TEST = new Integer[]{
            MediationAdHelper.AD_ADMOB, MediationAdHelper.AD_FACEBOOK,

            MediationAdHelper.AD_CUBI_IT};

    private void loadBannerAds() {


        MediationAdBanner.showBanner(false, PLACEHOLDER.MAIN_ACTIVITY, AdsActivity.this, ad_container,
                KEY_PRIORITY_NATIVE_AD,
                new OnBannerAdListener() {
                    @Override
                    public void onError(String errorMessage) {
                        Log.e("de_t", "onError: " + errorMessage);
                        hideDialog();

                    }

                    @Override
                    public void onLoaded(int adType) {
                        hideDialog();

                    }

                    @Override
                    public void onAdClicked(int adType) {

                    }

                    @Override
                    public void onFacebookAdCreated(com.facebook.ads.AdView facebookBanner) {
                    }
                });

    }

    private void loadNativeAds() {


        MediationNativeAd nativeAd = new MediationNativeAd(false,PLACEHOLDER.MAIN_ACTIVITY, ad_container, this, getString(R.string.app_name),
                (imageView, imageUrl) -> Glide.with(getApplicationContext())
                        .load(imageUrl)
                        .into(imageView));
        nativeAd.loadAD(KEY_PRIORITY_NATIVE_AD_TEST, new OnNativeAdListener() {
            @Override
            public void onError(String errorMessage) {
                Log.e("ads_act", "onError: "+ errorMessage);
                hideDialog();
            }

            @Override
            public void onLoaded(int adType) {

                hideDialog();
            }

            @Override
            public void onAdClicked(int adType) {


            }
        });


    }


    private void loadNativeBanner() {

        MediationNativeBanner nativeAd = new MediationNativeBanner(false,PLACEHOLDER.MAIN_ACTIVITY,ad_container, AdsActivity.this, getString(R.string.app_name), new MediationAdHelper.ImageProvider() {
            @Override
            public void onProvideImage(ImageView imageView, String imageUrl) {
                Glide.with(AdsActivity.this)
                        .load(imageUrl)
                        .into(imageView);
            }
        });
        nativeAd.loadAD(KEY_PRIORITY_NATIVE_AD, new OnNativeBannerListener() {
            @Override
            public void onError(String errorMessage) {
                hideDialog();
                Log.e("ads_act", "onError: "+ errorMessage);

            }

            @Override
            public void onLoaded(int adType) {
                hideDialog();

            }

            @Override
            public void onAdClicked(int adType) {

            }
        });
    }

    private void showDialog() {
        dialog = new ProgressDialog(this);

        dialog.setMessage("loading ads...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void hideDialog() {
        try {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception ignor) {
            ignor.printStackTrace();
        }
    }
}