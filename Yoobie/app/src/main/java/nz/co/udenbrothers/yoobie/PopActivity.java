package nz.co.udenbrothers.yoobie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class PopActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.dimAmount = 0.5f;
        params.alpha = 0.9f;
        params.gravity = Gravity.TOP | Gravity.START;
        getWindow().setAttributes(params);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float dp = 280f;
        float fpixels = metrics.density * dp;
        int pixels = (int) (fpixels + 0.5f);
        getWindow().setLayout(pixels, WindowManager.LayoutParams.MATCH_PARENT);

        findViewById(R.id.howItWorkButton).setOnClickListener(this);
        findViewById(R.id.aboutButton).setOnClickListener(this);
        findViewById(R.id.contactButton).setOnClickListener(this);
        findViewById(R.id.termServButton).setOnClickListener(this);
        findViewById(R.id.privacyButton).setOnClickListener(this);

        ImageView imgPro = (ImageView) findViewById(R.id.imageAvatar2);
        SharedPreferences pref = getSharedPreferences("app", MODE_PRIVATE);
        if(pref.getInt("avatar", -1) > 0){
            imgPro.setImageResource(pref.getInt("avatar", -1));
        }
        TextView ttt = (TextView) findViewById(R.id.PopProfileName);
        ttt.setText(pref.getString("name", "N/A"));
    }

    public void close(View v){
        finish();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MenuActivity.class);
        Bundle b = new Bundle();
        switch (v.getId()) {
            case R.id.howItWorkButton:
                b.putString("kiki", "How It Works");
                break;
            case R.id.aboutButton:
                b.putString("kiki", "About");
                break;
            case R.id.termServButton:
                b.putString("kiki", "Terms of Service");
                break;
            case R.id.contactButton:
                b.putString("kiki", "Contact");
                break;
            case R.id.privacyButton:
                b.putString("kiki", "Privacy Policy");
                break;
        }
        intent.putExtras(b);
        startActivity(intent);
    }
}
