/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.settings;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * Handles the 'autorole' command.
 *
 * @author Leon Kappes / Lee
 */
public class CommandAutorole extends CommandHandler {
    public CommandAutorole() {
        super(new String[]{"autorole"}, CommandCategory.ADMIN, new PermissionRequirements("command.autorole", false, false), "Set the Autorole. Triggers when a User Join your Guild", "<@Role/Rolename>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.getArgs().length < 1) {
            return createHelpMessage();
        }
        if (parsedCommandInvocation.getMessage().getMentionedRoles().size() < 1) {
            String toset = parsedCommandInvocation.getMessage().getGuild().getRolesByName(parsedCommandInvocation.getArgs()[0], true).get(0).getId();
            RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.getMessage().getGuild(), "autorole", toset);
        } else {
            RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.getMessage().getGuild(), "autorole", parsedCommandInvocation.getMessage().getMentionedRoles().get(0).getId());
        }
        return new MessageBuilder().setEmbed(EmbedUtil.success("Succes", "Succesfully set the Autorole!").build()).build();
    }
}
