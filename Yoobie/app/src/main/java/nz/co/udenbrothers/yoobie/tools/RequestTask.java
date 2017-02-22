package nz.co.udenbrothers.yoobie.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import nz.co.udenbrothers.yoobie.AsynCallback;
import nz.co.udenbrothers.yoobie.models.Response;

public class RequestTask extends AsyncTask<String,String,Response>
{
    private AsynCallback mFrag;
    private Context mContext;
    private String uploadString;
    private ProgressDialog mDialog;
    private String method;
    private String aus;

    public RequestTask(Context conn, AsynCallback act, String meth, String content, String au) {
        mFrag = act;
        uploadString = content;
        method = meth;
        mContext = conn;
        aus = au;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Please wait...");
        mDialog.show();
    }

    @Override
    protected Response doInBackground(String... params) {
        return InternetService.MyHttpConnection(method, uploadString, params[0], aus);
    }

    @Override
    protected void onPostExecute(Response response) {
        if(response.statusCode == 204){
            Toast.makeText(mContext,"Operation succeeded", Toast.LENGTH_SHORT).show();
            mFrag.postCallback("ok");
        }
        else if(response.statusCode == 200){
            mFrag.postCallback(response.content);
        }
        else if(response.statusCode == 999){
            Toast.makeText(mContext,response.content, Toast.LENGTH_SHORT).show();
        }
        else if(response.statusCode == 401){
            mFrag.postCallback("denied");
        }
        else {
            Toast.makeText(mContext,"Error: " + response.statusCode, Toast.LENGTH_SHORT).show();
        }
        mDialog.dismiss();
    }
}
