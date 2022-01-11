package org.cubiit.admanager;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import mediation.helper.MediationAdHelper;
import mediation.helper.backpress.MediationBackPressDialog;
import mediation.helper.backpress.OnBackPressListener;
import mediation.helper.interstitial.MediationAdInterstitial;
import mediation.helper.interstitial.OnInterstitialAdListener;

public class MainActivity extends AppCompatActivity {
    OnItemClickListener onItemClickListener;
    RecyclerView recyclerView;
    List <Item> adsList;
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
            MediationAdHelper.AD_CUBI_IT
           };
    FirebaseAnalytics firebaseAnalytics ;
    public void startAction() {
        recyclerView = findViewById(R.id.recycler_view);
        adsList = new ArrayList <>();
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




        MediationBackPressDialog.startDialog(false, MainActivity.this, getResources().getString(R.string.app_name)
                , KEY_PRIORITY_NATIVE_AD
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

    private void showInterstitalAds() {

        try {
            MediationAdInterstitial.showInterstitialAd(false, this,
                    KEY_PRIORITY_INTERSTITIAL_AD,
                    new OnInterstitialAdListener() {
                        @Override
                        public void onDismissed(int adType) {
                            hideDialog();

                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.d(TAG, "onError: " + errorMessage);
                            hideDialog();


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
    public class AdsAdapter extends RecyclerView.Adapter <AdsAdapter.AdsHolder> {
        List <Item> itemsList;
        OnItemClickListener onItemClickListener;

        public AdsAdapter(List <Item> itemsList, OnItemClickListener onItemClickListener) {
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