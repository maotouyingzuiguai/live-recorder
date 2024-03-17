package cloud.demarcia.downloader;

import cloud.demarcia.exception.TSDownloadException;
import cloud.demarcia.exception.UnhandledURLException;
import cloud.demarcia.model.M3U8ParseResult;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.model.MediaSegment;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import io.lindstrom.m3u8.parser.PlaylistParserException;

public class M3U8Parser {
    public M3U8ParseResult parse(String HLSUrl) {
        String m3u8File = HttpUtil.downloadString(HLSUrl, CharsetUtil.CHARSET_UTF_8);
        if (!checkResultValid(m3u8File)) return null;
        for (String line : m3u8File.split("\n")) {
            if (!line.contains("#") && line.contains("m3u8") && line.contains("http")) {
                m3u8File = HttpUtil.downloadString(line, CharsetUtil.CHARSET_UTF_8);
                break;
            }
        }
        double tempDuration = 0D;
        MediaPlaylistParser parser = new MediaPlaylistParser();
        M3U8ParseResult result = new M3U8ParseResult();
        MediaPlaylist playlist = null;
        try {
            playlist = parser.readPlaylist(m3u8File);
        } catch (PlaylistParserException e) {
            throw new UnhandledURLException(m3u8File);
        }
        for (MediaSegment segment : playlist.mediaSegments()) {
            String s = completeUrl(HLSUrl, segment.uri());
            tempDuration += segment.duration();
            if (s.contains("ts")) {
                result.tsFileUrlList.add(s);
                continue;
            }
            throw new TSDownloadException("");
        }
        result.startSegment = playlist.mediaSequence();
        result.avgDuration = (int) tempDuration / playlist.mediaSegments().size();
        return result;
    }

    private boolean checkResultValid(String s) {
        if (StrUtil.isEmpty(s) ||  s.contains("not found")) {
            Console.log("Found error: " + s);
            return false;
        }
        return true;
    }

    private String completeUrl(String hlsUrl, String incomplete) {
        if (hlsUrl.contains("stream")) {
            String prefix = hlsUrl.split("stream")[0];
            return prefix + incomplete;
        }
        throw new UnhandledURLException("unable to complete "+incomplete);
    }
}
