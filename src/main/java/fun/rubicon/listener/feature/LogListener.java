package fun.rubicon.listener.feature;

import fun.rubicon.listener.events.CommandExecutedEvent;
import fun.rubicon.listener.events.RubiconEventAdapter;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class LogListener extends RubiconEventAdapter {

    @Override
    public void onCommandExecution(CommandExecutedEvent event) {

    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {

    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {

    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {

    }

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {

    }

    @Override
    public void onGuildBan(GuildBanEvent event) {

    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {

    }


}
