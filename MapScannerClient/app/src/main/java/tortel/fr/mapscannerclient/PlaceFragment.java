package tortel.fr.mapscannerclient;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import tortel.fr.mapscannerclient.bean.Place;


public class PlaceFragment extends Fragment {

    private static final String ARG_PLACE = "place";

   private Place place;


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_place, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView nameTv = view.findViewById(R.id.name);
        TextView addressTv = view.findViewById(R.id.address);
        TextView distanceTv = view.findViewById(R.id.distance);
        ImageView imgView = view.findViewById(R.id.placeImg);

        nameTv.setText(place.getName());
        addressTv.setText(place.getFullAddress());
        distanceTv.setText(place.getDistance() + " m");
        imgView.setImageBitmap(place.getImage());

    }
}
