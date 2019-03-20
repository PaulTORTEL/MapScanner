package tortel.fr.mapscannerclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tortel.fr.mapscannerclient.adapter.PlaceArrayAdapter;
import tortel.fr.mapscannerclient.bean.Place;


public class RecommendationFragment extends Fragment {


    private static final String ARG_PLACE_LIST = "place_list";

    private List<Place> placeList;

    private static OnFragmentInteractionListener listener;

    private PlaceArrayAdapter placeArrayAdapter;
    private ListView recommendationListView;

    private ProgressBar progressBar;

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

        TextView title = view.findViewById(R.id.title);
        title.setText("List of recommended places");

        recommendationListView = view.findViewById(R.id.recommendationList);
        placeArrayAdapter = new PlaceArrayAdapter(view.getContext(), placeList);
        recommendationListView.setAdapter(placeArrayAdapter);

        // When the user clicks on a pokemon in the list
        recommendationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uri.Builder builder = new Uri.Builder();
                Uri uri = builder.appendQueryParameter("selectedItem", String.valueOf(i)).build();
                RecommendationFragment.listener.onPlaceClicked(uri);
            }
        });

        progressBar = view.findViewById(R.id.progressBar);

        FloatingActionButton filterBtn = view.findViewById(R.id.filterBtn);

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFilterBtnClicked();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (placeList != null && !placeList.isEmpty() && progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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
        void onPlaceClicked(Uri uri);
        void onFilterBtnClicked();
    }

    public void reloadList(List<Place> newList) {
        this.placeList = newList;
        placeArrayAdapter.clear();
        placeArrayAdapter.addAll(newList);
        placeArrayAdapter.notifyDataSetChanged();


        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
    }

    public void invalidateList() {
        this.placeList = new ArrayList<>();
        placeArrayAdapter.clear();
        placeArrayAdapter.notifyDataSetChanged();

        if (progressBar.getVisibility() == View.GONE)
            progressBar.setVisibility(View.VISIBLE);
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
