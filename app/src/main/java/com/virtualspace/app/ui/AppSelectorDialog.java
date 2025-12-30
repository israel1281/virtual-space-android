package com.virtualspace.app.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.virtualspace.app.R;
import java.util.ArrayList;
import java.util.List;

public class AppSelectorDialog extends Dialog {
    private ListView listView;
    private List<ApplicationInfo> installedApps;
    private OnAppSelectedListener listener;
    
    public interface OnAppSelectedListener {
        void onAppSelected(ApplicationInfo appInfo);
    }
    
    public AppSelectorDialog(@NonNull Context context, OnAppSelectedListener listener) {
        super(context);
        this.listener = listener;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_app_selector);
        setTitle("Select App to Clone");
        
        listView = findViewById(R.id.appListView);
        loadInstalledApps();
        setupListView();
    }
    
    private void loadInstalledApps() {
        PackageManager pm = getContext().getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_META_DATA);
        installedApps = new ArrayList<>();
        
        for (PackageInfo packageInfo : packages) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            // Filter out system apps and the host app
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && 
                !appInfo.packageName.equals(getContext().getPackageName())) {
                installedApps.add(appInfo);
            }
        }
    }
    
    private void setupListView() {
        List<String> appNames = new ArrayList<>();
        PackageManager pm = getContext().getPackageManager();
        
        for (ApplicationInfo appInfo : installedApps) {
            String appName = appInfo.loadLabel(pm).toString();
            appNames.add(appName);
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_list_item_1, appNames);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (listener != null) {
                listener.onAppSelected(installedApps.get(position));
            }
            dismiss();
        });
    }
}
