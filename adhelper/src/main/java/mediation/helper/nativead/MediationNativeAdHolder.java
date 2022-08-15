package mediation.helper.nativead;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mediation.helper.MediationAdHelper;
import mediation.helper.config.PLACEHOLDER;


/**
 * Created by cubiit
 */

public class MediationNativeAdHolder extends RecyclerView.ViewHolder {

    public MediationNativeAd mediationNativeAd;

    public MediationNativeAdHolder(Boolean b, PLACEHOLDER placeholder, ViewGroup itemView, Context context, String app_name) {
        super(itemView);
        mediationNativeAd = new MediationNativeAd(b,placeholder, itemView, context, app_name,null,null);
    }


    public MediationNativeAdHolder(boolean b, PLACEHOLDER placeholder,ViewGroup itemView, Context context, String app_name, MediationAdHelper.ImageProvider imageProvider) {
        super(itemView);
        mediationNativeAd = new MediationNativeAd(b,placeholder, itemView, context, app_name,  imageProvider);
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