package com.nplat.ui.main;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nplat.MainActivity;

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
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    private String id_token;

    private FirebaseAuth mAuth;

    private static final String TAG="PageViewModel";

    public class Post {
        String text;
    }

    List<PageViewModel.Post> post_list = null;

//    private LiveData<List<PageViewModel.Post>> post_list = null;

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
//    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
//        @Override
//        public String apply(Integer input) {
//            return "Hello world from section: " + input;
//        }
//    });

    private MutableLiveData<String> mText = new MutableLiveData<>();

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {

        ExecuteGetAllAsyncTask();

        return mText;

    }

    void ExecuteGetAllAsyncTask() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        user.getIdToken(false)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {

                        if (task.isSuccessful()) {

                            id_token = task.getResult().getToken();

                            new PageViewModel.GetAllAsyncTask().execute();

                        }
                    }
                });
    }

    private class GetAllAsyncTask extends AsyncTask<String, Void, Integer> {

        ProgressDialog progressDialog;

        public GetAllAsyncTask() {
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
                URL url = new URL("https://android.n-plat.com:443/getall/");
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

                    post_list = Arrays.asList(gson.fromJson(response, PageViewModel.Post[].class));

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

            mIndex.setValue(post_list.size());

//            mText.setValue(post_list.toString());

            mText.setValue(post_list.get(0).text+"\n"+post_list.get(1).text+"\n"+post_list.get(2).text+"\n"+post_list.get(3).text+"\n"+post_list.get(4).text+"\n"+post_list.get(4).text+"\n"+post_list.get(5).text+"\n"+post_list.get(6).text+"\n"+post_list.get(7).text+"\n"+post_list.get(8).text+"\n"+post_list.get(9).text);

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
}