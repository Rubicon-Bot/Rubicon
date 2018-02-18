package fun.rubicon.commands.settings;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2018
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.settings
 */
public class CommandLevelMessage extends CommandHandler {
    public CommandLevelMessage() {
        super(new String[]{"levelupmessage", "lvlmsg", "lvlmessage"}, CommandCategory.SETTINGS, new PermissionRequirements("command.lvlmessage", false, false), "Toggles the LevelUp Message", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        int temp = Integer.parseInt(RubiconBot.getMySQL().getGuildValue(parsedCommandInvocation.getGuild(), "lvlmsg"));
        if (temp == 0) {
            RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.getGuild(), "lvlmsg", "1");
            SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), EmbedUtil.success("Toggled LevelUp", "Successfully activated the LevelUp Notifications!").build());
        } else if (temp == 1) {
            RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.getGuild(), "lvlmsg", "0");
            SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), EmbedUtil.success("Toggled LevelUp", "Successfully deactivated the LevelUp Notifications!").setColor(Color.CYAN).build());
        }
        return null;
    }
}
