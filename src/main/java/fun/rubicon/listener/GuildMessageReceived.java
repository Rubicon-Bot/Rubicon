package fun.rubicon.listener;

import fun.rubicon.core.Main;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildMessageReceived extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (!Main.getMySQL().ifMemberExist(e.getMember())) {
            Main.getMySQL().createMember(e.getMember());
            return;
        }
    }
}
