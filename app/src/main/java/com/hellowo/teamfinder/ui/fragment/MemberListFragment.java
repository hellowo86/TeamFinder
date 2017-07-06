package com.hellowo.teamfinder.ui.fragment;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.model.Member;
import com.hellowo.teamfinder.ui.adapter.MemberListAdapter;
import com.hellowo.teamfinder.viewmodel.TeamDetailViewModel;

import java.util.List;

public class MemberListFragment extends LifecycleFragment{
    TeamDetailViewModel viewModel;
    RecyclerView recyclerView;
    MemberListAdapter adapter;

    public MemberListFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(TeamDetailViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_single_list, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MemberListAdapter(
                getContext(),
                false,
                viewModel.members.getValue(),
                R.layout.list_item_member_vertical,
                new MemberListAdapter.AdapterInterface() {
                    @Override
                    public void onDeleteClicked(Member member) {}
                    @Override
                    public void onItemClicked(Member member) {}
                });
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.members.observe(this, this::updateUI);
    }

    private void updateUI(List<Member> members) {
        adapter.notifyDataSetChanged();
    }
}
