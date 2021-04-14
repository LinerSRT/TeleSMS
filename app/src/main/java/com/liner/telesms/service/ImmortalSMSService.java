package com.liner.telesms.service;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.liner.telesms.Constant;
import com.liner.telesms.R;
import com.liner.telesms.messaging.SMSMessage;
import com.liner.telesms.telegram.TeleSMSBot;
import com.liner.telesms.utils.Broadcast;
import com.liner.telesms.utils.PM;

import java.util.Calendar;

public class ImmortalSMSService extends Service {
    private TeleSMSBot smsBot;
    private Broadcast messageHandler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PM.init(this);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "SMSService")
                .setSmallIcon(R.drawable.sms_icon)
                .setContentTitle("Telegram Service")
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(
                    "SMSService",
                    "TelegramService",
                    NotificationManager.IMPORTANCE_HIGH
            ));
            startForeground(9, notificationBuilder.build());
        }
        smsBot = new TeleSMSBot(message -> {
            Log.d("SERVICE", message.toString());
        });
        messageHandler = new Broadcast(this, Constant.ACTION_DEBUG_SEND_SMS) {
            @Override
            public void handleChanged(Intent intent) {
                SMSMessage smsMessage = (SMSMessage) intent.getSerializableExtra("message");
                if (smsMessage != null) {
                    new Thread(() -> {
                        try {
                            smsBot.sendSMS(Long.parseLong(PM.get(Constant.KEY_TELEGRAM_BOT_TOKEN, Constant.TELEGRAM_BOT_TOKEN)), smsMessage);
                            smsBot.sendSMS(Long.parseLong(PM.get(Constant.KEY_TELEGRAM_CHANNEL_ID, Constant.CHANNEL_ID)), smsMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        };
        messageHandler.setListening(true);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        messageHandler.setListening(false);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 3);
        Intent intent = new Intent(this, RestartReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
