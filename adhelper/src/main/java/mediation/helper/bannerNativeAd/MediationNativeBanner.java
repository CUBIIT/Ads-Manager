package mediation.helper.bannerNativeAd;

import static mediation.helper.TestAdIDs.TEST_ADMOB_NATIVE_ID;
import static mediation.helper.TestAdIDs.TEST_FB_NATIVE_ID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mediation.helper.AdHelperApplication;
import mediation.helper.AnalyticsEvents.MediationEvents;
import mediation.helper.IUtils;
import mediation.helper.MediationAdHelper;
import mediation.helper.R;
import mediation.helper.cubiad.NativeAdView.CubiNativeAd;
import mediation.helper.util.Constant;

public class MediationNativeBanner {


    ArrayList <Integer> adPriorityList;
    String app_name;
    String facebook_ad_key;
    String admob_ad_key;
    Context context;
    NativeAdView admobAppInstallRootView;
    ConstraintLayout view_container;
    ImageView native_banner_icon_view;
    TextView native_banner_ad_sponser_label;
    TextView native_banner_ad_body;
    MediaView native_banner_fb_media_view;
    com.facebook.ads.NativeAd facebookAd;
    OnNativeBannerListener onNativeAdListener;
    ViewGroup view_ad_choice;
    ConstraintLayout parentConstraintView;
    MediationAdHelper.ImageProvider imageProvider;

    ViewGroup containerView;

    RelativeLayout relativeLayout_adchoices;
    int admobNativeAdType;
    TextView adType;
    TextView native_banner_ad_title;
    RatingBar adRatingBar;
    Button native_banner_ad_calltoaction;
    String TEST = "DEBUG-AD";
    private com.google.android.gms.ads.nativead.NativeAd nativeAd;
    private Object NativeAd;
    private static CubiNativeAd cubiNativeAd;
    private boolean purchase;

//    public MediationNativeBanner(ViewGroup containerView, Context context, String app_name, String facebook_ad_key, String admob_ad_key) {
//        this(containerView, context, app_name, facebook_ad_key, admob_ad_key);
//    }

    public MediationNativeBanner(boolean purchased, ViewGroup itemView, Context context, String app_name, MediationAdHelper.ImageProvider imageProvider) {
        MediationEvents.onNativeBannerAdCalledEvents();
        this.purchase = purchased;
        this.containerView = itemView;
        this.context = context;
        this.app_name = app_name;
//        this.facebook_ad_key = facebook_ad_key;
//        this.admob_ad_key = admob_ad_key;
        this.imageProvider = imageProvider;
        cubiNativeAd = AdHelperApplication.getCubiNativeAd();
        initView();
    }

    private boolean checkTestIds(OnNativeBannerListener onBannerAdListener) {
        if (!AdHelperApplication.getAdIDs().getAdmob_native_id().isEmpty() || AdHelperApplication.getAdIDs().getFb_native_id().isEmpty()) {
            if (AdHelperApplication.getAdIDs().getAdmob_native_id().equals(TEST_ADMOB_NATIVE_ID) || AdHelperApplication.getAdIDs().getFb_native_id().equals(TEST_FB_NATIVE_ID)) {
                parentConstraintView.setVisibility(View.GONE);
                onBannerAdListener.onError("Found Test IDS..");
                return false;
            } else {
                return true;
            }
        } else {
            parentConstraintView.setVisibility(View.GONE);
            onBannerAdListener.onError("No IDs found..");
            return false;
        }
    }

    public MediationNativeBanner(boolean purchased, ViewGroup itemView, Context context, String app_name, CubiNativeAd cubiNativeAd, MediationAdHelper.ImageProvider imageProvider) {
        MediationEvents.onNativeBannerAdCalledEvents();
        this.purchase = purchased;
        this.containerView = itemView;
        this.context = context;
        this.app_name = app_name;
//        this.facebook_ad_key = facebook_ad_key;
//        this.admob_ad_key = admob_ad_key;
        this.imageProvider = imageProvider;
        this.cubiNativeAd = cubiNativeAd;
        initView();
    }

