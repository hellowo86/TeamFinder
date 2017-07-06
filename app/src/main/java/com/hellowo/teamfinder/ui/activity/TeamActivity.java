package com.hellowo.teamfinder.ui.activity;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.databinding.ActivityTeamBinding;
import com.hellowo.teamfinder.ui.fragment.CommentListFragment;
import com.hellowo.teamfinder.ui.fragment.MemberListFragment;
import com.hellowo.teamfinder.ui.fragment.TeamInfoFragment;
import com.hellowo.teamfinder.viewmodel.TeamDetailViewModel;

import static com.hellowo.teamfinder.AppConst.EXTRA_TEAM_ID;

public class TeamActivity extends LifecycleActivity {
    ActivityTeamBinding binding;
    TeamDetailViewModel viewModel;
    TeamPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_team_detail);
        viewModel = ViewModelProviders.of(this).get(TeamDetailViewModel.class);
        viewModel.initTeam(getIntent().getStringExtra(EXTRA_TEAM_ID));
        initLayout();
        initObserve();
    }

    private void initLayout() {
        pagerAdapter = new TeamPagerAdapter(getSupportFragmentManager());
        binding.viewPager.setAdapter(pagerAdapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }
            @Override
            public void onPageSelected(int pos) { viewModel.currentPage.setValue(pos); }
            @Override
            public void onPageScrollStateChanged(int i) {}
        });
        binding.backBtn.setOnClickListener(v->finish());
        binding.teamInfoTab.setOnClickListener(v->binding.viewPager.setCurrentItem(0, true));
        binding.membersTab.setOnClickListener(v->binding.viewPager.setCurrentItem(1, true));
        binding.commentsTab.setOnClickListener(v->binding.viewPager.setCurrentItem(2, true));
        binding.progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.colorPrimary),
                android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    private void initObserve() {
        viewModel.currentPage.observe(this, pos->{
            binding.teamInfoTab.setTextColor(pos == 0 ? getColor(R.color.primaryWhiteText) :  getColor(R.color.disableWhiteText));
            binding.membersTab.setTextColor(pos == 1 ? getColor(R.color.primaryWhiteText) :  getColor(R.color.disableWhiteText));
            binding.commentsTab.setTextColor(pos == 2 ? getColor(R.color.primaryWhiteText) :  getColor(R.color.disableWhiteText));
        });
        viewModel.loading.observe(this, loading -> {binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);});
    }

    class TeamPagerAdapter extends FragmentPagerAdapter {
        private final int NUM_ITEMS = 3;

        public TeamPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TeamInfoFragment();
                case 1:
                    return new MemberListFragment();
                case 2:
                    return new CommentListFragment();
                default:
                    return null;
            }
        }
    }
}
