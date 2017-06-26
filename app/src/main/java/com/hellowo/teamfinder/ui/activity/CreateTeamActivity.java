package com.hellowo.teamfinder.ui.activity;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.databinding.ActivityCreateTeamBinding;
import com.hellowo.teamfinder.ui.adapter.decoration.HorizontalSpaceItemDecoration;
import com.hellowo.teamfinder.ui.adapter.MemberListAdapter;
import com.hellowo.teamfinder.ui.dialog.SelectGameDialog;
import com.hellowo.teamfinder.utils.ViewUtil;
import com.hellowo.teamfinder.viewmodel.CreateTeamViewModel;

public class CreateTeamActivity extends LifecycleActivity {
    ActivityCreateTeamBinding binding;
    CreateTeamViewModel viewModel;
    MemberListAdapter memberListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_team);
        viewModel = ViewModelProviders.of(this).get(CreateTeamViewModel.class);

        initLayout();
        initObserve();
    }

    private void initLayout() {
        binding.backBtn.setOnClickListener(v->finish());
        binding.gameSelectBtn.setOnClickListener(v-> showSelectGameDialog());
        initMemberList();
    }

    private void initMemberList() {
        binding.memberRecyclerView.setHasFixedSize(true);
        binding.memberRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        memberListAdapter = new MemberListAdapter(
                this,
                true,
                viewModel.currentMember.getValue(),
                () -> {
                    viewModel.addNewMember();
                });
        binding.memberRecyclerView.setAdapter(memberListAdapter);
        HorizontalSpaceItemDecoration divider = new HorizontalSpaceItemDecoration(
                (int) ViewUtil.dpToPx(this, 5));
        binding.memberRecyclerView.addItemDecoration(divider);
    }

    private void initObserve() {
        viewModel.selectedGame.observe(this, game -> {
            binding.gameTitleText.setText(game.getTitle());
            binding.gameIconImg.setImageResource(game.getIconId());
        });
    }

    private void showSelectGameDialog() {
        BottomSheetDialogFragment bottomSheetDialogFragment = new SelectGameDialog();
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }
}
