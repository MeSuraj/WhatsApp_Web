
package learn.zero.say.whatsweb.Activity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import learn.zero.say.whatsweb.R;


public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_policy_activity);


        WebView view =(WebView)findViewById(R.id.webview);
        WebSettings settings = view.getSettings();
        settings.setJavaScriptEnabled(true);
        view.loadUrl("file:///android_asset/privacy.html");



    }
}