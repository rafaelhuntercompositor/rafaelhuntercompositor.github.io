package studio.hunter.lastdawn;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class MainActivity extends Activity {
    private static final String LOCAL = "https://hunter.mobile.local/index.html";
    private static final int BLACK=0xFF090B11, PANEL=0xFF191C26, RED=0xFFBA2D4A, LINE=0xFF55515E, IVORY=0xFFF2E8E4, SMOKE=0xFFB3A5AA;
    private WebView web;
    private TextView status;
    private int activeBits = 0;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Map<Integer,Long> keyTimes = new HashMap<>();

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        immersive();
        createInterface();
        web.loadUrl(LOCAL);
    }

    private int dp(float v) {return Math.round(getResources().getDisplayMetrics().density * v);}
    private void immersive() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
    private GradientDrawable grad(int a, int b, int radius, int stroke) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{a,b});
        d.setCornerRadius(dp(radius));
        if(stroke!=0) d.setStroke(dp(1.2f),stroke);
        return d;
    }
    private TextView label(String content,int size,int color,boolean strong) {
        TextView v=new TextView(this);
        v.setText(content);
        v.setTextSize(size);
        v.setTextColor(color);
        v.setGravity(Gravity.CENTER);
        v.setIncludeFontPadding(false);
        if(strong) v.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));
        else v.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));
        return v;
    }
    private LinearLayout column() {
        LinearLayout l=new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setGravity(Gravity.CENTER);
        l.setMotionEventSplittingEnabled(true);
        return l;
    }
    private void createInterface() {
        LinearLayout root=column();
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(5),dp(2),dp(5),dp(2));
        root.setBackground(grad(0xFF11111A,0xFF08090E,0,0));

        LinearLayout header=new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(9),0,dp(9),0);
        root.addView(header,new LinearLayout.LayoutParams(-1,dp(34)));
        TextView mark=label("H",19,IVORY,true);
        mark.setBackground(grad(RED,0xFF6C192D,6,0xFFE25B6D));
        header.addView(mark,new LinearLayout.LayoutParams(dp(28),dp(27)));
        TextView brand=label("  HUNTER GAME STUDIO",12,IVORY,true);
        brand.setGravity(Gravity.CENTER_VERTICAL);
        header.addView(brand,new LinearLayout.LayoutParams(0,-1,1));
        TextView gameTitle=label("LAST DAWN  •  HORROR EDITION",11,0xFFE7A6AD,true);
        header.addView(gameTitle,new LinearLayout.LayoutParams(0,-1,1));
        TextView quit=label("☰  MENU",11,IVORY,true);
        quit.setBackground(grad(0xFF34303B,0xFF26232C,6,LINE));
        header.addView(quit,new LinearLayout.LayoutParams(dp(88),dp(29)));
        quit.setOnClickListener(v -> confirmMenu());

        LinearLayout deck=new LinearLayout(this);
        deck.setGravity(Gravity.CENTER);
        deck.setMotionEventSplittingEnabled(true);
        root.addView(deck,new LinearLayout.LayoutParams(-1,0,1));

        int availableDp=getResources().getConfiguration().screenWidthDp;
        int side=availableDp>=820?178:availableDp>=650?155:129;

        LinearLayout left=makePanel();
        LinearLayout right=makePanel();
        deck.addView(left,new LinearLayout.LayoutParams(dp(side),-1));
        LinearLayout.LayoutParams panelRightParams=new LinearLayout.LayoutParams(dp(side),-1);
        deck.addView(right,panelRightParams);

        FrameLayout screen=new FrameLayout(this);
        screen.setPadding(dp(5),dp(5),dp(5),dp(5));
        screen.setBackground(grad(0xFF3E313E,0xFF211F2C,9,RED));
        LinearLayout.LayoutParams screenParams=new LinearLayout.LayoutParams(0,-1,1);
        screenParams.setMargins(dp(6),dp(3),dp(6),dp(3));
        deck.addView(screen,1,screenParams);

        FrameLayout viewport=new FrameLayout(this);
        viewport.setBackgroundColor(Color.BLACK);
        screen.addView(viewport,new FrameLayout.LayoutParams(-1,-1));

        web=new WebView(this);
        web.setBackgroundColor(Color.BLACK);
        web.setFocusable(true);
        web.setFocusableInTouchMode(true);
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setMediaPlaybackRequiresUserGesture(false);
        web.getSettings().setAllowFileAccess(false);
        web.getSettings().setAllowContentAccess(false);
        web.getSettings().setSupportZoom(false);
        web.getSettings().setBuiltInZoomControls(false);
        web.getSettings().setDisplayZoomControls(false);
        web.getSettings().setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient(){
            @Override public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request){
                android.net.Uri uri=request.getUrl();
                if(!"hunter.mobile.local".equals(uri.getHost())) return new WebResourceResponse("text/plain","UTF-8",new ByteArrayInputStream(new byte[0]));
                String name=uri.getPath();
                if(name==null || name.equals("/")) name="/index.html";
                if(name.contains("..")) return new WebResourceResponse("text/plain","UTF-8",new ByteArrayInputStream(new byte[0]));
                String mime=name.endsWith(".js")?"text/javascript":name.endsWith(".css")?"text/css":name.endsWith(".png")?"image/png":name.endsWith(".json")?"application/json":"text/html";
                try {
                    InputStream stream=getAssets().open("game"+name);
                    return new WebResourceResponse(mime,"UTF-8",stream);
                } catch(Exception e) {
                    return new WebResourceResponse("text/plain","UTF-8",new ByteArrayInputStream(new byte[0]));
                }
            }
            @Override public void onPageFinished(WebView view,String url){
                status.setText("JOGO INTEGRADO  •  FUNCIONAMENTO OFFLINE  •  TOQUE EM ▶ PARA INICIAR");
                view.requestFocus();
            }
        });
        viewport.addView(web,new FrameLayout.LayoutParams(-1,-1));

        left.addView(label("MOVIMENTO",11,SMOKE,true),new LinearLayout.LayoutParams(-1,dp(25)));
        createDpad(left,availableDp>=650?44:36);
        TextView dpadHint=label("DIRECIONAL",9,0xFF8E8997,false);
        left.addView(dpadHint,new LinearLayout.LayoutParams(-1,dp(26)));

        right.addView(label("COMANDOS",11,SMOKE,true),new LinearLayout.LayoutParams(-1,dp(25)));
        LinearLayout rowA=new LinearLayout(this);
        rowA.setGravity(Gravity.CENTER);
        rowA.setMotionEventSplittingEnabled(true);
        right.addView(rowA);
        int dia=availableDp>=650?59:49;
        rowA.addView(action("B","CORRER",KeyEvent.KEYCODE_Z,0xFF54416D,dia,0x10));
        rowA.addView(action("A","AGIR",KeyEvent.KEYCODE_X,RED,dia,0x20));
        LinearLayout rowB=new LinearLayout(this);
        rowB.setGravity(Gravity.CENTER);
        rowB.setMotionEventSplittingEnabled(true);
        right.addView(rowB);
        rowB.addView(action("M","MAPA",KeyEvent.KEYCODE_C,0xFF365166,dia-8,0));
        rowB.addView(action("Ⅱ","PAUSA",KeyEvent.KEYCODE_ENTER,0xFF343845,dia-8,0));

        LinearLayout foot=new LinearLayout(this);
        foot.setGravity(Gravity.CENTER_VERTICAL);
        root.addView(foot,new LinearLayout.LayoutParams(-1,dp(24)));
        status=label("INICIANDO SISTEMA...",9,0xFFB49FA5,true);
        foot.addView(status,new LinearLayout.LayoutParams(0,-1,1));
        TextView edition=label("HUNTER MOBILE  /  OFFLINE",8,0xFF8D5265,true);
        foot.addView(edition,new LinearLayout.LayoutParams(dp(172),-1));

        setContentView(root);
    }
    private LinearLayout makePanel(){
        LinearLayout p=column();
        p.setBackground(grad(0xFF24222F,0xFF11131A,12,0xFF51404B));
        p.setPadding(dp(1),dp(8),dp(1),dp(7));
        return p;
    }
    private void createDpad(LinearLayout left, int unit){
        LinearLayout a=column();
        left.addView(a);
        addDpadRow(a,unit,new String[]{"","▲",""},new int[]{0,KeyEvent.KEYCODE_DPAD_UP,0},new int[]{0,4,0});
        addDpadRow(a,unit,new String[]{"◀","◆","▶"},new int[]{KeyEvent.KEYCODE_DPAD_LEFT,0,KeyEvent.KEYCODE_DPAD_RIGHT},new int[]{1,0,2});
        addDpadRow(a,unit,new String[]{"","▼",""},new int[]{0,KeyEvent.KEYCODE_DPAD_DOWN,0},new int[]{0,8,0});
    }
    private void addDpadRow(LinearLayout parent,int unit,String[] sym,int[] key,int[] bits){
        LinearLayout row=new LinearLayout(this);
        row.setGravity(Gravity.CENTER);
        row.setMotionEventSplittingEnabled(true);
        parent.addView(row);
        for(int i=0;i<3;i++){
            TextView v=label(sym[i],sym[i].equals("◆")?14:17,sym[i].equals("◆")?0xFFA95964:IVORY,true);
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(dp(unit),dp(unit));
            p.setMargins(dp(1),dp(1),dp(1),dp(1));
            if(!sym[i].isEmpty()){
                v.setBackground(grad(0xFF474651,0xFF252832,7,0xFF6B6672));
                if(key[i]!=0) bindHold(v,key[i],bits[i]);
            }
            row.addView(v,p);
        }
    }
    private LinearLayout action(String symbol,String caption,int code,int color,int size,int bit){
        LinearLayout wrap=column();
        wrap.setPadding(dp(1),dp(1),dp(1),dp(1));
        TextView cap=label(symbol,symbol.length()>1?15:22,IVORY,true);
        cap.setBackground(grad(color,shade(color),64,0xFFA78B9C));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(dp(size),dp(size));
        p.setMargins(dp(2),dp(2),dp(2),dp(4));
        wrap.addView(cap,p);
        wrap.addView(label(caption,9,SMOKE,true),new LinearLayout.LayoutParams(-1,dp(15)));
        bindHold(cap,code,bit);
        return wrap;
    }
    private int shade(int c){
        return Color.rgb(Math.max(0,Color.red(c)*2/3),Math.max(0,Color.green(c)*2/3),Math.max(0,Color.blue(c)*2/3));
    }
    private void bindHold(View v,int code,int bit){
        v.setOnTouchListener((view,event)->{
            int what=event.getActionMasked();
            if(what==MotionEvent.ACTION_DOWN) {press(code,bit);view.setAlpha(.72f);return true;}
            if(what==MotionEvent.ACTION_UP || what==MotionEvent.ACTION_CANCEL) {release(code,bit);view.setAlpha(1);return true;}
            return true;
        });
    }
    private void dispatch(int action,int code,long since){
        if(web==null)return;
        KeyEvent key=new KeyEvent(since,SystemClock.uptimeMillis(),action,code,0,0,KeyCharacterMap.VIRTUAL_KEYBOARD,0,0,InputDevice.SOURCE_KEYBOARD);
        web.dispatchKeyEvent(key);
    }
    private void syncBits(){
        if(web!=null) web.evaluateJavascript("if(window.pico8_buttons){pico8_buttons[0]="+activeBits+";}",null);
    }
    private void press(int code,int bit){
        if(!pressedKeys.add(code))return;
        web.requestFocus();
        long time=SystemClock.uptimeMillis();
        keyTimes.put(code,time);
        dispatch(KeyEvent.ACTION_DOWN,code,time);
        if(bit!=0){activeBits |= bit;syncBits();}
    }
    private void release(int code,int bit){
        if(!pressedKeys.remove(code))return;
        Long time=keyTimes.remove(code);
        dispatch(KeyEvent.ACTION_UP,code,time==null?SystemClock.uptimeMillis():time);
        if(bit!=0){activeBits &= ~bit;syncBits();}
    }
    private void releaseAll(){
        for(Integer key:new HashSet<>(pressedKeys)){release(key,bitFor(key));}
    }
    private int bitFor(int code){
        switch(code){
            case KeyEvent.KEYCODE_DPAD_LEFT:return 1;
            case KeyEvent.KEYCODE_DPAD_RIGHT:return 2;
            case KeyEvent.KEYCODE_DPAD_UP:return 4;
            case KeyEvent.KEYCODE_DPAD_DOWN:return 8;
            case KeyEvent.KEYCODE_Z:return 16;
            case KeyEvent.KEYCODE_X:return 32;
            default:return 0;
        }
    }
    private void confirmMenu(){
        releaseAll();
        new AlertDialog.Builder(this).setTitle("LAST DAWN").setMessage("Deseja reiniciar a partida ou sair?")
            .setPositiveButton("Reiniciar",(d,w)->web.reload())
            .setNegativeButton("Continuar",(d,w)->immersive())
            .setNeutralButton("Sair",(d,w)->finish()).show();
    }
    @Override public void onBackPressed(){confirmMenu();}
    @Override protected void onPause(){releaseAll();if(web!=null)web.onPause();super.onPause();}
    @Override protected void onResume(){super.onResume();immersive();if(web!=null)web.onResume();}
    @Override protected void onDestroy(){releaseAll();if(web!=null)web.destroy();super.onDestroy();}
}
