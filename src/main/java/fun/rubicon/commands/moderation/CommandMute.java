package fun.rubicon.commands.moderation;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.mysql.MySQL;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CommandMute extends CommandHandler {

    public CommandMute() {
        super(new String[]{"mute"}, CommandCategory.MODERATION, new PermissionRequirements("mute", false, false), "Easily mute a member.", "<@User> [time in minutes]");
    }


    public static Role createMutedRole(Guild guild) {
        if (!guild.getRolesByName("rubicon-muted", false).isEmpty())
            return guild.getRolesByName("rubicon-muted", false).get(0);
        Member selfMember = guild.getSelfMember();
        if (!selfMember.getPermissions().contains(Permission.MANAGE_ROLES) || !selfMember.getPermissions().contains(Permission.MANAGE_CHANNEL)) {
            guild.getOwner().getUser().openPrivateChannel().complete().sendMessage("ERROR: Please give me `MANAGE_CHANNEL` and the `MANAGE_CHANNEL`permission in order to use the mute command").queue();
            return null;
        }

        Role muted = guild.getController().createRole().setName("rubicon-muted").complete();
        guild.getTextChannels().forEach(tc -> {
            PermissionOverride override = tc.createPermissionOverride(muted).complete();
            override.getManager().deny(Permission.MESSAGE_WRITE).complete();
        });
        return muted;
    }

    public static void handleChannelCreation(TextChannelCreateEvent event) {
        Guild guild = event.getGuild();
        Member selfMember = guild.getSelfMember();
        Role muted = createMutedRole(guild);

        if (!selfMember.getPermissions().contains(Permission.MANAGE_ROLES) || !selfMember.getPermissions().contains(Permission.MANAGE_CHANNEL)) {
            guild.getOwner().getUser().openPrivateChannel().complete().sendMessage("ERROR: Please give me `MANAGE_CHANNEL` and the `MANAGE_CHANNEL`permission in order to use the mute command").queue();
            return;
        }

        PermissionOverride override = event.getChannel().createPermissionOverride(muted).complete();
        override.getManager().deny(Permission.MESSAGE_WRITE).complete();
    }

    public static boolean assignRole(Member member) {
        Guild guild = member.getGuild();
        Role muted = createMutedRole(guild);
        if (muted == null) return false;
        if (!guild.getSelfMember().canInteract(muted)) return false;
        guild.getController().addSingleRoleToMember(member, muted).queue();
        return true;
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation command, UserPermissions userPermissions) {
        String[] args = command.getArgs();
        Message message = command.getMessage();
        Member member = command.getMember();
        Guild guild = command.getGuild();

        if (args.length == 0)
            return createHelpMessage();
        if(args[0].equals("settings")){
            PermissionRequirements settingsPerms = new PermissionRequirements("mute.settings", false, false);
            UserPermissions user = new UserPermissions(member.getUser(), guild);
            if(settingsPerms.coveredBy(userPermissions)){
                RubiconGuild rGuild = RubiconGuild.fromGuild(guild);
                if(!rGuild.useMuteSettings())
                    rGuild.insertMuteTable();
                switch (args[1]){
                    case "channel":
                        if(message.getMentionedChannels().isEmpty()) {
                            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.settings.nochannelmentioned.title"), command.translate("command.mute.settings.nochannelmentioned.description")).build()).build();
                        }
                        TextChannel channel = message.getMentionedChannels().get(0);
                        setMuteLogChannel(channel);
                        SafeMessage.sendMessage(command.getTextChannel(), EmbedUtil.success(command.translate("command.mute.settings.channelset.title"), String.format(command.translate("command.mute.settings.channelset.description"), channel.getName())).build(), 5);
                        break;
                    case "mute":
                        if(args.length == 2){
                            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.settings.mutemessage.nomsg.title"), command.translate("command.mute.settings.mutemessage.nomsg.description")).build()).build();
                        }

                        StringBuilder muteBuilder = new StringBuilder();
                        for(int i = 2; i < args.length; i++){
                            muteBuilder.append(args[i]).append(" ");
                        }
                        String mutedMessage = muteBuilder.replace(muteBuilder.lastIndexOf(" "), muteBuilder.lastIndexOf(" ") + 1, "").toString();
                        setMutedMessage(mutedMessage, guild);
                        if(mutedMessage.equals("disable")){
                            return new MessageBuilder().setEmbed(EmbedUtil.success(command.translate("command.mute.settings.mutemessage.disabled.title"), command.translate("command.mute.settings.mutemessage.disabled.description")).build()).build();
                        }
                        SafeMessage.sendMessage(command.getTextChannel(), EmbedUtil.success(command.translate("command.mute.settings.messages.success.title"), String.format(command.translate("command.mute.settings.mutemessaage.success.description"), mutedMessage)).build(), 5);
                        break;
                    case "unmute":
                        if(args.length == 2){
                            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.settings.mutemessage.nomsg.title"), command.translate("command.mute.settings.mutemessage.nomsg.description")).build()).build();
                        }

                        StringBuilder unmuteBuilder = new StringBuilder();
                        for(int i = 2; i < args.length; i++){
                            unmuteBuilder.append(args[i]).append(" ");
                        }
                        String unmutedMessage = unmuteBuilder.replace(unmuteBuilder.lastIndexOf(" "), unmuteBuilder.lastIndexOf(" ") + 1, "").toString();
                        setUnmutedMessage(unmutedMessage, guild);
                        if(unmutedMessage.equals("disable")){
                            return new MessageBuilder().setEmbed(EmbedUtil.success(command.translate("command.mute.settings.unmutemessage.disabled.title"), command.translate("command.mute.settings.mutemessage.undisabled.description")).build()).build();
                        }
                        SafeMessage.sendMessage(command.getTextChannel(), EmbedUtil.success(command.translate("command.mute.settings.messages.success.title"), String.format(command.translate("command.mute.settings.mutemessaage.success.description"), unmutedMessage)).build(), 5);
                        break;
                    default:
                        SafeMessage.sendMessage(command.getTextChannel(), EmbedUtil.info("USAGE", "mute settings channel <#Channel>\nmute settings <mute/unmute> <message/disable>").build(), 10);
                        break;
                }
            } else
                return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.settings.nopermission.title"), command.translate("command.mute.settings.nopermission.description")).build()).build();
            return null;
        }
        if (message.getMentionedUsers().isEmpty())
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.unknownuser.title"), command.translate("command.mute.unknownuser.description")).build()).build();
        Member victimMember = guild.getMember(message.getMentionedUsers().get(0));
        RubiconMember victim = RubiconMember.fromMember(victimMember);
        if (victim.isMuted())
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.muted.permanent.title"), command.translate("command.mute.muted.permanent.description")).build()).build();
        if(victimMember.equals(guild.getSelfMember()))
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.donotmuterubicon.title"), command.translate("command.mute.donotmuterubicon.description")).build()).build();
        if (!member.canInteract(victimMember) && !Arrays.asList(Info.BOT_AUTHOR_IDS).contains(member.getUser().getIdLong()))
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.nopermissions.user.title"), String.format(command.translate("command.mute.nopermissions.user.description"), victimMember.getAsMention())).build()).build();
        if (!command.getSelfMember().canInteract(victimMember))
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.nopermissions.bot.title"), String.format(command.translate("command.mute.nopermissions.bot.description"), victimMember.getAsMention())).build()).build();
        if (!assignRole(victimMember))
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.nopermissions.role.title"), command.translate("command.mute.nopermissions.role.description")).build()).build();
        RubiconGuild rGuild = RubiconGuild.fromGuild(guild);
        if (args.length == 1) {
            if(new PermissionRequirements("mute.permanent", false, false).coveredBy(command.getPerms()))
                return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.nopermissions.user.title"), command.translate("command.mute.permanent.noperms.description")).build()).build();
            victim.mute();
            if(rGuild.useMuteSettings())
                rGuild.getMuteChannel().sendMessage(rGuild.getMuteMessage().replace("%moderator%", member.getAsMention()).replace("%mention%", victim.getMember().getAsMention()).replace("%date%", "never"));
            return new MessageBuilder().setEmbed(EmbedUtil.success(command.translate("command.mute.muted.permanent.title"), String.format(command.translate("command.mute.muted.permanent.description"), victimMember.getAsMention())).build()).build();
        } else if (args.length > 1) {
            Integer delay;
            try {
                delay = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.invalidnumber.title"), command.translate("command.mute.invalidnumber.description")).build()).build();
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + delay);
            Date expiry = cal.getTime();
            victim.mute(expiry);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    victim.unmute();
                    CommandUnmute.deassignRole(victim.getMember());
                }
            }, expiry);
            return new MessageBuilder().setEmbed(EmbedUtil.success(command.translate("command.mute.muted.temporary.title"), command.translate("command.mute.muted.temporary.permanent").replace("%mention%", victimMember.getAsMention()).replace("%date%", )).build()).build();
        }
        return createHelpMessage();

    }

    private void setUnmutedMessage(String unmutedMessage, Guild guild) {
        try {
            PreparedStatement ps = RubiconBot.getMySQL().getConnection().prepareStatement("UPDATE `mutesettings` SET `unmutemsg` = ? WHERE serverid = ?");
            ps.setString(1, unmutedMessage);
            ps.setLong(2, guild.getIdLong());
            ps.execute();
        } catch (SQLException e){
            Logger.error(e);
        }
    }

    private void setMutedMessage(String mutedMessage, Guild guild) {
        try {
            PreparedStatement ps = RubiconBot.getMySQL().getConnection().prepareStatement("UPDATE `mutesettings` SET `mutedmsg` = ? WHERE serverid = ?");
            ps.setString(1, mutedMessage);
            ps.setLong(2, guild.getIdLong());
            ps.execute();
        } catch (SQLException e){
            Logger.error(e);
        }
    }

    private void setMuteLogChannel(TextChannel textChannel) {
        try{
            PreparedStatement ps = RubiconBot.getMySQL().getConnection().prepareStatement("UPDATE `mutesettings` SET channel = ? WHERE serverid = ?");
            ps.setLong(1, textChannel.getIdLong());
            ps.setLong(2, textChannel.getGuild().getIdLong());
            ps.execute();
        } catch (SQLException e){
            Logger.error(e);
        }
    }

    public static void loadMutes(){
        MySQL mySQL = RubiconBot.getMySQL();
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT * FROM members WHERE NOT mute = '' AND NOT mute = 'permanent'");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Date expiry = new Date(rs.getLong("mute"));
                Long guildid = rs.getLong("serverid");
                Long memberid = rs.getLong("userid");
                Guild guild = RubiconBot.getShardManager().getGuildById(rs.getLong("serverid"));

                Member member = guild.getMemberById(rs.getLong("userid"));
                RubiconMember rMember = RubiconMember.fromMember(member);
                if (expiry.after(new Date())) {
                    rMember.unmute();
                    CommandUnmute.deassignRole(member);
                    return;
                }
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        rMember.unmute();
                        CommandUnmute.deassignRole(member);
                    }
                }, expiry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
