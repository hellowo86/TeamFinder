package com.hellowo.teamfinder.ui.fragment;

import android.arch.lifecycle.LifecycleFragment;
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
import com.hellowo.teamfinder.viewmodel.TeamViewModel;

public class TeamInfoFragment extends LifecycleFragment{
    TeamViewModel viewModel;
    FragmentTeamInfoBinding binding;

    public TeamInfoFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(TeamViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_info, container, false);
        binding = DataBindingUtil.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.team.observe(this, this::updateUI);
    }

    private void updateUI(Team team) {
        binding.titleText.setText(team.getTitle());
    }
}
