package com.virtualspace.app.core;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import java.util.List;
import java.util.ArrayList;

public class VirtualPackageManager {
    private final Context mHostContext;
    private final PackageManager mHostPM;
    private final List<VirtualApplicationInfo> mVirtualApps;
    
    public VirtualPackageManager(Context hostContext) {
        mHostContext = hostContext;
        mHostPM = hostContext.getPackageManager();
        mVirtualApps = new ArrayList<>();
    }
    
    public void addVirtualApp(VirtualApplicationInfo appInfo) {
        mVirtualApps.add(appInfo);
    }
    
    public PackageInfo getPackageInfo(String packageName, int flags) {
        // Check if it's a virtual package
        for (VirtualApplicationInfo appInfo : mVirtualApps) {
            if (appInfo.packageName.equals(packageName)) {
                return createVirtualPackageInfo(appInfo);
            }
        }
        
        // Fall back to host system for non-virtual packages
        try {
            return mHostPM.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
    
    public ApplicationInfo getApplicationInfo(String packageName, int flags) {
        // Check if it's a virtual package
        for (VirtualApplicationInfo appInfo : mVirtualApps) {
            if (appInfo.packageName.equals(packageName)) {
                return createVirtualApplicationInfo(appInfo);
            }
        }
        
        // Fall back to host system
        try {
            return mHostPM.getApplicationInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
    
    public List<PackageInfo> getInstalledPackages(int flags) {
        List<PackageInfo> packages = new ArrayList<>();
        
        // Add virtual packages
        for (VirtualApplicationInfo appInfo : mVirtualApps) {
            packages.add(createVirtualPackageInfo(appInfo));
        }
        
        // Add host packages (filtered to exclude virtual ones)
        List<PackageInfo> hostPackages = mHostPM.getInstalledPackages(flags);
        for (PackageInfo pkg : hostPackages) {
            if (!isVirtualPackage(pkg.packageName)) {
                packages.add(pkg);
            }
        }
        
        return packages;
    }
    
    private PackageInfo createVirtualPackageInfo(VirtualApplicationInfo appInfo) {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = appInfo.packageName;
        packageInfo.activities = appInfo.activities;
        packageInfo.services = appInfo.services;
        packageInfo.receivers = appInfo.receivers;
        
        ApplicationInfo applicationInfo = createVirtualApplicationInfo(appInfo);
        packageInfo.applicationInfo = applicationInfo;
        
        return packageInfo;
    }
    
    private ApplicationInfo createVirtualApplicationInfo(VirtualApplicationInfo appInfo) {
        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.packageName = appInfo.packageName;
        applicationInfo.processName = appInfo.processName;
        applicationInfo.flags = ApplicationInfo.FLAG_INSTALLED;
        return applicationInfo;
    }
    
    private boolean isVirtualPackage(String packageName) {
        return packageName.startsWith("virtual.");
    }
    
    public boolean isHostPackage(String packageName) {
        return mHostContext.getPackageName().equals(packageName);
    }
}
