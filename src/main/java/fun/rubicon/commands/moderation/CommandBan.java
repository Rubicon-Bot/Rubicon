package fun.rubicon.commands.moderation;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.features.PunishmentHandler;
import fun.rubicon.mysql.MySQL;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.*;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CommandBan extends CommandHandler implements PunishmentHandler{
    public CommandBan() {
        super(new String[] {"ban", "tempban"}, CommandCategory.MODERATION, new PermissionRequirements("ban", false, false), "Easily ban or tempban members.", "<@User> [Time in minutes]");
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
                /*switch (args[1]){
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
                }*/
            } else
                return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.settings.nopermission.title"), command.translate("command.mute.settings.nopermission.description")).build()).build();
            return null;
        }
        if (message.getMentionedUsers().isEmpty())
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.ban.unknowuser.title"), command.translate("command.ban.unknownuser.description")).build()).build();
        Member victimMember = guild.getMember(message.getMentionedUsers().get(0));
        RubiconMember victim = RubiconMember.fromMember(victimMember);
        if (victim.isMuted())
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.ban.muted.permanent.title"), command.translate("command.ban.muted.permanent.description")).build()).build();
        if(victimMember.equals(guild.getSelfMember()) || Arrays.asList(Info.BOT_AUTHOR_IDS).contains(victimMember.getUser().getIdLong()))
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.ban.donotbanrubicon.title"), command.translate("command.ban.donotbanrubicon.description")).build()).build();
        if (!member.canInteract(victimMember) && !Arrays.asList(Info.BOT_AUTHOR_IDS).contains(member.getUser().getIdLong()))
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.ban.nopermissions.user.title"), String.format(command.translate("command.ban.nopermissions.user.description"), victimMember.getAsMention())).build()).build();
        if (!command.getSelfMember().canInteract(victimMember))
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.ban.nopermissions.bot.title"), String.format(command.translate("command.ban.nopermissions.bot.description"), victimMember.getAsMention())).build()).build();
        if (!guild.getSelfMember().getPermissions().contains(Permission.BAN_MEMBERS))
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.ban.nopermissions.ban.title"), command.translate("command.mute.nopermissions.role.description")).build()).build();
        RubiconGuild rGuild = RubiconGuild.fromGuild(guild);
        if (args.length == 1) {
            if(!new PermissionRequirements("ban.permanent", false, false).coveredBy(command.getPerms()))
                return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.nopermissions.user.title"), command.translate("command.ban.nopermissions.ban.description")).build()).build();
            guild.getController().ban(victimMember.getUser(), 7).queue();
            return new MessageBuilder().setEmbed(EmbedUtil.success(command.translate("command.ban.muted.permanent.title"), String.format(command.translate("command.ban.muted.permanent.description"), victimMember.getAsMention())).build()).build();
        } else if (args.length > 1) {
            Date expiry = StringUtil.parseDate(args[1]);
            if(expiry == null)
                return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.ban.invalidnumber.title"), command.translate("command.ban.invalidnumber.description")).build()).build();
            RubiconMember.fromMember(member).ban(expiry);
            guild.getController().ban(victimMember, 7).queue();
            if(rGuild.useMuteSettings())
                //SafeMessage.sendMessage(rGuild.getMuteChannel(), rGuild.getMuteMessage().replace("%moderator%", member.getAsMention()).replace("%mention%", victim.getMember().getAsMention()).replace("%date%", DateUtil.formatDate(expiry, command.translate("date.format"))));
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    guild.getController().unban(victimMember.getUser()).queue();
                    RubiconUser.fromUser(victim.getUser()).unban(guild);
                }
            }, expiry);
            return new MessageBuilder().setEmbed(EmbedUtil.success(command.translate("command.ban.muted.temporary.title"), command.translate("command.ban.muted.temporary.permanent").replace("%mention%", victimMember.getAsMention()).replace("%date%", DateUtil.formatDate(expiry, command.translate("date.format")))).build()).build();
        }
        return createHelpMessage();

    }

    @Override
    public void loadPunishments() {
        MySQL mySQL = RubiconBot.getMySQL();
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT * FROM members WHERE NOT banned = ''");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Date expiry = new Date(rs.getLong("banned"));
                Long guildid = rs.getLong("serverid");
                Long memberid = rs.getLong("userid");
                Guild guild = RubiconBot.getShardManager().getGuildById(rs.getLong("serverid"));

                User user = RubiconBot.getShardManager().getUserById(rs.getLong("userid"));
                if (expiry.after(new Date())) {
                    guild.getController().unban(user).queue();
                    RubiconUser.fromUser(user).unban(guild);
                    return;
                }
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        guild.getController().unban(user).queue();
                        RubiconUser.fromUser(user).unban(guild);
                    }
                }, expiry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
