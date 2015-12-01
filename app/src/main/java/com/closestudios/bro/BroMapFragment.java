package com.closestudios.bro;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.closestudios.bro.networking.Bro;
import com.closestudios.bro.util.BroApplication;
import com.closestudios.bro.util.BroHub;
import com.closestudios.bro.util.BroPreferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;

/**
 * Created by closestudios on 11/24/15.
 */
public class BroMapFragment extends Fragment implements OnMapReadyCallback, BroHub.BroHubListener  {

    public static BroMapFragment newInstance() {
        BroMapFragment fragment = new BroMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public BroMapFragment() {
        // Required empty public constructor
    }

    MapFragment mapFragment;
    Bro[] bros;
    GoogleMap googleMap = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bro_map, container, false);
        ButterKnife.inject(this, view);

        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        // Get Bros
        BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).subscribe(this);
        BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).getBros(this, true);

        return view;
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
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if(bros != null) {
            setUpBroMarkers(getActivity(), googleMap, bros);
        }
    }

    @Override
    public void onGettingBros() {

    }

    @Override
    public void onReceiveBros(Bro[] bros) {
        this.bros = bros;
        if(googleMap != null) {
            setUpBroMarkers(getActivity(), googleMap, bros);
        }
    }

    @Override
    public void onReceiveBrosFailed(String error) {

    }

    private static void setUpBroMarkers(Context context, GoogleMap map, Bro[] bros) {
        map.clear();

        map.setMyLocationEnabled(true);

        for(Bro bro : bros){
            LatLng markerPoint = new LatLng(bro.location.latitude, bro.location.longitude);

            map.addMarker(new MarkerOptions()
                    .title(bro.broName)
                    .position(markerPoint));

        }
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            Location loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 13));
        } catch (SecurityException e) {
            e.printStackTrace();
        }


    }



}
