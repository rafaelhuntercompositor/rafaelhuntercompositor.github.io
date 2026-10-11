package studio.hunter.lastdawn;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
public final class MainActivity extends Activity {
 private WebView web;
 private void immersive() { getWindow().getDecorView().setSystemUiVisibility(5894|1024|512|4096); }
 @Override public void onCreate(Bundle state) {
  super.onCreate(state);
  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
  getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  getWindow().setNavigationBarColor(Color.BLACK);
  getWindow().setStatusBarColor(Color.BLACK);
  immersive();
  web = new WebView(this);
  web.setBackgroundColor(Color.BLACK);
  web.setOverScrollMode(View.OVER_SCROLL_NEVER);
  web.setHorizontalScrollBarEnabled(false);
  web.setVerticalScrollBarEnabled(false);
  web.setLongClickable(false);
  WebSettings s=web.getSettings();
  s.setJavaScriptEnabled(true);
  s.setDomStorageEnabled(true);
  s.setAllowFileAccess(false);
  s.setAllowContentAccess(false);
  s.setMediaPlaybackRequiresUserGesture(false);
  s.setSupportZoom(false);
  s.setBuiltInZoomControls(false);
  s.setDisplayZoomControls(false);
  s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
  web.setWebChromeClient(new WebChromeClient());
  web.setWebViewClient(new WebViewClient(){
   @Override public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request){
    String host=request.getUrl().getHost();
    String file=request.getUrl().getPath();
    if(!"hunter.mobile.local".equals(host) || file==null || file.contains(".."))
     return new WebResourceResponse("text/plain","UTF-8",new ByteArrayInputStream(new byte[0]));
    if(file.equals("/"))file="/index.html";
    String mime=file.endsWith(".js")?"application/javascript":file.endsWith(".webp")?"image/webp":file.endsWith(".png")?"image/png":file.endsWith(".css")?"text/css":"text/html";
    try{InputStream in=getAssets().open("game"+file);return new WebResourceResponse(mime,"UTF-8",in);}
    catch(Exception ignored){return new WebResourceResponse("text/plain","UTF-8",new ByteArrayInputStream(new byte[0]));}
   }
  });
  setContentView(web);
  web.loadUrl("https://hunter.mobile.local/index.html");
 }
 @Override public void onBackPressed(){
  if(web!=null)web.evaluateJavascript("window.Hunter&&window.Hunter.stopAll()",null);
  new AlertDialog.Builder(this).setTitle("LAST DAWN").setMessage("O que deseja fazer?")
   .setPositiveButton("Continuar",(d,w)->immersive())
   .setNeutralButton("Reiniciar",(d,w)->{web.evaluateJavascript("window.Hunter.restart()",null);immersive();})
   .setNegativeButton("Sair",(d,w)->finish()).show();
 }
 @Override protected void onPause(){if(web!=null){web.evaluateJavascript("window.Hunter&&window.Hunter.stopAll()",null);web.onPause();}super.onPause();}
 @Override protected void onResume(){super.onResume();immersive();if(web!=null)web.onResume();}
 @Override protected void onDestroy(){if(web!=null){web.destroy();web=null;}super.onDestroy();}
}