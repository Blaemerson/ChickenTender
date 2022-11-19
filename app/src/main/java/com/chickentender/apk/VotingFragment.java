package com.chickentender.apk;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chickentender.apk.databinding.FragmentVotingBinding;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class VotingFragment extends Fragment {

    private static List<Restaurant> restaurants;
    private static Restaurant currentOp;
    private int index = 0;
    private static HashMap<Restaurant, Integer> votingResults;
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
        if (index < restaurants.size() - 1)
        {
            votingResults.put(currentOp, vote);
            System.out.println("Vote: " + currentOp.getName() + " = " + votingResults.get(currentOp));
            index++;
            resetCard();

        }
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
        String url = currentOp.getPhoto();
        if (url != "")
        {
            ImageView im = getView().findViewById(R.id.restaurantImg);
            new DownloadImageTask(im).execute(url);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState)
    {
        // Inflate the layout for this fragment
        binding = FragmentVotingBinding.inflate(inflater, container, false);

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

        restaurants = ((MainActivity)getActivity()).getActiveRoom().getRestaurants();
//        System.out.println(restaurants.get(index).getPhoto());
        votingResults = new HashMap<>();
        resetCard();
        binding.buttonYes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                registerVote(1);
            }
        });
        binding.buttonNo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                registerVote(-1);
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
            bmImage.setImageBitmap(result);
        }
    }

}