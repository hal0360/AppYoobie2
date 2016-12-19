package nz.co.udenbrothers.yoobie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import nz.co.udenbrothers.yoobie.models.Member;
import nz.co.udenbrothers.yoobie.tools.InternetService;
import nz.co.udenbrothers.yoobie.tools.RequestTask;
import nz.co.udenbrothers.yoobie.tools.UpdateReceiver;

public class LoginFragment extends Fragment implements View.OnClickListener, AsynCallback {

    private SignupActivity mainAct;
    private EditText ed, ed2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mainAct.mainLogo.setVisibility(View.VISIBLE);

        ed = (EditText)view.findViewById(R.id.editLogMail);
        ed2 = (EditText)view.findViewById(R.id.editLogPass);
        Button b = (Button) view.findViewById(R.id.LoginButton);
        b.setOnClickListener(this);
        TextView forgetPass = (TextView) view.findViewById(R.id.textForget);
        forgetPass.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mainAct = (SignupActivity)activity;
    }

    public void postCallback(String result){
        Gson gson = new Gson();
        try {
            Member myId = gson.fromJson(result, Member.class);
            SharedPreferences settings;
            SharedPreferences.Editor editor;
            settings = mainAct.getSharedPreferences("app", Context.MODE_PRIVATE);
            editor = settings.edit();
            editor.putInt("id", myId.id);
            editor.putString("name", myId.username);
            editor.putString("email", myId.email);
            editor.putString("dob", myId.date_of_birth);
            editor.putString("location", myId.country + ", " + myId.city);
            editor.putString("mobile", myId.mobile);
            editor.putString("gender", myId.gender);
            editor.putInt("active", 1);
            editor.apply();
            mainAct.startService(new Intent(mainAct, InternetService.class));
            UpdateReceiver ta = new UpdateReceiver();
            ta.starting(mainAct);
            Intent mSerIntent = new Intent(mainAct, YoobieService.class);
            mSerIntent.putExtra("active", 1);
            mainAct.startService(mSerIntent);
            startActivity(new Intent(mainAct, MainActivity.class));
            mainAct.finish();
        } catch (Exception e) {
            Toast.makeText(mainAct,result, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.LoginButton:
                String usr = ed.getText().toString().trim();
                String pass = ed2.getText().toString().trim();
                //mainAct.hideSoftKeyboard();
                Gson gson = new Gson();
                Member me = new Member(0, "dur", pass, usr, "dur", "dur", "dur", "dur", Build.MANUFACTURER + " " + android.os.Build.MODEL,"df");
                new RequestTask(mainAct,this,gson.toJson(me)).execute("http://103.18.58.26/Alpha/users/signin");
                break;
            case R.id.textForget:
                mainAct.mWaveHelper.paraAnimation(false);
                mainAct.replaceFrag(new ForgotFragment());
                mainAct.curFrag = 3;
                break;
        }
    }
}
