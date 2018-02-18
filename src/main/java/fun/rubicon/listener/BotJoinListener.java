/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class BotJoinListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        /* TODO DB
        * - Insert guild in database
        * - Insert new users in database
        * - Insert new members in database
        *
        * TODO Update stats
        * - discordbotlist.org
        * - bots.discord.pw
        */
    }
}
