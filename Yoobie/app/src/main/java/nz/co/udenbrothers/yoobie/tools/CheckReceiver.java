package nz.co.udenbrothers.yoobie.tools;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import nz.co.udenbrothers.yoobie.YoobieService;


public class CheckReceiver extends BroadcastReceiver {
    public CheckReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, YoobieService.class));
        boolean alarmUp = (PendingIntent.getBroadcast(context, 0, new Intent("yoobieSendStamp"), PendingIntent.FLAG_NO_CREATE) != null);
        if (!alarmUp)
        {
            UpdateReceiver ta = new UpdateReceiver();
            ta.starting(context);
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
