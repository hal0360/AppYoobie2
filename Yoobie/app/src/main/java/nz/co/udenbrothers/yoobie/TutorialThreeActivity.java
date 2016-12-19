package nz.co.udenbrothers.yoobie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class TutorialThreeActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_three);
        RelativeLayout thislayout = (RelativeLayout) findViewById(R.id.tutorial3);
        thislayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
