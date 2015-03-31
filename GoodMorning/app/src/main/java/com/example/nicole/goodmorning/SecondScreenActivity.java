package com.example.nicole.goodmorning;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by nicole on 3/29/15.
 */
public class SecondScreenActivity extends Activity {
    TextView dataTextView;
    long calID;

    // Projection array. Creating indices for this array instead of doing
// dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen2);

        // Run query
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[] {"test@gmail.com", "com.google",
                "test@gmail.com"};
// Submit the query and get a Cursor object back.
        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            Log.d("MyActivity", "Next Calendar");
            calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
            Log.d("Event ", calID+" "+displayName+" "+accountName+" "+ownerName);

            // Do something with the values...
            dataTextView = (TextView) findViewById(R.id.data);
            dataTextView.setText("Event " + displayName);
        }

        Log.d("MyActivity", "Get 3 events");
        getLastThreeEvents();

        //Weather
        TextView weatherIcon = (TextView) findViewById(R.id.custom_font);
        Typeface weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weather.ttf");
        weatherIcon.setTypeface(weatherFont);
        weatherIcon.setText(getString(R.string.weather_drizzle));

    }

    private static final String DATE_TIME_FORMAT = "MMM dd yyyy, HH:mm";
    public static String getDateTimeStr(int p_delay_min) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        if (p_delay_min == 0) {
            return sdf.format(cal.getTime());
        } else {
            Date l_time = cal.getTime();
            l_time.setMinutes(l_time.getMinutes() + p_delay_min);
            return sdf.format(l_time);
        }
    }
    public static String getDateTimeStr(String p_time_in_millis) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        Date l_time = new Date(Long.parseLong(p_time_in_millis));
        return sdf.format(l_time);
    }

    private void getLastThreeEvents() {
        Cursor l_managedCursor = null;
        ContentResolver cr = getContentResolver();
        Uri l_eventUri = CalendarContract.Events.CONTENT_URI;


        String[] l_projection = new String[]{"title", "dtstart"};
        Log.d("CalID: ", calID+" ");
        l_managedCursor = cr.query(l_eventUri, l_projection, "((calendar_id=" + calID+") AND (dtstart > "+ System.currentTimeMillis()+") AND (dtstart < "+ (System.currentTimeMillis()+86400000) +"))", null, "dtstart Asc");

        int l_cnt = 0;
        StringBuilder l_displayText = new StringBuilder();
        // Use the cursor to step through the returned records
        while (l_managedCursor.moveToNext()) {
            Log.d("MyActivity", "Next Event");
            String title = null;
            String startDate;

            // Get the field values
            title = l_managedCursor.getString(0);
            startDate = l_managedCursor.getString(1);
            Log.d("Event ", "Event: "+title + " " + startDate);

            // Do something with the values...
            l_displayText.append(title + "\n " + getDateTimeStr(startDate) + "\n\n");
            ++l_cnt;
        }

        dataTextView = (TextView) findViewById(R.id.data);
        dataTextView.setText(l_displayText);


    }
}
