package nz.co.udenbrothers.yoobie;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import java.lang.reflect.Field;

public class AvatarActivity extends AppCompatActivity {

    public SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        pref = getSharedPreferences("app", MODE_PRIVATE);
    }

    public void getResId(View view) {
        ImageButton imgButt = (ImageButton) view;
        String dooby = imgButt.getContentDescription().toString();
        int resid = getesId(dooby, R.drawable.class);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("avatar", resid);
        editor.apply();
        finish();
    }

    public int getesId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            return -1;
        }
    }
}