    public MediationNativeBanner(boolean purchased, ViewGroup itemView, Context context, String app_name, boolean showIconAds, MediationAdHelper.ImageProvider imageProvider) {
        MediationEvents.onNativeBannerAdCalledEvents();
        this.purchase = purchased;
        this.containerView = itemView;
        this.context = context;
        this.app_name = app_name;
//        this.facebook_ad_key = facebook_ad_key;
//        this.admob_ad_key = admob_ad_key;
        cubiNativeAd = AdHelperApplication.getCubiNativeAd();
        this.imageProvider = imageProvider;
        if (showIconAds) {
            initViewForIconAd();
        } else
            initView();
    }

    //    public MediationNativeBanner(ViewGroup containerView, Context context, String app_name, String facebook_ad_key, String admob_ad_key) {
//        this(containerView, context, app_name, facebook_ad_key, admob_ad_key);
//    }
    private void initViewForIconAd() {
        containerView.removeAllViews();
        View nativeView = LayoutInflater.from(context).inflate(R.layout.icon_ad_layout, containerView, false);
        parentConstraintView = (ConstraintLayout) nativeView.findViewById(R.id.parent_native_banner_constraint_layout);
        admobAppInstallRootView = (NativeAdView) nativeView.findViewById(R.id.ad_mob_banner_native_ad_view);
        view_container = (ConstraintLayout) nativeView.findViewById(R.id.view_container_native_banner);
        // Create native UI using the ad metadata.
        native_banner_ad_calltoaction = (Button) nativeView.findViewById(R.id.ad_call_to_action);
        native_banner_ad_title = (TextView) nativeView.findViewById(R.id.native_ad_title);
        native_banner_ad_body = (TextView) nativeView.findViewById(R.id.native_banner_ad_body_text);
        native_banner_icon_view = (ImageView) nativeView.findViewById(R.id.cubi_banner_square_icon);
//        tvName = (TextView) nativeView.findViewById(R.id.tv_name);
        native_banner_ad_sponser_label = (TextView) nativeView.findViewById(R.id.native_ad_sponsored_label);
//        tvBody = (TextView) nativeView.findViewById(R.id.tv_body);
        native_banner_fb_media_view = (MediaView) nativeView.findViewById(R.id.fb_ad_media_view);
//        tvEtc = (TextView) nativeView.findViewById(R.id.tv_etc);
        /* tvCallToAction = (TextView) nativeView.findViewById(R.id.tv_call_to_action);*/
        view_ad_choice = (ViewGroup) nativeView.findViewById(R.id.ad_choices_container);
        relativeLayout_adchoices = (RelativeLayout) nativeView.findViewById(R.id.ad_choices_container);
        containerView.addView(nativeView);
    }

    private void initView() {
        Log.d("DEBUG", "initView");
        containerView.removeAllViews();
        View nativeView = LayoutInflater.from(context).inflate(R.layout.layout_native_banner_ad, containerView, false);
        parentConstraintView = (ConstraintLayout) nativeView.findViewById(R.id.parent_native_banner_constraint_layout);
        admobAppInstallRootView = (NativeAdView) nativeView.findViewById(R.id.ad_mob_banner_native_ad_view);
        view_container = (ConstraintLayout) nativeView.findViewById(R.id.view_container_native_banner);
        // Create native UI using the ad metadata.
        native_banner_ad_calltoaction = (Button) nativeView.findViewById(R.id.ad_call_to_action);
        native_banner_ad_title = (TextView) nativeView.findViewById(R.id.native_ad_title);
        native_banner_ad_body = (TextView) nativeView.findViewById(R.id.native_banner_ad_body_text);
        native_banner_icon_view = (ImageView) nativeView.findViewById(R.id.cubi_banner_square_icon);
//        tvName = (TextView) nativeView.findViewById(R.id.tv_name);
        native_banner_ad_sponser_label = (TextView) nativeView.findViewById(R.id.native_ad_sponsored_label);
//        tvBody = (TextView) nativeView.findViewById(R.id.tv_body);
        native_banner_fb_media_view = (MediaView) nativeView.findViewById(R.id.fb_ad_media_view);
//        tvEtc = (TextView) nativeView.findViewById(R.id.tv_etc);
        /* tvCallToAction = (TextView) nativeView.findViewById(R.id.tv_call_to_action);*/
        view_ad_choice = (ViewGroup) nativeView.findViewById(R.id.ad_choices_container);
        relativeLayout_adchoices = (RelativeLayout) nativeView.findViewById(R.id.ad_choices_container);
        containerView.addView(nativeView);

    }

