package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.core.Main;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package commands.botowner
 */

public class CommandStop extends CommandHandler {

    public CommandStop() {
        super(new String[]{"stop"}, CommandCategory.BOT_OWNER, new PermissionRequirements(4,"command.stop"),"Stops the bot.","");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (RubiconBot.getMySQL() != null) {
            RubiconBot.getMySQL().disconnect();
        }
        parsedCommandInvocation.invocationMessage.getTextChannel().sendMessage(new EmbedBuilder().setDescription(":battery: System Shutdown :battery:").build()).queue();
        System.exit(0);
        return null;
    }


}
