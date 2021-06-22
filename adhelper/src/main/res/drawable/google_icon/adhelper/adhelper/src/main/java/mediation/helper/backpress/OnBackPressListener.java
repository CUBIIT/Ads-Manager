package mediation.helper.backpress;

/**
 * Created by CUBI-IT
 */

public interface OnBackPressListener  {

    void onReviewClick();

    void onFinish();

    void onError(String errorMessage);

    void onLoaded(int adType);

    void onAdClicked(int adType);
}
