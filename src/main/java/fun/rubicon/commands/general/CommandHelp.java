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
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.*;

/**
 * Handles the 'help' command which prints command description, aliases and usage.
 *
 * @author Yannick Seeger, tr808axm
 */
public class CommandHelp extends CommandHandler {

    public CommandHelp() {
        super(new String[]{"help", "usage", "?", "command", "manual", "man"}, CommandCategory.GENERAL,
                new PermissionRequirements(PermissionLevel.EVERYONE, "command.help"),
                "Shows the command manual.", "[command]");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        //Generate JSON File for website
        /*int i = 0;
        StringBuilder out = new StringBuilder();
        List<CommandHandler> allCommands = new ArrayList<>();
        for (CommandHandler commandHandler : RubiconBot.getCommandManager().getCommandAssociations().values()) {
            if (!allCommands.contains(commandHandler))
                allCommands.add(commandHandler);
        }
        for (CommandHandler commandHandler : allCommands) {
            if (commandHandler.getCategory().equals(CommandCategory.BOT_OWNER))
                continue;
            StringBuilder usage = new StringBuilder();
            for (String part : getParameterUsage().split("\n")) {
                if (getParameterUsage().split("\n").length > 1)
                    usage.append(Info.BOT_DEFAULT_PREFIX + commandHandler.getInvocationAliases()[0] + " " + part + "\\n");
                else
                    usage.append(Info.BOT_DEFAULT_PREFIX + commandHandler.getInvocationAliases()[0] + " " + part);
            }
            out.append("{\n\"id\":\"" + i + "\",\"name\":\"" + commandHandler.getInvocationAliases()[0] + "\",\n" +
                    "\t\"command\":\"" + Info.BOT_DEFAULT_PREFIX + commandHandler.getInvocationAliases()[0] + "\",\n" +
                    "\t\"description\":\"" + commandHandler.getDescription() + "\",\n" +
                    "\t\"category\":\"" + commandHandler.getCategory().getId() + "\",\n" +
                    "\t\"usage\":\"" + usage + "\"\n},\n");
            i++;
        }
        Logger.debug(out.toString());*/
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
                if (categoryEntry.getKey().equals(CommandCategory.BOT_OWNER))
                    continue;
                StringBuilder builder = new StringBuilder();
                categoryEntry.getValue().forEach(handler -> builder.append('`').append(parsedCommandInvocation.serverPrefix)
                        .append(handler.getInvocationAliases()[0]).append("` — ").append(handler.getDescription()).append('\n'));
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
                    : handler.createHelpMessage(Info.BOT_DEFAULT_PREFIX, parsedCommandInvocation.args[0]);
        }
    }
}
