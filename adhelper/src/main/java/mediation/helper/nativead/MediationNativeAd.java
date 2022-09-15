package mediation.helper.nativead;

import static mediation.helper.AdHelperApplication.adMobnativeAdLoad;
import static mediation.helper.AdHelperApplication.adTimeLimits;
import static mediation.helper.AdHelperApplication.applyLimitOnAdmob;
import static mediation.helper.AdHelperApplication.fbRequestNativeFaild;
import static mediation.helper.AdHelperApplication.getCubiNativeAd;
import static mediation.helper.TestAdIDs.TEST_ADMOB_NATIVE_ID;
import static mediation.helper.TestAdIDs.TEST_FB_NATIVE_ID;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.AdChoicesView;
import com.google.android.gms.ads.nativead.NativeAdOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mediation.helper.AdHelperApplication;
import mediation.helper.IUtils;
import mediation.helper.MediationAdHelper;
import mediation.helper.R;
import mediation.helper.config.AdSessions;
import mediation.helper.config.PLACEHOLDER;
import mediation.helper.cubiad.NativeAdView.CubiNativeAd;
import mediation.helper.cubiad.NativeAdView.NativeCubiAd;
import mediation.helper.util.Constant;

/**
 * Created by CUBI-IT
 */

public class MediationNativeAd {

    static com.google.android.gms.ads.nativead.NativeAd admobNativeAd;
    static com.facebook.ads.NativeAd facebookAd;
    static NativeCubiAd nativeCubiAd;
    static CubiNativeAd cubiNativeAd;

    static ArrayList<Integer> adPriorityList;
    static String app_name;
    static String facebook_ad_key;
    static String admob_ad_key;
    static Context context;
    static OnNativeAdListener onNativeAdListener;
    static String TAG = "TAG1_mediationnativead";
    MediationAdHelper.ImageProvider imageProvider;
    static boolean isPurchase = false;

    static ViewGroup containerView;
    static PLACEHOLDER placeholder;

    public MediationNativeAd(boolean isPurchase, PLACEHOLDER placeholder, ViewGroup containerView, Context context, String app_name, String facebook_ad_key, String admob_ad_key) {
        this(isPurchase, placeholder, containerView, context, app_name, null);
    }

    public MediationNativeAd(boolean isPurchase, PLACEHOLDER placeholder, ViewGroup itemView, Context context, String app_name, MediationAdHelper.ImageProvider imageProvider) {
        this.isPurchase = isPurchase;
        this.placeholder = placeholder;
        MediationNativeAd.containerView = itemView;
        MediationNativeAd.context = context;
        MediationNativeAd.app_name = app_name;
        MediationNativeAd.cubiNativeAd = getCubiNativeAd();
        this.imageProvider = imageProvider;
    }

    public void onDestroy() {
        if (admobNativeAd != null) {
            admobNativeAd.destroy();
        }
        if (facebookAd != null) {
            facebookAd.destroy();
        }
        if (cubiNativeAd != null) {
            cubiNativeAd = null;
        }
    }

    public static void loadFacebookAD(OnNativeAdListener onNativeAdListener) {
        loadAD(MediationAdHelper.AD_FACEBOOK, onNativeAdListener);
    }

    public static void loadAdmobAD(OnNativeAdListener onNativeAdListener) {
        loadAD(MediationAdHelper.AD_ADMOB, onNativeAdListener);
    }

    public static void loadCubiAD(OnNativeAdListener onNativeAdListener) {
        loadAD(MediationAdHelper.AD_CUBI_IT, onNativeAdListener);
    }

    public static void loadAD(int adPriority, OnNativeAdListener onNativeAdListener) {
        if (isPurchase) {
            onNativeAdListener.onError("You have pro version!");
            return;
        }
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
        loadAD(tempAdPriorityList, onNativeAdListener);
    }

