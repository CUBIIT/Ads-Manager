package org.cubiit.admanager;

import static org.cubiit.admanager.AdsActivity.KEY_PRIORITY_NATIVE_AD_TEST;
import static mediation.helper.AdHelperApplication.applyLimitOnAdmob;
import static mediation.helper.AdHelperApplication.canShowInterstitial;
import static mediation.helper.interstitial.MediationAdInterstitial.onInterstitialAdListener;
import static mediation.helper.util.Constant.KEY_PRIORITY_INTERSTITIAL_AD;
import static mediation.helper.util.Constant.KEY_PRIORITY_NATIVE_AD;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.cubiit.admanager.dev.R;

import java.util.ArrayList;
import java.util.List;

import mediation.helper.AdHelperApplication;
import mediation.helper.AdTimeLimits;
import mediation.helper.MediationAdHelper;
import mediation.helper.backpress.MediationBackPressDialog;
import mediation.helper.backpress.OnBackPressListener;
import mediation.helper.config.AdSessions;
import mediation.helper.config.PLACEHOLDER;
import mediation.helper.interstitial.MediationAdInterstitial;
import mediation.helper.interstitial.OnInterstitialAdListener;
import mediation.helper.util.Constant;

public class MainActivity extends AppCompatActivity {
    OnItemClickListener onItemClickListener;
    RecyclerView recyclerView;
    List<Item> adsList;
    AdsAdapter adsAdapter;
    public final static String interstitial = "interstitial";
    public final static String banner = "banner";
    public final static String nativeAds = "native";
    public final static String backPressDialog = "backPress";
    public final static String nativeBanner = "nativeBanner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        startAction();

    }

    public static Integer[] KEY_PRIORITY_INTERSTITIAL_AD_test = new Integer[]{
            MediationAdHelper.AD_ADMOB,
            MediationAdHelper.AD_FACEBOOK,

            MediationAdHelper.AD_CUBI_IT
    };
    FirebaseAnalytics firebaseAnalytics;

    public void startAction() {
        recyclerView = findViewById(R.id.recycler_view);
        adsList = new ArrayList<>();
        adsList.add(new Item(interstitial, getString(R.string.inters)));
        adsList.add(new Item(banner, getString(R.string.banner)));
        adsList.add(new Item(nativeAds, getString(R.string.nativeAd)));
        adsList.add(new Item(backPressDialog, getString(R.string.backPressDialog)));
        adsList.add(new Item(nativeBanner, getString(R.string.nativeBanner)));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        initListener();
        adsAdapter = new AdsAdapter(adsList, onItemClickListener);
        recyclerView.setAdapter(adsAdapter);

    }


    private void initListener() {
        onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                String id = item.getId();
                switch (id) {
                    case interstitial:
                        showDialog();
                       // loadAdmob();
                          showInterstitalAds();
                        break;
                    case nativeAds:
                        startActivity(item, "Native Ad");
                        break;
                    case banner:
                        startActivity(item, "Banner ad");
                        break;
                    case nativeBanner:
                        startActivity(item, "Native Banner Ad");
                        break;
                    case backPressDialog:
                        loadBackPressedDialog();
                        break;
                    default:
                        break;

                }
            }
        };
    }

    private void loadBackPressedDialog() {


        MediationBackPressDialog.startDialog(false, PLACEHOLDER.EXIT_ACTIVITY, MainActivity.this, getResources().getString(R.string.app_name)
                , KEY_PRIORITY_NATIVE_AD_TEST
                , false
                , new OnBackPressListener() {
                    @Override
                    public void onReviewClick() {

                    }

                    @Override
                    public void onFinish() {

                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }

                    @Override
                    public void onLoaded(int adType) {

                    }

                    @Override
                    public void onAdClicked(int adType) {

                    }
                });

    }

    private void startActivity(Item item, String name) {
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("id", item.getId());
        Intent intent = new Intent(MainActivity.this, AdsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    String TAG = "de_main";
    private InterstitialAd mInterstitialAd;

    private void showAd() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.");
                mInterstitialAd = null;
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.");
                mInterstitialAd = null;
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.");
            }
        });
    }

    private void loadAdmob() {

        AdRequest adRequest = new AdRequest.Builder().build();
        Log.i("de_ad", "loadAdmob: " + adRequest.isTestDevice(this));


        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        showAd();
                        mInterstitialAd.show(MainActivity.this);
                        Log.i("de_ad", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("de_ad", loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });


    }

    com.facebook.ads.InterstitialAd facebookInterstitialAD;

    private void showFb() {
        facebookInterstitialAD = new com.facebook.ads.InterstitialAd(this, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID");
        InterstitialAdListener interstitialAdListener = new com.facebook.ads.InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
                Log.d("de_adf", "[FACEBOOK FRONT AD]Displayed");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                Log.d(MediationAdHelper.TAG, "[FACEBOOK FRONT AD]Dismissed");
                // Interstitial dismissed callback

            }

            @Override
            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                Log.i("de_adf", "onError: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(TAG, "onAdLoaded: ");
                long dif = System.currentTimeMillis() - currentTimeStap;
                Log.i("de_fb", "onAdLoaded: " + dif);
                facebookInterstitialAD.show();

            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.i(TAG, "onAdClicked: ");

            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };
        currentTimeStap = System.currentTimeMillis();
        facebookInterstitialAD.loadAd(
                facebookInterstitialAD.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    long currentTimeStap = 0;

    private void showInterstitalAds() {

        try {

            MediationAdInterstitial.showInterstitialAd(false, PLACEHOLDER.ACTIVITY_1, this,
                    KEY_PRIORITY_INTERSTITIAL_AD,
                    new OnInterstitialAdListener() {
                        @Override
                        public void onDismissed(int adType) {
                            hideDialog();
                            Toast.makeText(MainActivity.this, "OnDismiss", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "onError: " + errorMessage);
                            hideDialog();
                            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();



                        }

                        @Override
                        public void onLoaded(int adType) {
                            hideDialog();

                        }

                        @Override
                        public void onBeforeAdShow() {
                        }

                        @Override
                        public void onAdClicked(int adType) {

                        }

                        @Override
                        public void onFacebookAdCreated(com.facebook.ads.InterstitialAd facebookFrontAD) {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //adapter class
    public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.AdsHolder> {
        List<Item> itemsList;
        OnItemClickListener onItemClickListener;

        public AdsAdapter(List<Item> itemsList, OnItemClickListener onItemClickListener) {
            this.itemsList = itemsList;
            this.onItemClickListener = onItemClickListener;
        }

        @NonNull
        @Override
        public AdsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_ads, parent, false);

            return new AdsHolder(root);
        }

        @Override
        public void onBindViewHolder(@NonNull AdsHolder holder, int position) {
            Item item = itemsList.get(position);
            holder.name.setText(item.getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(item);
                }
            });

        }

        @Override
        public int getItemCount() {
            return itemsList.size();
        }


        public class AdsHolder extends RecyclerView.ViewHolder {
            TextView name;

            public AdsHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.ads_name);
            }
        }


    }

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    @Override
    public void onBackPressed() {

        loadBackPressedDialog();
    }

    ProgressDialog dialog;

    private void showDialog() {
        dialog = new ProgressDialog(this);

        dialog.setMessage("loading ads...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void hideDialog() {
        try {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception ignor) {
            ignor.printStackTrace();
        }
    }
}