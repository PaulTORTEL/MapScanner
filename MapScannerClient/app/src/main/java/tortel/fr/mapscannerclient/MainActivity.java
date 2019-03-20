package tortel.fr.mapscannerclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tortel.fr.mapscannerclient.bean.Place;
import tortel.fr.mapscannerclient.bean.QueryFilter;
import tortel.fr.mapscannerclient.manager.SettingManager;
import tortel.fr.mapscannerclient.parser.HoursParser;
import tortel.fr.mapscannerclient.parser.RecommendationParser;
import tortel.fr.mapscannerclient.parser.SearchParser;
import tortel.fr.mapscannerclient.util.MessageUtil;
import tortel.fr.mapscannerlib.ApiResponse;
import tortel.fr.mapscannerlib.MessageUtils;

public class MainActivity extends AppCompatActivity implements RecommendationFragment.OnFragmentInteractionListener,
        PlaceFragment.OnPlaceFragmentInteractionListener, FilterFragment.OnFragmentInteractionListener {

    private Messenger mapScannerService = null;
    private boolean bound;

    private Messenger clientMessenger;


    private RecommendationFragment recommendationFragment;
    private PlaceFragment placeFragment;

    private FragmentManager fragmentManager;

    private Place selectedPlace;

    public Messenger getClientMessenger() {
        return clientMessenger;
    }

    public Place getSelectedPlace() {
        return selectedPlace;
    }

    public PlaceFragment getPlaceFragment() {
        return placeFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clientMessenger = new Messenger(new IncomingHandler(this));
        SettingManager.getInstance().init(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent("map.scanner.service.intent");
        intent.setComponent(new ComponentName("tortel.fr.mapscanner", "tortel.fr.mapscanner.MapScannerService"));
        bindService(intent, mapScannerConnection, Context.BIND_AUTO_CREATE);

        if (recommendationFragment == null || (!recommendationFragment.isAdded() || !placeFragment.isAdded())) {

            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            recommendationFragment = RecommendationFragment.newInstance(new ArrayList<Place>());
            fragmentTransaction.add(R.id.fragmentContainer, recommendationFragment);
            fragmentTransaction.commit();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (bound) {
            if (mapScannerService != null) {
                Message msg = Message.obtain(null, MessageUtils.UNREGISTER_CLIENT_MSG);
                msg.replyTo = clientMessenger;

                try {
                    mapScannerService.send(msg);
                } catch (RemoteException e) {
                    Log.e("error", e.getMessage());
                }
            }
            unbindService(mapScannerConnection);
            bound = false;
        }
    }

    private void performQuery() {
        QueryFilter queryFilter = SettingManager.getInstance().getQueryFilter();

        String group = "venues";
        StringBuilder endpoint = new StringBuilder();
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", "1");
        params.put("ll", "51.903614,-8.468399");

        if (queryFilter.getType() == 0) {
            endpoint.append("explore");
        } else {
            endpoint.append("search");
        }

        StringBuilder tagBuilder = new StringBuilder();

        for (String tag : queryFilter.getTags()) {
            tagBuilder.append(tag);
            tagBuilder.append(",");
        }

        if (queryFilter.getRadius() > 0)
            params.put("radius", String.valueOf(queryFilter.getRadius()));

        if (queryFilter.getTags().size() > 0)
            params.put("query", tagBuilder.substring(0, tagBuilder.toString().length() - 1));

        Message msgPlaces = MessageUtil.makeMessage(MessageUtils.VENUES_MSG, group, endpoint.toString(), null, getClientMessenger(), params);
        try {
            mapScannerService.send(msgPlaces);
        } catch (RemoteException e) {
            Log.d("error", e.getMessage());
        }
    }

    private ServiceConnection mapScannerConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mapScannerService = new Messenger(service);
            bound = true;
            Message msgRegistration = Message.obtain(null, MessageUtils.REGISTER_CLIENT_MSG);

          /*  HashMap<String, String> map = new HashMap<>();
            map.put("limit", "1");
            map.put("ll", "51.903614,-8.468399");

            Message msgRecommendations = MessageUtil.makeMessage(MessageUtils.VENUES_MSG, "venues", "explore", null, getClientMessenger(), map);*/

            /*TODO: TO REMOVE */
        /*    Message msgRecommendations = Message.obtain(null, MessageUtils.VENUES_MSG);
            Bundle b = new Bundle();
            Filter f = new Filter();
            f.setGroup("venues");
            f.setEndpoint("explore");

            HashMap<String, String> map = new HashMap<>();
            map.put("limit", "20");
            map.put("ll", "51.903614,-8.468399");
            map.put("query", "coffee");

            f.setParams(map);

            b.putSerializable("filter", f);
            msgRecommendations.setData(b);


            Message msg3 = Message.obtain(null, MessageUtils.PHOTOS_MSG);
            Bundle b2 = new Bundle();
            Filter f2 = new Filter();
            f2.setGroup("venues");
            f2.setGroupId("4b4a936cf964a520f18a26e3");
            f2.setEndpoint("photos");

            b2.putSerializable("filter", f2);
            msg3.setData(b2);

            Message msg4 = Message.obtain(null, MessageUtils.VENUES_MSG);
            Bundle b3 = new Bundle();
            Filter f3 = new Filter();
            f3.setGroup("venues");
            f3.setGroupId("4b4a936cf964a520f18a26e3");
            f3.setEndpoint("hours");

            b3.putSerializable("filter", f3);
            msg4.setData(b3);*/
            /**/

            msgRegistration.replyTo = clientMessenger;

          /*  msg3.replyTo = clientMessenger;
            msg4.replyTo = clientMessenger;*/
            try {
                mapScannerService.send(msgRegistration);
                //mapScannerService.send(msgRecommendations);
                /*mapScannerService.send(msg3);
                mapScannerService.send(msg4);*/
            } catch (RemoteException e) {
                Log.e("error", e.getMessage());
            }

            performQuery();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mapScannerService = null;
            bound = false;
        }
    };

    @Override
    public void onPlaceClicked(Uri uri) {

        int itemSelectedIndex = Integer.valueOf(uri.getQueryParameter("selectedItem"));
        selectedPlace = recommendationFragment.getPlaceList().get(itemSelectedIndex);

        if (selectedPlace.getWeekHours() == null) {
            Message hoursMsg = MessageUtil.makeMessage(MessageUtils.VENUES_MSG, "venues", "hours", selectedPlace.getId(),
                    getClientMessenger(), null);
            try {
                mapScannerService.send(hoursMsg);
            } catch (RemoteException e) {
                Log.e("error", e.getMessage());
            }
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        placeFragment = PlaceFragment.newInstance(selectedPlace);
        fragmentTransaction.replace(R.id.fragmentContainer, placeFragment).addToBackStack(null).commit();

    }

    @Override
    public void onFilterBtnClicked() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, new FilterFragment()).addToBackStack(null).commit();
    }

    @Override
    public void OnPlaceFragmentBackBtnInteraction() {
        selectedPlace = null;

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (recommendationFragment == null) {
            recommendationFragment = RecommendationFragment.newInstance(new ArrayList<Place>());
        } else if(recommendationFragment.isAdded()) {
            return;
        }
        fragmentTransaction.replace(R.id.fragmentContainer, recommendationFragment).commit();
    }

    @Override
    public void onFilterSaved() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (recommendationFragment == null) {
            recommendationFragment = RecommendationFragment.newInstance(new ArrayList<Place>());
        }
        fragmentTransaction.replace(R.id.fragmentContainer, recommendationFragment).commit();
        recommendationFragment.invalidateList();

        performQuery();
    }

    @Override
    public void onFilterCancel() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (recommendationFragment == null) {
            recommendationFragment = RecommendationFragment.newInstance(new ArrayList<Place>());
        }
        fragmentTransaction.replace(R.id.fragmentContainer, recommendationFragment).commit();
    }

    static class IncomingHandler extends Handler {
        private MainActivity mainActivity;

        IncomingHandler(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageUtils.VENUES_MSG:
                   // TextView test = mainActivity.findViewById(R.id.test);
                  //  TextView testHours = mainActivity.findViewById(R.id.testHours);
                    Bundle b = msg.getData();
                    ApiResponse resp = (ApiResponse) b.getSerializable("venues");

                    if (resp.getEndpoint().equals("explore") || resp.getEndpoint().equals("search")) {
                        List<Place> recommendedPlaces;
                        if (resp.getEndpoint().equals("explore"))
                            recommendedPlaces = RecommendationParser.parse(resp);

                        else
                            recommendedPlaces = SearchParser.parse(resp);

                        for (Place p : recommendedPlaces) {
                            Message photoMsg = MessageUtil.makeMessage(MessageUtils.PHOTOS_MSG, "venues", "photos", p.getId(),
                                    mainActivity.getClientMessenger(), null);
                            try {
                                mainActivity.mapScannerService.send(photoMsg);
                            } catch (RemoteException e) {
                                Log.e("error", e.toString());
                            }
                        }

                        if (mainActivity.recommendationFragment.isAdded()) {
                            mainActivity.recommendationFragment.reloadList(recommendedPlaces);
                        }

                    } else if (resp.getEndpoint().equals("hours")) {
                        if (mainActivity.getSelectedPlace() != null) {
                            mainActivity.getSelectedPlace().setWeekHours(HoursParser.parse(resp));
                            mainActivity.getPlaceFragment().reloadPlaceHours();
                        }
                    }

                    break;

                case MessageUtils.PHOTOS_MSG:
                    Bundle bundlePhoto = msg.getData();
                    ApiResponse responsePhoto = (ApiResponse) bundlePhoto.getSerializable("photos");
                    if (responsePhoto.getBitmap() != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(responsePhoto.getBitmap(), 0, responsePhoto.getBitmap().length);
                        mainActivity.recommendationFragment.setPlaceImage(responsePhoto.getRequestId(), bitmap);
                    }
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
}
