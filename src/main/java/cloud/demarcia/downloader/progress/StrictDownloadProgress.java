package cloud.demarcia.downloader.progress;

import cloud.demarcia.exception.TSDownloadException;
import cloud.demarcia.exception.UnhandledURLException;
import cn.hutool.core.lang.Console;

import java.text.ParseException;

public class StrictDownloadProgress implements DownloadProgress{
    @Override
    public void onStop() {
        Console.log("stopped");
    }

    @Override
    public void onUpdate(long sequence, int sizeMB) {
        Console.log("updated "+sequence+" size:"+sizeMB+" MB.");
    }

    @Override
    public void onParseURLError(String info,String url) {
        throw new UnhandledURLException(info+url);
    }

    @Override
    public void onDownloadTSError(String info,String url, String sequence) {
        throw new TSDownloadException(info+url+" seq:"+sequence);
    }
}
