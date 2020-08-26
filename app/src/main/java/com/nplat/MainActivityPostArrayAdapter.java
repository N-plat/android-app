package com.nplat;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nplat.ui.main.PageViewModel;

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
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;

public class MainActivityPostArrayAdapter extends ArrayAdapter<PageViewModel.Post> {

    private static final String TAG = "MainActivity";

    private String id_token;

    private final Context context;
    private final List<PageViewModel.Post> post_list;

    public MainActivityPostArrayAdapter(Context context, List<PageViewModel.Post> post_list) {
        super(context, R.layout.post, post_list);
        this.context = context;
        this.post_list = post_list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View post_view;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView post_nloves;
        TextView post_nreposts;
        TextView post_textview;
        TextView post_usernameview;
        TextView post_timestampview;
        final VideoView post_videoview;
        ImageView post_imageview;
        ImageView share_imageview;
        ImageView heart_imageview;
        ImageView repost_imageview;

        if (post_list.get(position).parent_uniqueid == 0) {

            if (post_list.get(position).videoid != 0) {

                post_view = inflater.inflate(R.layout.postwithvideo, parent, false);
                post_textview = (TextView) post_view.findViewById(R.id.text);
                post_usernameview = (TextView) post_view.findViewById(R.id.username);
                post_timestampview = (TextView) post_view.findViewById(R.id.timestamp);
                post_videoview = (VideoView) post_view.findViewById(R.id.video);
                post_nloves = (TextView) post_view.findViewById(R.id.nloves);
                post_nreposts = (TextView) post_view.findViewById(R.id.nreposts);

                post_textview.setText(post_list.get(position).text);
                post_usernameview.setText(post_list.get(position).username);
                post_timestampview.setText(post_list.get(position).timestamp);
                post_videoview.setVideoURI(Uri.parse("https://video.n-plat.com/?filename=video" + post_list.get(position).videoid + ".mp4"));
                post_videoview.setMediaController(new MediaController(post_videoview.getContext()));
                post_videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.seekTo(0);
                    }
                });
                post_nloves.setText(post_list.get(position).nloves);
                post_nreposts.setText(post_list.get(position).nreposts);
                share_imageview = (ImageView) post_view.findViewById(R.id.share);

                share_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(context, view);
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.actions, popup.getMenu());
                        popup.show();
                    }

                });
