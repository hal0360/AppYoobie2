package nz.co.udenbrothers.yoobie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Bundle b = getIntent().getExtras();
        TextView relay3 = (TextView) findViewById(R.id.MenuText);
        relay3.setText(b.getString("kiki"));
    }
}
