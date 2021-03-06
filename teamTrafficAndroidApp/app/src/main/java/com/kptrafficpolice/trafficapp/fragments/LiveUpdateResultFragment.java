package com.kptrafficpolice.trafficapp.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kptrafficpolice.trafficapp.R;
import com.kptrafficpolice.trafficapp.utilities.LiveUpdateMapCoordinates;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

//raabta
//rabta
public class LiveUpdateResultFragment extends Fragment {

    ArrayList<LatLng> locations = new ArrayList<>();
    MapView mMapView;
    String strStatus, strRoadName;
    Bundle args;
    PolylineOptions polylineOptions;
    Fragment fragment;
    Polyline polyline;
    View view;
    TextView tvRoadStatus, tvUpdateTime;
    ImageView ivHomeButton, ivSettingButton, ivWebsiteButton;
    CardView cardView;
    boolean timeFlag = false;
    private GoogleMap googleMap;

    public LiveUpdateResultFragment() {
    }


    public static LiveUpdateResultFragment newInstance() {
        return new LiveUpdateResultFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_live_update_result, container, false);

        mMapView = view.findViewById(R.id.mapView);
        cardView = view.findViewById(R.id.card_view_live_update);
        mMapView.onCreate(savedInstanceState);
        args = getArguments();
        strStatus = args.getString("status");
        strRoadName = args.getString("road_name");
        customActionBar();
        Log.d("zma road and status", strRoadName + "\n" + strStatus);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                // For showing a move to my ARRAY_CHARSADDA_ROAD button
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.setIndoorEnabled(true);
                googleMap.setTrafficEnabled(true);
                pickMapView();
            }
        });

        return view;
    }

    public void pickMapView() {
        googleMap.clear();
        switch (strRoadName) {
            case "gt_road":
                setPolylineOptions(LiveUpdateMapCoordinates.getArrayGtRoad());
                break;
            case "khyber_road":
                setPolylineOptions(LiveUpdateMapCoordinates.getArrayKhyberRoad());
                break;
            case "charsadda_road":
                setPolylineOptions(LiveUpdateMapCoordinates.getArrayCharsaddaRoad());
                break;
            case "jail_road":
                setPolylineOptions(LiveUpdateMapCoordinates.getArrayJailRoad());
                break;
            case "university_road":
                setPolylineOptions(LiveUpdateMapCoordinates.getArrayUniversityRoad());
                break;
            case "dalazak_road":
                setPolylineOptions(LiveUpdateMapCoordinates.getArrayDalazakRoad());
                break;
            case "saddar_road":
                setPolylineOptions(LiveUpdateMapCoordinates.getArraySaddarRoad());
                break;
            case "baghenaran_road":
                setPolylineOptions(LiveUpdateMapCoordinates.getArrayBaghENaranRoad());
                break;
            case "warsak_road":
                setPolylineOptions(LiveUpdateMapCoordinates.getArrayWarsakRoad());
                break;
            case "kohat_road":
                setPolylineOptions(LiveUpdateMapCoordinates.getArrayKohatRoad());
                break;
        }

    }

    private void setPolylineOptions(ArrayList<LatLng> roadCoordinates) {
        try {
            PolylineOptions options = new PolylineOptions();
            options.addAll(roadCoordinates);
            options.geodesic(true);
            options.width(10);
            if (strStatus.equals("Clear")) {
                cardView.setBackgroundResource(R.drawable.border_green_clear_live_status);
                options.color(Color.parseColor("#019875"));
            } else if (strStatus.equals("Busy")) {
                options.color(Color.RED);
                cardView.setBackgroundResource(R.drawable.border_busy_red_live_status);
            } else if (strStatus.equals("Congested")) {
                options.color(Color.parseColor("#22A7F0"));
                cardView.setBackgroundResource(R.drawable.border_congested_blue_live_status);
            }
            googleMap.addPolyline(options);
            setText();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(roadCoordinates.get(0), 12));
        } catch (Exception e) {
            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("No path available")
                    .show();
            e.printStackTrace();
        }
    }


    public void customActionBar() {
        android.support.v7.app.ActionBar mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.custom_action_bar, null);
        TextView mTitleTextView = mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText(args.getString("clicked_road_name") + " Status");
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

    }

    public void setText() {
        tvRoadStatus = view.findViewById(R.id.tv_road_status);
        tvUpdateTime = view.findViewById(R.id.tv_status_time);
        Bundle args = new Bundle(getArguments());
        tvUpdateTime.setText(String.valueOf(args.get("response_time")));
        tvRoadStatus.setText(String.valueOf(args.get("status")));
    }

}
