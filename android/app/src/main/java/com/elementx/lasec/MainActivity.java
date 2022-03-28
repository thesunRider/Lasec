package com.elementx.lasec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.*;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public final Handler messageHandler = new MessageHandler();
    private Switch simpleSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView prs1 =  (TextView) findViewById(R.id.log_edit);


        createNotificationChannel();

        simpleSwitch = (Switch) findViewById(R.id.switch_main);
        if(isMyServiceRunning(mainservice.class)){ simpleSwitch.setChecked(true);
        }else{
            simpleSwitch.setChecked(false);
        }

        final Intent intent = new Intent(this, mainservice.class);

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    String currentDate = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault()).format(new Date());
                    prs1.append("\n> "+currentDate +": Lasec Service Started" );

                    intent.putExtra("MESSENGER", new Messenger(messageHandler));
                    startService(intent);

                }else{
                    String currentDate = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault()).format(new Date());
                    prs1.append("\n> "+currentDate +": Lasec Service Terminated" );
                    stopService(intent);
                }
            }
        });



    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public  class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            int state = message.arg1;
            TextView prs1 =  (TextView) findViewById(R.id.log_edit);
            String currentDate = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault()).format(new Date());
            prs1.append("\n> "+currentDate +": Intruder Crossing Detected");
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case  R.id.settings:
                startActivity(new Intent(this, Settings.class));
                return true;

            case R.id.about:
                startActivity(new Intent(this, About.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}