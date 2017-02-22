package nz.co.udenbrothers.yoobie.tools;

import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpHelper {

    public String connAndGet(String urll, String aus){
        HttpURLConnection urlConnection = null;
        String result;
        try {
            urlConnection = (HttpURLConnection) ((new URL(urll).openConnection()));
            urlConnection.setDoOutput(false);
            if(aus != null){
                urlConnection.setRequestProperty("Authorization", "Basic " + aus);
            }
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            InputStream stream = urlConnection.getErrorStream();
            if (stream != null) {
                result = "error";
            }
            else {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                result = sb.toString();
            }
        } catch (Exception e) {
            result = "Bad";
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    public String connAndPost(String up, String urll, String aus){
        HttpURLConnection urlConnection = null;
        String result;
        try {
            //Connect
            urlConnection = (HttpURLConnection) ((new URL(urll).openConnection()));
            urlConnection.setDoOutput(true);
            if(aus != null){
                urlConnection.setRequestProperty("Authorization", "Basic " + aus);
            }
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            //Write
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            writer.write(up);
            writer.close();

            InputStream stream = urlConnection.getErrorStream();
            if (stream != null) {
                result = "error";
            }
            else {
                stream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                result = sb.toString();
            }
        } catch (Exception e) {
            result = "Bad";
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }
}
