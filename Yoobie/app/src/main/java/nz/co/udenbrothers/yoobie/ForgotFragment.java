package nz.co.udenbrothers.yoobie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nz.co.udenbrothers.yoobie.tools.RequestTask;

public class ForgotFragment extends Fragment implements View.OnClickListener, AsynCallback{

    public ForgotFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot, container, false);
    }

    public void postCallback(String result){


    }

    @Override
    public void onClick(View v) {
     //   new RequestTask(mainAct,this,"GET",null,mainAct.pref.getString("authorization", "N/A")).execute("http://yoobie-api.azurewebsites.net/profile");
    }

}
