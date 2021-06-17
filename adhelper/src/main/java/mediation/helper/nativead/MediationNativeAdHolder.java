package mediation.helper.nativead;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mediation.helper.MediationAdHelper;
import mediation.helper.cubiad.NativeAdView.CubiNativeAd;
import mediation.helper.cubiad.NativeAdView.NativeCubiAd;


/**
 * Created by TedPark on 16. 5. 19..
 */

public class MediationNativeAdHolder extends RecyclerView.ViewHolder {

    public MediationNativeAd mediationNativeAd;

    public MediationNativeAdHolder(ViewGroup itemView, Context context, String app_name, String facebook_ad_key, String admob_ad_key) {
        super(itemView);
        mediationNativeAd = new MediationNativeAd(itemView, context, app_name, facebook_ad_key, admob_ad_key);
    }


    public MediationNativeAdHolder(ViewGroup itemView, Context context, String app_name, String facebook_ad_key, String admob_ad_key,MediationAdHelper.ImageProvider imageProvider) {
        super(itemView);
        mediationNativeAd = new MediationNativeAd(itemView, context, app_name, facebook_ad_key, admob_ad_key, imageProvider);
    }

    public void loadFacebookAD(OnNativeAdListener onNativeAdListener) {
        mediationNativeAd.loadFacebookAD(onNativeAdListener);
    }
    public void loadCubiAd(OnNativeAdListener onNativeAdListener){
        mediationNativeAd.loadCubiAD(onNativeAdListener);
    }

    public void loadAdmobAD(OnNativeAdListener onNativeAdListener) {
        mediationNativeAd.loadAdmobAD(onNativeAdListener);
    }

    public void loadAD(int adPriority, OnNativeAdListener onNativeAdListener) {
        mediationNativeAd.loadAD(adPriority, onNativeAdListener);
    }

    public void loadAD(Integer[] tempAdPriorityList, OnNativeAdListener onNativeAdListener) {
        mediationNativeAd.loadAD(tempAdPriorityList, onNativeAdListener);
    }

    public void loadAD(ArrayList tempAdPriorityList, OnNativeAdListener onNativeAdListener) {
        mediationNativeAd.loadAD(tempAdPriorityList, onNativeAdListener);
    }

    public void onDestroy(){
        mediationNativeAd.onDestroy();
    }
}