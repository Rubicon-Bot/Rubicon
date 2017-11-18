package de.rubicon.listener;

import de.rubicon.core.Main;
import de.rubicon.util.MySQL;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildJoinListener extends ListenerAdapter{

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        MySQL mySQL = Main.getMySQL();
        mySQL.generatePermissions(event.getGuild());
    }
}
