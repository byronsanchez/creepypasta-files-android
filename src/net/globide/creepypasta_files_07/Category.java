/*
 * Copyright (c) 2013 Byron Sanchez
 * www.chompix.com
 *
 * Licensed under the MIT license.
 */

package net.globide.creepypasta_files_07;

/**
 * Defines the class that will store a row from the category table in the
 * database.
 */

public class Category {
    // Define the list item id and list label.
    public int id;
    public String category;

    /**
     * Constructs the CategoryModel object.
     */
    public Category(int id, String category) {
        this.id = id;
        this.category = category;
    }
}
