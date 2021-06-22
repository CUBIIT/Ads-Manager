package mediation.helper.nativead;

/**
 * Created by CUBI-IT
 */

public interface OnNativeAdListener {
    void onError(String errorMessage);

    void onLoaded(int adType);

    void onAdClicked(int adType);
}
