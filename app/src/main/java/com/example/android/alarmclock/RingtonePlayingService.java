package com.example.android.alarmclock;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import static android.R.attr.start;


public class RingtonePlayingService extends Service {

    MediaPlayer media_song;
    int startId;
    boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        Log.i("LocalService", "Received start id" + startId + ": " + intent);

        //fetch the extra string values
        String state = intent.getExtras().getString("extra");

        Log.e("Ringtone extra is ", state);


        //setup the notification service
        NotificationManager notification_manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        //setup an intent that goes to the Main Activity.
        Intent intent_main_activity = new Intent(this.getApplicationContext(), MainActivity.class);

        //setup a pending intent
        PendingIntent pending_intent_main_activity = PendingIntent.getActivity(this, 0,
                intent_main_activity, 0);

        //make the notification parameter
        Notification notification_popup = new Notification.Builder(this)
                .setContentTitle("Alarm is going off")
                .setContentText("click me!")
                .setContentIntent(pending_intent_main_activity)
                .setAutoCancel(true)
                .build();

        //set up notification call command
        // notification_manager.notify(0, notification_popup);


        //intent string to state id
        assert state != null;
        switch (state) {
            case "Alarm on":
                startId = 1;
                break;
            case "Alarm off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }


        //if there is no alarm playing and the user press "start_alarm"
        //the alarm music should start playing
        if (!this.isRunning && startId ==1){
            Log.e("there is no music", "you want set alarm");

            //create an instance of media player
            media_song = MediaPlayer.create(this, R.raw.dove);
            media_song.start();

            this.isRunning = true;
            this.startId = 0;

        }

        //if the user press random button
        else if (!this.isRunning && startId == 0){
            Log.e("there is no music", "you want stop alarm");

            this.isRunning = false;
            this.startId = 0;
        }
        else if(this.isRunning && startId == 1){
            Log.e("there is music", "you want start alarm");

            this.isRunning = true;
            this.startId = 0;
        }
        else{
            Log.e("there is music", "you want stop alarm");

            //stop the ringtone
            media_song.stop();
            media_song.reset();

            this.isRunning = false;
            this.startId = 0;
        }

        Log.e("MyActivity", "In the service");

        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy(){
        //tell the user we stopped

        Log.e("on Destroy called", "bye bye");

        super.onDestroy();
        this.isRunning = false;

        Toast.makeText(this, "On Destroy Called", Toast.LENGTH_SHORT).show();
    }
}
