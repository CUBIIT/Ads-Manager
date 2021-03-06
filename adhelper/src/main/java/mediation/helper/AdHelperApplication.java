package mediation.helper;

import static mediation.helper.util.Constant.ADMOB_APP_ID_KEY;
import static mediation.helper.util.Constant.ADMOB_BANNER_ID_KEY;
import static mediation.helper.util.Constant.ADMOB_INTERSTITIAL_ID_KEY;
import static mediation.helper.util.Constant.ADMOB_NATIVE_ID_KEY;
import static mediation.helper.util.Constant.BANNER_AD_PRIORITY_KEY;
import static mediation.helper.util.Constant.BANNER_KEY_AD_ADVERTISER_NAME;
import static mediation.helper.util.Constant.BANNER_KEY_AD_BODY;
import static mediation.helper.util.Constant.BANNER_KEY_AD_CALL_TO_ACTION;
import static mediation.helper.util.Constant.BANNER_KEY_AD_FEEDBACKS;
import static mediation.helper.util.Constant.BANNER_KEY_AD_RATING;
import static mediation.helper.util.Constant.BANNER_KEY_AD_TITLE;
import static mediation.helper.util.Constant.BANNER_KEY_AD_URL;
import static mediation.helper.util.Constant.BANNER_KEY_SQUARE_ICON_URL;
import static mediation.helper.util.Constant.ENABLE_CAT;
import static mediation.helper.util.Constant.FB_BANNER_ID_KEY;
import static mediation.helper.util.Constant.FB_INTERSTITIAL_ID_KEY;
import static mediation.helper.util.Constant.FB_NATIVE_ID_KEY;
import static mediation.helper.util.Constant.INTERSTITIAL_AD_FREQUENCY_KEY;
import static mediation.helper.util.Constant.INTERSTITIAL_AD_PRIORITY_KEY;
import static mediation.helper.util.Constant.INTERSTITIAL_AD_TIMER_KEY;
import static mediation.helper.util.Constant.INTERSTITIAL_KEY_AD_ADVERTISER_NAME;
import static mediation.helper.util.Constant.INTERSTITIAL_KEY_AD_BODY;
import static mediation.helper.util.Constant.INTERSTITIAL_KEY_AD_CALL_TO_ACTION;
import static mediation.helper.util.Constant.INTERSTITIAL_KEY_AD_FEEDBACKS;
import static mediation.helper.util.Constant.INTERSTITIAL_KEY_AD_RATING;
import static mediation.helper.util.Constant.INTERSTITIAL_KEY_AD_TITLE;
import static mediation.helper.util.Constant.INTERSTITIAL_KEY_AD_URL;
import static mediation.helper.util.Constant.INTERSTITIAL_KEY_FEATURE_ICON_URL;
import static mediation.helper.util.Constant.INTERSTITIAL_KEY_SQUARE_ICON_URL;
import static mediation.helper.util.Constant.KEY_PRIORITY_BANNER_AD;
import static mediation.helper.util.Constant.KEY_PRIORITY_INTERSTITIAL_AD;
import static mediation.helper.util.Constant.KEY_PRIORITY_NATIVE_AD;
import static mediation.helper.util.Constant.NATIVE_AD_PRIORITY_KEY;
import static mediation.helper.util.Constant.NATIVE_KEY_AD_ADVERTISER_NAME;
import static mediation.helper.util.Constant.NATIVE_KEY_AD_BODY;
import static mediation.helper.util.Constant.NATIVE_KEY_AD_CALL_TO_ACTION;
import static mediation.helper.util.Constant.NATIVE_KEY_AD_FEEDBACKS;
import static mediation.helper.util.Constant.NATIVE_KEY_AD_ICON_URL;
import static mediation.helper.util.Constant.NATIVE_KEY_AD_IMAGE_URL;
import static mediation.helper.util.Constant.NATIVE_KEY_AD_RATING;
import static mediation.helper.util.Constant.NATIVE_KEY_AD_TITLE;
import static mediation.helper.util.Constant.NATIVE_KEY_AD_URL;
import static mediation.helper.util.Constant.NEW_ADMOB_APP_ID_KEY;
import static mediation.helper.util.Constant.NEW_ADMOB_BANNER_ID_KEY;
import static mediation.helper.util.Constant.NEW_ADMOB_INTERSTITIAL_ID_KEY;
import static mediation.helper.util.Constant.NEW_ADMOB_NATIVE_ID_KEY;
import static mediation.helper.util.Constant.NEW_APP_VERSION;
import static mediation.helper.util.Constant.NEW_FB_BANNER_ID_KEY;
import static mediation.helper.util.Constant.NEW_FB_INTERSTITIAL_ID_KEY;
import static mediation.helper.util.Constant.NEW_FB_NATIVE_ID_KEY;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import mediation.helper.callbacks.OnFetchRemoteCallback;
import mediation.helper.cubiad.NativeAdView.CubiBannerAd;
import mediation.helper.cubiad.NativeAdView.CubiInterstitialAd;
import mediation.helper.cubiad.NativeAdView.CubiNativeAd;
import mediation.helper.cubiad.NativeAdView.GeneralInfo;
import mediation.helper.util.PrefManager;

