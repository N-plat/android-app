package com.nplat;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by amlevin on 8/25/2017.
 */

public class LoggedIn extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String id_token;

    private FirebaseAuth mAuth;

    private static final String TAG="Contacts";

    ListView contact_listview;

    ContactArrayAdapter contact_array_adapter;

    public class Contact {
        String username;
        String name;
        Boolean new_message;
    }

    List<Contact> contact_list = null;

    Context context;

    ProgressDialog progress_dialog;

    public LoggedIn() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("new_message")
        );

        update_contacts();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //new Chat.ChatAsyncTask1().execute();

            Log.d(TAG,intent.getExtras().getString("contact"));

          update_contacts();



        }
    };


    void update_contacts() {
/*
        user.getToken(false)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {

                        if (task.isSuccessful()) {

                            id_token = task.getResult().getToken();

                            new ContactsProcessor().execute();

                        }
                    }
                });
 */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        context = this;

        contact_array_adapter = new ContactArrayAdapter(this, contact_list);

        Button postbutton = (Button) findViewById(R.id.postbutton);

        postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth auth = FirebaseAuth.getInstance();

                FirebaseUser user = auth.getCurrentUser();

                if (user != null) {

                    user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {

                            if (task.isSuccessful()) {

                                id_token = task.getResult().getToken();

                                EditText editMessage = (EditText) findViewById(R.id.postText);

                                String messageString = editMessage.getText().toString();

                                new AsyncTask1().execute(messageString);
                            }
                        }
                    });
                }
            }

        });

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        Intent intent= new Intent(this,Chat.class);

        //TextView contact = (TextView) view.findViewById(R.id.contact);
        //mIntent.putExtra("contact_name", contact.getText().toString());

        intent.putExtra("contact_username",contact_list.get(position).username);
        intent.putExtra("contact_name",contact_list.get(position).name);
        startActivity(intent);

        contact_list.get(position).new_message = false;

    }

    private class ContactsProcessor extends AsyncTask<String, Void, Integer> {

        ProgressDialog progressDialog;

        public ContactsProcessor() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //progressDialog = ProgressDialog.show(context, "","Getting Contacts");
        }

        @Override
        protected Integer doInBackground(String... strings) {

            InputStream inputStream = null;
            HttpsURLConnection urlConnection = null;
            Integer result = 0;

            try {
                URL url = new URL("https://android.n-plat.com:443/contacts/");
                urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Content-Type","application/json");

                urlConnection.setRequestProperty("Accept","application/json");

                urlConnection.setRequestMethod("POST");

                urlConnection.setDoInput(true);

                urlConnection.setDoOutput(true);

                OutputStream os = urlConnection.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                JSONObject token_json = new JSONObject();

                token_json.put("id_token",id_token);

                writer.write(token_json.toString());

                writer.flush();

                writer.close();

                os.close();

                urlConnection.connect();

                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    String response = convertInputStreamToString(inputStream);

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();

                    contact_list = Arrays.asList(gson.fromJson(response, Contact[].class));

                    result = 1;

                }
                else {
                    result = 0;

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
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            contact_array_adapter = new ContactArrayAdapter(context, contact_list);

            contact_listview.setAdapter((ListAdapter) contact_array_adapter);

            //progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            progressDialog.dismiss();
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null){
            result += line;
        }

        if(null!=inputStream){
            inputStream.close();
        }
        return result;
    }

    public class AsyncTask1 extends AsyncTask<String, Void, String> {

        private JSONObject response_json_object = null;

        @Override
        protected void onPreExecute(){

            //doing just progress_dialog.show(...) leads to null pointer exceptions when progress_dialog.dismiss is called later
            progress_dialog = ProgressDialog.show(context, "","Posting");

        }

        @Override
        protected void onPostExecute(String string) {

            try {
                if (response_json_object.getBoolean("success") == false) {

                    if (progress_dialog != null) {
                        progress_dialog.dismiss();
                    }

                    //TextView tv = (TextView) findViewById(R.id.makecontactrequesterrors);
                    //tv.setText(response_json_object.getString("reason"));

                    return;
                } else {

                    if (progress_dialog != null) {
                        progress_dialog.dismiss();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... message) {
            InputStream inputStream = null;
            HttpsURLConnection urlConnection = null;
            String response = "";

            try {

                URL url = new URL("https://android.n-plat.com:443/post/");

                urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Content-Type", "application/json");

                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.setDoInput(true);

                urlConnection.setDoOutput(true);

                OutputStream os = urlConnection.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                JSONObject request_json_object = new JSONObject();

                request_json_object.put("message",message[0]);
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

}
