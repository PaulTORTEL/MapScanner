package tortel.fr.mapscanner.listener;

import android.os.Messenger;

import java.util.List;

import tortel.fr.mapscanner.bean.ImageVenue;
import tortel.fr.mapscannerlib.Filter;

public interface IImageDatabaseListener {
    void onImageVenueFetched(ImageVenue imageVenue, Filter filter, Messenger replyTo);
    void onImageVenueSaved();
    void onImageVenueDeleted();
    void onImageVenueUpdated();
}
