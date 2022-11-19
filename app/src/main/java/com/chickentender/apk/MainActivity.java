package com.chickentender.apk;

import android.app.Fragment;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.chickentender.apk.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String userID;
    private String roomID;
    private Room activeRoom;

    private String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }

    public boolean joinRoom(String id)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("rooms").whereEqualTo("id", id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG",
                                "Document Id: " + document.getId());

                        List<HashMap<String, Object>> rs = (List<HashMap<String, Object>>) document.get("restaurants");

                        List<Restaurant> r = new ArrayList<>();

                        for (int i = 0; i < rs.size(); i++) {
                            r.add(new Restaurant(
                                    rs.get(i).get("name").toString(),
                                    rs.get(i).get("vicinity").toString(),
                                    (double) rs.get(i).get("latitude"),
                                    (double) rs.get(i).get("longitude"),
                                    rs.get(i).get("userRating").toString(),
                                    rs.get(i).get("photo").toString()
                                    )
                            );
                        }
                        List<String> users = (List<String>) document.get("users");
                        activeRoom = new Room(
                                document.get("id", String.class),
                                r,
                                users,
                                document.get("hostID", String.class)
                        );
                    }
                }
            }
        });
        // Reload current fragment
//        db.collection("rooms")
//                .whereEqualTo("roomID", id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        Log.d("yes", "Successful query");
//                    }
//                });
        return true;
    }
    public void createRoom(String name, Restaurant[] restaurants, double radius, double lat, double lng)
    {
        Map<String, Object> room = new HashMap<>();
        List<String> userIDs = new ArrayList<>();
        userID = getRandomHexString(6);
        userIDs.add(userID);
        roomID = getRandomHexString(6);
        room.put("id", roomID);
        room.put("users", userIDs);
        room.put("hostID", userID);
        room.put("restaurants", Arrays.asList(restaurants));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("rooms").add(room).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("yes", "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("yes", "Error adding document", e);
            }
        });

        activeRoom = new Room(roomID, Arrays.asList(restaurants), userIDs, userID);
    }
    public void deleteRoom()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("rooms").whereEqualTo("id", activeRoom.getName());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG",
                                "Document Id: " + document.getId());
                        document.getReference().delete();
                    }
                }
        }
    });
        db.collection("rooms").document(activeRoom.getName()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Room has been deleted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Room has been deleted.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        this.activeRoom = null;
    }
    public Room getActiveRoom()
    {
        return activeRoom;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}