package com.hellowo.teamfinder.ui.activity;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.databinding.ActivityCreateTeamBinding;
import com.hellowo.teamfinder.model.Member;
import com.hellowo.teamfinder.ui.adapter.MemberListAdapter;
import com.hellowo.teamfinder.ui.adapter.decoration.HorizontalSpaceItemDecoration;
import com.hellowo.teamfinder.ui.dialog.SelectGameDialog;
import com.hellowo.teamfinder.ui.dialog.SelectRoleDialog;
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
                new MemberListAdapter.MemberListInterface() {
                    @Override
                    public void onAddClicked() {viewModel.addNewMember();}
                    @Override
                    public void onDeleteClicked(Member member) {viewModel.deleteMember(member);}
                    @Override
                    public void onItemClicked(Member member) {showRoloEditDialog(member);}
                });
        binding.memberRecyclerView.setAdapter(memberListAdapter);
        HorizontalSpaceItemDecoration divider = new HorizontalSpaceItemDecoration(
                (int) ViewUtil.dpToPx(this, 5));
        binding.memberRecyclerView.addItemDecoration(divider);
        setMemberSizeText();
    }

    private void initObserve() {
        viewModel.selectedGame.observe(this, game -> {
            binding.gameTitleText.setText(game.getTitle());
            binding.gameIconImg.setImageResource(game.getIconId());
        });

        viewModel.addedMemberPos.observe(this, position -> {
            memberListAdapter.notifyItemInserted(position);
            binding.memberRecyclerView.scrollToPosition(position + 1);
            setMemberSizeText();
        });

        viewModel.deletedMemberPos.observe(this, position -> {
            memberListAdapter.notifyItemRemoved(position);
            setMemberSizeText();
        });

        viewModel.isFullMember.observe(this, isFullMember -> {
            memberListAdapter.setIsFullMember(isFullMember);
        });

        viewModel.currentMember.observe(this, memberList -> {
            memberListAdapter.notifyDataSetChanged();
            setMemberSizeText();
        });
    }

    @SuppressLint("SetTextI18n")
    private void setMemberSizeText() {
        binding.memberSizeText.setText(
                "(" + viewModel.currentMember.getValue().size() +
                        "/" + viewModel.selectedGame.getValue().getMaxMemberCount() + ")");
    }

    private void showSelectGameDialog() {
        final SelectGameDialog selectGameDialog = new SelectGameDialog();
        selectGameDialog.setDialogInterface(game -> {
            viewModel.selectGame(game);
            selectGameDialog.dismiss();
        });
        selectGameDialog.show(getSupportFragmentManager(), selectGameDialog.getTag());
    }

    private void showRoloEditDialog(Member member) {
        final SelectRoleDialog selectRoleDialog = new SelectRoleDialog();
        selectRoleDialog.setGame(viewModel.selectedGame.getValue());
        selectRoleDialog.setDialogInterface(role -> {
            member.setRole(role);
            memberListAdapter.notifyItemChanged(viewModel.currentMember.getValue().indexOf(member));
            selectRoleDialog.dismiss();
        });
        selectRoleDialog.show(getSupportFragmentManager(), selectRoleDialog.getTag());
    }

}
