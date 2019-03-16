package tortel.fr.mapscannerclient.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tortel.fr.mapscannerclient.R;
import tortel.fr.mapscannerclient.bean.Place;

public class PlaceArrayAdapter extends ArrayAdapter<Place> {

    public PlaceArrayAdapter(@NonNull Context context, List<Place> placeList) {
        super(context, 0, placeList);
    }

    public View getView(int position, View view, ViewGroup group) {
        return initView(position, view, group);
    }

    private View initView(int position, View view, ViewGroup group) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.place_list_row, group, false);
        }

        TextView name = view.findViewById(R.id.placeName);
        ImageView img = view.findViewById(R.id.placeImg);
        TextView address = view.findViewById(R.id.address);
        TextView distance = view.findViewById(R.id.distance);

        Place place = getItem(position);

        if (place != null) {
            img.setImageBitmap(place.getImage());
            name.setText(place.getName());
            StringBuilder sbAddress = new StringBuilder();
            sbAddress.append(place.getAddress().isEmpty() ? "Unknown" : place.getAddress());
            sbAddress.append(", ");
            sbAddress.append(place.getCity().isEmpty() ? "City unknown" : place.getCity());
            address.setText(sbAddress.toString());
            distance.setText(String.valueOf(place.getDistance()) + " m");
        }

        return view;
    }
}