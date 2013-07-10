/*
 * Copyright (c) 2013 Byron Sanchez (hackbytes.com)
 * www.chompix.com
 *
 * Licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
