package com.application.food.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.application.food.R;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: r.deluca
 * Date: 04/04/12
 * Time: 10.38
 * To change this template use File | Settings | File Templates.
 */
public class ActivityUtils {
    public static String SPLASH_ACTIVITY="SPLASH_ACTIVITY";
    public static String MAIN_ACTIVITY="MAIN_ACTIVITY";

    public static final int MENU_ITEM_1 = 1;
    public static final int MENU_ITEM_2 = 2;
    public static final int MENU_ITEM_3 = 3;
    public static final int MENU_ITEM_4 = 4;

    public static void callClosing(final Activity a){
        AlertDialog.Builder ad = new AlertDialog.Builder(a);
        ad.setTitle(a.getString(R.string.close_title));
        ad.setMessage(a.getString(R.string.close_text));
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                clearAll(a.getApplicationContext(),a);
            }
        });
        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        ad.show();
    }

    public static  void clearAll(Context context,Activity a){
        context.deleteDatabase("webview.db");
        context.deleteDatabase("webviewCache.db");
        File cacheDir = context.getCacheDir();
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File file : files){
                file.delete();
                Log.i("File", "File deleted");
            }
        }
        FileSystemUtils.cleanTmp();
        a.moveTaskToBack(true);
        System.exit(0);

    }

    public static void dialogMemory(Context act) {
        final Dialog dialog = new Dialog(act);
        dialog.setContentView(R.layout.infodialog);
        dialog.setTitle("Memory");
        dialog.setCancelable(true);
        TextView tt = (TextView) dialog.findViewById(R.id.text);
        tt.setText(Utils.logHeap());
        Button button = (Button) dialog.findViewById(R.id.closeButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                dialog.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it
        dialog.show();
    }

    /* public static void deleteDbAndCache(Context context){
        context.deleteDatabase("webview.db");
        context.deleteDatabase("webviewCache.db");
        MediaUtils.deleteCache(context);
    }*/

    public static int getCurrentTaskActive(Activity ac){
        ActivityManager am = (ActivityManager)ac.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(3); //3 because we have to give it something. This is an arbitrary number
        return tasks.get(0).numActivities;
    }
}