public class AdHelperApplication extends Application {

    public static boolean isCATEnable = true;
    public static boolean isInit = false;
    //    @Override
//    public void onCreate() {
//        super.onCreate();
//        initMediation();
//    }
    static boolean testMode = false;
    static FirebaseAnalytics firebaseAnalytics = null;
    static OnFetchRemoteCallback onFetchRemoteCallbackListener;
    public static AdTimeLimits adTimeLimits;
    static String TAG = "de_adtest";
    @SuppressLint("StaticFieldLeak")
    static PrefManager prefManager;
    private static GeneralInfo generalInfo;
    private static CubiBannerAd cubiBannerAd;
    private static CubiInterstitialAd cubiInterstitialAd;
    private static CubiNativeAd cubiNativeAd;
    private static AdIDs adIDs;
    static  android.os.Handler handler = new android.os.Handler();

    public static void initMediation(Context context) {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Map <String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                for (String adapterClass : statusMap.keySet()) {
                    AdapterStatus status = statusMap.get(adapterClass);
                    Log.d("MyApp", String.format(
                            "Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass, status.getDescription(), status.getLatency()));
                }

                // Start loading ads here...
            }
        });
    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics;
    }

    public static void setFirebaseAnalytics(@NonNull FirebaseAnalytics analytics) {
        firebaseAnalytics = analytics;
    }

    public static boolean getTestMode() {
        return testMode;
    }


