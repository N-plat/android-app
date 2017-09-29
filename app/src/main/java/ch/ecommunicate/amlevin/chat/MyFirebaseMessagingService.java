package ch.ecommunicate.amlevin.chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.google.android.gms.internal.zzs.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private LocalBroadcastManager broadcaster;

    private static final String TAG = "MyFirebaseMessaging";

    public MyFirebaseMessagingService() {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        broadcaster = LocalBroadcastManager.getInstance(this);

        //Log.d(TAG,remoteMessage.getNotification().getBody().toString());

        try {

            JSONObject new_message_json = new JSONObject(remoteMessage.getNotification().getBody().toString());

            Intent intent = new Intent("new_message");

            intent.putExtra("contact",new_message_json.getString("contact"));
            intent.putExtra("message",new_message_json.getString("message"));

            broadcaster.sendBroadcast(intent);


        } catch (JSONException e) {

            //Log.d(TAG,"JSON exception 1");

        }


        Log.i(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }
}
