package progresys.com.scandatamatrix.NetworkUtils;

public interface CallBack {
    public void onProgress();
    public void onResult(String result);
    public void onResultExtra(String result, int id);
    public void onCancel();
}