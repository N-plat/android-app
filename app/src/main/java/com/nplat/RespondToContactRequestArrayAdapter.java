package com.nplat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

//import com.google.firebase.auth.GetTokenResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by amlevin on 8/25/2017.
 */

public class RespondToContactRequestArrayAdapter extends ArrayAdapter<RespondToContactRequestsActivity.RespondToContactRequest> {

    private static final String TAG = "ArrayAdapter1";

    private final Context context;
    private final List<RespondToContactRequestsActivity.RespondToContactRequest> respondtocontactrequest_list;

    public class SubmitContactRequestResponse extends AsyncTask<String, Void, String> {

        private static final String TAG = "AsyncTask1";

        private String id_token = "";

        private String username = "";

        private String accept = "";

        @Override
        protected String doInBackground(String... strings) {

            username = strings[0];

            accept = strings[1];

            return "";

        }

        public class NetworkActivity extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                Intent in = new Intent(context, LoggedIn.class);

                in.putExtra("id_token", id_token);

                context.startActivity(in);
            }

            @Override
            protected String doInBackground(String... strings) {

                try {

                    URL url = new URL("https://android.n-plat.com:443/submitcontactrequestresponse/");

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
                    json_object.put("username", username);
                    json_object.put("accept", accept);

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


    public RespondToContactRequestArrayAdapter(Context context, List<RespondToContactRequestsActivity.RespondToContactRequest> respondtocontactrequest_list) {
        super(context, R.layout.contact, respondtocontactrequest_list);
        this.context = context;
        this.respondtocontactrequest_list = respondtocontactrequest_list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View respondtocontactrequest_view;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView respondtocontactrequest_username_textview;
        TextView respondtocontactrequest_message_textview;

        respondtocontactrequest_view = inflater.inflate(R.layout.respondtocontactrequest, parent, false);
        respondtocontactrequest_username_textview = (TextView) respondtocontactrequest_view.findViewById(R.id.respondToContactRequestUsername);

        respondtocontactrequest_message_textview = (TextView) respondtocontactrequest_view.findViewById(R.id.respondToContactRequestMessageText);

        respondtocontactrequest_message_textview.setText(respondtocontactrequest_list.get(position).message);

        if (respondtocontactrequest_list.get(position).name != "")
            respondtocontactrequest_username_textview.setText(respondtocontactrequest_list.get(position).name);
        else
            respondtocontactrequest_username_textview.setText(respondtocontactrequest_list.get(position).username);

        Button acceptbutton = (Button) respondtocontactrequest_view.findViewById(R.id.accept_contact_request);

        acceptbutton.setTag(respondtocontactrequest_list.get(position).username);

        acceptbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SubmitContactRequestResponse().execute((String)view.getTag(),"true");

            }

        });

        Button rejectbutton = (Button) respondtocontactrequest_view.findViewById(R.id.reject_contact_request);;

        rejectbutton.setTag(respondtocontactrequest_list.get(position).username);

        rejectbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SubmitContactRequestResponse().execute((String)view.getTag(),"false");

            }

        });

        return respondtocontactrequest_view;
    }
}
