package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author CrazyPilz
 */


public class CommandNick extends CommandHandler {

    public CommandNick() {
        super(new String[]{"nick"}, CommandCategory.TOOLS, new PermissionRequirements("nick", false, false), "Sets the nickname of an user", "[@User] <Nickname>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        String[] args = invocation.getArgs();
        Message msg = invocation.getMessage();
        Member victim;
        Member selfMember = invocation.getSelfMember();
        String nickname;
        StringBuilder builder = new StringBuilder();
        if (args.length == 0)
            return createHelpMessage();
        if (msg.getMentionedMembers().isEmpty()) {
            victim = invocation.getMember();
            nickname = invocation.getArgsString();
        } else {
            victim = msg.getMentionedMembers().get(0);
            for (int i = 1; i < args.length; i++) {
                builder.append(args[i]).append(" ");
            }
            nickname = builder.toString();
        }
        if (!selfMember.canInteract(victim))
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.nick.cantinteract.title"), String.format(invocation.translate("command.nick.cantinteract.bot.description"), victim.getAsMention())).build()).build();

        if (!msg.getMember().canInteract(victim))
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.nick.cantinteract.title"), String.format(invocation.translate("command.nick.cantinteract.member.description"), victim.getAsMention())).build()).build();

        if (!selfMember.getPermissions().contains(Permission.NICKNAME_MANAGE))
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.nick.cantinteract.title"), invocation.translate("command.nick.nopermission.description")).build()).build();

        if (nickname.length() > 32)
            return new MessageBuilder().setEmbed(EmbedUtil.error(invocation.translate("command.nick.toolong.title"), invocation.translate("command.nick.toolong.description")).build()).build();

        invocation.getGuild().getController().setNickname(victim, nickname).queue();
        return new MessageBuilder().setEmbed(EmbedUtil.success(invocation.translate("command.nick.success.title"), String.format(invocation.translate("command.nick.success.description"), nickname)).build()).build();
    }
}