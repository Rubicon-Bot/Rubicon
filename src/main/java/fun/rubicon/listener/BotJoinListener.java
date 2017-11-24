package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.Main;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Timer;
import java.util.TimerTask;

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
     * @param e
     */
    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        try {
            Guild g = e.getGuild();
            if (!RubiconBot.getMySQL().ifGuildExits(e.getGuild())) {
                RubiconBot.getMySQL().createGuildServer(g);
            }
        } catch (Exception ex) {
            Logger.error(ex);
        }
    }
}