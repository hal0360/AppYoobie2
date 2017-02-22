package nz.co.udenbrothers.yoobie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

public class WarningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        getWindow().setLayout((int) (metrics.widthPixels * .8), (int) (metrics.heightPixels * .60));
    }

    public void turnoff(View v){
        SharedPreferences pref = getSharedPreferences("app", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Intent mSerIntent = new Intent(this, YoobieService.class);
        editor.putInt("active", 0);
        mSerIntent.putExtra("active", 0);
        startService(mSerIntent);
        editor.apply();
        finish();
    }
}
