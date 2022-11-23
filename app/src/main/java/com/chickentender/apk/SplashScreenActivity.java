package com.chickentender.apk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d("DEBUG", "onCreate: " + userID);
        DocumentReference doc = db.collection("users").document(userID);
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Object roomID = documentSnapshot.get("active_room");
                if (roomID == null) {
                    Log.d("DEBUG", "User has no active room");
                } else {
                    Log.d("DEBUG", "User has an active room");

                    MainActivity.joinRoom(roomID.toString());
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);

                startActivity(i);

                finish();
            }
        }, 2000);
    }
}