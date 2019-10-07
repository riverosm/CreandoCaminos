package com.grupo207.creandocaminos;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;
import static com.grupo207.creandocaminos.MainActivity.APP_PREFERENCES;

public class WebAppInterface {
    Context mContext;

    WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }
    @JavascriptInterface
    public void sendAuthToken(String token) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE).edit();

        if (token == "") {
            editor.remove("authToken");
            editor.apply();
        } else {
            editor.putString("authToken", token);
            editor.apply();
        }
    }
}