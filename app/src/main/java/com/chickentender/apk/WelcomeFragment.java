package com.chickentender.apk;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class WelcomeFragment extends Fragment
{
    private FragmentWelcomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonDeleteRoom.setVisibility(View.GONE);
        binding.buttonBeginVoting.setVisibility(View.GONE);
        binding.buttonBeginVoting.setOnClickListener(
            view13 ->
            {
                VotingFragment v = VotingFragment.newInstance(((MainActivity)getActivity()).getActiveRoom().getRestaurants());
                NavHostFragment.findNavController(WelcomeFragment.this)
                        .navigate(R.id.action_WelcomeFragment_to_VotingFragment);
            }
        );
        binding.buttonJoinRoom.setOnClickListener(view12 -> NavHostFragment.findNavController(WelcomeFragment.this)
                 .navigate(R.id.action_WelcomeFragment_to_JoinRoomFragment));
        binding.buttonCreateRoom.setOnClickListener(view1 -> NavHostFragment.findNavController(WelcomeFragment.this)
                .navigate(R.id.action_WelcomeFragment_to_CreateRoomFragment));
        binding.buttonDeleteRoom.setOnClickListener(view14 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view14.getContext());
            builder.setCancelable(true);
            builder.setTitle("Leave this Group?");
            builder.setMessage("If you leave this group, not be able to begin voting");
            builder.setPositiveButton("Confirm", (dialog, which) -> {
                        ((MainActivity) getActivity()).deleteRoom();
                        (WelcomeFragment.this).onViewCreated(view14, savedInstanceState);
                    });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {});

            AlertDialog dialog = builder.create();
            dialog.show();
        });
//        binding.buttonRestaurantList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                goToAttract(view);
//            }
//        });

        if (((MainActivity) getActivity()).getActiveRoom() == null)
        {
            binding.buttonCreateRoom.setVisibility(View.VISIBLE);
            binding.buttonJoinRoom.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.buttonCreateRoom.setVisibility(View.GONE);
            binding.buttonJoinRoom.setVisibility(View.GONE);
            binding.buttonDeleteRoom.setVisibility(View.VISIBLE);
            binding.buttonBeginVoting.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}