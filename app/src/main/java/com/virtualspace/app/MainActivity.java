package com.virtualspace.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.virtualspace.app.core.VirtualCore;
import com.virtualspace.app.core.VirtualApp;
import com.virtualspace.app.ui.AppListAdapter;
import com.virtualspace.app.ui.AppSelectorDialog;
import com.virtualspace.app.utils.ApkInstaller;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_APK_REQUEST = 1001;
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
        
        installButton.setText("Clone App");
        installButton.setOnClickListener(v -> showAppSelector());
    }
    
    private void showAppSelector() {
        AppSelectorDialog dialog = new AppSelectorDialog(this, this::cloneApp);
        dialog.show();
    }
    
    private void cloneApp(ApplicationInfo appInfo) {
        try {
            String apkPath = appInfo.sourceDir;
            boolean success = ApkInstaller.installApkToVirtualSpace(this, apkPath);
            if (success) {
                Toast.makeText(this, "App cloned to virtual space: " + appInfo.loadLabel(getPackageManager()), Toast.LENGTH_SHORT).show();
                loadInstalledApps(); // Refresh the list
            } else {
                Toast.makeText(this, "Failed to clone app", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error cloning app: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.android.package-archive");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        
        try {
            startActivityForResult(Intent.createChooser(intent, "Select APK file"), PICK_APK_REQUEST);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a file manager", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_APK_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri apkUri = data.getData();
                installApkFromUri(apkUri);
            }
        }
    }
    
    private void installApkFromUri(Uri apkUri) {
        try {
            String apkPath = apkUri.getPath();
            if (apkPath != null) {
                boolean success = ApkInstaller.installApkToVirtualSpace(this, apkPath);
                if (success) {
                    Toast.makeText(this, "APK installed to virtual space", Toast.LENGTH_SHORT).show();
                    loadInstalledApps(); // Refresh the list
                } else {
                    Toast.makeText(this, "Failed to install APK", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error installing APK: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
        boolean success = VirtualCore.get().launchApp(app.packageName);
        if (!success) {
            Toast.makeText(this, "Failed to launch virtual app", Toast.LENGTH_SHORT).show();
        }
    }
}
