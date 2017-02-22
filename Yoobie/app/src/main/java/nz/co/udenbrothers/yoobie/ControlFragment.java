package nz.co.udenbrothers.yoobie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;

import nz.co.udenbrothers.yoobie.tools.MySQLiteHelper;
import nz.co.udenbrothers.yoobie.tools.RequestTask;
import nz.co.udenbrothers.yoobie.tools.UpdateReceiver;

public class ControlFragment extends Fragment implements View.OnClickListener, AsynCallback{

    private MainActivity mainAct;
    private RadioButton radSlide;
    private RadioButton radZoom;
    private RadioButton radFade;
    private RadioButton radBefore;
    private RadioButton radAfter;
    private CheckBox wifiBox;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mainAct = (MainActivity)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        radSlide = (RadioButton) view.findViewById(R.id.radioButtonSlide);
        radSlide.setOnClickListener(this);
        radZoom = (RadioButton) view.findViewById(R.id.radioButtonZoom);
        radZoom.setOnClickListener(this);
        radFade = (RadioButton) view.findViewById(R.id.radioButtonFade);
        radFade.setOnClickListener(this);

        radBefore = (RadioButton) view.findViewById(R.id.radioButtonBefore);
        radBefore.setOnClickListener(this);
        radAfter = (RadioButton) view.findViewById(R.id.radioButtonAfter);
        radAfter.setOnClickListener(this);
        view.findViewById(R.id.logoutButton).setOnClickListener(this);

        wifiBox = (CheckBox) view.findViewById(R.id.checkBoxWifi);
        wifiBox.setOnClickListener(this);

        /*
        SeekBar seekBar = (SeekBar)view.findViewById(R.id.seekControlBar);
        if(mainAct.pref.getInt("lifespan", 3) == 2){
            seekBar.setProgress(0);
        }
        else if(mainAct.pref.getInt("lifespan", 3) == 3){
            seekBar.setProgress(50);
        }
        else{
            seekBar.setProgress(100);
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = mainAct.pref.edit();
                int flcl = seekBar.getProgress();
                if(flcl <= 25){
                    seekBar.setProgress(0);
                    editor.putInt("lifespan", 2000);
                }
                else if(25 < flcl && flcl < 75){
                    seekBar.setProgress(50);
                    editor.putInt("lifespan", 3000);
                }
                else {
                    seekBar.setProgress(100);
                    editor.putInt("lifespan", 4000);
                }
                editor.commit();
            }
        });
   */

        if(mainAct.pref.getInt("animation", 0) == 0){
            radSlide.setChecked(true);
        }
        else if(mainAct.pref.getInt("animation", 0) == 1){
            radZoom.setChecked(true);
        }
        else{
            radFade.setChecked(true);
        }

        if(mainAct.pref.getInt("priority", 0) == 0){
            radBefore.setChecked(true);
        }
        else{
            radAfter.setChecked(true);
        }

        if(mainAct.pref.getBoolean("wifi", false)){
            wifiBox.setChecked(true);
        }
        else{
            wifiBox.setChecked(false);
        }

        return view;
    }

    public void postCallback(String result){
        SharedPreferences.Editor editor = mainAct.pref.edit();
        editor.putString("authorization", "N/A");
        editor.putInt("active", 0);
        editor.apply();
        Toast.makeText(mainAct, "Logout successful. All data cleared", Toast.LENGTH_SHORT).show();
        UpdateReceiver ta = new UpdateReceiver();
        ta.cancelling(mainAct);
        Intent mSerIntenttt = new Intent(mainAct, YoobieService.class);
        mSerIntenttt.putExtra("active", 0);
        mSerIntenttt.putExtra("priority", 0);
        mainAct.startService(mSerIntenttt);

        File filess = mainAct.getExternalFilesDir(null);
        if (filess != null) {
            File[] filenames = filess.listFiles();
            for (File tmpf : filenames) {
                tmpf.delete();
            }
        }

        MySQLiteHelper dbHandler = MySQLiteHelper.getInstance(mainAct);
        dbHandler.clearStamp();
        dbHandler.clearImage();
        startActivity(new Intent(mainAct, SignupActivity.class));
        mainAct.finish();
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = mainAct.pref.edit();
        switch (v.getId()) {
            case R.id.radioButtonSlide:
                radFade.setChecked(false);
                radZoom.setChecked(false);
                editor.putInt("animation", 0);
                break;
            case R.id.radioButtonZoom:
                radFade.setChecked(false);
                radSlide.setChecked(false);
                editor.putInt("animation", 1);
                break;
            case R.id.radioButtonFade:
                radZoom.setChecked(false);
                radSlide.setChecked(false);
                editor.putInt("animation", 2);
                break;
            case R.id.radioButtonBefore:
                Intent mSerIntent = new Intent(mainAct, YoobieService.class);
                mSerIntent.putExtra("priority", 0);
                mainAct.startService(mSerIntent);
                radAfter.setChecked(false);
                editor.putInt("priority", 0);
                break;
            case R.id.radioButtonAfter:
                Intent mSerIntentt = new Intent(mainAct, YoobieService.class);
                mSerIntentt.putExtra("priority", 1);
                mainAct.startService(mSerIntentt);
                radBefore.setChecked(false);
                editor.putInt("priority", 1);
                break;
            case R.id.checkBoxWifi:
                if(wifiBox.isChecked()){
                    wifiBox.setChecked(false);
                    editor.putBoolean("wifi", false);
                }
                else {
                    wifiBox.setChecked(true);
                    editor.putBoolean("wifi", true);
                }
                break;
            case R.id.logoutButton:
                new RequestTask(mainAct,this,"POST",null,mainAct.pref.getString("authorization", "N/A")).execute("http://yoobie-api.azurewebsites.net/logout");
                break;
        }
        editor.apply();
    }
}
