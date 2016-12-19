package nz.co.udenbrothers.yoobie;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.annotation.NonNull;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import nz.co.udenbrothers.yoobie.models.Member;
import nz.co.udenbrothers.yoobie.tools.InternetService;
import nz.co.udenbrothers.yoobie.tools.RequestTask;
import nz.co.udenbrothers.yoobie.tools.UpdateReceiver;


public class RegisterTwoFragment extends Fragment implements View.OnClickListener , AsynCallback{

    private SignupActivity mainAct;
    private Calendar myCalendar;
    private String gender;
    private EditText ed;
    private EditText ed3;
    private EditText ed2;
    private EditText ed7;
    private RadioButton radMale;
    private RadioButton radFemale;
    private TextView dobTxt;
    private Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_two, container, false);
        gender = "male";
        mainAct.logoContain.setVisibility(View.INVISIBLE);

        dobTxt = (TextView) view.findViewById(R.id.editRegDob);
        dobTxt.setOnClickListener(this);

        spinner = (Spinner) view.findViewById(R.id.editRegCountry);
        ArrayList<String> allcountry = new ArrayList<>();
        allcountry.add("Select Country");
        Locale[] locale = Locale.getAvailableLocales();
        String countryy;
        for( Locale loc : locale ){
            countryy = loc.getDisplayCountry();
            if( countryy.length() > 0 && !allcountry.contains(countryy) ){
                allcountry.add( countryy );
            }
        }
        Collections.sort(allcountry);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mainAct, android.R.layout.simple_spinner_item, allcountry){
            @Override
            public boolean isEnabled(int position){
                if(position == 0) return false;
                else return true;
            }
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView tiv = (TextView) v;
                if(position == 0) tiv.setTextColor(Color.WHITE);
                else tiv.setTextColor(Color.parseColor("#FFFF00"));
                return v;
            }
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0) tv.setTextColor(Color.GRAY);
                else tv.setTextColor(Color.BLACK);
                return view;
            }
        };

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        ed = (EditText)view.findViewById(R.id.editRegFirst);
        ed2 = (EditText)view.findViewById(R.id.editRegMobile);
        ed3 = (EditText)view.findViewById(R.id.editRegLast);
        //ed6 = (EditText)view.findViewById(R.id.editRegCountry);
        ed7 = (EditText)view.findViewById(R.id.editRegCity);
        radMale = (RadioButton)view.findViewById(R.id.radioMale);
        radFemale = (RadioButton)view.findViewById(R.id.radioFemale);
        Button finButt = (Button)view.findViewById(R.id.FinishButton);

        TextView tted = (TextView)view.findViewById(R.id.addEmailText);
        tted.setText(mainAct.pref.getString("email", "N/A"));

        radMale.setOnClickListener(this);
        radFemale.setOnClickListener(this);
        finButt.setOnClickListener(this);
        myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.YEAR, 1999);
        myCalendar.set(Calendar.MONTH, 6);
        myCalendar.set(Calendar.DAY_OF_MONTH, 6);
        return view;
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
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mainAct = (SignupActivity) activity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FinishButton:
                String usr = ed.getText().toString().trim();
                String mob = ed2.getText().toString().trim();
                String bir = dobTxt.getText().toString().trim();
                String loc = spinner.getSelectedItem().toString();
                String loc2 = ed7.getText().toString().trim();

                if (usr.equals("") || mob.equals("") || bir.equals("Date Of Birth") || loc.equals("Select Country") || loc2.equals("")){
                    Toast.makeText(mainAct, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                Gson gson = new Gson();
                Member me = new Member(0, usr, mainAct.pref.getString("password", "N/A"), mainAct.pref.getString("email", "N/A"), gender, bir, loc, loc2, Build.MANUFACTURER + " " + android.os.Build.MODEL, mob);
                new RequestTask(mainAct,this,gson.toJson(me)).execute("http://103.18.58.26/Alpha/users/signin");
                break;
            case R.id.radioMale:
                radFemale.setChecked(false);
                gender = "male";
                break;
            case R.id.radioFemale:
                radMale.setChecked(false);
                gender = "female";
                break;
            case R.id.editRegDob:
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        dobTxt.setText(sdf.format(myCalendar.getTime()));
                        dobTxt.setTextColor(Color.parseColor("#FFFF00"));
                    }
                };
                new DatePickerDialog(v.getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
        }
    }
}
