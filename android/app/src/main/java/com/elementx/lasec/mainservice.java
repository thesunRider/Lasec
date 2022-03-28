package com.elementx.lasec;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class mainservice extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private Messenger messageHandler;
    private boolean running;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

            try {
                while (running) {
                    Message message = Message.obtain();
                    message.arg1 = 123;

                    NotificationCompat.Builder notfbuilder = new NotificationCompat.Builder(getApplicationContext(), getString(R.string.channel_id))
                            .setSmallIcon(R.drawable.thief)
                            .setContentTitle("Lasec Intruder Detected")
                            .setContentText("Alert: Someone has crossed the detector")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true);

                    notificationManager.notify(12, notfbuilder.build());

                    messageHandler.send(message);
                    Thread.sleep(5000);
                }

            } catch (Exception e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {

        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service starting", Toast.LENGTH_SHORT).show();
        running = true;

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);


        Bundle extras = intent.getExtras();
        messageHandler = (Messenger) extras.get("MESSENGER");

        // If we get killed, after returning from here, restart
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        running = false;
    }

}