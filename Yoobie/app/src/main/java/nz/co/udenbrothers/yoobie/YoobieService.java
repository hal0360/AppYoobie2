package nz.co.udenbrothers.yoobie;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import nz.co.udenbrothers.yoobie.tools.CheckReceiver;

public class YoobieService extends Service {

    private SharedPreferences pref;
    private Handler handler;
    private boolean cooldown;
    private Context connn;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        connn = this;
        handler = new Handler();
        pref = getSharedPreferences("yoobie", MODE_PRIVATE);
        setGister(pref.getInt("priority", 0));
        cooldown = true;
    }

    private void setGister(int vall){
        IntentFilter filter;
        if(vall == 0){
             filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        }
        else {
             filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        }
        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            SharedPreferences.Editor editor = pref.edit();
            int act = intent.getIntExtra("active", -1);
            int pri = intent.getIntExtra("priority", -1);
            if(act != -1) editor.putInt("active", act);
            if(pri != -1){
                editor.putInt("priority", pri);
                unregisterReceiver(mReceiver);
                setGister(pri);
            }
            editor.apply();
        }

        Notification noti = new Notification();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            noti.priority = Notification.PRIORITY_MIN;
        }
        startForeground(R.string.app_name, noti);

        return START_STICKY;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (pref.getInt("active", 0) == 0 || !cooldown || tm.getCallState() != TelephonyManager.CALL_STATE_IDLE) return;
            cooldown = false;
            startService(new Intent(connn, PopupService.class));

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopService(new Intent(connn, PopupService.class));
                }
            }, 3000);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cooldown = true;
                }
            }, 20000);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        sendBroadcast(new Intent(this, CheckReceiver.class));
        handler.removeCallbacksAndMessages(null);
    }
}
