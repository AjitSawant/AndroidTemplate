package com.palash.sampleapp.utilities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Base64;

import com.buzzbox.mob.android.scheduler.SchedulerManager;
import com.palash.sampleapp.R;
import com.palash.sampleapp.task.SynchronizationTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocalSettings {

    private static final String TAG = "LocalSettings";

    //progress dialog for background task
    public static ProgressDialog showDialog(Context context, String msg) {
        ProgressDialog progressDialog = null;
        try {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return progressDialog;
    }

    //hide currently showing progress dialog
    public static void hideDialog(ProgressDialog progressDialog) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //clean json string removes unnecessary \ from string
    public static String cleanResponseString(String result) {
        try {
            if (result != null && !result.equals("")) {
                result = result.substring(1, result.length() - 1);
                result = result.replace("\\", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void RunScheduler(Context context) {
        try {
            SchedulerManager.getInstance().runNow(context, SynchronizationTask.class, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //returns error message respect to error code
    public static String handleError(int responseCode) {
        String msg = null;
        switch (responseCode) {
            case Constants.HTTP_OK_200:
                msg = "Success";
                break;
            case Constants.HTTP_CREATED_201:
                msg = "Record created on server.";
                break;
            case Constants.HTTP_NO_RECORD_FOUND_OK_204:
                msg = "No records found.";
                break;
            case Constants.HTTP_NOT_FOUND_401:
                msg = "Unauthorized Access.";
                break;
            case Constants.HTTP_NOT_OK_404:
                msg = "Application resource not found on server.";
                break;
            case Constants.HTTP_AMBIGUOUS_300:
                msg = "Records already present.";
                break;
            case Constants.HTTP_NOT_OK_500:
                msg = "An error occurred at the server while executing your request. " +
                        "Please try again later. " +
                        "If the problem persists please contact your system administrator.";
                break;
            case Constants.HTTP_NOT_OK_501:
                msg = "Request not Processed.";
                break;
            default:
                msg = "Network is unreachable. Please check your internet connection and try again.";
                break;
        }
        return msg;
    }

    //used to show alert box
    public static void alertBox(final Context boxContext, String myMessage, final Boolean isBackPress) {
        new AlertDialog
                .Builder(new ContextThemeWrapper(boxContext, R.style.alertDialog))
                .setTitle(boxContext.getResources().getString(R.string.app_name))
                .setMessage(myMessage)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isBackPress != false) {

                        }
                    }
                })
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }

    //encodes string
    public static String encodeString(String password) {
        try {
            byte[] data = password.getBytes("UTF-8");
            password = Base64.encodeToString(data, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }

    @SuppressLint("SimpleDateFormat")
    public static String dateToStirng(Date arg0, String formate) {
        SimpleDateFormat dateFormat = null;
        String result = null;
        try {
            dateFormat = new SimpleDateFormat(formate);
            result = dateFormat.format(arg0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null
                    && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null
                        && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }

    public static Typeface setFont_text(Context context) {
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/smart watch.ttf");
        return tf;
    }


}
