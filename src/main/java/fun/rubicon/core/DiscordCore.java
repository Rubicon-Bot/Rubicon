/*
 * Copyright (c) 2017 Rubicon Dev Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core;

import fun.rubicon.RubiconBot;

/**
 * Former core class holding the JDA object statically.
 * @deprecated Use the Rubicon class instead.
 * @see RubiconBot
 */
@Deprecated
public class DiscordCore {

    /**
     * Initializes the static JDA bot instance and starts listeners.
     * @deprecated Use Rubicon.initJDA() instead.
     * @see RubiconBot
     */
    @Deprecated
    public static void start() {
        RubiconBot.initJDA();
    }

    /**
     * @return the static JDA instance. May be null if DiscordCore.start() was not called before.
     * @deprecated Use Rubicon.getJDA() instead.
     * @see RubiconBot
     */
    @Deprecated
    public static net.dv8tion.jda.core.JDA getJDA() {
        return RubiconBot.getJDA();
    }
}
