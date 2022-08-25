package mediation.helper.banner;

import static mediation.helper.AdHelperApplication.applyLimitOnAdmob;
import static mediation.helper.AdHelperApplication.fbRequestBannerFaild;
import static mediation.helper.AdHelperApplication.getCubiBannerAd;
import static mediation.helper.TestAdIDs.TEST_ADMOB_BANNER_ID;
import static mediation.helper.TestAdIDs.TEST_FB_BANNER_ID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import mediation.helper.AdHelperApplication;
import mediation.helper.IUtils;
import mediation.helper.MediationAdHelper;
import mediation.helper.R;
import mediation.helper.config.AdSessions;
import mediation.helper.config.PLACEHOLDER;
import mediation.helper.cubiad.NativeAdView.CubiBannerAd;
import mediation.helper.util.Constant;

/**
 * Created by CUBI-IT
 */

public class MediationAdBanner {

    private static WeakReference <Activity> activityRef;
    private static OnBannerAdListener onBannerAdListener;
    private static String facebookKey;
    private static String admobKey;
    private static ViewGroup bannerContainer;
    private static ArrayList <Integer> adPriorityList;

    private static CubiBannerAd cubiBannerAd;

    public static void showFacebookBanner(boolean isPurchased,PLACEHOLDER placeholder, Activity activity, ViewGroup bannerContainer, OnBannerAdListener onBannerAdListener) {
        showBanner(isPurchased,placeholder, activity, bannerContainer, new Integer[]{MediationAdHelper.AD_FACEBOOK}, onBannerAdListener);
    }

    public static void showAdmobBanner(boolean isPurchased,PLACEHOLDER placeholder, Activity activity, ViewGroup bannerContainer, OnBannerAdListener onBannerAdListener) {
        showBanner(isPurchased,placeholder, activity, bannerContainer, new Integer[]{MediationAdHelper.AD_ADMOB}, onBannerAdListener);
    }

    public static void showBanner(boolean isPurchased,PLACEHOLDER placeholder, ViewGroup bannerContainer, int adPriority, OnBannerAdListener onBannerAdListener) {
        if (isPurchased) {
            onBannerAdListener.onError("You have pro version");
          
            return;
        }
       
        MediationAdBanner.cubiBannerAd = getCubiBannerAd();
        Integer[] tempAdPriorityList = new Integer[3];
        tempAdPriorityList[0] = adPriority;

        if (adPriority == MediationAdHelper.AD_FACEBOOK) {
            tempAdPriorityList[1] = MediationAdHelper.AD_ADMOB;
            tempAdPriorityList[2] = MediationAdHelper.AD_CUBI_IT;
        } else if (adPriority == MediationAdHelper.AD_ADMOB) {
            tempAdPriorityList[1] = MediationAdHelper.AD_FACEBOOK;
            tempAdPriorityList[2] = MediationAdHelper.AD_CUBI_IT;
        } else {
            tempAdPriorityList[1] = MediationAdHelper.AD_FACEBOOK;
            tempAdPriorityList[2] = MediationAdHelper.AD_ADMOB;
        }

        //showBanner(bannerContainer, facebookKey, admobKey, tempAdPriorityList, onBannerAdListener);
        showBanner(bannerContainer, placeholder,tempAdPriorityList, onBannerAdListener);
    }

    @SuppressLint("StaticFieldLeak")
    private static Activity mActivity;

