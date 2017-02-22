package nz.co.udenbrothers.yoobie.tools;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;

import nz.co.udenbrothers.yoobie.SignupActivity;
import nz.co.udenbrothers.yoobie.models.Country;
import nz.co.udenbrothers.yoobie.models.Region;
import nz.co.udenbrothers.yoobie.models.Response;


public class DataTask extends AsyncTask<String,String,String>
{
    private SignupActivity mContext;

    public DataTask(SignupActivity conn) {
        mContext = conn;
    }

    @Override
    protected String doInBackground(String... params) {

        Response response = InternetService.MyHttpConnection("GET", null, "http://yoobie-api.azurewebsites.net/registration/country", null);
        if(response.statusCode == 200){
            mContext.countries = tryParse(response.content);
            if(mContext.countries != null){
                for (Country cunt : mContext.countries) {
                    Response regJson = InternetService.MyHttpConnection("GET", null, "http://yoobie-api.azurewebsites.net/registration/country/" + cunt.id + "/region", null);
                    cunt.allRegion = tryParse2(regJson.content);
                }
                return "Data ready";
            }
            else {
                return "Data not found";
            }
        }
        else {
            return "Error " + response.statusCode;
        }
    }

    private Region[] tryParse2(String text) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(text, Region[].class);
        } catch (Exception e) {
            return null;
        }
    }

    private Country[] tryParse(String text) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(text, Country[].class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(mContext,result, Toast.LENGTH_SHORT).show();
    }
}
