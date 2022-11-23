package com.chickentender.apk;


import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chickentender.apk.databinding.FragmentWaitingRoomBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WaitingRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WaitingRoomFragment extends Fragment {

    public WaitingRoomFragment() {
        // Required empty public constructor
    }
    private FragmentWaitingRoomBinding binding;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static WaitingRoomFragment newInstance(Room currentRoom) {
        WaitingRoomFragment fragment = new WaitingRoomFragment();
        Bundle args = new Bundle();
        args.putSerializable("userID", currentRoom);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWaitingRoomBinding.inflate(inflater, container, false);
        binding.button.setOnClickListener(view -> {
                NavHostFragment.findNavController(WaitingRoomFragment.this)
                        .navigate(R.id.action_Waiting_to_VotingFragment);
        });
        binding.textView.setText("Members: " + ((MainActivity)getActivity()).getActiveRoom().getMembers().size());
        binding.roomInfoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getContext().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", binding.roomID.getText());
                clipboard.setPrimaryClip(clip);
            }
        });
        binding.roomID.setText(((MainActivity) getActivity()).getActiveRoom().getRoomID());
        System.out.println(
                ((MainActivity) getActivity()).getActiveRoom().getRoomID()

        );
        binding.buttonDeleteRoom.setOnClickListener(view14 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view14.getContext());
            builder.setCancelable(true);
            builder.setTitle("Leave this Group?");
            builder.setMessage("If you leave this group, not be able to begin voting");
            builder.setPositiveButton("Confirm", (dialog, which) -> {
                ((MainActivity) getActivity()).deleteRoom();
                (WaitingRoomFragment.this).onViewCreated(view14, savedInstanceState);
                NavHostFragment.findNavController(WaitingRoomFragment.this)
                        .navigate(R.id.action_Waiting_to_WelcomeFragment);
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {});

            AlertDialog dialog = builder.create();
            dialog.show();


        });


        return binding.getRoot();    }
}