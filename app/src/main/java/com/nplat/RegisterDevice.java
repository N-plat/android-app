package com.nplat;

import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by amlevin on 8/17/2017.
 */



public class RegisterDevice extends AsyncTask<String, Void, String> {

    private static final String TAG = "RegisterDevice";

    private String device_token = "";

    private String id_token = "";

    @Override
    protected String doInBackground(String... strings) {

        return "";

    }

    public class NetworkActivity extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {

                URL url = new URL("https://android.n-plat.com:443/registerdevice/");

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Content-Type", "application/json");

                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.setDoInput(true);

                urlConnection.setDoOutput(true);

                OutputStream os = urlConnection.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                JSONObject json_object = new JSONObject();

                json_object.put("id_token", id_token);
                json_object.put("device_token", device_token);

                writer.write(json_object.toString());

                writer.flush();

                writer.close();

                os.close();

                urlConnection.setRequestMethod("POST");

                urlConnection.connect();

                int statusCode = urlConnection.getResponseCode();

                String response = "";

                if (statusCode == 200) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    Log.d(TAG, response);

                }

            } catch (Exception e) {

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

            return "";

        };

    };
}
