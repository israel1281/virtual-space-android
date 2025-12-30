package com.virtualspace.app.core;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.virtualspace.app.utils.ApkInstaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class VirtualCore {
    private static VirtualCore sInstance;
    private Context mContext;
    private List<VirtualApp> mInstalledApps = new ArrayList<>();
    private VirtualEnvironment mEnvironment;
    private VirtualPackageManager mPackageManager;
    private final ConcurrentHashMap<String, VirtualAppRuntime> mRuntimes = new ConcurrentHashMap<>();
    
    public static VirtualCore get() {
        if (sInstance == null) {
            synchronized (VirtualCore.class) {
                if (sInstance == null) {
                    sInstance = new VirtualCore();
                }
            }
        }
        return sInstance;
    }
    
    public void startup(Context context) {
        mContext = context.getApplicationContext();
        mEnvironment = new VirtualEnvironment(mContext);
        mPackageManager = new VirtualPackageManager(mContext);
        initVirtualEnvironment();
        loadDemoApps();
    }
    
    private void initVirtualEnvironment() {
        File virtualDir = new File(mContext.getFilesDir(), "virtual");
        if (!virtualDir.exists()) {
            virtualDir.mkdirs();
        }
    }
    
    private void loadDemoApps() {
        // Add demo app for testing
        VirtualApp demoApp = ApkInstaller.createDemoApp(mContext);
        mInstalledApps.add(demoApp);
    }
    
    public boolean installApp(String apkPath) {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            
            if (packageInfo != null) {
                // Create virtual app with unique user ID
                VirtualApp app = new VirtualApp();
                app.packageName = packageInfo.packageName;
                app.appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                app.apkPath = apkPath;
                app.isInstalled = true;
                app.userId = generateUserId();
                
                // Load and store app icon
                try {
                    app.icon = packageInfo.applicationInfo.loadIcon(pm);
                } catch (Exception e) {
                    app.icon = null;
                }
                
                // Import APK to private storage
                String privatePath = importApkToPrivateStorage(apkPath, app);
                if (privatePath != null) {
                    app.apkPath = privatePath;
                    
                    // Create runtime for the app
                    VirtualAppRuntime runtime = VirtualAppRuntime.createRuntime(mContext, app);
                    if (runtime != null) {
                        mRuntimes.put(app.packageName, runtime);
                        
                        // Register with virtual package manager
                        mPackageManager.addVirtualApp(runtime.getApplicationInfo());
                        
                        mInstalledApps.add(app);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private String importApkToPrivateStorage(String apkPath, VirtualApp app) {
        try {
            File sourceApk = new File(apkPath);
            File privateDir = new File(mContext.getFilesDir(), "virtual/apps/" + app.packageName + "_" + app.userId);
            if (!privateDir.exists()) {
                privateDir.mkdirs();
            }
            
            File targetApk = new File(privateDir, "base.apk");
            mEnvironment.copyFile(sourceApk, targetApk);
            
            return targetApk.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private int generateUserId() {
        return mInstalledApps.size() + 1000; // Start from 1000 for virtual users
    }
    
    public boolean launchApp(String packageName) {
        VirtualAppRuntime runtime = mRuntimes.get(packageName);
        if (runtime != null) {
            // Launch through ProxyActivity
            android.content.Intent intent = new android.content.Intent(mContext, ProxyActivity.class);
            intent.putExtra("target_package", packageName);
            intent.putExtra("target_activity", getMainActivity(packageName));
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            return true;
        }
        return false;
    }
    
    private String getMainActivity(String packageName) {
        VirtualAppRuntime runtime = mRuntimes.get(packageName);
        if (runtime != null) {
            return runtime.getMainActivity();
        }
        return "MainActivity"; // fallback
    }
    
    public List<VirtualApp> getInstalledApps() {
        return new ArrayList<>(mInstalledApps);
    }
    
    public Context getContext() {
        return mContext;
    }
    
    public VirtualEnvironment getEnvironment() {
        return mEnvironment;
    }
    
    public VirtualPackageManager getPackageManager() {
        return mPackageManager;
    }
    
    public VirtualAppRuntime getRuntime(String packageName) {
        return mRuntimes.get(packageName);
    }
    
    public boolean isHostApp(String packageName) {
        return mContext.getPackageName().equals(packageName);
    }
}
