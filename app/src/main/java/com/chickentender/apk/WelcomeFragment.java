package com.chickentender.apk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.chickentender.apk.databinding.FragmentCreateRoomBinding;
import com.chickentender.apk.databinding.FragmentWelcomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        if (((MainActivity)getActivity()).getActiveRoom() != null) {
            NavHostFragment.findNavController(this).navigate(R.id.action_Welcome_to_WaitingRoom);
        }

        binding.buttonJoinRoom.setOnClickListener(view12 -> {
            NavHostFragment.findNavController(WelcomeFragment.this)
                    .navigate(R.id.action_WelcomeFragment_to_JoinRoomFragment);
        });
        binding.buttonCreateRoom.setOnClickListener(view1 -> NavHostFragment.findNavController(WelcomeFragment.this)
                .navigate(R.id.action_WelcomeFragment_to_CreateRoomFragment));


//        binding.buttonRestaurantList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                goToAttract(view);
//            }
//        });
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}