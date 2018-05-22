package fun.rubicon.listener.feature;

import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.listener.events.*;
import fun.rubicon.util.Colors;
import fun.rubicon.util.DateUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.role.RoleCreateEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;

import java.awt.*;
import java.util.Date;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class LogListener extends RubiconEventAdapter {

    @Override
    public void onCommandExecution(CommandExecutedEvent event) {
        RubiconGuild guild = event.getRubiconGuild();
        if (guild.isCommandLogEnabled())
            sendLogMessage(guild, buildLogMessage("COMMAND EXECUTED", "Command `" + event.getHandler().getInvocationAliases()[0] + "` got executed by " + event.getMember().getAsMention(), Colors.FLAT_EMERALD).build());
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if (guild.isMemberLogEnabled())
            sendLogMessage(guild, buildLogMessage("MEMBER JOINED", "Member " + event.getMember().getAsMention() + " just joined Server.", Colors.FLAT_EMERALD).build());
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if (guild.isMemberLogEnabled())
            sendLogMessage(guild, buildLogMessage("MEMBER LEFT", "Member " + event.getMember().getAsMention() + " just left Server.", Colors.FLAT_AMETHYST).build());
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if (guild.isRoleLogEnabled())
            sendLogMessage(guild, buildLogMessage("ROLE ASSIGNED", "Member " + event.getMember().getAsMention() + " just got role `" + event.getRoles().get(0).getName() + "` assigned.", Colors.FLAT_EMERALD).build());

    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if (guild.isRoleLogEnabled())
            sendLogMessage(guild, buildLogMessage("ROLE REMOVED", "Member " + event.getMember().getAsMention() + " just got role `" + event.getRoles().get(0).getName() + "` removed.", Colors.FLAT_AMETHYST).build());
    }

    @Override
    public void onRoleCreate(RoleCreateEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if (guild.isRoleLogEnabled())
            sendLogMessage(guild, buildLogMessage("ROLE CREATED", "Role `" + event.getRole().getName() + "` just got created.", Colors.FLAT_EMERALD).build());

    }

    @Override
    public void onRoleDelete(RoleDeleteEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if (guild.isRoleLogEnabled())
            sendLogMessage(guild, buildLogMessage("ROLE DELETED", "Role `" + event.getRole().getName() + "` just got deleted.", Colors.FLAT_AMETHYST).build());

    }

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if (guild.isMessageLogEnabled())
            sendLogMessage(guild, buildLogMessage("MESSAGE DELETED", "Message `" + event.getMessageId() + "` just got deleted", Colors.FLAT_AMETHYST).build());
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if (guild.isPunishmentLogEnabled())
            sendLogMessage(guild, buildLogMessage("NEW BAN", "User `" + event.getUser().getAsMention() + "` just got banned.", Colors.FLAT_AMETHYST).build());
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if (guild.isPunishmentLogEnabled())
            sendLogMessage(guild, buildLogMessage("BAN REMOVED", "User `" + event.getUser().getAsMention() + "` just got unbanned.", Colors.FLAT_AMETHYST).build());

    }


    @Override
    public void onPunish(PunishmentEvent event) {
        System.out.println("HEY");
        RubiconGuild guild = event.getRubiconGuild();
        if (guild.isPunishmentLogEnabled())
            sendLogMessage(guild, buildLogMessage("NEW PUNISHMENT", buildPunishmentMessage(event), Colors.FLAT_AMETHYST).setFooter("Rubicon punishment system", null).build());
    }

    @Override
    public void onUnpunish(UnpunishEvent event) {
        System.out.println("HEY");
        RubiconGuild guild = event.getRubiconGuild();
        if (guild.isPunishmentLogEnabled())
            sendLogMessage(guild, buildLogMessage("PUNISHMENT REMOVED", buildPunishmentMessage(event), Colors.FLAT_EMERALD).setFooter("Rubicon punishment system", null).build());
    }

    private void sendLogMessage(RubiconGuild rubiconGuild, MessageEmbed msg) {
        if (rubiconGuild == null) return;
        if (!rubiconGuild.isLogChannelSet()) return;
        SafeMessage.sendMessage(rubiconGuild.getLogChannel(), msg);
    }

    private EmbedBuilder buildLogMessage(String title, String description, Color color) {
        return new EmbedBuilder()
                .setFooter("Rubicon logging system", null)
                .setColor(color)
                .setTitle(title)
                .setDescription(description);
    }

    private String buildPunishmentMessage(GenericPunishmentEvent event) {
        StringBuilder out = new StringBuilder().append("User: `").append(event.getUser().getName()).append("#").append(event.getUser().getDiscriminator()).append("`\n");
        if (event instanceof PunishmentEvent)
            out.append("Moderator: `").append(event.getModerator().getUser().getName()).append("#").append(event.getModerator().getUser().getDiscriminator()).append("`\n");
        out.append("Punishment type: ").append(event.getType().getName()).append("\n");
        if (event instanceof PunishmentEvent)
            out.append("Expiry: `").append(DateUtil.formatDate(new Date(((PunishmentEvent) event).getExpiry()), "%MM%/%dd%/%yyyy% %hh%:%mm%")).append("`");
        return out.toString();
    }

}
