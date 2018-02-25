/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.sql;

import java.sql.SQLException;

/**
 * MySQL database table generator.
 * @author Yannick Seeger / ForYaSee
 */
public interface DatabaseGenerator {
    /**
     * Creates the this generator's table if it does not already exist.
     */
    void createTableIfNotExist() throws SQLException;
}
