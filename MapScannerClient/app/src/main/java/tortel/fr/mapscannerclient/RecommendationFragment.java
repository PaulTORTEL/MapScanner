package tortel.fr.mapscannerclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import tortel.fr.mapscannerclient.adapter.PlaceArrayAdapter;
import tortel.fr.mapscannerclient.bean.Place;


public class RecommendationFragment extends Fragment {


    private static final String ARG_PLACE_LIST = "place_list";

    private List<Place> placeList;

    private static OnFragmentInteractionListener mListener;

    private PlaceArrayAdapter placeArrayAdapter;
    private ListView recommendationListView;

    public RecommendationFragment() {
        // Required empty public constructor
    }

    public static RecommendationFragment newInstance(ArrayList<Place> placeList) {
        RecommendationFragment fragment = new RecommendationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLACE_LIST, placeList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placeList = (ArrayList<Place>) getArguments().getSerializable(ARG_PLACE_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recommendation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recommendationListView = view.findViewById(R.id.recommendationList);
        placeArrayAdapter = new PlaceArrayAdapter(view.getContext(), placeList);
        recommendationListView.setAdapter(placeArrayAdapter);

        // When the user clicks on a pokemon in the list
        recommendationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // TODO: faire l'uri pour envoyer l'id de l'item cliqué
                Uri.Builder builder = new Uri.Builder();
                Uri uri = builder.appendQueryParameter("selectedItem", String.valueOf(i)).build();
                RecommendationFragment.mListener.onFragmentInteraction(uri);
               /* Intent intent = new Intent(MainActivity.this, PokemonOverviewActivity.class);
                Bundle bundle = new Bundle();

                bundle.putInt("pokeId", pokemonList.get(i).getId());
                intent.putExtras(bundle);
                startActivity(intent);*/
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void reloadList(List<Place> newList) {
        this.placeList = newList;
        placeArrayAdapter.clear();
        placeArrayAdapter.addAll(newList);
        placeArrayAdapter.notifyDataSetChanged();
    }

    public void setPlaceImage(String id, Bitmap image) {
        for (Place p : placeList) {
            if (p.getId().equals(id)) {
                p.setImage(image);
                break;
            }
        }

        reloadList(placeList);
    }

    public List<Place> getPlaceList() {
        return placeList;
    }
}
