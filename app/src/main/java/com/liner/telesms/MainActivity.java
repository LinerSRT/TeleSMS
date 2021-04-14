package com.liner.telesms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.liner.telesms.service.ImmortalSMSService;
import com.liner.telesms.utils.PM;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText botToken = findViewById(R.id.botToken);
        EditText channelID = findViewById(R.id.channelID);
        Button startButton = findViewById(R.id.startButton);
        Button autorunSettings = findViewById(R.id.autorunSettings);
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.READ_SMS);
        permissions.add(Manifest.permission.RECEIVE_SMS);
        permissions.add(Manifest.permission.SEND_SMS);
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        permissions.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissions.add(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                permissions.add(Manifest.permission.READ_PHONE_NUMBERS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                permissions.add(Manifest.permission.FOREGROUND_SERVICE);
        }
        ActivityCompat.requestPermissions(
                this,
                permissions.toArray(new String[0]),
                1338
        );
        PowerManager powerManager = (PowerManager) Core.getContext().getSystemService(POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!powerManager.isIgnoringBatteryOptimizations(Core.getContext().getPackageName())) {
                @SuppressLint("BatteryLife") Intent ignoringBatteryOptimizationIntent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                ignoringBatteryOptimizationIntent.setData(Uri.parse("package:" + Core.getContext().getPackageName()));
                startActivity(ignoringBatteryOptimizationIntent);
            }
        }
        botToken.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                PM.put(Constant.KEY_TELEGRAM_BOT_TOKEN, String.valueOf(charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        channelID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                PM.put(Constant.KEY_TELEGRAM_CHANNEL_ID, String.valueOf(charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        startButton.setOnClickListener(view -> {
            Intent serviceIntent = new Intent(MainActivity.this, ImmortalSMSService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        });
        autorunSettings.setOnClickListener(view -> gotoAutorunSettings());
    }


    private void gotoAutorunSettings() {
        try {
            Intent intent = new Intent();
            switch (android.os.Build.MANUFACTURER) {
                case "xiaomi":
                    intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    break;
                case "oppo":
                    intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                    break;
                case "vivo":
                    intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                    break;
                case "Letv":
                    intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
                    break;
                case "Honor":
                    intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                    break;
                default:
                    Toast.makeText(this, getResources().getString(R.string.autorun_settings_not_found), Toast.LENGTH_SHORT).show();
                    return;
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}