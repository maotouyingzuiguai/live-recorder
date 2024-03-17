package cloud.demarcia.model;

import java.util.ArrayList;
import java.util.List;

public class M3U8ParseResult {
    public List<String> tsFileUrlList = new ArrayList<>();
    public long startSegment = 0;
    public int avgDuration = 0;


    public List<String> getTsFileUrlList() {
        return tsFileUrlList;
    }

    public void setTsFileUrlList(List<String> tsFileUrlList) {
        this.tsFileUrlList = tsFileUrlList;
    }

    public long getStartSegment() {
        return startSegment;
    }

    public void setStartSegment(long startSegment) {
        this.startSegment = startSegment;
    }

    public int getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(int avgDuration) {
        this.avgDuration = avgDuration;
    }

    @Override
    public String toString() {
        return "M3u8ParseResult{" +
                "tsFileUrlList=" + tsFileUrlList +
                ", startSegment=" + startSegment +
                ", avgDuration=" + avgDuration +
                '}';
    }
}
