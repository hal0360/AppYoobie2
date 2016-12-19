package nz.co.udenbrothers.yoobie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import nz.co.udenbrothers.yoobie.models.WaveHelper;
import nz.co.udenbrothers.yoobie.models.WaveView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewPager pager;
    private View thumb, thumb2;
    public View homeV, profileV, prizeV, settingV;
    public Handler handler;
    public SharedPreferences pref;
    private boolean isHome;
    private float waterlevel;
    private WaveHelper mWaveHelper;
    private boolean hasStarted;
    public boolean hasEntry;
    private ImageView ihome, iprofile, iprize, isetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWaveHelper = new WaveHelper((WaveView) findViewById(R.id.myWave));
        waterlevel = 0.73f;
        isHome = true;
        hasStarted = true;
        hasEntry = true;
        pref = getSharedPreferences("app", MODE_PRIVATE);
        handler = new Handler();
        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        RelativeLayout relay = (RelativeLayout) findViewById(R.id.homeButton);
        relay.setOnClickListener(this);
        RelativeLayout relay2 = (RelativeLayout) findViewById(R.id.profileButton);
        relay2.setOnClickListener(this);
        RelativeLayout relay3 = (RelativeLayout) findViewById(R.id.prizesButton);
        relay3.setOnClickListener(this);
        RelativeLayout relay4 = (RelativeLayout) findViewById(R.id.settingButton);
        relay4.setOnClickListener(this);
        ImageView ima1 = (ImageView) findViewById(R.id.imageHam);
        ima1.setOnClickListener(this);

        homeV = findViewById(R.id.homeLine);
        profileV =  findViewById(R.id.profileLine);
        prizeV =  findViewById(R.id.prizesLine);
        settingV =  findViewById(R.id.settingLine);
        thumb = findViewById(R.id.thumb);
        thumb2 = findViewById(R.id.thumb2);
        ihome = (ImageView) findViewById(R.id.homeImg);
        iprize = (ImageView) findViewById(R.id.prizesImg);
        iprofile = (ImageView) findViewById(R.id.profileImg);
        isetting = (ImageView) findViewById(R.id.settingImg);

        View mySwitch = findViewById(R.id.mySwitch);
        mySwitch.setOnClickListener(this);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                switch(position) {
                    case 0:
                        homeV.setBackgroundColor(0xFF000000);
                        profileV.setBackgroundColor(0xFFFFFFFF);
                        prizeV.setBackgroundColor(0xFFFFFFFF);
                        settingV.setBackgroundColor(0xFFFFFFFF);
                        ihome.setBackgroundResource(R.drawable.dark_home);
                        iprofile.setBackgroundResource(R.drawable.light_profile);
                        iprize.setBackgroundResource(R.drawable.light_prize);
                        isetting.setBackgroundResource(R.drawable.light_setting);
                        isHome = true;
                        mWaveHelper.initAnimation(waterlevel,0.73f);
                        waterlevel = 0.73f;
                        break;
                    case 1:
                        homeV.setBackgroundColor(0xFFFFFFFF);
                        profileV.setBackgroundColor(0xFF000000);
                        prizeV.setBackgroundColor(0xFFFFFFFF);
                        settingV.setBackgroundColor(0xFFFFFFFF);
                        ihome.setImageResource(R.drawable.light_home);
                        iprofile.setImageResource(R.drawable.dark_profile);
                        iprize.setImageResource(R.drawable.light_prize);
                        isetting.setImageResource(R.drawable.light_setting);
                        isHome = false;
                        mWaveHelper.initAnimation(waterlevel,0.45f);
                        waterlevel = 0.45f;
                        break;
                    case 2:
                        homeV.setBackgroundColor(0xFFFFFFFF);
                        profileV.setBackgroundColor(0xFFFFFFFF);
                        prizeV.setBackgroundColor(0xFF000000);
                        settingV.setBackgroundColor(0xFFFFFFFF);
                        ihome.setBackgroundResource(R.drawable.light_home);
                        iprofile.setBackgroundResource(R.drawable.light_profile);
                        iprize.setBackgroundResource(R.drawable.dark_prize);
                        isetting.setBackgroundResource(R.drawable.light_setting);
                        isHome = false;
                        mWaveHelper.initAnimation(waterlevel,0.3f);
                        waterlevel = 0.3f;
                        break;
                    case 3:
                        homeV.setBackgroundColor(0xFFFFFFFF);
                        profileV.setBackgroundColor(0xFFFFFFFF);
                        prizeV.setBackgroundColor(0xFFFFFFFF);
                        settingV.setBackgroundColor(0xFF000000);
                        ihome.setBackgroundResource(R.drawable.light_home);
                        iprofile.setBackgroundResource(R.drawable.light_profile);
                        iprize.setBackgroundResource(R.drawable.light_prize);
                        isetting.setBackgroundResource(R.drawable.dark_setting);
                        isHome = false;
                        mWaveHelper.initAnimation(waterlevel,0.17f);
                        waterlevel = 0.17f;
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(hasStarted){
            mWaveHelper.initAnimation(0f,0.73f);
            hasStarted = false;
        }

        if(pref.getBoolean("firstUse", true)){
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firstUse", false);
            editor.apply();
            startActivity(new Intent(this, TutorialActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    public void onBackPressed() {
        if(isHome){
            finish();
        }
        else{
            pager.setCurrentItem(0, true);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        if(pref.getInt("active", 0) == 0){
            thumb2.setVisibility(View.INVISIBLE);
            thumb.setVisibility(View.VISIBLE);
        }
        else{
            thumb2.setVisibility(View.VISIBLE);
            thumb.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homeButton:
                pager.setCurrentItem(0, true);
                break;
            case R.id.profileButton:
                pager.setCurrentItem(1, true);
                break;
            case R.id.prizesButton:
                pager.setCurrentItem(2, true);
                break;
            case R.id.settingButton:
                pager.setCurrentItem(3, true);
                break;
            case R.id.imageHam:
                startActivity(new Intent(this, PopActivity.class));
                break;
            case R.id.mySwitch:
                if(pref.getInt("active", 0) == 0){
                    SharedPreferences.Editor editor = pref.edit();
                    Intent mSerIntent = new Intent(this, YoobieService.class);
                    thumb2.setVisibility(View.VISIBLE);
                    thumb.setVisibility(View.INVISIBLE);
                    editor.putInt("active", 1);
                    mSerIntent.putExtra("active", 1);
                    startService(mSerIntent);
                    editor.apply();
                }
                else{
                    startActivity(new Intent(this, WarningActivity.class));
                }
                break;
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 0:
                    return new MainFragment();
                case 1:
                    return new ProfileFragment();
                case 2:
                    return new PrizeFragment();
                case 3:
                    return new ControlFragment();
                default:
                    return new MainFragment();
            }
        }
        @Override
        public int getCount() {
            return 4;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
