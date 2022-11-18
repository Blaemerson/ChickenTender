package com.chickentender.apk;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.chickentender.apk.databinding.FragmentVotingBinding;
import com.chickentender.apk.databinding.FragmentWelcomeBinding;

import java.io.InputStream;
import java.net.URL;

import kotlinx.coroutines.Dispatchers;

public class VotingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static Restaurant[] restaurants = {};
    private static int votes[] = {};
    private int restaurantIndex = 0;
    private static Restaurant currentRestaurant;
    private FragmentVotingBinding binding;
    private float x1,x2;
    private float y1,y2;
    static final int MIN_DISTANCE = 100;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
    public static VotingFragment newInstance(Restaurant[] restaurants) {
        VotingFragment fragment = new VotingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(restaurantName);
            System.out.println(mParam1);
        }
    }

    public void resetCard() {
        binding.idRestaurantName.setText(restaurants[restaurantIndex].getName());
        binding.idRestaurantLocation.setText(restaurants[restaurantIndex].getVicinity());
        ImageView im = (ImageView) getView().findViewById(R.id.restaurantImg);
        String url = restaurants[restaurantIndex].getPhoto();
        binding.rating.setRating(Float.valueOf(restaurants[restaurantIndex].getUserRating()));
        if (url != "") {
            new DownloadImageTask((ImageView) im)
                    .execute(url);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentVotingBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        restaurants = ((MainActivity)getActivity()).getActiveRoom().getRestaurants();
//        im.setImageDrawable(LoadImageFromWebOperations(restaurants[restaurantIndex].getPhoto()));
        System.out.println(restaurants[restaurantIndex].getPhoto());
        votes = new int[restaurants.length];
        resetCard();
        binding.buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (restaurantIndex < restaurants.length - 1) {
                    votes[restaurantIndex] = 1;
                    restaurantIndex += 1;
                    resetCard();
                }
            }
        });
        binding.buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (restaurantIndex < restaurants.length - 1) {
                    votes[restaurantIndex] = -1;
                    restaurantIndex += 1;
                    resetCard();
                }
            }
        });
        binding.voteCard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // variables to store current configuration of vote card.
                DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
                float cardWidth = binding.voteCard.getWidth();
                float cardHeight = binding.voteCard.getHeight();
                float cardStart = (displayMetrics.widthPixels / 2) - (cardWidth / 2);

                // ... Respond to touch events
                switch(motionEvent.getAction())
                {
                }
                return false;
            }
        });

    }
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}