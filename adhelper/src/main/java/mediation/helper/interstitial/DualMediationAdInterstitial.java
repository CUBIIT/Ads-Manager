package mediation.helper.interstitial;

import android.app.Activity;
import android.os.Handler;
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

import mediation.helper.AdHelperApplication;
import mediation.helper.MediationAdHelper;
import mediation.helper.bannerNativeAd.OnNativeBannerListener;
import mediation.helper.util.Constant;

import static mediation.helper.MediationAdHelper.timer;
import static mediation.helper.TestAdIDs.TEST_ADMOB_INTERSTITIAL_ID;
import static mediation.helper.TestAdIDs.TEST_ADMOB_NATIVE_ID;
import static mediation.helper.TestAdIDs.TEST_FB_INTERSTITIAL_ID;
import static mediation.helper.TestAdIDs.TEST_FB_NATIVE_ID;


/**
 * Created by CUBI-IT
 */

public class DualMediationAdInterstitial {
    private static OnInterstitialAdListener onInterstitialAdListener;
    private static String facebookKey;
    private static String admobKey;
    private static WeakReference<Activity> activityRef;

    private static ArrayList<Integer> adPriorityList;

    private static boolean showAds = true;

    public static void showFacebookInterstitialAd(boolean isPurchased, Activity activity, OnInterstitialAdListener onInterstitialAdListener) {
        showInterstitialAd(isPurchased, activity, 0, onInterstitialAdListener);
    }

    public static void showAdmobInterstitialAd(boolean isPurchased, Activity activity, OnInterstitialAdListener onInterstitialAdListener) {
        showInterstitialAd(isPurchased, activity,  MediationAdHelper.AD_ADMOB, onInterstitialAdListener);
    }

