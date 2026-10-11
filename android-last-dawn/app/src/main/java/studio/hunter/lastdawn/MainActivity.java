package studio.hunter.lastdawn;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class MainActivity extends Activity {
    private static final String URL = "https://html-classic.itch.zone/html/19224812/index.html?v=1789319020";
    private static final int BG = 0xFF0C0D15, PANEL = 0xFF1C1C29, EDGE = 0xFF4C435B, WHITE = 0xFFF2E9F0, SUB = 0xFFC0ABB9;
    private WebView web;
    private TextView info;
    private final Set<Integer> keys = new HashSet<>();
    private final Map<Integer, Long> downTimes = new HashMap<>();

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        immersive();
        interfaceUi();
        web.loadUrl(URL);
    }

    private int dp(float n) { return Math.round(n * getResources().getDisplayMetrics().density); }
    private void immersive() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
    private GradientDrawable fill(int c, int r, int border) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(c);
        d.setCornerRadius(dp(r));
        if (border != 0) d.setStroke(dp(1), border);
        return d;
    }
    private TextView text(String s, int size, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(s);
        v.setTextColor(color);
        v.setTextSize(size);
        v.setGravity(Gravity.CENTER);
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return v;
    }
    private LinearLayout column() {
        LinearLayout c = new LinearLayout(this);
        c.setGravity(Gravity.CENTER);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setBackground(fill(PANEL, 12, 0));
        c.setMotionEventSplittingEnabled(true);
        return c;
    }
    private void interfaceUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setMotionEventSplittingEnabled(true);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView brand = text("HUNTER GAME STUDIO", 12, WHITE, true);
        top.addView(brand, new LinearLayout.LayoutParams(0, dp(28), 1));
        TextView title = text("LAST DAWN  /  MOBILE", 10, SUB, true);
        top.addView(title, new LinearLayout.LayoutParams(0, dp(28), 1));
        root.addView(top);

        LinearLayout deck = new LinearLayout(this);
        deck.setGravity(Gravity.CENTER);
        deck.setMotionEventSplittingEnabled(true);
        root.addView(deck, new LinearLayout.LayoutParams(-1, 0, 1));

        int wide = getResources().getConfiguration().screenWidthDp;
        int side = wide > 850 ? 168 : (wide > 650 ? 145 : 127);
        LinearLayout left = column(), right = column();
        deck.addView(left, new LinearLayout.LayoutParams(dp(side), -1));

        FrameLayout screen = new FrameLayout(this);
        screen.setBackground(fill(Color.BLACK, 8, EDGE));
        LinearLayout.LayoutParams scp = new LinearLayout.LayoutParams(0, -1, 1);
        scp.setMargins(dp(4), dp(4), dp(4), dp(4));
        deck.addView(screen, scp);
        deck.addView(right, new LinearLayout.LayoutParams(dp(side), -1));

        web = new WebView(this);
        web.setBackgroundColor(Color.BLACK);
        web.setFocusable(true);
        web.setFocusableInTouchMode(true);
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setMediaPlaybackRequiresUserGesture(false);
        web.getSettings().setSupportZoom(false);
        web.getSettings().setAllowFileAccess(false);
        web.getSettings().setAllowContentAccess(false);
        web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView v, String url) {
                info.setText("TOQUE NO JOGO PARA INICIAR  ·  INTERNET NECESSÁRIA");
                v.requestFocus();
            }
            @Override public void onReceivedError(WebView v, WebResourceRequest r, WebResourceError e) {
                if (r.isForMainFrame()) info.setText("FALHA DE CONEXÃO  ·  USE VOLTAR PARA RECARREGAR");
            }
            @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                Uri u = r.getUrl();
                String host = u.getHost();
                if (host != null && (host.equals("html-classic.itch.zone") || host.endsWith(".itch.zone"))) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (Exception ignored) {}
                return true;
            }
        });
        screen.addView(web, new FrameLayout.LayoutParams(-1, -1));

        left.addView(text("MOVIMENTO", 10, SUB, true));
        GridLayout arrows = new GridLayout(this);
        arrows.setColumnCount(3);
        arrows.setRowCount(3);
        arrows.setMotionEventSplittingEnabled(true);
        left.addView(arrows);
        addArrow(arrows, "", 0);
        addArrow(arrows, "▲", KeyEvent.KEYCODE_DPAD_UP);
        addArrow(arrows, "", 0);
        addArrow(arrows, "◀", KeyEvent.KEYCODE_DPAD_LEFT);
        addArrow(arrows, "", 0);
        addArrow(arrows, "▶", KeyEvent.KEYCODE_DPAD_RIGHT);
        addArrow(arrows, "", 0);
        addArrow(arrows, "▼", KeyEvent.KEYCODE_DPAD_DOWN);
        addArrow(arrows, "", 0);

        right.addView(text("AÇÕES", 10, SUB, true));
        LinearLayout actions1 = new LinearLayout(this);
        actions1.setGravity(Gravity.CENTER);
        right.addView(actions1);
        actions1.addView(action("B", "CORRER", KeyEvent.KEYCODE_Z, 0xFF794B8D));
        actions1.addView(action("A", "AGIR", KeyEvent.KEYCODE_X, 0xFFC0475F));
        LinearLayout actions2 = new LinearLayout(this);
        actions2.setGravity(Gravity.CENTER);
        right.addView(actions2);
        actions2.addView(action("C", "MAPA", KeyEvent.KEYCODE_C, 0xFF4C5B76));
        actions2.addView(action("II", "PAUSA", KeyEvent.KEYCODE_ENTER, 0xFF4C5B76));

        info = text("CARREGANDO...", 9, SUB, false);
        root.addView(info, new LinearLayout.LayoutParams(-1, dp(22)));
        setContentView(root);
    }

    private void addArrow(GridLayout g, String txt, int key) {
        TextView v = text(txt, 20, WHITE, true);
        int side = getResources().getConfiguration().screenWidthDp > 650 ? 44 : 38;
        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = dp(side);
        p.height = dp(side);
        p.setMargins(dp(1), dp(1), dp(1), dp(1));
        if (key != 0) {
            v.setBackground(fill(0xFF3C3D52, 7, EDGE));
            hold(v, key);
        }
        g.addView(v, p);
    }
    private LinearLayout action(String symbol, String description, int key, int color) {
        LinearLayout wrap = new LinearLayout(this);
        wrap.setOrientation(LinearLayout.VERTICAL);
        wrap.setGravity(Gravity.CENTER);
        int dimension = getResources().getConfiguration().screenWidthDp > 650 ? 55 : 48;
        TextView v = text(symbol, symbol.length() > 1 ? 12 : 23, WHITE, true);
        v.setBackground(fill(color, 40, 0xFF9C8DA0));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dp(dimension), dp(dimension));
        p.setMargins(dp(2), dp(3), dp(2), dp(1));
        wrap.addView(v, p);
        wrap.addView(text(description, 8, SUB, true));
        hold(v, key);
        return wrap;
    }
    private void hold(View v, int code) {
        v.setOnTouchListener((view, event) -> {
            int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                press(code);
                view.setAlpha(0.68f);
                return true;
            }
            if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
                release(code);
                view.setAlpha(1f);
                return true;
            }
            return true;
        });
    }
    private void send(int act, int code, long origin) {
        KeyEvent key = new KeyEvent(origin, SystemClock.uptimeMillis(), act, code, 0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0, InputDevice.SOURCE_KEYBOARD);
        web.dispatchKeyEvent(key);
    }
    private void press(int code) {
        if (!keys.add(code)) return;
        web.requestFocus();
        long now = SystemClock.uptimeMillis();
        downTimes.put(code, now);
        send(KeyEvent.ACTION_DOWN, code, now);
    }
    private void release(int code) {
        if (!keys.remove(code)) return;
        Long down = downTimes.remove(code);
        send(KeyEvent.ACTION_UP, code, down == null ? SystemClock.uptimeMillis() : down);
    }
    private void releaseAll() {
        for (Integer code : new HashSet<>(keys)) release(code);
    }
    @Override public void onBackPressed() {
        releaseAll();
        new AlertDialog.Builder(this).setTitle("Last Dawn").setMessage("O que deseja fazer?")
        .setPositiveButton("Recarregar", (d, w) -> { web.reload(); })
        .setNegativeButton("Continuar", (d, w) -> { })
        .setNeutralButton("Sair", (d, w) -> finish()).show();
    }
    @Override protected void onPause() { releaseAll(); web.onPause(); super.onPause(); }
    @Override protected void onResume() { super.onResume(); immersive(); if (web != null) web.onResume(); }
    @Override protected void onDestroy() { releaseAll(); web.destroy(); super.onDestroy(); }
}
