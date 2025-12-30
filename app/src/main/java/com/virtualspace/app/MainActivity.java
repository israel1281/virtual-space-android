package com.virtualspace.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.virtualspace.app.core.VirtualCore;
import com.virtualspace.app.core.VirtualApp;
import com.virtualspace.app.ui.AppListAdapter;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AppListAdapter adapter;
    private Button installButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupRecyclerView();
        loadInstalledApps();
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        installButton = findViewById(R.id.installButton);
        
        installButton.setOnClickListener(v -> {
            // For demo purposes, show toast
            Toast.makeText(this, "Select APK file to install", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppListAdapter(this::launchApp);
        recyclerView.setAdapter(adapter);
    }
    
    private void loadInstalledApps() {
        List<VirtualApp> apps = VirtualCore.get().getInstalledApps();
        adapter.updateApps(apps);
    }
    
    private void launchApp(VirtualApp app) {
        Intent intent = new Intent(this, VirtualAppActivity.class);
        intent.putExtra("package_name", app.packageName);
        intent.putExtra("apk_path", app.apkPath);
        startActivity(intent);
    }
}
