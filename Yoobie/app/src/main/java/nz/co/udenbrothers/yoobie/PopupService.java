package nz.co.udenbrothers.yoobie;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nz.co.udenbrothers.yoobie.models.Image;
import nz.co.udenbrothers.yoobie.tools.MySQLiteHelper;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class PopupService extends Service {

    private WindowManager windowManager;
    private GifImageView ads;
    private WindowManager.LayoutParams params;
    private Image targetImg;
    private MySQLiteHelper dbHandler;
    private boolean firstRun;
    private SharedPreferences pref;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firstRun = true;
        targetImg = null;
        dbHandler = MySQLiteHelper.getInstance(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        ads = new GifImageView(this);
        pref = getSharedPreferences("app", MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!firstRun) return START_NOT_STICKY;
        ads.setScaleType(ImageView.ScaleType.FIT_XY);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int diminsion;
        if (metrics.widthPixels > metrics.heightPixels) diminsion = metrics.heightPixels / 2;
        else diminsion = metrics.widthPixels / 2;
        params = new WindowManager.LayoutParams(
                diminsion,
                diminsion,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = pref.getInt("lastPosX", 0);
        params.y = pref.getInt("lastPosY", 0);

        switch (pref.getInt("animation", 0)) {
            case 0:
                params.windowAnimations = android.R.style.Animation_Translucent;
                break;
            case 1:
                params.windowAnimations = android.R.style.Animation_Dialog;
                break;
            case 2:
                params.windowAnimations = android.R.style.Animation_Toast;
                break;
        }

        ads.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        if (ads.isShown()) windowManager.updateViewLayout(ads, params);
                        break;
                }
                return true;
            }
        });

        targetImg = dbHandler.getImage();
        if (targetImg != null) {

            if(targetImg.type.equals("jpg")){
                ads.setImageDrawable(Drawable.createFromPath(getExternalFilesDir(null) + "/." + targetImg.id + ".jpg"));
            }
            else {
                try{
                    ads.setImageDrawable(new GifDrawable(getExternalFilesDir(null) + "/." + targetImg.id + ".gif"));
                }
                catch (Exception e){}
            }

        } else {

           ads.setImageResource(R.drawable.notaval);
        }
        windowManager.addView(ads, params);

        firstRun = false;

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = pref.edit();
        if (targetImg != null) {
            dbHandler.addStamp(targetImg.id, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
            editor.putString("lastImgType", targetImg.type);
            editor.putInt("lastImgNo", targetImg.id);
        }
        if (ads.isShown()){
            windowManager.removeView(ads);
            editor.putInt("lastPosX", params.x);
            editor.putInt("lastPosY", params.y);
        }
        editor.apply();
    }
}
