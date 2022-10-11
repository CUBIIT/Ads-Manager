package mediation.helper.openad;

import static mediation.helper.AdHelperApplication.applyLimitOnAdmob;
import static mediation.helper.TestAdIDs.TEST_ADMOB_OPEN_APP_ID;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Date;

import mediation.helper.AdHelperApplication;
import mediation.helper.util.PrefManager;

/**
 * Prefetches App Open Ads.
 */
public class MediationOpenAdManageWithLifeCycles {
    private AppOpenAd appOpenAd = null;
    private String admob_open_id_key;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private Activity currentActivity;
    private FirebaseAnalytics analytics;
    private static boolean isShowingAd = false;
    private long loadTime = 0;
    private static final String LOG_TAG = "de_openAdd";
    private OpenAddCallback openAddCallBack;
    private PrefManager prefManager;
    private String ignoreActivity;

    /**
     * Constructor
     */
    public MediationOpenAdManageWithLifeCycles(final Activity activity, OpenAddCallback appOpenAdCallBack, final String onlyShowOnActivity) {
        Log.d("de_op", "OpenAdManager: --------------------");
        this.currentActivity = activity;
        this.openAddCallBack = appOpenAdCallBack;
        ignoreActivity = onlyShowOnActivity;
        analytics = AdHelperApplication.getFirebaseAnalytics();
        prefManager = new PrefManager(activity.getApplicationContext());
        this.admob_open_id_key = TEST_ADMOB_OPEN_APP_ID;

        if (!AdHelperApplication.isAppOpenAdEnable) {
            if (!AdHelperApplication.getTestMode()) {
                appOpenAdCallBack.onErrorToShow("App open ad disable from remote");
                return;
            }
        }
        if (AdHelperApplication.isAdmobInLimit()) {
            if (applyLimitOnAdmob) {
                appOpenAdCallBack.onErrorToShow("admob limit is applied");
                return;
            }
        }
        if (!AdHelperApplication.isInit) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new MediationOpenAdManageWithLifeCycles(activity, openAddCallBack, onlyShowOnActivity);
                }
            }, 1000);
            return;
        }
        if (!checkIfAdCanBeShow(appOpenAdCallBack)) {
            return;
        }
        if (!AdHelperApplication.getTestMode()) {
//        if (true) {
            //check if ids or test skip
            if (AdHelperApplication.getAdIDs() != null) {
                if (!checkTestIds(appOpenAdCallBack))
                    return;
                admob_open_id_key = AdHelperApplication.getAdIDs().getAdmob_open_ad_id();
            } else {
                openAddCallBack.onErrorToShow("Open ad id failed");
            }
        }
        String currentActName = currentActivity.getClass().getSimpleName();
        Log.d("de_open", "fetchAd:classname " + currentActName);
        Log.d("de_open", "fetchAd:ignoreActivity " + ignoreActivity);

        if (currentActName.equals(ignoreActivity)) {
           showAdIfAvailable();
        } else {
            openAddCallBack.onErrorToShow("Not Allow on Splash");
            Log.d("de_open", "MediationOpenAdManagerCallBacks: not show");

        }

    }

    private boolean checkIfAdCanBeShow(OpenAddCallback openAddCallBack) {
        long counter = prefManager.getLong("open_ad_counter");
        Log.d("de_open", "checkIfAdCanBeShow: interval " + AdHelperApplication.OPENAPP_AD_CLICK_LIMIT + " :set counter:" + counter);
        if (counter > 0 && counter < AdHelperApplication.OPENAPP_AD_CLICK_LIMIT) {

            openAddCallBack.onErrorToShow("Open ad show after " + (AdHelperApplication.OPENAPP_AD_CLICK_LIMIT - counter) + " time load app");
            counter++;
            prefManager.setLong("open_ad_counter", counter);
            return false;
        } else {
            Log.d("de_open", "checkIfAdCanBeShow: interval: " + AdHelperApplication.OPENAPP_AD_CLICK_LIMIT + " :set counter:" + counter);
            counter = 0;
            counter++;
            prefManager.setLong("open_ad_counter", counter);
            return true;
        }
    }

    private static boolean checkTestIds(OpenAddCallback openAddCallBack) {
        Log.d("de_openappid", "checkTestIds: ad" + AdHelperApplication.getAdIDs().getAdmob_open_ad_id());
        if (!(AdHelperApplication.getAdIDs().getAdmob_open_ad_id().toString().isEmpty() && AdHelperApplication.getAdIDs().getAdmob_open_ad_id() == null)) {
            if (AdHelperApplication.getAdIDs().getAdmob_open_ad_id().equals(TEST_ADMOB_OPEN_APP_ID)) {
                openAddCallBack.onErrorToShow("Found Test IDS..");

                return false;
            } else {
                return true;
            }
        } else {
            openAddCallBack.onErrorToShow("No ids found");
            return false;
        }

    }


    /**
     * Shows the ad if one isn't already showing.
     */


    public void showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (isShowingAd) {
            Log.d("de_", "showAdIfAvailable:  ad is showing..");
            return;
        }
        if (!isAdAvailable()) {
            openAddCallBack.onDismissClick();
            Log.d(LOG_TAG, "Can not show ad.");
            fetchAd();
            return;
        }
        Log.d(LOG_TAG, "Will show ad.");
        FullScreenContentCallback fullScreenContentCallback =
                new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null;
                        isShowingAd = false;
                        openAddCallBack.onDismissClick();
                        fetchAd();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        openAddCallBack.onErrorToShow(adError.getMessage());
                        appOpenAd = null;
                        isShowingAd = false;
                        Bundle bundle = new Bundle();
                        bundle.putString("failedToShowFullScn", adError.getMessage());
                        if (analytics != null)
                            analytics.logEvent("OpenAppOnAdFailedToShowFullScreen", bundle);
                        fetchAd();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // isShowingAd = true;
                        Bundle bundle = new Bundle();
                        bundle.putString("show_success", "onAdShowedFullScreenContent");
                        if (analytics != null)
                            analytics.logEvent("OpenAppOnAdShowFullScreen", bundle);
                    }
                };

        // appOpenAd.show(currentActivity, fullScreenContentCallback);
        appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
        isShowingAd = true;
        appOpenAd.show(currentActivity);


    }

    /**
     * Request an ad
     */
    boolean isLoadingAd = false;

    public void fetchAd() {
        // Have unused ad, no need to fetch another.
        if (isAdAvailable()) {
            return;
        }
        isLoadingAd = true;
        loadCallback =
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        isLoadingAd = false;
                        openAddCallBack.onErrorToShow(loadAdError.getMessage());
                        if (AdHelperApplication.isAdmobInLimit()) {
                            applyLimitOnAdmob = true;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("openAdFailedToLoad", loadAdError.getMessage());
                        if (analytics != null)
                            analytics.logEvent("OpenApponAdFailedToLoad", bundle);

                    }

                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        appOpenAd = ad;
                        isLoadingAd = false;
                        loadTime = (new Date()).getTime();
                        Log.d("alam_", "onAdLoaded: ");
                        Bundle bundle = new Bundle();
                        bundle.putString("openAdLoaded", "onAddLoadedCalled ");
                        if (analytics != null) analytics.logEvent("OpenApponAdLoaded", bundle);
                        // showAdIfAvailable();
                    }


                };

        AdRequest request = getAdRequest();
        Log.d("de_open", "fetchAd:admobkey " + admob_open_id_key);
        AppOpenAd.load(
                currentActivity.getApplicationContext(), admob_open_id_key, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
        Bundle bundle = new Bundle();
        bundle.putString("openRequestAd", "On Request sent for ad ");
        if (analytics != null) analytics.logEvent("OpenAppAdRequestSend", bundle);
    }

    /**
     * Creates and returns ad request.
     */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().setHttpTimeoutMillis(5000).build();
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo();
    }

    /**
     * Utility method to check if ad was loaded more than n hours ago.
     */
    private boolean wasLoadTimeLessThanNHoursAgo() {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * (long) 4));
    }

}