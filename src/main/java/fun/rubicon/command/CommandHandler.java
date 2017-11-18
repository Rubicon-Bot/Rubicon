package fun.rubicon.command;

import java.util.HashMap;

public class CommandHandler {

    public static final CommandParser parser = new CommandParser();
    public static HashMap<String, Command> commands = new HashMap<String, Command>();

    public static void handleCommand(CommandParser.CommandContainer cmd) {

        if (commands.containsKey(cmd.invoke.toLowerCase().toLowerCase())) {
            commands.get(cmd.invoke.toLowerCase()).call(cmd.args, cmd.event);
            cmd.event.getChannel().sendTyping().queue();
        }
    }

    public static void addCommand(Command command) {
        commands.put(command.getCommand(), command);
    }

    public static HashMap<String, Command> getCommands() {
        return commands;
    }
}
