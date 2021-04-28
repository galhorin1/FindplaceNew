package com.example.galhorin.findplacenew;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

/**
 * Created by galhorin on 4/6/2016.
 */
public class AlertManager extends Application {
    //this class make an alert for the user when he got new message(answers).

    //notification manager
    public static NotificationManager myNotificationManager;
    public static Context con;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    protected void displayNote() {
        // Invoking the default notification service
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        Bitmap bm = BitmapFactory.decodeResource(con.getResources(), R.mipmap.ic_launcher);

        mBuilder.setContentTitle("FindPlace");
        mBuilder.setContentText("New message received!");
        mBuilder.setTicker("Implicit: New Message Received!");
        //show icon on notification bar closed
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        //show icon on notification bar opened
        mBuilder.setLargeIcon(bm);


        // when the user presses the notification, it is auto-removed
        mBuilder.setAutoCancel(true);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(con, Searchplace.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(con);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(Searchplace.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        mBuilder.setVibrate(new long[]{1000, 1000, 1000});

        myNotificationManager.notify(0, mBuilder.build());

    }
}
