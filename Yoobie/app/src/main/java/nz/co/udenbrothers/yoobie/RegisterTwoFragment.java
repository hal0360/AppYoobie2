package nz.co.udenbrothers.yoobie;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nz.co.udenbrothers.yoobie.models.Country;
import nz.co.udenbrothers.yoobie.models.Region;
import nz.co.udenbrothers.yoobie.models.Token;
import nz.co.udenbrothers.yoobie.tools.CountryAdaptor;
import nz.co.udenbrothers.yoobie.tools.InternetService;
import nz.co.udenbrothers.yoobie.tools.RegionAdaptor;
import nz.co.udenbrothers.yoobie.tools.RequestTask;
import nz.co.udenbrothers.yoobie.tools.UpdateReceiver;


public class RegisterTwoFragment extends Fragment implements View.OnClickListener, AsynCallback{

    private SignupActivity mainAct;
    private Calendar myCalendar;
    private EditText ed;
    private EditText ed3;
    private EditText ed2;
    private EditText ed4;
    private RadioButton radMale;
    private RadioButton radFemale;
    private TextView dobTxt;
    private Spinner spinner;
    private CountryAdaptor countryAdaptor;
    private Spinner spinner2;
    private RegionAdaptor regionAdaptor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_two, container, false);
        mainAct.logoContain.setVisibility(View.INVISIBLE);
        dobTxt = (TextView) view.findViewById(R.id.editRegDob);
        dobTxt.setOnClickListener(this);
        spinner = (Spinner) view.findViewById(R.id.editRegCountry);
        spinner2 = (Spinner) view.findViewById(R.id.editRegCity);
        ed = (EditText)view.findViewById(R.id.editRegFirst);
        ed2 = (EditText)view.findViewById(R.id.editRegMobile);
        ed4 = (EditText)view.findViewById(R.id.editRegMobileCode);
        ed3 = (EditText)view.findViewById(R.id.editRegLast);
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
        myCalendar.set(Calendar.MONTH, 9);
        myCalendar.set(Calendar.DAY_OF_MONTH, 9);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        countryAdaptor = new CountryAdaptor(mainAct, android.R.layout.simple_spinner_item, mainAct.countries);
        countryAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(countryAdaptor);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Country contry = countryAdaptor.getItem(position);
                if (contry != null) {
                    mainAct.newUsr.countryId = contry.id;
                    ed4.setText(contry.callingCode);
                    regionAdaptor = new RegionAdaptor(mainAct, android.R.layout.simple_spinner_item, contry.allRegion);
                    regionAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner2.setAdapter(regionAdaptor);
                    spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            Region region = regionAdaptor.getItem(position);
                            if (region != null) {
                                mainAct.newUsr.countryRegionId = region.id;
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapter) {  }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });
    }

    public void postCallback(String result){

        Gson gson = new Gson();
        if(result.equals("false")){
            ed2.requestFocus();
            ed2.setError("Phone  already in use.");
        }
        else if(result.equals("true")){
            new RequestTask(mainAct,this,"POST",gson.toJson(mainAct.newUsr),null).execute("http://yoobie-api.azurewebsites.net/registration");
        }
        else{
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
                String usr2 = ed3.getText().toString().trim();
                String mob = ed2.getText().toString().trim();
                String bir = dobTxt.getText().toString().trim();

                if (usr.equals("")){
                    ed.requestFocus();
                    ed.setError("Invaid first anme");
                    return;
                }

                if (usr2.equals("")){
                    ed3.requestFocus();
                    ed3.setError("Invaid last anme");
                    return;
                }

                Matcher matcher = Pattern.compile("^[0-9]{8,12}$").matcher(mob);
                if (!matcher.matches( )) {
                    ed2.requestFocus();
                    ed2.setError("Invaid phone number");
                    return;
                }

                if (bir.equals("Date Of Birth")){
                    Toast.makeText(mainAct, "Please select a birth date", Toast.LENGTH_SHORT).show();
                    return;
                }

                mainAct.newUsr.firstName = usr;
                mainAct.newUsr.lastName = usr2;
                mainAct.newUsr.phoneMobile = mob;
                mainAct.newUsr.dateOfBirth = bir;
                mainAct.newUsr.deviceModel = Build.MANUFACTURER + " " + android.os.Build.MODEL;
                new RequestTask(mainAct,this,"GET",null,null).execute("http://yoobie-api.azurewebsites.net/registration/phone-mobile-available?phone=" + mob);
                break;
            case R.id.radioMale:
                radFemale.setChecked(false);
                mainAct.newUsr.gender = 1;
                break;
            case R.id.radioFemale:
                radMale.setChecked(false);
                mainAct.newUsr.gender = 2;
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
