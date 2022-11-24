package com.chickentender.apk;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chickentender.apk.databinding.FragmentVotingBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firestore.v1.DocumentTransform;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        if (getArguments() != null) {}
    }

    public void registerVote(int vote)
    {
        if (restaurants.indexOf(currentOp) < restaurants.size())
        {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> v = new HashMap<>();

            Map<String, Object> voter = new HashMap<>();
            v.put("votes", vote);
            voter.put(currentOp.getName(), v);

            DocumentReference doc = db.collection("votes").document((MainActivity.getActiveRoom().getRoomID()));

            doc.update(currentOp.getRestaurantID(), FieldValue.increment(vote)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.w("TAG", "Success registering vote");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("TAG", "Error registering vote", e);
                }
            });


            votingResults.put(currentOp, vote);
            System.out.println("Vote: " + currentOp.getName() + " = " + votingResults.get(currentOp));
            index++;
        }

    }

    public void showNextCard()
    {
        binding.voteCard.clearAnimation();
        binding.voteCard.animate().translationX(-2000).translationY(0).scaleY(0.5f).scaleX(0.5f).withEndAction(new Runnable() {
            @Override
            public void run() {
                binding.voteCard.setVisibility(View.VISIBLE);

                binding.voteCard.animate().translationX(0).scaleY(1).scaleX(1).setDuration(500).withEndAction(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }
        }).start();
        if (index >= restaurants.size() - 1) {
            NavHostFragment.findNavController(VotingFragment.this).navigate(R.id.action_FinishedVoting);
        } else {
            resetCard();
        }
    }

    public void resetCard()
    {
        binding.restaurantImg.clearColorFilter();

        currentOp = restaurants.get(index);
        binding.idRestaurantName.setText(currentOp.getName());
        binding.idRestaurantLocation.setText(currentOp.getVicinity());

        if (currentOp.getPhoto() == "") {
            binding.restaurantImg.setImageResource(R.drawable.no_image_available);
        }
        else {
            new DownloadImageTask(binding.restaurantImg).execute(currentOp.getPhoto());
        }

        if (currentOp.getUserRating() != "")
        {
            binding.rating.setRating(Float.parseFloat(currentOp.getUserRating()));
        }
        else
        {
            binding.rating.setRating(0);
        }
//        binding.restaurantImg.setImageBitmap(imageMap.get(currentOp.getName()));
        binding.voteCard.setClickable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState)
    {
        // Inflate the layout for this fragment
        binding = FragmentVotingBinding.inflate(inflater, container, false);
        restaurants = ((MainActivity)getActivity()).getActiveRoom().getRestaurants();
//        imageMap = new HashMap<>();
//
//        for (Restaurant r : restaurants) {
//            String imgURL = r.getPhoto();
//            if (imgURL == "")
//            {
//                imageMap.put(r.getName(), BitmapFactory.decodeResource(getResources(), R.drawable.no_image_available));
//            }
//            else
//            {
//                try {
//                    imageMap.put(r.getName(), new DownloadImageTask().execute(r.getPhoto()).get());
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
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

    public void animateLeft() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.restaurantImg.setColorFilter(Color.argb(.25f, 0.9f, 0.1f, 0.1f));
        }
        binding.voteCard.animate().scaleY(0.4f).scaleX(0.4f).translationX(-2000).setDuration(400).withEndAction(new Runnable() {
            @Override
            public void run() {
                binding.voteCard.setVisibility(View.INVISIBLE);

                showNextCard();

            }
        });
    }
    public void animateRight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.restaurantImg.setColorFilter(Color.argb(.25f, 0.0f, 1.0f, 0.1f));
        }
        binding.voteCard.animate().scaleY(0.4f).scaleX(0.4f).translationX(2000).setDuration(400).withEndAction(new Runnable() {
            @Override
            public void run() {
                binding.voteCard.setVisibility(View.INVISIBLE);

                showNextCard();

            }
        });
    }
    public void animateDown() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.restaurantImg.setColorFilter(Color.argb(.25f, 1.0f, 0.1f, 0.1f));
        }
        binding.voteCard.animate().scaleX(0.4f).scaleY(0.4f).setDuration(400).withEndAction(new Runnable() {
            @Override
            public void run() {
                binding.voteCard.animate().translationY(-200).setDuration(400).setStartDelay(50).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        binding.voteCard.animate().scaleX(0.2f).scaleY(0.2f).translationY(300).setDuration(500).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                binding.voteCard.animate().translationY(200).setDuration(400).setStartDelay(50).start();
                                binding.voteCard.setVisibility(View.INVISIBLE);
                                showNextCard();

                            }
                        });
                    }
                });
            }
        });
    }
    public void animateUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.restaurantImg.setColorFilter(Color.argb(.35f, 0.5f, 0.5f, 0.5f));
        }
        binding.voteCard.animate().scaleY(0.4f).scaleX(0.4f).translationY(-2000).setDuration(400).withEndAction(new Runnable() {
            @Override
            public void run() {
                binding.voteCard.setVisibility(View.INVISIBLE);

                showNextCard();

            }
        });
    }

    private float x1;
    private float x2;
    private float y1;
    private float y2;
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

//        System.out.println(restaurants.get(index).getPhoto());
        votingResults = new HashMap<>();
        resetCard();
        binding.voteCard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = motionEvent.getX();
                        y1 = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = motionEvent.getX();
                        y2 = motionEvent.getY();
                        binding.voteCard.setClickable(false);
                        break;
                }
                if (x1 - x2 > 300) {
                    registerVote(-1);
                    animateLeft();
                    return true;
                }
                else if (x1 - x2 < -300) {
                    registerVote(1);
                    animateRight();
                    return true;
                }
                return true;
            }
        });
        binding.buttonMaybe.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                registerVote(0);
                vibe.vibrate(25);

                animateUp();
            }
        });
        binding.buttonYes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                registerVote(1);
                vibe.vibrate(25);

                animateRight();
            }
        });
        binding.buttonNo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                registerVote(-1);
                vibe.vibrate(25);

                animateLeft();
            }
        });
        binding.buttonHardNo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                vibe.vibrate(25);

                registerVote(-3);

                animateDown();
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