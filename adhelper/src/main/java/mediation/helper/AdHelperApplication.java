package mediation.helper;

import static mediation.helper.util.Constant.ADMOB_APP_ID_KEY;
import static mediation.helper.util.Constant.ADMOB_BANNER_ID_KEY;
import static mediation.helper.util.Constant.ADMOB_INTERSTITIAL_ID_KEY;
import static mediation.helper.util.Constant.ADMOB_NATIVE_ID_KEY;
import static mediation.helper.util.Constant.ADMOB_OPEN_AD_ID_KEY;
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
import static mediation.helper.util.Constant.NEW_ADMOB_OPEN_AD_ID_KEY;
import static mediation.helper.util.Constant.NEW_APP_VERSION;
import static mediation.helper.util.Constant.NEW_FB_BANNER_ID_KEY;
import static mediation.helper.util.Constant.NEW_FB_INTERSTITIAL_ID_KEY;
import static mediation.helper.util.Constant.NEW_FB_NATIVE_ID_KEY;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import mediation.helper.callbacks.OnFetchRemoteCallback;
import mediation.helper.config.PlaceholderConfig;
import mediation.helper.config.SessionConfig;
import mediation.helper.cubiad.NativeAdView.CubiBannerAd;
import mediation.helper.cubiad.NativeAdView.CubiInterstitialAd;
import mediation.helper.cubiad.NativeAdView.CubiNativeAd;
import mediation.helper.cubiad.NativeAdView.GeneralInfo;
import mediation.helper.util.Constant;
import mediation.helper.util.PrefManager;

public class AdHelperApplication extends Application {

