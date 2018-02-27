package com.hellowo.teamfinder.ui.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.databinding.FragmentTeamInfoBinding;
import com.hellowo.teamfinder.model.Team;
import com.hellowo.teamfinder.viewmodel.MainViewModel;
import com.hellowo.teamfinder.viewmodel.TeamDetailViewModel;

public class TeamInfoFragment extends Fragment {
    MainViewModel viewModel;
    FragmentTeamInfoBinding binding;

    public TeamInfoFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_info, container, false);
        binding = DataBindingUtil.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void updateUI(Team team) {
        binding.titleText.setText(team.getTitle());
    }
}
