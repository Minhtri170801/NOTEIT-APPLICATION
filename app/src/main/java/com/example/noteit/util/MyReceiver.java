package com.example.noteit.util;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.noteit.R;
import com.example.noteit.activites.EditActivity;
import com.example.noteit.activites.HomeActivity;

import java.util.Random;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        String title = bundle.getString("title");

        String id = bundle.getString("id");

        Intent intent1 = new Intent(context, HomeActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.putExtra("noteID",id);

        SaveSharedPreference.setFromNotification(context,id);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent1,0);
        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"979")
                .setContentTitle("Ghi chú: " + title)
                .setSmallIcon(R.drawable.ic_baseline_event_note_24)
                .setContentText("Nhắn nhở ghi chú")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        Random rdm = new Random();
        notificationManagerCompat.notify(rdm.nextInt(300),builder.build());

    }
}
