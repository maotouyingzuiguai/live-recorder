package cloud.demarcia;


import cloud.demarcia.downloader.LiveM3u8Downloader;
import cloud.demarcia.downloader.progress.DownloadProgress;
import cloud.demarcia.downloader.progress.StrictDownloadProgress;
import cloud.demarcia.model.RecordingMeta;
import cloud.demarcia.streamFinder.DouyinStreamFinder;
import cloud.demarcia.streamFinder.StreamFinder;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ThreadUtil;

import java.io.File;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class Recorder {
    private final int startTime;
    private final int endTime;

    private final int interval;

    private final String url;

    private volatile boolean stopped=false;

    private DownloadProgress progress=new StrictDownloadProgress();

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public Recorder(int startTime, int endTime, int interval, String url) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.interval = interval;
        this.url = url;
    }
    public Recorder(int startTime, int endTime, String url) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.interval = 5;
        this.url = url;
    }

    public Recorder(int interval, String url) {
        this.startTime=0;
        this.endTime=0;
        this.interval = interval;
        this.url = url;
    }

    public Recorder(String url) {
        this.startTime=0;
        this.endTime=0;
        this.interval =5;
        this.url = url;
    }

    String location=  new File("").getAbsolutePath()+File.separator+"download";
    {
        Console.log("LOCATION: "+location);
    }

    public RecordingMeta getMetaData(){
        List<StreamFinder> streamFinders = new LinkedList<>();
        streamFinders.add(new DouyinStreamFinder());

        RecordingMeta meta = null;
        for (StreamFinder sf : streamFinders) {
            meta = sf.findStream(url);
            if (!Objects.isNull(meta)) {
                break;
            }
        }
        if (Objects.isNull(meta)) {
            return null;
        }
        Console.log("LIVE ROOM: {}", meta.toString());
        return meta;
    }

    private boolean timeBetween(){
        if(startTime==endTime) return true;
        LocalTime time = LocalTime.now();
        int hour = time.getHour();
        if(startTime<endTime){
            return hour>=startTime && hour<endTime;
        }else{
            return hour>=startTime || hour<(endTime-24);
        }
    }

    public void setProgress(DownloadProgress progress){
        this.progress = progress;
    }

    public void record(){
        RecordingMeta metaData = getMetaData();
        if(!Objects.isNull(metaData)) {
            LiveM3u8Downloader downloader = new LiveM3u8Downloader(location, generateFileNameFromMeta(metaData),progress);
            downloader.downloadStream(metaData.getHlsLink());
        }
        ThreadUtil.safeSleep((long) interval *60*1000);
        while(!stopped){
            if (timeBetween()) {
                metaData = getMetaData();
                if(Objects.isNull(metaData)) {
                    ThreadUtil.safeSleep((long) interval *60*1000);
                    continue;
                }
                LiveM3u8Downloader downloader = new LiveM3u8Downloader(location, generateFileNameFromMeta(metaData),progress);
                downloader.downloadStream(metaData.getHlsLink());
            } else {
                ThreadUtil.safeSleep((long) interval *60*1000);
            }
        }
    }

    private String generateFileNameFromMeta(RecordingMeta metaData){
        String x="record_"+metaData.getAnchor()+"_"+metaData.getLiveName()+"_"
                + DateUtil.format(DateUtil.date(),"yyyyMMdd_HHmmss")+".ts";
        return FileNameUtil.cleanInvalid(x);
    }
}
