package fun.rubicon.commands.settings;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.entities.Message;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2018
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.settings
 */
public class CommandLevelMessage extends CommandHandler{
    public CommandLevelMessage() {
        super(new String[]{"levelupmessage","lvlmsg","lvlmessage"},CommandCategory.SETTINGS,new PermissionRequirements(PermissionLevel.WITH_PERMISSION,"command.lvlmessage"),"Toggles the LevelUp Message","");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        int temp = Integer.parseInt(RubiconBot.getMySQL().getGuildValue(parsedCommandInvocation.getGuild(),"lvlmsg"));
        if (temp == 0){
            RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.getGuild(),"lvlmsg","1");
            return SafeMessage.sendMessageBlocking(parsedCommandInvocation.getTextChannel(),"Successfully activated the LevelUp Notifications!");
        }else if(temp == 1){
            RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.getGuild(),"lvlmsg","0");
            return SafeMessage.sendMessageBlocking(parsedCommandInvocation.getTextChannel(),"Successfully deactivated the LevelUp Notifications!");
        }
        return null;
    }
}
