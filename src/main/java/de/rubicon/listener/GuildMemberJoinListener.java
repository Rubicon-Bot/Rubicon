package de.rubicon.listener;

import de.rubicon.core.Main;
import de.rubicon.util.MySQL;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildMemberJoinListener extends ListenerAdapter{
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        MySQL mySQL = Main.getMySQL();
        mySQL.createUserPermissiones(event.getUser(), event.getGuild());
    }
}
