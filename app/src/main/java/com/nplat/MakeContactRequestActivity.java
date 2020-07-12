package com.nplat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class MakeContactRequestActivity extends AppCompatActivity {

    private static final String TAG = "MakeContactRequest";

    private String custom_token;
    private String id_token;

    private FirebaseAuth auth;

    Context context;

    ProgressDialog progress_dialog;


    public class AsyncTask1 extends AsyncTask<String, Void, String> {

        private JSONObject response_json_object = null;

        @Override
        protected void onPreExecute(){

            //doing just progress_dialog.show(...) leads to null pointer exceptions when progress_dialog.dismiss is called later
            progress_dialog = ProgressDialog.show(context, "","Processing Contact Request");

        }

        @Override
        protected void onPostExecute(String string) {

            try {
                if (response_json_object.getBoolean("success") == false) {



                    if (progress_dialog != null) {
                        progress_dialog.dismiss();
                    }

                    TextView tv = (TextView) findViewById(R.id.makecontactrequesterrors);
                    tv.setText(response_json_object.getString("reason"));

                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent mIntent = new Intent(MakeContactRequestActivity.this, LoggedIn.class);

            mIntent.putExtra("id_token", id_token);

            if (progress_dialog != null) {
                progress_dialog.dismiss();
            }

            startActivity(mIntent);

        }

        @Override
        protected String doInBackground(String... username_and_message) {
            InputStream inputStream = null;
            HttpsURLConnection urlConnection = null;
            String response = "";

            try {

                String username = username_and_message[0];

                String message = username_and_message[1];

                URL url = new URL("https://android.n-plat.com:443/makecontactrequest/");

                urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Content-Type", "application/json");

                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.setDoInput(true);

                urlConnection.setDoOutput(true);

                OutputStream os = urlConnection.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                JSONObject request_json_object = new JSONObject();

                request_json_object.put("username",username);
                request_json_object.put("message",message);
                request_json_object.put("id_token",id_token);

                writer.write(request_json_object.toString());

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

                    try {

                        response_json_object = new JSONObject(response);

                    } catch (JSONException e) {

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

            if (response_json_object != null){

                try {
                    if(response_json_object.getBoolean("success")){

                        return "true";
                    }

                } catch (JSONException e) {

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

                    return "false";
                }

            }

            return "false";
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Make Contact Requests");

        context = this;

        setContentView(R.layout.activity_makecontactrequest);

        Intent in = getIntent();
        id_token = in.getStringExtra("id_token");

        Button btnMakeContactRequest = (Button) findViewById(R.id.btnMakeContactRequest);
        btnMakeContactRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }

        });
    }
}
