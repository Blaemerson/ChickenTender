package com.chickentender.apk;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.chickentender.apk.databinding.FragmentCreateRoomBinding;
import com.chickentender.apk.databinding.FragmentWelcomeBinding;

import java.util.ArrayList;

public class WelcomeFragment extends Fragment {

    private FragmentWelcomeBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void setText(String text) {
        binding.textView.setText(text);
    }
    public void goToAttract(View v)
    {
        Intent intent = new Intent(getActivity(), RestaurantListActivity.class);
        ArrayList<String> names = new ArrayList<>();
        // Fill list with names and general areas of restaurants
        for (Restaurant r : ((MainActivity)getActivity()).getActiveRoom().getRestaurants())
        {
            names.add(r.getName() + "\n" + r.getVicinity() + "\nRating: " + r.getUserRating());
        }
        intent.putStringArrayListExtra("listview", names);
        startActivity(intent);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonDeleteRoom.setVisibility(View.GONE);
        binding.buttonRestaurantList.setVisibility(View.GONE);
        binding.buttonBeginVoting.setVisibility(View.GONE);
        binding.buttonBeginVoting.setOnClickListener(view12 -> NavHostFragment.findNavController(WelcomeFragment.this)
                .navigate(R.id.action_WelcomeFragment_to_VotingFragment));
        binding.buttonJoinRoom.setOnClickListener(view12 -> NavHostFragment.findNavController(WelcomeFragment.this)
                .navigate(R.id.action_WelcomeFragment_to_JoinRoomFragment));
        binding.buttonCreateRoom.setOnClickListener(view1 -> NavHostFragment.findNavController(WelcomeFragment.this)
                .navigate(R.id.action_WelcomeFragment_to_CreateRoomFragment));
        binding.buttonDeleteRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).deleteRoom();
                (WelcomeFragment.this).onViewCreated(view, savedInstanceState);
            }
        });
        binding.buttonRestaurantList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAttract(view);
            }
        });

        if (((MainActivity) getActivity()).getActiveRoom() == null) {
            binding.buttonCreateRoom.setVisibility(View.VISIBLE);
            binding.buttonJoinRoom.setVisibility(View.VISIBLE);
            binding.textView.setText("No room active");
        } else {
            binding.buttonCreateRoom.setVisibility(View.GONE);
            binding.buttonJoinRoom.setVisibility(View.GONE);
            binding.buttonDeleteRoom.setVisibility(View.VISIBLE);
            binding.buttonBeginVoting.setVisibility(View.VISIBLE);
            binding.textView.setText(((MainActivity) getActivity()).getActiveRoom().getName());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}