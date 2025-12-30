package com.virtualspace.app.core;

import android.graphics.drawable.Drawable;

public class VirtualApp {
    public String packageName;
    public String appName;
    public String apkPath;
    public boolean isInstalled;
    public Drawable icon;
    public int userId = 0;
    
    @Override
    public String toString() {
        return appName != null ? appName : packageName;
    }
}
