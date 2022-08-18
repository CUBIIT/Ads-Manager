package mediation.helper.util;

import android.util.Log;

import java.util.Locale;
import java.util.Map;

import mediation.helper.MediationAdHelper;

/**
 * Created by CUBI-IT
 */

public class Constant {
    public static final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
    public static final String ERROR_MESSAGE_FACEBOOK_NOT_INSTALLED = "Facebook app not installed";
    public static final String TAG = "SESSIONS_";


    //make theme constans

    //keys for general info

    public static String BANNER_AD_PRIORITY_KEY = "banner_ad_priority";
    public static String INTERSTITIAL_AD_PRIORITY_KEY = "interstitial_ad_priority";
    public static String NATIVE_AD_PRIORITY_KEY = "native_ad_priority";
    public static String INTERSTITIAL_AD_FREQUENCY_KEY = "interstitial_ad_frequency";
    public static String INTERSTITIAL_AD_TIMER_KEY = "interstitial_ad_timer";


    //keys for remote config file to fecth data
    public static String INTERSTITIAL_KEY_AD_TITLE = "interstitial_ad_title";
    public static String INTERSTITIAL_KEY_AD_BODY = "interstitial_ad_body_text";
    public static String INTERSTITIAL_KEY_SQUARE_ICON_URL = "interstitial_square_icon_url";
    public static String INTERSTITIAL_KEY_FEATURE_ICON_URL = "interstitial_feature_icon_url";
    public static String INTERSTITIAL_KEY_AD_URL = "interstitial_ad_url";
    public static String INTERSTITIAL_KEY_AD_ADVERTISER_NAME = "interstitial_ad_advertiser_name";
    public static String INTERSTITIAL_KEY_AD_CALL_TO_ACTION = "interstitial_ad_call_to_action";
    public static String INTERSTITIAL_KEY_AD_FEEDBACKS = "interstitial_ad_feedbacks";
    public static String INTERSTITIAL_KEY_AD_RATING = "interstitial_ad_rating";
    //FORNATIVE ADS
    public static String NATIVE_KEY_AD_TITLE = "native_ad_title";
    public static String NATIVE_KEY_AD_BODY = "native_ad_body_text";
    public static String NATIVE_KEY_AD_ICON_URL = "native_ad_icon_url";
    public static String NATIVE_KEY_AD_IMAGE_URL = "native_ad_image_url";
    public static String NATIVE_KEY_AD_URL = "native_ad_url";
    public static String NATIVE_KEY_AD_ADVERTISER_NAME = "native_ad_advertiser_name";
    public static String NATIVE_KEY_AD_CALL_TO_ACTION = "native_ad_call_to_action";
    public static String NATIVE_KEY_AD_FEEDBACKS = "native_ad_feedbacks";
    public static String NATIVE_KEY_AD_RATING = "native_ad_rating";
    //FOR BANNER ADS
    public static String BANNER_KEY_AD_TITLE = "banner_ad_title";
    public static String BANNER_KEY_AD_BODY = "banner_ad_body_text";
    public static String BANNER_KEY_SQUARE_ICON_URL = "banner_square_icon_url";
    public static String BANNER_KEY_AD_URL = "banner_ad_url";
    public static String BANNER_KEY_AD_ADVERTISER_NAME = "banner_ad_advertiser_name";
    public static String BANNER_KEY_AD_CALL_TO_ACTION = "banner_ad_call_to_action";
    public static String BANNER_KEY_AD_FEEDBACKS = "banner_ad_feedbacks";
    public static String BANNER_KEY_AD_RATING = "banner_ad_rating";

    //cat key
    public static String ENABLE_CAT = "enable_CAT";

    public static Integer[] KEY_PRIORITY_BANNER_AD = new Integer[]{

            MediationAdHelper.AD_FACEBOOK,
            MediationAdHelper.AD_ADMOB,
            MediationAdHelper.AD_CUBI_IT};

    public static Integer[] KEY_PRIORITY_INTERSTITIAL_AD = new Integer[]{
            MediationAdHelper.AD_FACEBOOK,
            MediationAdHelper.AD_ADMOB,
            MediationAdHelper.AD_CUBI_IT};

    public static Integer[] KEY_PRIORITY_NATIVE_AD = new Integer[]{
            MediationAdHelper.AD_FACEBOOK,
            MediationAdHelper.AD_ADMOB,
            MediationAdHelper.AD_CUBI_IT};


    public static String KEY_NEXT_TIME_AD_SHOW = "key_interstitial_next_day";
    public static String KEY_AD_FREQUENCY = "key_ad_frequency";

    // AD IDs
    public static String FB_BANNER_ID_KEY="fb_banner_id";
    public static String FB_NATIVE_ID_KEY="fb_native_id";
    public static String FB_INTERSTITIAL_ID_KEY="fb_interstitial_id";
    public static String ADMOB_BANNER_ID_KEY="admob_banner_id";
    public static String ADMOB_INTERSTITIAL_ID_KEY="admob_interstitial_id";
    public static String ADMOB_NATIVE_ID_KEY="admob_native_id";
    public static String ADMOB_APP_ID_KEY="admob_app_id";
    public static String NEW_FB_BANNER_ID_KEY="new_fb_banner_id";
    public static String NEW_FB_NATIVE_ID_KEY="new_fb_native_id";
    public static String NEW_FB_INTERSTITIAL_ID_KEY="new_fb_interstitial_id";
    public static String NEW_ADMOB_BANNER_ID_KEY="new_admob_banner_id";
    public static String NEW_ADMOB_INTERSTITIAL_ID_KEY="new_admob_interstitial_id";
    public static String NEW_ADMOB_NATIVE_ID_KEY="new_admob_native_id";
    public static String NEW_ADMOB_APP_ID_KEY="new_admob_app_id";
    public static String NEW_APP_VERSION="new_app_version";
    public static String TEST_MODE_KEY="test_mode";
    public static String RELEASE_KEY="release";



    public static String findValueInMap(String key, Map<String, String> map) {
        key = key.toUpperCase(Locale.ROOT);
        Log.d("Constant", "findValueInMap: key " + key);
        String value = "default";
        if (map == null) {
            Log.e("Constant", "findValueInMapInterstitial: map is null");
        } else {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Log.i("Constant", "findValueInMap: " + entry.getKey());
                if (entry.getKey().equals(key.toUpperCase(Locale.ROOT))) {
                    value = entry.getValue();
                    Log.d("Constant", "findValueInMap:find " + value);
                    break;

                }
            }
        }
        Log.d("Constant", "findValueInMap:value  " + value);
        return value;

    }
  public static Integer findIntegerValueInMap(String key, Map<String, Integer> map) {
        key = key.toUpperCase(Locale.ROOT);
        Log.d("Constant", "findValueInMap: key " + key);
        int value = 1;
        if (map == null) {
            Log.e("Constant", "findValueInMapInterstitial: map is null");
        } else {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                Log.i("Constant", "findValueInMap: " + entry.getKey());
                if (entry.getKey().equals(key.toLowerCase(Locale.ROOT))) {
                    value = entry.getValue();
                    Log.d("Constant", "findValueInMap:find " + value);
                    break;

                }
            }
        }
        Log.d("Constant", "findValueInMap:value  " + value);
        return value;

    }



}
