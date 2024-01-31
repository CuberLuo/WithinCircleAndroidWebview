package cn.edu.zjut.withincircle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import cn.edu.zjut.withincircle.utils.AMapUtil;
import cn.edu.zjut.withincircle.utils.LogUtil;
import cn.edu.zjut.withincircle.utils.PermissionUtil;
import cn.edu.zjut.withincircle.utils.WebViewUtil;

public class MainActivity extends AppCompatActivity  {
    private final String siteUrl = "https://within-circle.techvip.site/#/auth";
    private WebView webView;
    private boolean isPageFinished=false;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private Uri imageUri; //图片地址
    public PermissionUtil permissionUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isPageFinished) {//实现启动页的异步加载
                    content.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                } else {
                    return false;
                }
            }
        });
        permissionUtil = PermissionUtil.getInstance();
        permissionUtil.setRepPermissionResult(new PermissionUtil.RepPermissionResult() {
            @Override
            public void OnReqPermissionPass() {
                LogUtil.i("所有权限已授予");
            }

            @Override
            public void OnReqPermissionNoPass() {
                LogUtil.i("权限未完全授予");
            }
        });
        String[] permissions = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        permissionUtil.checkSelPermission(MainActivity.this, permissions);
        Context applicationContext = getApplicationContext();
        Amap amap = new Amap();
        AMapUtil.initAmapLocation(applicationContext, amap);

        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        WebViewUtil.setWebViewThemeMode(this,webView);
        amap.mLocationClient.startAssistantLocation(webView);//启动H5辅助定位
        setWebView();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtil.onReqPermissionResult(this, requestCode, permissions, grantResults);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView(){
        webView.loadUrl(siteUrl);
        WebSettings webSettings=webView.getSettings();
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isPageFinished=true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            // 处理javascript中的alert
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return true;
            }
            // 处理javascript中的confirm
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return true;
            }
            // 处理定位权限请求
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
            // 设置应用程序的标题title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }
            @Override
            public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> filePathCallback,
                                              WebChromeClient.FileChooserParams fileChooserParams) {
                mUploadCallbackAboveL = filePathCallback;
                takePhoto();
                return true;
            }
        });
        //启用js
        webSettings.setJavaScriptEnabled(true);
        //禁止缓存
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //启用localStorage
        webSettings.setDomStorageEnabled(true);
        //启用地理定位
        webSettings.setGeolocationEnabled(true);
    }
    private void takePhoto() {
        String filePath = Environment.getExternalStorageDirectory() + File.separator;
//                + File.separator
//                + Environment.DIRECTORY_PICTURES + File.separator;
        String fileName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        imageUri = Uri.fromFile(new File(filePath + fileName));
        //相册相机选择窗
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        Intent Photo = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooserIntent = Intent.createChooser(Photo, "选择上传方式");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
        startActivityForResult(chooserIntent, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        chooseAbove(resultCode, intent);
    }
    public void chooseAbove(int resultCode, Intent data) {
        Log.e("Base", "调用方法  chooseAbove   " +data);

        if (Activity.RESULT_OK == resultCode) {
            updatePhotos();

            if (data != null) {
                // 这里是针对从文件中选图片的处理
                Uri[] results;
                Uri uriData = data.getData();
                if (uriData != null) {
                    results = new Uri[]{uriData};
                    for (Uri uri : results) {
                        Log.e("Base", "系统里取到的图片：" + uri.toString());
                    }
                    mUploadCallbackAboveL.onReceiveValue(results);
                } else {
                    mUploadCallbackAboveL.onReceiveValue(null);
                }
            } else {
                Log.e("Base", "自己命名的图片：" + imageUri.toString());
                mUploadCallbackAboveL.onReceiveValue(new Uri[]{imageUri});
            }
        } else {
            mUploadCallbackAboveL.onReceiveValue(null);
        }
        mUploadCallbackAboveL = null;
    }
    private void updatePhotos() {
        // 该广播即使多发（即选取照片成功时也发送）也没有关系，只是唤醒系统刷新媒体文件
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(imageUri);
        sendBroadcast(intent);
    }
}