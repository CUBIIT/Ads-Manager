package mediation.helper.AnalyticsEvents;

import android.os.Bundle;
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
        Bundle bundle = new Bundle();
        bundle.putString("name", "onBannerAdCalledEvents");
        analytics.logEvent(EVENT_BANNER_CALLED, bundle);
    }

    public static void onBannerAdSuccessEvents(int type) {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putString("name", "onBannerAdSuccessEvents");
        analytics.logEvent(EVENT_BANNER_SUCCESS, bundle);
    }

    public static void onBannerAdErrorEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("name", "onBannerAdErrorEvents");
        analytics.logEvent(EVENT_BANNER_ERROR, bundle);
    }

    public static void onBannerAdAdClickedEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("name", "onBannerAdAdClickedEvents");
        analytics.logEvent(EVENT_BANNER_CLICKED, bundle);
    }

    public static void onNativeAdAdClickedEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("name", "onNativeAdAdClickedEvents");
        analytics.logEvent(EVENT_NATIVE_CLICKED, bundle);
    }

    public static void onNativeAdCalledEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("name", "onNativeAdCalledEvents");
        analytics.logEvent(EVENT_NATIVE_CALLED, bundle);
    }

    public static void onNativeAdSuccessEvents(int type) {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putString("name", "EVENT_NATIVE_SUCCESS");
        analytics.logEvent(EVENT_NATIVE_SUCCESS, bundle);
    }

    public static void onNativeAdErrorEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("name", "EVENT_NATIVE_ERROR");
        analytics.logEvent(EVENT_NATIVE_ERROR, bundle);
    }

    //    NATIVE BANNER
    public static void onNativeBannerAdClickedEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("name", "EVENT_NATIVE_BANNER_CLICKED");
        analytics.logEvent(EVENT_NATIVE_BANNER_CLICKED, bundle);
    }

    public static void onNativeBannerAdCalledEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("name", "EVENT_NATIVE_CALLED");
        analytics.logEvent(EVENT_NATIVE_BANNER_CALLED, bundle);
    }

    public static void onNativeBannerAdSuccessEvents(int type) {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("name", "EVENT_NATIVE_BANNER_SUCCESS");
        bundle.putInt("type", type);
        analytics.logEvent(EVENT_NATIVE_BANNER_SUCCESS, bundle);
    }

    public static void onNativeBannerAdErrorEvents() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("name", "EVENT_NATIVE_BANNER_ERROR");
        analytics.logEvent(EVENT_NATIVE_BANNER_ERROR, bundle);
    }

    public static void onInterstitialAdCalledEvent() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("name", "EVENT_INTERSTITIAL_CALLED");
        analytics.logEvent(EVENT_INTERSTITIAL_CALLED, bundle);
    }

    public static void onInterstitialAdSuccessEvent(int type) {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putString("name", "EVENT_INTERSTITIAL_SUCCESS");
        analytics.logEvent(EVENT_INTERSTITIAL_SUCCESS, bundle);
    }

    public static void onInterstitialAdErrorEvent() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("name", "EVENT_INTERSTITIAL_ERROR");
        analytics.logEvent(EVENT_INTERSTITIAL_ERROR, bundle);
    }

    public static void onInterstitialAdClickedEvent() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("name", "EVENT_INTERSTITIAL_CLICKED");
        analytics.logEvent(EVENT_INTERSTITIAL_CLICKED, bundle);
    }
//

    public static void onDialogAdClickedEvent() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("name", "EVENT_EXIT_DIALOG_AD_CLICKED");
        analytics.logEvent(EVENT_EXIT_DIALOG_AD_CLICKED, bundle);
    }

    public static void onDialogAdCalledEvent() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("name", "EVENT_EXIT_DIALOG_AD_CALLED");
        analytics.logEvent(EVENT_EXIT_DIALOG_AD_CALLED, bundle);
    }

    public static void onDialogAdSuccessEvent(int type) {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putString("name", "EVENT_EXIT_DIALOG_AD_SUCCESS");
        analytics.logEvent(EVENT_EXIT_DIALOG_AD_SUCCESS, bundle);
    }

    public static void onDialogAdErrorEvent() {
        FirebaseAnalytics analytics = AdHelperApplication.getFirebaseAnalytics();
        if (analytics == null) {
            Log.d(TAG, "Unable to log events! FirebaseAnalytics object is null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("name", "EVENT_EXIT_DIALOG_AD_ERROR");
        analytics.logEvent(EVENT_EXIT_DIALOG_AD_ERROR, bundle);
    }

}
