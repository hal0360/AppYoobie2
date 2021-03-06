package nz.co.udenbrothers.yoobie;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class LastImageActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_image);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        getWindow().setLayout((int) (metrics.widthPixels * .8), (int) (metrics.heightPixels * .85));

        SharedPreferences pref = getSharedPreferences("app", MODE_PRIVATE);
        GifImageView ima = (GifImageView) findViewById(R.id.imageLastView);
        String imgType = pref.getString("lastImgType", "no");
        String imgNo= pref.getString("lastImgNo", "N/A");
        try{
            if(imgType.equals("gif")){
                ima.setImageDrawable(new GifDrawable(getExternalFilesDir(null) + "/" + imgNo + ".gif"));
            }
            else {
                ima.setImageDrawable(Drawable.createFromPath(getExternalFilesDir(null) + "/" + imgNo + "." + imgType));
            }
        }
        catch (Exception e){
            ima.setImageResource(R.drawable.notaval);
        }
    }
}
