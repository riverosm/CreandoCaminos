package com.grupo207.creandocaminos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String APP_PREFERENCES = "MyPrefsFile";

    private String myHost = "creandocaminos.micerino.urltemporal.com";
    private String myURL = "https://" + myHost;

    private ProgressBar spinner;
    private ImageView imageHome;
    private TextView textHeader;
    private TextView textLoading;


    String ShowOrHideWebViewInitialUse = "show";

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(this.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;

        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            // Toast.makeText(this, "Hay internet " + networkInfo.getTypeName(), Toast.LENGTH_SHORT).show();
            isConnected = true;
        } else {
            // Toast.makeText(this, "No hay internet", Toast.LENGTH_SHORT).show();
        }

        return isConnected;
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals(myHost)) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageStarted(WebView webview, String url, Bitmap favicon) {
            // only make it invisible the FIRST time the app is run
            if (ShowOrHideWebViewInitialUse.equals("show")) {
                webview.setVisibility(webview.INVISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            ShowOrHideWebViewInitialUse = "hide";

            // spinner.setVisibility(View.GONE);
            // imageHome.setVisibility(View.GONE);
            // textHeader.setVisibility(View.GONE);
            textLoading.setVisibility(View.GONE);

            view.setVisibility(webview.VISIBLE);
            super.onPageFinished(view, url);

        }
    }

    private WebView webview;

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        hideSystemUI();
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webview = (WebView) findViewById(R.id.webView);

        // spinner = findViewById(R.id.progressBar);
        // textHeader = findViewById(R.id.textHeader);
        textLoading = findViewById(R.id.textLoading);
        // imageHome = findViewById(R.id.imageHome);

        webview.setWebViewClient(new CustomWebViewClient());

        if (!checkNetworkConnection()) {
            textLoading.setText(getString(R.string.no_connection));
        } else {

            // webview.setWebViewClient(new MyWebViewClient());

            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setDomStorageEnabled(true);
            webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);

            webview.addJavascriptInterface(new WebAppInterface(this), "Android");

            SharedPreferences appPreferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

            String myAuthToken = appPreferences.getString("authToken", "");
            String openPath = appPreferences.getString("openPath", "/");
            final String myFirebaseToken = "";
            /* final String myFirebaseToken = appPreferences.getString("firebaseToken", "");

            if (myFirebaseToken == "") {
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w("LOG", "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();

                                String postData = "platform=android&firebaseToken=" + token;
                                webview.postUrl(myURL, postData.getBytes());

                            }
                        });

            } else {
            */
                if (myAuthToken != "") {
                    String authData = "platform=android&token=" + myAuthToken + "&goto=" + openPath;
                    webview.postUrl(myURL + "/auth/checkToken.php", authData.getBytes());
                } else {
                    String postData = "platform=android&firebaseToken=" + myFirebaseToken;
                    webview.postUrl(myURL, postData.getBytes());
                }
            /* } */

            SharedPreferences.Editor editor = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE).edit();
            editor.putString("openPath", "/");
            editor.apply();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            webview.loadUrl(myURL + "/preferences/");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            webview.loadUrl(myURL);
        } else if (id == R.id.nav_gallery) {
            webview.loadUrl(myURL + "/routes/");
        } else if (id == R.id.nav_slideshow) {
            webview.loadUrl(myURL + "/profile/");
        } else if (id == R.id.nav_tools) {
            webview.loadUrl(myURL + "/preferences/");
        } else if (id == R.id.nav_share) {
            webview.loadUrl(myURL + "/share/");
        } else if (id == R.id.nav_send) {
            webview.loadUrl(myURL + "/send/");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
