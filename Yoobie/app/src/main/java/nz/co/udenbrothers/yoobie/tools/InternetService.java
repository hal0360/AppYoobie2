package nz.co.udenbrothers.yoobie.tools;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import nz.co.udenbrothers.yoobie.models.Image;
import nz.co.udenbrothers.yoobie.models.Image_stamp;


public class InternetService extends IntentService {

    public InternetService() {
        super("InternetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        MySQLiteHelper dbHandler = MySQLiteHelper.getInstance(this);
        SharedPreferences pref = getSharedPreferences("app", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();

        List<Image_stamp> listy = dbHandler.getStamps();
        if(listy.size() > 0){
            connAndGet(gson.toJson(dbHandler.getStamps()), "http://103.18.58.26/Alpha/image-stamps/recieveStamp/" + pref.getInt("id",0));
            dbHandler.clearStamp();
        }

        Calendar calendar = Calendar.getInstance();
        int tooday = calendar.get(Calendar.DAY_OF_WEEK);
        if(tooday != pref.getInt("today", 0)){
            editor.putInt("today", tooday);
            editor.apply();
            Image[] imgs = tryParse(connAndGet("blah", "http://103.18.58.26/Alpha/images/getAllImages/" + tooday));

            if(imgs != null){
                File filess = getExternalFilesDir(null);
                if (filess != null) {
                    File[] filenames = filess.listFiles();
                    for (File tmpf : filenames) {
                        tmpf.delete();
                    }
                }
                dbHandler.clearImage();

                for(Image img : imgs) {

                    try {
                        URL url = new URL("http://103.18.58.26/images2/" + img.id + "." + img.type);
                        InputStream is = url.openStream();
                        OutputStream os = new FileOutputStream(new File(getExternalFilesDir(null), "." + img.id + "." + img.type));
                        byte[] b = new byte[2048];
                        int length;
                        while ((length = is.read(b)) != -1) {
                            os.write(b, 0, length);
                        }
                        is.close();
                        os.close();
                        dbHandler.addImage(img);
                    } catch (Exception e) {

                    }
                }
            }
        }
        UpdateReceiver.completeWakefulIntent(intent);
    }

    public String connAndGet(String up, String urll){
        HttpURLConnection urlConnection;
        String result;
        try {
            //Connect
            urlConnection = (HttpURLConnection) ((new URL(urll).openConnection()));
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(10000);
            urlConnection.connect();
            //Write
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            writer.write(up);
            writer.close();
            //Read
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            result = sb.toString();
            urlConnection.disconnect();
        } catch (Exception e) {
            result = "Bad server or connection, try again later.";
        }
        return result;
    }

    public Image[] tryParse(String text) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(text, Image[].class);
        } catch (Exception e) {
            return null;
        }
    }
}
