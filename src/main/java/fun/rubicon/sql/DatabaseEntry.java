/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.sql;

/**
 * Represents a row in a database.
 * @author tr808axm
 */
public interface DatabaseEntry {
    /**
     * Checks whether this entry exists.
     * @return whether this entry exists.
     */
    boolean exists();

    /**
     * Creates this entry with default values.
     */
    void create();

    /**
     * Retrieves a value from this database entry.
     * @param type the column.
     * @return the found value.
     * @throws RuntimeException if any database exceptions occur.
     */
    String get(String type);

    /**
     * Updates a value in this database entry.
     * @param type the column.
     * @param value the new value.
     * @throws RuntimeException if any database exceptions occur.
     */
    void set(String type, String value);
}
