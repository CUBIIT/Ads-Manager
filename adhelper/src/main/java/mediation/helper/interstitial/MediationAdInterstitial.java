package mediation.helper.interstitial;

import static mediation.helper.AdHelperApplication.applyLimitOnAdmob;
import static mediation.helper.AdHelperApplication.canShowInterstitial;
import static mediation.helper.AdHelperApplication.getCubiInterstitialAd;
import static mediation.helper.AdHelperApplication.getGeneralInfo;
import static mediation.helper.AdHelperApplication.interstitialClickAdCounter;
import static mediation.helper.AdHelperApplication.isAdPreloadEnable;
import static mediation.helper.MediationAdHelper.timer;
import static mediation.helper.TestAdIDs.TEST_ADMOB_INTERSTITIAL_ID;
import static mediation.helper.TestAdIDs.TEST_FB_INTERSTITIAL_ID;
import static mediation.helper.util.Constant.KEY_AD_FREQUENCY;
import static mediation.helper.util.Constant.KEY_NEXT_TIME_AD_SHOW;
import static mediation.helper.util.SharedPreferenceUtil.getSharedPreference;
import static mediation.helper.util.SharedPreferenceUtil.putSharedPreference;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import mediation.helper.AdHelperApplication;
import mediation.helper.AdTimeLimits;
import mediation.helper.AnalyticsEvents.MediationEvents;
import mediation.helper.IUtils;
import mediation.helper.MediationAdHelper;
import mediation.helper.config.AdSessions;
import mediation.helper.config.PLACEHOLDER;
import mediation.helper.cubiad.NativeAdView.CubiInterstitialAd;
import mediation.helper.util.Constant;
import mediation.helper.util.PrefManager;


/**
 * Created by CUBI-IT
 */

public class MediationAdInterstitial {
    public static OnInterstitialAdListener onInterstitialAdListener;
    public static CubiInterstitialAd cubiInterstitialAd;
    public static boolean showAds = true;
    static String TAG = "de_inters";
    private static String facebookKey;
    private static String admobKey;
    private static WeakReference<Activity> activityRef;
    private static ProgressDialog interstitialAdDialog = null;
    private static PrefManager prefManager;
    private static ArrayList<Integer> initAdPriorityList, adPriorityList;
    private static int num = -1;
    private static com.facebook.ads.InterstitialAd facebookInterstitialAD = null;
    private static com.google.android.gms.ads.interstitial.InterstitialAd admobInterstitialAD = null;
    private static boolean timeOut = false;
    private static boolean isAdInProgress = true;

    public static void showFacebookInterstitialAd(boolean isPurchased, Activity activity, String facebookKey, OnInterstitialAdListener onInterstitialAdListener) {
        showInterstitialAd(isPurchased, PLACEHOLDER.DEFAULT, activity, MediationAdHelper.AD_FACEBOOK, onInterstitialAdListener);
    }

    public static void initAdmobInterstitialAd(boolean isPurchased, Activity activity, String admobKey, OnInterstitialAdListener onInterstitialAdListener) {
        showInterstitialAd(isPurchased, PLACEHOLDER.DEFAULT, activity, MediationAdHelper.AD_ADMOB, onInterstitialAdListener);
    }

