package mediation.helper.interstitial;

import static mediation.helper.interstitial.MediationAdInterstitial.cubiInterstitialAd;
import static mediation.helper.interstitial.MediationAdInterstitial.onInterstitialAdListener;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import mediation.helper.R;

public class CubiInterstitialAdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cubit_interstitial_ad);
        Log.d("TAG1_cubiact", "onCreate: ");
         
        ImageView cancel = findViewById(R.id.image_view_cancel);
        TextView title = findViewById(R.id.native_ad_title);
        RatingBar adRatingBar = findViewById(R.id.ad_stars);
        TextView adFeedback = findViewById(R.id.ad_stars_feedback_num);
        FloatingActionButton floatingActionButton = findViewById(R.id.floating_button_calltoaction);
        TextView bodyText = findViewById(R.id.ad_body);
        TextView advertiserName = findViewById(R.id.ad_advertiser);
        ImageView cubiInterstitialSquareIcon = findViewById(R.id.cubi_interstitial_square_icon);
        ImageView mediaContent = findViewById(R.id.ad_media);
        Button callToAction = findViewById(R.id.ad_call_to_action);
        LinearLayout linearLayoutRatingFeedback = findViewById(R.id.linear_layout_rating_feedback);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //to remain the logic same.so call onbeforeAdshow to move next activity
                    onInterstitialAdListener.onDismissed(3);
                    Log.d("TAG1_cubiact", "onClick: dissmiss");
                }catch (Exception e){
                    Log.d("TAG1_cubiact", "onClick: "+ e.getMessage());
                }
                finish();
            }
        });
        try {
            if (!(cubiInterstitialAd.getInterstitialAdRating() == null || cubiInterstitialAd.getInterstitialAdRating().equals(""))) {
                Log.d("TAG1", "initCubiITIntersitialAd: innner");
                linearLayoutRatingFeedback.setVisibility(View.VISIBLE);
                adRatingBar.setRating(Long.parseLong(cubiInterstitialAd.getInterstitialAdRating()));
                adFeedback.setText("(" + cubiInterstitialAd.getInterstitialAdFeedback() + ")");
            }
        } catch (Exception e) {
            Log.d("TAG1", "initCubiITIntersitialAd:11 " + e.getMessage());
            MediationAdInterstitial.onLoadError(e.getMessage());
        }
        try {
            title.setText(cubiInterstitialAd.getInterstitialAdTitle());
            bodyText.setText(cubiInterstitialAd.getInterstitialAdBodyText());
            advertiserName.setText(cubiInterstitialAd.getInterstitialAdvertiserName());
            Glide.with(getApplicationContext())
                    .load(cubiInterstitialAd.getInterstitialSquareIconUrl())
                    .centerCrop()
                    .into(cubiInterstitialSquareIcon);
            Glide.with(getApplicationContext())
                    .load(cubiInterstitialAd.getInterstitialFeatureIconUrl())
                    .into(mediaContent);

            callToAction.setText(cubiInterstitialAd.getInterstitialAdCallToActionData());
            callToAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                         
                        onInterstitialAdListener.onAdClicked(3);
                        actionOnCubiAdClicked();
                    }catch (Exception e){e.printStackTrace();}
                }
            });
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {try {
                     
                    onInterstitialAdListener.onAdClicked(3);
                    actionOnCubiAdClicked();
                }catch (Exception e){
                    e.printStackTrace();
                }
                }
            });
            mediaContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                         
                        onInterstitialAdListener.onAdClicked(3);
                        actionOnCubiAdClicked();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            onInterstitialAdListener.onLoaded(3);
        } catch (Exception e) {
            Log.d("TAG1", "initCubiITIntersitialAd:11 " + e.getMessage());
            MediationAdInterstitial.onLoadError(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        //fix NullPointException
        try {
            if(onInterstitialAdListener != null){
                 onInterstitialAdListener.onDismissed(3);
                 Log.d("TAG1_", "onBackPressed: onInterstitalAd Dismiss");
            }
            else
                Log.d("TAG1_", "onBackPressed: listener is null");
        } catch (Exception ignor) {
            ignor.printStackTrace();
        }
        super.onBackPressed();
    }

    public void actionOnCubiAdClicked() {
        String url = cubiInterstitialAd.getInterstitialAdUrlLink();
        if (url.isEmpty()) {
             
            onInterstitialAdListener.onAdClicked(3);
            onInterstitialAdListener.onBeforeAdShow();
            finish();
            return;
        }
//        String packageName = "";
//        //split package name
//        if(url.contains("play.google.com/store/apps")){
//            Log.d("de_", "actionOnCubiAdClicked: contains");
//            String[] a = url.split("=");
//            packageName = a[1];
//        }else{
//            packageName = url;
//        }
//
//        Uri uri = Uri.parse("market://details?id=" + packageName);
//        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
//                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
//                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            this.startActivity(i);
//            this.startActivity(new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {
             
            onInterstitialAdListener.onError("Action OnClick: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
