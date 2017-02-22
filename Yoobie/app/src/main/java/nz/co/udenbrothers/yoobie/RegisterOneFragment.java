package nz.co.udenbrothers.yoobie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nz.co.udenbrothers.yoobie.tools.RequestTask;

public class RegisterOneFragment extends Fragment implements View.OnClickListener, AsynCallback{

    private SignupActivity mainAct;
    private EditText ed;
    private EditText ed2;
    private EditText ed3;
    private CheckBox terms;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_one, container, false);
        mainAct.logoContain.setVisibility(View.VISIBLE);
        ed = (EditText)view.findViewById(R.id.editRegMail);
        ed2 = (EditText)view.findViewById(R.id.editRegPass);
        ed3 = (EditText)view.findViewById(R.id.editRegPass2);
        terms = (CheckBox) view.findViewById(R.id.termCond);
        terms.setOnClickListener(this);
        terms.setText(Html.fromHtml("I agree to the <b>Terms and Conditions</b>"));
        if(mainAct.pref.getBoolean("accepted", false)){
            terms.setChecked(true);
        }
        Button account = (Button) view.findViewById(R.id.AccountButton);
        account.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mainAct = (SignupActivity) activity;
    }

    public void postCallback(String result){
        if(result.equals("true")){
            mainAct.mWaveHelper.initAnimation(0.62f,0.95f);
            mainAct.replaceFrag(new RegisterTwoFragment());
            mainAct.curFrag = 2;
        }
        else if(result.equals("false")){
            ed.requestFocus();
            ed.setError("Email already in use.");
        }
        else {
            Toast.makeText(mainAct,"Data error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.termCond:
                if(terms.isChecked()) {
                    startActivity(new Intent(mainAct, TermActivity.class));
                }
                else {
                    SharedPreferences.Editor editor = mainAct.pref.edit();
                    editor.putBoolean("accepted", false);
                    editor.apply();
                }
                break;
            case R.id.AccountButton:
                String usr = ed.getText().toString().trim();
                String pass = ed2.getText().toString().trim();
                String pass2 = ed3.getText().toString().trim();

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

                if (!pass.equals(pass2)){
                    ed3.requestFocus();
                    Toast.makeText(mainAct, "Password not match", Toast.LENGTH_SHORT).show();
                    ed3.setError("Password not match");
                    ed3.setText("");
                    return;
                }

                if (!terms.isChecked()){
                    Toast.makeText(mainAct, "Please agree to terms and conditions", Toast.LENGTH_SHORT).show();
                    return;
                }

                new RequestTask(mainAct,this,"GET",null,null).execute("http://yoobie-api.azurewebsites.net/registration/email-available?email=" + usr);
                mainAct.newUsr.email = usr;
                mainAct.newUsr.password = pass;
                break;
        }
    }
}