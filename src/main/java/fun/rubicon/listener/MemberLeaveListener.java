package fun.rubicon.listener;


import fun.rubicon.RubiconBot;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MemberLeaveListener extends ListenerAdapter{
    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {



        /* Leave message */
        TextChannel channel = event.getGuild().getTextChannelById(RubiconBot.getMySQL().getGuildValue(event.getGuild(), "channel"));
        channel.sendMessage(RubiconBot.getMySQL().getGuildValue(event.getGuild(), "leavemsg").replace("%user%", event.getMember().getAsMention()).replace("%guild%", event.getGuild().getName())).queue();
    }
}
