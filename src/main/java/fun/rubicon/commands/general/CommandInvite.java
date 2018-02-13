/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public class CommandInvite extends CommandHandler {

    public CommandInvite() {
        super(new String[]{"invite", "inv"}, CommandCategory.GENERAL, new PermissionRequirements("command.invite", false, true), "Gives you the invite-link of the bot.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.COLOR_SECONDARY);
        builder.setAuthor(Info.BOT_NAME + " - Invite", null, parsedCommandInvocation.getMessage().getJDA().getSelfUser().getAvatarUrl());
        builder.setDescription("[Invite Rubicon Bot](https://discordapp.com/oauth2/authorize?client_id=380713705073147915&scope=bot&permissions=1898982486)\n" +
                "[Join Rubicon Server](https://discord.gg/UrHvXY9)");
        return new MessageBuilder().setEmbed(builder.build()).build();
    }


}
