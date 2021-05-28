package com.willblaschko.android.alexa.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.annotation.AnyRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author willb_000 on 12/29/2015.
 */
public class NotificationBuilder {

    String mTitle;
    String mDescription;
    String mImage;
    Bitmap mLargeImage;
    String mBackground;
    PendingIntent mIntent;
    int mSmallIcon;
    int mPriority = NotificationCompat.PRIORITY_DEFAULT;
    List<NotificationCompat.Action> mActions = new ArrayList<>();

    public NotificationBuilder(){

    }

    public NotificationBuilder setTitle(String title) {
        mTitle = title;
        return this;
    }

    public NotificationBuilder setDescription(String description) {
        mDescription = description;
        return this;
    }

    public NotificationBuilder setImage(String uri) {
        mImage = uri;
        return this;
    }

    public NotificationBuilder setSmallIcon(@DrawableRes int icon){
        mSmallIcon = icon;
        return this;
    }

    public NotificationBuilder setLargeImage(Bitmap bitmap) {
        mLargeImage = bitmap;
        return this;
    }

    public NotificationBuilder setLargeImage(String uri) {
        mLargeImage = getBitmapFromURL(uri);
        return this;
    }


    public NotificationBuilder setBackground(String uri) {
        mBackground = uri;
        return this;
    }
    public NotificationBuilder setIntent(PendingIntent intent){
        mIntent = intent;
        return this;
    }

    public NotificationBuilder setPriority(int priority){
        mPriority = priority;
        return this;
    }

    public NotificationBuilder addAction(@DrawableRes int drawable, String title, PendingIntent pendingIntent) {
        mActions.add(new NotificationCompat.Action(drawable, title, pendingIntent));
        return this;
    }

    public Notification build(Context context) throws IOException {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(mTitle)
                .setContentText(mDescription)
                .setPriority(mPriority)
                .setLocalOnly(true)
                .setOngoing(true)
                .setAutoCancel(false)
                .setCategory(Notification.CATEGORY_RECOMMENDATION)
                .setLargeIcon(mLargeImage)
                .setSmallIcon(mSmallIcon)
                .setContentIntent(mIntent);

        for(NotificationCompat.Action action: mActions){
            builder.addAction(action);
        }

        Notification notification;
        if(isDirectToTV(context)) {
            notification = new NotificationCompat.BigPictureStyle(builder).build();
        }else{
            notification = builder.build();
        }
        return notification;
    }

    public static boolean isDirectToTV(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEVISION)
                    || context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK));
        }
        return false;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
    /**
     * get uri to drawable or any other resource type if u wish
     * @param context - context
     * @param drawableId - drawable res id
     * @return - uri
     */
    public static String getUriToDrawable(@NonNull Context context, @AnyRes int drawableId) {
        String imageUri = ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId);
        return imageUri;
    }

}
