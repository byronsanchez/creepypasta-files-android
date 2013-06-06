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

/**
 * Defines an interface that handles selection events within the Browse Activity
 * scope. This includes all fragments it contains. This interface allows
 * fragments to communicate with one another as well as with the host activity,
 * and therefore, pass input data to one another. This allows fragments to
 * behave differently based on the input data OTHER fragments may receive. This
 * interface is necessary to allow the host Browse Activity to communicate with
 * multiple fragments using this same interface. The alternative would be to
 * define a local interface within each fragment, each with a different name to
 * prevent collisions. This alternative is obviously not as efficient as the
 * current implementation.
 */

// Callback interface that the host activity must implement to share events
// triggered by the various fragments it hosts.
public interface OnBrowseListTouchListener {
    public void onBrowseListTouchListener(String fragment, int dbId);
}
