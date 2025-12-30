package com.virtualspace.app.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
        AppIconAdapter adapter = new AppIconAdapter(getContext(), installedApps);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (listener != null) {
                listener.onAppSelected(installedApps.get(position));
            }
            dismiss();
        });
    }
    
    private static class AppIconAdapter extends BaseAdapter {
        private Context context;
        private List<ApplicationInfo> apps;
        private PackageManager pm;
        
        AppIconAdapter(Context context, List<ApplicationInfo> apps) {
            this.context = context;
            this.apps = apps;
            this.pm = context.getPackageManager();
        }
        
        @Override
        public int getCount() {
            return apps.size();
        }
        
        @Override
        public Object getItem(int position) {
            return apps.get(position);
        }
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_app_with_icon, parent, false);
            }
            
            ApplicationInfo appInfo = apps.get(position);
            
            ImageView icon = convertView.findViewById(R.id.appIcon);
            TextView name = convertView.findViewById(R.id.appName);
            TextView packageName = convertView.findViewById(R.id.packageName);
            
            icon.setImageDrawable(appInfo.loadIcon(pm));
            name.setText(appInfo.loadLabel(pm));
            packageName.setText(appInfo.packageName);
            
            return convertView;
        }
    }
}
