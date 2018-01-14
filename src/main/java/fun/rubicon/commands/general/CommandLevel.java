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
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class CommandLevel extends CommandHandler {


    public CommandLevel() {
        super(new String[]{"level", "lvl"}, CommandCategory.FUN, new PermissionRequirements(PermissionLevel.EVERYONE, "command.rank"), "Get your level, points and ruby's.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        User user = parsedCommandInvocation.getMessage().getAuthor();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.COLOR_PRIMARY);
        builder.setAuthor(user.getName(), null, user.getAvatarUrl());
        builder.addField("Points", RubiconBot.getMySQL().getUserValue(user, "points"), true);
        builder.addField("Level", RubiconBot.getMySQL().getUserValue(user, "level"), true);
        builder.addField("Ruby's", RubiconBot.getMySQL().getUserValue(user, "money"), true);
        return new MessageBuilder().setEmbed(builder.build()).build();
    }
}
