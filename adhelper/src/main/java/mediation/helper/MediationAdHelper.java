package mediation.helper;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.IntDef;

import com.facebook.ads.AdSettings;
import com.google.android.gms.ads.AdRequest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import mediation.helper.util.AppUtil;
import mediation.helper.util.Constant;

/**
 * Created by CUBI-IT
 */

public class MediationAdHelper {

    public static int timer = 5000;
    public static final String TAG = "MediationAdHelper";
    public static final int AD_FACEBOOK = 1;
    public static final int AD_ADMOB = 2;
    public static final int AD_CUBI_IT = 3;
    private static String admobDeviceId;
    private static boolean onlyFacebookInstalled = false;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ADMOB_NATIVE_AD_TYPE.NATIVE_EXPRESS, ADMOB_NATIVE_AD_TYPE.NATIVE_ADVANCED, ADMOB_NATIVE_AD_TYPE.BANNER})
    public @interface ADMOB_NATIVE_AD_TYPE {
        int NATIVE_EXPRESS = 1;
        int NATIVE_ADVANCED = 2;
        int BANNER = 3;
    }

    public static void setTestDeviceId(String facebookDeviceId, String admobDeviceId) {
        setFacebookTestDeviceId(facebookDeviceId);
        setAdmobTestDeviceId(admobDeviceId);
    }

    public static void setFacebookTestDeviceId(String deviceId) {
        AdSettings.addTestDevice(deviceId);
    }

    public static void setAdmobTestDeviceId(String deviceId) {
        admobDeviceId = deviceId;
    }

    public static void showAdOnlyFacebookInstalledUser(boolean value) {
        onlyFacebookInstalled = value;
    }

    public static AdRequest getAdRequest() {
        AdRequest.Builder builder = new AdRequest.Builder();
        
        return builder.build();
    }

    public static boolean isSkipFacebookAd(Context context) {
        return MediationAdHelper.onlyFacebookInstalled && !AppUtil.isExistApp(context, Constant.FACEBOOK_PACKAGE_NAME);
    }

    public static String getMessageFromAdmobErrorCode(int errorCode) {
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                return "an invalid response was received from the ad server";

            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                return "ad unit ID was incorrect";

            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                return "The ad request was unsuccessful due to network connectivity";

            case AdRequest.ERROR_CODE_NO_FILL:
                return "The ad request was successful, but no ad was returned due to lack of ad inventory";

            default:
                return "";
        }
    }

    public interface ImageProvider {
        void onProvideImage(ImageView imageView, String imageUrl);
    }
}
