package cn.edu.zjut.withincircle.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {

    public static PermissionUtil permissionUtil;
    public static final int ReqCode = 0x001;
    public static boolean showSystemSetting = true;

    public RepPermissionResult repPermissionResult;

    private PermissionUtil() {}

    public static PermissionUtil getInstance() {
        if (permissionUtil == null) {
            return new PermissionUtil();
        }
        return permissionUtil;
    }

    public void setRepPermissionResult(RepPermissionResult repPermissionResult) {
        this.repPermissionResult = repPermissionResult;
    }

    /**
     * 校验权限是否授权，如果没有授权就去申请权限
     */
    public void checkSelPermission(Activity mActivity, String... permissions) {
        //未授权集合
        List<String> stringList = new ArrayList<>();

        for (String permission : permissions) {
            //没有授权：PackageManager.PERMISSION_DENIED；已授权：PackageManager.PERMISSION_GRANTED
            if (ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                stringList.add(permission);
            }
        }

        if (stringList.size() > 0) {
            permissionUtil(mActivity, stringList.toArray(new String[stringList.size()]));
        } else {
            repPermissionResult.OnReqPermissionPass();
        }
    }

    /**
     * 统一申请权限
     */
    private void permissionUtil(Activity mActivity, String... permissions) {
        ActivityCompat.requestPermissions(mActivity, permissions, ReqCode);
    }

    public void onReqPermissionResult(Activity mActivity,int reqCode, String[] permissions, int[] grantResults){
        boolean hasPermissionDismiss = false;
        if (reqCode == ReqCode) {
            for (int grantResult : grantResults) {
                if (grantResult == -1) {
                    hasPermissionDismiss = true;
                    break;
                }
            }
            if (hasPermissionDismiss) {//存在未授予的权限
                if (showSystemSetting) {
                    //showSystemPermissionsSettingDialog(mActivity);//跳转到系统设置权限页面
                    LogUtil.i("权限未完全授予，应用无法正常运行");
                } else {
                    repPermissionResult.OnReqPermissionNoPass();
                }
            } else {
                repPermissionResult.OnReqPermissionPass();
            }
        }
    }


    /**
     * 不再提示权限时的展示对话框
     */
    AlertDialog mPermissionDialog;
    public void showSystemPermissionsSettingDialog(final Activity mActivity) {
        final String mPackName = mActivity.getPackageName();
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(mActivity)
                    .setMessage("权限未完全授予，应用无法正常运行")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //mPermissionDialog.dismiss();//关闭弹窗
                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            mActivity.startActivity(intent);
                            mActivity.finish();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    public interface RepPermissionResult {
        //通过
        void OnReqPermissionPass();

        void OnReqPermissionNoPass();
    }


}
