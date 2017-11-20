package fun.rubicon.command;

import fun.rubicon.util.ChannelLog;
import fun.rubicon.util.Logger;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.command
 */

public class CommandHandler {

    public static final CommandParser parser = new CommandParser();
    public static HashMap<String, Command> commands = new HashMap<String, Command>();
    public static HashMap<String, Command> aliases = new HashMap<>();

    public static void handleCommand(CommandParser.CommandContainer cmd) throws ParseException {

        if (commands.containsKey(cmd.invoke.toLowerCase())) {
            commands.get(cmd.invoke.toLowerCase()).call(cmd.args, cmd.event);
            ChannelLog.logCommand(cmd.invoke.toString(), cmd.event);
        } else if(aliases.containsKey(cmd.invoke.toLowerCase())){
            aliases.get(cmd.invoke.toLowerCase()).call(cmd.args, cmd.event);
            ChannelLog.logCommand(cmd.invoke.toString(), cmd.event);
        }
    }

    public static Command getCommandFromName(String command) {
        try {
            return commands.get(command);
        } catch (NullPointerException ex) {

        }
        return null;
    }

    public static void addCommand(Command command) {
        if(command.getAliases() != null) {
            for(String s : command.getAliases()) {
                aliases.put(s, command);
            }
        }
        commands.put(command.getCommand(), command);
    }

    public static HashMap<String, Command> getCommands() {
        return commands;
    }
}
