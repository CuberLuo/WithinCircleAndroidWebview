package cn.edu.zjut.withincircle.utils;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import cn.edu.zjut.withincircle.Amap;

public class AMapUtil {

    //声明定位回调监听器
    public static AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    LogUtil.i(String.valueOf(aMapLocation.getLocationType()));
                    LogUtil.i(String.valueOf(aMapLocation.getLatitude()));
                    LogUtil.i(String.valueOf(aMapLocation.getLongitude()));
                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }

        }
    };

    public static void initAmapLocation(Context applicationContext, Amap amp){
        //设置接口之前保证隐私政策合规
        AMapLocationClient.updatePrivacyShow(applicationContext,true,true);
        AMapLocationClient.updatePrivacyAgree(applicationContext,true);

        try {
            //初始化定位
            amp.mLocationClient = new AMapLocationClient(applicationContext);
            //初始化AMapLocationClientOption对象
            amp.mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            amp.mLocationClient.setLocationListener(mLocationListener);
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            amp.mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
            amp.mLocationOption.setInterval(1000);
            //给定位客户端对象设置定位参数
            amp.mLocationClient.setLocationOption(amp.mLocationOption);
            //启动定位
            amp.mLocationClient.startLocation();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
