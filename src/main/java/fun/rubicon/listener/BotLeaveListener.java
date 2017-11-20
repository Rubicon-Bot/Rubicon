package fun.rubicon.listener;

import fun.rubicon.core.Main;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.listener
 */
public class BotLeaveListener extends ListenerAdapter {

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        Main.getMySQL().deleteGuild(e.getGuild());
    }
}
