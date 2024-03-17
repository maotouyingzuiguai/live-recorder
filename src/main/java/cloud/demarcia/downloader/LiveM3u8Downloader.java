package cloud.demarcia.downloader;

import cloud.demarcia.downloader.progress.DownloadProgress;
import cloud.demarcia.downloader.progress.StrictDownloadProgress;
import cloud.demarcia.exception.TSDownloadException;
import cloud.demarcia.model.M3U8ParseResult;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;


public class LiveM3u8Downloader {
    private final String location;
    private final String fileName;
    private final DownloadProgress progress;
    private final File mergedFile;
    private volatile long lastSegment = 0L;
    private volatile DownloadStatus status;

    public LiveM3u8Downloader(String location, String fileName) {
        this.location = location;
        this.fileName = fileName;
        this.mergedFile = getMergedFile();
        this.status = DownloadStatus.PENDING;
        this.progress = new StrictDownloadProgress();
    }

    public LiveM3u8Downloader(String location, String fileName, DownloadProgress progress) {
        this.location = location;
        this.fileName = fileName;
        this.mergedFile = getMergedFile();
        this.status = DownloadStatus.PENDING;
        this.progress = progress;
    }

    private File getMergedFile() {
        String path = location + "/" + fileName;
        return FileUtil.file(path);
    }

    public void downloadStream(String url) {
        while (true) {
            int result = downloadOnce(url);
            if (result == -1) {
                Console.log("Cannot fetch data. retrying...");
                ThreadUtil.safeSleep(1000);
                if (downloadOnce(url) == -1) {
                    break;
                } else {
                    continue;
                }
            }
            merge2Main();
//            Console.log("Wait for {} seconds.", result / 1000);
            status = DownloadStatus.WAITING;
            progress.onUpdate(lastSegment, (int) (FileUtil.size(mergedFile) / (1000 * 1000)));
            ThreadUtil.safeSleep(result);
        }
        status = DownloadStatus.WAITING;
        ThreadUtil.safeSleep(1000);
        merge2Main();
        status = DownloadStatus.STOPPED;
        progress.onStop();
    }

    public DownloadStatus getStatus() {
        return status;
    }


    private void merge2Main() {
        status = DownloadStatus.MERGING;
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(mergedFile, true));
            long start=0;
            for (long sequence = lastSegment; sequence >= 0 && FileUtil.exist(location + "/" + sequence + ".ts"); sequence--) {
                start=sequence;
            }
            for (long sequence = start; sequence <=lastSegment; sequence++) {
                BufferedInputStream inputStream = FileUtil.getInputStream(location + "/" + sequence + ".ts");
                outputStream.write(inputStream.readAllBytes());

                outputStream.flush();
                inputStream.close();
                FileUtil.del(location + "/" + sequence + ".ts");
            }

            outputStream.close();
        } catch (Exception ignored) {
        }
    }

    /**
     * @return -1 retry & terminate | >0 loop (second)
     */
    private int downloadOnce(String url) {
        status = DownloadStatus.DOWNLOADING;
        class WaitingTime {
            private final double seg;
            double total = 0D;
            boolean start = false;

            WaitingTime(double seg) {
                this.seg = seg;
            }

            public void add() {
                if (!start) {
                    start = true;
                    return;
                }
                total += seg;
                total -= 0.1D;
            }

            public int getSuggestedWaitingMillSecond() {
                if (total < 1) {
                    return 1000;
                }
                int a = (int) (total * 1000 - 1000);
                return Math.max(a, 1000);
            }

        }

        M3U8Parser parser = new M3U8Parser();
        M3U8ParseResult parseResult = parser.parse(url);
        if (Objects.isNull(parseResult)) {
            Console.log("Live terminated or network disconnected.");
            return -1;
        }
        WaitingTime waitingTime = new WaitingTime(parseResult.avgDuration);
        long currentSequence = parseResult.startSegment;
        for (String uri : parseResult.tsFileUrlList) {
            waitingTime.add();
            if (currentSequence > lastSegment) {
                if (lastSegment != 0L && currentSequence - lastSegment != 1)
                    progress.onDownloadTSError("Some ts block overlooked!", url, String.valueOf(currentSequence));
                String fileName = currentSequence + ".ts";
                File file = FileUtil.touch(location + "/" + fileName);
                try {
                    if (HttpUtil.downloadFile(uri, file) > 0) {
//                        Console.log("downloaded segment:{}", currentSequence);
                        lastSegment = currentSequence;
                    } else {
                        throw new TSDownloadException("Download filed.Download size==0 url:" + uri + "seg:" + currentSequence);
                    }
                } catch (Exception e) {
                    throw new TSDownloadException("Download filed.Download Exception! url:" + uri + "seg:" + currentSequence);
                }
            }
            currentSequence++;
        }

        return waitingTime.getSuggestedWaitingMillSecond();
    }


}
