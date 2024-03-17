package cloud.demarcia.streamFinder;


import cloud.demarcia.BrowserConfig;
import cloud.demarcia.model.RecordingMeta;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import java.io.IOException;
import java.util.Map;


public class DouyinStreamFinder implements StreamFinder {


    WebClient webClient= BrowserConfig.webClient;


    private String trimURL(String url){
        return url.split("\\?")[0];
    }


    @Override
    public RecordingMeta findStream(String url) {
        if (!url.contains("live.douyin.com")) return null;
        url=trimURL(url);
        RecordingMeta meta = new RecordingMeta(url, "DOUYIN");
        Page page;
        try {
            page = webClient.getPage(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            String content = page.getWebResponse().getContentAsString(CharsetUtil.CHARSET_UTF_8);
            String group1 = ReUtil.getGroup1("(\\{\\\\\"state\\\\\":.*?)]\\\\n\"]\\)", content);
            String s = StrUtil.removeAll(group1, '\\');
            String d = StrUtil.replace(s, "u0026", "&");
            String room_store = ReUtil.getGroup1("\"roomStore\":(.*?),\"linkmicStore\"", d);
            String anchor_name = ReUtil.getGroup1("\"nickname\":\"(.*?)\",\"avatar_thumb", room_store);
            room_store = room_store.split(",\"has_commerce_goods\"")[0] + "}}}";
            JSONObject obj = JSONUtil.parseObj(room_store);
            JSONObject obj1 = obj.getJSONObject("roomInfo").getJSONObject("room");

            meta.setAnchor(anchor_name);
            if (obj1.getInt("status") != 2) {
                Console.log("当前主播 " + anchor_name + " 未开播");
                return null;
            }
            meta.setLiveName(obj1.getStr("title"));
            getDouyinStreamUrl(obj1, meta);
        } catch (Exception e) {
            Console.log("parse info failed.");
            return null;
        }
        return meta;
    }


    private void getDouyinStreamUrl(JSONObject json, RecordingMeta meta) {
        JSONObject urlMap = json.getJSONObject("stream_url").getJSONObject("hls_pull_url_map");
        JSONObject flvLinks = json.getJSONObject("stream_url").getJSONObject("flv_pull_url");
        for (Map.Entry<String, Object> entry : urlMap) {
            String key = entry.getKey();
            meta.setQuality(key.contains("FULL") ? "原画" : key);
            meta.setHlsLink(urlMap.getStr(key));
            break;
        }
        for (Map.Entry<String, Object> entry : flvLinks) {
            String key = entry.getKey();
            meta.setFlvLink(flvLinks.getStr(key));
            break;
        }
    }

}
