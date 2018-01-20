package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.sql.ServerLogSQL;
import fun.rubicon.util.DBLUtil;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.listener
 */

/**
 * Listener if the RubiconBot joins a new guild
 */
public class BotJoinListener extends ListenerAdapter {

    /**
     * Creates the new guild in the database
     *
     * @param event
     */
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        //post statistics to discordbots.org
        DBLUtil.postStats(event.getJDA());
        try {
            Guild g = event.getGuild();
            if (!RubiconBot.getMySQL().ifGuildExits(event.getGuild())) {
                RubiconBot.getMySQL().createGuildServer(g);
                new ServerLogSQL(event.getGuild());
            }
        } catch (Exception ex) {
            Logger.error(ex);
        }
    }
}