package tortel.fr.mapscanner.database.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import tortel.fr.mapscanner.bean.ImageVenue;

@Dao
public interface ImageVenueDao {
    @Query("SELECT * FROM imageVenue")
    List<ImageVenue> getAll();

    @Query("SELECT * FROM imageVenue WHERE venue_id = (:venue_id)")
    ImageVenue loadAById(String venue_id);

    @Insert
    void insertAll(ImageVenue... alarms);

    @Delete
    void delete(ImageVenue alarm);

    @Update
    void updateAlarms(ImageVenue... alarms);
}