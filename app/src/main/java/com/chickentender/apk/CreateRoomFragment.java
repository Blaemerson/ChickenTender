package com.chickentender.apk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.chickentender.apk.databinding.FragmentCreateRoomBinding;

import java.util.Random;

public class CreateRoomFragment extends Fragment {
    private static boolean isRoomActive = false;
    private static String roomCode = "000000";
    private FragmentCreateRoomBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentCreateRoomBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        if (roomCode == "000000") {
            // Generate a random 6 character alpha-numeric code for the room
            String zeros = "000000";
            Random rnd = new Random();
            roomCode = String.format("%06x", rnd.nextInt(0x1000000));
            roomCode = zeros.substring(roomCode.length()) + roomCode;
            // TODO: Make sure this room code is unique
            // TODO: Put this code in the database
        }

        super.onViewCreated(view, savedInstanceState);

        binding.textviewSecond.setText(roomCode);
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(CreateRoomFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
        isRoomActive = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("Is Room Active? " + isRoomActive);
        System.out.println("Room code: " + roomCode);
        binding = null;
    }

}