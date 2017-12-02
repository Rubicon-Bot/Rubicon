package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class UserJoinListener extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        try {
            String joinMessage = RubiconBot.getMySQL().getGuildValue(e.getGuild(), "joinmsg");
            if (joinMessage.equals("0")) {
                return;
            } else {
                TextChannel messageChannel = e.getJDA().getTextChannelById(RubiconBot.getMySQL().getGuildValue(e.getGuild(), "channel"));
                if (messageChannel == null)
                    return;
                joinMessage = joinMessage
                        .replace("%user%", e.getMember().getAsMention()
                                .replace("%guild%", e.getGuild().getName()));
                messageChannel.sendMessage(joinMessage).queue();
            }
        } catch (NullPointerException ex) {
            //Channel does not exits
        }
    }
}
