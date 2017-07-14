package com.hellowo.teamfinder.ui.activity;

import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.databinding.ActivityCreateTeamBinding;
import com.hellowo.teamfinder.ui.adapter.RolesAdapter;
import com.hellowo.teamfinder.ui.dialog.SelectGameDialog;
import com.hellowo.teamfinder.ui.dialog.SelectRoleDialog;
import com.hellowo.teamfinder.ui.dialog.SelectTagDialog;
import com.hellowo.teamfinder.ui.dialog.SetActiveTimeDialog;
import com.hellowo.teamfinder.viewmodel.CreateTeamViewModel;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

public class CreateTeamActivity extends LifecycleActivity {
    ActivityCreateTeamBinding binding;
    CreateTeamViewModel viewModel;
    ProgressDialog progressDialog;
    RolesAdapter rolesAdapter;

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
        binding.contentsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setContents(s);
            }
        });
        HashTagHelper tagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.primaryText), null);
        tagHelper.handle(binding.contentsInput);
        binding.gameSelectBtn.setOnClickListener(v->showSelectGameDialog());
        binding.activeTimeBtn.setOnClickListener(v->showAcitveTimeDialog());
        binding.addMemberBtn.setOnClickListener(v ->{
            if(!viewModel.isFullMember.getValue()) {
                showRoloEditDialog(null, 1);
            }
        });
        binding.addTagBtn.setOnClickListener(v ->showSelectTagDialog());
        binding.confirmBtn.setOnClickListener(v->{
            if(viewModel.isConfirmable.getValue()) {
                viewModel.saveTeam();
            }
        });
        initRolosRecyclerView();
    }

    private void initRolosRecyclerView() {
        binding.rolesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rolesAdapter = new RolesAdapter(
                this,
                true,
                viewModel.currentRoles.getValue(),
                new RolesAdapter.AdapterInterface() {
                    @Override
                    public void onChangedRoleCount(String role, int count) {
                        viewModel.setRole(role, count);
                    }
                    @Override
                    public void onClickedRole(String role, int count) {
                        showRoloEditDialog(role, count);
                    }
                });
        binding.rolesRecyclerView.setAdapter(rolesAdapter);
    }

    private void initObserve() {
        viewModel.selectedGame.observe(this, game -> {
            binding.gameTitleText.setText(game.getTitle());
            binding.gameIconImg.setImageResource(game.getIconId());
        });

        viewModel.isFullMember.observe(this, isFullMember -> {
            if(isFullMember) {
                binding.addMemberBtn.setBackgroundResource(R.color.disableText);
            }else {
                binding.addMemberBtn.setBackgroundResource(R.drawable.accent_ripple_button);
            }
            rolesAdapter.setIsFullMember(isFullMember);
        });

        viewModel.isConfirmable.observe(this, isConfirmable -> {
            if(isConfirmable) {
                binding.confirmBtn.setBackgroundResource(R.drawable.primary_ripple_button);
            }else {
                binding.confirmBtn.setBackgroundResource(R.color.disableText);
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

        viewModel.currentRoles.observe(this, roles->{
            rolesAdapter.refresh(roles);
        });

        viewModel.needMemberSize.observe(this, size->{
            binding.memberSizeText.setText(String.format(getString(R.string.total_need_member_count), size));
            rolesAdapter.setIsOnlyOneMember(size <= 1);
        });
    }

    private void showSelectGameDialog() {
        final SelectGameDialog selectGameDialog = new SelectGameDialog();
        selectGameDialog.setDialogInterface(game -> {
            viewModel.selectGame(game);
            selectGameDialog.dismiss();
        });
        selectGameDialog.show(getSupportFragmentManager(), selectGameDialog.getTag());
    }

    private void showRoloEditDialog(String prevRole, int delta) {
        final SelectRoleDialog selectRoleDialog = new SelectRoleDialog();
        selectRoleDialog.setGame(viewModel.selectedGame.getValue());
        selectRoleDialog.setDialogInterface(role -> {
            if(TextUtils.isEmpty(prevRole)) {
                viewModel.setRole(role, 1);
            }else {
                viewModel.setRole(prevRole, -delta);
                viewModel.setRole(role, delta);
            }
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
            int startPos = binding.contentsInput.getSelectionStart();
            binding.contentsInput.getText().insert(startPos < 0 ? 0 : startPos, tag);
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
