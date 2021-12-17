package mediation.helper.banner;

import static mediation.helper.AdHelperApplication.getCubiBannerAd;
import static mediation.helper.TestAdIDs.TEST_ADMOB_BANNER_ID;
import static mediation.helper.TestAdIDs.TEST_FB_BANNER_ID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

import mediation.helper.AdHelperApplication;
import mediation.helper.IUtils;
import mediation.helper.MediationAdHelper;
import mediation.helper.R;
import mediation.helper.cubiad.NativeAdView.CubiBannerAd;
import mediation.helper.util.Constant;

/**
 * Created by CUBI-IT
 */

public class MediationAdBanner {

    private static WeakReference <Activity> activityRef;
    private static OnBannerAdListener onBannerAdListener;
    private static String facebookKey;
    private static String admobKey;
    private static ViewGroup bannerContainer;
    private static ArrayList <Integer> adPriorityList;

    private static CubiBannerAd cubiBannerAd;

    public static void showFacebookBanner(boolean isPurchased, Activity activity, ViewGroup bannerContainer, OnBannerAdListener onBannerAdListener) {
        showBanner(isPurchased, activity, bannerContainer, new Integer[]{MediationAdHelper.AD_FACEBOOK}, onBannerAdListener);
    }

    public static void showAdmobBanner(boolean isPurchased, Activity activity, ViewGroup bannerContainer, OnBannerAdListener onBannerAdListener) {
        showBanner(isPurchased, activity, bannerContainer, new Integer[]{MediationAdHelper.AD_ADMOB}, onBannerAdListener);
    }

    public static void showBanner(boolean isPurchased, ViewGroup bannerContainer, int adPriority, OnBannerAdListener onBannerAdListener) {
        if (isPurchased)
            return;
        MediationAdBanner.cubiBannerAd = getCubiBannerAd();
        Integer[] tempAdPriorityList = new Integer[3];
        tempAdPriorityList[0] = adPriority;

        if (adPriority == MediationAdHelper.AD_FACEBOOK) {
            tempAdPriorityList[1] = MediationAdHelper.AD_ADMOB;
            tempAdPriorityList[2] = MediationAdHelper.AD_CUBI_IT;
        } else if (adPriority == MediationAdHelper.AD_ADMOB) {
            tempAdPriorityList[1] = MediationAdHelper.AD_FACEBOOK;
            tempAdPriorityList[2] = MediationAdHelper.AD_CUBI_IT;
        } else {
            tempAdPriorityList[1] = MediationAdHelper.AD_FACEBOOK;
            tempAdPriorityList[2] = MediationAdHelper.AD_ADMOB;
        }

        //showBanner(bannerContainer, facebookKey, admobKey, tempAdPriorityList, onBannerAdListener);
        showBanner(bannerContainer, tempAdPriorityList, onBannerAdListener);
    }

    public static void showBanner(boolean isPurchased, Activity activity, ViewGroup bannerContainer, Integer[] tempAdPriorityList, OnBannerAdListener onBannerAdListener) {
        if (isPurchased)
            return;
        if (!AdHelperApplication.getTestMode()) {
            //check if ids or test skip
            if (AdHelperApplication.getAdIDs() != null) {
                if (!checkTestIds(onBannerAdListener))
                    return;
            }
        }
        Log.d("de_", "showBanner:move forward :)) ");
        MediationAdBanner.activityRef = new WeakReference <>(activity);
        MediationAdBanner.cubiBannerAd = getCubiBannerAd();

        // showBanner(bannerContainer, facebookKey, admobKey, tempAdPriorityList, onBannerAdListener);
        showBanner(bannerContainer, tempAdPriorityList, onBannerAdListener);
    }

    private static boolean checkTestIds(OnBannerAdListener onBannerAdListener) {
        if (AdHelperApplication.getAdIDs().getFb_banner_id().equals(TEST_FB_BANNER_ID) || AdHelperApplication.getAdIDs().getAdmob_banner_id().equals(TEST_ADMOB_BANNER_ID)) {
            onBannerAdListener.onError("Found Test IDS..");
            return false;
        }
        return true;
    }

    public static void showBanner(ViewGroup bannerContainer, Integer[] tempAdPriorityList, final OnBannerAdListener onBannerAdListener) {
        try {

            MediationAdBanner.adPriorityList = new ArrayList <>(Arrays.asList(tempAdPriorityList));
            MediationAdBanner.facebookKey = AdHelperApplication.getAdIDs().getFb_banner_id();
            MediationAdBanner.admobKey = AdHelperApplication.getAdIDs().getFb_banner_id();
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
                showFacebookBanner();
                break;
            case MediationAdHelper.AD_ADMOB:
                showAdmobBanner();
                break;
            case MediationAdHelper.AD_CUBI_IT:
                selectCubiAd();
                break;
            default:
                onBannerAdListener.onError("You have to select priority type ADMOB or FACEBOOK");

        }
    }

    private static boolean isPkgInstalledAlready() {
        cubiBannerAd = getCubiBannerAd();
        Context context = new AdHelperApplication().getContext();
        if (cubiBannerAd == null || context == null) {
            Log.d("TAG1_NativeBanner", "isPkgInstalledAlready: context or cubiBannerad are null");
            return true;//
        }
        if (IUtils.isContainPkg(cubiBannerAd.getBannerAdUrlLink())) {
            if (IUtils.isPackageInstalled(IUtils.getPackageName(cubiBannerAd.getBannerAdUrlLink()), activityRef.get())) {
                Log.d("TAG1_nativeBanner", "isPkgInstalledAlready: true ");
                return true;
            } else {
                Log.d("TAG1_nativeBanner", "isPkgInstalledAlready: true ");
                return false;
            }
        } else
            return false;

    }


