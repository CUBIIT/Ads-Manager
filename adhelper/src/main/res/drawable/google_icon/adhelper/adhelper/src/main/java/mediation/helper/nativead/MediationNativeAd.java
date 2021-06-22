package mediation.helper.nativead;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mediation.helper.MediationAdHelper;
import mediation.helper.R;
import mediation.helper.util.Constant;
import mediation.helper.util.ConvertUtil;

/**
 * Created by CUBI-IT
 */

public class MediationNativeAd {


    ArrayList<Integer> adPriorityList;
    String app_name;
    String facebook_ad_key;
    String admob_ad_key;
    Context context;
    NativeAppInstallAdView admobAppInstallRootView;
    NativeContentAdView admobContentRootView;
    ViewGroup container_admob_express;
    ProgressBar progressView;
    RelativeLayout viewNativeRoot;
    LinearLayout view_container;
    ImageView ivLogo;
    TextView tvName;
    TextView tvBody;
    MediaView nativeAdMedia;
    ImageView ivImage;
    TextView tvEtc;
    TextView tvCallToAction;
    com.facebook.ads.NativeAd facebookAd;
    OnNativeAdListener onNativeAdListener;
    ViewGroup view_ad_choice;

    MediationAdHelper.ImageProvider imageProvider;

    ViewGroup containerView;
    ViewGroup admobBannerContainer;

    @MediationAdHelper.ADMOB_NATIVE_AD_TYPE
    int admobNativeAdType;

    public MediationNativeAd(ViewGroup containerView, Context context, String app_name, String facebook_ad_key, String admob_ad_key) {
        this(containerView, context, app_name, facebook_ad_key, admob_ad_key, null, MediationAdHelper.ADMOB_NATIVE_AD_TYPE.NATIVE_EXPRESS);
    }

    public MediationNativeAd(ViewGroup itemView, Context context, String app_name, String facebook_ad_key, String admob_ad_key, MediationAdHelper.ImageProvider imageProvider, @MediationAdHelper.ADMOB_NATIVE_AD_TYPE int admobNativeAdType) {
        this.containerView = itemView;
        this.context = context;
        this.app_name = app_name;
        this.facebook_ad_key = facebook_ad_key;
        this.admob_ad_key = admob_ad_key;
        this.imageProvider = imageProvider;
        this.admobNativeAdType = admobNativeAdType;
        initView();
    }

    private void initView() {
        containerView.removeAllViews();
        View nativeView = LayoutInflater.from(context).inflate(R.layout.adview_native_base, containerView,false);

        viewNativeRoot = (RelativeLayout) nativeView.findViewById(R.id.view_native_root);
        admobAppInstallRootView = (NativeAppInstallAdView) nativeView.findViewById(R.id.admobAppInstallRootView);
        admobBannerContainer = (ViewGroup) nativeView.findViewById(R.id.admob_banner_container);

        container_admob_express = (ViewGroup) nativeView.findViewById(R.id.container_admob_express);
        progressView = (ProgressBar) nativeView.findViewById(R.id.progressView);
        view_container = (LinearLayout) nativeView.findViewById(R.id.view_container);
        // Create native UI using the ad metadata.
        ivLogo = (ImageView) nativeView.findViewById(R.id.iv_logo);
        tvName = (TextView) nativeView.findViewById(R.id.tv_name);
        tvBody = (TextView) nativeView.findViewById(R.id.tv_body);
        nativeAdMedia = (MediaView) nativeView.findViewById(R.id.native_ad_media);
        ivImage = (ImageView) nativeView.findViewById(R.id.iv_image);

        tvEtc = (TextView) nativeView.findViewById(R.id.tv_etc);
        tvCallToAction = (TextView) nativeView.findViewById(R.id.tv_call_to_action);
        view_ad_choice = (ViewGroup) nativeView.findViewById(R.id.view_ad_choice);

        containerView.addView(nativeView);

    }

