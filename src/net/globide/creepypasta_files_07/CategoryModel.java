/*
 * Copyright (c) 2013 Byron Sanchez
 * www.chompix.com
 *
 * Licensed under the MIT license.
 */

package net.globide.creepypasta_files_07;

/**
 * Defines the list data that this app generates.
 */

public class CategoryModel {

    // Define the list item id and list label.
    public int id;
    public String category;

    /**
     * Constructs the CategoryModel object.
     */
    public CategoryModel(int id, String category) {
        this.id = id;
        this.category = category;
    }
}
