/*
 * Copyright (c) 2013 Byron Sanchez
 * www.chompix.com
 *
 * Licensed under the MIT license.
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