    public static void showInterstitialAd(boolean isPurchased, PLACEHOLDER placeholder, Activity activity, int adPriority, OnInterstitialAdListener onInterstitialAdListener) {
        if (isPurchased) {
            onInterstitialAdListener.onError("You have pro version!");
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

        showInterstitialAd(isPurchased, placeholder, activity, tempAdPriorityList, onInterstitialAdListener);
    }

    private static boolean checkTestIds(OnInterstitialAdListener listener) {
        Log.d("de_ch", "checkTestIds: ad" + AdHelperApplication.getAdIDs().getAdmob_interstitial_id() + " Fac: " + AdHelperApplication.getAdIDs().getFb_interstitial_id());

        if (!(AdHelperApplication.getAdIDs().getAdmob_interstitial_id().isEmpty() || AdHelperApplication.getAdIDs().getFb_interstitial_id().isEmpty())) {
            if (AdHelperApplication.getAdIDs().getAdmob_interstitial_id().equals(TEST_ADMOB_INTERSTITIAL_ID) || AdHelperApplication.getAdIDs().getFb_interstitial_id().equals(TEST_FB_INTERSTITIAL_ID)) {
                listener.onError("Found Test IDS..");
                return false;
            } else
                return true;
        } else {
            listener.onError("No IDS found..");
            return false;
        }

    }

    static String TAG_ = "de_place";

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
                String value = findValueInMap(placeholder.name().toLowerCase(Locale.ROOT).toString(), AdHelperApplication.placeholderConfig.interstitial).toLowerCase(Locale.ROOT);

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

    public static boolean isAddOff(String value) {
        if (value.equals("off") || value.equals("OFF") || value.equals("Off") || value.equals("of") || value.equals("0") || value.equals("FALSE")) {
            return true;
        } else {
            return false;
        }
    }

    private static void initInterstitalOnTimeLoad(final boolean isPurchased, final Activity activity, final Integer[] tempAdPriorityList, final OnInterstitialAdListener onInterstitialAdListener) {

        prefManager = new PrefManager(activity);
        Log.d(TAG, "loadAD: before check");
        if (!AdHelperApplication.isInit) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "not init ads");
                    initInterstitalOnTimeLoad(isPurchased, activity, tempAdPriorityList, onInterstitialAdListener);
                }
            }, 500);
            return;
        }
        Log.d(TAG, "pass success");
        if (isPurchased) {
            return;
        }

        if (tempAdPriorityList == null || tempAdPriorityList.length == 0) {
            throw new RuntimeException("You have to select priority type ADMOB/FACEBOOK/TNK");
        }
        MediationAdInterstitial.facebookKey = TEST_FB_INTERSTITIAL_ID;
        MediationAdInterstitial.admobKey = TEST_ADMOB_INTERSTITIAL_ID;
        if (!AdHelperApplication.getTestMode()) {
            //check if ids or test skip
            if (AdHelperApplication.getAdIDs() != null) {
                MediationAdInterstitial.facebookKey = AdHelperApplication.getAdIDs().getFb_interstitial_id();
                MediationAdInterstitial.admobKey = AdHelperApplication.getAdIDs().getAdmob_interstitial_id();

            }

        }
        Log.d(TAG, String.format("Interstitial Ad Ids----facebook: %s ----Admob: %s", MediationAdInterstitial.facebookKey, MediationAdInterstitial.admobKey));
        MediationAdInterstitial.initAdPriorityList = new ArrayList<>(Arrays.asList(tempAdPriorityList));
        Log.d(TAG, "showInterstitialAd: " + initAdPriorityList.size());
        MediationAdInterstitial.activityRef = new WeakReference<>(activity);
        initSelectedAd();

    }

    //for cubiads
    public static void showInterstitialAd(boolean isPurchased, PLACEHOLDER placeholder, Activity activity, Integer[] tempAdPriorityList, OnInterstitialAdListener onInterstitialAdListener) {
        Log.d(TAG, "showintisAdPreloadEnable: " + isAdPreloadEnable);
        if (!isAdPreloadEnable) {
            showInterstitialAdOnTime(isPurchased, placeholder, activity, tempAdPriorityList, onInterstitialAdListener);
            return;
        }
        prefManager = new PrefManager(activity);
        if (isPurchased) {
            onInterstitialAdListener.onError("You have pro version!");
            return;
        }
        if (isAddOff(findValueInMap(placeholder.name().toLowerCase(Locale.ROOT).toString(), AdHelperApplication.placeholderConfig.interstitial))) {
            MediationAdInterstitial.onError("showInterstitialAd: is off by remote ");
            onInterstitialAdListener.onError("showInterstitialAd: is off by remote ");

            Log.d(Constant.TAG, "showInterstitialAd: is off by remote");
            return;
        }
        Log.d(TAG, "showInterstitialAd: counter " + interstitialClickAdCounter);
        if (AdHelperApplication.interstitialClickAdCounter > 0 && AdHelperApplication.interstitialClickAdCounter < AdHelperApplication.INTERSTITIAL_CLICK_LIMIT) {
            MediationAdInterstitial.onError("Ad will show after " + (AdHelperApplication.INTERSTITIAL_CLICK_LIMIT - AdHelperApplication.interstitialClickAdCounter) + " Clicks");
            onInterstitialAdListener.onError("Ad will show after " + (AdHelperApplication.INTERSTITIAL_CLICK_LIMIT - AdHelperApplication.interstitialClickAdCounter) + " Clicks");
            AdHelperApplication.interstitialClickAdCounter++;
            return;
        }
        //reset ad

        AdHelperApplication.interstitialClickAdCounter = 0;

        AdHelperApplication.interstitialClickAdCounter++;


        if (tempAdPriorityList == null || tempAdPriorityList.length == 0) {
            throw new RuntimeException("You have to select priority type ADMOB/FACEBOOK/TNK");
        }
        MediationAdInterstitial.facebookKey = TEST_FB_INTERSTITIAL_ID;
        MediationAdInterstitial.admobKey = TEST_ADMOB_INTERSTITIAL_ID;
        if (!AdHelperApplication.getTestMode()) {
            //check if ids or test skip
            if (AdHelperApplication.getAdIDs() != null) {
                if (!checkTestIds(onInterstitialAdListener))
                    return;

                MediationAdInterstitial.facebookKey = AdHelperApplication.getAdIDs().getFb_interstitial_id();
                MediationAdInterstitial.admobKey = AdHelperApplication.getAdIDs().getAdmob_interstitial_id();

            }

        }
        interstitialAdDialog = new ProgressDialog(activity);
        interstitialAdDialog.setTitle("Loading");
        interstitialAdDialog.setMessage("Wait while ad is loading...");
        interstitialAdDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        try {
            interstitialAdDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
//select list on placeholder
        MediationAdInterstitial.adPriorityList = new ArrayList<>(Arrays.asList(getPriorityAgainstPlaceHolder(placeholder, tempAdPriorityList)));
        MediationAdInterstitial.initAdPriorityList = new ArrayList<>(Arrays.asList(getPriorityAgainstPlaceHolder(placeholder, tempAdPriorityList)));
        Log.d(TAG_, "showInterstitialAd:adPriorityList " + MediationAdInterstitial.adPriorityList.toString());
        Log.d(TAG_, "showInterstitialAd:initAdPriorityList " + MediationAdInterstitial.initAdPriorityList.toString());
        try {
            MediationAdInterstitial.activityRef = new WeakReference<>(activity);
            MediationAdInterstitial.onInterstitialAdListener = onInterstitialAdListener;
            MediationAdInterstitial.cubiInterstitialAd = getCubiInterstitialAd();
            showAds = true;
            Log.d(TAG, String.format("Interstitial Ad Ids----facebook: %s ----Admob: %s", MediationAdInterstitial.facebookKey, MediationAdInterstitial.admobKey));
            showSelectedAd();
            //only run looper  if we show admob and facebook ad

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                public void run() {
                    Log.d(MediationAdHelper.TAG, "Delay Time is Finished!");
                    if (!showAds && MediationAdInterstitial.onInterstitialAdListener != null) {
                        //on cubi ad error listener called already
                        if (num != 3)

                            MediationAdInterstitial.onInterstitialAdListener.onError("Delay Time is Finished!");
                        try {
                            if (interstitialAdDialog.isShowing()) interstitialAdDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showAds = false;
                        timer = 2000;//5 second delay time
                    }
                }
            }, timer);
        } catch (Exception e) {
            e.printStackTrace();
            if (onInterstitialAdListener != null) {

                onInterstitialAdListener.onError(e.toString());
            }
            finishAd();
        }

    }

    private static void showInterstitialAdOnTime(boolean isPurchased, PLACEHOLDER placeholder, Activity activity, Integer[] tempAdPriorityList, OnInterstitialAdListener onInterstitialAdListener) {

        prefManager = new PrefManager(activity);
        if (isPurchased) {
            onInterstitialAdListener.onError("You have pro version!");
            return;
        }
        if (isAddOff(findValueInMap(placeholder.name().toLowerCase(Locale.ROOT).toString(), AdHelperApplication.placeholderConfig.interstitial))) {
            MediationAdInterstitial.onError("showInterstitialAd: is off by remote ");
            onInterstitialAdListener.onError("showInterstitialAd: is off by remote ");

            Log.d(Constant.TAG, "showInterstitialAd: is off by remote");
            return;
        }
        Log.d(TAG, "showInterstitialAd: counter " + interstitialClickAdCounter);
        if (AdHelperApplication.interstitialClickAdCounter > 0 && AdHelperApplication.interstitialClickAdCounter < AdHelperApplication.INTERSTITIAL_CLICK_LIMIT) {
            MediationAdInterstitial.onError("Ad will show after " + (AdHelperApplication.INTERSTITIAL_CLICK_LIMIT - AdHelperApplication.interstitialClickAdCounter) + " Clicks");
            onInterstitialAdListener.onError("Ad will show after " + (AdHelperApplication.INTERSTITIAL_CLICK_LIMIT - AdHelperApplication.interstitialClickAdCounter) + " Clicks");
            AdHelperApplication.interstitialClickAdCounter++;
            return;
        }
        //reset ad

        AdHelperApplication.interstitialClickAdCounter = 0;

        AdHelperApplication.interstitialClickAdCounter++;


        if (tempAdPriorityList == null || tempAdPriorityList.length == 0) {
            throw new RuntimeException("You have to select priority type ADMOB/FACEBOOK/TNK");
        }
        MediationAdInterstitial.facebookKey = TEST_FB_INTERSTITIAL_ID;
        MediationAdInterstitial.admobKey = TEST_ADMOB_INTERSTITIAL_ID;
        if (!AdHelperApplication.getTestMode()) {
            //check if ids or test skip
            if (AdHelperApplication.getAdIDs() != null) {
                if (!checkTestIds(onInterstitialAdListener))
                    return;

                MediationAdInterstitial.facebookKey = AdHelperApplication.getAdIDs().getFb_interstitial_id();
                MediationAdInterstitial.admobKey = AdHelperApplication.getAdIDs().getAdmob_interstitial_id();

            }

        }
        interstitialAdDialog = new ProgressDialog(activity);
        // interstitialAdDialog.setTitle("Loading");
        interstitialAdDialog.setMessage("Wait while ad is loading...");
        interstitialAdDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        try {
            interstitialAdDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
//select list on placeholder
        MediationAdInterstitial.adPriorityList = new ArrayList<>(Arrays.asList(getPriorityAgainstPlaceHolder(placeholder, tempAdPriorityList)));
        MediationAdInterstitial.initAdPriorityList = new ArrayList<>(Arrays.asList(getPriorityAgainstPlaceHolder(placeholder, tempAdPriorityList)));
        Log.d(TAG_, "showInterstitialAd:adPriorityList " + MediationAdInterstitial.adPriorityList.toString());
        Log.d(TAG_, "showInterstitialAd:initAdPriorityList " + MediationAdInterstitial.initAdPriorityList.toString());
        try {
            MediationAdInterstitial.activityRef = new WeakReference<>(activity);
            MediationAdInterstitial.onInterstitialAdListener = onInterstitialAdListener;
            MediationAdInterstitial.cubiInterstitialAd = getCubiInterstitialAd();
            showAds = true;
            Log.d(TAG, String.format("Interstitial Ad Ids----facebook: %s ----Admob: %s", MediationAdInterstitial.facebookKey, MediationAdInterstitial.admobKey));
            // showSelectedAd();
            Log.d(TAG, "showInterstitialAdOnTime: " + admobInterstitialAD + facebookInterstitialAD);
            if (admobInterstitialAD != null || facebookInterstitialAD != null) {
                showSelectedAd();
                return;
            }
            initInterstitalOnTimeLoad(isPurchased, activity, tempAdPriorityList, null);
            //only run looper  if we show admob and facebook ad

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                public void run() {
                    Log.d(MediationAdHelper.TAG, "Delay Time is Finished!");
                    if (showAds && MediationAdInterstitial.onInterstitialAdListener != null) {
                        //on cubi ad error listener called already
                        if (num != 3)
                            Log.d("test_", "run: ");
                        MediationEvents.onInterstitialAdErrorEvent();
                        MediationAdInterstitial.onInterstitialAdListener.onError("Delay Time is Finished!");
                        try {
                            if (interstitialAdDialog.isShowing()) interstitialAdDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showAds = false;
                        timer = 5000;//5second
                    }
                }
            }, timer);
        } catch (Exception e) {
            e.printStackTrace();
            if (onInterstitialAdListener != null) {

                onInterstitialAdListener.onError(e.toString());
            }
            finishAd();
        }

    }

    public static void initInterstitialAd(final boolean isPurchased, final Activity activity, final Integer[] tempAdPriorityList, final OnInterstitialAdListener onInterstitialAdListener) {

        prefManager = new PrefManager(activity);
        Log.d(TAG, "loadAD: before check");
        if (!AdHelperApplication.isInit) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "not init ads");
                    initInterstitialAd(isPurchased, activity, tempAdPriorityList, onInterstitialAdListener);
                }
            }, 1500);
            return;
        }
        Log.d(TAG, "isAdPreloadEnable: " + isAdPreloadEnable);
        if (!AdHelperApplication.isAdPreloadEnable)
            return;
        Log.d(TAG, "pass success");
        if (isPurchased) {
            return;
        }

        if (tempAdPriorityList == null || tempAdPriorityList.length == 0) {
            throw new RuntimeException("You have to select priority type ADMOB/FACEBOOK/TNK");
        }
        MediationAdInterstitial.facebookKey = TEST_FB_INTERSTITIAL_ID;
        MediationAdInterstitial.admobKey = TEST_ADMOB_INTERSTITIAL_ID;
        if (!AdHelperApplication.getTestMode()) {
            //check if ids or test skip
            if (AdHelperApplication.getAdIDs() != null) {
                MediationAdInterstitial.facebookKey = AdHelperApplication.getAdIDs().getFb_interstitial_id();
                MediationAdInterstitial.admobKey = AdHelperApplication.getAdIDs().getAdmob_interstitial_id();

            }

        }
        Log.d(TAG, String.format("Interstitial Ad Ids----facebook: %s ----Admob: %s", MediationAdInterstitial.facebookKey, MediationAdInterstitial.admobKey));
        MediationAdInterstitial.initAdPriorityList = new ArrayList<>(Arrays.asList(tempAdPriorityList));
        Log.d(TAG, "showInterstitialAd: " + initAdPriorityList.size());
        MediationAdInterstitial.activityRef = new WeakReference<>(activity);
        initSelectedAd();
    }

    private static void initSelectedAd() {
        int adPriority = initAdPriorityList.remove(0);
        Log.e(TAG, "showSelectedAd: " + adPriority);
        switch (adPriority) {
            case MediationAdHelper.AD_FACEBOOK:
                initFacebookInterstitialAd();
                break;
            case MediationAdHelper.AD_ADMOB:
                initAdmobInterstitialAd();
                break;
            case MediationAdHelper.AD_CUBI_IT:
                MediationAdInterstitial.onLoadError("Load another!");
                break;
            default:
//                onInterstitialAdListener.onError("You have to select priority type ADMOB or FACEBOOK");
                finishAd();
        }
    }

    private static void showSelectedAd() {
        if (!prefManager.canShowAd(activityRef.get(), AdTimeLimits.INTERSTITIAL_KEY, AdHelperApplication.adTimeLimits.getInterstitial_ad_time())) {

            onInterstitialAdListener.onError("Time limit: Time is remaining to show!!");
            finishAd();
            return;
        }



        int adPriority = adPriorityList.remove(0);
        num = adPriority;
        Log.d(TAG, "showSelectedAd: " + adPriority);
        switch (adPriority) {
            case MediationAdHelper.AD_FACEBOOK:
                showFacebookInterstitialAd();
                break;
            case MediationAdHelper.AD_ADMOB:
                showAdmobInterstitialAd();
                break;
            case MediationAdHelper.AD_CUBI_IT:
                showCubiITInterstitialAd();
                break;
            default:

                onInterstitialAdListener.onError("You have to select priority type ADMOB or FACEBOOK");
                finishAd();
        }
    }

    private static boolean isPkgInstalledAlready() {
        cubiInterstitialAd = getCubiInterstitialAd();
        if (cubiInterstitialAd == null) {
            Log.d("TAG1_cubiIntersitital", "isPkgInstalledAlready: context or cubiBannerad are null");
            return true;// return true to retain logic same
        }
        if (IUtils.isContainPkg(cubiInterstitialAd.getInterstitialAdUrlLink())) {

            if (IUtils.isPackageInstalled(IUtils.getPackageName(cubiInterstitialAd.getInterstitialAdUrlLink()), activityRef.get().getApplicationContext())) {

                return true;
            } else {

                return false;
            }
        } else
            return false;

    }

    private static void showCubiITInterstitialAd() {

        try {
            long adFrequency = Long.parseLong(getGeneralInfo().getInterstitialAdFrequency());
            if (isPkgInstalledAlready()) {

                onInterstitialAdListener.onError("CubiAds pkg already installed");
                onError(" Cubiads pkg already installed");
                return;
            }

            if (passInterstitialTimer()) {

                long savedFrequency = getSharedPreference(activityRef.get(), KEY_AD_FREQUENCY, 0);
                if (adFrequency > savedFrequency) {
                    if (cubiInterstitialAd == null || cubiInterstitialAd.getInterstitialAdUrlLink() == null || cubiInterstitialAd.getInterstitialAdUrlLink().isEmpty()) {

                        onInterstitialAdListener.onError("cubiNativeAdd is null");
                        return;
                    }

                    if (showAds) {
                        onInterstitialAdListener.onBeforeAdShow();

                        activityRef.get().startActivity(new Intent(activityRef.get(), CubiInterstitialAdActivity.class));
                        try {
                            interstitialAdDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        showAds = false;
                        initSelectedAd();
                        putSharedPreference(activityRef.get(), KEY_AD_FREQUENCY, savedFrequency + 1);
                    }
                } else {
                    putSharedPreference(activityRef.get(), KEY_NEXT_TIME_AD_SHOW, System.currentTimeMillis());
                    putSharedPreference(activityRef.get(), KEY_AD_FREQUENCY, 0);
                    MediationAdInterstitial.onError("CUBI-IT ad limit is over.");

                }
            } else {
                MediationAdInterstitial.onError("CUBI-IT ad time limit is over.");
            }
        } catch (Exception e) {
            MediationAdInterstitial.onError(e.getMessage());
        }
    }

    private static long getSavedTime() {
        return getSharedPreference(activityRef.get(), KEY_NEXT_TIME_AD_SHOW, 0);
    }

    private static Boolean passInterstitialTimer() {
        // time has elapsed
        return System.currentTimeMillis() >= getSavedTime() + (Long.parseLong(getGeneralInfo().getInterstitialAdTimer()) * 60 * 60 * 1000);
    }

    private static void
    initFacebookInterstitialAd() {
        if (MediationAdHelper.isSkipFacebookAd(activityRef.get())) {
            Log.e(MediationAdHelper.TAG, "[FACEBOOK FRONT AD]Error: " + Constant.ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED);
            onError(Constant.ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED);
            return;
        }
        if (facebookKey.isEmpty() || facebookKey.equals(TEST_FB_INTERSTITIAL_ID)) {
            if (!AdHelperApplication.getTestMode()) {
                Log.e(MediationAdHelper.TAG, "[FACEBOOK FRONT AD]Error: NULL OR TEST IDS FOUND");
                onError("NULL OR TEST IDS FOUND");
                return;
            }
        }
        //check sesssion
        // 0>=1
        if (AdHelperApplication.fbRequestInterFaild >= Constant.findIntegerValueInMap(AdSessions.interstitial_session.name(), AdHelperApplication.sessionConfig.fb_sessions)) {
            Log.e(MediationAdHelper.TAG, "Facebook interstial session out");
            MediationAdInterstitial.onLoadError("Facebook interstial session out");
            facebookInterstitialAD = null;
            AdHelperApplication.fbRequestInterFaild++;
        }
        facebookInterstitialAD = new com.facebook.ads.InterstitialAd(activityRef.get(), facebookKey);

        if (onInterstitialAdListener != null) {
            onInterstitialAdListener.onFacebookAdCreated(facebookInterstitialAD);
        }
        // Set listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new com.facebook.ads.InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
                Log.d(MediationAdHelper.TAG, "[FACEBOOK FRONT AD]Displayed");
                facebookInterstitialAD = null;//ad consoume
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK FRONT AD]Dismissed");
                // Interstitial dismissed callback
                if (onInterstitialAdListener != null) {
                    onInterstitialAdListener.onDismissed(MediationAdHelper.AD_FACEBOOK);
//                    finishAd();
                }
                if (isAdPreloadEnable)
                    initSelectedAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(MediationAdHelper.TAG, "[FACEBOOK FRONT AD]Error: " + adError.getErrorMessage());
                MediationAdInterstitial.onLoadError(adError.getErrorMessage());
                facebookInterstitialAD = null;
                AdHelperApplication.fbRequestInterFaild++;
                if (isAdPreloadEnable)
                    initSelectedAd();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK FRONT AD]Loaded");
                if (onInterstitialAdListener != null) {

                    onInterstitialAdListener.onLoaded(MediationAdHelper.AD_FACEBOOK);
                }
                showAds = true;
                if (!isAdPreloadEnable)
                    showSelectedAd();
                //set interstitial time
