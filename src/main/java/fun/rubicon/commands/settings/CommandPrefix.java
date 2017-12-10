/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.settings;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.concurrent.TimeUnit;

public class CommandPrefix extends CommandHandler{
    public CommandPrefix() {
        super(new String[]{"prefix", "pr"}, CommandCategory.SETTINGS,
                new PermissionRequirements(2, "command.prefix"),
                "Set the Server Prefix!", "prefix <prefix>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation p, UserPermissions userPermissions) {
        if(p.args.length <= 1) {
            MessageChannel ch = p.invocationMessage.getTextChannel();

            if(p.args.length == 0) {
                RubiconBot.getMySQL().updateGuildValue(p.invocationMessage.getGuild(), "prefix", "rc!");
                EmbedBuilder builder = new EmbedBuilder();
                builder.setAuthor("Prefix updated", null, p.invocationMessage.getGuild().getIconUrl());
                builder.setDescription(":white_check_mark: Successfully changed prefix to `rc!`");
                ch.sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            } else {
                RubiconBot.getMySQL().updateGuildValue(p.invocationMessage.getGuild(), "prefix", p.args[0]);
                EmbedBuilder builder = new EmbedBuilder();
                builder.setAuthor("Prefix updated", null, p.invocationMessage.getGuild().getIconUrl());
                builder.setDescription(":white_check_mark: Successfully changed prefix to `" + p.args[0] + "`");
                ch.sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            }
        } else {
            return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription(getUsage()).build()).build();
        }

        return null;
    }
}
