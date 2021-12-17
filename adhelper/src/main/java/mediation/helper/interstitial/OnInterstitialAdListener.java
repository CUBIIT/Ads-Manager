package mediation.helper.interstitial;

/**
 * Created by TedPark on 2017. 3. 8..
 */

public interface OnInterstitialAdListener {
    void onDismissed(int adType);

    void onError(String errorMessage);

    void onLoaded(int adType);

    void onBeforeAdShow();

    void onAdClicked(int adType);

    void onFacebookAdCreated(com.facebook.ads.InterstitialAd facebookFrontAD);
}
