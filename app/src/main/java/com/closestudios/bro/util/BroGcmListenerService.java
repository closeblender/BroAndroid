package com.closestudios.bro.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.closestudios.bro.MainMenuActivity;
import com.closestudios.bro.R;
import com.closestudios.bro.SignInActivity;
import com.closestudios.bro.networking.BroMessage;
import com.closestudios.bro.networking.ServerApi;
import com.closestudios.bro.networking.ServerApiCalls;
import com.google.android.gms.gcm.GcmListenerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by closestudios on 12/1/15.
 */
public class BroGcmListenerService extends GcmListenerService {

    static String TAG = "BroGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        // Get Message Id, Get Message, Notify Bro
        String messageId = message; // Is the message JSON?

        // Get the message
        ServerApi.getApi().createNewRequest().getBroMessage(BroPreferences.getPrefs(this).getToken(), messageId, new ServerApiCalls.BroMessageCallback() {
            @Override
            public void onSuccess(BroMessage message) {
                Log.d(TAG, "Received Message: " + message.messageTitle);
                try {
                    notifyUser(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "Error: " + error);
            }
        });


    }

    private void notifyUser(BroMessage message) throws IOException {


        File tempMp3 = File.createTempFile("tempfile", "mp3", getCacheDir());

        FileOutputStream fos = new FileOutputStream(tempMp3);
        fos.write(message.audioBytes);
        fos.close();


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.cast_ic_notification_0)
                        .setContentTitle(message.messageTitle)
                        .setContentText(message.messageDetails)
                        .setSound(android.net.Uri.parse(tempMp3.toURI().toString()));

                        //.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                                //+ "://" + getPackageName() + "/raw/bro"));
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainMenuActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainMenuActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(9, mBuilder.build());

    }

}