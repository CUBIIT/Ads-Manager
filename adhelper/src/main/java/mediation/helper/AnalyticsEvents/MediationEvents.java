package mediation.helper.AnalyticsEvents;

import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import mediation.helper.AdHelperApplication;

public class MediationEvents {
    static String EVENT_BANNER_CALLED = "load_banner_ad";
    static String EVENT_BANNER_SUCCESS = "success_banner_ad";
    static String EVENT_BANNER_ERROR = "error_banner_ad";
    static String EVENT_BANNER_CLICKED = "clicked_banner_ad";

    static String EVENT_NATIVE_CALLED = "load_native_ad";
    static String EVENT_NATIVE_SUCCESS = "success_native_ad";
    static String EVENT_NATIVE_ERROR = "error_native_ad";
    static String EVENT_NATIVE_CLICKED = "clicked_native_ad";

    static String EVENT_INTERSTITIAL_CALLED = "load_interstitial_ad";
    static String EVENT_INTERSTITIAL_SUCCESS = "success_interstitial_ad";
    static String EVENT_INTERSTITIAL_ERROR = "error_interstitial_ad";
    static String EVENT_INTERSTITIAL_CLICKED = "clicked_interstitial_ad";

    static String EVENT_NATIVE_BANNER_CALLED = "load_native_banner_ad";
    static String EVENT_NATIVE_BANNER_SUCCESS = "success_native_banner_ad";
    static String EVENT_NATIVE_BANNER_ERROR = "error_native_banner_ad";
    static String EVENT_NATIVE_BANNER_CLICKED = "clicked_native_banner_ad";

    static String EVENT_EXIT_DIALOG_AD_CALLED = "load_exit_dialog_ad";
    static String EVENT_EXIT_DIALOG_AD_SUCCESS = "success_exit_dialog_ad";
    static String EVENT_EXIT_DIALOG_AD_ERROR = "error_exit_dialog_ad";
    static String EVENT_EXIT_DIALOG_AD_CLICKED = "clicked_exit_dialog_ad";


    static String TAG = "De_AdManager_Events:";

    public static void onBannerAdCalledEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onBannerAdSuccessEvents(int type) {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onBannerAdErrorEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onBannerAdAdClickedEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onNativeAdAdClickedEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onNativeAdCalledEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onNativeAdSuccessEvents(int type) {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onNativeAdErrorEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    //    NATIVE BANNER
    public static void onNativeBannerAdClickedEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onNativeBannerAdCalledEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onNativeBannerAdSuccessEvents(int type) {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onNativeBannerAdErrorEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onInterstitialAdCalledEvent() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onInterstitialAdSuccessEvent(int type) {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onInterstitialAdErrorEvent() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onInterstitialAdClickedEvent() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }
//

    public static void onDialogAdClickedEvent() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onDialogAdCalledEvent() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onDialogAdSuccessEvent(int type) {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

    public static void onDialogAdErrorEvent() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }

    }

}
