package fun.rubicon.listener;


import fun.rubicon.RubiconBot;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MemberLeaveListener extends ListenerAdapter {
    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        /* Leave message */
        String message = RubiconBot.getMySQL().getGuildValue(event.getGuild(), "leavemsg").replace("%user%", event.getMember().getAsMention()).replace("%guild%", event.getGuild().getName());
        if (message == null) return;
        if (message.equalsIgnoreCase("0") || message.equalsIgnoreCase(" 0")) return;
        TextChannel channel = event.getGuild().getTextChannelById(RubiconBot.getMySQL().getGuildValue(event.getGuild(), "channel"));
        if (channel == null) return;

        SafeMessage.sendMessage(channel, message);
    }
}
