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
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.features.translation.TranslationLocale;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static fun.rubicon.util.EmbedUtil.error;
import static fun.rubicon.util.EmbedUtil.info;
import static fun.rubicon.util.EmbedUtil.message;

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
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        TranslationLocale locale = RubiconBot.sGetTranslations()
                .getUserLocale(invocation.invocationMessage.getAuthor());
        if (invocation.args.length == 0) {
            // show complete command manual
            return message(info(locale.getResourceBundle().getString("command.help.title"),
                            commandFormat(invocation, locale, "command.help.description"))
                    .addField(commandFormat(invocation, locale, "command.help.field.documentation.title"),
                            commandFormat(invocation, locale, "command.help.field.documentation.content"),
                            false)
                    .setFooter(locale.getResourceBundle().getString("command.help.footer").replaceAll("%count%",
                            String.valueOf(new HashSet<>(RubiconBot.getCommandManager().getCommandAssociations().values()).size())),
                            null));
        } else {
            CommandHandler handler = RubiconBot.getCommandManager().getCommandHandler(invocation.args[0]);
            return handler == null
                    // invalid command
                    ? message(error(locale.getResourceBundle().getString("command.help.error.invalidcommand.title"),
                        commandFormat(invocation, locale, "command.help.error.invalidcommand.description")
                                .replaceAll("%othercommand%", invocation.getArgs()[0])))
                    // show command help for a single command
                    : handler.createHelpMessage(invocation.getPrefix(), invocation.args[0]);
        }
    }

    private String commandFormat(CommandManager.ParsedCommandInvocation invocation, TranslationLocale locale, String key) {
        return locale.getResourceBundle().getString(key)
                .replaceAll("%prefix%", invocation.serverPrefix)
                .replaceAll("%command%", invocation.invocationCommand);
    }
}
