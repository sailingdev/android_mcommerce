package com.example.mcommerce.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.mcommerce.AlertProductActivity;
import com.example.mcommerce.CustomerInfoActivity;
import com.example.mcommerce.CustomerPortalActivity;
import com.example.mcommerce.R;
import com.example.mcommerce.utils.ObjectUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static String TAG = "MyFirebaseMessagingService";

    private NotificationManager mNotificationManager;
    private static int notKey = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String from = remoteMessage.getFrom();
        if (!ObjectUtils.isEmpty(from)) {
            final int notificationID = notKey++;

            Map<String, String> dataMap = remoteMessage.getData();
            String title = dataMap.get("product_name");
            String subtitle = dataMap.get("product_message");

            String product_name = title;
            String product_message = subtitle;
            String product_code = dataMap.get("product_code");
            String product_description = dataMap.get("product_description");
            String product_photo = dataMap.get("product_photo");
            String product_id = dataMap.get("product_id");
            String username = dataMap.get("username");
            String product_category = dataMap.get("product_category");
            String redeem_without_cash = dataMap.get("redeem_without_cash");
            String redeem_with_cash = dataMap.get("redeem_with_cash");
            String product_shop_name = dataMap.get("product_shop_name");


            mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent intent = new Intent(getApplicationContext(), AlertProductActivity.class);
            intent.putExtra("product_name", product_name);
            intent.putExtra("product_message", product_message);
            intent.putExtra("product_code", product_code);
            intent.putExtra("product_description", product_description);
            intent.putExtra("product_photo", product_photo);
            intent.putExtra("product_id", product_id);
            intent.putExtra("username", username);
            intent.putExtra("product_category", product_category);
            intent.putExtra("redeem_without_cash", redeem_without_cash);
            intent.putExtra("redeem_with_cash", redeem_with_cash);
            intent.putExtra("product_shop_name", product_shop_name);

            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(subtitle);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.setContentIntent(contentIntent);
            mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            mBuilder.setAutoCancel(true);
            mBuilder.setColor(0x00000000);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
                mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            }

            createNotificationChannel();

            mNotificationManager.notify(notificationID, mBuilder.build());


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                String CHANNEL_ID = "my_channel_01";// The id of the channel.
                CharSequence name = "Notification";// The user-visible name of the channel.
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                // Create a notification and set the notification channel.
                Notification notification = new Notification.Builder(getApplicationContext())
                        .setContentTitle(title)
                        .setContentText(subtitle)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(contentIntent)
                        .setChannelId(CHANNEL_ID)
                        .build();

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.createNotificationChannel(mChannel);

                // Issue the notification.
                mNotificationManager.notify(notificationID , notification);
            }
        }
    }



    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "channel_id",
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        Log.e(TAG, token);
        //sendRegistrationToServer(token);
    }
}


