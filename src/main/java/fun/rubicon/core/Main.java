/*
 * Copyright (c) 2017 Rubicon Dev Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core;

import fun.rubicon.RubiconBot;

/**
 * Rubicon's former main class.
 * @deprecated Use the Rubicon class instead.
 * @see RubiconBot
 */
public class Main {
    /**
     * @return the MySQL adapter.
     * @deprecated Use Rubicon.getMySQL() instead.
     * @see RubiconBot
     */
    @Deprecated
    public static fun.rubicon.util.MySQL getMySQL() {
        return RubiconBot.getMySQL();
    }

    /**
     * @return the configuration.
     * @deprecated Use Rubicon.getConfiguration() instead.
     * @see RubiconBot
     */
    @Deprecated
    public static fun.rubicon.util.Configuration getConfiguration() {
        return RubiconBot.getConfiguration();
    }
}