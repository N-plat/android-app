package com.nplat;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
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


public class FollowActivity extends AppCompatActivity {

    private static final String TAG = "Follow";

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
            progress_dialog = ProgressDialog.show(context, "","Processing Follow Request");

        }

        @Override
        protected void onPostExecute(String string) {

            try {
                if (response_json_object.getBoolean("success") == false) {



                    if (progress_dialog != null) {
                        progress_dialog.dismiss();
                    }

                    TextView tv = (TextView) findViewById(R.id.followerrors);
                    tv.setText(response_json_object.getString("reason"));

                    return;
                }

                else {
                    TextView tv = (TextView) findViewById(R.id.followerrors);
                    tv.setText("");
                    EditText editMessage = (EditText) findViewById(R.id.followUsernameText);
                    editMessage.setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (progress_dialog != null) {
                progress_dialog.dismiss();
            }


        }

        @Override
        protected String doInBackground(String... username) {
            InputStream inputStream = null;
            HttpsURLConnection urlConnection = null;
            String response = "";

            try {

                URL url = new URL("https://android.n-plat.com:443/follow/");

                urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Content-Type", "application/json");

                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.setDoInput(true);

                urlConnection.setDoOutput(true);

                OutputStream os = urlConnection.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                JSONObject request_json_object = new JSONObject();

                request_json_object.put("username",username[0]);
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
        setContentView(R.layout.activity_follow);

        context = this;

        Button btnMakeContactRequest = (Button) findViewById(R.id.btnFollow);
        btnMakeContactRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth auth = FirebaseAuth.getInstance();

                FirebaseUser user = auth.getCurrentUser();

                if (user != null) {

                    user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {

                            if (task.isSuccessful()) {

                                id_token = task.getResult().getToken();

                                EditText editUsername = (EditText) findViewById(R.id.followUsernameText);

                                String usernameString = editUsername.getText().toString();

                                new FollowActivity.AsyncTask1().execute(usernameString);
                            }
                        }
                    });
                }


            }

        });
    }
}