    public MediationNativeAd(ViewGroup containerView, Context context, String app_name, String facebook_ad_key, String admob_ad_key, @MediationAdHelper.ADMOB_NATIVE_AD_TYPE int admobNativeAdType) {
        this(containerView, context, app_name, facebook_ad_key, admob_ad_key, null, admobNativeAdType);
    }

    public void onDestroy() {
        if (nativeAdMedia != null) {
            nativeAdMedia.destroy();
        }
        if (facebookAd != null) {
            facebookAd.destroy();
        }

    }

    public void loadFacebookAD(OnNativeAdListener onNativeAdListener) {
        loadAD(MediationAdHelper.AD_FACEBOOK, onNativeAdListener);
    }

    public void loadAdmobAD(OnNativeAdListener onNativeAdListener) {
        loadAD(MediationAdHelper.AD_ADMOB, onNativeAdListener);
    }

    public void loadAD(int adPriority, OnNativeAdListener onNativeAdListener) {
        Integer[] tempAdPriorityList = new Integer[2];
        tempAdPriorityList[0] = adPriority;
        if (adPriority == MediationAdHelper.AD_FACEBOOK) {
            tempAdPriorityList[1] = MediationAdHelper.AD_ADMOB;
        } else {
            tempAdPriorityList[1] = MediationAdHelper.AD_FACEBOOK;
        }
        loadAD(tempAdPriorityList, onNativeAdListener);
    }

    public void loadAD(Integer[] tempAdPriorityList, OnNativeAdListener onNativeAdListener) {
        if (tempAdPriorityList == null || tempAdPriorityList.length == 0) {
            if (onNativeAdListener != null) {
                onNativeAdListener.onError("You have to select priority type ADMOB/FACEBOOK/TNK");
            }
            return;
        }
        ArrayList resultTempAdPriorityList = new ArrayList<>(Arrays.asList(tempAdPriorityList));
        loadAD(resultTempAdPriorityList, onNativeAdListener);

    }

