package com.nplat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by amlevin on 8/25/2017.
 */

public class PostActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private final static int REQUEST_PICTURE = 0;
    private final static int REQUEST_VIDEO = 1;
    private final static int PERMISSIONS_REQUEST_READ_MEDIA = 10;
    private final static int PERMISSIONS_REQUEST_LOCATION = 11;

    private String photoPath;
    private String videoPath;

    private String id_token;

    private FirebaseAuth mAuth;

    private static final String TAG="Contacts";

    ListView contact_listview;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(600,TimeUnit.SECONDS)
            .writeTimeout(600,TimeUnit.SECONDS)
            .readTimeout(600,TimeUnit.SECONDS)
            .build();

    PostActivityPostArrayAdapter contact_array_adapter;

    public class Post {
        String text;
        String username;
        String timestamp;
        int videoid;
        int imageid;
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

            contact_array_adapter = new PostActivityPostArrayAdapter(context, post_list);

            contact_listview.setAdapter((ListAdapter) contact_array_adapter);

            //progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            progressDialog.dismiss();
        }
    }

    public PostActivity() {
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

            Log.d(TAG,intent.getExtras().getString("post"));

          update_posts();



        }
    };
    File photoFile = null;
    File videoFile = null;

    private File getImageFile() {

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            photoFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            photoPath = photoFile.getAbsolutePath();
            Log.d(TAG, photoPath);
        } catch (IOException ex) {
//            Snackbar.make(viewPager, R.string.main_error_dispatch_camera, Snackbar.LENGTH_SHORT).show();
        }
        return photoFile;
    }

    private File getVideoFile() {

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            videoFile = File.createTempFile(imageFileName, ".mp4", storageDir);
            videoPath = videoFile.getAbsolutePath();
            Log.d(TAG, videoPath);
        } catch (IOException ex) {
//            Snackbar.make(viewPager, R.string.main_error_dispatch_camera, Snackbar.LENGTH_SHORT).show();
        }
        return videoFile;
    }

    private List<Intent> addIntentsToList(List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    private String getRealPathFromImageURI(Uri contentURI) {
        String result = null;

        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            if (contentURI.toString().contains("mediaKey")) {
                cursor.close();

                try {
                    File file = File.createTempFile("tempImg", ".jpg", getCacheDir());
                    InputStream input = getContentResolver().openInputStream(contentURI);
                    OutputStream output = new FileOutputStream(file);

                    try {
                        byte[] buffer = new byte[4 * 1024];
                        int read;

                        while ((read = input.read(buffer)) != -1) {
                            output.write(buffer, 0, read);
                        }
                        output.flush();
                        result = file.getAbsolutePath();
                    } finally {
                        output.close();
                        input.close();
                    }

                } catch (Exception e) {
                    Log.e(MainActivity.class.getSimpleName(), "Error getting file path", e);
                }
            } else {
                cursor.moveToFirst();
                int dataColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(dataColumn);
                cursor.close();
            }

        }
        return result;
    }

    private String getRealPathFromVideoURI(Uri contentURI) {
        String result = null;

        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            if (contentURI.toString().contains("mediaKey")) {
                cursor.close();

                try {
                    File file = File.createTempFile("tempImg", ".jpg", getCacheDir());
                    InputStream input = getContentResolver().openInputStream(contentURI);
                    OutputStream output = new FileOutputStream(file);

                    try {
                        byte[] buffer = new byte[4 * 1024];
                        int read;

                        while ((read = input.read(buffer)) != -1) {
                            output.write(buffer, 0, read);
                        }
                        output.flush();
                        result = file.getAbsolutePath();
                    } finally {
                        output.close();
                        input.close();
                    }

                } catch (Exception e) {
                    Log.e(MainActivity.class.getSimpleName(), "Error getting file path", e);
                }
            } else {
                cursor.moveToFirst();
                int dataColumn = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                result = cursor.getString(dataColumn);
                cursor.close();
            }

        }
        return result;
    }

    Uri video_data = null;
    Uri image_data = null;
    String image_path;
    String image_string;
    String video_path;
    boolean ispostwithimage = false;
    boolean ispostwithvideo = false;


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_PICTURE) {
            boolean isCamera = (data == null ||
                    data.getData() == null);

            ImageView imageview = (ImageView) findViewById(R.id.image);
            imageview.setImageURI(data.getData());
            ispostwithimage = true;
            ispostwithvideo = false;
            image_data = data.getData();
            image_path = getRealPathFromImageURI(image_data);

        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_VIDEO) {
            boolean isCamera = (data == null ||
                    data.getData() == null);

            VideoView videoview = (VideoView) findViewById(R.id.video);
            videoview.setVideoURI(data.getData());
            ispostwithvideo = true;
            ispostwithimage = false;
            video_data = data.getData();
            video_path = getRealPathFromVideoURI(video_data);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        context = this;

        final Activity activity = this;

        contact_array_adapter = new PostActivityPostArrayAdapter(this, post_list);

        contact_listview = (ListView) findViewById(R.id.contactListView);

        Button postbutton = (Button) findViewById(R.id.postbutton);
        Button takephotobutton = (Button) findViewById(R.id.takephotobutton);
        Button takevideobutton = (Button) findViewById(R.id.takevideobutton);

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

        takephotobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_MEDIA);
                } else {
                    Intent chooserIntent = null;

                    List<Intent> intentList = new ArrayList<>();

                    Intent pickIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePhotoIntent.putExtra("return-data", true);
                    File photoFile = getImageFile();

                    if (photoFile != null) {
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
                            intentList = addIntentsToList(intentList, takePhotoIntent);
                        }
                    }

                    if (pickIntent.resolveActivity(getPackageManager()) != null) {
                        intentList = addIntentsToList(intentList, pickIntent);
                    }

                    if (intentList.size() > 0) {
                        chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                                "main_message_picture_source");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
                    }

                    startActivityForResult(chooserIntent, REQUEST_PICTURE);
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();

                FirebaseUser user = auth.getCurrentUser();

                if (user != null) {

                    user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {

                            if (task.isSuccessful()) {

                                id_token = task.getResult().getToken();

                                EditText editMessage = (EditText) findViewById(R.id.postText);

                                String messageString = editMessage.getText().toString();

//                                new AsyncTask1().execute(messageString);
                            }
                        }
                    });
                }
            }

        });

        takevideobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_MEDIA);
                } else {
                    Intent chooserIntent = null;

                    List<Intent> intentList = new ArrayList<>();

                    Intent pickIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    takeVideoIntent.putExtra("return-data", true);
//                    File videoFile = getVideoFile();

                    if (videoFile != null) {
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                            intentList = addIntentsToList(intentList, takeVideoIntent);
                        }
                    }

                    if (pickIntent.resolveActivity(getPackageManager()) != null) {
                        intentList = addIntentsToList(intentList, pickIntent);
                    }

                    if (intentList.size() > 0) {
                        chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                                "main_message_video_source");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
                    }

                    startActivityForResult(chooserIntent, REQUEST_VIDEO);
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();

                FirebaseUser user = auth.getCurrentUser();

                if (user != null) {

                    user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {

                            if (task.isSuccessful()) {

                                id_token = task.getResult().getToken();

                                EditText editMessage = (EditText) findViewById(R.id.postText);

                                String messageString = editMessage.getText().toString();

//                                new AsyncTask1().execute(messageString);
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

        //TextView post = (TextView) view.findViewById(R.id.post);
        //mIntent.putExtra("contact_name", post.getText().toString());

        intent.putExtra("contact_username", post_list.get(position).text);
        intent.putExtra("contact_name", post_list.get(position).text);
        startActivity(intent);

    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private String readTextFromUri(Uri uri) throws IOException {
        String line = "";
        String result = "";
        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            while((line = reader.readLine()) != null){
                result += line;
            }
            if(null!=inputStream){
                inputStream.close();
            }
            return result;
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

    private String convertFileInputStreamToString(FileInputStream fileInputStream) throws IOException {
//        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(fileInputStream));
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(fileInputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null){
            result += line;
        }

        if(null!=fileInputStream){
            fileInputStream.close();
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
                        EditText editMessage = (EditText) findViewById(R.id.postText);
                        editMessage.setText("");
                        update_posts();
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


            try {

                if (ispostwithvideo) {

                    MediaType MEDIA_TYPE_MP4 = MediaType.parse("video/mp4");

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("videoFile", "catvideo.mp4",
                                    RequestBody.create(MEDIA_TYPE_MP4, new File(video_path)))
                            .build();

                    Request request = new Request.Builder()
                            .url("https://android.n-plat.com:443/postwithvideo/")
                            .post(requestBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);

                        System.out.println(response.body().string());
                    }

                } else if (ispostwithimage) {

                    MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("imageFile", "catimage.jpeg",
                                    RequestBody.create(MEDIA_TYPE_JPEG, new File(image_path)))
                            .build();

                    Request request = new Request.Builder()
                            .url("https://android.n-plat.com:443/postwithimage/")
                            .post(requestBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);

                        System.out.println(response.body().string());
                    }
                } else {

                    String response = "";

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

                    request_json_object.put("message", message[0]);
                    request_json_object.put("id_token", id_token);

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
                            while ((line = br.readLine()) != null) {
                                response += line;
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

                    }

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
