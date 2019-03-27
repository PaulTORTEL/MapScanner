package tortel.fr.mapscanner.listener;

import android.os.Messenger;

import tortel.fr.mapscanner.bean.HoursVenue;
import tortel.fr.mapscanner.bean.ImageVenue;
import tortel.fr.mapscannerlib.Filter;

public interface IHoursDatabaseListener {
    void onHoursVenueFetched(HoursVenue hoursVenue, Filter filter, Messenger replyTo);
    void onHoursVenueSaved();
    void onHoursVenueDeleted();
    void onHoursVenueUpdated();
}
