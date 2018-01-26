/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Configuration;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;

public class CommandPlay extends CommandHandler {

    private String configKey = "playingStatus";

    public CommandPlay() {
        super(new String[]{"botplay"}, CommandCategory.BOT_OWNER, new PermissionRequirements(PermissionLevel.BOT_AUTHOR, "command.botplay"), "Change bot's playing status.", "<text>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Configuration configuration = RubiconBot.getConfiguration();
        if(!configuration.has(configKey)) {
            configuration.set(configKey, "0");
        }
        if(parsedCommandInvocation.args.length == 0) {
            configuration.set(configKey, "0");
            return null;
        }
        String message = "";
        for (String s : parsedCommandInvocation.args)
            message += s + " ";

        RubiconBot.getConfiguration().set(configKey, message);
        parsedCommandInvocation.getMessage().getJDA().getPresence().setGame(Game.playing(message));

        return new MessageBuilder().setEmbed(EmbedUtil.success("Status set!", "Successfully set the playing status!").build()).build();
    }
}
