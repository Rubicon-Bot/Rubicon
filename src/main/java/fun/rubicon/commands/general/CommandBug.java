/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;


public class CommandBug extends CommandHandler {

    public CommandBug() {
        super(new String[]{"bug", "bugreport"}, CommandCategory.GENERAL, new PermissionRequirements("command.bug", false, true), "Sends a bug to the bot developers", "<message (min. 3 words)>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.getArgs();
        Message message = parsedCommandInvocation.getMessage();
        //Check if enough args
        if (args.length < 3) {
            return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "bug [message] (min. 3 args)").build()).build();
        }
        //Make String out of args
        String text = parsedCommandInvocation.getMessage().getContentDisplay().replace(parsedCommandInvocation.getPrefix() + parsedCommandInvocation.getCommandInvocation() + " ", "");

        //Post Report to Dev Server
        RubiconBot.getJDA().getTextChannelById("382231366064144384").sendMessage(
                new EmbedBuilder()
                        .setAuthor(message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator(), null, message.getAuthor().getAvatarUrl())
                        .setDescription("**New Bug Detected!**\n```fix\n" + text + "```")
                        .build()
        ).queue();
        //User Feedback
        return new MessageBuilder().setEmbed(EmbedUtil.success("Bug reported", "Successfully send the bug to the developers.").build()).build();
    }
}
