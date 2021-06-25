package mediation.helper;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import mediation.helper.cubiad.NativeAdView.CubiBannerAd;
import mediation.helper.cubiad.NativeAdView.CubiInterstitialAd;
import mediation.helper.cubiad.NativeAdView.CubiNativeAd;
import mediation.helper.cubiad.NativeAdView.GeneralInfo;

import static mediation.helper.util.Constant.BANNER_AD_PRIORITY_KEY;
import static mediation.helper.util.Constant.BANNER_KEY_AD_ADVERTISER_NAME;
import static mediation.helper.util.Constant.BANNER_KEY_AD_BODY;
import static mediation.helper.util.Constant.BANNER_KEY_AD_CALL_TO_ACTION;
import static mediation.helper.util.Constant.BANNER_KEY_AD_FEEDBACKS;
import static mediation.helper.util.Constant.BANNER_KEY_AD_RATING;
import static mediation.helper.util.Constant.BANNER_KEY_AD_TITLE;
import static mediation.helper.util.Constant.BANNER_KEY_AD_URL;
import static mediation.helper.util.Constant.BANNER_KEY_SQUARE_ICON_URL;
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

public class AdHelperApplication extends Application {

    private static GeneralInfo generalInfo;
    private static CubiBannerAd cubiBannerAd;
    private static CubiInterstitialAd cubiInterstitialAd;
    private static CubiNativeAd cubiNativeAd;

    public static void initMediation(Context context) {
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

    public Context getContext() {
        return AdHelperApplication.this;
    }


//    @Override
//    public void onCreate() {
//        super.onCreate();
//        initMediation();
//    }

    public static void getValuesFromConfig(FirebaseRemoteConfig mFirebaseConfig, Context context) {

        FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseConfig.setConfigSettingsAsync(settings);
        mFirebaseConfig.setDefaultsAsync(R.xml.remote_config_default_values);
        fetchValues(mFirebaseConfig, context);

        if (!verifyInstallerId(context)) {
//            KEY_PRIORITY_BANNER_AD = new Integer[]{MediationAdHelper.AD_CUBI_IT};
//            KEY_PRIORITY_INTERSTITIAL_AD = new Integer[]{MediationAdHelper.AD_CUBI_IT};
//            KEY_PRIORITY_NATIVE_AD = new Integer[]{MediationAdHelper.AD_CUBI_IT};
        }
    }

    private static void fetchValues(final FirebaseRemoteConfig mFirebaseConfig, final Context context) {
        mFirebaseConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    Log.d("TAG1", "onComplete: success ");
                } else {
                    Log.d("TAG1", "onComplete: faile");
                }
                updateData(mFirebaseConfig, context);
            }

        });
    }

    private static void updateData(FirebaseRemoteConfig mFirebaseConfig, Context context) {
        //GENERAL INFO
        generalInfo = new GeneralInfo();
        generalInfo.setBannerAdPriority(mFirebaseConfig.getString(BANNER_AD_PRIORITY_KEY));
        generalInfo.setInterstitialAdPriority(mFirebaseConfig.getString(INTERSTITIAL_AD_PRIORITY_KEY));
        generalInfo.setNativeAdPriority(mFirebaseConfig.getString(NATIVE_AD_PRIORITY_KEY));
        generalInfo.setInterstitialAdFrequency(mFirebaseConfig.getString(INTERSTITIAL_AD_FREQUENCY_KEY));
        generalInfo.setInterstitialAdTimer(mFirebaseConfig.getString(INTERSTITIAL_AD_TIMER_KEY));

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
}
