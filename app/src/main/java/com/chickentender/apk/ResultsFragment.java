package com.chickentender.apk;

import android.graphics.Bitmap;
import android.icu.text.SymbolTable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chickentender.apk.databinding.FragmentResultsBinding;
import com.chickentender.apk.databinding.FragmentWaitingRoomBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FragmentResultsBinding binding;
    private String roomID;
    private List<Restaurant> restaurants;

    // TODO: Rename and change types of parameters
    private static Map<String, Integer> results;

    public ResultsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ResultsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultsFragment newInstance(HashMap<String, Bitmap> imageMap) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable("ImageMap", imageMap);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        roomID = ((MainActivity) getActivity()).getActiveRoom().getRoomID();
        restaurants = ((MainActivity) getActivity()).getActiveRoom().getRestaurants();
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button even
                Log.d("BACKBUTTON", "Back button clicks");
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentResultsBinding.inflate(inflater, container, false);
        binding.result.setVisibility(View.INVISIBLE);
        binding.done.setVisibility(View.GONE);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
db.collection("votes").document(roomID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
    @Override
    public void onSuccess(DocumentSnapshot documentSnapshot) {
        Map<String, Object> results = documentSnapshot.getData();
        long mostVotes = 0;
        Restaurant highest = null;
        for (Restaurant r : restaurants) {
            if (((Long) results.get(r.getRestaurantID())) >= mostVotes) {
                mostVotes = (Long) results.get(r.getRestaurantID());
                highest = r;
            }
        }

        binding.idRestaurantName.setText(highest.getName());
        binding.idRestaurantLocation.setText(highest.getVicinity());
        binding.restaurantImg.setImageBitmap(((HashMap<String, Bitmap>) getArguments().get("ImageMap")).get(highest.getName()));
    }
});
        ((MainActivity)getActivity()).leaveRoom();
        db.collection("rooms").document(roomID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                List<String> users = (List<String>) value.get("users");
                if (users != null) {
                    if (users.size() == 0) {
                        binding.loadingBar.setVisibility(View.GONE);
                        binding.result.setVisibility(View.VISIBLE);
                        binding.done.setVisibility(View.VISIBLE);
                        value.getReference().delete();
                        db.collection("votes").document(roomID).delete();

                        System.out.println("helo");
                    }
                }
            }
        });

binding.done.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        NavHostFragment.findNavController(ResultsFragment.this).navigate(R.id.action_Results_to_WelcomeFragment);
    }
});

        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}