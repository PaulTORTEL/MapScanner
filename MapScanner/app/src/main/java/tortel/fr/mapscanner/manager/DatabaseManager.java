package tortel.fr.mapscanner.manager;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.os.Messenger;

import tortel.fr.mapscanner.bean.HoursDbBundle;
import tortel.fr.mapscanner.bean.HoursVenue;
import tortel.fr.mapscanner.bean.ImageDbBundle;
import tortel.fr.mapscanner.bean.ImageVenue;
import tortel.fr.mapscanner.database.MapScannerDatabase;
import tortel.fr.mapscanner.database.dao.HoursVenueDao;
import tortel.fr.mapscanner.database.dao.ImageVenueDao;
import tortel.fr.mapscanner.listener.IHoursDatabaseListener;
import tortel.fr.mapscanner.listener.IImageDatabaseListener;
import tortel.fr.mapscannerlib.Filter;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Context appContext;

    /** TASKS **/
    private class GetImageVenueTask extends AsyncTask<IImageDatabaseListener, Void, Void> {

        private Filter filter;
        private Messenger replyTo;

        public GetImageVenueTask(Filter filter, Message msg) {
            this.filter = filter;
            this.replyTo = msg.replyTo;
        }

        @Override
        protected Void doInBackground(IImageDatabaseListener[] params) {

            ImageVenueDao imageVenue = DatabaseManager.this.getDatabase(DatabaseManager.this.appContext).imageVenueDao();
            if (params.length != 1)
                return null;

            params[0].onImageVenueFetched(imageVenue.loadAById(filter.getGroupId()), filter, replyTo);
            return null;

        }
    }

    private class InsertImageVenueTask extends AsyncTask<ImageDbBundle, Void, ImageDbBundle> {
        @Override
        protected ImageDbBundle doInBackground(ImageDbBundle[] dbBundles) {

            if (dbBundles.length != 1) {
                return null;
            }

            ImageVenueDao imageVenue = DatabaseManager.this.getDatabase(DatabaseManager.this.appContext).imageVenueDao();
            imageVenue.insertAll(dbBundles[0].getImageVenues());
            return dbBundles[0];
        }

        @Override
        protected void onPostExecute(ImageDbBundle dbBundle) {
            super.onPostExecute(dbBundle);
            if (dbBundle.getImageListener() != null) {
                dbBundle.getImageListener().onImageVenueSaved();
            }
        }
    }

    private class UpdateImageVenueTask extends AsyncTask<ImageDbBundle, Void, ImageDbBundle> {
        @Override
        protected ImageDbBundle doInBackground(ImageDbBundle[] dbBundles) {

            if (dbBundles.length != 1) {
                return null;
            }

            ImageVenueDao imageVenue = DatabaseManager.this.getDatabase(DatabaseManager.this.appContext).imageVenueDao();
            imageVenue.updateAlarms(dbBundles[0].getImageVenues());
            return dbBundles[0];
        }

        @Override
        protected void onPostExecute(ImageDbBundle dbBundle) {
            super.onPostExecute(dbBundle);
            if (dbBundle.getImageListener() != null) {
                dbBundle.getImageListener().onImageVenueUpdated();
            }
        }
    }

    private class DeleteImageVenueTask extends AsyncTask<ImageDbBundle, Void, ImageDbBundle> {
        @Override
        protected ImageDbBundle doInBackground(ImageDbBundle[] dbBundles) {

            if (dbBundles.length != 1) {
                return null;
            }

            ImageVenueDao imageVenue = DatabaseManager.this.getDatabase(DatabaseManager.this.appContext).imageVenueDao();
            imageVenue.delete(dbBundles[0].getImageVenues()[0]);
            return dbBundles[0];
        }

        @Override
        protected void onPostExecute(ImageDbBundle dbBundle) {
            super.onPostExecute(dbBundle);
            dbBundle.getImageListener().onImageVenueDeleted();
        }
    }




    private class GetHoursVenueTask extends AsyncTask<IHoursDatabaseListener, Void, Void> {

        private Filter filter;
        private Messenger replyTo;

        public GetHoursVenueTask(Filter filter, Message msg) {
            this.filter = filter;
            this.replyTo = msg.replyTo;
        }

        @Override
        protected Void doInBackground(IHoursDatabaseListener[] params) {

            HoursVenueDao hoursVenue = DatabaseManager.this.getDatabase(DatabaseManager.this.appContext).hoursVenueDao();
            if (params.length != 1)
                return null;

            params[0].onHoursVenueFetched(hoursVenue.loadAById(filter.getGroupId()), filter, replyTo);
            return null;

        }
    }

    private class InsertHoursVenueTask extends AsyncTask<HoursDbBundle, Void, HoursDbBundle> {
        @Override
        protected HoursDbBundle doInBackground(HoursDbBundle[] dbBundles) {

            if (dbBundles.length != 1) {
                return null;
            }

            HoursVenueDao imageVenue = DatabaseManager.this.getDatabase(DatabaseManager.this.appContext).hoursVenueDao();
            imageVenue.insertAll(dbBundles[0].getHoursVenues());
            return dbBundles[0];
        }

        @Override
        protected void onPostExecute(HoursDbBundle dbBundle) {
            super.onPostExecute(dbBundle);
            if (dbBundle.getHoursListener() != null) {
                dbBundle.getHoursListener().onHoursVenueSaved();
            }
        }
    }

    private class DeleteHoursVenueTask extends AsyncTask<HoursDbBundle, Void, HoursDbBundle> {
        @Override
        protected HoursDbBundle doInBackground(HoursDbBundle[] dbBundles) {

            if (dbBundles.length != 1) {
                return null;
            }

            HoursVenueDao imageVenue = DatabaseManager.this.getDatabase(DatabaseManager.this.appContext).hoursVenueDao();
            imageVenue.delete(dbBundles[0].getHoursVenues()[0]);
            return dbBundles[0];
        }

        @Override
        protected void onPostExecute(HoursDbBundle dbBundle) {
            super.onPostExecute(dbBundle);
            dbBundle.getHoursListener().onHoursVenueDeleted();
        }
    }





    /** === **/

    private MapScannerDatabase db;

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }

        return instance;
    }

    private DatabaseManager() {
    }

    public MapScannerDatabase getDatabase(Context appContext) {

        if (db == null) {
            this.appContext = appContext;
            db = Room.databaseBuilder(appContext, MapScannerDatabase.class, "map-scanner-database").fallbackToDestructiveMigration().build();
        }
        return db;
    }

    public void getImageVenue(IImageDatabaseListener listener, Context appContext, Filter filter, Message msg) {

        if (this.appContext == null) {
            this.appContext = appContext;
        }

        new GetImageVenueTask(filter, msg).execute(listener);
    }

    public void insertImageVenue(IImageDatabaseListener listener, Context appContext, ImageVenue... imageVenues) {

        if (this.appContext == null) {
            this.appContext = appContext;
        }
        ImageDbBundle dbBundle = new ImageDbBundle();
        dbBundle.setImageListener(listener);
        dbBundle.setImageVenues(imageVenues);
        new InsertImageVenueTask().execute(dbBundle);
    }

    public void updateImageVenue(IImageDatabaseListener listener, Context appContext, ImageVenue... imageVenues) {
        if (this.appContext == null) {
            this.appContext = appContext;
        }
        ImageDbBundle dbBundle = new ImageDbBundle();
        dbBundle.setImageListener(listener);
        dbBundle.setImageVenues(imageVenues);
        new UpdateImageVenueTask().execute(dbBundle);
    }

    public void deleteImageVenue(IImageDatabaseListener listener, Context appContext, ImageVenue imageVenue) {
        if (this.appContext == null) {
            this.appContext = appContext;
        }
        ImageDbBundle dbBundle = new ImageDbBundle();
        dbBundle.setImageListener(listener);
        dbBundle.setImageVenues(new ImageVenue[] {imageVenue});
        new DeleteImageVenueTask().execute(dbBundle);
    }

    public void getHoursVenue(IHoursDatabaseListener listener, Context appContext, Filter filter, Message msg) {

        if (this.appContext == null) {
            this.appContext = appContext;
        }

        new GetHoursVenueTask(filter, msg).execute(listener);
    }

    public void insertHoursVenue(IHoursDatabaseListener listener, Context appContext, HoursVenue... hoursVenues) {

        if (this.appContext == null) {
            this.appContext = appContext;
        }
        HoursDbBundle dbBundle = new HoursDbBundle();
        dbBundle.setHoursListener(listener);
        dbBundle.setHoursVenues(hoursVenues);
        new InsertHoursVenueTask().execute(dbBundle);
    }

    public void deleteHoursVenue(IHoursDatabaseListener listener, Context appContext, HoursVenue hoursVenue) {
        if (this.appContext == null) {
            this.appContext = appContext;
        }
        HoursDbBundle dbBundle = new HoursDbBundle();
        dbBundle.setHoursListener(listener);
        dbBundle.setHoursVenues(new HoursVenue[] {hoursVenue});
        new DeleteHoursVenueTask().execute(dbBundle);
    }




}
