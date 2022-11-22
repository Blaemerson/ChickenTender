package com.chickentender.apk;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chickentender.apk.databinding.FragmentVotingBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class VotingFragment extends Fragment {

    private RecyclerView recyclerView;
    private static List<Restaurant> restaurants;
    private static Restaurant currentOp;
    private int index = 0;
    private static HashMap<Restaurant, Integer> votingResults;
    private static HashMap<String, Bitmap> imageMap;
    private FragmentVotingBinding binding;

    public VotingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param restaurants The restaurants for this voting session.
     * @return A new instance of fragment VotingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VotingFragment newInstance(List<Restaurant> restaurants) {
        VotingFragment fragment = new VotingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("rooms").whereEqualTo()
//        document.getReference().addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w("TAG", "Listen failed.", e);
//                    return;
//                }
//
//                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
//                        ? "Local" : "Server";
//
//                if (snapshot != null && snapshot.exists()) {
//                    Log.d("TAG", source + " data: " + snapshot.getData());
//                } else {
//                    Log.d("TAG", source + " data: null");
//                }
//            }
//        });
        if (getArguments() != null) {}
    }

    public void registerVote(int vote)
    {
        if (index < restaurants.size() - 1)
        {
            votingResults.put(currentOp, vote);
            System.out.println("Vote: " + currentOp.getName() + " = " + votingResults.get(currentOp));
            index++;

        }
    }

    public void showNextCard()
    {
        binding.voteCard.clearAnimation();
        binding.voteCard.animate().translationX(-2000).translationY(0).scaleY(1).scaleX(1).withEndAction(new Runnable() {
            @Override
            public void run() {
                binding.voteCard.setVisibility(View.VISIBLE);

                binding.voteCard.animate().translationX(0).setDuration(500).withEndAction(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }
        }).start();
        resetCard();
    }

    public void resetCard()
    {

        currentOp = restaurants.get(index);
        binding.idRestaurantName.setText(currentOp.getName());
        binding.idRestaurantLocation.setText(currentOp.getVicinity());

        if (currentOp.getUserRating() != "")
        {
            binding.rating.setRating(Float.parseFloat(currentOp.getUserRating()));
        }
        else
        {
            binding.rating.setRating(0);
        }
        binding.restaurantImg.setImageBitmap(imageMap.get(currentOp.getName()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState)
    {
        // Inflate the layout for this fragment
        binding = FragmentVotingBinding.inflate(inflater, container, false);
        restaurants = ((MainActivity)getActivity()).getActiveRoom().getRestaurants();
        imageMap = new HashMap<>();

        for (Restaurant r : restaurants) {
            String imgURL = r.getPhoto();
            if (imgURL == "")
            {
                imageMap.put(r.getName(), BitmapFactory.decodeResource(getResources(), R.drawable.no_image_available));
            }
            else
            {
                try {
                    imageMap.put(r.getName(), new DownloadImageTask().execute(r.getPhoto()).get());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return binding.getRoot();
    }

    public static Drawable LoadImageFromWebOperations(String url)
    {
        try
        {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

//        System.out.println(restaurants.get(index).getPhoto());
        votingResults = new HashMap<>();
        resetCard();
        binding.buttonMaybe.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                binding.voteCard.animate().translationY(-2000).setDuration(500).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        binding.voteCard.setVisibility(View.INVISIBLE);

                        showNextCard();

                    }
                });
                registerVote(0);

            }
        });
        binding.buttonYes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                binding.voteCard.animate().translationX(2000).setDuration(500).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        binding.voteCard.setVisibility(View.INVISIBLE);

                        showNextCard();

                    }
                });

                registerVote(1);
            }
        });
        binding.buttonNo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                binding.voteCard.animate().translationY(2000).setDuration(500).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        binding.voteCard.setVisibility(View.INVISIBLE);

                        showNextCard();

                    }
                });

                showNextCard();
                registerVote(-1);

            }
        });
        binding.buttonHardNo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                binding.voteCard.animate().scaleX(0.5f).scaleY(0.5f).setDuration(200).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        binding.voteCard.animate().translationY(-200).setDuration(500).setStartDelay(50).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                binding.voteCard.animate().translationY(300).setDuration(500).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.voteCard.animate().translationY(200).setDuration(500).setStartDelay(50).start();
                                        binding.voteCard.animate().scaleY(0.1f).scaleX(0.1f).setDuration(500).start();
                                        binding.voteCard.setVisibility(View.INVISIBLE);
                                        showNextCard();

                                    }
                                });
                            }
                        });
                    }
                });




                registerVote(-3);

            }
        });

    }
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
    {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage)
        {
            this.bmImage = bmImage;
        }
        public DownloadImageTask()
        {
        }


        protected Bitmap doInBackground(String... urls)
        {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try
            {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            }
            catch (Exception e)
            {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result)
        {

                System.out.println("Here2");
                if (bmImage != null) {
                    bmImage.setImageBitmap(result);
                }
        }
    }

}