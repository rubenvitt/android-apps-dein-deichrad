package de.dein_deichrad.apps.android.deindeichrad;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    MainActivity context;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    webView.loadUrl("https://dein-deichrad.de/");
                    return true;
                case R.id.navigation_dashboard:
                    webView.loadUrl("https://dein-deichrad.de/?page_id=25");
                    return true;
                case R.id.navigation_notifications:
                    webView.loadUrl("https://dein-deichrad.de/?page_id=139");
                    return true;
                case R.id.navigation_myBookings:
                    webView.loadUrl("https://dein-deichrad.de/?page_id=24");
                    return true;
            }
            return false;
        }

    };

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (isOnline()) {
                    if (request.getUrl().toString().contains("https://dein-deichrad.de"))
                        view.loadUrl(request.getUrl().toString());
                    else
                        startActivity(new Intent(Intent.ACTION_VIEW, request.getUrl()));
                } else {
                    internetAlert(false);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                context.setTitle(view.getTitle());
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (isOnline())
            webView.loadUrl("https://dein-deichrad.de");
        else
            internetAlert(true);
    }


    private void internetAlert(final boolean closeApp) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.no_internet);
        builder.setTitle(R.string.no_internetTitle);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (closeApp) {
                    if (!isOnline())
                        finish();
                    else
                        webView.loadUrl("https://dein-deichrad.de");
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if (webView.canGoBack())
            webView.goBack();
        else
            super.onBackPressed();
    }
}