    public static void showBanner(boolean isPurchased,PLACEHOLDER placeholder, Activity activity, ViewGroup bannerContainer, Integer[] tempAdPriorityList, OnBannerAdListener onBannerAdListener) {
        if (isPurchased) {
            onBannerAdListener.onError("You have pro version");
           
            return;
        }
         
        mActivity = activity;
        MediationAdBanner.facebookKey = TEST_FB_BANNER_ID;
        MediationAdBanner.admobKey = TEST_ADMOB_BANNER_ID;
        if (!AdHelperApplication.getTestMode()) {
            //check if ids or test skip
            if (AdHelperApplication.getAdIDs() != null) {
                if (!checkTestIds(onBannerAdListener))
                    return;
                MediationAdBanner.facebookKey = AdHelperApplication.getAdIDs().getFb_banner_id();
                MediationAdBanner.admobKey = AdHelperApplication.getAdIDs().getAdmob_banner_id();
            }
        }
        Log.d("de_", "showBanner:move forward :)) ");
        MediationAdBanner.activityRef = new WeakReference <>(activity);
        MediationAdBanner.cubiBannerAd = getCubiBannerAd();

        // showBanner(bannerContainer, facebookKey, admobKey, tempAdPriorityList, onBannerAdListener);
        showBanner(bannerContainer,placeholder, tempAdPriorityList, onBannerAdListener);
    }
    private static String findValueInMap(String key, Map<String, String> map) {
        key = key.toUpperCase(Locale.ROOT);
        Log.d(TAG_, "findValueInMap: key "+ key);
        String value = "default";
        if(map==null){
            Log.e(TAG_, "findValueInMapInterstitial: map is null" );
        }else {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Log.i(TAG_, "findValueInMap: "+ entry.getKey());
                if (entry.getKey().equals(key.toUpperCase(Locale.ROOT))) {
                    value = entry.getValue();
                    Log.d(TAG_, "findValueInMap:find "+ value);
                    break;

                }
            }
        }
        Log.d(TAG_, "findValueInMap:value  " + value);
        return value;

    }
