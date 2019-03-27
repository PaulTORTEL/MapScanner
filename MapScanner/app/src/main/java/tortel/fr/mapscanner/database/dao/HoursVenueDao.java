package tortel.fr.mapscanner.database.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import tortel.fr.mapscanner.bean.HoursVenue;

@Dao
public interface HoursVenueDao {
    @Query("SELECT * FROM hoursVenue")
    List<HoursVenue> getAll();

    @Query("SELECT * FROM hoursVenue WHERE venue_id = (:venue_id)")
    HoursVenue loadAById(String venue_id);

    @Insert
    void insertAll(HoursVenue... hoursVenues);

    @Delete
    void delete(HoursVenue hoursVenue);

    @Update
    void updateAlarms(HoursVenue... hoursVenues);
}