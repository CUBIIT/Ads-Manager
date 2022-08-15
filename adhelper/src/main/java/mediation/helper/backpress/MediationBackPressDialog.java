package mediation.helper.backpress;

import static mediation.helper.TestAdIDs.TEST_ADMOB_NATIVE_ID;
import static mediation.helper.TestAdIDs.TEST_FB_NATIVE_ID;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import mediation.helper.AdHelperApplication;
import mediation.helper.AnalyticsEvents.MediationEvents;
import mediation.helper.MediationAdHelper;
import mediation.helper.R;
import mediation.helper.config.PLACEHOLDER;
import mediation.helper.nativead.MediationNativeAd;
import mediation.helper.nativead.OnNativeAdListener;
import mediation.helper.util.AppUtil;
import mediation.helper.util.SharedPreferenceUtil;


public class MediationBackPressDialog extends AppCompatActivity {

    private static final String EXTRA_APP_NAME = "app_real_name";
    private static final String EXTRA_FACEBOOK_KEY = "facebook_key";
    private static final String EXTRA_ADMOB_KEY = "admob_key";
    private static final String EXTRA_AD_PRIORITY_LIST = "ad_priority_list";
    private static final String EXTRA_SHOW_REVIEW_BUTTON = "show_review_button";
    private static final String EXTRA_ADMOB_NATIVE_TYPE = "admob_native_type";
    private static final String EXTRA_IS_PURCHASE = "is_purchase";

    private static OnBackPressListener onBackPressListener;
    private static MediationAdHelper.ImageProvider imageProvider;
    ViewGroup adViewContainer;
    TextView tvFinish;
    TextView tvReview;
    View dividerBtn;
    String appName;
    String facebookKey;
    String admobKey;
    boolean showReviewButton;
    @MediationAdHelper.ADMOB_NATIVE_AD_TYPE
    int admobNativeAdType;
    MediationNativeAd adViewNativeAd;
    ArrayList<Integer> adPriorityList;
    static boolean isPurchase;
    static String TAG = "de_exitdialgo";
    static String TAG_ = "de_exitdialgo";
    static PLACEHOLDER placeholder = PLACEHOLDER.DEFAULT;

