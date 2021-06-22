package mediation.helper.banner;

import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.util.ArrayList;
import java.util.Arrays;

import mediation.helper.MediationAdHelper;
import mediation.helper.util.Constant;

/**
 * Created by CUBI-IT
 */

public class MediationAdBanner {

    private static OnBannerAdListener onBannerAdListener;
    private static String facebookKey;
    private static String admobKey;
    private static ViewGroup bannerContainer;
    private static ArrayList<Integer> adPriorityList;

    public static void showFacebookBanner(boolean isPurchased, ViewGroup bannerContainer, String facebookKey, OnBannerAdListener onBannerAdListener) {
        showBanner(isPurchased, bannerContainer, facebookKey, null, MediationAdHelper.AD_FACEBOOK, onBannerAdListener);
    }

    public static void showAdmobBanner(boolean isPurchased, ViewGroup bannerContainer, String admobKey, OnBannerAdListener onBannerAdListener) {
        showBanner(isPurchased, bannerContainer, null, admobKey, MediationAdHelper.AD_ADMOB, onBannerAdListener);
    }

    public static void showBanner(boolean isPurchased, ViewGroup bannerContainer, String facebookKey, String admobKey, int adPriority, OnBannerAdListener onBannerAdListener) {
        if(isPurchased)
            return;
        Integer[] tempAdPriorityList = new Integer[2];
        tempAdPriorityList[0] = adPriority;
        if (adPriority == MediationAdHelper.AD_FACEBOOK) {
            tempAdPriorityList[1] = MediationAdHelper.AD_ADMOB;
        } else {
            tempAdPriorityList[1] = MediationAdHelper.AD_FACEBOOK;
        }
        showBanner(bannerContainer, facebookKey, admobKey, tempAdPriorityList, onBannerAdListener);
    }

    public static void showBanner(ViewGroup bannerContainer, String facebookKey, String admobKey, Integer[] tempAdPriorityList, OnBannerAdListener onBannerAdListener) {
        try {
            MediationAdBanner.adPriorityList = new ArrayList<>(Arrays.asList(tempAdPriorityList));
            MediationAdBanner.facebookKey = facebookKey;
            MediationAdBanner.admobKey = admobKey;
            MediationAdBanner.onBannerAdListener = onBannerAdListener;
            MediationAdBanner.bannerContainer = bannerContainer;

            if (bannerContainer == null) {
                onBannerAdListener.onError("BannerContainer can not null");
                return;
            }

            selectAd();

        } catch (Exception e) {

            if (onBannerAdListener != null) {
                onBannerAdListener.onError("");
            }

        }
    }

    private static void selectAd() {
        int adPriority = adPriorityList.remove(0);
        switch (adPriority) {
            case MediationAdHelper.AD_FACEBOOK:
                showFacebookBanner(!TextUtils.isEmpty(admobKey));
                break;
            case MediationAdHelper.AD_ADMOB:
                showAdmobBanner(!TextUtils.isEmpty(facebookKey));
                break;
            default:
                onBannerAdListener.onError("You have to select priority type ADMOB or FACEBOOK");

        }
    }


    private static void showFacebookBanner(final boolean failToAdmob) {
        if (MediationAdHelper.isSkipFacebookAd(bannerContainer.getContext())) {
            Log.e(MediationAdHelper.TAG, "[FACEBOOK BANNER]Error: " + Constant.ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED);
            Log.d(MediationAdHelper.TAG, "failToAdmob: " + failToAdmob);

            if (failToAdmob) {
                showAdmobBanner(false);
            } else if (onBannerAdListener != null) {
                onBannerAdListener.onError(Constant.ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED);
            }
            return;
        }


        final com.facebook.ads.AdView facebookBanner = new com.facebook.ads.AdView(bannerContainer.getContext(), facebookKey, AdSize.BANNER_HEIGHT_50);

        if (onBannerAdListener != null) {
            onBannerAdListener.onFacebookAdCreated(facebookBanner);
        }

        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(MediationAdHelper.TAG, "[FACEBOOK BANNER]Error: " + adError.getErrorMessage());
                if (failToAdmob) {
                    showAdmobBanner(false);
                } else if (onBannerAdListener != null) {
                    onBannerAdListener.onError(adError.getErrorMessage());
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK BANNER]Loaded");
                bannerContainer.removeAllViews();
                ViewGroup parentView = ((ViewGroup) facebookBanner.getParent());
                if (parentView != null) {
                    parentView.removeAllViews();
                }

                bannerContainer.addView(facebookBanner);

                if (onBannerAdListener != null) {
                    onBannerAdListener.onLoaded(MediationAdHelper.AD_FACEBOOK);
                }


            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK BANNER]Clicked");
                if (onBannerAdListener != null) {
                    onBannerAdListener.onAdClicked(MediationAdHelper.AD_FACEBOOK);
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }

        };
        // Request an ad
        facebookBanner.loadAd(facebookBanner.buildLoadAdConfig().withAdListener(adListener).build());

    }

    private static void showAdmobBanner(final boolean failToFacebook) {
        final com.google.android.gms.ads.AdView admobBanner = new AdView(bannerContainer.getContext());
        admobBanner.setAdSize(com.google.android.gms.ads.AdSize.SMART_BANNER);
        admobBanner.setAdUnitId(admobKey);

        admobBanner.loadAd(MediationAdHelper.getAdRequest());

        admobBanner.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.e(MediationAdHelper.TAG, "[ADMOB BANNER]Error: " + adError.getMessage());

                if (failToFacebook) {
                    showFacebookBanner(false);
                } else if (onBannerAdListener != null) {
                    onBannerAdListener.onError(adError.getMessage());
                }
            }

            @Override
            public void onAdLoaded() {
                Log.d(MediationAdHelper.TAG, "[ADMOB BANNER]Loaded");
                bannerContainer.removeAllViews();
                ViewGroup parentView = ((ViewGroup) admobBanner.getParent());
                if (parentView != null) {
                    parentView.removeAllViews();
                }
                bannerContainer.addView(admobBanner);

                if (onBannerAdListener != null) {
                    onBannerAdListener.onLoaded(MediationAdHelper.AD_ADMOB);
                }
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(MediationAdHelper.TAG, "[ADMOB BANNER]Clicked");
                super.onAdLeftApplication();
                if (onBannerAdListener != null) {
                    onBannerAdListener.onAdClicked(MediationAdHelper.AD_ADMOB);
                }
            }
        });
    }
}
