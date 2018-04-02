/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the 'help' command which prints command description, aliases and usage.
 *
 * @author Yannick Seeger, tr808axm
 */
public class CommandHelp extends CommandHandler {

    public CommandHelp() {
        super(new String[]{"help", "usage", "?", "command", "manual", "man"}, CommandCategory.GENERAL,
                new PermissionRequirements("help", false, true),
                "Shows the command manual.", "[command]");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invoke, UserPermissions userPermissions) {
        if (invoke.getArgs().length == 0) {
            // show complete command manual
            SafeMessage.sendMessage(invoke.getTextChannel(), generateFullHelp(invoke).build());
            return null;
        } else {
            CommandHandler handler = RubiconBot.getCommandManager().getCommandHandler(invoke.getArgs()[0]);
            return handler == null
                    // invalid command
                    ? new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setColor(Colors.COLOR_ERROR)
                    .setTitle(":warning:" + invoke.translate("command.help.warning.title"))
                    .setDescription(invoke.translate("command.help.warning.description").replaceAll("%command%", invoke.getArgs()[0]).replaceAll("%help_command%", invoke.getPrefix() + invoke.getCommandInvocation()))
                    .build()).build()
                    // show command help for a single command
                    : handler.createHelpMessage(Info.BOT_DEFAULT_PREFIX, invoke.getArgs()[0]);
        }
    }

    private EmbedBuilder generateFullHelp(CommandManager.ParsedCommandInvocation invocation) {
        EmbedBuilder builder = new EmbedBuilder();
        List<CommandHandler> filteredCommandList = RubiconBot.getCommandManager().getCommandAssociations().values().stream().filter(commandHandler -> commandHandler.getCategory() != CommandCategory.BOT_OWNER).collect(Collectors.toList());

        ArrayList<String> alreadyAdded = new ArrayList<>();

        StringBuilder listGeneral = new StringBuilder();
        for (CommandHandler commandHandler : filteredCommandList) {
            if (commandHandler.getCategory() == CommandCategory.GENERAL && !alreadyAdded.contains(commandHandler.getInvocationAliases()[0])) {
                alreadyAdded.add(commandHandler.getInvocationAliases()[0]);
                listGeneral.append("`").append(commandHandler.getInvocationAliases()[0]).append("` ");
            }
        }
        StringBuilder listMusic = new StringBuilder();
        for (CommandHandler commandHandler : filteredCommandList) {
            if (commandHandler.getCategory() == CommandCategory.MUSIC && !alreadyAdded.contains(commandHandler.getInvocationAliases()[0])) {
                alreadyAdded.add(commandHandler.getInvocationAliases()[0]);
                listMusic.append("`").append(commandHandler.getInvocationAliases()[0]).append("` ");
            }
        }
        StringBuilder listModeration = new StringBuilder();
        for (CommandHandler commandHandler : filteredCommandList) {
            if (commandHandler.getCategory() == CommandCategory.MODERATION && !alreadyAdded.contains(commandHandler.getInvocationAliases()[0])) {
                alreadyAdded.add(commandHandler.getInvocationAliases()[0]);
                listModeration.append("`").append(commandHandler.getInvocationAliases()[0]).append("` ");
            }
        }
        StringBuilder listAdmin = new StringBuilder();
        for (CommandHandler commandHandler : filteredCommandList) {
            if (commandHandler.getCategory() == CommandCategory.ADMIN && !alreadyAdded.contains(commandHandler.getInvocationAliases()[0])) {
                alreadyAdded.add(commandHandler.getInvocationAliases()[0]);
                listAdmin.append("`").append(commandHandler.getInvocationAliases()[0]).append("` ");
            }
        }
        StringBuilder listSettings = new StringBuilder();
        for (CommandHandler commandHandler : filteredCommandList) {
            if (commandHandler.getCategory() == CommandCategory.SETTINGS && !alreadyAdded.contains(commandHandler.getInvocationAliases()[0])) {
                alreadyAdded.add(commandHandler.getInvocationAliases()[0]);
                listSettings.append("`").append(commandHandler.getInvocationAliases()[0]).append("` ");
            }
        }
        StringBuilder listTools = new StringBuilder();
        for (CommandHandler commandHandler : filteredCommandList) {
            if (commandHandler.getCategory() == CommandCategory.TOOLS && !alreadyAdded.contains(commandHandler.getInvocationAliases()[0])) {
                alreadyAdded.add(commandHandler.getInvocationAliases()[0]);
                listTools.append("`").append(commandHandler.getInvocationAliases()[0]).append("` ");
            }
        }
        StringBuilder listFun = new StringBuilder();
        for (CommandHandler commandHandler : filteredCommandList) {
            if (commandHandler.getCategory() == CommandCategory.FUN && !alreadyAdded.contains(commandHandler.getInvocationAliases()[0])) {
                alreadyAdded.add(commandHandler.getInvocationAliases()[0]);
                listFun.append("`").append(commandHandler.getInvocationAliases()[0]).append("` ");
            }
        }

        builder.setTitle(":information_source: " + invocation.translate("command.help.list.title"));
        builder.setDescription(invocation.translate("command.help.list.description").replaceAll("%help_command%", invocation.getPrefix() + "help <command>").replaceAll("%website%", "[rubicon.fun](https://rubicon.fun)"));
        builder.setColor(Colors.COLOR_SECONDARY);
        builder.setFooter(invocation.translate("command.help.list.footer").replaceAll("%command_amount%", new HashSet<>(RubiconBot.getCommandManager().getCommandAssociations().values()).size() + ""), null);

        //Add Categories
        builder.addField(invocation.translate("command.help.list.category.general"), listGeneral.toString(), false);
        builder.addField(invocation.translate("command.help.list.category.music"), listMusic.toString(), false);
        builder.addField(invocation.translate("command.help.list.category.moderation"), listModeration.toString(), false);
        builder.addField(invocation.translate("command.help.list.category.admin"), listAdmin.toString(), false);
        builder.addField(invocation.translate("command.help.list.category.settings"), listSettings.toString(), false);
        builder.addField(invocation.translate("command.help.list.category.tools"), listTools.toString(), false);
        builder.addField(invocation.translate("command.help.list.category.fun"), listFun.toString(), false);
        return builder;
    }
}