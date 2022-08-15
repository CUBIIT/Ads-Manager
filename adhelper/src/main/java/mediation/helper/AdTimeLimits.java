package mediation.helper;

public class AdTimeLimits {
   public static String BANNER_KEY="BANNER_KEYS";
   public static String INTERSTITIAL_KEY="INTERSTITIAL_KEY";
   public static String NATIVE_BANNER_KEY="NATIVE_BANNER_KEY";
   public static String NATIVE_KEY="NATIVE_KEY";
    boolean can_skip;
    long banner_ad_time;
    long interstitial_ad_time;
    long native_ad_time;
    public AdTimeLimits(boolean can_skip, long banner_ad_time, long interstitial_ad_time, long native_ad_time) {
        this.can_skip = can_skip;
        this.banner_ad_time = banner_ad_time;
        this.interstitial_ad_time = interstitial_ad_time;
        this.native_ad_time = native_ad_time;
    }

    public AdTimeLimits() {
    }

    public boolean isCan_skip() {
        return can_skip;
    }

    public void setCan_skip(boolean can_skip) {
        this.can_skip = can_skip;
    }

    public long getBanner_ad_time() {
        return banner_ad_time;
    }

    public void setBanner_ad_time(long banner_ad_time) {
        this.banner_ad_time = banner_ad_time;
    }

    public long getInterstitial_ad_time() {
        return interstitial_ad_time;
    }

    public void setInterstitial_ad_time(long interstitial_ad_time) {
        this.interstitial_ad_time = interstitial_ad_time;
    }

    public long getNative_ad_time() {
        return native_ad_time;
    }

    public void setNative_ad_time(long native_ad_time) {
        this.native_ad_time = native_ad_time;
    }
}
