package tortel.fr.mapscannerclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
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

public class MainActivity extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener,
        PlaceFragment.OnPlaceFragmentInteractionListener, FilterFragment.OnFragmentInteractionListener {

    private Messenger mapScannerService = null;
    private boolean bound;

    private Messenger clientMessenger;


    private ListFragment listFragment;
    private PlaceFragment placeFragment;

    private FragmentManager fragmentManager;

    private Place selectedPlace;

    private LocationManager lm;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            lm.removeUpdates(this);
            doPerformQuery(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

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
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent("map.scanner.service.intent");
        intent.setComponent(new ComponentName("tortel.fr.mapscanner", "tortel.fr.mapscanner.MapScannerService"));
        bindService(intent, mapScannerConnection, Context.BIND_AUTO_CREATE);

        if (listFragment == null || (!listFragment.isAdded() || (placeFragment != null && !placeFragment.isAdded()))) {

            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            listFragment = ListFragment.newInstance(new ArrayList<Place>());
            fragmentTransaction.add(R.id.fragmentContainer, listFragment);
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

    @Override
    public void onBackPressed() {
        if (listFragment != null && listFragment.isAdded())
            finish();
    }

    public void performQuery() {
        if (PermissionChecker.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null || location.getTime() <= Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
            } else {
                doPerformQuery(location);
            }
        }
    }

    private void doPerformQuery(Location location) {

        QueryFilter queryFilter = SettingManager.getInstance().getQueryFilter();

        String group = "venues";
        StringBuilder endpoint = new StringBuilder();
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", "6");

        if ( PermissionChecker.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            Log.e("error", "[MAIN ACTIVITY]: Lacking of permission for the location");
            return;
        }

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        params.put("ll", String.valueOf(latitude) + "," + String.valueOf(longitude));

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
            Log.e("error", e.getMessage());
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
            msgRegistration.replyTo = clientMessenger;

            try {
                mapScannerService.send(msgRegistration);
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
        selectedPlace = listFragment.getPlaceList().get(itemSelectedIndex);

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

        if (listFragment == null) {
            listFragment = ListFragment.newInstance(new ArrayList<Place>());
        } else if(listFragment.isAdded()) {
            return;
        }
        fragmentTransaction.replace(R.id.fragmentContainer, listFragment).commit();
    }

    @Override
    public void onFilterSaved() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (listFragment == null) {
            listFragment = ListFragment.newInstance(new ArrayList<Place>());
        }
        fragmentTransaction.replace(R.id.fragmentContainer, listFragment).commit();
        listFragment.invalidateList();

        performQuery();
    }

    @Override
    public void onFilterCancel() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (listFragment == null) {
            listFragment = ListFragment.newInstance(new ArrayList<Place>());
        }
        fragmentTransaction.replace(R.id.fragmentContainer, listFragment).commit();
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

                        if (mainActivity.listFragment.isAdded()) {
                            mainActivity.listFragment.reloadList(recommendedPlaces);
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
                        mainActivity.listFragment.setPlaceImage(responsePhoto.getRequestId(), bitmap);
                    }
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
}
