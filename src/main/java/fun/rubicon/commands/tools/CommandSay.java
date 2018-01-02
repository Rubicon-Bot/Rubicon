/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public class CommandSay extends CommandHandler {

    public CommandSay() {
        super(new String[]{"say", "s"}, CommandCategory.TOOLS, new PermissionRequirements(PermissionLevel.WITH_PERMISSION, "command.say"), "Send a Message as the Bot!", "<Channel> <Message>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.args.length < 2) {
            return createHelpMessage();
        }

        if (parsedCommandInvocation.invocationMessage.getMentionedChannels().size() != 1) {
            return createHelpMessage();
        }

        String text = "";
        for (int i = parsedCommandInvocation.invocationMessage.getMentionedChannels().get(0).getAsMention().split(" ").length; i < parsedCommandInvocation.args.length; i++) {
            text += parsedCommandInvocation.args[i] + " ";
        }
        parsedCommandInvocation.invocationMessage.getMentionedChannels().get(0).sendMessage(text).queue();
        return new MessageBuilder().setEmbed(EmbedUtil.success("Successful", "Successful sent message in " + parsedCommandInvocation.invocationMessage.getMentionedChannels().get(0).getAsMention()).build()).build();
    }
}