    public void loadAD(ArrayList tempAdPriorityList, OnNativeAdListener onNativeAdListener) {
        this.onNativeAdListener = onNativeAdListener;

        if (tempAdPriorityList == null || tempAdPriorityList.size() == 0) {
            if (onNativeAdListener != null) {
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
            default:
                onNativeAdListener.onError("You have to select priority type ADMOB or FACEBOOK");
        }
    }

    private void selectAdmobAd() {
        switch (admobNativeAdType) {
            case MediationAdHelper.ADMOB_NATIVE_AD_TYPE.NATIVE_EXPRESS:
                loadAdmobExpressAD();
                break;
            case MediationAdHelper.ADMOB_NATIVE_AD_TYPE.NATIVE_ADVANCED:
                loadAdmobAdvanceAD();
                break;
            case MediationAdHelper.ADMOB_NATIVE_AD_TYPE.BANNER:
                loadAdmobBanner();
                break;
        }

    }

    private void loadAdmobBanner() {
        viewNativeRoot.setVisibility(View.GONE);
        admobBannerContainer.removeAllViews();
        AdView admobBannerView = new AdView(context);
        AdRequest adRequest = new AdRequest.Builder().build();

        admobBannerView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        admobBannerView.setAdUnitId(admob_ad_key);
        admobBannerView.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                String errorMessage = MediationAdHelper.getMessageFromAdmobErrorCode(errorCode);
                Log.e(MediationAdHelper.TAG, "[ADMOB NATIVE BANNER AD]errorMessage: " + errorMessage);
                onLoadAdError(errorMessage);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.d(MediationAdHelper.TAG, "[ADMOB NATIVE BANNER AD]Opend");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d(MediationAdHelper.TAG, "[ADMOB NATIVE BANNER AD]Loaded");
            }
        });

        admobBannerView.loadAd(adRequest);
        admobBannerContainer.addView(admobBannerView);
    }

    private void onLoadAdError(String errorMessage) {
        if (adPriorityList.size() > 0) {
            //loadAdmobAdvanceAD(false);
            selectAd();
        } else {
            viewNativeRoot.setVisibility(View.GONE);

            if (onNativeAdListener != null) {
                onNativeAdListener.onError(errorMessage);
            }

        }
    }

    private void loadAdmobExpressAD() {
        viewNativeRoot.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.VISIBLE);
        view_container.setVisibility(View.INVISIBLE);


        final NativeExpressAdView admobExpressAdView = new NativeExpressAdView(context);

        // Set its video options.
        admobExpressAdView.setVideoOptions(new VideoOptions.Builder()
                .setStartMuted(true)
                .build());


        // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
        // loading.
        admobExpressAdView.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(MediationAdHelper.TAG, "[ADMOB NATIVE EXPRESS AD]Loaded");

                view_container.setVisibility(View.GONE);
                progressView.setVisibility(View.GONE);

                if (onNativeAdListener != null) {
                    onNativeAdListener.onLoaded(MediationAdHelper.AD_ADMOB);
                }


            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

                super.onAdFailedToLoad(errorCode);

                String errorMessage = MediationAdHelper.getMessageFromAdmobErrorCode(errorCode);
                Log.e(MediationAdHelper.TAG, "[ADMOB NATIVE EXPRESS AD]errorMessage: " + errorMessage);
                onLoadAdError(errorMessage);
            }

            @Override
            public void onAdOpened() {
                Log.d(MediationAdHelper.TAG, "[ADMOB NATIVE EXPRESS AD]Opend");
                super.onAdOpened();
                if (onNativeAdListener != null) {
                    onNativeAdListener.onAdClicked(MediationAdHelper.AD_ADMOB);
                }
            }

        });

        container_admob_express.post(new Runnable() {
            @Override
            public void run() {

                int realWidth = ConvertUtil.pxToDp(context, container_admob_express.getWidth());


                int realHeight = 320 * realWidth / 360;
                if (admobExpressAdView.getAdUnitId() == null) {
                    admobExpressAdView.setAdUnitId(admob_ad_key);
                }

                if (admobExpressAdView.getAdSize() == null) {
                    admobExpressAdView.setAdSize(new AdSize(realWidth, realHeight));
                }

                //admobExpressAdView.setAdSize(new AdSize(AdSize.FULL_WIDTH,320));


                for (int i = 0; i < container_admob_express.getChildCount(); i++) {
                    if (container_admob_express.getChildAt(i) instanceof NativeExpressAdView) {
                        NativeExpressAdView temp = (NativeExpressAdView) container_admob_express.getChildAt(i);
                        temp.destroy();
                    }
                }
                container_admob_express.removeAllViews();
                container_admob_express.addView(admobExpressAdView);


                admobExpressAdView.loadAd(MediationAdHelper.getAdRequest());

            }

        });

        /*
        ViewTreeObserver viewTreeObserver = container_admob_express.getViewTreeObserver();

        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
container_admob_express.getViewTreeObserver().removeGlobalOnLayoutListener(this);

            }
        });
*/

    }

    private void loadAdmobAdvanceAD() {
        viewNativeRoot.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.VISIBLE);
        view_container.setVisibility(View.INVISIBLE);

        AdLoader.Builder builder = new AdLoader.Builder(context, admob_ad_key);
        builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
            @Override
            public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                bindAdmobAppInstallAD(nativeAppInstallAd);
                Log.d(MediationAdHelper.TAG, "[ADMOB NATIVE ADVANCED AD]InstallAd Load");
            }
        });

        builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
            @Override
            public void onContentAdLoaded(NativeContentAd nativeContentAd) {
                Log.d(MediationAdHelper.TAG, "[ADMOB NATIVE ADVANCED AD]ContentAd Load");
                bindAdmobContentAD(nativeContentAd);
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
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e(MediationAdHelper.TAG, "[ADMOB NATIVE ADVANCED AD]fail");
                String errorMessage = MediationAdHelper.getMessageFromAdmobErrorCode(errorCode);
                onLoadAdError(errorMessage);

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
        viewNativeRoot.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.VISIBLE);
        view_container.setVisibility(View.INVISIBLE);

        facebookAd = new com.facebook.ads.NativeAd(context, facebook_ad_key);
        NativeAdListener nativeAdListener = new  com.facebook.ads.NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(MediationAdHelper.TAG, "[FACEBOOK NATIVE AD]Error: " + adError.getErrorMessage());
                onLoadAdError(adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK NATIVE AD]Loaded");
                bindFacebookAD(ad);

                if (onNativeAdListener != null) {
                    onNativeAdListener.onLoaded(MediationAdHelper.AD_FACEBOOK);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK NATIVE AD]Clicked");
                if (onNativeAdListener != null) {
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
            viewNativeRoot.setVisibility(View.GONE);
            return;
        }

        MyNativeAd myNativeAd = new MyNativeAd();

        myNativeAd.setName(facebookAd.getAdvertiserName());


        myNativeAd.setBody(facebookAd.getAdBodyText());
        myNativeAd.setCallToAction(facebookAd.getAdCallToAction());
        myNativeAd.setEtc(facebookAd.getAdSocialContext());

        bindNativeAd(myNativeAd);

        NativeAdLayout nativeAdLayout = new NativeAdLayout(context);
        AdOptionsView adOptionsView = new AdOptionsView(context, facebookAd, nativeAdLayout);
        view_ad_choice.addView(adOptionsView);

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(ivLogo);
        clickableViews.add(tvName);
        clickableViews.add(ivImage);
        clickableViews.add(tvBody);
        clickableViews.add(tvCallToAction);
        clickableViews.add(nativeAdMedia);

        facebookAd.unregisterView();
        facebookAd.registerViewForInteraction(viewNativeRoot, nativeAdMedia, ivLogo, clickableViews);
    }

    private void bindNativeAd(MyNativeAd myNativeAd) {
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
        Drawable logoDrawable = myNativeAd.getLogoDrawable();

        if (logoDrawable != null) {
            ivLogo.setImageDrawable(logoDrawable);
        } else if (TextUtils.isEmpty(logoUrl)) {
        } else if (imageProvider == null) {
            Glide.with(context)
                    .load(logoUrl)
                    .into(ivLogo);

        } else if (!TextUtils.isEmpty(logoUrl)) {
            imageProvider.onProvideImage(ivLogo, logoUrl);
        }

        String name = myNativeAd.getName();
        if (TextUtils.isEmpty(name)) {
            name = app_name;
        }

        tvName.setText(name);

        String imageUrl = myNativeAd.getImageUrl();
        Drawable imageDrawable = myNativeAd.getImageDrawable();
        if (imageDrawable != null) {
            ivImage.setImageDrawable(imageDrawable);
        } else if (TextUtils.isEmpty(imageUrl)) {
        } else if (imageProvider == null) {
            Glide.with(context)
                    .load(imageUrl)
                    .into(ivImage);
        } else {
            imageProvider.onProvideImage(ivImage, imageUrl);
        }

        String body = myNativeAd.getBody();
        tvBody.setText(body);

        String etc = myNativeAd.getEtc();
        if (TextUtils.isEmpty(etc)) {
            tvEtc.setVisibility(View.GONE);
        } else {
            tvEtc.setVisibility(View.VISIBLE);
            tvEtc.setText(etc);
        }


        String callToAction = myNativeAd.getCallToAction();
        if (TextUtils.isEmpty(callToAction)) {
            tvCallToAction.setVisibility(View.GONE);
        } else {
            tvCallToAction.setVisibility(View.VISIBLE);
            tvCallToAction.setText(myNativeAd.getCallToAction());
        }


        viewNativeRoot.setVisibility(View.VISIBLE);
        view_container.setVisibility(View.VISIBLE);
        container_admob_express.setVisibility(View.GONE);
        progressView.setVisibility(View.GONE);
    }


    private void bindAdmobContentAD(NativeContentAd nativeContentAd) {

        if (nativeContentAd == null) {
            onLoadAdError("nativeContentAd is null");
            return;
        }
        MyNativeAd myNativeAd = new MyNativeAd();

        if (nativeContentAd.getLogo() != null) {
            myNativeAd.setLogoDrawable(nativeContentAd.getLogo().getDrawable());
        }

        if (nativeContentAd.getHeadline() != null) {
            myNativeAd.setName(nativeContentAd.getHeadline().toString());
            admobContentRootView.setHeadlineView(tvName);
        }


        List<NativeAd.Image> images = nativeContentAd.getImages();
        if (images != null && images.size() > 0) {
            myNativeAd.setImageDrawable(nativeContentAd.getImages().get(0).getDrawable());
            admobContentRootView.setImageView(ivImage);
        }

        if (nativeContentAd.getBody() != null) {
            myNativeAd.setBody(nativeContentAd.getBody().toString());
            admobContentRootView.setBodyView(tvBody);
        }

        if (nativeContentAd.getCallToAction() != null) {
            myNativeAd.setCallToAction(nativeContentAd.getCallToAction().toString());
            admobContentRootView.setCallToActionView(tvCallToAction);
        }

        if (nativeContentAd.getAdvertiser() != null) {
            myNativeAd.setEtc(nativeContentAd.getAdvertiser().toString());
        }


        bindNativeAd(myNativeAd);

        admobContentRootView.setNativeAd(nativeContentAd);

    }

    private void bindAdmobAppInstallAD(NativeAppInstallAd nativeAppInstallAd) {
        if (nativeAppInstallAd == null) {
            onLoadAdError("nativeAppInstallAd is null");
            return;
        }
        MyNativeAd myNativeAd = new MyNativeAd();

        if (nativeAppInstallAd.getIcon() != null) {
            myNativeAd.setLogoDrawable(nativeAppInstallAd.getIcon().getDrawable());
            admobAppInstallRootView.setIconView(ivLogo);
        }

        if (nativeAppInstallAd.getHeadline() != null) {
            myNativeAd.setName(nativeAppInstallAd.getHeadline().toString());
            admobAppInstallRootView.setHeadlineView(tvName);
        }


        List<NativeAd.Image> images = nativeAppInstallAd.getImages();
        if (images != null && images.size() > 0) {
            myNativeAd.setImageDrawable(nativeAppInstallAd.getImages().get(0).getDrawable());
            admobAppInstallRootView.setImageView(ivImage);
        }

        if (nativeAppInstallAd.getBody() != null) {
            myNativeAd.setBody(nativeAppInstallAd.getBody().toString());
            admobAppInstallRootView.setBodyView(tvBody);
        }

        if (nativeAppInstallAd.getCallToAction() != null) {
            myNativeAd.setCallToAction(nativeAppInstallAd.getCallToAction().toString());
            admobAppInstallRootView.setCallToActionView(tvCallToAction);
        }

        if (nativeAppInstallAd.getStore() != null) {
            myNativeAd.setEtc(nativeAppInstallAd.getStore().toString());
            admobAppInstallRootView.setStoreView(tvEtc);
        }


        bindNativeAd(myNativeAd);

        admobAppInstallRootView.setNativeAd(nativeAppInstallAd);

    }


    class MyNativeAd {
        String logoUrl;
        Drawable logoDrawable;
        String name;
        String imageUrl;
        Drawable imageDrawable;
        String body;
        String callToAction;
        String etc;

        public String getLogoUrl() {
            return logoUrl;
        }

        public void setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getCallToAction() {
            return callToAction;
        }

        public void setCallToAction(String callToAction) {
            this.callToAction = callToAction;
        }

        public String getEtc() {
            return etc;
        }

        public void setEtc(String etc) {
            this.etc = etc;
        }

        public Drawable getLogoDrawable() {
            return logoDrawable;
        }

        public void setLogoDrawable(Drawable logoDrawable) {
            this.logoDrawable = logoDrawable;
        }

        public Drawable getImageDrawable() {
            return imageDrawable;
        }

        public void setImageDrawable(Drawable imageDrawable) {
            this.imageDrawable = imageDrawable;
        }
    }

}
