package nz.co.udenbrothers.yoobie.tools;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import nz.co.udenbrothers.yoobie.models.Campaign;
import nz.co.udenbrothers.yoobie.models.Image;
import nz.co.udenbrothers.yoobie.models.Image_stamp;
import nz.co.udenbrothers.yoobie.models.Response;


public class InternetService extends IntentService {

    public InternetService() {
        super("InternetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        MySQLiteHelper dbHandler = MySQLiteHelper.getInstance(this);
        SharedPreferences pref = getSharedPreferences("app", MODE_PRIVATE);

        List<Image_stamp> listy = dbHandler.getStamps();
        if(listy.size() > 0){

           // connAndPost(gson.toJson(dbHandler.getStamps()), "http://103.18.58.26/Alpha/image-stamps/recieveStamp/" + pref.getInt("id",0));
            dbHandler.clearStamp();
        }

        Calendar calendar = Calendar.getInstance();
        int tooday = calendar.get(Calendar.DAY_OF_WEEK);
        if(tooday != pref.getInt("today", 0)){

            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("today", tooday);
            editor.apply();

            Response response = MyHttpConnection("GET",null,"http://yoobie-api.azurewebsites.net/campaign",pref.getString("authorization", "N/A"));
            Campaign[] campains = tryParse(response.content);

            if(campains != null){
                File filess = getExternalFilesDir(null);
                if (filess != null) {
                    File[] filenames = filess.listFiles();
                    for (File tmpf : filenames) {
                        tmpf.delete();
                    }
                }
                dbHandler.clearImage();

                for(Campaign camp : campains) {
                    try {
                        URL url = new URL("http://yoobie-api.azurewebsites.net/campaign/" + camp.id + "/image/" + camp.imageId);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestProperty("Authorization", "Basic " + pref.getString("authorization", "N/A"));
                        connection.setDoInput(true);
                        connection.connect();
                        String contentType = connection.getContentType();
                        String[] parts = contentType.split("/");
                        InputStream is = connection.getInputStream();
                        OutputStream os;
                        if(parts[1] != null){
                             os = new FileOutputStream(new File(getExternalFilesDir(null), camp.id +  "." + parts[1]));
                        }
                        else {
                             os = new FileOutputStream(new File(getExternalFilesDir(null), camp.id +  ".jpg"));
                        }
                        byte[] b = new byte[2048];
                        int length;
                        while ((length = is.read(b)) != -1) {
                            os.write(b, 0, length);
                        }
                        is.close();
                        os.close();
                        Image img = new Image(camp.id, camp.name, 0, parts[1]);
                        dbHandler.addImage(img);
                    } catch (Exception e) {

                    }
                }
            }
        }
        UpdateReceiver.completeWakefulIntent(intent);
    }

    public static Response MyHttpConnection(String method, String content, String url, String aus){
        HttpURLConnection urlConnection = null;
        String result = "N/A";
        int statusCode = 999;
        try {
            urlConnection = (HttpURLConnection) ((new URL(url).openConnection()));
            if(content != null) urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            if(aus != null){
                urlConnection.setRequestProperty("Authorization", "Basic " + aus);
            }
            urlConnection.setRequestMethod(method);
            urlConnection.setConnectTimeout(10000);
            urlConnection.connect();
            if(content != null){
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(content);
                writer.close();
            }

            try {
                statusCode = urlConnection.getResponseCode();
            } catch (IOException e) {
                statusCode = urlConnection.getResponseCode();
            }

            if (statusCode < 400 ) {
                 InputStream is = urlConnection.getInputStream();
                 if(is != null){
                     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                     String line;
                     StringBuilder sb = new StringBuilder();
                     while ((line = bufferedReader.readLine()) != null) {
                         sb.append(line);
                     }
                     bufferedReader.close();
                     result = sb.toString();
                 }
            }
        } catch (Exception e) {
            result = "Problem with connection or server. Try again later";
        }
        finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return new Response(result, statusCode);
    }

    public Campaign[] tryParse(String text) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(text, Campaign[].class);
        } catch (Exception e) {
            return null;
        }
    }
}