    public static void showInterstitialAd(boolean isPurchased, Activity activity,  int adPriority, OnInterstitialAdListener onInterstitialAdListener) {
        Integer[] tempAdPriorityList = new Integer[2];
        tempAdPriorityList[0] = adPriority;
        if (adPriority == MediationAdHelper.AD_FACEBOOK) {
            tempAdPriorityList[1] = MediationAdHelper.AD_ADMOB;
        } else {
            tempAdPriorityList[1] = MediationAdHelper.AD_FACEBOOK;
        }

        showInterstitialAd(isPurchased, activity, tempAdPriorityList, onInterstitialAdListener);
    }
    private static boolean checkTestIds(OnInterstitialAdListener listener) {
        Log.d(TAG, "checkTestIds: ");
        if(!AdHelperApplication.getAdIDs().getAdmob_interstitial_id().isEmpty() || AdHelperApplication.getAdIDs().getFb_interstitial_id().isEmpty()) {
            if (AdHelperApplication.getAdIDs().getAdmob_interstitial_id().equals(TEST_ADMOB_INTERSTITIAL_ID) || AdHelperApplication.getAdIDs().getFb_interstitial_id().equals(TEST_FB_INTERSTITIAL_ID)) {
                listener.onError("Found Test IDS..");
                return false;
            }else
                return true;
        }else{
            listener.onError("No IDs found..");
            return false;
        }
    }
    static String TAG = "de_intes";
    public static void showInterstitialAd(boolean isPurchased, Activity activity, Integer[] tempAdPriorityList, OnInterstitialAdListener onInterstitialAdListener) {
        if (isPurchased) {
            onInterstitialAdListener.onError("You have pro version!");
            return;
        }

        if (tempAdPriorityList == null || tempAdPriorityList.length == 0) {
            throw new RuntimeException("You have to select priority type ADMOB/FACEBOOK/TNK");
        }
        //by defualt test ids
        DualMediationAdInterstitial.facebookKey = TEST_FB_NATIVE_ID;
        DualMediationAdInterstitial.admobKey = TEST_ADMOB_NATIVE_ID;
        if(!AdHelperApplication.getTestMode()){
            //check if ids or test skip
            if(AdHelperApplication.getAdIDs() != null){
                if(!checkTestIds(onInterstitialAdListener))
                    return;
                Log.d(TAG, "showInterstitialAd: after check");
                DualMediationAdInterstitial.facebookKey = AdHelperApplication.getAdIDs().getFb_interstitial_id();
                DualMediationAdInterstitial.admobKey = AdHelperApplication.getAdIDs().getAdmob_interstitial_id();

            }

        }
        DualMediationAdInterstitial.adPriorityList = new ArrayList<>(Arrays.asList(tempAdPriorityList));
        try {
            DualMediationAdInterstitial.activityRef = new WeakReference<>(activity);

            DualMediationAdInterstitial.onInterstitialAdListener = onInterstitialAdListener;
            selectAd();
            showAds = true;

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Log.d(MediationAdHelper.TAG, "Delay Time is Finished!");
                    if (showAds && DualMediationAdInterstitial.onInterstitialAdListener != null) {
                        DualMediationAdInterstitial.onInterstitialAdListener.onError("Delay Time is Finished!");
                        showAds = false;
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

    private static void selectAd() {
        int adPriority = adPriorityList.remove(0);
        switch (adPriority) {
            case MediationAdHelper.AD_FACEBOOK:
                showFacebookInterstitialAd();
                break;
            case MediationAdHelper.AD_ADMOB:
                showAdmobInterstitialAd();
                break;
            default:
                onInterstitialAdListener.onError("You have to select priority type ADMOB or FACEBOOK");
                finishAd();
        }
    }

    private static void showFacebookInterstitialAd() {
        if (MediationAdHelper.isSkipFacebookAd(activityRef.get())) {
            Log.e(MediationAdHelper.TAG, "[FACEBOOK FRONT AD]Error: " + Constant.ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED);
            onError(Constant.ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED);
            return;
        }
        final com.facebook.ads.InterstitialAd facebookInterstitialAD = new com.facebook.ads.InterstitialAd(activityRef.get(), facebookKey);

        if (onInterstitialAdListener != null) {
            onInterstitialAdListener.onFacebookAdCreated(facebookInterstitialAD);
        }
        // Set listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
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
                DualMediationAdInterstitial.onError(adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK FRONT AD]Loaded");
                if (showAds) {
                    showAds = false;
                    // Show the ad when it's done loading.
                    try {
                        onInterstitialAdListener.onBeforeAdShow();
                        facebookInterstitialAD.show();
                    } catch (Exception e) {
                        DualMediationAdInterstitial.onError(e.getMessage());
                        return;
                    }

                    if (onInterstitialAdListener != null) {
                        onInterstitialAdListener.onLoaded(MediationAdHelper.AD_FACEBOOK);
                    }
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
        facebookInterstitialAD.loadAd(
                facebookInterstitialAD.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    private static InterstitialAd admobInterstitialAD;

    private static void showAdmobInterstitialAd() {
        InterstitialAd.load(activityRef.get(), admobKey, MediationAdHelper.getAdRequest(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                admobInterstitialAD = interstitialAd;
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                Log.d(MediationAdHelper.TAG, "[ADMOB FRONT AD]Loaded");
                if (showAds) {
                    showAds = false;
                    onInterstitialAdListener.onBeforeAdShow();

                    admobInterstitialAD.setFullScreenContentCallback(new FullScreenContentCallback(){
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
                            admobInterstitialAD = null;
                            Log.d("TAG", "The ad was shown.");
                        }
                    });

                    admobInterstitialAD.show(activityRef.get());

                    if (onInterstitialAdListener != null) {
                        onInterstitialAdListener.onLoaded(MediationAdHelper.AD_ADMOB);
                    }
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.e(MediationAdHelper.TAG, "[ADMOB FRONT AD]Error: " + loadAdError.getMessage());
                DualMediationAdInterstitial.onError(loadAdError.getMessage());
            }
        });

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