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
        new DownloadImageTask((ImageView) im)
                .execute(restaurants[restaurantIndex].getPhoto());

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

                    case MotionEvent.ACTION_DOWN:
                        x1 = motionEvent.getX();
                        y1 = motionEvent.getY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        x2 = motionEvent.getX();
                        float deltaX = x2 - x1;
                        y2 = motionEvent.getY();
                        float deltaY = y2 - y1;
                        System.out.println("Hello");
                        if (deltaY < -150) { // or newX < cardStart + cardHeight
                            binding.voteCard.animate().y(
                                            Math.min(cardStart - cardHeight, y2 - (cardHeight / 2))
                                    )
                                    .setDuration(400)
                                    .start();
                            System.out.println("down to up");
                            votes[restaurantIndex] = 0;
                            Toast.makeText(view.getContext(), "Voted 'I don't care'", Toast.LENGTH_SHORT).show ();
                        }
                        else if (deltaY > 150) { // or newX < cardStart + cardHeight
                            binding.voteCard.animate().y(
                                        2000
                                    )
                                    .setDuration(400)
                                    .start();
                            System.out.println("up to down");
                            votes[restaurantIndex] = -2;
                            Toast.makeText(view.getContext(), "Voted 'Hard Pass'", Toast.LENGTH_SHORT).show ();
                        }
                        else if (deltaX < -150) { // or newX < cardStart + cardWidth
                            binding.voteCard.animate().x(
                                      Math.min(cardStart - cardWidth, x2 - (cardWidth / 2))
                                    )
                                    .setDuration(400)
                                    .start();
                            binding.voteCard.animate().setStartDelay(400).translationX(0).setDuration(300).start();
                            System.out.println("right to left");
                            votes[restaurantIndex] = -1;
                            Toast.makeText(view.getContext(), "Voted 'No'", Toast.LENGTH_SHORT).show ();
                        }
                        else if (deltaX > 150) {
                            binding.voteCard.animate().x(
                                            Math.min(cardStart + cardWidth, x2 + (cardWidth / 2))
                                    )
                                    .setDuration(400)
                                    .start();
                            System.out.println("left to right");
                            votes[restaurantIndex] = 1;
                            Toast.makeText(view.getContext(), "Voted 'Yes'", Toast.LENGTH_SHORT).show ();
                        }
//                        restaurantIndex += 1;
                        if (restaurantIndex < restaurants.length) {
                            resetCard();
                        }
                        else {
                        }
                        break;
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