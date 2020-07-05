package ch.ecommunicate.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class Chat extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Chat";

    ImageButton sendButton;
    EditText messageText;
    RecyclerView messageList;
    // ArrayAdapter <String> mAdapter = null;
    // ArrayList<String> messages = null;
    RecyclerView.Adapter mAdapter = null;
    ArrayList<Message> messages = null;
    int in_index = 0;

    private String id_token;
    private String contact_username;
    private String contact_name;

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("new_message")
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getStringExtra("contact").equals(contact_username)) {

                FirebaseAuth auth = FirebaseAuth.getInstance();

                FirebaseUser user = auth.getCurrentUser();

                user.getToken(false)
                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            public void onComplete(@NonNull Task<GetTokenResult> task) {

                                if (task.isSuccessful()) {

                                    id_token = task.getResult().getToken();

                                    String message = messageText.getText().toString();

                                    if (!message.equals("")) {

                                        new ChatAsyncTask1().execute();
                                    }

                                }
                            }
                        });



            }
            else
                Toast.makeText(context, intent.getStringExtra("contact")+": "+intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        }
    };

    public class ChatAsyncTask1 extends AsyncTask<String, Void, Integer> {

        private static final String TAG = "ChatAsyncTask1";

        @Override
        protected void onPostExecute(Integer result) {

            messageList.scrollToPosition(messages.size()-1);
            mAdapter.notifyDataSetChanged();

        }

        @Override
        protected Integer doInBackground(String... strings) {
            InputStream inputStream = null;
            HttpsURLConnection urlConnection = null;
            Integer result = 0;

            try {

                URL url = new URL("https://chat.android.ecommunicate.ch:443/messages/");
                urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Content-Type","application/json");

                urlConnection.setRequestProperty("Accept","application/json");

                urlConnection.setRequestMethod("POST");

                urlConnection.setDoInput(true);

                urlConnection.setDoOutput(true);

                OutputStream os = urlConnection.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                JSONObject json = new JSONObject();

                json.put("contact",contact_username);
                json.put("id_token",id_token);

                writer.write(json.toString());

                writer.flush();

                writer.close();

                os.close();

                urlConnection.connect();

                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    String response = convertInputStreamToString(inputStream);

                    process_response(response);

                    result = 1;

                } else {

                    result = 0;

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

                result = 0;
            }

            return result;
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

        private void process_response(String response) {

            JSONArray messages_json = null;
            try {
                messages_json = new JSONArray(response);
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

            messages.clear();

            for (int i = 0 ; i < messages_json.length(); i=i+1){
                Message message = null;
                try {
                    message = new Message(contact_username, contact_name, messages_json.getJSONObject(i).getString("messages"), messages_json.getJSONObject(i).getBoolean("forward"),  new Date());
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
                messages.add(message);
            }


        }

    }

    public class ChatAsyncTask2 extends AsyncTask<String, Void, Integer> {

        private static final String TAG = "ChatAsyncTask2";


        @Override
        protected void onPostExecute(Integer result) {
            new ChatAsyncTask1().execute();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            InputStream inputStream = null;
            HttpsURLConnection urlConnection = null;
            Integer result = 0;
            String message = strings[0];

            try {

                URL url = new URL("https://chat.android.ecommunicate.ch:443/submitmessage/");
                urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Content-Type","application/json");

                urlConnection.setRequestProperty("Accept","application/json");

                urlConnection.setRequestMethod("POST");

                urlConnection.setDoInput(true);

                urlConnection.setDoOutput(true);

                OutputStream os = urlConnection.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                JSONObject json = new JSONObject();

                json.put("id_token",id_token);
                json.put("contact",contact_username);
                json.put("message",message);

                writer.write(json.toString());

                writer.flush();

                writer.close();

                os.close();

                urlConnection.connect();

                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {

                    result = 1;

                } else {

                    result = 0;

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

                result = 0;
            }

            return result;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        FloatingActionButton send_message_button = (FloatingActionButton) findViewById(R.id.send_message_button);

        send_message_button.setOnClickListener(this);

//        sendButton = (ImageButton) findViewById(R.id.sendButton);
//        sendButton.setOnClickListener(this);

        messageText = (EditText) findViewById(R.id.messageText);

        messages = new ArrayList<Message>();

        mAdapter = new ChatAdapter(this, messages);

        Intent in = getIntent();
        contact_username = in.getStringExtra("contact_username");
        contact_name = in.getStringExtra("contact_name");
        id_token = in.getStringExtra("id_token");

        if (contact_name.equals(""))
            getSupportActionBar().setTitle("Chat with " + contact_username);
        else
            getSupportActionBar().setTitle("Chat with " + contact_name);

        messageList = (RecyclerView) findViewById(R.id.messageList);
        messageList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        messageList.setLayoutManager(llm);
        messageList.setAdapter(mAdapter);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        user.getToken(false)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {

                        if (task.isSuccessful()) {

                            id_token = task.getResult().getToken();

                            new ChatAsyncTask1().execute();

                        }
                    }
                });


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.send_message_button:

                FirebaseAuth auth = FirebaseAuth.getInstance();

                FirebaseUser user = auth.getCurrentUser();

                user.getToken(false)
                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            public void onComplete(@NonNull Task<GetTokenResult> task) {

                                if (task.isSuccessful()) {

                                    id_token = task.getResult().getToken();

                                    String message = messageText.getText().toString();

                                    if (!message.equals("")) {

                                        new ChatAsyncTask2().execute(message);

                                        messageText.setText("");
                                    }

                                }
                            }
                        });

                break;

            default:
                break;
        }
    }

}

