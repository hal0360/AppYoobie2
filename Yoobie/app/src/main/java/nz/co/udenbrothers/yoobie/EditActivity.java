package nz.co.udenbrothers.yoobie;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import nz.co.udenbrothers.yoobie.models.Member;
import nz.co.udenbrothers.yoobie.models.WaveHelper;
import nz.co.udenbrothers.yoobie.models.WaveView;
import nz.co.udenbrothers.yoobie.tools.RequestTask;

public class EditActivity extends AppCompatActivity implements View.OnClickListener ,AsynCallback{

    private Calendar myCalendar;
    private String gender;
    private EditText ed;
    private EditText ed2;
    private EditText ed7;
    private RadioButton radMale;
    private RadioButton radFemale;
    private TextView dobTxt;
    private Spinner spinner;
    public SharedPreferences pref;
    public WaveHelper mWaveHelper;
    private float startingY;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        pref = getSharedPreferences("app", MODE_PRIVATE);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        ed = (EditText)findViewById(R.id.editeditName);
        ed2 = (EditText)findViewById(R.id.editeditMobile);
        ed7 = (EditText)findViewById(R.id.editeditCity);
        radMale = (RadioButton)findViewById(R.id.radioeditMale);
        radFemale = (RadioButton)findViewById(R.id.radioeditFemale);
        radMale.setOnClickListener(this);
        radFemale.setOnClickListener(this);

        dobTxt = (TextView) findViewById(R.id.editeditDob);
        dobTxt.setOnClickListener(this);

        spinner = (Spinner) findViewById(R.id.editeditCountry);
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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allcountry){
            @Override
            public boolean isEnabled(int position){
                if(position == 0) return false;
                else return true;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView tiv = (TextView) v;
                if(position == 0) tiv.setTextColor(Color.WHITE);
                else tiv.setTextColor(Color.GREEN);
                return v;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0) tv.setTextColor(Color.GRAY);
                else tv.setTextColor(Color.BLACK);
                return view;
            }
        };

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        Button finButt = (Button)findViewById(R.id.FinishEditButton);
        finButt.setOnClickListener(this);
        myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.YEAR, 1999);
        myCalendar.set(Calendar.MONTH, 6);
        myCalendar.set(Calendar.DAY_OF_MONTH, 6);

        ed.setText(pref.getString("name", "N/A"));
        ed2.setText(pref.getString("mobile", "N/A"));
        dobTxt.setText(pref.getString("dob", "N/A"));
        ed7.setText(pref.getString("city", "N/A"));
        if(pref.getString("gender", "N/A").equals("male")){
            radMale.setChecked(true);
        }else{
            radFemale.setChecked(true);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        mWaveHelper = new WaveHelper((WaveView) findViewById(R.id.myWave3));
        mWaveHelper.initAnimation(0.7f,0.94f);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View viewCurrent = getCurrentFocus();
        if(viewCurrent == null) return super.dispatchTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                float deltaa = startingY - event.getRawY();
                if (deltaa > 100)
                {
                    TextView nextField = (TextView)viewCurrent.focusSearch(View.FOCUS_DOWN);
                    if(nextField != null) nextField.requestFocus();
                    else {
                        viewCurrent.clearFocus();
                        imm.hideSoftInputFromWindow(viewCurrent.getWindowToken(), 0);
                    }
                }
                else{
                    int scrcoords[] = new int[2];
                    viewCurrent.getLocationOnScreen(scrcoords);
                    float x = event.getRawX() + viewCurrent.getLeft() - scrcoords[0];
                    float y = event.getRawY() + viewCurrent.getTop() - scrcoords[1];
                    if (x < viewCurrent.getLeft() || x > viewCurrent.getRight() || y < viewCurrent.getTop() || y > viewCurrent.getBottom()) imm.hideSoftInputFromWindow(viewCurrent.getWindowToken(), 0);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                startingY = event.getRawY();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public void postCallback(String result){
        Toast.makeText(this,result, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FinishEditButton:
               /* String usr = ed.getText().toString().trim();
                String mob = ed2.getText().toString().trim();
                String bir = ed5.getText().toString().trim();
                String loc = ed6.getText().toString().trim();
                String loc2 = ed7.getText().toString().trim();
                //mainAct.hideSoftKeyboard();
                if (usr.equals("") || mob.equals("") || bir.equals("") || loc.equals("") || loc2.equals("")){
                    Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences pref = this.getSharedPreferences("app", Context.MODE_PRIVATE); //1
                Gson gson = new Gson();
                Member me = new Member(0, usr, pref.getString("password", "N/A"), pref.getString("email", "N/A"), gender, bir, loc, loc2, Build.MANUFACTURER + " " + android.os.Build.MODEL, mob);
                new RequestTask(this,this,gson.toJson(me)).execute("http://103.18.58.26/Alpha/users/modify"); */

                //ed7.setError("This field can not be blank");
                break;
            case R.id.radioeditMale:
                radFemale.setChecked(false);
                gender = "male";
                break;
            case R.id.radioeditFemale:
                radMale.setChecked(false);
                gender = "female";
                break;
            case R.id.editeditDob:
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
