package nz.co.udenbrothers.yoobie;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nz.co.udenbrothers.yoobie.models.Profile;

import nz.co.udenbrothers.yoobie.models.WaveHelper;
import nz.co.udenbrothers.yoobie.models.WaveView;
import nz.co.udenbrothers.yoobie.tools.RequestTask;

public class EditActivity extends AppCompatActivity implements View.OnClickListener ,AsynCallback{

    private Calendar myCalendar;
    private EditText ed;
    private EditText ed2;
    private EditText ed3;
    private EditText ed4;
    private RadioButton radMale;
    private RadioButton radFemale;
    private TextView dobTxt;
    public SharedPreferences pref;
    public WaveHelper mWaveHelper;
    private float startingY;
    private InputMethodManager imm;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Gson gson = new Gson();
        profile = gson.fromJson(getIntent().getStringExtra("profileJson"), Profile.class);
        pref = getSharedPreferences("app", MODE_PRIVATE);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ed = (EditText)findViewById(R.id.editeditFirst);
        ed2 = (EditText)findViewById(R.id.editeditLast);
        ed3 = (EditText)findViewById(R.id.editeditMobile);
        ed4 = (EditText)findViewById(R.id.editeditOther);
        radMale = (RadioButton)findViewById(R.id.radioeditMale);
        radFemale = (RadioButton)findViewById(R.id.radioeditFemale);
        radMale.setOnClickListener(this);
        radFemale.setOnClickListener(this);
        dobTxt = (TextView) findViewById(R.id.editeditDob);
        dobTxt.setOnClickListener(this);
        Button finButt = (Button)findViewById(R.id.FinishEditButton);
        finButt.setOnClickListener(this);
        myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.YEAR, 1999);
        myCalendar.set(Calendar.MONTH, 6);
        myCalendar.set(Calendar.DAY_OF_MONTH, 6);
        ed.setText(profile.firstName);
        ed2.setText(profile.lastName);
        ed3.setText(profile.phoneMobile);
        if(!profile.phoneOther.equals("00000000")){
            ed4.setText(profile.phoneOther);
        }
        dobTxt.setText(profile.dateOfBirth);
        if(profile.gender == 1){
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

        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("hasPro", false);
        editor.apply();

        Toast.makeText(this,result, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FinishEditButton:
                String first = ed.getText().toString().trim();
                String last = ed2.getText().toString().trim();
                String mob = ed3.getText().toString().trim();
                String other = ed4.getText().toString().trim();
                String dob = dobTxt.getText().toString().trim();

                if (first.equals("")){
                    ed.requestFocus();
                    ed.setError("Invaid first anme");
                    return;
                }

                if (last.equals("")){
                    ed2.requestFocus();
                    ed2.setError("Invaid last  anme");
                    return;
                }

                Matcher matcher = Pattern.compile("^[0-9]{8,12}$").matcher(mob);
                if (!matcher.matches( )) {
                    ed3.requestFocus();
                    ed3.setError("Invaid phone number");
                    return;
                }

                if (!matcher.matches( )) {
                    ed4.requestFocus();
                    ed4.setError("Invaid phone number");
                    return;
                }

                profile.firstName = first;
                profile.lastName = last;
                profile.phoneMobile = mob;
                profile.phoneOther = other;
                profile.dateOfBirth = dob;

                SharedPreferences pref = this.getSharedPreferences("app", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                new RequestTask(this,this,"PUT",gson.toJson(profile),pref.getString("authorization", "N/A")).execute("http://yoobie-api.azurewebsites.net/profile");
                break;
            case R.id.radioeditMale:
                radFemale.setChecked(false);
                profile.gender = 1;
                break;
            case R.id.radioeditFemale:
                radMale.setChecked(false);
                profile.gender = 2;
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
