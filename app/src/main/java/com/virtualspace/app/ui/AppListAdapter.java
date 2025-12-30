package com.virtualspace.app.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.virtualspace.app.R;
import com.virtualspace.app.core.VirtualApp;
import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {
    private List<VirtualApp> apps = new ArrayList<>();
    private OnAppClickListener listener;
    private Context context;
    
    public interface OnAppClickListener {
        void onAppClick(VirtualApp app);
    }
    
    public AppListAdapter(Context context, OnAppClickListener listener) {
        this.context = context;
        this.listener = listener;
    }
    
    public void updateApps(List<VirtualApp> newApps) {
        apps.clear();
        apps.addAll(newApps);
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app_with_icon, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VirtualApp app = apps.get(position);
        holder.appName.setText(app.appName);
        holder.packageName.setText(app.packageName);
        
        // Load app icon
        if (app.icon != null) {
            holder.appIcon.setImageDrawable(app.icon);
        } else {
            // Try to load icon from package manager
            try {
                PackageManager pm = context.getPackageManager();
                holder.appIcon.setImageDrawable(pm.getApplicationIcon(app.packageName));
            } catch (Exception e) {
                holder.appIcon.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAppClick(app);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return apps.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView packageName;
        
        ViewHolder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            packageName = itemView.findViewById(R.id.packageName);
        }
    }
}
