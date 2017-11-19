package fun.rubicon.commands.botowner;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.Main;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package commands.botowner
 */

public class CommandStop extends Command {

    public CommandStop(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        //TODO Saving Stuff?
        Main.getMySQL().disconnect();
        sendEmbededMessage(":battery: System Shutdown :battery:");
        System.exit(0);
    }

    @Override
    public String getDescription() {
        return "Stops the bot.";
    }

    @Override
    public String getUsage() {
        return "stop";
    }

    @Override
    public int getPermissionLevel() {
        return 4;
    }
}
