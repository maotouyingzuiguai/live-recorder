package cloud.demarcia;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import org.apache.commons.logging.LogFactory;

import java.net.MalformedURLException;
import java.net.URL;


public class BrowserConfig {

    public static WebClient webClient;

    static {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.waitForBackgroundJavaScript(15 * 1000);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setTimeout(600 * 1000);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.setAlertHandler(new AlertHandler() {
            @Override
            public void handleAlert(Page page, String s) {

            }
        });
        webClient.setIncorrectnessListener(new IncorrectnessListener() {
            @Override
            public void notify(String s, Object o) {

            }
        });
        webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {
            @Override
            public void scriptException(HtmlPage htmlPage, ScriptException e) {

            }

            @Override
            public void timeoutError(HtmlPage htmlPage, long l, long l1) {

            }

            @Override
            public void malformedScriptURL(HtmlPage htmlPage, String s, MalformedURLException e) {

            }

            @Override
            public void loadScriptError(HtmlPage htmlPage, URL url, Exception e) {

            }

            @Override
            public void warn(String s, String s1, int i, String s2, int i1) {

            }
        });
        webClient.setJavaScriptTimeout(20000);
    }
}
