package com.example.android.alarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //to make our alarm manager
    AlarmManager alarm_manager;
    TimePicker alarm_timepicker;
    TextView update_alarm;
    Context context;
    PendingIntent pending_intent;

    //Map directions code start
    TextView mapSourceText;

    TextView walkText;

    TextView bikeText;

    TextView driveText;

    Button directionsButton;

    //Map directions code end

    private void set_alarm_text(String output) {

        update_alarm.setText(output);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        this.context = this;

        //initialise alarm manager
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //initialise our timepicker
        alarm_timepicker = (TimePicker) findViewById(R.id.timePicker);

        //initialise text update box
        update_alarm = (TextView) findViewById(R.id.update_alarm);

        //create an instance of calendar
        final Calendar calendar = Calendar.getInstance();

        //create an intent to the alarm receiver class
        final Intent alarm_intent = new Intent (this.context, Alarm_Receiver.class);

        //Initialise the start alarm button
        Button start_alarm = (Button) findViewById(R.id.start_alarm);

        //Create an onClick listener to start the alarm
        start_alarm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //setting calendar instance with the hour and minute that we have picked
                //on the time picker
                calendar.set(Calendar.HOUR_OF_DAY, alarm_timepicker.getHour());
                calendar.set(Calendar.MINUTE, alarm_timepicker.getMinute());

                //get the string values of the hour and minute
                int hour = alarm_timepicker.getHour();
                int minute = alarm_timepicker.getMinute();

                //convert the int values to string
                String hour_string = String.valueOf(hour);
                String minute_string = String.valueOf(minute);

                //convert 24 hour time to 12 hour time
                if (hour > 12){
                    hour_string = String.valueOf(hour - 12);
                }

                if (minute < 10) {
                    //10:7 -> 10:07
                    minute_string = "0" + String.valueOf(minute);
                }


                //method to update the alarm text box
                set_alarm_text("Alarm set to: " + hour_string + ":" + minute_string);

                //put in extra string into alarm_intent
                //tells the clock that you put the alarm on button
                alarm_intent.putExtra("extra","Alarm on");


                //create a pendig intent that delays the intent
                //until the specified calendar time
                pending_intent =  PendingIntent.getBroadcast(MainActivity.this, 0, alarm_intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                //set the alarm manager
                alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        pending_intent);


            }
        });

        //initialise the end alarm button
        Button end_alarm = (Button) findViewById(R.id.end_alarm);

        //Create an onClick listener to turn off the alarm

        end_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //method to update the alarm text
                set_alarm_text("Alarm off!");

                //cancel the alarm
                alarm_manager.cancel(pending_intent);

                //put extra string into alarm_intent
                //tells the clock that you pressed "end_alarm" button
                alarm_intent.putExtra("extra", "Alarm off");

                //stop alarm
                sendBroadcast(alarm_intent);
            }

        });

        //Map Directions Code from here till end of file

        mapSourceText = (TextView) findViewById(R.id.map_source_text);
        walkText = (TextView) findViewById(R.id.walk_text);
        bikeText = (TextView)findViewById(R.id.bike_text);
        driveText = (TextView) findViewById(R.id.drive_text);
        directionsButton = (Button) findViewById(R.id.get_directions);
        mapSourceText.setText("");
        walkText.setText("");
        bikeText.setText("");
        driveText.setText("");

        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDirections();

            }
        });


    }


 //Routing
    private void getDirections(){
        LatLng startPos = new LatLng(33.4217795, -111.9196112);
        LatLng endPos = new LatLng(33.45353829999999,-112.07312159999998);

        Routing walkrouting = new Routing.Builder().travelMode(Routing.TravelMode.WALKING).withListener(walkingListener).waypoints(startPos, endPos).build();

        walkrouting.execute();

        Routing bikerouting = new Routing.Builder().travelMode(Routing.TravelMode.BIKING).withListener(bikingListener).waypoints(startPos, endPos).build();

        bikerouting.execute();

        Routing driveRouting = new Routing.Builder().travelMode(Routing.TravelMode.DRIVING).withListener(drivingListener).waypoints(startPos, endPos).build();

        driveRouting.execute();

    }

    RoutingListener walkingListener = new RoutingListener() {
        @Override
        public void onRoutingFailure(RouteException e) {
            Log.d("Subbu", "Walking Failed");
            walkText.setText("Failed to get walking directions");
        }

        @Override
        public void onRoutingStart() {

        }

        @Override
        public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
            Log.d("Subbu", "Walking Success");
            if(arrayList.size() > 0){
                Route route = arrayList.get(0);
                walkText.setText("It takes "+route.getDurationText()+" to walk from "+route.getName()+" with a distance of "+route.getDistanceText());
                mapSourceText.setText("Getting directions from "+route.getName());

            } else {
                walkText.setText("There are no walking options available for this route");
            }
        }

        @Override
        public void onRoutingCancelled() {

        }
    };

    RoutingListener drivingListener = new RoutingListener() {
        @Override
        public void onRoutingFailure(RouteException e) {
            Log.d("Subbu", "Drive Failure");
            driveText.setText("Failed to get driving directions");
        }

        @Override
        public void onRoutingStart() {

        }

        @Override
        public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
            Log.d("Subbu", "Drive Success");
            if(arrayList.size() > 0){
                Route route = arrayList.get(0);
                driveText.setText("It takes "+route.getDurationText()+" to drive from "+route.getName()+" with a distance of "+route.getDistanceText());
            } else {
                driveText.setText("There are no driving options available for this route");
            }

        }

        @Override
        public void onRoutingCancelled() {

        }
    };

    RoutingListener bikingListener = new RoutingListener() {
        @Override
        public void onRoutingFailure(RouteException e) {
            Log.d("Subbu", "Bike Failure");
            bikeText.setText("Failed to get Bike Directions");
        }

        @Override
        public void onRoutingStart() {

        }

        @Override
        public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
            Log.d("Subbu", "biking success");
            if(arrayList.size() > 0){
                Route route = arrayList.get(0);
                bikeText.setText("It takes "+route.getDurationText()+" to bike from "+route.getName()+" with a distance of "+route.getDistanceText());
            } else {
                bikeText.setText("There are no biking options available for this route");
            }

        }

        @Override
        public void onRoutingCancelled() {

        }
    };
}