    public static void loadAD(final Integer[] tempAdPriorityList, final OnNativeAdListener onNativeAdListener) {
        if (isPurchase) {
            onNativeAdListener.onError("You have pro version!");
            return;
        }

        if (tempAdPriorityList == null || tempAdPriorityList.length == 0) {
            if (onNativeAdListener != null) {

                onNativeAdListener.onError("You have to select priority type ADMOB/FACEBOOK/TNK");
            }
            return;
        }
        if (!AdHelperApplication.isInit) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadAD(tempAdPriorityList, onNativeAdListener);
                }
            }, 1500);
            return;
        }
        if (isAddOff(findValueInMap(placeholder.name().toLowerCase(Locale.ROOT).toString(), AdHelperApplication.placeholderConfig.native_placeholder))) {

            onNativeAdListener.onError("native ad is off from remote");
            Log.d(Constant.TAG, "native ad is off from remote");
            return;
        }
        ArrayList resultTempAdPriorityList = new ArrayList<>(Arrays.asList(getPriorityAgainstPlaceHolder(placeholder, tempAdPriorityList)));
        loadAD(resultTempAdPriorityList, onNativeAdListener);

    }

    static String TAG_ = "de_native";

    private static String findValueInMap(String key, Map<String, String> map) {
        key = key.toUpperCase(Locale.ROOT);
        Log.d(TAG_, "findValueInMap: key " + key);
        String value = "default";
        if (map == null) {
            Log.e(TAG_, "findValueInMapInterstitial: map is null");
        } else {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Log.i(TAG_, "findValueInMap: " + entry.getKey());
                if (entry.getKey().equals(key.toUpperCase(Locale.ROOT))) {
                    value = entry.getValue();
                    Log.d(TAG_, "findValueInMap:find " + value);
                    break;

                }
            }
        }
        Log.d(TAG_, "findValueInMap:value  " + value);
        return value;

    }

    private static Integer[] getPriorityAgainstPlaceHolder(PLACEHOLDER placeholder, Integer[] tempAdPriorityList) {

        if (placeholder.toString().toUpperCase(Locale.ROOT).equals(PLACEHOLDER.DEFAULT)) {
            Log.d(TAG, "getPriorityAgainstPlaceHolder: find default");
            return tempAdPriorityList;
        } else {
            if (AdHelperApplication.placeholderConfig != null) {
                Integer[] rearrange = new Integer[3];
                String value = findValueInMap(placeholder.name().toLowerCase(Locale.ROOT).toString(), AdHelperApplication.placeholderConfig.native_placeholder).toLowerCase(Locale.ROOT);

                if (value.equals("admob") || value.equals("1") || value.equals("01")) {
                    rearrange[0] = MediationAdHelper.AD_ADMOB;
                    rearrange[1] = MediationAdHelper.AD_FACEBOOK;
                    rearrange[2] = MediationAdHelper.AD_CUBI_IT;
                } else if (value.equals("fb") || value.equals("facebook") || value.equals("2") || value.equals("02")) {
                    rearrange[0] = MediationAdHelper.AD_FACEBOOK;
                    rearrange[1] = MediationAdHelper.AD_ADMOB;
                    rearrange[2] = MediationAdHelper.AD_CUBI_IT;
                } else if (value.equals("default") || value.equals("DEFAULT") || value.equals("-1")) {
                    rearrange = tempAdPriorityList;
                }
                // Log.d(TAG_, "getPriorityAgainstPlaceHolder: " + rearrange);
                return rearrange;
            } else {
                return tempAdPriorityList;
            }
        }
    }

    private static boolean checkTestIds(OnNativeAdListener onBannerAdListener) {
        Log.d("de_ch", "checkTestIds: ad" + AdHelperApplication.getAdIDs().getAdmob_native_id() + " Fac: " + AdHelperApplication.getAdIDs().getFb_native_id());

        if (!AdHelperApplication.getAdIDs().getAdmob_native_id().isEmpty() || AdHelperApplication.getAdIDs().getFb_native_id().isEmpty()) {
            if (AdHelperApplication.getAdIDs().getAdmob_native_id().equals(TEST_ADMOB_NATIVE_ID) || AdHelperApplication.getAdIDs().getFb_native_id().equals(TEST_FB_NATIVE_ID)) {
                onBannerAdListener.onError("Found Test IDS..");
                return false;
            } else {

                return true;
            }
        } else {
            onBannerAdListener.onError("No ID found..");
            return false;
        }
    }

    public static void loadAD(final ArrayList tempAdPriorityList, final OnNativeAdListener onNativeAdListener) {
        if (isPurchase) {
            onNativeAdListener.onError("You have pro version!");
            return;
        }
        Log.d(TAG, "loadAD: before check");
        if (!AdHelperApplication.isInit) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "not init ads");
                    loadAD(tempAdPriorityList, onNativeAdListener);
                }
            }, 2000);
            return;
        }
        Log.d(TAG, "loadAD: pass successful");
        MediationNativeAd.onNativeAdListener = onNativeAdListener;

        if (tempAdPriorityList == null || tempAdPriorityList.size() == 0) {
            if (onNativeAdListener != null) {

                onNativeAdListener.onError("You have to select priority type ADMOB/FACEBOOK/CUBI-IT");
            }
            return;
        }
        adPriorityList = tempAdPriorityList;
        MediationNativeAd.facebook_ad_key = TEST_FB_NATIVE_ID;
        MediationNativeAd.admob_ad_key = TEST_ADMOB_NATIVE_ID;
        Log.d(TAG, "Test Mode is: " + AdHelperApplication.getTestMode());
        if (!AdHelperApplication.getTestMode()) {
            //check if ids or test skip
            if (AdHelperApplication.getAdIDs() != null) {
                if (!checkTestIds(onNativeAdListener))
                    return;
                MediationNativeAd.facebook_ad_key = AdHelperApplication.getAdIDs().getFb_native_id();
                MediationNativeAd.admob_ad_key = AdHelperApplication.getAdIDs().getAdmob_native_id();
            }
        }
        Log.d("de_native", String.format("Native ids:------Facebook: %s -----Admob: %s", facebook_ad_key, admob_ad_key));
        try {
            Log.d(TAG, "loadAD: selec ads");
            fbOnErrorCalled = false;
            selectAd();
        } catch (Exception e) {
            e.printStackTrace();
            if (onNativeAdListener != null) {

                onNativeAdListener.onError(e.toString());
            }
        }

    }

    private static boolean isPkgInstalledAlready() {
        cubiNativeAd = getCubiNativeAd();
        if (cubiNativeAd == null || context == null) {
            Log.d(TAG, "isPkgInstalledAlready: context or CUBINATIVEAD are null");
            return true;// return true to retain logic same
        }
        if (IUtils.isContainPkg(cubiNativeAd.getNativeAdUrlLink())) {
            if (IUtils.isPackageInstalled(IUtils.getPackageName(cubiNativeAd.getNativeAdUrlLink()), context))
                return true;
            else
                return false;
        } else
            return false;

    }

    private static void selectAd() {
        if (adTimeLimits.isCan_skip()) {
            adTimeLimits.setCan_skip(false);

            onNativeAdListener.onError("Native can skip firstly");
            Log.e(TAG, "selectAd: Native can skip first time");
            return;
        }
        Log.d(Constant.TAG, "selectAd: another");
        int adPriority = adPriorityList.remove(0);
        switch (adPriority) {
            case MediationAdHelper.AD_FACEBOOK:
                if (facebookAd != null)
                    bindFacebookAD(facebookAd);// for fast load
                loadFacebookAD();
                break;
            case MediationAdHelper.AD_ADMOB:
                if (admobNativeAd != null)
                    bindAdmobContentAD();// for fast load
                selectAdmobAd();
                break;
            case MediationAdHelper.AD_CUBI_IT:
                Log.d(TAG, "selectAd: cbui-it");
                bindCubiAd();
            default:

                onNativeAdListener.onError("You have to select priority type ADMOB or FACEBOOK");
        }
    }

    private static void bindCubiAd() {
        cubiNativeAd = getCubiNativeAd();
        if (cubiNativeAd == null || cubiNativeAd.getNativeAdUrlLink() == null || cubiNativeAd.getNativeAdUrlLink().isEmpty()) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                public void run() {
                    cubiNativeAd = getCubiNativeAd();
                    if (cubiNativeAd == null || cubiNativeAd.getNativeAdUrlLink() == null || cubiNativeAd.getNativeAdUrlLink().isEmpty()) {
                        onLoadAdError("nativeContentAd is null");
                        return;
                    }
                    loadCubiNativeAd();
                }
            }, 1500);
            return;
        }
        loadCubiNativeAd();
    }

    private static void loadCubiNativeAd() {
        if (isPkgInstalledAlready()) {
            Log.d(TAG, "bindCubiAd: pkg already installed");
            onLoadAdError("CubiNativeAd pkg already installed");
            return;
        }
        Log.d(TAG, "bindCubiAd: pkg not installed ");
        containerView.removeAllViews();
        ConstraintLayout nativeView = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.cubi_it_native_ads_layout, containerView, false);
        containerView.addView(nativeView);

        // Set other ad assets.
        TextView title = (nativeView.findViewById(R.id.ad_headline));
        TextView bodyText = (nativeView.findViewById(R.id.ad_body));
        TextView advertiserName = (nativeView.findViewById(R.id.ad_advertiser));
        ImageView adIcon = (nativeView.findViewById(R.id.cubi_interstitial_square_icon));
        ImageView medicontent = (nativeView.findViewById(R.id.ad_media));
        Button calltoAction = (nativeView.findViewById(R.id.ad_call_to_action));
        title.setText(cubiNativeAd.getNativeAdtitle());
        bodyText.setText(cubiNativeAd.getNativeAdbodyText());
        advertiserName.setText(cubiNativeAd.getNativeAdadvertiserName());
