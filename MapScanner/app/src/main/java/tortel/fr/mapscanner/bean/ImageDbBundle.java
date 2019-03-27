package tortel.fr.mapscanner.bean;

import tortel.fr.mapscanner.listener.IImageDatabaseListener;

public class ImageDbBundle {
    private IImageDatabaseListener imageListener;
    private ImageVenue[] imageVenues;

    public IImageDatabaseListener getImageListener() {
        return imageListener;
    }

    public void setImageListener(IImageDatabaseListener imageListener) {
        this.imageListener = imageListener;
    }

    public ImageVenue[] getImageVenues() {
        return imageVenues;
    }

    public void setImageVenues(ImageVenue[] imageVenues) {
        this.imageVenues = imageVenues;
    }
}
