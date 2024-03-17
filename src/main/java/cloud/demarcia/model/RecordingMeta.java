package cloud.demarcia.model;

import cn.hutool.core.date.DateUtil;

import java.util.Date;


public class RecordingMeta {

    String liveRoomUrl;
    String anchor;
    String liveName;
    String platform;
    Date startRecordingDate;
    Date endRecordingDate;
    String hlsLink;
    String flvLink;
    String quality;

    @Override
    public String toString() {
        return
                " ANCHOR=" + anchor +
                ", TITLE=" + liveName +
                ", HLS=" + hlsLink +
                ", FLV=" + flvLink ;
    }

    public RecordingMeta(String liveRoomUrl, String platform) {
        this.liveRoomUrl = liveRoomUrl;
        this.platform = platform;
        startRecordingDate= DateUtil.date();
    }

    public String getLiveRoomUrl() {
        return liveRoomUrl;
    }

    public void setLiveRoomUrl(String liveRoomUrl) {
        this.liveRoomUrl = liveRoomUrl;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public String getLiveName() {
        return liveName;
    }

    public void setLiveName(String liveName) {
        this.liveName = liveName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Date getStartRecordingDate() {
        return startRecordingDate;
    }

    public void setStartRecordingDate(Date startRecordingDate) {
        this.startRecordingDate = startRecordingDate;
    }

    public Date getEndRecordingDate() {
        return endRecordingDate;
    }

    public void setEndRecordingDate(Date endRecordingDate) {
        this.endRecordingDate = endRecordingDate;
    }

    public String getHlsLink() {
        return hlsLink;
    }

    public void setHlsLink(String hlsLink) {
        this.hlsLink = hlsLink;
    }

    public String getFlvLink() {
        return flvLink;
    }

    public void setFlvLink(String flvLink) {
        this.flvLink = flvLink;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }
}
