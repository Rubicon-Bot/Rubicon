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
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.concurrent.TimeUnit;

public class CommandPrefix extends CommandHandler {
    public CommandPrefix() {
        super(new String[]{"prefix", "pr"}, CommandCategory.SETTINGS,
                new PermissionRequirements("command.prefix", false, false),
                "Set the Server Prefix!", "<prefix>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation p, UserPermissions userPermissions) {
        if (p.getArgs().length <= 1) {
            MessageChannel ch = p.getMessage().getTextChannel();

            if (p.getArgs().length == 0) {
                RubiconBot.getMySQL().updateGuildValue(p.getMessage().getGuild(), "prefix", "rc!");
                EmbedBuilder builder = new EmbedBuilder();
                builder.setAuthor("Prefix updated", null, p.getMessage().getGuild().getIconUrl());
                builder.setDescription(":white_check_mark: Successfully changed prefix to `rc!`");
                ch.sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            } else {
                RubiconBot.getMySQL().updateGuildValue(p.getMessage().getGuild(), "prefix", p.getArgs()[0]);
                EmbedBuilder builder = new EmbedBuilder();
                builder.setAuthor("Prefix updated", null, p.getMessage().getGuild().getIconUrl());
                builder.setDescription(":white_check_mark: Successfully changed prefix to `" + p.getArgs()[0] + "`");
                ch.sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            }
        } else {
            return createHelpMessage();
        }

        return null;
    }
}
