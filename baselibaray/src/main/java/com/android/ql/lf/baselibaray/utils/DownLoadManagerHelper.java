package com.android.ql.lf.baselibaray.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * Created by liufeng on 2018/3/11.
 */

public class DownLoadManagerHelper {

    public static void downLoadApk(final Context context, Uri uri, String fileName){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedOverMetered(true);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(true);
        request.setTitle("发现新版本");
        request.setDescription("正在下载……");
        request.setMimeType("application/vnd.android.package-archive");
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
        final String downPath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+Environment.DIRECTORY_DOWNLOADS+File.separator+fileName+".apk";
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName+".apk");
        final long myDownId = downloadManager.enqueue(request);
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
                if (downId == myDownId){
                    VersionHelp.install(context,downPath);
                }
            }
        };
        context.registerReceiver(broadcastReceiver,intentFilter);
    }


    private static boolean canDownloadState(Context ctx) {
        try {
            int state = ctx.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");

            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
