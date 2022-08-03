package learn.zero.say.whatsweb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.core.view.InputDeviceCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import learn.zero.say.whatsweb.Activity.PrivacyPolicy;


public class WhatsWeb extends AppCompatActivity {

    //Dark and Day Mode
    SharedPreferences sharedPreferences;
    boolean isDarkModeOn;
    SharedPreferences.Editor editor;


    //WebView
    private static ValueCallback<Uri[]> mUploadMessageArr;
    String tag = WhatsWeb.class.getSimpleName();
    private ImageView ivRefresh;
    final Activity mActivity = this;
    WebView webView;


    @RequiresApi(api = 17)
    @SuppressLint({"SetJavaScriptEnabled", "WrongConstant"})
    public void onCreate(Bundle bundle) {
//        Utils.loadLocale(this);
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        //Navigation Drawer
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        // Toolbar view
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {

                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id)
                {

                    case R.id.navMessage:
                        Toast.makeText(WhatsWeb.this, "Send Message", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.navShare:
                        String appUrl = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
                        Intent sharing = new Intent(Intent.ACTION_SEND);
                        sharing.setType("text/plain");
                        sharing.putExtra(Intent.EXTRA_SUBJECT, "Download Now");
                        sharing.putExtra(Intent.EXTRA_TEXT, appUrl);
                        startActivity(Intent.createChooser(sharing, "Share via"));
                        break;

                    case R.id.navContact:
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        // only email apps should handle this
                        String[] to = {"thanksforcontactus@gmail.com"};
                        intent.putExtra(Intent.EXTRA_EMAIL, to);
                        startActivity(Intent.createChooser(intent, "Contact us!"));
                        break;

                    case R.id.navPrivacy:
                        startActivity(new Intent(getApplicationContext(), PrivacyPolicy.class));
                        return true;


                    default:
                        return true;
                }
                return true;
            }
        });

        //Web
        getWindow().getDecorView().setSystemUiVisibility(InputDeviceCompat.SOURCE_TOUCHSCREEN);
        //Refresh
        this.ivRefresh = (ImageView) findViewById(R.id.iv_refresh);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText("WhatsApp Web");
        this.ivRefresh.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.READ_PHONE_STATE",
                            "android.permission.ACCESS_COARSE_LOCATION"},
                    123);
        }
        //webView
        this.webView = (WebView) findViewById(R.id.WebView);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.webView.getSettings().setBuiltInZoomControls(true);
        this.webView.getSettings().setDisplayZoomControls(false);
        this.webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; U; " +
                "Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/44.0");
        this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.setWebViewClient(new MyWebViewClient());
        this.webView.getSettings().setSaveFormData(true);
        this.webView.getSettings().setLoadsImagesAutomatically(true);
        this.webView.getSettings().setUseWideViewPort(true);
        this.webView.getSettings().setAllowFileAccessFromFileURLs(true);
        this.webView.getSettings().setBlockNetworkImage(false);
        this.webView.getSettings().setBlockNetworkLoads(false);
        this.webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        this.webView.getSettings().setSupportMultipleWindows(true);
        this.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.webView.getSettings().setLoadWithOverviewMode(true);
        this.webView.getSettings().setNeedInitialFocus(false);
        this.webView.getSettings().setAppCacheEnabled(true);
        this.webView.getSettings().setDatabaseEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.webView.getSettings().setGeolocationEnabled(true);
        this.webView.getSettings().setCacheMode(2);
        this.webView.setScrollBarStyle(0);
        this.webView.getSettings().setUserAgentString("Mozilla/5.0 " +
                "(Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/78.0.3904.96 Safari/537.36");
        final StringBuilder sb = new StringBuilder();
        sb.append("https://web.whatsapp.com/🌐/");
        sb.append(Locale.getDefault().getLanguage());
        this.webView.loadUrl(sb.toString());
        this.webView.setWebChromeClient(new chromeView());

        //Refresh
        this.ivRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                WhatsWeb.this.getWindow().getDecorView().setSystemUiVisibility
                        (InputDeviceCompat.SOURCE_TOUCHSCREEN);

                WhatsWeb.this.webView.loadUrl(sb.toString());
                WhatsWeb.this.webView.setWebChromeClient(new chromeView());

            }
        });
    }

    public void addcss() {
        try {
            InputStream open = getAssets().open("s.css");
            byte[] bArr = new byte[open.available()];
            open.read(bArr);
            open.close();
            String encodeToString = Base64.encodeToString(bArr, 2);
            WebView webView2 = this.webView;
            webView2.loadUrl("javascript:(function() {var parent " +
                    "= document.getElementsByTagName('head').item(0);var style" +
                    " = document.createElement('style');style.type " +
                    "= 'text/css';style.innerHTML = window.atob('" + encodeToString + "');" +
                    "parent.appendChild(style)})();", (Map) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView webView, String str) {
            //add
        }

        @Override
        public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
            //add
        }

        private MyWebViewClient() {
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            String str2 = WhatsWeb.this.tag;
            Log.e(str2, "shouldOverrideUrlLoading: " + str);
            if (Uri.parse(str).getHost().contains(".whatsapp.com")) {
                return true;
            }
            WhatsWeb.this.startActivity(new Intent("android.intent.action.VIEW",
                    Uri.parse(str)));
            return true;
        }
    }

    public class chromeView extends WebChromeClient {

        @SuppressLint({"NewApi"})
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback,
                                         FileChooserParams fileChooserParams) {
            return WhatsWeb.this.startFileChooserIntent(valueCallback,
                    fileChooserParams.createIntent());
        }

        @RequiresApi(api = 21)
        @Override
        public void onPermissionRequest(PermissionRequest permissionRequest) {
            permissionRequest.grant(permissionRequest.getResources());
        }

        @Override
        public void onProgressChanged(WebView webView, int i) {
            WhatsWeb.this.mActivity.setTitle("  Loading ...");
            WhatsWeb.this.mActivity.setProgress(i * 100);
            if (i == 100) {
                WhatsWeb.this.mActivity.setTitle("Whatsweb");
            }
            WhatsWeb.this.addcss();
        }
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4 || !this.webView.canGoBack()) {
            return super.onKeyDown(i, keyEvent);
        }
        this.webView.goBack();
        return true;
    }

    @SuppressLint({"NewApi"})
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint({"NewApi", "RestrictedApi"})
    public boolean startFileChooserIntent(ValueCallback<Uri[]> valueCallback, Intent intent) {
        ValueCallback<Uri[]> valueCallback2 = mUploadMessageArr;
        if (valueCallback2 != null) {
            valueCallback2.onReceiveValue(null);
            mUploadMessageArr = null;
        }
        mUploadMessageArr = valueCallback;
        try {
            startActivityForResult(intent, 1001, new Bundle());
            return true;
        } catch (Throwable th) {
            th.printStackTrace();
            ValueCallback<Uri[]> valueCallback3 = mUploadMessageArr;
            if (valueCallback3 != null) {
                valueCallback3.onReceiveValue(null);
                mUploadMessageArr = null;
            }
            return Boolean.parseBoolean((String) null);
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1001 && Build.VERSION.SDK_INT >= 21) {
            mUploadMessageArr.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(i2, intent));
            mUploadMessageArr = null;
        }
    }


    class Abc implements MediaScannerConnection.OnScanCompletedListener {
        @Override
        public void onScanCompleted(String str, Uri uri) {
            //add
        }
    }

    @Override
    public void onResume() {
        getWindow().getDecorView().setSystemUiVisibility(InputDeviceCompat.SOURCE_TOUCHSCREEN);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.webView.clearCache(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.webView.clearCache(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        this.webView.clearCache(true);
        super.onStop();
    }

    //Toolbar MenuItem
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_menu, menu);

        //Day And Night Mode
        sharedPreferences
                = getSharedPreferences(
                "sharedPrefs", MODE_PRIVATE);
        editor
                = sharedPreferences.edit();
        isDarkModeOn
                = sharedPreferences
                .getBoolean(
                        "isDarkModeOn", false);

        final MenuItem nightMode = menu.findItem(R.id.night_mode);
        final MenuItem dayMode = menu.findItem(R.id.day_mode);

        if (isDarkModeOn) {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);
            dayMode.setVisible(true);
            nightMode.setVisible(false);
        } else {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);
            dayMode.setVisible(false);
            nightMode.setVisible(true);

        }

        nightMode.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AppCompatDelegate
                        .setDefaultNightMode(
                                AppCompatDelegate
                                        .MODE_NIGHT_YES);

                editor.putBoolean(
                        "isDarkModeOn", true);
                editor.apply();
                Toast.makeText(getApplicationContext(), "Dark Mode On ", Toast.LENGTH_SHORT).show();
                dayMode.setVisible(true);
                nightMode.setVisible(false);
                return true;
            }
        });

        dayMode.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AppCompatDelegate
                        .setDefaultNightMode(
                                AppCompatDelegate
                                        .MODE_NIGHT_NO);
                editor.putBoolean(
                        "isDarkModeOn", false);
                editor.apply();
                Toast.makeText(getApplicationContext(), "Dark Mode Off", Toast.LENGTH_SHORT).show();
                dayMode.setVisible(false);
                nightMode.setVisible(true);
                return true;

            }
        });
        return true;
    }
}