static int APP_CURRENT_VERSION = -1;

    public static void getValuesFromConfig(@NonNull final FirebaseRemoteConfig mFirebaseConfig,int app_current_version, @NonNull final Context context, @NonNull OnFetchRemoteCallback onFetchRemoteCallback) {
        testMode = (0 != (context.getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        Log.e(TAG, "testMode: " + testMode);
        APP_CURRENT_VERSION = app_current_version;
        prefManager = new PrefManager(context);
        onFetchRemoteCallbackListener = onFetchRemoteCallback;
        FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseConfig.setConfigSettingsAsync(settings);
        mFirebaseConfig.setDefaultsAsync(R.xml.remote_config_default_values);
       handler.postDelayed(new Runnable() {
           @Override
           public void run() {
               fetchValues(mFirebaseConfig, context);
           }
       }, 0);

        if (!verifyInstallerId(context)) {
//            KEY_PRIORITY_BANNER_AD = new Integer[]{MediationAdHelper.AD_CUBI_IT};
//            KEY_PRIORITY_INTERSTITIAL_AD = new Integer[]{MediationAdHelper.AD_CUBI_IT};
//            KEY_PRIORITY_NATIVE_AD = new Integer[]{MediationAdHelper.AD_CUBI_IT};
        }
        //init admob
        initMediation(context);



    }

    private static void fetchValues(final FirebaseRemoteConfig mFirebaseConfig, final Context context) {
        mFirebaseConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener <Boolean>() {
            @Override
            public void onComplete(@NonNull Task <Boolean> task) {
                if (task.isSuccessful()) {
                    Log.d("TAG1", "onComplete: success ");
                    onFetchRemoteCallbackListener.onFetchValuesSuccess();
                    updateData(mFirebaseConfig, context);
                } else {
                    onFetchRemoteCallbackListener.onFetchValuesFailed();
                    Log.d("TAG1", "onComplete: faile");
                    updateData(mFirebaseConfig, context);
                }

            }

        });
    }

    /*private static void loadSharedPrefValues() {
        adIDs = new AdIDs();

       adIDs.setFb_native_id(prefManager.getString(FB_NATIVE_ID_KEY, ""));
        adIDs.setFb_banner_id(prefManager.getString(FB_BANNER_ID_KEY, ""));
        adIDs.setFb_interstitial_id(prefManager.getString(FB_INTERSTITIAL_ID_KEY, ""));

      *//*   adIDs.setAdmob_interstitial_id(prefManager.getString(ADMOB_INTERSTITIAL_ID_KEY, ""));
        adIDs.setAdmob_native_id(prefManager.getString(ADMOB_NATIVE_ID_KEY, ""));
        adIDs.setAdmob_banner_id(prefManager.getString(ADMOB_BANNER_ID_KEY, ""));
        adIDs.setAdmob_app_id(prefManager.getString(ADMOB_APP_ID_KEY, "")); *//*

        adIDs.setAdmob_interstitial_id("");
        adIDs.setAdmob_native_id("");
        adIDs.setAdmob_banner_id("");
        adIDs.setAdmob_app_id(prefManager.getString(ADMOB_APP_ID_KEY, ""));

       *//* adIDs.setTest_mode(prefManager.getBooleanTestMode(TEST_MODE_KEY));
        adIDs.setRelease(prefManager.getString(RELEASE_KEY, ""));*//*

        //show log here
        Log.d("DE_AdManager", String.format("FB_Banner: %s\nFB_Native: %s\nFB_INTERSTITIAL: %s\nAdM_Banner: %s\nADM_Native: %s\nADM_INTERSTITIAL: %s\nAdbAppId: %s ",
                adIDs.getFb_banner_id(), adIDs.getFb_native_id(), adIDs.getFb_interstitial_id(), adIDs.getAdmob_banner_id(), adIDs.getAdmob_native_id(), adIDs.getAdmob_interstitial_id(), adIDs.getAdmob_app_id()
                ));


    }*/

    private static void updateData(FirebaseRemoteConfig mFirebaseConfig, Context context) {
        //GENERAL INFO
        generalInfo = new GeneralInfo();
        generalInfo.setBannerAdPriority(mFirebaseConfig.getString(BANNER_AD_PRIORITY_KEY));
        generalInfo.setInterstitialAdPriority(mFirebaseConfig.getString(INTERSTITIAL_AD_PRIORITY_KEY));
        generalInfo.setNativeAdPriority(mFirebaseConfig.getString(NATIVE_AD_PRIORITY_KEY));
        generalInfo.setInterstitialAdFrequency(mFirebaseConfig.getString(INTERSTITIAL_AD_FREQUENCY_KEY));
        generalInfo.setInterstitialAdTimer(mFirebaseConfig.getString(INTERSTITIAL_AD_TIMER_KEY));
        Log.d("De_AdManager", String.format("Ads Priorities: Banner: %s -- Native:%s -- Interstitial: %s", mFirebaseConfig.getString(BANNER_AD_PRIORITY_KEY), mFirebaseConfig.getString(NATIVE_AD_PRIORITY_KEY), mFirebaseConfig.getString(INTERSTITIAL_AD_PRIORITY_KEY)));
        if (verifyInstallerId(context)) {
            if (generalInfo.getBannerAdPriority() != null || !generalInfo.getBannerAdPriority().isEmpty()) {
                if (generalInfo.getBannerAdPriority().equals("FACEBOOK")) {
                    KEY_PRIORITY_BANNER_AD = new Integer[]{
                            MediationAdHelper.AD_FACEBOOK,
                            MediationAdHelper.AD_ADMOB,
                            MediationAdHelper.AD_CUBI_IT};
                } else if (generalInfo.getBannerAdPriority().equals("ADMOB")) {
                    KEY_PRIORITY_BANNER_AD = new Integer[]{
                            MediationAdHelper.AD_ADMOB,
                            MediationAdHelper.AD_FACEBOOK,
                            MediationAdHelper.AD_CUBI_IT};
                } else {
                    KEY_PRIORITY_BANNER_AD = new Integer[]{
                            MediationAdHelper.AD_CUBI_IT,
                            MediationAdHelper.AD_ADMOB,
                            MediationAdHelper.AD_FACEBOOK};
                }
            }

            if (generalInfo.getInterstitialAdPriority() != null || !generalInfo.getInterstitialAdPriority().isEmpty()) {
                if (generalInfo.getInterstitialAdPriority().equals("FACEBOOK")) {
                    KEY_PRIORITY_INTERSTITIAL_AD = new Integer[]{
                            MediationAdHelper.AD_FACEBOOK,
                            MediationAdHelper.AD_ADMOB,
                            MediationAdHelper.AD_CUBI_IT};
                } else if (generalInfo.getInterstitialAdPriority().equals("ADMOB")) {
                    KEY_PRIORITY_INTERSTITIAL_AD = new Integer[]{
                            MediationAdHelper.AD_ADMOB,
                            MediationAdHelper.AD_FACEBOOK,
                            MediationAdHelper.AD_CUBI_IT};
                } else {
                    KEY_PRIORITY_INTERSTITIAL_AD = new Integer[]{
                            MediationAdHelper.AD_CUBI_IT,
                            MediationAdHelper.AD_ADMOB,
                            MediationAdHelper.AD_FACEBOOK};
                }
            }

            if (generalInfo.getNativeAdPriority() != null || !generalInfo.getNativeAdPriority().isEmpty()) {
                if (generalInfo.getNativeAdPriority().equals("FACEBOOK")) {
                    KEY_PRIORITY_NATIVE_AD = new Integer[]{
                            MediationAdHelper.AD_FACEBOOK,
                            MediationAdHelper.AD_ADMOB,
                            MediationAdHelper.AD_CUBI_IT};
                } else if (generalInfo.getNativeAdPriority().equals("ADMOB")) {
                    KEY_PRIORITY_NATIVE_AD = new Integer[]{
                            MediationAdHelper.AD_ADMOB,
                            MediationAdHelper.AD_FACEBOOK,
                            MediationAdHelper.AD_CUBI_IT};
                } else {
                    KEY_PRIORITY_NATIVE_AD = new Integer[]{
                            MediationAdHelper.AD_CUBI_IT,
                            MediationAdHelper.AD_ADMOB,
                            MediationAdHelper.AD_FACEBOOK};
                }
            }
        }
        //Enable or disable Call to Action button
        try {
            int val = Integer.parseInt(mFirebaseConfig.getString(ENABLE_CAT));
            if (val == 0) {
                isCATEnable = false;
            } else {
                isCATEnable = true;
            }
            Log.d(TAG, "isCATEnable: " + isCATEnable);
        } catch (Exception e) {
            Log.d(TAG, "updateData: " + e.getMessage());

        }
        //BANNER AD
        cubiBannerAd = new CubiBannerAd();

        cubiBannerAd.setBannerAdtitle(mFirebaseConfig.getString(BANNER_KEY_AD_TITLE));
        cubiBannerAd.setBannerAdbodyText(mFirebaseConfig.getString(BANNER_KEY_AD_BODY));
        cubiBannerAd.setBannerSquareIconUrl(mFirebaseConfig.getString(BANNER_KEY_SQUARE_ICON_URL));
        cubiBannerAd.setBannerAdUrlLink(mFirebaseConfig.getString(BANNER_KEY_AD_URL));
        cubiBannerAd.setBannerAdadvertiserName(mFirebaseConfig.getString(BANNER_KEY_AD_ADVERTISER_NAME));
        cubiBannerAd.setBannerAdcallToActionData(mFirebaseConfig.getString(BANNER_KEY_AD_CALL_TO_ACTION));
        cubiBannerAd.setBannerAdrating(mFirebaseConfig.getString(BANNER_KEY_AD_RATING));
        cubiBannerAd.setBannerAdfeedBack(mFirebaseConfig.getString(BANNER_KEY_AD_FEEDBACKS));
        //INTERSTITIAL AD
        cubiInterstitialAd = new CubiInterstitialAd();
        cubiInterstitialAd.setInterstitialAdTitle(mFirebaseConfig.getString(INTERSTITIAL_KEY_AD_TITLE));
        cubiInterstitialAd.setInterstitialAdBodyText(mFirebaseConfig.getString(INTERSTITIAL_KEY_AD_BODY));
        cubiInterstitialAd.setInterstitialSquareIconUrl(mFirebaseConfig.getString(INTERSTITIAL_KEY_SQUARE_ICON_URL));
        cubiInterstitialAd.setInterstitialFeatureIconUrl(mFirebaseConfig.getString(INTERSTITIAL_KEY_FEATURE_ICON_URL));
        cubiInterstitialAd.setInterstitialAdUrlLink(mFirebaseConfig.getString(INTERSTITIAL_KEY_AD_URL));
        cubiInterstitialAd.setInterstitialAdvertiserName(mFirebaseConfig.getString(INTERSTITIAL_KEY_AD_ADVERTISER_NAME));
        cubiInterstitialAd.setInterstitialAdCallToActionData(mFirebaseConfig.getString(INTERSTITIAL_KEY_AD_CALL_TO_ACTION));
        cubiInterstitialAd.setInterstitialAdRating(mFirebaseConfig.getString(INTERSTITIAL_KEY_AD_RATING));
        cubiInterstitialAd.setInterstitialAdFeedback(mFirebaseConfig.getString(INTERSTITIAL_KEY_AD_FEEDBACKS));
        //NATIVE AD
        cubiNativeAd = new CubiNativeAd();
        cubiNativeAd.setNativeAdtitle(mFirebaseConfig.getString(NATIVE_KEY_AD_TITLE));
        cubiNativeAd.setNativeAdbodyText(mFirebaseConfig.getString(NATIVE_KEY_AD_BODY));
        cubiNativeAd.setNativeAdIconUrl(mFirebaseConfig.getString(NATIVE_KEY_AD_ICON_URL));
        cubiNativeAd.setNativeAdImageUrl(mFirebaseConfig.getString(NATIVE_KEY_AD_IMAGE_URL));
        cubiNativeAd.setNativeAdUrlLink(mFirebaseConfig.getString(NATIVE_KEY_AD_URL));
        cubiNativeAd.setNativeAdadvertiserName(mFirebaseConfig.getString(NATIVE_KEY_AD_ADVERTISER_NAME));
        cubiNativeAd.setNativeAdcallToActionData(mFirebaseConfig.getString(NATIVE_KEY_AD_CALL_TO_ACTION));
        cubiNativeAd.setNativeAdrating(mFirebaseConfig.getString(NATIVE_KEY_AD_RATING));
        cubiNativeAd.setNativeAdfeedBack(mFirebaseConfig.getString(NATIVE_KEY_AD_FEEDBACKS));
        //set ad time limist
        adTimeLimits = new AdTimeLimits();
        String b = mFirebaseConfig.getString("can_skip");
        Log.i(TAG, "updateData: can skip check "+b+" : "+ !b.equals("0"));
        adTimeLimits.setCan_skip(!b.equals("0"));
        Log.i(TAG, "updateData: can skip "+ adTimeLimits.isCan_skip());
        Log.d(TAG, "banner_ad_time: " + (mFirebaseConfig.getString("banner_ad_time")));
        Log.d(TAG, "native_ad_time: " + (mFirebaseConfig.getString("native_ad_time")));
        Log.d(TAG, "interstitial_ad_time: " + (mFirebaseConfig.getString("interstitial_ad_time")));
        String banner = mFirebaseConfig.getString("banner_ad_time");
        String native_ad_time = mFirebaseConfig.getString("native_ad_time");
        String interstitial_ad_time = mFirebaseConfig.getString("interstitial_ad_time");
        String native_banner_ad_time = mFirebaseConfig.getString("native_banner_ad_time");
        if (banner.equals("") || banner.isEmpty()) {
            banner = "0";
        }
        if (native_ad_time.equals("") || native_ad_time.isEmpty()) {
            native_ad_time = "0";
        } if (interstitial_ad_time.equals("") || interstitial_ad_time.isEmpty()) {
            interstitial_ad_time = "0";
        } if (native_banner_ad_time.equals("") || native_banner_ad_time.isEmpty()) {
            native_banner_ad_time = "0";
        }
        adTimeLimits.setBanner_ad_time(Long.parseLong(banner));
        adTimeLimits.setNative_ad_time(Long.parseLong(native_ad_time));
        adTimeLimits.setInterstitial_ad_time(Long.parseLong(interstitial_ad_time));
        adTimeLimits.setInterstitial_ad_time(Long.parseLong(native_banner_ad_time));
        //IDS
        adIDs = new AdIDs();

        int newAppVersion = Integer.parseInt(mFirebaseConfig.getString(NEW_APP_VERSION));
        Log.d(TAG, "newAppVersion: "+ newAppVersion);
        Log.d(TAG, "CurrentVersion: "+ APP_CURRENT_VERSION);
        if((newAppVersion !=-1 && APP_CURRENT_VERSION!=-1 )&&(newAppVersion > APP_CURRENT_VERSION)){
            Log.d(TAG, "loading ids for new update version: ");

            adIDs.setFb_banner_id(mFirebaseConfig.getString(NEW_FB_BANNER_ID_KEY));
            adIDs.setFb_interstitial_id(mFirebaseConfig.getString(NEW_FB_INTERSTITIAL_ID_KEY));
            adIDs.setFb_native_id(mFirebaseConfig.getString(NEW_FB_NATIVE_ID_KEY));
            //SET NEW ADMOB
            adIDs.setAdmob_app_id(mFirebaseConfig.getString(NEW_ADMOB_APP_ID_KEY));
            adIDs.setAdmob_banner_id(mFirebaseConfig.getString(NEW_ADMOB_BANNER_ID_KEY));
            adIDs.setAdmob_native_id(mFirebaseConfig.getString(NEW_ADMOB_NATIVE_ID_KEY));
            adIDs.setAdmob_interstitial_id(mFirebaseConfig.getString(NEW_ADMOB_INTERSTITIAL_ID_KEY));

        }else {
            Log.d(TAG, "loading for current/old version ");
            //set fb ids
            adIDs.setFb_banner_id(mFirebaseConfig.getString(FB_BANNER_ID_KEY));
            adIDs.setFb_interstitial_id(mFirebaseConfig.getString(FB_INTERSTITIAL_ID_KEY));
            adIDs.setFb_native_id(mFirebaseConfig.getString(FB_NATIVE_ID_KEY));
            //SET ADMOB
            adIDs.setAdmob_app_id(mFirebaseConfig.getString(ADMOB_APP_ID_KEY));
            adIDs.setAdmob_banner_id(mFirebaseConfig.getString(ADMOB_BANNER_ID_KEY));
            adIDs.setAdmob_native_id(mFirebaseConfig.getString(ADMOB_NATIVE_ID_KEY));
            adIDs.setAdmob_interstitial_id(mFirebaseConfig.getString(ADMOB_INTERSTITIAL_ID_KEY));
        }
        Log.d(TAG, String.format("FB_Banner: %s\nFB_Native: %s\nFB_INTERSTITIAL: %s\nAdM_Banner: %s\nADM_Native: %s\nADM_INTERSTITIAL: %s\nAdbAppId: %s ",
                adIDs.getFb_banner_id(), adIDs.getFb_native_id(), adIDs.getFb_interstitial_id(), adIDs.getAdmob_banner_id(), adIDs.getAdmob_native_id(), adIDs.getAdmob_interstitial_id(), adIDs.getAdmob_app_id()
        ));

        //check test-mode
        updateSharedPreference(adIDs);

        isInit = true;

    }

    public static void updateSharedPreference(AdIDs adIDs) {
        Log.d(TAG, "updateSharedPreference: ");
        if (prefManager != null) {
            //put fb ids
            prefManager.setString(FB_BANNER_ID_KEY, adIDs.getFb_banner_id());
            prefManager.setString(FB_INTERSTITIAL_ID_KEY, adIDs.getFb_interstitial_id());
            prefManager.setString(FB_NATIVE_ID_KEY, adIDs.getFb_native_id());
            //put admob
            prefManager.setString(ADMOB_APP_ID_KEY, adIDs.getAdmob_app_id());
            prefManager.setString(ADMOB_BANNER_ID_KEY, adIDs.getAdmob_banner_id());
            prefManager.setString(ADMOB_NATIVE_ID_KEY, adIDs.getAdmob_native_id());
            prefManager.setString(ADMOB_INTERSTITIAL_ID_KEY, adIDs.getAdmob_interstitial_id());
            //testmod
            /*prefManager.setBoolean(TEST_MODE_KEY, adIDs.isTest_mode());
            prefManager.setString(RELEASE_KEY, adIDs.getRelease());*/
            //  loadSharedPrefValues();
            onFetchRemoteCallbackListener.onUpdateSuccess(adIDs.getAdmob_app_id());
            //logger
        } else {
            Log.d(TAG, "updateSharedPreference: pref manager is null");
        }

    }

    public static AdIDs getAdIDs() {
        return adIDs;
    }

    public static GeneralInfo getGeneralInfo() {
        return generalInfo;
    }

    public static CubiBannerAd getCubiBannerAd() {
        return cubiBannerAd;
    }

    public static CubiInterstitialAd getCubiInterstitialAd() {
        return cubiInterstitialAd;
    }

    public static CubiNativeAd getCubiNativeAd() {
        return cubiNativeAd;
    }

    static boolean verifyInstallerId(Context context) {
        // A list with valid installers package name
        List <String> validInstallers = new ArrayList <>(Arrays.asList("com.android.vending", "com.google.android.feedback"));

        // The package name of the app that has installed your app
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());

        // true if your app has been downloaded from Play Store
        return installer != null && validInstallers.contains(installer);
    }

    public Context getContext() {
        return AdHelperApplication.this;
    }
}
