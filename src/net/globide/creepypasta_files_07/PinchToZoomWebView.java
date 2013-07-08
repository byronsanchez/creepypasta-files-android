package net.globide.creepypasta_files_07;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

// TODO: remove
import android.util.Log;

/**
 * A WebView that offers only the "pinch to zoom" mechanism and does not offer
 * the default zoom controls.
 */

public class PinchToZoomWebView extends WebView {

    public PinchToZoomWebView(Context context) {
        super(context);
    }

    public PinchToZoomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PinchToZoomWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();

        if (pointerCount > 1) {
            getSettings().setSupportZoom(true);
            getSettings().setBuiltInZoomControls(true);
        }
        else {
            getSettings().setSupportZoom(false);
            getSettings().setBuiltInZoomControls(false);
        }

        super.onTouchEvent(event);
        return true;
    }
}
