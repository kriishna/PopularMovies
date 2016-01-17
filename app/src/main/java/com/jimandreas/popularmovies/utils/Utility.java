package com.jimandreas.popularmovies.utils;

/**
 *   Youtube credit for this particular code work - with backstory on Recycler View and ListView:
 *    Using RecyclerView Part 3
 *   https://www.youtube.com/watch?v=jkK-Uxx6dLQ
 *   part of the Advanced Android App Development training at Audacity
 *       See also this repo:
 *
 * https://github.com/udacity/Advanced_Android_Development/tree/6.18_Bonus_RecyclerView_Code/app/src/main/java/com/example/android/sunshine/app
 *
 * And the training (HIGHLY RECOMMENDED):
 * https://www.udacity.com/course/advanced-android-app-development--ud855
 *
 * in particular for this module, ref:
 *
 *   wisdom.credit_to(Dan_Galpin);
 *
 INSTRUCTOR
 Dan Galpin is a Developer Advocate for Android,
 where his focus has been on Android performance tuning,
 developer training, and games. He has spent over 10 years
 working in the mobile space, developing at almost every
 layer of the phone stack. There are videos that demonstrate
 that he has performed in musical theater productions, but he would deny it.
 */

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

public class Utility {

    /**
     * Returns true if the network is available or about to become available.
     *
     * @param context Context used to get the ConnectivityManager
     * @return true if the network is available
     */
    static public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isAppInstalled(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public static void viewUrl(String url, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    /*
     *  see stackoverflow for wisdom on this technique:
     *  http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
     */
    public static void watchYoutubeVideo(String id, Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            if (Utility.isAppInstalled("com.google.android.youtube", context)) {
                intent.setClassName("com.google.android.youtube", "com.google.android.youtube.WatchActivity");
            }
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            context.startActivity(intent);
        }
    }
}
