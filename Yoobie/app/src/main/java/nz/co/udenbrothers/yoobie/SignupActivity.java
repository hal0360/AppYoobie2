package nz.co.udenbrothers.yoobie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import nz.co.udenbrothers.yoobie.models.Country;
import nz.co.udenbrothers.yoobie.models.User;
import nz.co.udenbrothers.yoobie.models.WaveHelper;
import nz.co.udenbrothers.yoobie.models.WaveView;
import nz.co.udenbrothers.yoobie.tools.DataTask;

public class SignupActivity extends AppCompatActivity {

    private android.support.v4.app.FragmentManager manager;
    public Handler handler;
    public SharedPreferences pref;
    public ImageView mainLogo;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    public int curFrag;
    public RelativeLayout logoContain;
    public WaveHelper mWaveHelper;
    private float startingY;
    private InputMethodManager imm;
    public User newUsr;
    public Country[] countries;

    @Override
    public void onBackPressed() {
        switch (curFrag) {
            case 0:
                finish();
                break;
            case 1:
                mWaveHelper.paraAnimation(true);
                replaceFrag(new StarupFragment());
                curFrag = 0;
                break;
            case 2:
                mWaveHelper.initAnimation(0.95f,0.62f);
                replaceFrag(new RegisterOneFragment());
                curFrag = 1;
                break;
            case 3:
                mWaveHelper.paraAnimation(true);
                replaceFrag(new LoginFragment());
                curFrag = 1;
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View viewCurrent = getCurrentFocus();
        if(viewCurrent == null) return super.dispatchTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                float deltaa = startingY - event.getRawY();
                if (deltaa > 100)
                {
                    TextView nextField = (TextView)viewCurrent.focusSearch(View.FOCUS_DOWN);
                    if(nextField != null) nextField.requestFocus();
                    else {
                        viewCurrent.clearFocus();
                        imm.hideSoftInputFromWindow(viewCurrent.getWindowToken(), 0);
                    }
                }
                else{
                    int scrcoords[] = new int[2];
                    viewCurrent.getLocationOnScreen(scrcoords);
                    float x = event.getRawX() + viewCurrent.getLeft() - scrcoords[0];
                    float y = event.getRawY() + viewCurrent.getTop() - scrcoords[1];
                    if (x < viewCurrent.getLeft() || x > viewCurrent.getRight() || y < viewCurrent.getTop() || y > viewCurrent.getBottom()) imm.hideSoftInputFromWindow(viewCurrent.getWindowToken(), 0);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                startingY = event.getRawY();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = getSharedPreferences("app", MODE_PRIVATE);

        try {
            int versionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
            if(versionCode > pref.getInt("version", 0)){
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.apply();
                editor.putInt("version", versionCode);
                editor.apply();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "problem versioning this app", Toast.LENGTH_SHORT).show();
        }

        startService(new Intent(this, YoobieService.class));
        handler = new Handler();
        if (!pref.getString("authorization", "N/A").equals("N/A")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        newUsr = new User();
        new DataTask(this).execute("http://yoobie-api.azurewebsites.net/registration/country");
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(R.layout.activity_signup);
        curFrag = 0;
        mainLogo = (ImageView) findViewById(R.id.imageView777);
        logoContain = (RelativeLayout)findViewById(R.id.animationHoldingFrame);
        mWaveHelper = null;
        manager = getSupportFragmentManager();
        FragmentTransaction transactionn = manager.beginTransaction();
        transactionn.add(R.id.fraggy, new DummyFragment());
        transactionn.commit();
        setinallup();
    }

    public void setinallup() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                replaceFrag(new StarupFragment());
                mWaveHelper = new WaveHelper((WaveView) findViewById(R.id.myWave2));
                mWaveHelper.initAnimation(0f,0.62f);
            }
        }, 3000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationSet s = new AnimationSet(false);
                s.addAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zooming));
                s.addAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveup));
                s.setFillAfter(true);
                mainLogo.startAnimation(s);
            }
        }, 2000);
    }

    public void replaceFrag(Fragment frag) {
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.fraggy, frag);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