    public void onDestroy() {
        if (native_banner_fb_media_view != null) {
            native_banner_fb_media_view.destroy();
        }
        if (facebookAd != null) {
            facebookAd.destroy();
        }

    }

    public void loadFacebookAD(OnNativeBannerListener onNativeAdListener) {
        loadAD(MediationAdHelper.AD_FACEBOOK, onNativeAdListener);
    }

    public void loadAdmobAD(OnNativeBannerListener onNativeAdListener) {
        loadAD(MediationAdHelper.AD_ADMOB, onNativeAdListener);
    }

    public void loadCubiAd(OnNativeBannerListener onNativeAdListener) {
        loadAD(MediationAdHelper.AD_CUBI_IT, onNativeAdListener);
    }

    public void loadAD(int adPriority, OnNativeBannerListener onNativeAdListener) {
        if (purchase) {
            if(parentConstraintView!= null){
                parentConstraintView.setVisibility(View.GONE);
            }
            MediationEvents.onNativeBannerAdErrorEvents();
            onNativeAdListener.onError("You have pro version");
            return;
        }
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
        Log.d("TAG1", "loadAD: tempAdPriorty:" + tempAdPriorityList[0] + tempAdPriorityList[1] + tempAdPriorityList[2]);
        loadAD(tempAdPriorityList, onNativeAdListener);
    }

    public void loadAD(Integer[] tempAdPriorityList, OnNativeBannerListener onNativeAdListener) {
        if (purchase) {
            if(parentConstraintView!= null){
                parentConstraintView.setVisibility(View.GONE);
            }
            MediationEvents.onNativeBannerAdErrorEvents();
            onNativeAdListener.onError("You have pro version");
            return;
        }
        if (tempAdPriorityList == null || tempAdPriorityList.length == 0) {
            if (onNativeAdListener != null) {
                MediationEvents.onNativeBannerAdErrorEvents();
                onNativeAdListener.onError("You have to select priority type ADMOB/FACEBOOK/TNK");
            }
            return;
        }

        ArrayList resultTempAdPriorityList = new ArrayList <>(Arrays.asList(tempAdPriorityList));
        loadAD(resultTempAdPriorityList, onNativeAdListener);

    }

    //for cubiBanner ads
    public void loadAD(Integer[] tempAdPriorityList, CubiNativeAd CubiNativeAd, OnNativeBannerListener onNativeAdListener) {
        if (purchase) {
            MediationEvents.onNativeBannerAdErrorEvents();
            onNativeAdListener.onError("You have pro version");
            return;
        }
        if (tempAdPriorityList == null || tempAdPriorityList.length == 0) {
            if (onNativeAdListener != null) {
                MediationEvents.onNativeBannerAdErrorEvents();
                onNativeAdListener.onError("You have to select priority type ADMOB/FACEBOOK/TNK");
            }
            return;
        }
        ArrayList resultTempAdPriorityList = new ArrayList <>(Arrays.asList(tempAdPriorityList));
        loadAD(resultTempAdPriorityList, CubiNativeAd, onNativeAdListener);

    }

    public void loadAD(ArrayList tempAdPriorityList, OnNativeBannerListener onNativeAdListener) {
        if(purchase)
            return;
        this.onNativeAdListener = onNativeAdListener;

        if (tempAdPriorityList == null || tempAdPriorityList.size() == 0) {
            if (onNativeAdListener != null) {
                MediationEvents.onNativeBannerAdErrorEvents();
                onNativeAdListener.onError("You have to select priority type ADMOB/FACEBOOK/TNK");
            }
            return;
        }
        adPriorityList = tempAdPriorityList;
        if (!AdHelperApplication.getTestMode()) {
            //check if ids or test skip
            if (AdHelperApplication.getAdIDs() != null) {
                if (!checkTestIds(onNativeAdListener))
                    return;
                this.facebook_ad_key = AdHelperApplication.getAdIDs().getFb_native_id();
                this.admob_ad_key = AdHelperApplication.getAdIDs().getAdmob_native_id();
            }

        } else {
            this.facebook_ad_key = TEST_FB_NATIVE_ID;
            this.admob_ad_key = TEST_ADMOB_NATIVE_ID;
        }
        Log.d("de_nativeBan", String.format("NativeBan ids:------Facebook: %s -----Admob: %s", facebook_ad_key, admob_ad_key));
        try {
            selectAd();
        } catch (Exception e) {
            e.printStackTrace();
            if (onNativeAdListener != null) {
                MediationEvents.onNativeBannerAdErrorEvents();
                onNativeAdListener.onError(e.toString());
            }
        }

    }

