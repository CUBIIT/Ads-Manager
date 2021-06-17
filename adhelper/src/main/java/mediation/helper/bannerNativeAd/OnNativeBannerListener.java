package mediation.helper.bannerNativeAd;

public interface OnNativeBannerListener {
    void onError(String errorMessage);

    void onLoaded(int adType);

    void onAdClicked(int adType);
}