    private static void selectCubiAd() {
        Log.d("DEBUG", "initView");

        try {
            if (isPkgInstalledAlready()) {
                onBannerAdListener.onError("CubiBannerAd already installed");
                MediationAdBanner.onError("CubiBannerAd already installed");
                Log.d("TAG1_banner", "selectCubiAdBanner:  pkg already installed");
                return;

            }
            bannerContainer.removeAllViews();
            View bannerView = LayoutInflater.from(bannerContainer.getContext()).inflate(R.layout.layout_cubi_banner_ad, bannerContainer, false);
            ConstraintLayout parentConstraintView = (ConstraintLayout) bannerView.findViewById(R.id.parent_native_banner_constraint_layout);
            ConstraintLayout view_container = (ConstraintLayout) bannerView.findViewById(R.id.view_container_native_banner);
            // Create native UI using the ad metadata.
            Button native_banner_ad_calltoaction = (Button) bannerView.findViewById(R.id.ad_call_to_action);
            TextView native_banner_ad_title = (TextView) bannerView.findViewById(R.id.native_ad_title);
            TextView native_banner_ad_body = (TextView) bannerView.findViewById(R.id.native_banner_ad_body_text);
            ImageView cubi_banner_square_icon = (ImageView) bannerView.findViewById(R.id.cubi_banner_square_icon);
            TextView native_banner_ad_sponser_label = (TextView) bannerView.findViewById(R.id.native_ad_sponsored_label);
            bannerContainer.addView(bannerView);

            if (cubiBannerAd == null || cubiBannerAd.getBannerAdUrlLink() == null || cubiBannerAd.getBannerAdUrlLink().isEmpty()) {
                parentConstraintView.setVisibility(View.GONE);
                MediationAdBanner.onError("CubiAd is null");
                onBannerAdListener.onError("CubiAd is null");
                return;
            }
            native_banner_ad_title.setText(cubiBannerAd.getBannerAdtitle());
            native_banner_ad_body.setText(cubiBannerAd.getBannerAdbodyText());
            native_banner_ad_calltoaction.setText(cubiBannerAd.getBannerAdcallToActionData());
            Glide.with(bannerContainer.getContext().getApplicationContext())
                    .load(cubiBannerAd.getBannerSquareIconUrl())
                    .centerCrop()
                    .into(cubi_banner_square_icon);
            native_banner_ad_calltoaction.setText(cubiBannerAd.getBannerAdcallToActionData());
            native_banner_ad_calltoaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBannerAdListener.onAdClicked(3);
                    actionOnCubiAdClicked();
                }
            });
            cubi_banner_square_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBannerAdListener.onAdClicked(3);
                    actionOnCubiAdClicked();
                }
            });
            native_banner_ad_sponser_label.setText(cubiBannerAd.getBannerAdadvertiserName());
            onBannerAdListener.onLoaded(3);

        } catch (Exception e) {
            onBannerAdListener.onError(e.getMessage());
            MediationAdBanner.onError(e.getMessage());
            e.printStackTrace();
        }
    }


    private static void actionOnCubiAdClicked() {
        String url = cubiBannerAd.getBannerAdUrlLink();
        if (url.isEmpty()) {
            onBannerAdListener.onError("Url is empty");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        bannerContainer.getContext().getApplicationContext().startActivity(intent);
    }

    private static void showFacebookBanner() {
        if (MediationAdHelper.isSkipFacebookAd(bannerContainer.getContext())) {
            onBannerAdListener.onError(Constant.ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED);
            MediationAdBanner.onError(Constant.ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED);
            return;
        }


        final com.facebook.ads.AdView facebookBanner = new com.facebook.ads.AdView(bannerContainer.getContext(), facebookKey, AdSize.BANNER_HEIGHT_50);

        if (onBannerAdListener != null) {
            onBannerAdListener.onFacebookAdCreated(facebookBanner);
        }

        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                MediationAdBanner.onError(adError.getErrorMessage());
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

    private static void showAdmobBanner() {
        final com.google.android.gms.ads.AdView admobBanner = new AdView(bannerContainer.getContext());
        admobBanner.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        admobBanner.setAdUnitId(admobKey);

        admobBanner.loadAd(MediationAdHelper.getAdRequest());

        admobBanner.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                MediationAdBanner.onError(adError.getMessage());
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
            public void onAdOpened() {
                super.onAdOpened();
                if (onBannerAdListener != null) {
                    onBannerAdListener.onAdClicked(MediationAdHelper.AD_ADMOB);
                }
            }

        });
    }


    private static void onError(String errorMessage) {
        if (adPriorityList != null && adPriorityList.size() > 0) {
            selectAd();
        } else {
            if (onBannerAdListener != null) {
                onBannerAdListener.onError(errorMessage);
            }
            finishAd();
        }
    }

    private static void finishAd() {
        onBannerAdListener = null;
        facebookKey = null;
        admobKey = null;
        adPriorityList = null;
        cubiBannerAd = null;
    }
}