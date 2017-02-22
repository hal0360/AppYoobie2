package nz.co.udenbrothers.yoobie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import nz.co.udenbrothers.yoobie.models.Profile;
import nz.co.udenbrothers.yoobie.tools.RequestTask;

public class ProfileFragment extends Fragment implements View.OnClickListener, AsynCallback{

    private MainActivity mainAct;
    private TextView tttt, tv1, tv3, tv4, tv5, tv6;
    private ImageView imgPro;

    @Override
    public void onAttach(Context activity){
        super.onAttach(activity);
        mainAct = (MainActivity)activity;
    }

    private void getProfile(){
        new RequestTask(mainAct,this,"GET",null,mainAct.pref.getString("authorization", "N/A")).execute("http://yoobie-api.azurewebsites.net/profile");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        imgPro = (ImageView) v.findViewById(R.id.imgProfile);
        tv1 = (TextView) v.findViewById(R.id.profileEmailTxt);
        tv3 = (TextView) v.findViewById(R.id.profileMobileTxt);
        tv4 = (TextView) v.findViewById(R.id.profileDobTxt);
        tv5 = (TextView) v.findViewById(R.id.profileLocationTxt);
        tv6 = (TextView) v.findViewById(R.id.profileGenderTxt);
        tttt = (TextView) v.findViewById(R.id.EntryTxt);
        tttt.setText("N/A");
        ImageView ima1 = (ImageView) v.findViewById(R.id.editButton);
        ima1.setOnClickListener(this);
        imgPro.setOnClickListener(this);
        setupProfile();
        return v;
    }

    private void setupProfile(){
        tv1.setText(mainAct.profile.firstName);
        tv3.setText(mainAct.profile.phoneMobile);
        tv4.setText(mainAct.profile.dateOfBirth);
        if(!mainAct.profile.phoneOther.equals("00000000")){
            tv5.setText(mainAct.profile.phoneOther);
        }
        if(mainAct.profile.gender == 1){
            tv6.setText("Male");
        }
        else {
            tv6.setText("Female");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mainAct.pref.getInt("avatar", -1) > 0){
            imgPro.setImageResource(mainAct.pref.getInt("avatar", -1));
        }

        if(!mainAct.pref.getBoolean("hasPro", false)){
            mainAct.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getProfile();
                }
            }, 1000);
        }
    }

    public void postCallback(String result){

        if(result.equals("denied")){
            Toast.makeText(mainAct,"Login expired", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = mainAct.pref.edit();
        editor.putBoolean("hasPro", true);
        editor.apply();

        Gson gson = new Gson();
        try {
            mainAct.profile = gson.fromJson(result, Profile.class);
        } catch (Exception e) {
            mainAct.profile = new Profile();
            Toast.makeText(mainAct,"Data error", Toast.LENGTH_SHORT).show();
        }
        setupProfile();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editButton:
                Gson gson = new Gson();
                Intent intent = new Intent(mainAct, EditActivity.class);
                intent.putExtra("profileJson", gson.toJson(mainAct.profile));
                startActivity(intent);
                break;
            case R.id.imgProfile:
                startActivity(new Intent(mainAct, AvatarActivity.class));
                break;
        }
    }
}