//            post_videoview.setOnClickListener(new VideoView.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    post_videoview.start();
//                }
//            });

            } else if (post_list.get(position).imageid != 0) {

                post_view = inflater.inflate(R.layout.postwithimage, parent, false);
                post_textview = (TextView) post_view.findViewById(R.id.text);
                post_usernameview = (TextView) post_view.findViewById(R.id.username);
                post_timestampview = (TextView) post_view.findViewById(R.id.timestamp);
                post_imageview = (ImageView) post_view.findViewById(R.id.image);
                post_nloves = (TextView) post_view.findViewById(R.id.nloves);
                post_nreposts = (TextView) post_view.findViewById(R.id.nreposts);

                post_textview.setText(post_list.get(position).text);
                post_usernameview.setText(post_list.get(position).username);
                post_timestampview.setText(post_list.get(position).timestamp);
                Glide.with(this.context).load("https://image.n-plat.com/?filename=image" + post_list.get(position).imageid + ".jpeg").into(post_imageview);
                post_nloves.setText(post_list.get(position).nloves);
                post_nreposts.setText(post_list.get(position).nreposts);
                share_imageview = (ImageView) post_view.findViewById(R.id.share);

                share_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(context, view);
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.actions, popup.getMenu());
                        popup.show();
                    }

                });


            } else {

                post_view = inflater.inflate(R.layout.post, parent, false);
                post_textview = (TextView) post_view.findViewById(R.id.text);
                post_usernameview = (TextView) post_view.findViewById(R.id.username);
                post_timestampview = (TextView) post_view.findViewById(R.id.timestamp);
                post_nloves = (TextView) post_view.findViewById(R.id.nloves);
                post_nreposts = (TextView) post_view.findViewById(R.id.nreposts);

                post_textview.setText(post_list.get(position).text);
                post_usernameview.setText(post_list.get(position).username);
                post_timestampview.setText(post_list.get(position).timestamp);
                post_nloves.setText(post_list.get(position).nloves);
                post_nreposts.setText(post_list.get(position).nreposts);
                share_imageview = (ImageView) post_view.findViewById(R.id.share);

                share_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Context wrapper = new ContextThemeWrapper(context, R.style.PopupWindowTextView);
                        PopupWindow popup = new PopupWindow(context);
                        TextView textview = new TextView(wrapper);
                        textview.setText(Html.fromHtml("<a href=\"http://n-plat.com/singlepost/?id=70\">http://n-plat.com/singlepost/?id=70</a>"));
                        popup.setContentView(textview);
//                        popup.update(100,100);
                        popup.showAsDropDown(view);
//                        PopupMenu popup = new PopupMenu(context, view);
//                        MenuInflater inflater = popup.getMenuInflater();
//                        inflater.inflate(R.menu.actions, popup.getMenu());
 //                       popup.show();
                    }

                });

                heart_imageview = (ImageView) post_view.findViewById(R.id.heart);
                heart_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FirebaseAuth auth = FirebaseAuth.getInstance();

                        FirebaseUser user = auth.getCurrentUser();

                        user.getIdToken(false)
                                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                    public void onComplete(@NonNull Task<GetTokenResult> task) {

                                        if (task.isSuccessful()) {

                                            id_token = task.getResult().getToken();

                                            new HeartAsyncTask().execute(String.valueOf(post_list.get(position).uniqueid));

                                        }
                                    }
                                });

                    }

                });

                repost_imageview = (ImageView) post_view.findViewById(R.id.repost);
                repost_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FirebaseAuth auth = FirebaseAuth.getInstance();

                        FirebaseUser user = auth.getCurrentUser();

                        user.getIdToken(false)
                                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                    public void onComplete(@NonNull Task<GetTokenResult> task) {

                                        if (task.isSuccessful()) {

                                            id_token = task.getResult().getToken();

                                            new RepostAsyncTask().execute(post_list.get(position).text, post_list.get(position).timestamp, String.valueOf(post_list.get(position).uniqueid), post_list.get(position).username);

                                        }
                                    }
                                });

                    }

                });

            }
        } else {
            if (post_list.get(position).parent_videoid != 0) {

                post_view = inflater.inflate(R.layout.postwithvideo, parent, false);
                post_textview = (TextView) post_view.findViewById(R.id.text);
                post_usernameview = (TextView) post_view.findViewById(R.id.username);
                post_timestampview = (TextView) post_view.findViewById(R.id.timestamp);
                post_videoview = (VideoView) post_view.findViewById(R.id.video);
                post_nloves = (TextView) post_view.findViewById(R.id.nloves);
                post_nreposts = (TextView) post_view.findViewById(R.id.nreposts);

                post_textview.setText(post_list.get(position).parent_text);
                post_usernameview.setText(post_list.get(position).parent_username + " (reposted by "+post_list.get(position).username+")");
                post_timestampview.setText(post_list.get(position).parent_timestamp);
                post_videoview.setVideoURI(Uri.parse("https://video.n-plat.com/?filename=video" + post_list.get(position).parent_videoid + ".mp4"));
                post_videoview.setMediaController(new MediaController(post_videoview.getContext()));
                post_videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.seekTo(0);
                    }
                });
                post_nloves.setText(post_list.get(position).nloves);
                post_nreposts.setText(post_list.get(position).nreposts);
                share_imageview = (ImageView) post_view.findViewById(R.id.share);

                share_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(context, view);
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.actions, popup.getMenu());
                        popup.show();
                    }

                });
