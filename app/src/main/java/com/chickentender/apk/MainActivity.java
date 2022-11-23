package com.chickentender.apk;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.chickentender.apk.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static String userID;
    private static Room activeRoom;

    private String getRandomHexString(int numChars) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < numChars) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.substring(0, numChars).toUpperCase();
    }

    public void replaceFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.joinRoomFragment,fragment)
                .commit();
    }


    public static Room joinRoom(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("rooms").whereEqualTo("id", id);
        Map<String, Object> user = new HashMap<>();

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("TAG",
                            "Document Id: " + document.getId());


                    List<HashMap<String, Object>> rs = (List<HashMap<String, Object>>) document.get("restaurants");

                    List<Restaurant> r = new ArrayList<>();

                        for (int i = 0; i < Objects.requireNonNull(rs).size(); i++) {
                            r.add(new Restaurant(
                                            rs.get(i).get("name").toString(),
                                            rs.get(i).get("restaurantID").toString(),
                                            rs.get(i).get("vicinity").toString(),
                                            (double) rs.get(i).get("latitude"),
                                            (double) rs.get(i).get("longitude"),
                                            rs.get(i).get("userRating").toString(),
                                            rs.get(i).get("photo").toString()
                                    )
                            );
                    }
                    List<String> users = (List<String>) document.get("users");

                    // Only update database if user has not already joined
                    assert users != null;
                    if (!users.contains(userID) && userID != null) {
                        users.add(userID);
                        db.collection("rooms").document(document.getId()).update("users", users);
                    }
                    activeRoom = new Room(
                            document.get("id", String.class),
                            r,
                            users,
                            document.get("hostID", String.class)
                    );
                    String roomID = activeRoom.getRoomID();
                    if (userID != null) {
                        user.put("active_room", roomID);
                        db.collection("users").document(userID).set(user).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.w("TAG", "Success adding document");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error adding document", e);
                            }
                        });
                    }

                }
            }
        });

        return activeRoom;
    }


    public void createRoom(Restaurant[] restaurants) {
        Map<String, Object> room = new HashMap<>();
        List<String> userIDs = new ArrayList<>();

        userIDs.add(userID);
        Map<String, Object> user = new HashMap<>();
        String roomID = getRandomHexString(6);
        user.put("active_room", roomID);

        room.put("id", roomID);
        room.put("users", userIDs);
        room.put("hostID", userID);
        room.put("restaurants", Arrays.asList(restaurants));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userID).set(user).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.w("TAG", "Success adding document");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Error adding document", e);
            }
        });
        db.collection("rooms").document(roomID).set(room).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Error adding document", e);
            }
        });

        activeRoom = new Room(roomID, Arrays.asList(restaurants), userIDs, userID);
    }

    public void deleteRoom() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("rooms").whereEqualTo("id", activeRoom.getRoomID());

        Map<String, Object> user = new HashMap<>();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        List<String> users = (List<String>) document.get("users");
                        assert users != null;
                        if (users.contains(userID)) {
                            users.remove(userID);
                            db.collection("rooms").document(document.getId()).update("users", users);
                        }
                        if (users.isEmpty()) {
                            Log.d("DEBUG", "Deleted room because it has no users");
                            document.getReference().delete();
                        }
                    }

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG",
                                "Document Id: " + document.getId());
                    }
                }
            }
        });

        user.put("active_room", "");
        db.collection("users").document(userID).update(user);

        this.activeRoom = null;
    }

    public Room getActiveRoom() {
        return activeRoom;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        userID = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        com.chickentender.apk.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}