package com.chickentender.apk;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.chickentender.apk.databinding.FragmentJoinRoomBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JoinRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JoinRoomFragment extends Fragment
{

    private FragmentJoinRoomBinding binding;
    public JoinRoomFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment JoinRoomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JoinRoomFragment newInstance() {
        JoinRoomFragment fragment = new JoinRoomFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.editTextRoomID.setOnEditorActionListener(
            new TextView.OnEditorActionListener()
            {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
                {
                    if (i == EditorInfo.IME_ACTION_DONE)
                    {
                        InputMethodManager imm =
                            (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    }
                    return true;
                }
            }
        );
        binding.buttonBack.setOnClickListener(
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    NavHostFragment.findNavController(JoinRoomFragment.this)
                            .navigate(R.id.action_backFromJoinRoomScreen);
                }
            }
        );
        binding.buttonRoomSearch.setOnClickListener(
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Toast.makeText(view.getContext(), "Joined Room", Toast.LENGTH_LONG).show();
                    // TODO: get room from server
                }
            }
        );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState)
    {
        binding = FragmentJoinRoomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}