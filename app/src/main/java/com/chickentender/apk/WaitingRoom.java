package com.chickentender.apk;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chickentender.apk.databinding.FragmentWaitingRoomBinding;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WaitingRoom#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WaitingRoom extends Fragment {

    public WaitingRoom() {
        // Required empty public constructor
    }
    private FragmentWaitingRoomBinding binding;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WaitingRoom.
     */
    // TODO: Rename and change types and number of parameters
    public static WaitingRoom newInstance(String param1, String param2) {
        WaitingRoom fragment = new WaitingRoom();
        Bundle args = new Bundle();
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
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(WaitingRoom.this)
                        .navigate(R.id.action_Waiting_to_VotingFragment);

            }
        });
        binding.textView.setText("Members: " + ((MainActivity) getActivity()).getActiveRoom().getMembers().size() + "/" + "6");
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


        return binding.getRoot();    }
}