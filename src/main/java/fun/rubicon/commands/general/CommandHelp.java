/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.*;

/**
 * Handles the 'help' command which prints command description, aliases and usage.
 * @author Yannick Seeger / ForYaSee, tr808axm
 */
public class CommandHelp extends CommandHandler {

    public CommandHelp() {
        super(new String[]{"help", "usage", "?", "command", "manual", "man"}, CommandCategory.GENERAL,
                new PermissionRequirements(0, "command.help"),
                "Shows the command manual.", "help [command]");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.args.length == 0) {
            // show complete command manual
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(Colors.COLOR_SECONDARY)
                    .setTitle(":information_source: Rubicon Bot command manual")
                    .setDescription("Use `" + parsedCommandInvocation.serverPrefix
                            + parsedCommandInvocation.invocationCommand + " <command>` to get a more detailed command help");
            Map<CommandCategory, List<CommandHandler>> commandCategoryListMap = new HashMap<>();
            for (CommandHandler commandHandler : RubiconBot.getCommandManager().getCommandAssociations().values()) {
                if (!commandCategoryListMap.containsKey(commandHandler.getCategory()))
                    commandCategoryListMap.put(commandHandler.getCategory(), new ArrayList<>());
                List<CommandHandler> categoryList = commandCategoryListMap.get(commandHandler.getCategory());
                if (!categoryList.contains(commandHandler))
                    categoryList.add(commandHandler);
            }
            for (Map.Entry<CommandCategory, List<CommandHandler>> categoryEntry : commandCategoryListMap.entrySet()) {
                StringBuilder builder = new StringBuilder();
                categoryEntry.getValue().forEach(handler -> builder.append('`').append(parsedCommandInvocation.serverPrefix)
                        .append(handler.getUsage()).append("` — ").append(handler.getDescription()).append('\n'));
                embedBuilder.addField(categoryEntry.getKey().getDisplayname() + " — "
                        + categoryEntry.getValue().size(), builder.toString(), false);
            }
            embedBuilder.setFooter("Loaded a total of "
                    + new HashSet<>(RubiconBot.getCommandManager().getCommandAssociations().values()).size()
                    + " commands.", null);
            parsedCommandInvocation.invocationMessage.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder().setEmbed(embedBuilder.build()).build()).queue());
            return new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setColor(Colors.COLOR_PRIMARY)
                    .setTitle(":white_check_mark: Command help sent")
                    .setDescription("Check your private messages <@"
                            + parsedCommandInvocation.invocationMessage.getAuthor().getId() + ">!")
                    .build()).build();
        } else {
            CommandHandler handler = RubiconBot.getCommandManager().getCommandHandler(parsedCommandInvocation.args[0]);
            return handler == null
                    // invalid command
                    ? new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setColor(Colors.COLOR_ERROR)
                    .setTitle(":warning: Invalid command")
                    .setDescription("There is no command named '" + parsedCommandInvocation.args[0] + "'. Use `"
                            + parsedCommandInvocation.serverPrefix + parsedCommandInvocation.invocationCommand
                            + "` to get a full command list.")
                    .build()).build()
                    // show command help for a single command
                    : new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setColor(Colors.COLOR_SECONDARY)
                    .setTitle(":information_source: '" + parsedCommandInvocation.invocationCommand + "' command help")
                    .setDescription(handler.getDescription())
                    .addField("Aliases", String.join(", ", handler.getInvocationAliases()), false)
                    .addField("Usage", handler.getUsage(), false)
                    .build()).build();
        }
    }
}