//                prefManager.setTime(activityRef.get(), AdTimeLimits.INTERSTITIAL_KEY);
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK FRONT AD]Clicked");
                // Ad clicked callback
                if (onInterstitialAdListener != null) {

                    onInterstitialAdListener.onAdClicked(MediationAdHelper.AD_FACEBOOK);
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };
        AdHelperApplication.testFBLoad++;
        Log.d("de_load", "SendRequestfb: " + AdHelperApplication.testFBLoad);
        // Load the interstitial ad
        facebookInterstitialAD.loadAd(
                facebookInterstitialAD.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    private static void showFacebookInterstitialAd() {
        Log.d(TAG, "showFacebookInterstitialAd: ");
        if (showAds) {
            // Check if interstitialAd has been loaded successfully
            if (facebookInterstitialAD == null || !facebookInterstitialAD.isAdLoaded()) {
                MediationAdInterstitial.onError("Facebook interstitial ad is null or not loaded");
                return;
            }
            // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
            if (facebookInterstitialAD.isAdInvalidated()) {
                MediationAdInterstitial.onError("Facebook interstitial ad is expired.");
                return;
            }
            interstitialAdDialog.dismiss();
            showAds = false;
            // Show the ad when it's done loading.
            try {
                onInterstitialAdListener.onBeforeAdShow();
                facebookInterstitialAD.show();
            } catch (Exception e) {
                MediationAdInterstitial.onError(e.getMessage());
            }
            if (isAdPreloadEnable)
                initSelectedAd();
        } else {
            Log.d(TAG, "showFacebookInterstitialAd: show ad is off " + showAds);
        }
    }

    private static void initAdmobInterstitialAd() {
        try {
            if (!AdHelperApplication.canShowInterstitial) {
                MediationAdInterstitial.onError("Admob blocked whole session due to request failed equals to give attempt.");
                return;
            }
            //check in limit
            if (AdHelperApplication.isAdmobInLimit()) {
                if (applyLimitOnAdmob) {
                    onError("Interstitial add banned in current due to admob limit ");
                    return;
                }
            }
            if (admobKey.isEmpty() || admobKey.equals(TEST_ADMOB_INTERSTITIAL_ID)) {
                if (!AdHelperApplication.getTestMode()) {
                    Log.e(MediationAdHelper.TAG, "[ADMOB FRONT AD]Error: NULL OR TEST IDS FOUND");
                    onError("NULL OR TEST IDS FOUND");
                    return;
                }
            }
            AdHelperApplication.loadAdmobInters++;
            Log.d("de_load", "load admob: " + AdHelperApplication.loadAdmobInters);
            /*events*/

            Bundle bundle = new Bundle();
            bundle.putString("admb_req", "Send interstitial admob request");
            if (AdHelperApplication.getFirebaseAnalytics() != null) {
                AdHelperApplication.getFirebaseAnalytics().logEvent("SendAdmobRequest", bundle);
            }
            InterstitialAd.load(activityRef.get(), admobKey, MediationAdHelper.getAdRequest(), new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                    admobInterstitialAD = interstitialAd;
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    showAds = true;
                    canShowInterstitial = true;
                    Log.d(MediationAdHelper.TAG, "[ADMOB FRONT AD]Loaded");
                    if (onInterstitialAdListener != null) {

                        onInterstitialAdListener.onLoaded(MediationAdHelper.AD_ADMOB);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("admob_loaded", "Inters admob ad loaded");
                    if (AdHelperApplication.getFirebaseAnalytics() != null) {
                        AdHelperApplication.getFirebaseAnalytics().logEvent("OnInterstitialAdMobLoaded", bundle);
                    }
                    //ifpreload off means show ad when loaded
                    if (!isAdPreloadEnable)
                        showSelectedAd();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.e(MediationAdHelper.TAG, "[ADMOB FRONT AD]Error: " + loadAdError.getMessage());
                    Bundle bundle = new Bundle();
                    bundle.putString("amb_failtedtoload", "Inters admob ad failedToload");
                    if (AdHelperApplication.getFirebaseAnalytics() != null) {
                        AdHelperApplication.getFirebaseAnalytics().logEvent("OnIntersAMBFailedToLoad", bundle);
                    }
                    MediationAdInterstitial.onLoadError(loadAdError.getMessage());
                    if (!isAdPreloadEnable)
                        initSelectedAd();

                    if (AdHelperApplication.admobRequestInterFaild >= Constant.findIntegerValueInMap(AdSessions.interstitial_session.name().toString(), AdHelperApplication.sessionConfig.admob_sessions)) {
                        canShowInterstitial = false;
                    } else
                        AdHelperApplication.admobRequestInterFaild++;
                    if (AdHelperApplication.isAdmobInLimit()) {
                        applyLimitOnAdmob = true;
                    }
                    admobInterstitialAD = null;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showAdmobInterstitialAd() {
        if (showAds) {

            if (admobInterstitialAD == null) {
                MediationAdInterstitial.onError("Admob interstitial ad is null.");
            } else {
                interstitialAdDialog.dismiss();
                showAds = false;
                if (onInterstitialAdListener != null) {
                    onInterstitialAdListener.onBeforeAdShow();
                }
                admobInterstitialAD.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError var1) {
                        MediationAdInterstitial.onError(var1.getMessage());
                        Bundle bundle = new Bundle();
                        bundle.putString("amb_failedToShow", "Inters admob ad failedtoShowAd");
                        if (AdHelperApplication.getFirebaseAnalytics() != null) {
                            AdHelperApplication.getFirebaseAnalytics().logEvent("OnIntersAMBFailedToShowFlScrn", bundle);
                        }
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d(MediationAdHelper.TAG, "[ADMOB FRONT AD]Dismissed");
                        if (onInterstitialAdListener != null) {
                            onInterstitialAdListener.onDismissed(MediationAdHelper.AD_ADMOB);
                        }
                    }


                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        Bundle bundle = new Bundle();
                        bundle.putString("amb_AdShowdFlScr", "Inters admob ad showFullScrn");
                        if (AdHelperApplication.getFirebaseAnalytics() != null) {
                            AdHelperApplication.getFirebaseAnalytics().logEvent("OnIntersAMBShowedFlScreen", bundle);
                        }
                        admobInterstitialAD = null;
                        //set interstitial time
                        prefManager.setTime(activityRef.get(), AdTimeLimits.INTERSTITIAL_KEY);
                        Log.d(MediationAdHelper.TAG, "The admob ad was shown.");
                    }
                });
                admobInterstitialAD.show(activityRef.get());
                if (isAdPreloadEnable)
                    initSelectedAd();
            }
        }
    }

    public static void onLoadError(String errorMessage) {
        if (initAdPriorityList != null && initAdPriorityList.size() > 0) {
            if (isAdPreloadEnable)
                initSelectedAd();
        }
        try {
            if (interstitialAdDialog.isShowing())
                interstitialAdDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void onError(String errorMessage) {
        Log.d(Constant.TAG, "onError: " + errorMessage);
        try {
            if (interstitialAdDialog.isShowing())
                interstitialAdDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (adPriorityList != null && adPriorityList.size() > 0) {

                showSelectedAd();
        } else {
            if (onInterstitialAdListener != null) {

                onInterstitialAdListener.onError(errorMessage);
            }
            finishAd();
        }
    }

    private static void finishAd() {
        onInterstitialAdListener = null;
        facebookKey = null;
        admobKey = null;
        activityRef = null;
        initAdPriorityList = null;
        adPriorityList = null;
        cubiInterstitialAd = null;
        if (interstitialAdDialog != null)
            interstitialAdDialog.dismiss();
    }

}