package fun.rubicon.listener;

import fun.rubicon.commands.moderation.CommandMute;
import fun.rubicon.core.entities.RubiconMember;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MuteListener extends ListenerAdapter{

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        RubiconMember member = RubiconMember.fromMember(event.getMember());
        if(member.isMuted())
            CommandMute.assignRole(member.getMember());
    }

    @Override
    public void onTextChannelCreate(TextChannelCreateEvent event) {
        CommandMute.handleChannelCreation(event);
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        CommandMute.createMutedRole(event.getGuild());
    }
}
