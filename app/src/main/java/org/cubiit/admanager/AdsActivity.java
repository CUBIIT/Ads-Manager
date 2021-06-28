package org.cubiit.admanager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import mediation.helper.MediationAdHelper;
import mediation.helper.banner.MediationAdBanner;
import mediation.helper.banner.OnBannerAdListener;
import mediation.helper.bannerNativeAd.MediationNativeBanner;
import mediation.helper.bannerNativeAd.OnNativeBannerListener;
import mediation.helper.nativead.MediationNativeAd;
import mediation.helper.nativead.OnNativeAdListener;

import static mediation.helper.util.Constant.KEY_PRIORITY_BANNER_AD;
import static mediation.helper.util.Constant.KEY_PRIORITY_NATIVE_AD;

public class AdsActivity extends AppCompatActivity {
    FrameLayout ad_container;
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

    private void loadBannerAds() {
        MediationAdBanner.showBanner(false, AdsActivity.this, ad_container,
                getString(R.string.fb_banner_id),
                getString(R.string.admob_banner_id),
                KEY_PRIORITY_BANNER_AD,
                new OnBannerAdListener() {
                    @Override
                    public void onError(String errorMessage) {
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
        MediationNativeAd nativeAd = new MediationNativeAd(false, ad_container, this, getString(R.string.app_name),
                getString(R.string.fb_native_id),
                getString(R.string.admob_native_id), new MediationAdHelper.ImageProvider() {
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
        MediationNativeBanner nativeAd = new MediationNativeBanner(ad_container, AdsActivity.this, getString(R.string.app_name), getString(R.string.nativeBanner),
                getString(R.string.admob_native_id), new MediationAdHelper.ImageProvider() {
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