package com.virtualspace.app.core;

import android.content.pm.ActivityInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.ActivityInfo;

public class VirtualApplicationInfo {
    public String packageName;
    public String processName;
    public String realPackageName;
    public ActivityInfo[] activities;
    public ServiceInfo[] services;
    public ActivityInfo[] receivers;
    public int userId;
    public boolean isVirtual = true;
}
