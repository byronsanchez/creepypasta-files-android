/*
 * Copyright (c) 2013 Byron Sanchez
 * www.chompix.com
 *
 * Licensed under the MIT license.
 */

package net.globide.creepypasta_files_07;

/**
 * Defines the class that will store a row from the node table in the database.
 */

public class Node {
    // Define the list item id and list label.
    public int id;
    public String body;
    public String title;

    /**
     * Constructs the Node object.
     */
    public Node(int id, String title) {
        this.id = id;
        this.title = title;
    }

    /**
     * Constructs the Node object.
     */
    public Node() {
    }
}
