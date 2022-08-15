package mediation.helper.callbacks;

public interface OnFetchRemoteCallback {
    void onFetchValuesSuccess();
    void onFetchValuesFailed();
    void onUpdateSuccess(String appid);
}
