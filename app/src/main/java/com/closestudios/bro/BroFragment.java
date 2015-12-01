package com.closestudios.bro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.closestudios.bro.util.BroListView;
import com.closestudios.bro.util.BroPreferences;
import com.closestudios.bro.util.BroViewAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class BroFragment extends Fragment implements BroHub.BroHubListener {

    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.my_recycler_view)
    RecyclerView recyclerView;
    BroViewAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    @InjectView(R.id.tvNoBros)
    TextView tvNoBros;



    public static BroFragment newInstance() {
        BroFragment fragment = new BroFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public BroFragment() {
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
        setHasOptionsMenu(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new BroViewAdapter();
        recyclerView.setAdapter(mAdapter);

        // Get Bros
        BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).subscribe(this);
        BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).getBros(this, true);

        // Show Progress Bar
        progressBar.setVisibility(View.VISIBLE);
        tvNoBros.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bro_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_bro:
                ModifyBroDialogFragment addDialog = ModifyBroDialogFragment.getInstance(ModifyBroDialogFragment.ModifyBroType.Add);
                addDialog.show(getActivity().getSupportFragmentManager(), "add_bro");
                return true;
            case R.id.remove_bro:
                ModifyBroDialogFragment removeDialog = ModifyBroDialogFragment.getInstance(ModifyBroDialogFragment.ModifyBroType.Add);
                removeDialog.show(getActivity().getSupportFragmentManager(), "remove_bro");
                return true;
            case R.id.block_bro:
                ModifyBroDialogFragment blockDialog = ModifyBroDialogFragment.getInstance(ModifyBroDialogFragment.ModifyBroType.Add);
                blockDialog.show(getActivity().getSupportFragmentManager(), "block_bro");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGettingBros() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReceiveBros(Bro[] bros) {

        // Has bros?
        tvNoBros.setVisibility(bros.length == 0 ? View.GONE : View.VISIBLE);

        // Show Bros
        progressBar.setVisibility(View.GONE);
        BroListView[] broListViews = new BroListView[bros.length];
        for(int i=0;i<bros.length;i++) {
            broListViews[i] = new BroListView(bros[i]);
        }
        mAdapter.setBros(broListViews);

    }

    @Override
    public void onReceiveBrosFailed(String error) {

        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), error,Toast.LENGTH_SHORT).show();

    }
}
