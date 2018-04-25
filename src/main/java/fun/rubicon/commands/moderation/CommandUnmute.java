package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;

public class CommandUnmute extends CommandHandler{
    public CommandUnmute() {
        super(new String[] {"unmute", "demute"}, CommandCategory.MODERATION, new PermissionRequirements("unmute", false, false), "Unmute members, that are muted through rc!mute command", "<@User>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        String[] args = invocation.getArgs();
        Message message = invocation.getMessage();
        Member member = invocation.getMember();
        Guild guild = invocation.getGuild();
        if(args.length == 0)
            return createHelpMessage();
        if(message.getMentionedMembers().isEmpty())
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.mute.unknownuser.title"), invocation.translate("command.mute.unknownuser.description")).build()).build();
        Member victimMember = message.getMentionedMembers().get(0);
        RubiconMember victim = RubiconMember.fromMember(victimMember);
        if(!victim.isMuted())
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.unmute.notmuted.title"), invocation.translate("command.unmute.notmuted.description")).build()).build();
        if(victimMember.equals(guild.getSelfMember()) || Arrays.asList(Info.BOT_AUTHOR_IDS).contains(victimMember.getUser().getIdLong()))
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.mute.donotmuterubicon.title"), invocation.translate("command.mute.donotmuterubicon.description")).build()).build();
        if(!member.canInteract(victimMember) && !Arrays.asList(Info.BOT_AUTHOR_IDS).contains(victimMember.getUser().getIdLong()))
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.mute.nopermissions.user.title"), String.format(invocation.translate("command.mute.nopermissions.user.description"), victimMember.getAsMention())).build()).build();
        if(!invocation.getSelfMember().canInteract(victimMember))
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.mute.nopermissions.bot.title"), String.format(invocation.translate("command.mute.nopermissions.bot.description"), victimMember.getAsMention())).build()).build();
        victim.unmute(true);
        return new MessageBuilder().setEmbed(EmbedUtil.success(invocation.translate("command.unmute.unmuted.title"), String.format(invocation.translate("command.unmute.unmuted.description"), victimMember.getAsMention())).build()).build();
    }
}
