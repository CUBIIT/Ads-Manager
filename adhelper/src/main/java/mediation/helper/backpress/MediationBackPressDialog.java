package mediation.helper.backpress;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import java.util.Objects;

import mediation.helper.MediationAdHelper;
import mediation.helper.R;
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

    public static void startFacebookDialog(boolean b, Activity activity, String appName, String facebookKey, OnBackPressListener onBackPressListener) {
        startDialog(b, activity, appName, facebookKey, null, MediationAdHelper.AD_FACEBOOK, onBackPressListener);
    }

    public static void startDialog(boolean b, Activity activity, String appName, String facebookKey, String admobKey, int adPriority, OnBackPressListener onBackPressListener) {
        startDialog(b, activity, appName, facebookKey, admobKey, adPriority, true, onBackPressListener);
    }

    public static void startDialog(boolean b, Activity activity, String appName, String facebookKey, String admobKey, int adPriority, boolean showReviewButton, OnBackPressListener onBackPressListener) {
        Integer[] tempAdPriorityList = new Integer[2];
        tempAdPriorityList[0] = adPriority;
        if (adPriority == MediationAdHelper.AD_FACEBOOK) {
            tempAdPriorityList[1] = MediationAdHelper.AD_ADMOB;
        } else {
            tempAdPriorityList[1] = MediationAdHelper.AD_FACEBOOK;
        }
        startDialog(b, activity, appName, facebookKey, admobKey, tempAdPriorityList, showReviewButton, onBackPressListener);

    }

    public static void startDialog(boolean b, Activity activity, String appName, String facebookKey, String admobKey, Integer[] adPriorityList, boolean showReviewButton, OnBackPressListener onBackPressListener) {
        startDialog(b, activity, appName, facebookKey, admobKey, adPriorityList, showReviewButton, onBackPressListener, null);
    }

    public static void startDialog(boolean b, Activity activity, String appName, String facebookKey, String admobKey, Integer[] adPriorityList, boolean showReviewButton, OnBackPressListener onBackPressListener, MediationAdHelper.ImageProvider imageProvider) {
        Intent intent = new Intent(activity, MediationBackPressDialog.class);
        intent.putExtra(EXTRA_APP_NAME, appName);
        intent.putExtra(EXTRA_FACEBOOK_KEY, facebookKey);
        intent.putExtra(EXTRA_ADMOB_KEY, admobKey);
        intent.putExtra(EXTRA_SHOW_REVIEW_BUTTON, showReviewButton);
        intent.putExtra(EXTRA_AD_PRIORITY_LIST, new ArrayList<>(Arrays.asList(adPriorityList)));
        intent.putExtra(EXTRA_IS_PURCHASE, b);

        if (onBackPressListener == null) {
            throw new RuntimeException("OnBackPressListener can not null");
        }
        MediationBackPressDialog.onBackPressListener = onBackPressListener;
        MediationBackPressDialog.imageProvider = imageProvider;

        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    public static void startAdmobDialog(boolean isPurchase, Activity activity, String appName, String admobKey, OnBackPressListener onBackPressListener) {
        startDialog(isPurchase, activity, appName, null, admobKey, MediationAdHelper.AD_ADMOB, onBackPressListener);
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
      try{
            getSupportActionBar().hide();
        }catch (Exception ignore){
            ignore.printStackTrace();
        }

        setupFromSavedInstanceState(savedInstanceState);

        setContentView(R.layout.dialog_backpress);

        setFinishOnTouchOutside(false);

        initView();
        showReviewButton();
        checkReview();
        //provide null overads
        adViewNativeAd = new MediationNativeAd(isPurchase, adViewContainer, this, appName, facebookKey, admobKey, imageProvider);

        MediationNativeAd.loadAD(adPriorityList, new OnNativeAdListener() {
            @Override
            public void onError(String errorMessage) {
                if (onBackPressListener != null) {
                    onBackPressListener.onError(errorMessage);
                }
            }

            @Override
            public void onLoaded(int adType) {
                if (onBackPressListener != null) {
                    onBackPressListener.onLoaded(adType);
                }
            }

            @Override
            public void onAdClicked(int adType) {
                if (onBackPressListener != null) {
                    onBackPressListener.onAdClicked(adType);
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
