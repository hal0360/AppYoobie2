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

public class RegisterOneFragment extends Fragment implements View.OnClickListener {

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

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = mainAct.pref.edit();
        switch (v.getId()) {
            case R.id.termCond:
                if(terms.isChecked()) {
                    startActivity(new Intent(mainAct, TermActivity.class));

                }
                else {
                    editor.putBoolean("accepted", false);
                }
                break;
            case R.id.AccountButton:
                String usr = ed.getText().toString().trim();
                String pass = ed2.getText().toString().trim();
                String pass2 = ed3.getText().toString().trim();
                //mainAct.hideSoftKeyboard();
                if (usr.equals("") || pass.equals("") || !terms.isChecked()){
                    Toast.makeText(mainAct, "Please fill all fields and agree to terms and conditions", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pass.equals(pass2)){
                    Toast.makeText(mainAct, "Password not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                mainAct.mWaveHelper.initAnimation(0.62f,0.95f);
                editor.putString("email", usr);
                editor.putString("password", pass);
                mainAct.replaceFrag(new RegisterTwoFragment());
                mainAct.curFrag = 2;
                break;
        }
        editor.apply();
    }
}