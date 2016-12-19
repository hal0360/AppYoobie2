package nz.co.udenbrothers.yoobie;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import nz.co.udenbrothers.yoobie.models.Member;
import nz.co.udenbrothers.yoobie.models.User;
import nz.co.udenbrothers.yoobie.tools.RequestTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener, AsynCallback{

    private MainActivity mainAct;
    private TextView tttt;
    private ImageView imgPro;

    @Override
    public void onAttach(Context activity){
        super.onAttach(activity);
        mainAct = (MainActivity)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

  //      if(mainAct.hasEntry){
     //       new RequestTask(mainAct,this,"ssd").execute("http://103.18.58.26/Alpha/image-stamps/getEntry/" + mainAct.pref.getInt("id", -1));
   //         mainAct.hasEntry = false;
    //    }

        imgPro = (ImageView) v.findViewById(R.id.imgProfile);
        TextView ttt = (TextView) v.findViewById(R.id.profileEmailTxt);
        ttt.setText(mainAct.pref.getString("email", "N/A"));
        ttt = (TextView) v.findViewById(R.id.profileNameTxt);
        ttt.setText(mainAct.pref.getString("name", "N/A"));
        ttt = (TextView) v.findViewById(R.id.profileMobileTxt);
        ttt.setText(mainAct.pref.getString("mobile", "N/A"));
        ttt = (TextView) v.findViewById(R.id.profileDobTxt);
        ttt.setText(mainAct.pref.getString("dob", "N/A"));
        ttt = (TextView) v.findViewById(R.id.profileLocationTxt);
        ttt.setText(mainAct.pref.getString("location", "N/A"));
        ttt = (TextView) v.findViewById(R.id.profileGenderTxt);
        ttt.setText(mainAct.pref.getString("gender", "N/A"));
        tttt = (TextView) v.findViewById(R.id.EntryTxt);
        ImageView ima1 = (ImageView) v.findViewById(R.id.editButton);
        ima1.setOnClickListener(this);
        imgPro.setOnClickListener(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mainAct.pref.getInt("avatar", -1) > 0){
            imgPro.setImageResource(mainAct.pref.getInt("avatar", -1));
        }
    }

    public void postCallback(String result){
        tttt.setText(result);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editButton:
                startActivity(new Intent(mainAct, EditActivity.class));
                break;
            case R.id.imgProfile:
                startActivity(new Intent(mainAct, AvatarActivity.class));
                break;
        }
    }
}
