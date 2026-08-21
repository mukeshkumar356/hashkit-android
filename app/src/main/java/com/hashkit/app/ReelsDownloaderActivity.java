package com.hashkit.app;

import android.app.DownloadManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class ReelsDownloaderActivity extends AppCompatActivity {

    private EditText etUrl;
    private TextView btnFetch, btnDownload, tvStatus;
    private View layoutStatus, loader;
    private WebView webView;
    private String videoUrl = null;
    private volatile boolean videoFound = false;
    private Runnable timeoutRunnable;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final String CHROME_UA =
        "Mozilla/5.0 (Linux; Android 13; Pixel 7) "
        + "AppleWebKit/537.36 (KHTML, like Gecko) "
        + "Chrome/120.0.0.0 Mobile Safari/537.36";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reels_downloader);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Reels Downloader");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etUrl        = findViewById(R.id.et_url);
        btnFetch     = findViewById(R.id.btn_fetch);
        btnDownload  = findViewById(R.id.btn_download);
        tvStatus     = findViewById(R.id.tv_status);
        layoutStatus = findViewById(R.id.layout_status);
        loader       = findViewById(R.id.loader);

        webView = findViewById(R.id.hidden_webview);
        setupWebView();

        btnFetch.setOnClickListener(v -> fetchVideoUrl());
        btnDownload.setOnClickListener(v -> downloadVideo());

        findViewById(R.id.btn_paste).setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (cm != null && cm.hasPrimaryClip() && cm.getPrimaryClip() != null) {
                CharSequence text = cm.getPrimaryClip().getItemAt(0).getText();
                if (text != null) etUrl.setText(text.toString().trim());
            }
        });
    }

    // ─── Fetch flow ───────────────────────────────────────────────────────────

    private void fetchVideoUrl() {
        String raw = etUrl.getText().toString().trim();
        if (raw.isEmpty()) { showStatus("Please paste an Instagram Reel link first.", false); return; }

        String shortcode = extractShortcode(raw);
        if (shortcode == null) { showStatus("❌ Invalid link. Paste a valid Instagram Reel URL.", false); return; }

        loader.setVisibility(View.VISIBLE);
        btnFetch.setEnabled(false);
        btnDownload.setVisibility(View.GONE);
        videoUrl   = null;
        videoFound = false;
        showStatus("🔍 Fetching reel...", false);

        if (timeoutRunnable != null) mainHandler.removeCallbacks(timeoutRunnable);
        timeoutRunnable = () -> {
            if (!videoFound) {
                if (webView != null) webView.setVisibility(View.GONE);
                loader.setVisibility(View.GONE);
                btnFetch.setEnabled(true);
                showStatus("❌ Could not fetch reel.\n\nMake sure the link is correct and the post is public.", false);
            }
        };
        mainHandler.postDelayed(timeoutRunnable, 25000);

        final String sc = shortcode;
        new Thread(() -> tryAllMethods(sc)).start();
    }

    // ─── Method cascade ───────────────────────────────────────────────────────

    private void tryAllMethods(String shortcode) {
        // Method 1: cobalt.tools API (public, no login needed)
        if (!videoFound) {
            String found = tryCobaltApi(
                "https://www.instagram.com/reel/" + shortcode + "/");
            if (found != null && !videoFound) {
                videoFound = true;
                mainHandler.post(() -> onVideoFoundCallback(found));
                return;
            }
        }

        // Method 2: WebView fallback (loads the public post page directly)
        if (!videoFound) {
            mainHandler.post(() -> startWebViewFetch(shortcode));
        }
    }

    // ─── Method 1: cobalt.tools API ───────────────────────────────────────────

    private String tryCobaltApi(String igUrl) {
        try {
            HttpURLConnection conn = (HttpURLConnection)
                new URL("https://api.cobalt.tools/").openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(12000);
            conn.setReadTimeout(15000);

            String body = "{\"url\":\"" + igUrl.replace("\"", "\\\"") + "\"}";
            conn.getOutputStream().write(body.getBytes("UTF-8"));

            int code = conn.getResponseCode();
            if (code != 200) return null;

            String resp = readStream(conn.getInputStream(), conn.getContentEncoding());
            if (resp == null || resp.contains("\"status\":\"error\"")) return null;

            Matcher m = Pattern.compile("\"url\":\"([^\"]+)\"").matcher(resp);
            if (m.find()) {
                return m.group(1).replace("\\/", "/").replace("\\u0026", "&");
            }
        } catch (Exception ignored) {}
        return null;
    }

    // ─── Method 3: WebView fallback ───────────────────────────────────────────

    private void startWebViewFetch(String shortcode) {
        webView.setVisibility(View.VISIBLE);
        webView.clearCache(true);

        String[] urls = {
            "https://www.instagram.com/reel/" + shortcode + "/",
            "https://www.instagram.com/p/"    + shortcode + "/",
            "https://www.instagram.com/reel/" + shortcode + "/embed/",
        };

        webView.loadUrl(urls[0]);
        mainHandler.postDelayed(() -> { if (!videoFound) webView.loadUrl(urls[1]); }, 5000);
        mainHandler.postDelayed(() -> { if (!videoFound) webView.loadUrl(urls[2]); }, 10000);
    }

    private void setupWebView() {
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setDatabaseEnabled(true);
        ws.setMediaPlaybackRequiresUserGesture(false);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        ws.setUserAgentString(CHROME_UA);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void onVideoUrl(String url) {
                if (!videoFound && isVideoUrl(url)) {
                    videoFound = true;
                    mainHandler.post(() -> onVideoFoundCallback(url));
                }
            }
        }, "HashKit");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                view.evaluateJavascript(
                    "(function(){function r(u){if(u&&u.indexOf('http')===0&&window.HashKit)"
                    + "{try{window.HashKit.onVideoUrl(u);}catch(e){}}}"
                    + "var ox=XMLHttpRequest.prototype.send;"
                    + "XMLHttpRequest.prototype.send=function(){"
                    + "this.addEventListener('load',function(){try{var t=this.responseText;"
                    + "var m=t.match(/\"video_url\":\"([^\"]+)\"/);"
                    + "if(m)r(m[1].replace(/\\\\u0026/g,'&').replace(/\\\\\\/g,'/'));}"
                    + "catch(e){}});ox.apply(this,arguments);};"
                    + "var of=window.fetch;if(of){window.fetch=function(){"
                    + "return of.apply(this,arguments).then(function(rsp){"
                    + "rsp.clone().text().then(function(t){try{"
                    + "var m=t.match(/\"video_url\":\"([^\"]+)\"/);"
                    + "if(m)r(m[1].replace(/\\\\u0026/g,'&').replace(/\\\\\\/g,'/'));"
                    + "}catch(e){}}).catch(function(){});return rsp;});};}})();", null);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest req) {
                if (!videoFound) {
                    String url = req.getUrl().toString();
                    if (isVideoUrl(url)) {
                        videoFound = true;
                        mainHandler.post(() -> onVideoFoundCallback(url));
                    }
                }
                return null;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!videoFound) {
                    view.evaluateJavascript(
                        "(function(){function r(u){if(u&&u.indexOf('http')===0&&window.HashKit)"
                        + "{try{window.HashKit.onVideoUrl(u);}catch(e){}}}"
                        + "document.querySelectorAll('video').forEach(function(v){"
                        + "if(v.src)r(v.src);});"
                        + "document.querySelectorAll('script').forEach(function(s){"
                        + "var t=s.textContent||'';"
                        + "var m=t.match(/\"video_url\":\"([^\"]+)\"/);"
                        + "if(m)r(m[1].replace(/\\\\u0026/g,'&').replace(/\\\\\\/g,'/'));"
                        + "});})();", null);
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient());
    }

    // ─── HTML parse ───────────────────────────────────────────────────────────

    // ─── Shared helpers ───────────────────────────────────────────────────────

    private void onVideoFoundCallback(String url) {
        if (timeoutRunnable != null) mainHandler.removeCallbacks(timeoutRunnable);
        videoUrl = url;
        if (webView != null) { webView.stopLoading(); webView.setVisibility(View.GONE); }
        loader.setVisibility(View.GONE);
        btnFetch.setEnabled(true);
        showStatus("✅ Reel found! Tap the button below to download.", true);
    }

    private boolean isVideoUrl(String url) {
        if (url == null || url.isEmpty()) return false;
        boolean isCDN = url.contains("cdninstagram.com") || url.contains("fbcdn.net");
        if (!isCDN) return false;
        if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")
                || url.contains(".gif") || url.contains(".webp")) return false;
        return url.contains(".mp4") || url.contains("video_dashinit")
            || url.contains("t50.2886-16") || url.contains("v/t50") || url.contains("efg=");
    }

    private String extractShortcode(String url) {
        Matcher m = Pattern.compile("instagram\\.com/(reel|p|tv)/([A-Za-z0-9_\\-]+)").matcher(url);
        return m.find() ? m.group(2) : null;
    }

    private String readStream(InputStream is, String encoding) {
        try {
            if ("gzip".equalsIgnoreCase(encoding)) is = new GZIPInputStream(is);
            BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
            r.close();
            return sb.toString();
        } catch (Exception e) { return null; }
    }

    private void showStatus(String msg, boolean showDownload) {
        layoutStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(msg);
        btnDownload.setVisibility(showDownload ? View.VISIBLE : View.GONE);
    }

    // ─── Download ─────────────────────────────────────────────────────────────

    private void downloadVideo() {
        if (videoUrl == null) return;
        try {
            DownloadManager.Request req = new DownloadManager.Request(Uri.parse(videoUrl));
            req.setTitle("Instagram Reel — HashKit");
            req.setDescription("Downloading reel...");
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            req.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_MOVIES,
                "HashKit_Reel_" + System.currentTimeMillis() + ".mp4");
            req.setMimeType("video/mp4");

            String cookies = CookieManager.getInstance().getCookie(videoUrl);
            if (cookies != null && !cookies.isEmpty()) req.addRequestHeader("Cookie", cookies);
            req.addRequestHeader("Referer",    "https://www.instagram.com/");
            req.addRequestHeader("User-Agent", CHROME_UA);
            req.allowScanningByMediaScanner();

            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            long dlId = dm.enqueue(req);
            getSharedPreferences("hashkit_downloads", MODE_PRIVATE)
                .edit().putBoolean("dl_" + dlId, true).apply();

            showStatus("⬇️ Download started!\nNotification aayegi jab complete ho — tap karke video dekho 🎬", false);
            Toast.makeText(this, "✅ Reel downloading...", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Download failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeoutRunnable != null) mainHandler.removeCallbacks(timeoutRunnable);
        if (webView != null) { webView.stopLoading(); webView.destroy(); }
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