    //cubi ads
    public void loadAD(ArrayList tempAdPriorityList, CubiNativeAd CubiNativeAd, OnNativeBannerListener onNativeAdListener) {
        this.onNativeAdListener = onNativeAdListener;
        this.cubiNativeAd = CubiNativeAd;
        if (tempAdPriorityList == null || tempAdPriorityList.size() == 0) {
            if (onNativeAdListener != null) {
                MediationEvents.onNativeBannerAdErrorEvents();
                onNativeAdListener.onError("You have to select priority type ADMOB/FACEBOOK/TNK");
            }
            return;
        }
        adPriorityList = tempAdPriorityList;

        try {
            selectAd();
        } catch (Exception e) {
            e.printStackTrace();
            if (onNativeAdListener != null) {
                MediationEvents.onNativeBannerAdErrorEvents();
                onNativeAdListener.onError(e.toString());
            }
        }

    }

    private void selectAd() {
        view_ad_choice.removeAllViews();

        int adPriority = adPriorityList.remove(0);
        switch (adPriority) {
            case MediationAdHelper.AD_FACEBOOK:
                loadFacebookAD();
                break;
            case MediationAdHelper.AD_ADMOB:
                selectAdmobAd();
                break;
            case MediationAdHelper.AD_CUBI_IT:
                selectCubiAd();
                break;
            default:
                MediationEvents.onNativeBannerAdErrorEvents();
                onNativeAdListener.onError("You have to select priority type ADMOB or FACEBOOK");
        }
    }

    private boolean isPkgInstalledAlready() {
        if (cubiNativeAd == null || context == null) {
            Log.d("TAG1_NativeBanner", "isPkgInstalledAlready: context or CubiNativeAd are null");
            return true;//
        }
        if (IUtils.isContainPkg(cubiNativeAd.getNativeAdUrlLink())) {
            if (IUtils.isPackageInstalled(IUtils.getPackageName(cubiNativeAd.getNativeAdUrlLink()), context))
                return true;
            else
                return false;
        } else
            return false;

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void selectCubiAd() {
        try {
            //for testing
            if (cubiNativeAd == null) {
                parentConstraintView.setVisibility(View.GONE);
                MediationEvents.onNativeBannerAdErrorEvents();
                onNativeAdListener.onError("CubiAd is null");
                onLoadAdError("CubiAd is null");
                return;
            }
            /*
            don't show ad if advertisement app is already installed on device
             */
            if (isPkgInstalledAlready()) {
                Log.d("TAG1_banner_ad", "selectCubiAd: app already installed");
                parentConstraintView.setVisibility(View.GONE);
                MediationEvents.onNativeBannerAdErrorEvents();
                onNativeAdListener.onError("CubiAd pkg already installed");
                onLoadAdError("CubiAd pkg already installed");
                return;
            }
            Log.d("TAG1_bannerAd", "selectCubiAd: pkg not installed already ");
            parentConstraintView.setVisibility(View.VISIBLE);
            native_banner_ad_title.setText(cubiNativeAd.getNativeAdtitle());
            native_banner_ad_body.setText(cubiNativeAd.getNativeAdbodyText());
            native_banner_ad_calltoaction.setText(cubiNativeAd.getNativeAdcallToActionData());
            Glide.with(context.getApplicationContext())
                    .load(cubiNativeAd.getNativeAdIconUrl())
                    .centerCrop()
                    .into(native_banner_icon_view);
            native_banner_ad_calltoaction.setText(cubiNativeAd.getNativeAdcallToActionData());
            native_banner_ad_calltoaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNativeAdListener.onAdClicked(3);
                    MediationEvents.onNativeBannerAdClickedEvents();
                    actionOnCubiAdClicked();
                }
            });
            native_banner_icon_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNativeAdListener.onAdClicked(3);
                    MediationEvents.onNativeBannerAdClickedEvents();
                    actionOnCubiAdClicked();
                }
            });
            native_banner_ad_sponser_label.setText(cubiNativeAd.getNativeAdadvertiserName());
            relativeLayout_adchoices.setBackground(context.getResources().getDrawable(R.drawable.ic_ads_view));
            onNativeAdListener.onLoaded(3);
            MediationEvents.onNativeBannerAdSuccessEvents(3);


        } catch (Exception ignor) {
            ignor.printStackTrace();
            onLoadAdError(ignor.getMessage());
        }
    }

    private void actionOnCubiAdClicked() {
        String url = cubiNativeAd.getNativeAdUrlLink();
        if (url.isEmpty()) {
            MediationEvents.onNativeBannerAdErrorEvents();
            onNativeAdListener.onError("Url is empty");
            return;
        }
        /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);*/
