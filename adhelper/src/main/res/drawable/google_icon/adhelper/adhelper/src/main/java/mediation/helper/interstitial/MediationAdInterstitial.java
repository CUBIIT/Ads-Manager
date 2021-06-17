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

import mediation.helper.MediationAdHelper;
import mediation.helper.util.Constant;

import static mediation.helper.MediationAdHelper.timer;


/**
 * Created by CUBI-IT
 */

public class MediationAdInterstitial {
    private static OnInterstitialAdListener onInterstitialAdListener;
    private static String facebookKey;
    private static String admobKey;
    private static WeakReference<Activity> activityRef;

    private static ArrayList<Integer> adPriorityList;

    private static boolean showAds = true;

    public static void showFacebookInterstitialAd(boolean isPurchased, Activity activity, String facebookKey, OnInterstitialAdListener onInterstitialAdListener) {
        showInterstitialAd(isPurchased, activity, facebookKey, null, MediationAdHelper.AD_FACEBOOK, onInterstitialAdListener);
    }

    public static void showAdmobInterstitialAd(boolean isPurchased, Activity activity, String admobKey, OnInterstitialAdListener onInterstitialAdListener) {
        showInterstitialAd(isPurchased, activity, null, admobKey, MediationAdHelper.AD_ADMOB, onInterstitialAdListener);
    }

    public static void showInterstitialAd(boolean isPurchased, Activity activity, String facebookKey, final String admobKey, int adPriority, OnInterstitialAdListener onInterstitialAdListener) {
        Integer[] tempAdPriorityList = new Integer[2];
        tempAdPriorityList[0] = adPriority;
        if (adPriority == MediationAdHelper.AD_FACEBOOK) {
            tempAdPriorityList[1] = MediationAdHelper.AD_ADMOB;
        } else {
            tempAdPriorityList[1] = MediationAdHelper.AD_FACEBOOK;
        }

        showInterstitialAd(isPurchased, activity, facebookKey, admobKey, tempAdPriorityList, onInterstitialAdListener);
    }

    public static void showInterstitialAd(boolean isPurchased, Activity activity, String facebookKey, final String admobKey, Integer[] tempAdPriorityList, OnInterstitialAdListener onInterstitialAdListener) {
        if (isPurchased) {
            onInterstitialAdListener.onError("You have pro version!");
            return;
        }

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
            showAds = true;

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Log.d(MediationAdHelper.TAG, "Delay Time is Finished!");
                    if (showAds && MediationAdInterstitial.onInterstitialAdListener != null) {
                        MediationAdInterstitial.onInterstitialAdListener.onError("Delay Time is Finished!");
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
                if (showAds) {
                    showAds = false;
                    // Show the ad when it's done loading.
                    try {
                        onInterstitialAdListener.onBeforeAdShow();
                        facebookInterstitialAD.show();
                    } catch (Exception e) {
                        MediationAdInterstitial.onError(e.getMessage());
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

    private static com.google.android.gms.ads.interstitial.InterstitialAd admobInterstitialAD;

    private static void showAdmobInterstitialAd() {
        InterstitialAd.load(activityRef.get(), admobKey, MediationAdHelper.getAdRequest(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
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
                MediationAdInterstitial.onError(loadAdError.getMessage());
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
