package tortel.fr.mapscanner.database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import tortel.fr.mapscanner.bean.HoursVenue;
import tortel.fr.mapscanner.bean.ImageVenue;
import tortel.fr.mapscanner.database.dao.HoursVenueDao;
import tortel.fr.mapscanner.database.dao.ImageVenueDao;

@Database(entities = {ImageVenue.class, HoursVenue.class}, version = 2)
public abstract class MapScannerDatabase extends RoomDatabase {
    public abstract ImageVenueDao imageVenueDao();
    public abstract HoursVenueDao hoursVenueDao();
}
