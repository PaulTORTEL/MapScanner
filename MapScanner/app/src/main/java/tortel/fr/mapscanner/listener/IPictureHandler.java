package tortel.fr.mapscanner.listener;

import android.graphics.Bitmap;

public interface IPictureHandler {
    void onPictureDownloaded(Bitmap bitmap);
    void onPictureDownloadFailed(String error);
}
