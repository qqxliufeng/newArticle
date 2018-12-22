package com.android.ql.lf.baselibaray.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by liufeng on 2018/3/11.
 */

public class VersionHelp {

    public static void checkUpgrade() {
    }

    public static void getUpgradeInfo() {
    }

    public static void downNewVersion(Context context, Uri uri, String fileName) {
        DownLoadManagerHelper.downLoadApk(context, uri, fileName);
    }

    public static int currentVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    public static String currentVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0.0";
        }
    }

    public static void install(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File file = new File(path);
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}
