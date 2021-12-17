package mediation.helper;

import android.os.Parcel;
import android.os.Parcelable;

public class AdIDs implements Parcelable {
    boolean test_mode;
    String release;

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    protected AdIDs(Parcel in) {
        test_mode = in.readByte() != 0;
        release = in.readString();
        fb_banner_id = in.readString();
        fb_native_id = in.readString();
        fb_interstitial_id = in.readString();
        admob_interstitial_id = in.readString();
        admob_native_id = in.readString();
        admob_app_id = in.readString();
        admob_banner_id = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (test_mode ? 1 : 0));
        dest.writeString(fb_banner_id);
        dest.writeString(fb_native_id);
        dest.writeString(fb_interstitial_id);
        dest.writeString(admob_interstitial_id);
        dest.writeString(admob_native_id);
        dest.writeString(admob_app_id);
        dest.writeString(admob_banner_id);
        dest.writeString(release);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator <AdIDs> CREATOR = new Creator <AdIDs>() {
        @Override
        public AdIDs createFromParcel(Parcel in) {
            return new AdIDs(in);
        }

        @Override
        public AdIDs[] newArray(int size) {
            return new AdIDs[size];
        }
    };

    public boolean isTest_mode() {
        return test_mode;
    }

    public void setTest_mode(boolean test_mode) {
        this.test_mode = test_mode;
    }

    public AdIDs(String fb_banner_id, String fb_native_id, String fb_interstitial_id) {
        this.fb_banner_id = fb_banner_id;
        this.fb_native_id = fb_native_id;
        this.fb_interstitial_id = fb_interstitial_id;
    }

    public AdIDs(String fb_banner_id, String fb_native_id, String fb_interstitial_id, String admob_interstitial_id, String admob_native_id, String admob_app_id, String admob_banner_id) {
        this.fb_banner_id = fb_banner_id;
        this.fb_native_id = fb_native_id;
        this.fb_interstitial_id = fb_interstitial_id;
        this.admob_interstitial_id = admob_interstitial_id;
        this.admob_native_id = admob_native_id;
        this.admob_app_id = admob_app_id;
        this.admob_banner_id = admob_banner_id;
    }

    public AdIDs(String admob_interstitial_id, String admob_native_id, String admob_app_id, String admob_banner_id) {
        this.admob_interstitial_id = admob_interstitial_id;
        this.admob_native_id = admob_native_id;
        this.admob_app_id = admob_app_id;
        this.admob_banner_id = admob_banner_id;
    }

    String fb_banner_id;

    public String getFb_banner_id() {
        return fb_banner_id;
    }

    public void setFb_banner_id(String fb_banner_id) {
        this.fb_banner_id = fb_banner_id;
    }

    public String getFb_native_id() {
        return fb_native_id;
    }

    public void setFb_native_id(String fb_native_id) {
        this.fb_native_id = fb_native_id;
    }

    public String getFb_interstitial_id() {
        return fb_interstitial_id;
    }

    public void setFb_interstitial_id(String fb_interstitial_id) {
        this.fb_interstitial_id = fb_interstitial_id;
    }

    public String getAdmob_interstitial_id() {
        return admob_interstitial_id;
    }

    public void setAdmob_interstitial_id(String admob_interstitial_id) {
        this.admob_interstitial_id = admob_interstitial_id;
    }

    public String getAdmob_native_id() {
        return admob_native_id;
    }

    public void setAdmob_native_id(String admob_native_id) {
        this.admob_native_id = admob_native_id;
    }

    public String getAdmob_app_id() {
        return admob_app_id;
    }

    public void setAdmob_app_id(String admob_app_id) {
        this.admob_app_id = admob_app_id;
    }

    public String getAdmob_banner_id() {
        return admob_banner_id;
    }

    public void setAdmob_banner_id(String admob_banner_id) {
        this.admob_banner_id = admob_banner_id;
    }

    String fb_native_id;
    String fb_interstitial_id;
    String admob_interstitial_id;
    String admob_native_id;
    String admob_app_id;
    String admob_banner_id;

    public AdIDs() {
    }


    public static Creator <AdIDs> getCREATOR() {
        return CREATOR;
    }

}
