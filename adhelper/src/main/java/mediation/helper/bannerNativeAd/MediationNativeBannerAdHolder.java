package mediation.helper.bannerNativeAd;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mediation.helper.MediationAdHelper;
import mediation.helper.cubiad.NativeAdView.CubiBannerAd;
import mediation.helper.cubiad.NativeAdView.CubiNativeAd;


public class MediationNativeBannerAdHolder extends RecyclerView.ViewHolder {
    MediationNativeBanner nativeBanner;


    //    public MediationNativeBannerAdHolder(ViewGroup itemView, Context context, String app_name, String facebook_ad_key, String admob_ad_key) {
//        super(itemView);
//        nativeBanner = new MediationNativeBanner(itemView, context, app_name, facebook_ad_key, admob_ad_key);
//
//
//    }
    public MediationNativeBannerAdHolder(ViewGroup itemView, Context context, String app_name, String facebook_ad_key, String admob_ad_key, MediationAdHelper.ImageProvider imageProvider) {
        super(itemView);
        nativeBanner = new MediationNativeBanner(itemView, context, app_name, facebook_ad_key, admob_ad_key, imageProvider);

    }

    public MediationNativeBannerAdHolder(ViewGroup itemView, Context context, String app_name, String facebook_ad_key, String admob_ad_key, CubiBannerAd cubiBannerAd, MediationAdHelper.ImageProvider imageProvider) {
        super(itemView);
        nativeBanner = new MediationNativeBanner(itemView, context, app_name, facebook_ad_key, admob_ad_key, cubiBannerAd, imageProvider);

    }

    public void loadFacebookAD(OnNativeBannerListener OnNativeBannerListener) {
        nativeBanner.loadFacebookAD(OnNativeBannerListener);
    }

    public void loadAdmobAD(OnNativeBannerListener OnNativeBannerListener) {
        nativeBanner.loadAdmobAD(OnNativeBannerListener);
    }

    public void loadCubiAd(OnNativeBannerListener onNativeBannerListener) {
        nativeBanner.loadCubiAd(onNativeBannerListener);
    }

    public void loadAD(int adPriority, OnNativeBannerListener OnNativeBannerListener) {
        nativeBanner.loadAD(adPriority, OnNativeBannerListener);
    }

    public void loadAD(Integer[] tempAdPriorityList, OnNativeBannerListener OnNativeBannerListener) {
        nativeBanner.loadAD(tempAdPriorityList, OnNativeBannerListener);
    }

    public void loadAD(ArrayList tempAdPriorityList, OnNativeBannerListener OnNativeBannerListener) {
        nativeBanner.loadAD(tempAdPriorityList, OnNativeBannerListener);
    }

    public void onDestroy() {
        nativeBanner.onDestroy();
    }
}
