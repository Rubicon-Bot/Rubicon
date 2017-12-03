package fun.rubicon.commands.tools;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.tools
 */

public class CommandSay extends CommandHandler {

    public CommandSay() {
        super(new String[]{"say", "s"}, CommandCategory.TOOLS, new PermissionRequirements(1,"command.say"),"Send a Message as the Bot!","say <Channel> <Message>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if(parsedCommandInvocation.args.length < 2) {
            return new MessageBuilder().setEmbed(EmbedUtil.error(":warning: Error", getUsage()).build()).build();
        }

        if(parsedCommandInvocation.invocationMessage.getMentionedChannels().size() != 1) {
            return new MessageBuilder().setEmbed(EmbedUtil.error(":warning: Error", getUsage()).build()).build();
        }

        String text = "";
        for(int i = parsedCommandInvocation.invocationMessage.getMentionedChannels().get(0).getAsMention().split(" ").length; i < parsedCommandInvocation.args.length; i++) {
            text += parsedCommandInvocation.args[i] + " ";
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(parsedCommandInvocation.invocationMessage.getMember().getEffectiveName() + "'s Commands", null, parsedCommandInvocation.invocationMessage.getMember().getUser().getEffectiveAvatarUrl());
        builder.setDescription(text);
        builder.setColor(Colors.COLOR_PRIMARY);
        parsedCommandInvocation.invocationMessage.getMentionedChannels().get(0).sendMessage(builder.build()).queue();
        return new MessageBuilder().setEmbed(EmbedUtil.success("Completed", "Send Message :`" + text + "` to Channel" + parsedCommandInvocation.invocationMessage.getMentionedChannels().get(0).getAsMention()).build()).build();
    }
}
