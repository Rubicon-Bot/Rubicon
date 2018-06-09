package fun.rubicon.listener.feature;

import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.listener.events.*;
import fun.rubicon.util.Colors;
import fun.rubicon.util.DateUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.text.update.GenericTextChannelUpdateEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.voice.update.GenericVoiceChannelUpdateEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.role.RoleCreateEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.events.role.update.GenericRoleUpdateEvent;

import java.awt.*;
import java.util.Date;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class LogListener extends RubiconEventAdapter {

    @Override
    public void onCommandExecution(CommandExecutedEvent event) {
        RubiconGuild guild = event.getRubiconGuild();
        if(guild.isCommandLogEnabled())
            sendLogMessage(guild, buildLogMessage("COMMAND EXECUTED", "Command `" + event.getHandler().getInvocationAliases()[0] + "` got executed by " + event.getMember().getAsMention(), Colors.FLAT_EMERALD).build());
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isMemberLogEnabled())
            sendLogMessage(guild, buildLogMessage("MEMBER JOINED", "Member " + event.getMember().getAsMention() + " just joined Server.", Colors.FLAT_EMERALD).build());
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isMemberLogEnabled())
            sendLogMessage(guild, buildLogMessage("MEMBER LEFT", "Member " + event.getMember().getAsMention() + " just left Server.", Colors.FLAT_AMETHYST).build());
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isRoleLogEnabled())
            sendLogMessage(guild, buildLogMessage("ROLE ASSIGNED", "Member " + event.getMember().getAsMention() + " just got role `" + event.getRoles().get(0).getName() + "` assigned.", Colors.FLAT_EMERALD).build());

    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isRoleLogEnabled())
            sendLogMessage(guild, buildLogMessage("ROLE REMOVED", "Member " + event.getMember().getAsMention() + " just got role `" + event.getRoles().get(0).getName() + "` removed.", Colors.FLAT_AMETHYST).build());
    }

    @Override
    public void onRoleCreate(RoleCreateEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isRoleLogEnabled())
            sendLogMessage(guild, buildLogMessage("ROLE CREATED", "Role `" +  event.getRole().getName() + "` just got created.", Colors.FLAT_EMERALD).build());

    }

    @Override
    public void onRoleDelete(RoleDeleteEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isRoleLogEnabled())
            sendLogMessage(guild, buildLogMessage("ROLE DELETED", "Role `" +  event.getRole().getName() + "` just got deleted.", Colors.FLAT_AMETHYST).build());

    }

    @Override
    public void onGenericRoleUpdate(GenericRoleUpdateEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isRoleLogEnabled())
            sendLogMessage(guild, buildLogMessage("ROLE UPDATE", "Property `" + event.getPropertyIdentifier() + "` of role `" + event.getEntity().getName() + "` just got updated from `" + event.getOldValue() + "` to `" + event.getNewValue() + "`", Colors.FLAT_GREEN_SEA).build());
    }

    @Override
    public void onVoiceChannelCreate(VoiceChannelCreateEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isVoiceLogEnabled())
            sendLogMessage(guild, buildLogMessage("VOICE CHANNEL CREATED", "Voice channel `" + event.getChannel().getName() + "` just got created", Colors.FLAT_EMERALD).build());
    }

    @Override
    public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isVoiceLogEnabled())
            sendLogMessage(guild, buildLogMessage("VOICE CHANNEL DELETED", "Voice channel `" + event.getChannel().getName() + "` just got deleted", Colors.FLAT_AMETHYST).build());
    }

    @Override
    public void onGenericVoiceChannelUpdate(GenericVoiceChannelUpdateEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isVoiceLogEnabled())
            sendLogMessage(guild, buildLogMessage("VOICE CHANNEL UPDATE", "Property `" + event.getPropertyIdentifier() + "` of channel `" + event.getEntity().getName() + "` just got updated from `" + event.getOldValue() + "` to `" + event.getNewValue() + "`", Colors.FLAT_GREEN_SEA).build());
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isVoiceLogEnabled())
            sendLogMessage(guild, buildLogMessage("VOICE CHANNEL JOINED", event.getMember().getAsMention() + " just joined voice channel " + event.getChannelJoined().getName(), Colors.FLAT_EMERALD).build());
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isVoiceLogEnabled())
            sendLogMessage(guild, buildLogMessage("VOICE CHANNEL LEFT", event.getMember().getAsMention() + " left joined voice channel " + event.getChannelLeft().getName(), Colors.FLAT_AMETHYST).build());
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isVoiceLogEnabled())
            sendLogMessage(guild, buildLogMessage("VOICE CHANNEL MOVED", event.getMember().getAsMention() + " just moved from channel `" + event.getChannelLeft().getName() + "` to `" + event.getChannelJoined().getName() + "`", Colors.FLAT_EMERALD).build());

    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isPunishmentLogEnabled())
            sendLogMessage(guild, buildLogMessage("NEW BAN", "User `" + event.getUser().getAsMention() + "` just got banned.", Colors.FLAT_AMETHYST).build());
    }



    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isPunishmentLogEnabled())
            sendLogMessage(guild, buildLogMessage("BAN REMOVED", "User `" + event.getUser().getAsMention() + "` just got unbanned.", Colors.FLAT_AMETHYST).build());

    }


    @Override
    public void onPunish(PunishmentEvent event) {
        System.out.println("HEY");
        RubiconGuild guild = event.getRubiconGuild();
        if(guild.isPunishmentLogEnabled())
            sendLogMessage(guild, buildLogMessage("NEW PUNISHMENT", buildPunishmentMessage(event), Colors.FLAT_AMETHYST).setFooter("Rubicon punishment system", null).build());
    }

    @Override
    public void onUnpunish(UnpunishEvent event) {
        System.out.println("HEY");
        RubiconGuild guild = event.getRubiconGuild();
        if(guild.isPunishmentLogEnabled())
            sendLogMessage(guild, buildLogMessage("PUNISHMENT REMOVED", buildPunishmentMessage(event), Colors.FLAT_EMERALD).setFooter("Rubicon punishment system", null).build());
    }

    @Override
    public void onTextChannelCreate(TextChannelCreateEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isMessageLogEnabled())
            sendLogMessage(guild, buildLogMessage("TEXT CHANNEL CREATED", "Textchannel `" + event.getChannel().getName() + "` just got created", Colors.FLAT_EMERALD).build());
    }

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isMessageLogEnabled())
            sendLogMessage(guild, buildLogMessage("TEXT CHANNEL DELETED", "Textchannel `" + event.getChannel().getName() + "` just got deleted", Colors.FLAT_EMERALD).build());

    }

    @Override
    public void onGenericTextChannelUpdate(GenericTextChannelUpdateEvent event) {
        RubiconGuild guild = RubiconGuild.fromGuild(event.getGuild());
        if(guild.isMessageLogEnabled())
            sendLogMessage(guild, buildLogMessage("TEXT CHANNEL UPDATE", "Property `" + event.getPropertyIdentifier() + "` of channel `" + event.getEntity().getName() + "` just got updated from `" + event.getOldValue() + "` to `" + event.getNewValue() + "`", Colors.FLAT_GREEN_SEA).build());

    }

    private void sendLogMessage(RubiconGuild rubiconGuild, MessageEmbed msg){
        if(rubiconGuild == null) return;
        if(!rubiconGuild.isLogChannelSet()) return;
        SafeMessage.sendMessage(rubiconGuild.getLogChannel(), msg);
    }

    private EmbedBuilder buildLogMessage(String title, String description, Color color){
        return new EmbedBuilder()
                .setFooter("Rubicon logging system", null)
                .setColor(color)
                .setTitle(title)
                .setDescription(description);
    }
    private String buildPunishmentMessage(GenericPunishmentEvent event){
        StringBuilder out = new StringBuilder().append("User: `").append(event.getUser().getName()).append("#").append(event.getUser().getDiscriminator()).append("`\n");
        if(event instanceof PunishmentEvent)
                out.append("Moderator: `").append(event.getModerator().getUser().getName()).append("#").append(event.getModerator().getUser().getDiscriminator()).append("`\n");
        out.append("Punishment type: ").append(event.getType().getName()).append("\n");
        if(event instanceof PunishmentEvent)
            out.append("Expiry: `").append(DateUtil.formatDate(new Date(((PunishmentEvent) event).getExpiry()), "%MM%/%dd%/%yyyy% %hh%:%mm%")).append("`");
        return out.toString();
    }

}
