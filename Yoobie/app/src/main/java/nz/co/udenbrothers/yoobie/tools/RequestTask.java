package nz.co.udenbrothers.yoobie.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import nz.co.udenbrothers.yoobie.AsynCallback;

public class RequestTask extends AsyncTask<String,String,String>
{
    private AsynCallback mFrag;
    private Context mContext;
    private String uploadString;
    private ProgressDialog mDialog;

    public RequestTask(Context conn, AsynCallback act, String mem) {
        mFrag = act;
        uploadString = mem;
        mContext = conn;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Please wait...");
        mDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        InternetService inServe = new InternetService();
        return inServe.connAndGet(uploadString, params[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mFrag.postCallback(result);
        mDialog.dismiss();
    }
}
