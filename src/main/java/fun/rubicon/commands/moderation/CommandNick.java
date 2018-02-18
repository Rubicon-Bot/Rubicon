package fun.rubicon.commands.moderation;

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

import java.util.ArrayList;
import java.util.Arrays;

public class CommandNick extends CommandHandler {
    public CommandNick() {
        super(new String[]{"nick", "nickname", "name"}, CommandCategory.TOOLS, new PermissionRequirements("command.nick", false, false), "Easily nick yourself or others", "[@User] <nickname/reset>", false);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.getMessage();
        String[] args = parsedCommandInvocation.getArgs();
        if (args.length < 1) {
            return createHelpMessage();
        }
        Member member;
        if (!message.getMentionedUsers().isEmpty())
            member = message.getGuild().getMember(message.getMentionedUsers().get(0));
        else
            member = message.getMember();
        String oldName = member.getEffectiveName();
        String nickname = String.join(" ", new ArrayList<>(Arrays.asList(args).subList(1, args.length))).replace(member.getEffectiveName(), "").replace("@", "");
        if (!message.getGuild().getSelfMember().canInteract(member) || !message.getGuild().getSelfMember().hasPermission(Permission.NICKNAME_MANAGE))
            return new MessageBuilder().setEmbed(EmbedUtil.error("No permission", "Sorry but Rubicon has no permission to change " + member.getAsMention() + "'s nickname").build()).build();
        if (nickname.length() > 32) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("Nickname to long", "Your nickname can not be longer than 32 chars").build()).build();
        }
        if (nickname.equals("reset")) {
            message.getGuild().getController().setNickname(member, member.getUser().getName()).queue();
            return new MessageBuilder().setEmbed(EmbedUtil.success("Reset Nickname", "Succesfully reset " + member.getAsMention() + "'s nickname").build()).build();
        } else {
            message.getGuild().getController().setNickname(member, nickname).queue();
            return new MessageBuilder().setEmbed(EmbedUtil.success("Changed nickname", "Successfully changed nickname of " + oldName + " to `" + nickname + "`").build()).build();
        }
    }
}
