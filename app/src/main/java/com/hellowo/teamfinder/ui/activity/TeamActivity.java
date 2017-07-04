package com.hellowo.teamfinder.ui.activity;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hellowo.teamfinder.AppConst;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.data.ConnectedUserLiveData;
import com.hellowo.teamfinder.databinding.ActivitySplashBinding;
import com.hellowo.teamfinder.databinding.ActivityTeamBinding;
import com.hellowo.teamfinder.model.Team;
import com.hellowo.teamfinder.viewmodel.SplashViewModel;
import com.hellowo.teamfinder.viewmodel.TeamViewModel;

import static com.hellowo.teamfinder.AppConst.EXTRA_TEAM_ID;

public class TeamActivity extends LifecycleActivity {
    ActivityTeamBinding binding;
    TeamViewModel viewModel;
    TeamPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_team);
        viewModel = ViewModelProviders.of(this).get(TeamViewModel.class);
        viewModel.initTeam(getIntent().getStringExtra(EXTRA_TEAM_ID));
        initLayout();
        initObserve();
    }

    private void initLayout() {
        pagerAdapter = new TeamPagerAdapter();
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
    }

    private void initObserve() {
        viewModel.team.observe(this, this::updateUI);
        viewModel.currentPage.observe(this, pos->{
            binding.teamInfoTab.setTextColor(pos == 0 ? getColor(R.color.primaryWhiteText) :  getColor(R.color.disableWhiteText));
            binding.membersTab.setTextColor(pos == 1 ? getColor(R.color.primaryWhiteText) :  getColor(R.color.disableWhiteText));
            binding.commentsTab.setTextColor(pos == 2 ? getColor(R.color.primaryWhiteText) :  getColor(R.color.disableWhiteText));
        });
    }

    private void updateUI(Team team) {
        pagerAdapter.titleText.setText(team.getTitle());
    }

    class TeamPagerAdapter extends PagerAdapter {
        View infoview;
        TextView titleText;
        RecyclerView membersView;
        RecyclerView commentsView;

        TeamPagerAdapter(){
            infoview = findViewById(R.id.infoView);
            titleText = (TextView)infoview.findViewById(R.id.titleText);
            membersView = (RecyclerView)findViewById(R.id.membersView);
            commentsView = (RecyclerView)findViewById(R.id.commentsView);
        }

        public Object instantiateItem(View collection, int position) {
            View v = null;
            switch (position) {
                case 0:
                    v = infoview;
                    break;
                case 1:
                    v = membersView;
                    break;
                case 2:
                    v = commentsView;
                    break;
            }
            return v;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }
}
