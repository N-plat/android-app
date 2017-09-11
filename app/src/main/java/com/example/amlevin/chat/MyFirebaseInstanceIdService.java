package com.example.amlevin.chat;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.google.android.gms.internal.zzs.TAG;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    public MyFirebaseInstanceIdService() {
    }

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(String token) {
        new DeviceRegistration().execute(token);
    }

}
