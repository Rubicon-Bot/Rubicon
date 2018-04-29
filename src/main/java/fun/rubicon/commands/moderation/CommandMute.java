package fun.rubicon.commands.moderation;

import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.features.poll.PunishmentHandler;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.*;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.*;

public class CommandMute extends CommandHandler implements PunishmentHandler {
    public CommandMute() {
        super(new String[]{"mute", "tempmute"}, CommandCategory.MODERATION, new PermissionRequirements("mute", false, false), "Mute members temporary or permanent", "<@User> [time]");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        String[] args = invocation.getArgs();
        Message message = invocation.getMessage();
        Member member = invocation.getMember();
        Guild guild = invocation.getGuild();

        if (args.length == 0)
            return createHelpMessage();
        if (args[0].equals("settings"))
            return new MessageBuilder().setEmbed(EmbedUtil.info("Work in progress", "This feature is still work in progress").build()).build();
        if (message.getMentionedUsers().isEmpty())
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.mute.unknownuser.titlecommand.mute.unknownuser.title"), invocation.translate("command.mute.unknownuser.description")).build()).build();
        Member victimMember = message.getMentionedMembers().get(0);
        RubiconMember victim = RubiconMember.fromMember(victimMember);
        String rawArgs = String.join(" ", new ArrayList<>(Arrays.asList(args))).replace("@", "x").replace(victimMember.getEffectiveName(), "");
        args = rawArgs.split(" ");
        if (victim.isMuted())
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.mute.muted.permanent.title"), invocation.translate("command.mute.ismuted.description")).build()).build();
        if (victimMember.equals(guild.getSelfMember()) || Arrays.asList(Info.BOT_AUTHOR_IDS).contains(victimMember.getUser().getIdLong()))
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.mute.donotmuterubicon.title"), invocation.translate("command.mute.donotmuterubicon.description")).build()).build();
        if (!member.canInteract(victimMember) && !Arrays.asList(Info.BOT_AUTHOR_IDS).contains(victimMember.getUser().getIdLong()))
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.mute.nopermissions.user.title"), String.format(invocation.translate("command.mute.nopermissions.user.description"), victimMember.getAsMention())).build()).build();
        if (!invocation.getSelfMember().canInteract(victimMember))
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.mute.nopermissions.bot.title"), String.format(invocation.translate("command.mute.nopermissions.bot.description"), victimMember.getAsMention())).build()).build();
        if (args.length == 1) {
            if (!new PermissionRequirements("mute.permanent", false, false).coveredBy(invocation.getPerms()))
                return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.mute.nopermissions.user.title"), invocation.translate("command.mute.permanent.noperms.description")).build()).build();

            victim.mute();
            return new MessageBuilder().setEmbed(EmbedUtil.success(invocation.translate("command.mute.muted.permanent.title"), String.format(invocation.translate("command.mute.muted.permanent.description"), victimMember.getAsMention())).build()).build();
        } else if (args.length > 1) {
            Date expiry = StringUtil.parseDate(args[1]);
            if (expiry == null)
                return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("general.punishment.invalidnumber.title"), invocation.translate("general.punishment.invalidnumber.description")).build()).build();
            victim.mute(expiry);
            return new MessageBuilder().setEmbed(EmbedUtil.success(invocation.translate("command.mute.muted.temporary.title"), invocation.translate("command.mute.muted.temporary.description").replace("%mention%", victimMember.getAsMention()).replace("%date%", DateUtil.formatDate(expiry, invocation.translate("date.format")))).build()).build();
        }
        return createHelpMessage();
    }

    @Override
    public void loadPunishments() {
        Cursor cursor = RubiconBot.getRethink().db.table("punishments").filter(RubiconBot.getRethink().rethinkDB.hashMap("type", "mute")).run(RubiconBot.getRethink().connection);
        for (Object obj : cursor) {
            Map map = (Map) obj;
            RubiconMember member;
            try {
               member = RubiconMember.fromMember(RubiconBot.getShardManager().getGuildById((String) map.get("guildId")).getMemberById((String) map.get("userId")));
            }catch (Exception ignored){
                continue;
            }
            RubiconBot.getPunishmentManager().getMuteCache().put(member.getMember(), (long) map.get("expiry"));
            if ((long) map.get("expiry") == 1L) return;
            if (new Date((long) map.get("expiry")).before(new Date())) member.unmute(true);
            RubiconMember finalMember = member;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    finalMember.unmute(true);
                }
            }, new Date((long) map.get("expiry")));
        }
    }
}