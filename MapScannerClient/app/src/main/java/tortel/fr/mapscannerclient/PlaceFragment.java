package tortel.fr.mapscannerclient;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import tortel.fr.mapscannerclient.adapter.CustomMapView;
import tortel.fr.mapscannerclient.bean.Place;


public class PlaceFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_PLACE = "place";

    private Place place;
    private CustomMapView mapView;
    private GoogleMap map;

    private OnPlaceFragmentInteractionListener listener;


    public PlaceFragment() {
        // Required empty public constructor
    }


    public static PlaceFragment newInstance(Place place) {
        PlaceFragment fragment = new PlaceFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLACE, place);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.place = (Place) getArguments().getSerializable(ARG_PLACE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_place, container, false);
        // Gets the MapView from the XML layout and creates it
        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);


        mapView.getMapAsync(this);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView nameTv = view.findViewById(R.id.name);
        TextView categoryTv = view.findViewById(R.id.category);
        TextView addressTv = view.findViewById(R.id.address);
        TextView distanceTv = view.findViewById(R.id.distance);
        ImageView imgView = view.findViewById(R.id.placeImg);

        nameTv.setText(place.getName());
        categoryTv.setText(place.getCategory());
        addressTv.setText(place.getFullAddress());
        distanceTv.setText(place.getDistance() + " m");
        imgView.setImageBitmap(place.getImage());

        FloatingActionButton backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnPlaceFragmentBackBtnInteraction();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);

        if ( ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            if ( ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
                map.setMyLocationEnabled(true);
            }
        }

        LatLng latLng = new LatLng(place.getLat(), place.getLng());
        map.addMarker(new MarkerOptions().position(latLng).title(place.getName()));

        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.setMinZoomPreference(15);
        map.setMaxZoomPreference(20);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlaceFragmentInteractionListener) {
            listener = (OnPlaceFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    public interface OnPlaceFragmentInteractionListener {
        void OnPlaceFragmentBackBtnInteraction();
    }
}
