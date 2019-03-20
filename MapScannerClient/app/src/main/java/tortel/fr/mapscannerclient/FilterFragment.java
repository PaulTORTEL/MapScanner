package tortel.fr.mapscannerclient;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Set;
import java.util.TreeSet;

import tortel.fr.mapscannerclient.bean.QueryFilter;
import tortel.fr.mapscannerclient.manager.SettingManager;


public class FilterFragment extends Fragment {


    private OnFragmentInteractionListener listener;

    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        final RadioButton recommendationRadio = view.findViewById(R.id.recommendationsRadioBtn);
        final RadioButton searchRadio = view.findViewById(R.id.searchRadioBtn);

        final CheckBox coffeeCb = view.findViewById(R.id.coffeeCb);
        final CheckBox tacosCb = view.findViewById(R.id.tacosCb);
        final CheckBox restaurantCb = view.findViewById(R.id.restaurantCb);
        final CheckBox cinemaCb = view.findViewById(R.id.cinemaCb);
        final CheckBox gameCb = view.findViewById(R.id.gameCb);
        final CheckBox storeCb = view.findViewById(R.id.storeCb);

        final SeekBar radiusSb = view.findViewById(R.id.radius);
        final TextView radiusBox = view.findViewById(R.id.radiusBox);

        radiusSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                radiusBox.setText(String.valueOf(i + 250) + " m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button saveBtn = view.findViewById(R.id.saveBtn);
        Button cancelBtn = view.findViewById(R.id.cancelBtn);

        QueryFilter currentFilter = SettingManager.getInstance().getQueryFilter();
        radiusSb.setProgress(currentFilter.getRadius() - 250);
        radiusBox.setText(currentFilter.getRadius() + " m");

        if (currentFilter.getType() == 0) {
            recommendationRadio.setChecked(true);
            searchRadio.setChecked(false);
        } else {
            recommendationRadio.setChecked(false);
            searchRadio.setChecked(true);
        }

        for (String tag : currentFilter.getTags()) {
            switch(tag) {
                case "coffee":
                    coffeeCb.setChecked(true); break;
                case "tacos":
                    tacosCb.setChecked(true); break;
                case "restaurant":
                    restaurantCb.setChecked(true); break;
                case "cinema":
                    cinemaCb.setChecked(true); break;
                case "game":
                    gameCb.setChecked(true); break;
                case "store":
                    storeCb.setChecked(true); break;
            }
        }


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                QueryFilter queryFilter = new QueryFilter();

                if (recommendationRadio.isChecked()) {
                   queryFilter.setType(0);
                } else if (searchRadio.isChecked()) {
                   queryFilter.setType(1);
                }

                Set<String> tags = new TreeSet<>();

                if (coffeeCb.isChecked()) {
                   tags.add("coffee");
                }
                if (tacosCb.isChecked()) {
                   tags.add("tacos");
                }
                if (restaurantCb.isChecked()) {
                   tags.add("restaurant");
                }
                if (cinemaCb.isChecked()) {
                   tags.add("cinema");
                }
                if (gameCb.isChecked()) {
                   tags.add("game");
                }
                if (storeCb.isChecked()) {
                   tags.add("store");
                }

                queryFilter.setTags(tags);
                queryFilter.setRadius(radiusSb.getProgress() + 250);

                SettingManager.getInstance().savePreferences(getActivity(), queryFilter);
                if (listener != null) {
                    listener.onFilterSaved();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onFilterCancel();
                }
            }
        });
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
        void onFilterSaved();
        void onFilterCancel();
    }
}
