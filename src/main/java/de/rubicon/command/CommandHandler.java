package de.rubicon.command;

import java.util.HashMap;

public class CommandHandler {

    public static final CommandParser parser = new CommandParser();
    private static HashMap<String, Command> commands = new HashMap<String, Command>();

    public static void handleCommand(CommandParser.CommandContainer cmd) {

        if (commands.containsKey(cmd.invoke.toLowerCase())) {
            commands.get(cmd.invoke).call(cmd.args, cmd.event);
        }
    }

    public static void addCommand(Command command) {
        commands.put(command.getCommand(), command);
    }

    public static HashMap<String, Command> getCommands() {
        return commands;
    }
}
