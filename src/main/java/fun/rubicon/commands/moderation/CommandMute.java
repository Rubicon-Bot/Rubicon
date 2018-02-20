package fun.rubicon.commands.moderation;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.core.translation.TranslationLocale;
import fun.rubicon.core.translation.TranslationManager;
import fun.rubicon.mysql.MySQL;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Logger;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;

import javax.xml.soap.Text;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommandMute extends CommandHandler {

    public CommandMute() {
        super(new String[]{"mute"}, CommandCategory.MODERATION, new PermissionRequirements("mute", false, false), "Easily mute a player", "<@User> [time in minutes]");

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
        if (message.getMentionedUsers().isEmpty())
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.unknownuser.title"), command.translate("command.mute.unknownuser.description")).build()).build();
        Member victimMember = guild.getMember(message.getMentionedUsers().get(0));
        RubiconMember victim = RubiconMember.fromMember(victimMember);
        if (victim.isMuted())
            return new MessageBuilder().setEmbed(EmbedUtil.error("Already muted", "This user is already muted").build()).build();
        if (!member.canInteract(victimMember))
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.nopermissions.user.title"), String.format(command.translate("command.mute.nopermissions.user.description"), victimMember.getAsMention())).build()).build();
        if (!command.getSelfMember().canInteract(victimMember))
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.nopermissions.bot.title"), String.format(command.translate("command.mute.nopermissions.bot.description"), victimMember.getAsMention())).build()).build();
        if (!assignRole(victimMember))
            return new MessageBuilder().setEmbed(EmbedUtil.error(command.translate("command.mute.nopermissions.role.title"), command.translate("command.mute.nopermissions.role.description")).build()).build();
        if (args.length == 1) {
            victim.mute();
            return new MessageBuilder().setEmbed(EmbedUtil.success(command.translate("command.mute.muted.permanent.title"), String.format(command.translate("command.mute.muted.permanent.description"), victimMember.getAsMention())).build()).build();
        } else if (args.length > 1) {
            Integer delay = 0;
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
            SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    victim.unmute();
                    CommandUnmute.deassignRole(victim.getMember());
                }
            }, expiry);
            return new MessageBuilder().setEmbed(EmbedUtil.success(command.translate("command.mute.muted.temporary.title"), command.translate("command.mute.muted.temporary.permanent").replace("%mention%", victimMember.getAsMention()).replace("%date%", sdf.format(expiry))).build()).build();
        }
        return createHelpMessage();

    }

    public static void loadMutes(){
        MySQL mySQL = RubiconBot.getMySQL();
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT * FROM members WHERE NOT mute = '' AND NOT mute = 'permanent'");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Date expiry = new Date(rs.getLong("mute"));
                if (expiry.after(new Date())) {
                    Guild guild = RubiconBot.getShardManager().getGuildById(rs.getLong("serverid"));
                    Member member = guild.getMemberById(rs.getLong("userid"));
                    RubiconMember rMember = RubiconMember.fromMember(member);
                    rMember.unmute();
                    CommandUnmute.deassignRole(member);
                    return;
                }
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            Member member = RubiconMember.fromMember(RubiconBot.getShardManager().getGuildById(rs.getLong("serverid")).getMemberById(rs.getLong("userid"))).unmute().getMember();
                            CommandUnmute.deassignRole(member);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }, expiry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
