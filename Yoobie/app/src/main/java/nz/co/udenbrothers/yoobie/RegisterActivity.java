package nz.co.udenbrothers.yoobie;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import nz.co.udenbrothers.yoobie.models.Member;
import nz.co.udenbrothers.yoobie.tools.InternetService;
import nz.co.udenbrothers.yoobie.tools.RequestTask;
import nz.co.udenbrothers.yoobie.tools.UpdateReceiver;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener , AsynCallback{

    private Calendar myCalendar;
    private String gender;
    private EditText ed;
    private EditText ed2;
    private EditText ed5;
    private EditText ed6;
    private EditText ed7;
    private RadioButton radMale;
    private RadioButton radFemale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        gender = "male";
        ed = (EditText)findViewById(R.id.editRegName);
        ed2 = (EditText)findViewById(R.id.editRegMobile);
        ed5 = (EditText)findViewById(R.id.editRegDob);
        ed6 = (EditText)findViewById(R.id.editRegCountry);
        ed7 = (EditText)findViewById(R.id.editRegCity);
        radMale = (RadioButton)findViewById(R.id.radioMale);
        radFemale = (RadioButton)findViewById(R.id.radioFemale);
        Button finButt = (Button)findViewById(R.id.FinishButton);
        finButt.setOnClickListener(this);
        myCalendar = Calendar.getInstance();
        ed5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        ed5.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(v.getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void postCallback(String result){
        Gson gson = new Gson();
        try {
            Member myId = gson.fromJson(result, Member.class);
            SharedPreferences settings;
            SharedPreferences.Editor editor;
            settings = getSharedPreferences("app", Context.MODE_PRIVATE);
            editor = settings.edit();
            editor.putInt("id", myId.id);
            editor.putString("name", myId.username);
            editor.putString("email", myId.email);
            editor.putString("dob", myId.date_of_birth);
            editor.putString("location", myId.country + ", " + myId.city);
            editor.putString("mobile", myId.mobile);
            editor.putString("gender", myId.gender);
            editor.putInt("active", 1);
            editor.commit();
            startService(new Intent(this, InternetService.class));
            UpdateReceiver ta = new UpdateReceiver();
            ta.starting(this);
            Intent mSerIntent = new Intent(this, YoobieService.class);
            mSerIntent.putExtra("active", 1);
            startService(mSerIntent);
            startActivity(new Intent(this, MainActivity.class));
           /// mainAct.finish();
        } catch (Exception e) {
          //  Toast.makeText(mainAct,result, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FinishButton:
                String usr = ed.getText().toString().trim();
                String mob = ed2.getText().toString().trim();
                String bir = ed5.getText().toString().trim();
                String loc = ed6.getText().toString().trim();
                String loc2 = ed7.getText().toString().trim();
               //hideSoftKeyboard();
                if (usr.equals("") || mob.equals("") || bir.equals("") || loc.equals("") || loc2.equals("")){
                  //  Toast.makeText(mainAct, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences pref = getSharedPreferences("app", Context.MODE_PRIVATE); //1
                Gson gson = new Gson();
                Member me = new Member(0, usr, pref.getString("password", "N/A"), pref.getString("email", "N/A"), gender, bir, loc, loc2, Build.MANUFACTURER + " " + android.os.Build.MODEL, mob);
                new RequestTask(this,this,gson.toJson(me)).execute("http://103.18.58.26/Alpha/users/signin");
                break;
            case R.id.radioMale:
                radFemale.setChecked(false);
                gender = "male";
                break;
            case R.id.radioFemale:
                radMale.setChecked(false);
                gender = "female";
                break;
        }
    }
}
