package tortel.fr.mapscannerclient.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.MapView;

public class CustomMapView extends MapView {

    public CustomMapView(Context context) {
        super(context);
    }

    public CustomMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private void performTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Disallow ScrollView to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_UP:
                // Allow ScrollView to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        performTouchEvent(ev);

        // Handle MapView's touch events.
        super.onTouchEvent(ev);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        performTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