//        Toast.makeText(context, context.getAttributionTag(), Toast.LENGTH_SHORT).show();
        Glide.with(context.getApplicationContext())
                .load(cubiNativeAd.getNativeAdIconUrl())
                .centerCrop()
                .into(adIcon);
        Glide.with(context.getApplicationContext())
                .load(cubiNativeAd.getNativeAdImageUrl())
                .fitCenter()
                .into(medicontent);
        calltoAction.setText(cubiNativeAd.getNativeAdcallToActionData());

        adIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onNativeAdListener.onAdClicked(3);
                actionOnCubiAdClicked();
            }
        });
        calltoAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onNativeAdListener.onAdClicked(3);
                actionOnCubiAdClicked();
            }
        });
        medicontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onNativeAdListener.onAdClicked(3);
                actionOnCubiAdClicked();
            }
        });
        onNativeAdListener.onLoaded(3);
    }

    public static void actionOnCubiAdClicked() {
        String url = cubiNativeAd.getNativeAdUrlLink();
        if (url.isEmpty()) {

            onNativeAdListener.onError("Url is empty");
            return;
        }

        /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);*/
//        String packageName = "";
//        //split package name
//        if (url.contains("play.google.com/store/apps")) {
//            Log.d("de_", "actionOnCubiAdClicked: contains");
//            String[] a = url.split("=");
//            packageName = a[1];
//        } else {
//            packageName = url;
//        }
//
//        Uri uri = Uri.parse("market://details?id=" + packageName);
//        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
//                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
//                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
//            context.startActivity(new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {

            onNativeAdListener.onError("onClick:" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void selectAdmobAd() {
        loadAdmobAdvanceAD();
    }

    private static void onLoadAdError(String errorMessage) {
        Log.d(Constant.TAG, "onLoadAdError: " + errorMessage);
        if (adPriorityList.size() > 0) {
            selectAd();
        } else {
            // containerView.setVisibility(View.GONE);
            Log.d(Constant.TAG, "onLoadAdError: ");
            if (onNativeAdListener != null) {

                onNativeAdListener.onError(errorMessage);
            }

        }
    }

    public static boolean isAddOff(String value) {
        if (value.equals("off") || value.equals("OFF") || value.equals("Off") || value.equals("of") || value.equals("0") || value.equals("false") || value.equals("FALSE")) {
            return true;
        } else {
            return false;
        }
    }

    private static void loadAdmobAdvanceAD() {
        Log.d(TAG, "lOADaDmOBAdvancedAD");
        if (admob_ad_key.isEmpty() || admob_ad_key.equals(TEST_ADMOB_NATIVE_ID)) {
            if (!AdHelperApplication.getTestMode()) {
                Log.e(TAG, "[lOADaDmOBAdvancedAD NATIVE AD]Error: empty id found ");
                onLoadAdError("Empty id found");

                return;
            }
        }

        //check adlimit
        if (AdHelperApplication.isAdmobInLimit()) {
            if (applyLimitOnAdmob) {
                onLoadAdError("Native admob banned due to admob in limit");
                return;
            }
        }
        Log.d(Constant.TAG, "loadAdmobAdvanceAD: " + AdHelperApplication.admobRequestNativeFaild + "  : " + Constant.findIntegerValueInMap(AdSessions.native_session.name(), AdHelperApplication.sessionConfig.admob_sessions));
        if (AdHelperApplication.admobRequestNativeFaild >= Constant.findIntegerValueInMap(AdSessions.native_session.name(), AdHelperApplication.sessionConfig.admob_sessions)) {
            onLoadAdError("Native admob Sesssion out");
            AdHelperApplication.admobRequestNativeFaild++;
            admobNativeAd = null;
            return;
        }

        AdLoader.Builder builder = new AdLoader.Builder(context, admob_ad_key);
        builder.forNativeAd(new com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                admobNativeAd = nativeAd;
                bindAdmobContentAD();
                Log.d(MediationAdHelper.TAG, "[ADMOB NATIVE ADVANCED AD]InstallAd Load");
            }

        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();
        com.google.android.gms.ads.nativead.NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();
        builder.withNativeAdOptions(adOptions);


        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError errorCode) {
                super.onAdFailedToLoad(errorCode);
                String errorMessage = errorCode.getMessage();
                Log.e(MediationAdHelper.TAG, "[ADMOB NATIVE EXPRESS AD]errorMessage: " + errorMessage);
                onLoadAdError(errorMessage);
                AdHelperApplication.admobRequestNativeFaild++;
                if (AdHelperApplication.isAdmobInLimit()) {
                    applyLimitOnAdmob = true;
                }

            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d(MediationAdHelper.TAG, "[ADMOB NATIVE EXPRESS AD]Loaded");

                if (onNativeAdListener != null) {

                    onNativeAdListener.onLoaded(MediationAdHelper.AD_ADMOB);
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.d(MediationAdHelper.TAG, "[ADMOB NATIVE EXPRESS AD]Opend");

                if (onNativeAdListener != null) {

                    onNativeAdListener.onAdClicked(MediationAdHelper.AD_ADMOB);
                }
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.d(MediationAdHelper.TAG, "[ADMOB NATIVE AD]Clicked");
                if (onNativeAdListener != null) {

                    onNativeAdListener.onAdClicked(MediationAdHelper.AD_ADMOB);
                }
            }
        }).build();
        AdHelperApplication.adMobnativeAdLoad++;
        Log.d("load_n", "loadAdmobAdvanceAD: " + adMobnativeAdLoad);
        AdRequest.Builder requestBuilder = new AdRequest.Builder();
        adLoader.loadAd(requestBuilder.build());

    }

    private static boolean fbOnErrorCalled = false;

    private static void loadFacebookAD() {
        if (MediationAdHelper.isSkipFacebookAd(context)) {
            Log.e(MediationAdHelper.TAG, "[FACEBOOK NATIVE AD]Error: " + Constant.ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED);
            onLoadAdError(Constant.ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED);
            return;
        }
        //individual check to empty ids
        if (facebook_ad_key.isEmpty() || facebook_ad_key.equals(TEST_FB_NATIVE_ID)) {
            if (!AdHelperApplication.getTestMode()) {
                Log.e(TAG, "[lOADaDmOBAdvancedAD NATIVE AD]Error: empty id found ");
                onLoadAdError("Empty id found");
                return;
            }
        }

        //check session
        if (AdHelperApplication.fbRequestNativeFaild >= Constant.findIntegerValueInMap(AdSessions.native_session.name(), AdHelperApplication.sessionConfig.fb_sessions)) {
            facebookAd = null;
            onLoadAdError("fb Native Session out");

            AdHelperApplication.fbRequestNativeFaild++;
            return;
        }
        Log.d(Constant.TAG, "loadFacebookAD: passing after out session");
        facebookAd = new com.facebook.ads.NativeAd(context, facebook_ad_key);
        NativeAdListener nativeAdListener = new com.facebook.ads.NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.d(Constant.TAG, "[FACEBOOK NATIVE AD]Error: " + adError.getErrorMessage());
                if (!fbOnErrorCalled) {
                    onLoadAdError(adError.getErrorMessage());
                    fbOnErrorCalled = true;
                }
                fbRequestNativeFaild++;
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK NATIVE AD]Loaded");
                bindFacebookAD(ad);

                if (onNativeAdListener != null) {

                    onNativeAdListener.onLoaded(MediationAdHelper.AD_FACEBOOK);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK NATIVE AD]Clicked");
                if (onNativeAdListener != null) {

                    onNativeAdListener.onAdClicked(MediationAdHelper.AD_FACEBOOK);
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        AdHelperApplication.nativeAdLoad++;
        Log.d("load_n", "SendNativeRequest: " + AdHelperApplication.nativeAdLoad);
        facebookAd.loadAd(
                facebookAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }

    private static void bindFacebookAD(Ad ad) {
        try {
            containerView.removeAllViews();
            View nativeView = LayoutInflater.from(context).inflate(R.layout.facebook_native_ad_layout, containerView, false);
            containerView.addView(nativeView);

            // Add the AdOptionsView
            LinearLayout adChoicesContainer = nativeView.findViewById(R.id.ad_choices_container);

            NativeAdLayout nativeAdLayout = new NativeAdLayout(context);

            AdOptionsView adOptionsView = new AdOptionsView(context, facebookAd, nativeAdLayout);
            adChoicesContainer.removeAllViews();
            adChoicesContainer.addView(adOptionsView, 0);

            // Create native UI using the ad metadata.
            MediaView nativeAdIcon = nativeView.findViewById(R.id.native_ad_icon);
            TextView nativeAdTitle = nativeView.findViewById(R.id.native_ad_title);
            MediaView nativeAdMedia = nativeView.findViewById(R.id.native_ad_media);
            TextView nativeAdSocialContext = nativeView.findViewById(R.id.native_ad_social_context);
            TextView nativeAdBody = nativeView.findViewById(R.id.native_ad_body);
            TextView sponsoredLabel = nativeView.findViewById(R.id.native_ad_sponsored_label);
            Button nativeAdCallToAction = nativeView.findViewById(R.id.native_ad_call_to_action);

            // Set the Text.
            nativeAdTitle.setText(facebookAd.getAdvertiserName());
            nativeAdBody.setText(facebookAd.getAdBodyText());
            nativeAdSocialContext.setText(facebookAd.getAdSocialContext());
            nativeAdCallToAction.setVisibility(facebookAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
            nativeAdCallToAction.setText(facebookAd.getAdCallToAction());
            sponsoredLabel.setText(facebookAd.getSponsoredTranslation());

            // Create a list of clickable views
            List<View> clickableViews = new ArrayList<>();
            // clickableViews.add(nativeAdTitle);
            if (AdHelperApplication.isCATEnable) {
                nativeAdCallToAction.setEnabled(true);

            } else {
                nativeAdCallToAction.setEnabled(false);
                Log.d(TAG, "bindFacebookAD: CAT is not Enable");
            }

            clickableViews.add(nativeAdCallToAction);

            // Register the Title and CTA button to listen for clicks.
            facebookAd.registerViewForInteraction(
                    nativeView, nativeAdMedia, nativeAdIcon, clickableViews);
        } catch (Exception e) {
            // onLoadAdError(e.getMessage());
            e.printStackTrace();
        }
    }


    private static void bindAdmobContentAD() {
        try {
            if (admobNativeAd == null) {
                onLoadAdError("ADMOB nativeContentAd is null");
                return;
            }

            containerView.removeAllViews();
            com.google.android.gms.ads.nativead.NativeAdView nativeView = (com.google.android.gms.ads.nativead.NativeAdView) LayoutInflater.from(context).inflate(R.layout.admob_native_ad_layout, containerView, false);
            containerView.addView(nativeView);
            Log.d(TAG, "bindAdmobContentAD:enableBorder " + AdHelperApplication.enableBorder);
            Log.d(TAG, "bindAdmobContentAD:enableDarkMode " + AdHelperApplication.enableDarkMode);
            if (AdHelperApplication.enableBorder) {
                nativeView.setBackgroundResource(R.drawable.bg_border);
            } else {
                nativeView.setBackgroundResource(R.drawable.bg_no_border);
            }
            if (AdHelperApplication.enableDarkMode) {
                nativeView.setBackgroundResource(R.drawable.bg_black_theme);
                TextView tv = (TextView) nativeView.findViewById(R.id.ad_headline);
                tv.setTextColor(context.getResources().getColor(R.color.white));
                ((TextView) nativeView.findViewById(R.id.ad_body)).setTextColor(context.getResources().getColor(R.color.white));
                ((TextView) nativeView.findViewById(R.id.ad_advertiser)).setTextColor(context.getResources().getColor(R.color.grayWhite));
                ((TextView) nativeView.findViewById(R.id.tvAd)).setTextColor(context.getResources().getColor(R.color.yellow));

                ((TextView) nativeView.findViewById(R.id.tvAd)).setBackgroundResource(R.drawable.bg__ye_ad_broder);
            }
            if( AdHelperApplication.enableBorder && AdHelperApplication.enableDarkMode){
                nativeView.setBackgroundResource(R.drawable.bg_border_black_theme);
                TextView tv = (TextView) nativeView.findViewById(R.id.ad_headline);
                tv.setTextColor(context.getResources().getColor(R.color.white));
                ((TextView) nativeView.findViewById(R.id.ad_body)).setTextColor(context.getResources().getColor(R.color.white));
                ((TextView) nativeView.findViewById(R.id.ad_advertiser)).setTextColor(context.getResources().getColor(R.color.grayWhite));
                ((TextView) nativeView.findViewById(R.id.tvAd)).setTextColor(context.getResources().getColor(R.color.yellow));

                ((TextView) nativeView.findViewById(R.id.tvAd)).setBackgroundResource(R.drawable.bg__ye_ad_broder);
            }


            // Set the media view.
            nativeView.setMediaView((com.google.android.gms.ads.nativead.MediaView) nativeView.findViewById(R.id.ad_media));
            //DISABLE CLICK ON
            nativeView.findViewById(R.id.ad_headline).setEnabled(false);
            nativeView.findViewById(R.id.ad_body).setEnabled(false);
            nativeView.findViewById(R.id.cubi_interstitial_square_icon).setEnabled(false);
            nativeView.findViewById(R.id.ad_media).setEnabled(false);
            if (AdHelperApplication.isCATEnable) {
                nativeView.findViewById(R.id.ad_call_to_action).setEnabled(true);
            } else {
                nativeView.findViewById(R.id.ad_call_to_action).setEnabled(false);
            }

            // Set other ad assets.
            nativeView.setHeadlineView(nativeView.findViewById(R.id.ad_headline));
            nativeView.setBodyView(nativeView.findViewById(R.id.ad_body));
            nativeView.setCallToActionView(nativeView.findViewById(R.id.ad_call_to_action));
            nativeView.setIconView(nativeView.findViewById(R.id.cubi_interstitial_square_icon));
            nativeView.setPriceView(nativeView.findViewById(R.id.ad_price));
            nativeView.setStarRatingView(nativeView.findViewById(R.id.ad_stars));
            nativeView.setStoreView(nativeView.findViewById(R.id.ad_store));
            nativeView.setAdvertiserView(nativeView.findViewById(R.id.ad_advertiser));
            nativeView.setAdChoicesView(nativeView.<AdChoicesView>findViewById(R.id.ad_choices_container));


            ((TextView) nativeView.getHeadlineView()).setText(admobNativeAd.getHeadline());
            nativeView.getMediaView().setMediaContent(admobNativeAd.getMediaContent());

            // These assets aren't guaranteed to be in every NativeAd, so it's important to
            // check before trying to display them.
            if (admobNativeAd.getBody() == null) {
                nativeView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                nativeView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) nativeView.getBodyView()).setText(admobNativeAd.getBody());
            }

            if (admobNativeAd.getCallToAction() == null) {
                nativeView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                nativeView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) nativeView.getCallToActionView()).setText(admobNativeAd.getCallToAction());
            }

            if (admobNativeAd.getIcon() == null) {
                nativeView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) nativeView.getIconView()).setImageDrawable(
                        admobNativeAd.getIcon().getDrawable());
                nativeView.getIconView().setVisibility(View.VISIBLE);
            }

            if (admobNativeAd.getPrice() == null) {
                nativeView.getPriceView().setVisibility(View.INVISIBLE);
            } else {
                nativeView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) nativeView.getPriceView()).setText(admobNativeAd.getPrice());
            }

            if (admobNativeAd.getStore() == null) {
                nativeView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                nativeView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) nativeView.getStoreView()).setText(admobNativeAd.getStore());
            }

            if (admobNativeAd.getStarRating() == null) {
                nativeView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) nativeView.getStarRatingView())
                        .setRating(admobNativeAd.getStarRating().floatValue());
                nativeView.getStarRatingView().setVisibility(View.VISIBLE);
            }

            if (admobNativeAd.getAdvertiser() == null) {
                nativeView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                ((TextView) nativeView.getAdvertiserView()).setText(admobNativeAd.getAdvertiser());
                nativeView.getAdvertiserView().setVisibility(View.VISIBLE);
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            nativeView.setNativeAd(admobNativeAd);

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
            VideoController vc = admobNativeAd.getMediaContent().getVideoController();

            // Updates the UI to say whether or not this ad has a video asset.
            if (vc.hasVideoContent()) {


                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                // VideoController will call methods on this object when events occur in the video
                // lifecycle.
                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        super.onVideoEnd();
                    }
                });
            }
        } catch (Exception e) {

            onLoadAdError(e.getMessage());
            e.printStackTrace();
        }

    }


    static class MyNativeAd {
        String logoUrl;
        Drawable logoDrawable;
        String name;
        String imageUrl;
        Drawable imageDrawable;
        String adType;
        String adHeading;
        Double adRatingBar;
        MediaContent mediaContent;
        String body;
        String callToAction;
        String etc;
        String fbBodyText;
        String fbSocialContext;

        public String getFbBodyText() {
            return fbBodyText;
        }

        public void setFbBodyText(String fbBodyText) {
            this.fbBodyText = fbBodyText;
        }

        public String getFbSocialContext() {
            return fbSocialContext;
        }

        public void setFbSocialContext(String fbSocialContext) {
            this.fbSocialContext = fbSocialContext;
        }

        public String getAdType() {
            return adType;
        }

        public void setAdType(String adType) {
            this.adType = adType;
        }

        public String getAdHeading() {
            return adHeading;
        }

        public void setAdHeading(String adHeading) {
            this.adHeading = adHeading;
        }

        public Double getAdRatingBar() {
            return adRatingBar;
        }

        public void setAdRatingBar(Double adRatingBar) {
            this.adRatingBar = adRatingBar;
        }


        public MediaContent getMediaContent() {
            return mediaContent;
        }

        public void setMediaContent(MediaContent mediaContent) {
            this.mediaContent = mediaContent;
        }

        public String getLogoUrl() {
            return logoUrl;
        }

        public void setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getCallToAction() {
            return callToAction;
        }

        public void setCallToAction(String callToAction) {
            this.callToAction = callToAction;
        }

        public String getEtc() {
            return etc;
        }

        public void setEtc(String etc) {
            this.etc = etc;
        }

        public Drawable getLogoDrawable() {
            return logoDrawable;
        }

        public void setLogoDrawable(Drawable logoDrawable) {
            this.logoDrawable = logoDrawable;
        }

        public Drawable getImageDrawable() {
            return imageDrawable;
        }

        public void setImageDrawable(Drawable imageDrawable) {
            this.imageDrawable = imageDrawable;
        }
    }

}