    public static boolean isCATEnable = true;
    public static boolean isInit = false;
    public static int admobRequestInterFaild = 0;
    public static int admobRequestBannerFaild = 0;
    public static int admobRequestNativeFaild = 0;
    public static int admobRequestNativeBannerFaild = 0;
    public static int admobRequestExitFaild = 0;
    public static int fbRequestInterFaild = 0;
    public static int fbRequestBannerFaild = 0;
    public static int fbRequestNativeFaild = 0;
    public static int fbRequestNativeBannerFaild = 0;
    public static int fbRequestExitFaild = 0;
    public static boolean isAppOpenAdEnable = true;
    public static int testFBLoad = 0;
    public static int nativeAdLoad = 0;
    public static int adMobnativeAdLoad = 0;//test
    public static int loadAdmobInters = 0;
    public static boolean enableBorder = false;//default
    public static boolean enableDarkMode = true;//default
    //    @Override
//    public void onCreate() {
//        super.onCreate();
//        initMediation();
//    }
    static boolean testMode = false;
    static FirebaseAnalytics firebaseAnalytics = null;
    static OnFetchRemoteCallback onFetchRemoteCallbackListener;
    public static AdTimeLimits adTimeLimits;
    public static int ADMOB_OPENAD_REQUEST_FAILED = 0;
    @SuppressLint("StaticFieldLeak")
    static PrefManager prefManager;
    private static GeneralInfo generalInfo;
    private static CubiBannerAd cubiBannerAd;
    private static CubiInterstitialAd cubiInterstitialAd;
    private static CubiNativeAd cubiNativeAd;
    private static AdIDs adIDs;
    static android.os.Handler handler = new android.os.Handler();
    public static boolean canShowInterstitial = true;
    public static boolean adMobBannerSession = true;
    public static boolean adMobNativeSession = true;
    public static boolean adMobNativeBannerSession = true;
    public static boolean adMobInterstialSession = true;
    public static int interstitialClickAdCounter = 0;
    public static long INTERSTITIAL_CLICK_LIMIT = 3;//DEFAULT
    public static long OPENAPP_AD_CLICK_LIMIT = 3;//DEFAULT
    public static boolean isAdPreloadEnable = false;//DEFAULT
    static String TAG = "de_adherlper";
    public static void initMediation(Context context) {
        List<String> testDeviceIds = Arrays.asList("49CB5184DFD9ACB6581265EA7DF47D8A");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
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

    public static void getValuesFromConfig(@NonNull final FirebaseRemoteConfig mFirebaseConfig, int app_current_version, @NonNull final Context context, @NonNull OnFetchRemoteCallback onFetchRemoteCallback) {
        testMode = (0 != (context.getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        Log.e(TAG, "testMode: " + testMode);
        Log.i(TAG, "test prebuild --------------------------------------> wow it works <-----------------------------------");
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
        mFirebaseConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
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

    private static void checkOpenAddIsEnable(FirebaseRemoteConfig remoteConfig) {
        String a = remoteConfig.getString("enable_app_openad");
        Log.d(TAG, "checkOpenAddIsEnable: value is " + a);
        if (a.toLowerCase().equals("true") || a.toLowerCase().equals("1") || a.toLowerCase().equals("on") || a.toLowerCase().equals("yes")) {
            Log.d(TAG, "checkOpenAddIsEnable: " + true);
            isAppOpenAdEnable = true;
        } else {
            isAppOpenAdEnable = false;
        }
    }

    public static PlaceholderConfig placeholderConfig = null;

    private static void loadPlaceholderData(FirebaseRemoteConfig firebaseRemoteConfig) {
        Log.d(TAG, "loadPlaceholderData: " + firebaseRemoteConfig.getString("placeholders").toString());
        try {
            String val = null;
            if (firebaseRemoteConfig == null)
                val = Constant.DEFUALT_PLACEHOLDER_JSON;
            else
                val = firebaseRemoteConfig.getString("placeholders");
            if (val == null) {
                val = Constant.DEFUALT_PLACEHOLDER_JSON;
            }
            if (val.isEmpty() || val.equals(" ")) {
                val = Constant.DEFUALT_PLACEHOLDER_JSON;
            }
            placeholderConfig = new Gson().fromJson(val, PlaceholderConfig.class);
        } catch (Exception e) {

            if (e instanceof NullPointerException) {
                try {
                    String val = Constant.DEFUALT_PLACEHOLDER_JSON;
                    placeholderConfig = new Gson().fromJson(val, PlaceholderConfig.class);
                } catch (Exception ed) {
                    ed.printStackTrace();
                }
            } else {
                try {
                    String val = Constant.DEFUALT_PLACEHOLDER_JSON;
                    placeholderConfig = new Gson().fromJson(val, PlaceholderConfig.class);
                } catch (Exception ed) {
                    ed.printStackTrace();
                }
            }

            e.printStackTrace();
        }

    }

    public static SessionConfig sessionConfig;
    private static String admobLimit = "false";
    public static boolean applyLimitOnAdmob = false;

    public static boolean isAdmobInLimit() {
        if (admobLimit.equals("true") || admobLimit.equals("on") || admobLimit.equals("1") || admobLimit.equals("yes") || admobLimit.equals("TRUE")) {
            return true;
        } else {
            return false;
        }
    }

    private static void fetchSessions(FirebaseRemoteConfig firebaseRemoteConfig) {
        try {
            String TAG = Constant.TAG;
            String val = firebaseRemoteConfig.getString("sessions");
            Log.d(TAG, "fetchSessions: "+ val);
            if(val.isEmpty())
                val = Constant.DEFUALT_SESSIONS;

            sessionConfig = new Gson().fromJson(val, SessionConfig.class);
            Collection<Integer> d = sessionConfig.admob_sessions.values();
            Log.d(TAG, "fetchSessions: "+ d);

            admobLimit = firebaseRemoteConfig.getString("is_admob_limit");
        }catch (Exception e){
            Log.d(TAG, "fetchSessions: "+ e.getMessage());
        }

        //Log.d(TAG, "fetchPlaceholder: banner "+ placeholderConfig.interstitial.MAIN_ACTIVITY);


    }

    private static void getAdsThemeValues(FirebaseRemoteConfig remoteConfig) {
        try {
            if (remoteConfig == null)
                return;
            String borderVal = remoteConfig.getString("enable_border").toLowerCase();
            String theme = remoteConfig.getString("enable_dark_mode");
            if (!borderVal.isEmpty()) {
                if (borderVal.equals("1") || borderVal.equals("true") || borderVal.equals("yes")) {
                    enableBorder = true;
                } else {
                    enableBorder = false;
                }
            } else {
                enableBorder = false;
            }
            //theme
            if (!theme.isEmpty()) {
                if (theme.equals("1") || theme.equals("true") || theme.equals("yes")) {
                    enableDarkMode = true;
                } else {
                    enableDarkMode = false;
                }
            } else {
                enableDarkMode = false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static boolean checkAddPreload(String val) {
        String lcVal = val.toLowerCase();
        if (lcVal.equals("0") || lcVal.equals("off") || lcVal.equals("no") || lcVal.equals("false")) {
            return false;
        } else
            return true;
    }

    private static void updateData(FirebaseRemoteConfig mFirebaseConfig, Context context) {
        //appopen ad
        checkOpenAddIsEnable(mFirebaseConfig);
        //PLACEHOLDERS
        loadPlaceholderData(mFirebaseConfig);
        //sessions
        fetchSessions(mFirebaseConfig);
        //fetch theme
        getAdsThemeValues(mFirebaseConfig);
        //check preload
        String preload = mFirebaseConfig.getString("enable_preload");
        isAdPreloadEnable = checkAddPreload(preload);
        if (preload == null || preload.isEmpty()) {
            preload = "0";
        }
        //CLICK LIMIT
        try {
            INTERSTITIAL_CLICK_LIMIT = mFirebaseConfig.getLong("inter_click_limit");
        } catch (Exception e) {
            INTERSTITIAL_CLICK_LIMIT = 3;
        } //OPENAPP AD CLICK
        try {
            OPENAPP_AD_CLICK_LIMIT = mFirebaseConfig.getLong("admob_open_ad_interval");
        } catch (Exception e) {
            OPENAPP_AD_CLICK_LIMIT = 3;
            e.printStackTrace();
        }
        //prefManager.setLong(ADMOB_OPEN_AD_ID_KEY,OPENAPP_AD_CLICK_LIMIT);
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
                if (generalInfo.getBannerAdPriority().equalsIgnoreCase("facebook")) {
                    KEY_PRIORITY_BANNER_AD = new Integer[]{
                            MediationAdHelper.AD_FACEBOOK,
                            MediationAdHelper.AD_ADMOB,
                            MediationAdHelper.AD_CUBI_IT};
                } else if (generalInfo.getBannerAdPriority().equalsIgnoreCase("admob")) {
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
        Log.i(TAG, "updateData: can skip check " + b + " : " + !b.equals("0"));
        adTimeLimits.setCan_skip(!b.equals("0"));

        String banner = mFirebaseConfig.getString("banner_ad_time");
        String native_ad_time = mFirebaseConfig.getString("native_ad_time");
        String interstitial_ad_time = mFirebaseConfig.getString("interstitial_ad_time");
        String native_banner_ad_time = mFirebaseConfig.getString("native_banner_ad_time");
        if (banner.equals("") || banner.isEmpty()) {
            banner = "0";
        }
        if (native_ad_time.equals("") || native_ad_time.isEmpty()) {
            native_ad_time = "0";
        }
        if (interstitial_ad_time.equals("") || interstitial_ad_time.isEmpty()) {
            interstitial_ad_time = "0";
        }
        if (native_banner_ad_time.equals("") || native_banner_ad_time.isEmpty()) {
            native_banner_ad_time = "0";
        }
        adTimeLimits.setBanner_ad_time(Long.parseLong(banner));
        adTimeLimits.setNative_ad_time(Long.parseLong(native_ad_time));
        adTimeLimits.setInterstitial_ad_time(Long.parseLong(interstitial_ad_time));
        adTimeLimits.setInterstitial_ad_time(Long.parseLong(native_banner_ad_time));
        //IDS
        adIDs = new AdIDs();
        String version = mFirebaseConfig.getString(NEW_APP_VERSION);
        if (version.isEmpty())
            version = "-1";

        int newAppVersion = APP_CURRENT_VERSION;
        try {
            newAppVersion = Integer.parseInt(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "newAppVersion: " + newAppVersion);
        Log.d(TAG, "CurrentVersion: " + APP_CURRENT_VERSION);
        if ((newAppVersion != -1 && APP_CURRENT_VERSION != -1) && (newAppVersion > APP_CURRENT_VERSION)) {
            Log.d(TAG, "loading ids for new update version: ");

            adIDs.setFb_banner_id(mFirebaseConfig.getString(NEW_FB_BANNER_ID_KEY));
            adIDs.setFb_interstitial_id(mFirebaseConfig.getString(NEW_FB_INTERSTITIAL_ID_KEY));
            adIDs.setFb_native_id(mFirebaseConfig.getString(NEW_FB_NATIVE_ID_KEY));
            //SET NEW ADMOB
            adIDs.setAdmob_app_id(mFirebaseConfig.getString(NEW_ADMOB_APP_ID_KEY));
            adIDs.setAdmob_banner_id(mFirebaseConfig.getString(NEW_ADMOB_BANNER_ID_KEY));
            adIDs.setAdmob_open_ad_id(mFirebaseConfig.getString(NEW_ADMOB_OPEN_AD_ID_KEY));
            adIDs.setAdmob_native_id(mFirebaseConfig.getString(NEW_ADMOB_NATIVE_ID_KEY));
            adIDs.setAdmob_interstitial_id(mFirebaseConfig.getString(NEW_ADMOB_INTERSTITIAL_ID_KEY));

        } else {
            Log.d(TAG, "loading for current/old version ");
            //set fb ids
            adIDs.setFb_banner_id(mFirebaseConfig.getString(FB_BANNER_ID_KEY));

            adIDs.setFb_interstitial_id(mFirebaseConfig.getString(FB_INTERSTITIAL_ID_KEY));
            adIDs.setFb_native_id(mFirebaseConfig.getString(FB_NATIVE_ID_KEY));
            //SET ADMOB
            adIDs.setAdmob_open_ad_id(mFirebaseConfig.getString(ADMOB_OPEN_AD_ID_KEY));
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
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));

        // The package name of the app that has installed your app
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());

        // true if your app has been downloaded from Play Store
        return installer != null && validInstallers.contains(installer);
    }

    public Context getContext() {
        return AdHelperApplication.this;
    }
}
