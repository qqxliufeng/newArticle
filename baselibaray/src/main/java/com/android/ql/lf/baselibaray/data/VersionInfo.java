package com.android.ql.lf.baselibaray.data;

/**
 * Created by lf on 18.3.12.
 *
 * @author lf on 18.3.12
 */

public class VersionInfo {

    private VersionInfo(){}
    private static VersionInfo versionInfo;

    private int versionCode = 1;
    private String content;
    private String downUrl = null;

    public static VersionInfo getInstance(){
        if (versionInfo == null){
            synchronized (VersionInfo.class){
                if (versionInfo == null){
                    versionInfo = new VersionInfo();
                }
            }
        }
        return versionInfo;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }
}
