package mediation.helper.front;

import android.app.Activity;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.LoadAdError;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

import mediation.helper.MediationAdHelper;
import mediation.helper.util.Constant;


/**
 * Created by CUBI-IT
 */

public class MediationAdInterstitial {
    private static OnInterstitialAdListener onInterstitialAdListener;
    private static String facebookKey;
    private static String admobKey;
    private static WeakReference<Activity> activityRef;

    private static ArrayList<Integer> adPriorityList;

    public static void showFacebookFrontAd(Activity activity, String facebookKey, OnInterstitialAdListener onInterstitialAdListener) {
        showFrontAD(activity, facebookKey, null, MediationAdHelper.AD_FACEBOOK, onInterstitialAdListener);
    }

    public static void showAdmobFrontAd(Activity activity, String admobKey, OnInterstitialAdListener onInterstitialAdListener) {
        showFrontAD(activity, null, admobKey, MediationAdHelper.AD_ADMOB, onInterstitialAdListener);
    }

    public static void showFrontAD(Activity activity, String facebookKey, final String admobKey, int adPriority, OnInterstitialAdListener onInterstitialAdListener) {
        Integer[] tempAdPriorityList = new Integer[2];
        tempAdPriorityList[0] = adPriority;
        if (adPriority == MediationAdHelper.AD_FACEBOOK) {
            tempAdPriorityList[1] = MediationAdHelper.AD_ADMOB;
        } else {
            tempAdPriorityList[1] = MediationAdHelper.AD_FACEBOOK;
        }

        showFrontAD(activity, facebookKey, admobKey, tempAdPriorityList, onInterstitialAdListener);
    }

    public static void showFrontAD(Activity activity, String facebookKey, final String admobKey, Integer[] tempAdPriorityList, OnInterstitialAdListener onInterstitialAdListener) {
        if (tempAdPriorityList == null || tempAdPriorityList.length == 0) {
            throw new RuntimeException("You have to select priority type ADMOB/FACEBOOK/TNK");
        }

        MediationAdInterstitial.adPriorityList = new ArrayList<>(Arrays.asList(tempAdPriorityList));
        try {
            MediationAdInterstitial.activityRef = new WeakReference<>(activity);
            MediationAdInterstitial.facebookKey = facebookKey;
            MediationAdInterstitial.admobKey = admobKey;
            MediationAdInterstitial.onInterstitialAdListener = onInterstitialAdListener;
            selectAd();
        } catch (Exception e) {
            e.printStackTrace();
            if (onInterstitialAdListener != null) {
                onInterstitialAdListener.onError(e.toString());
            }
            finishAd();
        }

    }

    private static void selectAd() {
        int adPriority = adPriorityList.remove(0);
        switch (adPriority) {
            case MediationAdHelper.AD_FACEBOOK:
                showFacebookFrontAd();
                break;
            case MediationAdHelper.AD_ADMOB:
                showAdmobFrontAd();
                break;
            default:
                onInterstitialAdListener.onError("You have to select priority type ADMOB or FACEBOOK");
                finishAd();
        }
    }

    private static void showFacebookFrontAd() {
        if (MediationAdHelper.isSkipFacebookAd(activityRef.get())) {
            Log.e(MediationAdHelper.TAG, "[FACEBOOK FRONT AD]Error: " + Constant.ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED);
            onError(Constant.ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED);
            return;
        }
        final com.facebook.ads.InterstitialAd facebookFrontAD = new com.facebook.ads.InterstitialAd(activityRef.get(), facebookKey);

        if (onInterstitialAdListener != null) {
            onInterstitialAdListener.onFacebookAdCreated(facebookFrontAD);
        }
        // Set listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new com.facebook.ads.InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
                Log.d(MediationAdHelper.TAG, "[FACEBOOK FRONT AD]Displayed");
            }
            @Override
            public void onInterstitialDismissed(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK FRONT AD]Dismissed");
                // Interstitial dismissed callback
                if (onInterstitialAdListener != null) {
                    onInterstitialAdListener.onDismissed(MediationAdHelper.AD_FACEBOOK);
                    finishAd();
                }
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(MediationAdHelper.TAG, "[FACEBOOK FRONT AD]Error: " + adError.getErrorMessage());
                MediationAdInterstitial.onError(adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK FRONT AD]Loaded");
                // Show the ad when it's done loading.
                try {
                    if (facebookFrontAD != null) {
                        onInterstitialAdListener.onBeforeAdShow();
                        facebookFrontAD.show();
                    }
                } catch (Exception e) {
                    MediationAdInterstitial.onError(e.getMessage());
                    return;
                }

                if (onInterstitialAdListener != null) {
                    onInterstitialAdListener.onLoaded(MediationAdHelper.AD_FACEBOOK);
                }
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

        // Load the interstitial ad
        facebookFrontAD.loadAd(
                facebookFrontAD.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }


    private static void showAdmobFrontAd() {
        final com.google.android.gms.ads.InterstitialAd admobFrontAD = new com.google.android.gms.ads.InterstitialAd(activityRef.get());
        admobFrontAD.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdClosed() {
                Log.d(MediationAdHelper.TAG, "[ADMOB FRONT AD]Dismissed");
                if (onInterstitialAdListener != null) {
                    onInterstitialAdListener.onDismissed(MediationAdHelper.AD_ADMOB);
                }
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                Log.e(MediationAdHelper.TAG, "[ADMOB FRONT AD]Error: " + loadAdError.getMessage());
                MediationAdInterstitial.onError(loadAdError.getMessage());
            }

            @Override
            public void onAdLoaded() {
                Log.d(MediationAdHelper.TAG, "[ADMOB FRONT AD]Loaded");
                onInterstitialAdListener.onBeforeAdShow();
                admobFrontAD.show();

                if (onInterstitialAdListener != null) {
                    onInterstitialAdListener.onLoaded(MediationAdHelper.AD_ADMOB);
                }
            }


            @Override
            public void onAdLeftApplication() {
                Log.d(MediationAdHelper.TAG, "[ADMOB FRONT AD]Clicked");
                super.onAdLeftApplication();
                if (onInterstitialAdListener != null) {
                    onInterstitialAdListener.onAdClicked(MediationAdHelper.AD_ADMOB);
                }
            }

        });
        admobFrontAD.setAdUnitId(admobKey);
        admobFrontAD.loadAd(MediationAdHelper.getAdRequest());

    }

    private static void onClose() {
    }

    private static void onError(String errorMessage) {
        if (adPriorityList != null && adPriorityList.size() > 0) {
            selectAd();
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
        adPriorityList = null;
    }

}
