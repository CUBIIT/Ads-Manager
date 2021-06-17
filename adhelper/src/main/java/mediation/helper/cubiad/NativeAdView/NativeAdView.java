package mediation.helper.cubiad.NativeAdView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.nativead.AdChoicesView;
import com.google.android.gms.ads.nativead.MediaView;
//import com.google.android.gms.ads.nativead.NativeAd;
//import com.google.android.gms.ads.nativead.zzb;
//import com.google.android.gms.ads.nativead.zzc;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.internal.ads.zzaba;
import com.google.android.gms.internal.ads.zzabp;
import com.google.android.gms.internal.ads.zzaev;
import com.google.android.gms.internal.ads.zzazk;
import com.google.android.gms.internal.ads.zzwr;

public class NativeAdView extends FrameLayout {
    private final FrameLayout zzbny;
    private final zzaev zzbnz;

    public NativeAdView(Context var1) {
        super(var1);
        this.zzbny = this.zzd(var1);
        this.zzbnz = this.zzju();
    }

    public NativeAdView(Context var1, AttributeSet var2) {
        super(var1, var2);
        this.zzbny = this.zzd(var1);
        this.zzbnz = this.zzju();
    }

    public NativeAdView(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
        this.zzbny = this.zzd(var1);
        this.zzbnz = this.zzju();
    }

    @TargetApi(21)
    public NativeAdView(Context var1, AttributeSet var2, int var3, int var4) {
        super(var1, var2, var3, var4);
        this.zzbny = this.zzd(var1);
        this.zzbnz = this.zzju();
    }

    private final void zza(String var1, View var2) {
        try {
            this.zzbnz.zzb(var1, ObjectWrapper.wrap(var2));
        } catch (RemoteException var4) {
            zzazk.zzc("Unable to call setAssetView on delegate", var4);
        }
    }

    public final void setHeadlineView(View var1) {
        this.zza("3001", var1);
    }

    public final void setCallToActionView(View var1) {
        this.zza("3002", var1);
    }

    public final void setIconView(View var1) {
        this.zza("3003", var1);
    }

    public final void setBodyView(View var1) {
        this.zza("3004", var1);
    }

    public final void setAdvertiserView(View var1) {
        this.zza("3005", var1);
    }

    public final void setStoreView(View var1) {
        this.zza("3006", var1);
    }

    public final void setClickConfirmingView(View var1) {
        try {
            this.zzbnz.zzf(ObjectWrapper.wrap(var1));
        } catch (RemoteException var3) {
            zzazk.zzc("Unable to call setClickConfirmingView on delegate", var3);
        }
    }

    public final void setPriceView(View var1) {
        this.zza("3007", var1);
    }

    public final void setImageView(View var1) {
        this.zza("3008", var1);
    }

    public final void setStarRatingView(View var1) {
        this.zza("3009", var1);
    }

//    public final void setMediaView(MediaView var1) {
//        this.zza("3010", var1);
//        if (var1 != null) {
//            var1.zza(new zzb(this));
//            var1.zza(new zzc(this));
//        }
//
//    }

    public final void setAdChoicesView(AdChoicesView var1) {
        this.zza("3011", var1);
    }

//    public final void setNativeAd(NativeAd var1) {
//        try {
//            this.zzbnz.zza((IObjectWrapper)var1.zzjs());
//        } catch (RemoteException var3) {
//            zzazk.zzc("Unable to call setNativeAd on delegate", var3);
//        }
//    }

    private final View zzbj(String var1) {
        try {
            IObjectWrapper var2;
            if ((var2 = this.zzbnz.zzco(var1)) != null) {
                return (View)ObjectWrapper.unwrap(var2);
            }
        } catch (RemoteException var3) {
            zzazk.zzc("Unable to call getAssetView on delegate", var3);
        }

        return null;
    }

    public final View getHeadlineView() {
        return this.zzbj("3001");
    }

    public final View getCallToActionView() {
        return this.zzbj("3002");
    }

    public final View getIconView() {
        return this.zzbj("3003");
    }

    public final View getBodyView() {
        return this.zzbj("3004");
    }

    public final View getStoreView() {
        return this.zzbj("3006");
    }

    public final View getPriceView() {
        return this.zzbj("3007");
    }

    public final View getAdvertiserView() {
        return this.zzbj("3005");
    }

    public final View getImageView() {
        return this.zzbj("3008");
    }

    public final View getStarRatingView() {
        return this.zzbj("3009");
    }

    public final MediaView getMediaView() {
        View var1;
        if ((var1 = this.zzbj("3010")) instanceof MediaView) {
            return (MediaView)var1;
        } else {
            if (var1 != null) {
                zzazk.zzdy("View is not an instance of MediaView");
            }

            return null;
        }
    }

    public final AdChoicesView getAdChoicesView() {
        View var1;
        return (var1 = this.zzbj("3011")) instanceof AdChoicesView ? (AdChoicesView)var1 : null;
    }

    public final void destroy() {
        try {
            this.zzbnz.destroy();
        } catch (RemoteException var2) {
            zzazk.zzc("Unable to destroy native ad view", var2);
        }
    }

    private final FrameLayout zzd(Context var1) {
        FrameLayout var2;
        (var2 = new FrameLayout(var1)).setLayoutParams(new LayoutParams(-1, -1));
        this.addView(var2);
        return var2;
    }

    private final zzaev zzju() {
        Preconditions.checkNotNull(this.zzbny, "createDelegate must be called after overlayFrame has been created");
        return this.isInEditMode() ? null : zzwr.zzqo().zza(this.zzbny.getContext(), this, this.zzbny);
    }

    public final void addView(View var1, int var2, android.view.ViewGroup.LayoutParams var3) {
        super.addView(var1, var2, var3);
        super.bringChildToFront(this.zzbny);
    }

    public final void removeView(View var1) {
        if (this.zzbny != var1) {
            super.removeView(var1);
        }
    }

    public final void removeAllViews() {
        super.removeAllViews();
        super.addView(this.zzbny);
    }

    public final void bringChildToFront(View var1) {
        super.bringChildToFront(var1);
        if (this.zzbny != var1) {
            super.bringChildToFront(this.zzbny);
        }

    }

    public final void onVisibilityChanged(View var1, int var2) {
        super.onVisibilityChanged(var1, var2);
        if (this.zzbnz != null) {
            try {
                this.zzbnz.zzc(ObjectWrapper.wrap(var1), var2);
                return;
            } catch (RemoteException var4) {
                zzazk.zzc("Unable to call onVisibilityChanged on delegate", var4);
            }
        }

    }

    public final boolean dispatchTouchEvent(MotionEvent var1) {
        zzaba var3 = zzabp.zzcso;
        if ((Boolean)zzwr.zzqr().zzd(var3) && this.zzbnz != null) {
            try {
                this.zzbnz.zzg(ObjectWrapper.wrap(var1));
            } catch (RemoteException var4) {
                zzazk.zzc("Unable to call handleTouchEvent on delegate", var4);
            }
        }

        return super.dispatchTouchEvent(var1);
    }
}
