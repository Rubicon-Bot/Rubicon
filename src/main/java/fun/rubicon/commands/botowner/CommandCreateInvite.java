/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.botowner;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public class CommandCreateInvite extends CommandHandler {

    public CommandCreateInvite() {
        super(new String[]{"createinvite"}, CommandCategory.BOT_OWNER, new PermissionRequirements("command.createinvite", true, false), "Creates an invite", "<guildid>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        try {
            return new MessageBuilder().append(parsedCommandInvocation.getMessage().getJDA().getGuildById(parsedCommandInvocation.getArgs()[0]).getTextChannels().get(0).createInvite().complete().getURL()).build();
        } catch (Exception ex) {
            Logger.info("Create Invite: " + ex.getMessage());
            return new MessageBuilder().setEmbed(EmbedUtil.error("Error!", "An error occurred!").build()).build();
        }
    }
}