//        String packageName = "";
//        //split package name
//        if (url.contains("play.google.com/store/apps")) {
//            Log.d("de_", "actionOnCubiAdClicked: contains");
//            String[] a = url.split("=");
//            packageName = a[1];
//        } else {
//            packageName = url;
//        }
//
//        Uri uri = Uri.parse("market://details?id=" + packageName);
//        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
//                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
//                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
//            context.startActivity(new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void selectAdmobAd() {
        loadAdmobNativeBannerAd();
    }


    private void onLoadAdError(String errorMessage) {
        if (adPriorityList.size() > 0) {
            //loadAdmobAdvanceAD(false);
            selectAd();
        } else {
            parentConstraintView.setVisibility(View.GONE);

            if (onNativeAdListener != null) {
                MediationEvents.onNativeBannerAdErrorEvents();
                onNativeAdListener.onError(errorMessage);
            }

        }
    }


    private void loadAdmobNativeBannerAd() {
        parentConstraintView.setVisibility(View.INVISIBLE);

        AdLoader.Builder builder = new AdLoader.Builder(context, admob_ad_key);
        builder.forNativeAd(new com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                bindAdmobContentAD(nativeAd, admobAppInstallRootView);
                Log.d(MediationAdHelper.TAG, "[ADMOB NATIVE BANNER AD]InstallAd Load");

            }

        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();
        builder.withNativeAdOptions(adOptions);


        AdLoader adLoader = builder.withAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError errorCode) {
                super.onAdFailedToLoad(errorCode);

                String errorMessage = errorCode.getMessage();
                Log.e(MediationAdHelper.TAG, "[ADMOB NATIVE EXPRESS AD]errorMessage: " + errorMessage);
                onLoadAdError(errorMessage);

            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d(MediationAdHelper.TAG, "[ADMOB NATIVE EXPRESS AD]Loaded");
                parentConstraintView.setVisibility(View.VISIBLE);

                if (onNativeAdListener != null) {
                    MediationEvents.onNativeBannerAdSuccessEvents(2);
                    onNativeAdListener.onLoaded(MediationAdHelper.AD_ADMOB);
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.d(MediationAdHelper.TAG, "[ADMOB NATIVE EXPRESS AD]Opend");
                if (onNativeAdListener != null) {
                    MediationEvents.onNativeBannerAdClickedEvents();
                    onNativeAdListener.onAdClicked(MediationAdHelper.AD_ADMOB);
                }
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.d(MediationAdHelper.TAG, "[FACEBOOK NATIVE AD]Clicked");
                if (onNativeAdListener != null) {
                    MediationEvents.onNativeBannerAdClickedEvents();
                    onNativeAdListener.onAdClicked(MediationAdHelper.AD_FACEBOOK);
                }
            }
        }).build();


        AdRequest.Builder requestBuilder = new AdRequest.Builder();
        // requestBuilder.addTestDevice("4126DBFAA8B1B6F71AB0DF06CD69F9E3");

        adLoader.loadAd(requestBuilder.build());

    }

    private void loadFacebookAD() {

        if (MediationAdHelper.isSkipFacebookAd(context)) {
            Log.e(MediationAdHelper.TAG, "[FACEBOOK NATIVE AD]Error: " + Constant.ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED);
            onLoadAdError(Constant.ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED);
            return;
        }

        parentConstraintView.setVisibility(View.INVISIBLE);


        facebookAd = new com.facebook.ads.NativeAd(context, facebook_ad_key);
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(MediationAdHelper.TAG, "[FACEBOOK NATIVE AD]Error: " + adError.getErrorMessage());
                parentConstraintView.setVisibility(View.INVISIBLE);
                onLoadAdError(adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK NATIVE AD]Loaded");
                bindFacebookAD(ad);

                if (onNativeAdListener != null) {
                    MediationEvents.onNativeBannerAdSuccessEvents(1);
                    onNativeAdListener.onLoaded(MediationAdHelper.AD_FACEBOOK);
                    parentConstraintView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK NATIVE AD]Clicked");
                if (onNativeAdListener != null) {
                    MediationEvents.onNativeBannerAdClickedEvents();
                    onNativeAdListener.onAdClicked(MediationAdHelper.AD_FACEBOOK);
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };
        facebookAd.loadAd(
                facebookAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }

    private void bindFacebookAD(Ad ad) {
        if (facebookAd != ad) {
            parentConstraintView.setVisibility(View.GONE);
            return;
        }

        MyNativeBannerAds myNativeAd = new MyNativeBannerAds();

        myNativeAd.setSponserLabel(facebookAd.getSponsoredTranslation());
        myNativeAd.setAdTitle(facebookAd.getAdHeadline());
        myNativeAd.setAdBody(facebookAd.getAdBodyText());
        myNativeAd.setAdCallToAction(facebookAd.getAdCallToAction());
//        myNativeAd.set(facebookAd.getAdSocialContext());

        bindNativeAd(myNativeAd);

        NativeAdLayout nativeAdLayout = new NativeAdLayout(context);
        AdOptionsView adOptionsView = new AdOptionsView(context, facebookAd, nativeAdLayout);
        view_ad_choice.addView(adOptionsView);
        List <View> clickableViews = new ArrayList <>();
       // clickableViews.add(native_banner_icon_view);
        clickableViews.add(native_banner_ad_sponser_label);
      // clickableViews.add(native_banner_ad_body);
        clickableViews.add(native_banner_ad_calltoaction);
       // clickableViews.add(native_banner_fb_media_view);

        facebookAd.unregisterView();
        facebookAd.registerViewForInteraction(parentConstraintView, native_banner_fb_media_view, native_banner_icon_view, clickableViews);
    }

    private void bindNativeAd(MyNativeBannerAds myNativeAd) {
        Log.d(TEST, "bindNaiveAd");
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
                return;
            }

            if (activity.isFinishing()) {
                return;
            }
        }
        String logoUrl = myNativeAd.getLogoUrl();
        Drawable logoDrawable = myNativeAd.getAdImageIcon();

        if (logoDrawable != null) {
            native_banner_icon_view.setImageDrawable(logoDrawable);
        } else if (TextUtils.isEmpty(logoUrl)) {
        } else if (imageProvider == null) {
            Glide.with(context)
                    .load(logoUrl)
                    .into(native_banner_icon_view);

        } else if (!TextUtils.isEmpty(logoUrl)) {
            imageProvider.onProvideImage(native_banner_icon_view, logoUrl);
        }

        String sponserLabel = myNativeAd.getSponserLabel();
        if (TextUtils.isEmpty(sponserLabel)) {
            sponserLabel = app_name;
        }

        native_banner_ad_sponser_label.setText(sponserLabel);

        String title = myNativeAd.getAdTitle();
        if (TextUtils.isEmpty(title)) {
            native_banner_ad_title.setVisibility(View.GONE);
        } else {
            native_banner_ad_title.setVisibility(View.VISIBLE);
            native_banner_ad_title.setText(title);
        }
