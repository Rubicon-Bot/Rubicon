package fun.rubicon.listener;

import fun.rubicon.core.entities.RubiconUser;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BanListener extends ListenerAdapter{

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        RubiconUser.fromUser(event.getUser()).unban(event.getGuild());
    }
}
