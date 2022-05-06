package com.bcttgd.parentapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class CustomNotification {

    public static void createOutOfZoneNotification(String phone_number, String child_name, String zone_name, String last_position_time, Activity activity, Context context){
        String title = "ChildSafe: "+child_name+" is not in a safe place !";
        String content =  child_name +" is in : "+zone_name+". Their position was last recorded at "+last_position_time;
        createUrgentNotification(title, content, phone_number, activity, context);
    }

    public static void createDownloadNotification(String fileDownloaded, String child_name, Activity activity, Context context){
        String title = "ChildSafe: "+child_name+" downloaded : " + fileDownloaded;
        String content =  child_name +" downloaded : " + fileDownloaded+". We believe it contains disturbing content.";
        createUrgentNotification(title, content, activity, context);
    }

    public static void createNoLocationChange(String phone_number, String child_name, String zone_name, String last_position_time, Activity activity, Context context){
        String title = "ChildSafe: "+child_name+"'s location hasn't moved for a while !";
        String content =  child_name +" is in : "+zone_name+". Their position was last recorded at "+last_position_time;
        createMinorNotification(title, content, phone_number, activity, context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static NotificationChannel createUrgentChannel(Activity activity){
        /*NotificationChannel is necessary Since Oreo*/

        NotificationChannel channel = new NotificationChannel("UrgentNotification", "UrgentNotification", NotificationManager.IMPORTANCE_HIGH);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        channel.setSound(alarmSound, new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build());
        channel.setVibrationPattern(new long []{ 0, 100 , 50 , 100 , 250 , 500, 500, 500 });
        NotificationManager manager = activity.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        return channel;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static NotificationChannel createMinorChannel(Activity activity){
        /*NotificationChannel is necessary Since Oreo*/

        NotificationChannel channel = new NotificationChannel("MinorNotification", "MinorNotification", NotificationManager.IMPORTANCE_DEFAULT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        channel.setSound(alarmSound, new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build());
        channel.setVibrationPattern(new long []{ 0, 1000});
        NotificationManager manager = activity.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        return channel;
    }

    private static void createUrgentNotification(String title, String content, String phone_number, Activity activity, Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = createUrgentChannel(activity);
        }

        /*Create all intents for notification tap action and additional actions*/
        Intent openAppIntent = new Intent(context, activity.getClass());
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE);
        PendingIntent firstActionPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE);
        Intent dialActionIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone_number));
        PendingIntent dialActionPendingIntent = PendingIntent.getActivity(context, 0, dialActionIntent, PendingIntent.FLAG_IMMUTABLE);


        /*Build the OutOfZone Notification*/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, "UrgentNotification");
        builder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_notification_logo)
                .setAutoCancel(true)
                .setCategory(Notification.CATEGORY_ALARM)
                .setDefaults((Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS))
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_notification_logo/*TODO Add the logo*/, "Open App", firstActionPendingIntent)
                .addAction(R.drawable.ic_notification_logo/*TODO Add this icon*/, "Call", dialActionPendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(activity);

        /*Send the notification*/
        managerCompat.notify(1, builder.build());
    }
    private static void createUrgentNotification(String title, String content, Activity activity, Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = createUrgentChannel(activity);
        }

        /*Create all intents for notification tap action and additional actions*/
        Intent openAppIntent = new Intent(context, activity.getClass());
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE);
        PendingIntent firstActionPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE);



        /*Build the OutOfZone Notification*/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, "UrgentNotification");
        builder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_notification_logo)
                .setAutoCancel(true)
                .setCategory(Notification.CATEGORY_ALARM)
                .setDefaults((Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS))
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_notification_logo/*TODO Add the logo*/, "Open App", firstActionPendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(activity);

        /*Send the notification*/
        managerCompat.notify(1, builder.build());
    }

    private static void createMinorNotification(String title, String content, String phone_number, Activity activity, Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = createMinorChannel(activity);
        }

        /*Create all intents for notification tap action and additional actions*/
        Intent openAppIntent = new Intent(context, activity.getClass());
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE);
        PendingIntent firstActionPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE);



        /*Build the OutOfZone Notification*/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, "MinorNotification");
        builder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_notification_logo)
                .setAutoCancel(true)
                .setCategory(Notification.CATEGORY_ALARM)
                .setDefaults((Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS))
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_notification_logo/*TODO Add the logo*/, "Open App", firstActionPendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(activity);

        /*Send the notification*/
        managerCompat.notify(1, builder.build());
    }


}
