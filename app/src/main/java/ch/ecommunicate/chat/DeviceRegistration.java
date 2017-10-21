package ch.ecommunicate.chat;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by amlevin on 8/17/2017.
 */



public class DeviceRegistration extends AsyncTask<String, Void, String> {

    private static final String TAG = "DeviceRegistration";

    @Override
    protected String doInBackground(String... strings) {
        InputStream inputStream = null;
        HttpsURLConnection urlConnection = null;

        String token = strings[0];

        String response = "";

        try {

            URL url = new URL("https://android.ecommunicate.ch:443/deviceregistration/");

            urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Content-Type", "application/json");

            urlConnection.setRequestProperty("Accept", "application/json");

            urlConnection.setDoInput(true);

            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            JSONObject token_json = new JSONObject();

            token_json.put("token",token);

            writer.write(token_json.toString());

            writer.flush();

            writer.close();

            os.close();

            urlConnection.setRequestMethod("POST");

            urlConnection.connect();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
                Log.d(TAG,response);

            } else {

            }
        }
        catch (Exception e) {

            if (e.getMessage() != null) {
                Log.d(TAG, e.getMessage());
            }

            if (e.getLocalizedMessage() != null) {
                Log.d(TAG, e.getLocalizedMessage());
            }

            if (e.getCause() != null) {
                Log.d(TAG, e.getCause().toString());
            }

            e.printStackTrace();
        }

        return response;
    }
}
