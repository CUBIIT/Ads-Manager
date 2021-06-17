package mediation.helper.cubiad.NativeAdView;

public class CubiNativeAd {
    String nativeAdtitle;
    String nativeAdadvertiserName;
    String nativeAdbodyText;
    String nativeAdIconUrl;
    String nativeAdImageUrl;
    String nativeAdUrlLink;
    String nativeAdcallToActionData;
    String nativeAdrating;
    String nativeAdfeedBack;

    public CubiNativeAd(String nativeAdtitle, String nativeAdadvertiserName, String nativeAdbodyText, String nativeAdImageUrl, String nativeAdUrlLink, String nativeAdcallToActionData) {
        this.nativeAdtitle = nativeAdtitle;
        this.nativeAdadvertiserName = nativeAdadvertiserName;
        this.nativeAdbodyText = nativeAdbodyText;
        this.nativeAdImageUrl = nativeAdImageUrl;
        this.nativeAdUrlLink = nativeAdUrlLink;
        this.nativeAdcallToActionData = nativeAdcallToActionData;
    }

    public CubiNativeAd(String nativeAdtitle, String nativeAdadvertiserName, String nativeAdbodyText, String nativeAdImageUrl, String nativeAdUrlLink, String nativeAdcallToActionData, String nativeAdrating, String nativeAdfeedBack) {
        this.nativeAdtitle = nativeAdtitle;
        this.nativeAdadvertiserName = nativeAdadvertiserName;
        this.nativeAdbodyText = nativeAdbodyText;
        this.nativeAdImageUrl = nativeAdImageUrl;
        this.nativeAdUrlLink = nativeAdUrlLink;
        this.nativeAdcallToActionData = nativeAdcallToActionData;
        this.nativeAdrating = nativeAdrating;
        this.nativeAdfeedBack = nativeAdfeedBack;
    }

    public CubiNativeAd() {
    }

    public String getNativeAdtitle() {
        return nativeAdtitle;
    }

    public void setNativeAdtitle(String nativeAdtitle) {
        this.nativeAdtitle = nativeAdtitle;
    }

    public String getNativeAdadvertiserName() {
        return nativeAdadvertiserName;
    }

    public void setNativeAdadvertiserName(String nativeAdadvertiserName) {
        this.nativeAdadvertiserName = nativeAdadvertiserName;
    }

    public String getNativeAdbodyText() {
        return nativeAdbodyText;
    }

    public void setNativeAdbodyText(String nativeAdbodyText) {
        this.nativeAdbodyText = nativeAdbodyText;
    }

    public String getNativeAdIconUrl() {
        return nativeAdIconUrl;
    }

    public void setNativeAdIconUrl(String nativeAdIconUrl) {
        this.nativeAdIconUrl = nativeAdIconUrl;
    }

    public String getNativeAdImageUrl() {
        return nativeAdImageUrl;
    }

    public void setNativeAdImageUrl(String nativeAdImageUrl) {
        this.nativeAdImageUrl = nativeAdImageUrl;
    }

    public String getNativeAdUrlLink() {
        return nativeAdUrlLink;
    }

    public void setNativeAdUrlLink(String nativeAdUrlLink) {
        this.nativeAdUrlLink = nativeAdUrlLink;
    }

    public String getNativeAdcallToActionData() {
        return nativeAdcallToActionData;
    }

    public void setNativeAdcallToActionData(String nativeAdcallToActionData) {
        this.nativeAdcallToActionData = nativeAdcallToActionData;
    }

    public String getNativeAdrating() {
        return nativeAdrating;
    }

    public void setNativeAdrating(String nativeAdrating) {
        this.nativeAdrating = nativeAdrating;
    }

    public String getNativeAdfeedBack() {
        return nativeAdfeedBack;
    }

    public void setNativeAdfeedBack(String nativeAdfeedBack) {
        this.nativeAdfeedBack = nativeAdfeedBack;
    }
}
