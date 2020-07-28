package com.nplat;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

//import com.google.firebase.auth.GetTokenResult;

/**
 * Created by amlevin on 8/25/2017.
 */

public class Feed extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String id_token;

    private FirebaseAuth mAuth;

    private static final String TAG="Contacts";

    ListView feed_listview;

    MainActivityPostArrayAdapter feed_array_adapter;

    public class Post {
        String text;
    }

    List<Post> post_list = null;

    Context context;

    ProgressDialog progress_dialog;

    void update_posts() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        user.getIdToken(false)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {

                        if (task.isSuccessful()) {

                            id_token = task.getResult().getToken();

                            new PostsProcessor().execute();

                        }
                    }
                });
    }


    private class PostsProcessor extends AsyncTask<String, Void, Integer> {

        ProgressDialog progressDialog;

        public PostsProcessor() {
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
                URL url = new URL("https://android.n-plat.com:443/feed/");
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

                    post_list = Arrays.asList(gson.fromJson(response, Post[].class));

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

//            feed_array_adapter = new FeedPostArrayAdapter(context, post_list);

            feed_listview.setAdapter((ListAdapter) feed_array_adapter);

            //progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            progressDialog.dismiss();
        }
    }

    public Feed() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("new_message")
        );

        update_posts();
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

          update_posts();



        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        context = this;

//        feed_array_adapter = new FeedPostArrayAdapter(this, post_list);

        feed_listview = (ListView) findViewById(R.id.feedListView);

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        Intent intent= new Intent(this,Chat.class);

        //TextView contact = (TextView) view.findViewById(R.id.contact);
        //mIntent.putExtra("contact_name", contact.getText().toString());

        intent.putExtra("contact_username", post_list.get(position).text);
        intent.putExtra("contact_name", post_list.get(position).text);
        startActivity(intent);

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
}
