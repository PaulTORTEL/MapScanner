package tortel.fr.mapscanner.bean;

import tortel.fr.mapscanner.listener.IHoursDatabaseListener;

public class HoursDbBundle {
    private IHoursDatabaseListener hoursListener;
    private HoursVenue[] hoursVenues;

    public IHoursDatabaseListener getHoursListener() {
        return hoursListener;
    }

    public void setHoursListener(IHoursDatabaseListener hoursListener) {
        this.hoursListener = hoursListener;
    }

    public HoursVenue[] getHoursVenues() {
        return hoursVenues;
    }

    public void setHoursVenues(HoursVenue[] hoursVenues) {
        this.hoursVenues = hoursVenues;
    }
}
