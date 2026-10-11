package studio.hunter.sanatorio;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {
    private WebView browser;
    private static final String BASE = "https://hunter.mobile.local/";
    private static final String TAG = "SanatorioAndroid";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            getWindow().setStatusBarColor(Color.BLACK);
            getWindow().setNavigationBarColor(Color.BLACK);
            hideSystemBars();
            browser = new WebView(this);
            browser.setBackgroundColor(Color.rgb(6,9,8));
            browser.setOverScrollMode(View.OVER_SCROLL_NEVER);
            browser.setVerticalScrollBarEnabled(false);
            browser.setHorizontalScrollBarEnabled(false);
            browser.setKeepScreenOn(true);
            WebSettings settings = browser.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setMediaPlaybackRequiresUserGesture(false);
            settings.setAllowFileAccess(false);
            settings.setAllowContentAccess(false);
            settings.setSupportZoom(false);
            settings.setBuiltInZoomControls(false);
            settings.setLoadsImagesAutomatically(true);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            settings.setDefaultTextEncodingName("UTF-8");
            if(Build.VERSION.SDK_INT >= 26)
                browser.setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, true);
            browser.setWebChromeClient(new WebChromeClient() {
                @Override public boolean onConsoleMessage(ConsoleMessage msg) {
                    Log.i(TAG,"JS: "+msg.message()+" @"+msg.lineNumber()); return true;
                }
            });
            browser.setWebViewClient(new WebViewClient() {
                @Override public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    try { return assetResponse(request); }
                    catch (Exception ex) {
                        Log.e(TAG,"Asset error "+request.getUrl(),ex);
                        return notFound();
                    }
                }
                @Override public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                    Log.e(TAG,"WebView renderer ended; didCrash="+detail.didCrash());
                    try { browser.destroy(); } catch (Exception ignored) {}
                    browser=null;
                    showError("O Android encerrou o motor gráfico por falta de memória. Feche outros aplicativos e abra o SANATÓRIO novamente.");
                    return true;
                }
            });
            setContentView(browser);
            browser.loadUrl(BASE+"index.html");
        } catch(Throwable ex) {
            Log.e(TAG,"Startup failed",ex);
            showError("Não foi possível iniciar o Sanatório: "+ex.getClass().getSimpleName()+": "+ex.getMessage());
        }
    }

    private void hideSystemBars() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private void showError(String message) {
        TextView t=new TextView(this);
        t.setText("SANATÓRIO\n\n"+message);
        t.setTextColor(Color.WHITE);
        t.setBackgroundColor(Color.rgb(12,16,14));
        t.setPadding(36,40,36,40);
        t.setTextSize(18);
        setContentView(t);
    }

    private static WebResourceResponse notFound() {
        WebResourceResponse r=new WebResourceResponse("text/plain","UTF-8",
            new ByteArrayInputStream(new byte[0]));
        if(Build.VERSION.SDK_INT>=21)r.setStatusCodeAndReasonPhrase(404,"Not Found");
        return r;
    }

    private String mime(String path) {
        String lower=path.toLowerCase(Locale.ROOT);
        if(lower.endsWith(".html"))return "text/html";
        if(lower.endsWith(".js"))return "application/javascript";
        if(lower.endsWith(".css"))return "text/css";
        if(lower.endsWith(".png"))return "image/png";
        if(lower.endsWith(".jpg")||lower.endsWith(".jpeg"))return "image/jpeg";
        if(lower.endsWith(".webp"))return "image/webp";
        if(lower.endsWith(".mp3"))return "audio/mpeg";
        if(lower.endsWith(".wav"))return "audio/wav";
        if(lower.endsWith(".mp4"))return "video/mp4";
        if(lower.endsWith(".webm"))return "video/webm";
        if(lower.endsWith(".json"))return "application/json";
        if(lower.endsWith(".svg"))return "image/svg+xml";
        return "application/octet-stream";
    }

    private static class LimitedInput extends FilterInputStream {
        private long left;
        LimitedInput(InputStream in,long max) {super(in);left=max;}
        @Override public int read() throws IOException {if(left<=0)return -1;int x=super.read();if(x!=-1)left--;return x;}
        @Override public int read(byte[] b,int off,int len) throws IOException {
            if(left<=0)return -1;
            int got=super.read(b,off,(int)Math.min(left,len));
            if(got>0)left-=got;
            return got;
        }
        @Override public long skip(long n) throws IOException {
            long got=super.skip(Math.min(n,left));left-=got;return got;
        }
    }

    private static void advance(InputStream stream,long count) throws IOException {
        while(count>0) {
            long n=stream.skip(count);
            if(n<=0) {if(stream.read()<0)throw new IOException("Unexpected EOF");n=1;}
            count-=n;
        }
    }

    private WebResourceResponse assetResponse(WebResourceRequest request) throws IOException {
        Uri url=request.getUrl();
        if(url==null || !"hunter.mobile.local".equals(url.getHost()))return null;
        String relative=url.getPath();
        if(relative==null || relative.equals("/"))relative="/index.html";
        while(relative.startsWith("/"))relative=relative.substring(1);
        if(relative.isEmpty() || relative.contains("..") || relative.indexOf('\\')>=0)return notFound();
        String file="game/"+relative;
        String type=mime(relative);
        Map<String,String> headers=new HashMap<>();
        headers.put("Access-Control-Allow-Origin","*");
        headers.put("Cache-Control","public, max-age=604800");
        headers.put("Accept-Ranges","bytes");
        if(type.startsWith("video/") || type.startsWith("audio/")) {
            try {
                AssetFileDescriptor fd=getAssets().openFd(file);
                long total=fd.getLength();
                String requestRange=request.getRequestHeaders().get("Range");
                if(requestRange!=null && requestRange.startsWith("bytes=")) {
                    String[] span=requestRange.substring(6).split("-",2);
                    long start=Long.parseLong(span[0]);
                    long end=span.length>1&&!span[1].isEmpty()?Long.parseLong(span[1]):total-1;
                    if(start>=total)return notFound();
                    end=Math.min(end,total-1);
                    if(end<start)return notFound();
                    InputStream stream=fd.createInputStream();
                    advance(stream,start);
                    long len=end-start+1;
                    headers.put("Content-Range","bytes "+start+"-"+end+"/"+total);
                    headers.put("Content-Length",Long.toString(len));
                    return new WebResourceResponse(type,null,206,"Partial Content",headers,new LimitedInput(stream,len));
                }
                fd.close();
                headers.put("Content-Length",Long.toString(total));
            } catch(Exception ex) {
                Log.w(TAG,"Media no random-access mode: "+file,ex);
            }
        }
        return new WebResourceResponse(type, "UTF-8",200,"OK",headers,
                getAssets().open(file,AssetManager.ACCESS_STREAMING));
    }

    @Override protected void onResume() {
        super.onResume();hideSystemBars();if(browser!=null)browser.onResume();
    }
    @Override protected void onPause() {
        if(browser!=null)browser.onPause();super.onPause();
    }
    @Override protected void onDestroy() {
        if(browser!=null) {browser.destroy();browser=null;}
        super.onDestroy();
    }
    @Override public void onBackPressed() {
        if(browser!=null)browser.evaluateJavascript(
            "if(window.Hunter){window.Hunter.stopAll()}else if(typeof pause==='function'){pause()}",
            null);
        else super.onBackPressed();
    }
}