    private static String findValueInMap(String key, Map<String, String> map) {
        key = key.toUpperCase(Locale.ROOT);
        Log.d(TAG_, "findValueInMap: key " + key);
        String value = "default";
        if (map == null) {
            Log.e(TAG_, "findValueInMapInterstitial: map is null");
        } else {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Log.i(TAG_, "findValueInMap: " + entry.getKey());
                if (entry.getKey().equals(key.toUpperCase(Locale.ROOT))) {
                    value = entry.getValue();
                    Log.d(TAG_, "findValueInMap:find " + value);
                    break;

                }
            }
        }
        Log.d(TAG_, "findValueInMap:value  " + value);
        return value;

    }

    private static Integer[] getPriorityAgainstPlaceHolder(PLACEHOLDER placeholder, Integer[] tempAdPriorityList) {

        if (placeholder.toString().toUpperCase(Locale.ROOT).equals(PLACEHOLDER.DEFAULT)) {
            Log.d(TAG, "getPriorityAgainstPlaceHolder: find default");
            return tempAdPriorityList;
        } else {
            if (AdHelperApplication.placeholderConfig != null) {
                Integer[] rearrange = new Integer[3];
                String value = findValueInMap(placeholder.name().toLowerCase(Locale.ROOT).toString(), AdHelperApplication.placeholderConfig.exit_dialog);

                if (value.equals("admob") || value.equals("1") || value.equals("01")) {
                    rearrange[0] = MediationAdHelper.AD_ADMOB;
                    rearrange[1] = MediationAdHelper.AD_FACEBOOK;
                    rearrange[2] = MediationAdHelper.AD_CUBI_IT;
                } else if (value.equals("fb") || value.equals("facebook") || value.equals("2") || value.equals("02")) {
                    rearrange[0] = MediationAdHelper.AD_FACEBOOK;
                    rearrange[1] = MediationAdHelper.AD_ADMOB;
                    rearrange[2] = MediationAdHelper.AD_CUBI_IT;
                } else if (value.equals("default") || value.equals("DEFAULT") || value.equals("-1")) {
                    rearrange = tempAdPriorityList;
                }
                // Log.d(TAG_, "getPriorityAgainstPlaceHolder: " + rearrange);
                return rearrange;
            } else {
                return tempAdPriorityList;
            }
        }
    }

    public static void startFacebookDialog(boolean b, PLACEHOLDER placeholder, Activity activity, String appName, OnBackPressListener onBackPressListener) {
        startDialog(b, placeholder, activity, appName, 0, onBackPressListener);
    }

    public static void startDialog(boolean b, PLACEHOLDER placeholder, Activity activity, String appName, int adPriority, OnBackPressListener onBackPressListener) {
        startDialog(b, placeholder, activity, appName, adPriority, true, onBackPressListener);
    }

    public static void startDialog(boolean b, PLACEHOLDER p, Activity activity, String appName, int adPriority, boolean showReviewButton, OnBackPressListener onBackPressListener) {
        Integer[] tempAdPriorityList = new Integer[2];
        tempAdPriorityList[0] = adPriority;
        placeholder = p;
        if (adPriority == MediationAdHelper.AD_FACEBOOK) {
            tempAdPriorityList[1] = MediationAdHelper.AD_ADMOB;
        } else {
            tempAdPriorityList[1] = MediationAdHelper.AD_FACEBOOK;
        }
        startDialog(b, p, activity, appName, getPriorityAgainstPlaceHolder(p, tempAdPriorityList), showReviewButton, onBackPressListener);

    }

    public static void startDialog(boolean b, PLACEHOLDER placeholder, Activity activity, String appName, Integer[] adPriorityList, boolean showReviewButton, OnBackPressListener onBackPressListener) {
        startDialog(b, placeholder, activity, appName, adPriorityList, showReviewButton, onBackPressListener, null);
    }

    private static boolean checkTestIds(OnBackPressListener listener) {
        if (AdHelperApplication.getAdIDs().getFb_native_id().equals(TEST_FB_NATIVE_ID) || AdHelperApplication.getAdIDs().getAdmob_native_id().equals(TEST_ADMOB_NATIVE_ID)) {
            listener.onError("Found Test IDS..");
            return false;
        }
        return true;
    }

    public static void startDialog(boolean b, PLACEHOLDER placeholder, Activity activity, String appName, Integer[] adPriorityList, boolean showReviewButton, OnBackPressListener onBackPressListener, MediationAdHelper.ImageProvider imageProvider) {
       /* if (!AdHelperApplication.getTestMode()) {
            //check if ids or test skip
            if (AdHelperApplication.getAdIDs() != null) {
                if (!checkTestIds(onBackPressListener))
                    return;
            }
        }*/
        MediationEvents.onDialogAdCalledEvent();
        Intent intent = new Intent(activity, MediationBackPressDialog.class);
        intent.putExtra(EXTRA_APP_NAME, appName);
        intent.putExtra(EXTRA_FACEBOOK_KEY, TEST_FB_NATIVE_ID);
        intent.putExtra(EXTRA_ADMOB_KEY, TEST_ADMOB_NATIVE_ID);
        intent.putExtra(EXTRA_SHOW_REVIEW_BUTTON, showReviewButton);
        intent.putExtra(EXTRA_AD_PRIORITY_LIST, new ArrayList<>(Arrays.asList(getPriorityAgainstPlaceHolder(placeholder, adPriorityList))));
        intent.putExtra(EXTRA_IS_PURCHASE, b);

        if (onBackPressListener == null) {
            throw new RuntimeException("OnBackPressListener can not null");
        }
        MediationBackPressDialog.onBackPressListener = onBackPressListener;
        MediationBackPressDialog.imageProvider = imageProvider;

        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    public static void startAdmobDialog(boolean isPurchase, PLACEHOLDER placeholder, Activity activity, String appName, String admobKey, OnBackPressListener onBackPressListener) {
        startDialog(isPurchase, placeholder, activity, appName, 0, onBackPressListener);
    }

    public static int getScreenWidth(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().width() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        try {
            getSupportActionBar().hide();
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

        setupFromSavedInstanceState(savedInstanceState);

        setContentView(R.layout.dialog_backpress);

        setFinishOnTouchOutside(false);

        initView();
        showReviewButton();
        checkReview();
        //provide null overads
        adViewNativeAd = new MediationNativeAd(isPurchase, placeholder, adViewContainer, this, appName, imageProvider);

        MediationNativeAd.loadAD(adPriorityList, new OnNativeAdListener() {
            @Override
            public void onError(String errorMessage) {
                if (onBackPressListener != null) {
                    MediationEvents.onDialogAdErrorEvent();
                    onBackPressListener.onError(errorMessage);
                }
            }

            @Override
            public void onLoaded(int adType) {
                if (onBackPressListener != null) {
                    onBackPressListener.onLoaded(adType);
                    MediationEvents.onDialogAdSuccessEvent(adType);
                }
            }

            @Override
            public void onAdClicked(int adType) {
                if (onBackPressListener != null) {
                    onBackPressListener.onAdClicked(adType);
                    MediationEvents.onDialogAdClickedEvent();
                }
            }
        });
    }

    private void showReviewButton() {
        if (!showReviewButton) {
            dividerBtn.setVisibility(View.GONE);
            tvReview.setVisibility(View.GONE);
        }
    }

    private void checkReview() {
        boolean isReview = SharedPreferenceUtil.getSharedPreference(this, SharedPreferenceUtil.REVIEW, false);
        if (isReview) {
            dividerBtn.setVisibility(View.GONE);
            tvReview.setVisibility(View.GONE);
        }
    }

    private void setupFromSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            appName = savedInstanceState.getString(EXTRA_APP_NAME);
            facebookKey = savedInstanceState.getString(EXTRA_FACEBOOK_KEY);
            admobKey = savedInstanceState.getString(EXTRA_ADMOB_KEY);
            adPriorityList = savedInstanceState.getIntegerArrayList(EXTRA_AD_PRIORITY_LIST);
            showReviewButton = savedInstanceState.getBoolean(EXTRA_SHOW_REVIEW_BUTTON);
            isPurchase = savedInstanceState.getBoolean(EXTRA_IS_PURCHASE);
        } else {
            appName = getIntent().getStringExtra(EXTRA_APP_NAME);
            facebookKey = getIntent().getStringExtra(EXTRA_FACEBOOK_KEY);
            admobKey = getIntent().getStringExtra(EXTRA_ADMOB_KEY);
            adPriorityList = getIntent().getIntegerArrayListExtra(EXTRA_AD_PRIORITY_LIST);
            showReviewButton = getIntent().getBooleanExtra(EXTRA_SHOW_REVIEW_BUTTON, false);
            isPurchase = getIntent().getBooleanExtra(EXTRA_IS_PURCHASE, false);
        }
    }

    private void initView() {
        adViewContainer = (ViewGroup) findViewById(R.id.adview_container);
        dividerBtn = findViewById(R.id.divider_btn);
        tvReview = (TextView) findViewById(R.id.tv_review);
        tvReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReviewClick();
            }
        });
        tvFinish = (TextView) findViewById(R.id.tv_finish);
        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFinishClick();
            }
        });
    }

    private void onReviewClick() {
        AppUtil.openPlayStore(this, getPackageName());
        SharedPreferenceUtil.putSharedPreference(this, SharedPreferenceUtil.REVIEW, true);
        if (onBackPressListener != null) {
            onBackPressListener.onReviewClick();
        }
    }

    private void onFinishClick() {
        finish();
        overridePendingTransition(0, 0);
        if (onBackPressListener != null) {
            onBackPressListener.onFinish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(EXTRA_APP_NAME, appName);
        savedInstanceState.putString(EXTRA_FACEBOOK_KEY, facebookKey);
        savedInstanceState.putString(EXTRA_ADMOB_KEY, admobKey);
        savedInstanceState.putIntegerArrayList(EXTRA_AD_PRIORITY_LIST, adPriorityList);
        savedInstanceState.putBoolean(EXTRA_IS_PURCHASE, isPurchase);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        if (adViewNativeAd != null) {
            adViewNativeAd.onDestroy();
        }
        onBackPressListener = null;
        imageProvider = null;

        super.onDestroy();
    }
}
