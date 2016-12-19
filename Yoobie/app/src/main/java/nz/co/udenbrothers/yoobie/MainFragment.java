package nz.co.udenbrothers.yoobie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainFragment extends Fragment implements View.OnClickListener{

    private MainActivity mainAct;
    private TextView tvDay, tvHour, tvMinute, tvSecond;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        tvDay = (TextView) v.findViewById(R.id.txtTimerDay2);
        tvHour = (TextView) v.findViewById(R.id.txtTimerHour2);
        tvMinute = (TextView) v.findViewById(R.id.txtTimerMinute2);
        tvSecond = (TextView) v.findViewById(R.id.txtTimerSecond2);
        countDownStart();
        ImageView ima1 = (ImageView) v.findViewById(R.id.lastTileButton);
        ima1.setOnClickListener(this);
        return v;
    }

    public void countDownStart() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mainAct.handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    // Here Set your Event Date
                    Date futureDate = dateFormat.parse("2017-11-25");
                    Date currentDate = new Date();
                    if (!currentDate.after(futureDate)) {
                        long diff = futureDate.getTime() - currentDate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;
                        tvDay.setText(String.format(Locale.US,"%02d", days));
                        tvHour.setText(String.format(Locale.US,"%02d", hours));
                        tvMinute.setText(String.format(Locale.US,"%02d", minutes));
                        tvSecond.setText(String.format(Locale.US,"%02d", seconds));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mainAct.handler.postDelayed(runnable, 0);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mainAct = (MainActivity) activity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lastTileButton:
                startActivity(new Intent(mainAct, LastImageActivity.class));
                break;
        }
    }

}
