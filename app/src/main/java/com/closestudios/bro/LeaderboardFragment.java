package com.closestudios.bro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.closestudios.bro.networking.Bro;
import com.closestudios.bro.util.BroApplication;
import com.closestudios.bro.util.BroHub;
import com.closestudios.bro.util.BroLeaderView;
import com.closestudios.bro.util.BroListView;
import com.closestudios.bro.util.BroPreferences;
import com.closestudios.bro.util.BroViewAdapter;

import java.util.Arrays;
import java.util.Comparator;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by closestudios on 11/8/15.
 */
public class LeaderboardFragment extends Fragment implements BroHub.BroHubListener,
        SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.my_recycler_view)
    RecyclerView recyclerView;
    BroViewAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    @InjectView(R.id.tvNoBros)
    TextView tvNoBros;
    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    public static LeaderboardFragment newInstance() {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).subscribe(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).unsubscribe(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bro_list, container, false);
        ButterKnife.inject(this, view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new BroViewAdapter();
        recyclerView.setAdapter(mAdapter);

        // Get Bros
        BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).subscribe(this);
        BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).getBros(this, true);

        // Show Progress Bar
        swipeRefreshLayout.setRefreshing(BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).isGettingBros());
        tvNoBros.setVisibility(!BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).isGettingBros() && BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).getBrosCache() != null &&
                BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).getBrosCache().length == 0 ? View.VISIBLE : View.GONE);

        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onGettingBros() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

    }

    @Override
    public void onReceiveBros(final Bro[] bros) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Has bros?
                tvNoBros.setVisibility(bros.length == 0 ? View.VISIBLE : View.GONE);

                // Show Bros
                swipeRefreshLayout.setRefreshing(false);

                Arrays.sort(bros, new Comparator<Bro>() {
                    @Override
                    public int compare(Bro b1, Bro b2) {
                        return b1.totalTimeSecs - b2.totalTimeSecs;
                    }
                });

                BroLeaderView[] broLeaderViews = new BroLeaderView[bros.length];
                for (int i = 0; i < bros.length; i++) {
                    broLeaderViews[i] = new BroLeaderView(bros[i], i + 1);
                }
                mAdapter.setBros(broLeaderViews);

            }
        });

    }

    @Override
    public void onReceiveBrosFailed(final String error) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onRefresh() {
        BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).getBros(this, false);
    }

}
