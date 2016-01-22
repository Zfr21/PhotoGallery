package com.bignerdranch.android.photogallery.Networking;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.bignerdranch.android.photogallery.Activities.PhotoGalleryActivity;
import com.bignerdranch.android.photogallery.Model.GalleryItem;
import com.bignerdranch.android.photogallery.R;
import com.bignerdranch.android.photogallery.Utils.QueryPreferences;

import java.util.List;
/**
 * Created by zafer on 20.01.16.
 */

public class PollService extends IntentService {

    private static final String TAG = "PollService";
    private static final int POLL_INTERVAL = 1000*60;//60 seconds
    private static final int NOTIFY_ID = 1;


    public static Intent newInstance(Context context) {
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    //calling on background thread
    @Override
    protected void onHandleIntent(Intent intent) {

        if(!isNetworkAvailableAndConnected()) {
            return;
        }
        String query = QueryPreferences.getStoredQuery(this);
        String lastResultId  = QueryPreferences.getLastResultId(this);
        List<GalleryItem> items;

        if(query == null) {
            items = new FlickrFetchr().fetchRecentPhotos();
        }else {
            items = new FlickrFetchr().searchPhotos(query);
        }

        if(items.size() == 0) {
            return;
        }

        String resultId = items.get(0).getId();

        if(resultId.equals(lastResultId)) {

            Log.i(TAG, "Got an old result: " + resultId);
        } else {
            Log.i(TAG, "Got a new result: " + resultId);
        }

        Resources resources = getResources();
        Intent i = PhotoGalleryActivity.newIntent(this);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        Notification notification = new NotificationCompat.Builder(this)
                                        .setTicker(resources.getString(R.string.new_pictures_title))
                                        .setSmallIcon(android.R.drawable.ic_menu_report_image)
                                        .setContentTitle(resources.getString(R.string.new_pictures_title))
                                        .setContentText(resources.getString(R.string.new_pictures_text))
                                        .setContentIntent(pi)
                                        .setAutoCancel(true)
                                        .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFY_ID, notification);

        QueryPreferences.setLastResultId(this,resultId);
    }

    private boolean isNetworkAvailableAndConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }

    public static void setServiceAlarm(Context context, boolean isOn) {

        Intent i = PollService.newInstance(context);

        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context) {

        Intent i = PollService.newInstance(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);

        return pi != null;
    }
}