//            post_videoview.setOnClickListener(new VideoView.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    post_videoview.start();
//                }
//            });

            } else if (post_list.get(position).parent_imageid != 0) {

                post_view = inflater.inflate(R.layout.postwithimage, parent, false);
                post_textview = (TextView) post_view.findViewById(R.id.text);
                post_usernameview = (TextView) post_view.findViewById(R.id.username);
                post_timestampview = (TextView) post_view.findViewById(R.id.timestamp);
                post_imageview = (ImageView) post_view.findViewById(R.id.image);
                post_nloves = (TextView) post_view.findViewById(R.id.nloves);
                post_nreposts = (TextView) post_view.findViewById(R.id.nreposts);

                post_textview.setText(post_list.get(position).parent_text);
                post_usernameview.setText(post_list.get(position).parent_username + "(reposted by "+post_list.get(position).username+")");
                post_timestampview.setText(post_list.get(position).parent_timestamp);
                Glide.with(this.context).load("https://image.n-plat.com/?filename=image" + post_list.get(position).parent_imageid + ".jpeg").into(post_imageview);
                post_nloves.setText(post_list.get(position).nloves);
                post_nreposts.setText(post_list.get(position).nreposts);
                share_imageview = (ImageView) post_view.findViewById(R.id.share);

                share_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(context, view);
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.actions, popup.getMenu());
                        popup.show();
                    }

                });


            } else {

                post_view = inflater.inflate(R.layout.post, parent, false);
                post_textview = (TextView) post_view.findViewById(R.id.text);
                post_usernameview = (TextView) post_view.findViewById(R.id.username);
                post_timestampview = (TextView) post_view.findViewById(R.id.timestamp);
                post_nloves = (TextView) post_view.findViewById(R.id.nloves);
                post_nreposts = (TextView) post_view.findViewById(R.id.nreposts);

                post_textview.setText(post_list.get(position).parent_text);
                post_usernameview.setText(post_list.get(position).parent_username + " (reposted by "+post_list.get(position).username+")");
                post_timestampview.setText(post_list.get(position).parent_timestamp);
                post_nloves.setText(post_list.get(position).nloves);
                post_nreposts.setText(post_list.get(position).nreposts);
                share_imageview = (ImageView) post_view.findViewById(R.id.share);

                share_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(context, view);
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.actions, popup.getMenu());
                        popup.show();
                    }

                });

                heart_imageview = (ImageView) post_view.findViewById(R.id.heart);
                heart_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FirebaseAuth auth = FirebaseAuth.getInstance();

                        FirebaseUser user = auth.getCurrentUser();

                        user.getIdToken(false)
                                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                    public void onComplete(@NonNull Task<GetTokenResult> task) {

                                        if (task.isSuccessful()) {

                                            id_token = task.getResult().getToken();

                                            new HeartAsyncTask().execute(String.valueOf(post_list.get(position).uniqueid));

                                        }
                                    }
                                });

                    }

                });

            }

        }

        return post_view;
    }

    private class HeartAsyncTask extends AsyncTask<String, Void, Integer> {

        ProgressDialog progressDialog;

        public HeartAsyncTask() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //progressDialog = ProgressDialog.show(context, "","Getting Contacts");
        }

        @Override
        protected Integer doInBackground(String... post_id) {

            InputStream inputStream = null;
            HttpsURLConnection urlConnection = null;
            Integer result = 0;

            try {
                URL url = new URL("https://android.n-plat.com:443/love/");
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
                token_json.put("post_id",post_id[0]);

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

            //progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            progressDialog.dismiss();
        }
    }

    private class RepostAsyncTask extends AsyncTask<String, Void, Integer> {

        ProgressDialog progressDialog;

        public RepostAsyncTask() {
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
                URL url = new URL("https://android.n-plat.com:443/repost/");
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
                token_json.put("message",strings[0]);
                token_json.put("time",strings[1]);
                token_json.put("unique_id",strings[2]);
                token_json.put("username",strings[3]);
                token_json.put("image_unique_id",JSONObject.NULL);
                token_json.put("video_unique_id",JSONObject.NULL);

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