//        //setmedia content
//        MediaContent mediaContent = myNativeAd.getMediaContent();
//        if (mediaContent != null) {
//            admobNativeAdMediaView.setMediaContent(mediaContent);
//            admobNativeAdMediaView.setVisibility(View.VISIBLE);
//        } else {
//            admobNativeAdMediaView.setVisibility(View.INVISIBLE);
//        }

//
//        String imageUrl = myNativeAd.getImageUrl();
//        Drawable imageDrawable = myNativeAd.getImageDrawable();
//        if (imageDrawable != null) {
//            ivImage.setImageDrawable(imageDrawable);
//        } else if (TextUtils.isEmpty(imageUrl)) {
//        } else if (imageProvider == null) {
//            Glide.with(context)
//                    .load(imageUrl)
//                    .into(ivImage);
//        } else {
//            imageProvider.onProvideImage(ivImage, imageUrl);
//        }

        String body = myNativeAd.getAdBody();
        if (TextUtils.isEmpty(body)) {
            native_banner_ad_body.setVisibility(View.INVISIBLE);

        } else {
            native_banner_ad_body.setVisibility(View.VISIBLE);
            native_banner_ad_body.setText(body);
        }
//        //fbboyd
//        String fbBodyText = myNativeAd.getFbBodyText();
//        if(TextUtils.isEmpty(fbBodyText)){
//            fbBody.setVisibility(View.INVISIBLE);
//
//        }
//        else {
//            fbBody.setVisibility(View.VISIBLE);
//            fbBody.setText(fbBodyText);
//        }


        String callToAction = myNativeAd.getAdCallToAction();
        if (TextUtils.isEmpty(callToAction)) {
            native_banner_ad_calltoaction.setVisibility(View.GONE);
        } else {
            native_banner_ad_calltoaction.setVisibility(View.VISIBLE);
            native_banner_ad_calltoaction.setText(callToAction);
        }
        parentConstraintView.setVisibility(View.VISIBLE);

    }


    private void bindAdmobContentAD(com.google.android.gms.ads.nativead.NativeAd nativeAd, NativeAdView nativeContentAd) {

        if (nativeContentAd == null) {
            onLoadAdError("nativeContentAd is null");
            return;
        }
        MyNativeBannerAds myNativeAd = new MyNativeBannerAds();

        if (nativeAd.getIcon() != null) {
            myNativeAd.setAdImageIcon(nativeAd.getIcon().getDrawable());
            nativeContentAd.setIconView(native_banner_icon_view);
        }
//        if (nativeAd.getMediaContent() != null) {
//            myNativeAd.setMediaContent(nativeAd.getMediaContent());
//            nativeContentAd.setMediaView(admobNativeAdMediaView);
//        }
        if (nativeAd.getHeadline() != null) {
            myNativeAd.setAdTitle(nativeAd.getHeadline());
            nativeContentAd.setHeadlineView(native_banner_ad_title);
        }

        if (nativeAd.getAdvertiser() != null) {
            myNativeAd.setSponserLabel(nativeAd.getAdvertiser().toString());
            nativeContentAd.setAdvertiserView(native_banner_ad_sponser_label);
        }


        if (nativeAd.getBody() != null) {
            myNativeAd.setAdBody(nativeAd.getBody().toString());
            nativeContentAd.setBodyView(native_banner_ad_body);
        }
        if (nativeAd.getCallToAction() != null) {
            myNativeAd.setAdCallToAction(nativeAd.getCallToAction());
            nativeContentAd.setCallToActionView(native_banner_ad_calltoaction);
        }

//        if (nativeAd.getCallToAction() != null) {
//            myNativeAd.setCallToAction(nativeAd.getCallToAction().toString());
//            nativeContentAd.setCallToActionView(tvCallToAction);
//        }

//        if (nativeAd.getAdvertiser() != null) {
//            myNativeAd.setEtc(nativeAd.getAdvertiser().toString());
//        }


        bindNativeAd(myNativeAd);

        nativeContentAd.setNativeAd(nativeAd);

    }


    public class MyNativeBannerAds {
        String sponserLabel;
        String adTitle;
        String adBody;
        Drawable adImageIcon;
        String adCallToAction;
        String logoUrl;

        public String getLogoUrl() {
            return logoUrl;
        }

        public void setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
        }

        public String getSponserLabel() {
            return sponserLabel;
        }

        public void setSponserLabel(String sponserLabel) {
            this.sponserLabel = sponserLabel;
        }

        public String getAdTitle() {
            return adTitle;
        }

        public void setAdTitle(String adTitle) {
            this.adTitle = adTitle;
        }

        public String getAdBody() {
            return adBody;
        }

        public void setAdBody(String adBody) {
            this.adBody = adBody;
        }

        public Drawable getAdImageIcon() {
            return adImageIcon;
        }

        public void setAdImageIcon(Drawable adImageIcon) {
            this.adImageIcon = adImageIcon;
        }

        public String getAdCallToAction() {
            return adCallToAction;
        }

        public void setAdCallToAction(String adCallToAction) {
            this.adCallToAction = adCallToAction;
        }

    }

}
