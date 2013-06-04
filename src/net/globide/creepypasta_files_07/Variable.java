/*
 * Copyright (c) 2013 Byron Sanchez
 * www.chompix.com
 *
 * Licensed under the MIT license.
 */

package net.globide.creepypasta_files_07;

/**
 * Defines the class that will store a row from the variable table in the database.
 */

public class Variable {
    
    public int id;
    public String key;
    public String value;
    public String sku;

    /**
     * Constructs the Variable object.
     */
    public Variable(int id, String key, String value, String sku) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.sku = sku;
    }

    /**
     * Constructs the Variable object.
     */
    public Variable() {
    }
}
