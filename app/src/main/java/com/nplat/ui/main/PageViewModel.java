package com.nplat.ui.main;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nplat.R;

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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    private String id_token;

    private FirebaseAuth mAuth;

    private static final String TAG="PageViewModel";

    public static class Post {
        public String text;
        public String username;
        public String timestamp;
        public int videoid;
        public int imageid;
        public int uniqueid;
        public String nloves;
        public String nreposts;
        public String parent_text;
        public String parent_username;
        public String parent_timestamp;
        public int parent_videoid;
        public int parent_imageid;
        public int parent_uniqueid;
        public String parent_nloves;
        public String parent_nreposts;

    }

    public class Username {
        public String username;
    }

    List<PageViewModel.Post> post_list = null;

    List<PageViewModel.Username> username_list = null;

//    private LiveData<List<PageViewModel.Post>> post_list = null;

//    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();

    public int mIndex = 1;

//    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
//        @Override
//        public String apply(Integer input) {
//            return "Hello world from section: " + input;
//        }
//    });

    private MutableLiveData<String> mText = new MutableLiveData<>();

    private MutableLiveData<List<PageViewModel.Post>> mList = new MutableLiveData<>();

    public void setIndex(int index) {
        mIndex = index;
    }

//    public void setIndex(int index) {
//        mIndex.setValue(index);
//    }


    public LiveData<String> getText() {

        if (mIndex == 1) {
            ExecuteGetPostsAsyncTask();
        }

        if (mIndex == 2) {
            ExecuteGetFeedAsyncTask();
        }

        if (mIndex == 3) {
            ExecuteGetFollowingAsyncTask();
        }

        if (mIndex == 4) {
            ExecuteGetFollowersAsyncTask();
        }

        return mText;

    }

    public LiveData<List<PageViewModel.Post>> getList() {

        if (mIndex == 1) {
            ExecuteGetPostsAsyncTask();
        }

        if (mIndex == 2) {
            ExecuteGetFeedAsyncTask();
        }

        if (mIndex == 3) {
            ExecuteGetFollowingAsyncTask();
        }

        if (mIndex == 4) {
            ExecuteGetFollowersAsyncTask();
        }

        return mList;

    }

    void ExecuteGetFeedAsyncTask() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        user.getIdToken(false)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {

                        if (task.isSuccessful()) {

                            id_token = task.getResult().getToken();

                            new PageViewModel.GetFeedAsyncTask().execute();

                        }
                    }
                });
    }

    void ExecuteGetPostsAsyncTask() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        user.getIdToken(false)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {

                        if (task.isSuccessful()) {

                            id_token = task.getResult().getToken();

                            new PageViewModel.GetPostsAsyncTask().execute();

                        }
                    }
                });
    }

    void ExecuteGetFollowersAsyncTask() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        user.getIdToken(false)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {

                        if (task.isSuccessful()) {

                            id_token = task.getResult().getToken();

                            new PageViewModel.GetFollowersAsyncTask().execute();

                        }
                    }
                });
    }

    void ExecuteGetFollowingAsyncTask() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        user.getIdToken(false)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {

                        if (task.isSuccessful()) {

                            id_token = task.getResult().getToken();

                            new PageViewModel.GetFollowingAsyncTask().execute();

                        }
                    }
                });
    }

    private class GetPostsAsyncTask extends AsyncTask<String, Void, Integer> {

        ProgressDialog progressDialog;

        private String response;

        public GetPostsAsyncTask() {
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
                URL url = new URL("https://android.n-plat.com:443/posts/");
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

                    response = convertInputStreamToString(inputStream);

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

//            mIndex.setValue(post_list.size());

//            mText.setValue(post_list.toString());

            //mText.setValue(post_list.get(0).text+"\n"+post_list.get(1).text+"\n"+post_list.get(2).text+"\n"+post_list.get(3).text+"\n"+post_list.get(4).text+"\n"+post_list.get(4).text+"\n"+post_list.get(5).text+"\n"+post_list.get(6).text+"\n"+post_list.get(7).text+"\n"+post_list.get(8).text+"\n"+post_list.get(9).text);

            mText.setValue(response);

            //progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            progressDialog.dismiss();
        }
    }

    private class GetFeedAsyncTask extends AsyncTask<String, Void, Integer> {

        ProgressDialog progressDialog;

        private String response;

        public GetFeedAsyncTask() {
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

                    response = convertInputStreamToString(inputStream);

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();

                    post_list = Arrays.asList(gson.fromJson(response, PageViewModel.Post[].class));

                    mText.setValue(response);

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

//            mIndex.setValue(post_list.size());

//            mText.setValue(post_list.toString());

//            mText.setValue(post_list.get(0).text+"\n"+post_list.get(1).text+"\n"+post_list.get(2).text+"\n"+post_list.get(3).text+"\n"+post_list.get(4).text+"\n"+post_list.get(4).text+"\n"+post_list.get(5).text+"\n"+post_list.get(6).text+"\n"+post_list.get(7).text+"\n"+post_list.get(8).text+"\n"+post_list.get(9).text);

            mText.setValue(response);

            //progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            progressDialog.dismiss();
        }
    }

    private class GetFollowersAsyncTask extends AsyncTask<String, Void, Integer> {

        ProgressDialog progressDialog;

        private String response;

        public GetFollowersAsyncTask() {
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
                URL url = new URL("https://android.n-plat.com:443/followers/");
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

                    response = convertInputStreamToString(inputStream);

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();

                    username_list = Arrays.asList(gson.fromJson(response, PageViewModel.Username[].class));

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

//            mIndex.setValue(post_list.size());

//            mText.setValue(post_list.toString());

//            mText.setValue(username_list.get(0).username+"\n"+username_list.get(1).username+"\n"+username_list.get(2).username+"\n"+username_list.get(3).username+"\n"+username_list.get(4).username+"\n"+username_list.get(5).username+"\n"+username_list.get(6).username+"\n"+username_list.get(7).username+"\n"+username_list.get(8).username+"\n"+username_list.get(9).username);

            mText.setValue(response);

            //progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            progressDialog.dismiss();
        }
    }

    private class GetFollowingAsyncTask extends AsyncTask<String, Void, Integer> {

        ProgressDialog progressDialog;

        private String response;

        public GetFollowingAsyncTask() {
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
                URL url = new URL("https://android.n-plat.com:443/following/");
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

                    response = convertInputStreamToString(inputStream);

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();

                    username_list = Arrays.asList(gson.fromJson(response, PageViewModel.Username[].class));

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

//            mIndex.setValue(post_list.size());

//            mText.setValue(post_list.toString());

//            mText.setValue(username_list.get(0).username+"\n"+username_list.get(1).username+"\n"+username_list.get(2).username+"\n"+username_list.get(3).username+"\n"+username_list.get(4).username+"\n"+username_list.get(5).username+"\n"+username_list.get(6).username+"\n"+username_list.get(7).username+"\n"+username_list.get(8).username+"\n"+username_list.get(9).username);

            mText.setValue(response);

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