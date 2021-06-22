package mediation.helper.cubiad.NativeAdView;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.MuteThisAdListener;
import com.google.android.gms.ads.MuteThisAdReason;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.common.annotation.KeepForSdk;

import java.util.List;

public abstract  class NativeCubiAd {
    public NativeCubiAd() {
    }

    public abstract String getHeadline();

    public abstract List<NativeAd.Image> getImages();

    public abstract String getBody();

    public abstract NativeAd.Image getIcon();

    public abstract String getCallToAction();

    public abstract String getAdvertiser();

    public abstract Double getStarRating();

    public abstract String getStore();

    public abstract String getPrice();

    public abstract NativeAd.AdChoicesInfo getAdChoicesInfo();

    public abstract boolean isCustomMuteThisAdEnabled();

    public abstract List<MuteThisAdReason> getMuteThisAdReasons();

    public abstract void muteThisAd(MuteThisAdReason var1);

    public abstract void setMuteThisAdListener(MuteThisAdListener var1);

    public abstract Bundle getExtras();

    public abstract void destroy();

    public abstract void setUnconfirmedClickListener(NativeAd.UnconfirmedClickListener var1);

    public abstract void cancelUnconfirmedClick();

    public abstract void enableCustomClickGesture();

    public abstract boolean isCustomClickGestureEnabled();

    public abstract void recordCustomClickGesture();

    @KeepForSdk
    public abstract void performClick(Bundle var1);

    @KeepForSdk
    public abstract boolean recordImpression(Bundle var1);

    @KeepForSdk
    public abstract void reportTouchEvent(Bundle var1);

    public abstract MediaContent getMediaContent();

    protected abstract Object zzjs();

    @Nullable
    public abstract ResponseInfo getResponseInfo();

    public abstract void setOnPaidEventListener(@Nullable OnPaidEventListener var1);

    public abstract static class AdChoicesInfo {
        public AdChoicesInfo() {
        }

        public abstract CharSequence getText();

        public abstract List<NativeAd.Image> getImages();
    }

    public abstract static class Image {
        public Image() {
        }

        @Nullable
        public abstract Drawable getDrawable();

        @Nullable
        public abstract Uri getUri();

        public abstract double getScale();
    }

    public interface UnconfirmedClickListener {
        void onUnconfirmedClickReceived(String var1);

        void onUnconfirmedClickCancelled();
    }

    public interface OnNativeAdLoadedListener {
        void onNativeAdLoaded(NativeAd var1);
    }
}
