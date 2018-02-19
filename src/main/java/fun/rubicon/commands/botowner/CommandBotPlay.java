package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Configuration;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2018
 * @license GPL-3.0 License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.botowner
 */
public class CommandBotPlay extends CommandHandler {

    public CommandBotPlay() {
        super(new String[]{"botplay","statusset"}, CommandCategory.BOT_OWNER, new PermissionRequirements("botplay", true, false), "", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Configuration configuration = RubiconBot.getConfiguration();
        String configKey = "playingStatus";
        if (!configuration.has(configKey)) {
            configuration.set(configKey, "0");
        }
        if (parsedCommandInvocation.getArgs().length == 0) {
            configuration.set(configKey, "0");
            return null;
        }
        StringBuilder message = new StringBuilder();
        for (String s : parsedCommandInvocation.getArgs())
            message.append(s).append(" ");

        RubiconBot.getConfiguration().set(configKey, message.toString());
        parsedCommandInvocation.getMessage().getJDA().getPresence().setGame(Game.playing(message.toString()));

        return new MessageBuilder().setEmbed(EmbedUtil.success("Status set!", "Successfully set the playing status!").build()).build();
    }
}