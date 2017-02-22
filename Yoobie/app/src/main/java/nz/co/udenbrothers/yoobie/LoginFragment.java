package nz.co.udenbrothers.yoobie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nz.co.udenbrothers.yoobie.models.LoginData;
import nz.co.udenbrothers.yoobie.models.Token;
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

        if(result.equals("denied")){
            ed.requestFocus();
            ed.setError("Email or password incorrect");
            return;
        }

        try {
            Token myTok = gson.fromJson(result, Token.class);
            SharedPreferences settings;
            SharedPreferences.Editor editor;
            settings = mainAct.getSharedPreferences("app", Context.MODE_PRIVATE);
            editor = settings.edit();
            editor.putString("authorization", Base64.encodeToString((myTok.userId + ":" + myTok.accessToken).getBytes("UTF-8"), Base64.NO_WRAP));
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
            Toast.makeText(mainAct,"Data error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.LoginButton:
                String usr = ed.getText().toString().trim();
                String pass = ed2.getText().toString().trim();

                Matcher m = Pattern.compile("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*$").matcher(usr);
                if (!m.matches( )) {
                    ed.requestFocus();
                    ed.setError("Invaid email");
                    return;
                }

                if (pass.length() < 6){
                    ed2.requestFocus();
                    ed2.setError("Must be at least 6 length");
                    ed2.setText("");
                    return;
                }

                Gson gson = new Gson();
                LoginData loginData = new LoginData(usr, pass, Build.MANUFACTURER + " " + android.os.Build.MODEL);
                new RequestTask(mainAct,this,"POST",gson.toJson(loginData),null).execute("http://yoobie-api.azurewebsites.net/login");
                break;
            case R.id.textForget:
                mainAct.mWaveHelper.paraAnimation(false);
                mainAct.replaceFrag(new ForgotFragment());
                mainAct.curFrag = 3;
                break;
        }
    }
}
