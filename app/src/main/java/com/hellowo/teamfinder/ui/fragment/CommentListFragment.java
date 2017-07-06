package com.hellowo.teamfinder.ui.fragment;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.databinding.FragmentCommentsListBinding;
import com.hellowo.teamfinder.model.Comment;
import com.hellowo.teamfinder.ui.adapter.CommentListAdapter;
import com.hellowo.teamfinder.ui.adapter.decoration.VerticalSpaceDecoration;
import com.hellowo.teamfinder.utils.ViewUtil;
import com.hellowo.teamfinder.viewmodel.TeamDetailViewModel;

import java.util.List;

public class CommentListFragment extends LifecycleFragment{
    TeamDetailViewModel viewModel;
    FragmentCommentsListBinding binding;
    CommentListAdapter adapter;

    public CommentListFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(TeamDetailViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments_list, container, false);
        binding = DataBindingUtil.bind(view);
        binding.sendBtn.setOnClickListener(v->enterComment());

        binding.swipeRefreshLy.setProgressViewOffset(false, 0,
                (int) ViewUtil.dpToPx(getActivity(), 50));
        binding.swipeRefreshLy.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        binding.swipeRefreshLy.setOnRefreshListener(() -> viewModel.getComments());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CommentListAdapter(
                getContext(),
                viewModel.comments.getValue(),
                userId -> {});
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new VerticalSpaceDecoration(getContext()));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.comments.observe(this, this::updateUI);
    }

    private void updateUI(List<Comment> commentList) {
        adapter.notifyDataSetChanged();
        binding.swipeRefreshLy.setRefreshing(false);
    }

    private void enterComment() {
        if(!TextUtils.isEmpty(binding.messageInput.getText())) {
            viewModel.postComment(binding.messageInput.getText().toString());
            binding.messageInput.setText("");
            binding.swipeRefreshLy.setRefreshing(true);
        }
    }
}
