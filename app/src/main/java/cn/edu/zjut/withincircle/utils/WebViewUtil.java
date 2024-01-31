package cn.edu.zjut.withincircle.utils;

import android.app.UiModeManager;
import android.content.Context;
import android.graphics.Color;
import android.webkit.WebView;

import androidx.core.content.ContextCompat;

import cn.edu.zjut.withincircle.R;

public class WebViewUtil {
    public static void setWebViewThemeMode(Context context, WebView webView){
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        //判断系统是否为深色模式
        String themeMode = (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) ? "dark" : "light";
        if (themeMode.equals("dark")) {//根据系统模式自动设置WebView的背景颜色
            webView.setBackgroundColor(ContextCompat.getColor(context,R.color.webview_dark_bg_color));//webview背景设置为暗黑色
        } else {
            webView.setBackgroundColor(ContextCompat.getColor(context,R.color.webview_light_bg_color));//webview背景设置为浅灰色
        }
    }
}