static String TAG = "de_banner";
static String TAG_ = "de_banner";
    private static Integer[] getPriorityAgainstPlaceHolder(PLACEHOLDER placeholder, Integer[] tempAdPriorityList) {
        Log.d(TAG, "getPriorityAgainstPlaceHolder:size "+tempAdPriorityList.length);
        if (placeholder.toString().toUpperCase(Locale.ROOT).equals(PLACEHOLDER.DEFAULT)) {

            return tempAdPriorityList;
        } else {
            if(AdHelperApplication.placeholderConfig!=null) {
                Integer[] rearrange = new Integer[3];
                String value = findValueInMap(placeholder.name().toLowerCase(Locale.ROOT).toString(), AdHelperApplication.placeholderConfig.banner).toLowerCase(Locale.ROOT);

                if (value.equals("admob") || value.equals("1") || value.equals("01")) {
                    rearrange[0] = MediationAdHelper.AD_ADMOB;
                    rearrange[1] = MediationAdHelper.AD_FACEBOOK;
                    rearrange[2] = MediationAdHelper.AD_CUBI_IT;
                } else if (value.equals("fb") || value.equals("facebook") || value.equals("2") || value.equals("02")) {
                    rearrange[0] = MediationAdHelper.AD_FACEBOOK;
                    rearrange[1] = MediationAdHelper.AD_ADMOB;
                    rearrange[2] = MediationAdHelper.AD_CUBI_IT;
                } else if(value.equals("default") || value.equals("DEFAULT") || value.equals("-1")){
                    rearrange = tempAdPriorityList;
                }
                // Log.d(TAG_, "getPriorityAgainstPlaceHolder: " + rearrange);
                return rearrange;
            }else{
                return tempAdPriorityList;
            }
        }
    }

    private static boolean checkTestIds(OnBannerAdListener onBannerAdListener) {
        Log.d("de_ch", "checkTestIds: ad" + AdHelperApplication.getAdIDs().getAdmob_banner_id() + " Fac: " + AdHelperApplication.getAdIDs().getFb_banner_id());
        if(!(AdHelperApplication.getAdIDs().getFb_banner_id().isEmpty() && AdHelperApplication.getAdIDs().getAdmob_banner_id().isEmpty()))
        {
            if (AdHelperApplication.getAdIDs().getFb_banner_id().equals(TEST_FB_BANNER_ID) && AdHelperApplication.getAdIDs().getAdmob_banner_id().equals(TEST_ADMOB_BANNER_ID)) {
                onBannerAdListener.onError("Found Test IDS..");
                 
                return false;
            }else {
                return true;
            }
        }else{
             
            onBannerAdListener.onError("No ids found");
            return false;
        }

    }

    public static void showBanner(ViewGroup bannerContainer,PLACEHOLDER placeholder, Integer[] tempAdPriorityList, final OnBannerAdListener onBannerAdListener) {
        try {
            //check if add is off


            if(isAddOff(findValueInMap(placeholder.name().toLowerCase(Locale.ROOT).toString(), AdHelperApplication.placeholderConfig.banner).toLowerCase(Locale.ROOT))){
                Log.d(Constant.TAG, "showBanner: is off on this place holder");
                onBannerAdListener.onError("showBanner: is off on this place holder");
                 
                return;
            }
            MediationAdBanner.adPriorityList = new ArrayList <>(Arrays.asList(getPriorityAgainstPlaceHolder(placeholder,tempAdPriorityList)));

            MediationAdBanner.onBannerAdListener = onBannerAdListener;
            MediationAdBanner.bannerContainer = bannerContainer;
            if (bannerContainer == null) {
                onBannerAdListener.onError("BannerContainer can not null");
                 
                return;
            }
            Log.d("de_banner", String.format("Banner Ids:----Facebook: %s -------Admob: %s",facebookKey,admobKey));
            selectAd();
        } catch (Exception e) {
            if (onBannerAdListener != null) {
                 
                onBannerAdListener.onError("");
            }

        }
    }

    private static void selectAd() {
        Log.d(MediationAdHelper.TAG, "selectAd: ");
        int adPriority = adPriorityList.remove(0);
        switch (adPriority) {
            case MediationAdHelper.AD_FACEBOOK:
                showFacebookBanner();
                break;
            case MediationAdHelper.AD_ADMOB:
                showAdmobBanner();
                break;
            case MediationAdHelper.AD_CUBI_IT:
                selectCubiAd();
                break;
            default: {
                 
                onBannerAdListener.onError("You have to select priority type ADMOB or FACEBOOK");
            }

        }
    }

    private static boolean isPkgInstalledAlready() {
        //test send null
       //  cubiBannerAd = null;
      cubiBannerAd = getCubiBannerAd();
        Context context = new AdHelperApplication().getContext();
        if (cubiBannerAd == null || context == null) {
            Log.d("TAG1_NativeBanner", "isPkgInstalledAlready: context or cubiBannerad are null");
            return true;//
        }
        if (IUtils.isContainPkg(cubiBannerAd.getBannerAdUrlLink())) {
            if (IUtils.isPackageInstalled(IUtils.getPackageName(cubiBannerAd.getBannerAdUrlLink()), activityRef.get())) {
                Log.d("TAG1_nativeBanner", "isPkgInstalledAlready: true ");
                return true;
            } else {
                Log.d("TAG1_nativeBanner", "isPkgInstalledAlready: true ");
                return false;
            }
        } else
            return false;

    }


    private static void selectCubiAd() {
        Log.d("DEBUG", "initView");
        //for test
        try {
            if (isPkgInstalledAlready()) {
                 
                onBannerAdListener.onError("CubiBannerAd already installed");
                MediationAdBanner.onError("CubiBannerAd already installed");
                Log.d("TAG1_banner", "selectCubiAdBanner:  pkg already installed");
                return;

            }
            bannerContainer.removeAllViews();
            View bannerView = LayoutInflater.from(bannerContainer.getContext()).inflate(R.layout.layout_cubi_banner_ad, bannerContainer, false);
            ConstraintLayout parentConstraintView = (ConstraintLayout) bannerView.findViewById(R.id.parent_native_banner_constraint_layout);
            ConstraintLayout view_container = (ConstraintLayout) bannerView.findViewById(R.id.view_container_native_banner);
            // Create native UI using the ad metadata.
            Button native_banner_ad_calltoaction = (Button) bannerView.findViewById(R.id.ad_call_to_action);
            TextView native_banner_ad_title = (TextView) bannerView.findViewById(R.id.native_ad_title);
            TextView native_banner_ad_body = (TextView) bannerView.findViewById(R.id.native_banner_ad_body_text);
            ImageView cubi_banner_square_icon = (ImageView) bannerView.findViewById(R.id.cubi_banner_square_icon);
            TextView native_banner_ad_sponser_label = (TextView) bannerView.findViewById(R.id.native_ad_sponsored_label);
            bannerContainer.addView(bannerView);

            if (cubiBannerAd == null || cubiBannerAd.getBannerAdUrlLink() == null || cubiBannerAd.getBannerAdUrlLink().isEmpty()) {
                parentConstraintView.setVisibility(View.GONE);
                MediationAdBanner.onError("CubiAd is null");
                 
                onBannerAdListener.onError("CubiAd is null");
                return;
            }
            native_banner_ad_title.setText(cubiBannerAd.getBannerAdtitle());
            native_banner_ad_body.setText(cubiBannerAd.getBannerAdbodyText());
            native_banner_ad_calltoaction.setText(cubiBannerAd.getBannerAdcallToActionData());
            Glide.with(bannerContainer.getContext().getApplicationContext())
                    .load(cubiBannerAd.getBannerSquareIconUrl())
                    .centerCrop()
                    .into(cubi_banner_square_icon);
            native_banner_ad_calltoaction.setText(cubiBannerAd.getBannerAdcallToActionData());
            native_banner_ad_calltoaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {try{
                   
                    onBannerAdListener.onAdClicked(3);
                    actionOnCubiAdClicked();}catch (Exception e){
                    e.printStackTrace();
                }
                }
            });
            cubi_banner_square_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        
                        onBannerAdListener.onAdClicked(3);
                        actionOnCubiAdClicked();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            native_banner_ad_sponser_label.setText(cubiBannerAd.getBannerAdadvertiserName());
            onBannerAdListener.onLoaded(3);
           

        } catch (Exception e) {
            MediationAdBanner.onError(e.getMessage());
            e.printStackTrace();
        }
    }


    private static void actionOnCubiAdClicked() {
        String url = cubiBannerAd.getBannerAdUrlLink();
        if (url.isEmpty()) {
             
            onBannerAdListener.onError("Url is empty");
            return;
        }
       /* String packageName = "";
        //split package name
        if (url.contains("play.google.com/store/apps")) {
            Log.d("de_", "actionOnCubiAdClicked: contains");
            String[] a = url.split("=");
            packageName = a[1];
        } else {
            packageName = url;
        }
        Log.d("de_", "actionOnCubiAdClicked: " + packageName);
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);*/

        try {
            if (mActivity != null) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                mActivity.startActivity(i);
//                mActivity.startActivity(new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            } else {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                bannerContainer.getContext().startActivity(i);
//                bannerContainer.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }

    private static void showFacebookBanner() {
        Log.d(MediationAdHelper.TAG, "show faaceboookBanner");
        if (MediationAdHelper.isSkipFacebookAd(bannerContainer.getContext())) {
             
            onBannerAdListener.onError(Constant.ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED);
            MediationAdBanner.onError(Constant.ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED);
            return;
        }
        if(AdHelperApplication.getAdIDs().getFb_banner_id().isEmpty() || AdHelperApplication.getAdIDs().getFb_banner_id().equals(TEST_FB_BANNER_ID)){
            if(!AdHelperApplication.getTestMode()) {
                Log.d(MediationAdHelper.TAG, "[ADMOB NATIVE BANNER AD]InstallAd Load");
                Log.e(MediationAdHelper.TAG, "[ADMOB NATIVE BANNER AD]Error:  IDS null or test found");
                 
                onBannerAdListener.onError("NULL OR TEST IDS FOUND");
                MediationAdBanner.onError("NULL OR TEST IDS FOUND");
                return;
            }
        }
        if(AdHelperApplication.fbRequestBannerFaild >= Constant.findIntegerValueInMap(AdSessions.banner_session.name(), AdHelperApplication.sessionConfig.fb_sessions)){
             
            onBannerAdListener.onError("Banner Sesssion out");
            MediationAdBanner.onError("Banner Sesssion out");
            AdHelperApplication.fbRequestBannerFaild++;
            return;
        }


        final com.facebook.ads.AdView facebookBanner = new com.facebook.ads.AdView(bannerContainer.getContext(), facebookKey, AdSize.BANNER_HEIGHT_50);

        if (onBannerAdListener != null) {
            onBannerAdListener.onFacebookAdCreated(facebookBanner);
        }

        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                 
                MediationAdBanner.onError(adError.getErrorMessage());
                Log.d(MediationAdHelper.TAG, "[FACEBOOK BANNER]error: " + adError.getErrorMessage());
                fbRequestBannerFaild++;
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK BANNER]Loaded");
                bannerContainer.removeAllViews();
                ViewGroup parentView = ((ViewGroup) facebookBanner.getParent());
                if (parentView != null) {
                    parentView.removeAllViews();
                }

                bannerContainer.addView(facebookBanner);

                if (onBannerAdListener != null) {
                  
                    onBannerAdListener.onLoaded(MediationAdHelper.AD_FACEBOOK);
                }

            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK BANNER]Clicked");
                if (onBannerAdListener != null) {
                   
                    onBannerAdListener.onAdClicked(MediationAdHelper.AD_FACEBOOK);
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }

        };
        // Request an ad
        facebookBanner.loadAd(facebookBanner.buildLoadAdConfig().withAdListener(adListener).build());

    }
    public static  boolean isAddOff(String value) {
        if (value.equals("off") || value.equals("OFF") || value.equals("Off") || value.equals("of") || value.equals("0") || value.equals("FALSE")) {
            return true;
        } else {
            return false;
        }
    }
    private static void showAdmobBanner() {
        if(AdHelperApplication.admobRequestBannerFaild >= Constant.findIntegerValueInMap(AdSessions.banner_session.name(), AdHelperApplication.sessionConfig.admob_sessions)){
             
            onBannerAdListener.onError("NULL OR TEST IDS FOUND");
            MediationAdBanner.onError("NULL OR TEST IDS FOUND");
            AdHelperApplication.admobRequestBannerFaild++;
            return;
        }
        //check if admob in limit or not
        if(AdHelperApplication.isAdmobInLimit()){
            if(applyLimitOnAdmob){
                 
                onBannerAdListener.onError("Admob in limit & ads limited in current session");
                MediationAdBanner.onError("Admob in limit & ads limited in current session");
                return;
            }
        }
        if(admobKey.isEmpty() || admobKey.equals(TEST_ADMOB_BANNER_ID)){
            if(!AdHelperApplication.getTestMode()) {
                Log.d(MediationAdHelper.TAG, "[ADMOB NATIVE BANNER AD]InstallAd Load");
                Log.e(MediationAdHelper.TAG, "[ADMOB NATIVE BANNER AD]Error:  IDS null or test found");
                 
                onBannerAdListener.onError("NULL OR TEST IDS FOUND");
                MediationAdBanner.onError("NULL OR TEST IDS FOUND");
                return;
            }
        }

        final com.google.android.gms.ads.AdView admobBanner = new AdView(bannerContainer.getContext());
        admobBanner.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        admobBanner.setAdUnitId(admobKey);

        admobBanner.loadAd(MediationAdHelper.getAdRequest());

        admobBanner.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                MediationAdBanner.onError(adError.getMessage());
                 
                Log.d(MediationAdHelper.TAG, "[ADMOB BANNER]ERROR: " + adError.getMessage());
                AdHelperApplication.admobRequestBannerFaild++;
                // when admob in limit don't show next ad after one is failed
                if(AdHelperApplication.isAdmobInLimit()){
                    applyLimitOnAdmob = true;
                }
            }

            @Override
            public void onAdLoaded() {
                Log.d(MediationAdHelper.TAG, "[ADMOB BANNER]Loaded");
                bannerContainer.removeAllViews();
                ViewGroup parentView = ((ViewGroup) admobBanner.getParent());
                if (parentView != null) {
                    parentView.removeAllViews();
                }
                bannerContainer.addView(admobBanner);

                if (onBannerAdListener != null) {
                  
                    onBannerAdListener.onLoaded(MediationAdHelper.AD_ADMOB);
                  
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                if (onBannerAdListener != null) {
                    
                    onBannerAdListener.onAdClicked(MediationAdHelper.AD_ADMOB);
                }
            }

        });
    }


    private static void onError(String errorMessage) {
        Log.d(Constant.TAG, "onError: "+ errorMessage);
        if (adPriorityList != null && adPriorityList.size() > 0) {
            selectAd();
        } else {
            if (onBannerAdListener != null) {
                 
                onBannerAdListener.onError(errorMessage);
            }
            finishAd();
        }
    }

    private static void finishAd() {
        onBannerAdListener = null;
        facebookKey = null;
        admobKey = null;
        adPriorityList = null;
        cubiBannerAd = null;
    }
}
