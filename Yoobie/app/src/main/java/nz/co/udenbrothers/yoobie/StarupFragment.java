package nz.co.udenbrothers.yoobie;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StarupFragment extends Fragment implements View.OnClickListener{

    private SignupActivity mainAct;
    private TextView tvDay, tvHour, tvMinute, tvSecond;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_starup, container, false);

        mainAct.mainLogo.setVisibility(View.VISIBLE);

        tvDay = (TextView) view.findViewById(R.id.txtTimerDay);
        tvHour = (TextView) view.findViewById(R.id.txtTimerHour);
        tvMinute = (TextView) view.findViewById(R.id.txtTimerMinute);
        tvSecond = (TextView) view.findViewById(R.id.txtTimerSecond);
        countDownStart();
        TextView logtxt = (TextView) view.findViewById(R.id.loginText);
        logtxt.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            logtxt.setText(Html.fromHtml("Already have an account? <b>Sign in</b>", Html.FROM_HTML_MODE_LEGACY));
        }
        else {
            logtxt.setText(Html.fromHtml("Already have an account? <b>Sign in</b>"));
        }
        view.findViewById(R.id.emailJoinButton).setOnClickListener(this);
        view.findViewById(R.id.facebookButton).setOnClickListener(this);
        return view;
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
        mainAct = (SignupActivity) activity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emailJoinButton:
                mainAct.mWaveHelper.paraAnimation(false);
                mainAct.replaceFrag(new RegisterOneFragment());
                mainAct.curFrag = 1;
                break;
            case R.id.facebookButton:
                Toast.makeText(mainAct, "Under construction", Toast.LENGTH_SHORT).show();
                break;
            case R.id.loginText:
                mainAct.mWaveHelper.paraAnimation(false);
                mainAct.replaceFrag(new LoginFragment());
                mainAct.curFrag = 1;
                break;
        }
    }
}
