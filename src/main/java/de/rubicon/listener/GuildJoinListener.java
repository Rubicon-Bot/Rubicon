package de.rubicon.listener;

import fun.rubicon.core.Main;
import fun.rubicon.util.MySQL;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildJoinListener extends ListenerAdapter{

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        MySQL mySQL = Main.getMySQL();
        mySQL.generatePermissions(event.getGuild());
    }
}
