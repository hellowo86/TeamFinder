package com.hellowo.teamfinder.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.databinding.ActivityCreateTeamBinding;
import com.hellowo.teamfinder.model.Member;
import com.hellowo.teamfinder.ui.adapter.MemberListAdapter;
import com.hellowo.teamfinder.ui.adapter.decoration.HorizontalDotDecoration;
import com.hellowo.teamfinder.ui.dialog.SelectGameDialog;
import com.hellowo.teamfinder.ui.dialog.SelectTagDialog;
import com.hellowo.teamfinder.ui.dialog.SelectRoleDialog;
import com.hellowo.teamfinder.ui.dialog.SetActiveTimeDialog;
import com.hellowo.teamfinder.utils.ViewUtil;
import com.hellowo.teamfinder.viewmodel.CreateTeamViewModel;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

public class CreateTeamActivity extends LifecycleActivity {
    ActivityCreateTeamBinding binding;
    CreateTeamViewModel viewModel;
    MemberListAdapter memberListAdapter;
    ProgressDialog progressDialog;

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
        binding.titleInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setTitle(s);
            }
        });
        binding.gameSelectBtn.setOnClickListener(v->showSelectGameDialog());
        binding.activeTimeText.setOnClickListener(v->showAcitveTimeDialog());
        binding.addMemberBtn.setOnClickListener(v ->viewModel.addNewMember());
        binding.addTagBtn.setOnClickListener(v ->showSelectTagDialog());
        binding.confirmBtn.setOnClickListener(v->{
            if(viewModel.isConfirmable.getValue()) {
                viewModel.saveTeam(binding.descriptionInput.getText().toString());
            }
        });
        initMemberList();
        initDescriptionInput();
    }

    private void initMemberList() {
        binding.memberRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        memberListAdapter = new MemberListAdapter(
                this,
                true,
                viewModel.currentMember.getValue(),
                new MemberListAdapter.AdapterInterface() {
                    @Override
                    public void onDeleteClicked(Member member) {viewModel.deleteMember(member);}
                    @Override
                    public void onItemClicked(Member member) {showRoloEditDialog(member);}
                });
        binding.memberRecyclerView.setAdapter(memberListAdapter);
        binding.memberRecyclerView.addItemDecoration(
                new HorizontalDotDecoration((int) ViewUtil.dpToPx(this, 5)));
        setMemberSizeText();
    }

    private void initDescriptionInput() {
        HashTagHelper tagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.primaryText), null);
        tagHelper.handle(binding.descriptionInput);
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
            binding.addMemberBtn.setVisibility(isFullMember ? View.GONE : View.VISIBLE);
        });

        viewModel.currentMember.observe(this, memberList -> {
            memberListAdapter.notifyDataSetChanged();
            setMemberSizeText();
        });

        viewModel.isConfirmable.observe(this, isConfirmable -> {
            if(isConfirmable) {
                binding.confirmBtn.setAlpha(1f);
            }else {
                binding.confirmBtn.setAlpha(0.2f);
            }
        });

        viewModel.loading.observe(this, loading -> {
            if(loading) {
                showProgressDialog();
            }else {
                hideProgressDialog();
            }
        });

        viewModel.confirmed.observe(this, confirmed -> {
            if(confirmed) {
                finish();
            }
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

    private void showAcitveTimeDialog() {
        final SetActiveTimeDialog activeTimeDialog = new SetActiveTimeDialog();
        activeTimeDialog.setDialogInterface((text, time) -> {
            binding.activeTimeText.setText(text);
            viewModel.setActiveTime(time);
            activeTimeDialog.dismiss();
        });
        activeTimeDialog.show(getSupportFragmentManager(), activeTimeDialog.getTag());
    }

    private void showSelectTagDialog() {
        final SelectTagDialog selectTagDialog = new SelectTagDialog();
        selectTagDialog.setDialogInterface(option -> {
            String tag = option.makeTag();
            int startPos = binding.descriptionInput.getSelectionStart();
            binding.descriptionInput.getText().insert(startPos < 0 ? 0 : startPos, tag);
        });
        selectTagDialog.show(getSupportFragmentManager(), selectTagDialog.getTag());
    }

    private void showProgressDialog() {
        hideProgressDialog();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.plz_wait));
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
