package cloud.demarcia.downloader.progress;

public interface DownloadProgress {
    void onStop();

    void onUpdate(long sequence,int sizeMB);

    void onParseURLError(String info,String url);

    void onDownloadTSError(String info,String url,String sequence);
}
