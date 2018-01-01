package fun.rubicon.command;

import fun.rubicon.RubiconBot;
import fun.rubicon.command2.CommandHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.command
 */
@Deprecated
public class CommandHandler {

    /**
     * Find a Command based on a major command or alias.
     * @param command the major command or alias to be searched with.
     * @return the {@link Command} associated with the command parameter or null if no Command was added with this
     * alias. {@link fun.rubicon.command2.CommandHandler}s from the {@link fun.rubicon.command2} package can not be
     * returned.
     * @deprecated Use classes from the {@link fun.rubicon.command2} package instead.
     */
    @Deprecated
    public static Command getCommandFromName(String command) {
        fun.rubicon.command2.CommandHandler api2Handler = RubiconBot.getCommandManager().getCommandHandler(command);
        if(api2Handler instanceof CommandHandlerAdapter)
            return ((CommandHandlerAdapter) api2Handler).getOldCommand();
        return null;
    }

    /**
     * Registers the given command in the event pipe.
     * @param command the command to be registered.
     * @deprecated Use classes from the {@link fun.rubicon.command2} package instead.
     */
    @Deprecated
    public static void addCommand(Command command) {
        RubiconBot.getCommandManager().registerCommandHandler(new CommandHandlerAdapter(command));
    }

    /**
     * @return the command-invocation map.
     * @deprecated Only includes {@link Command}s. Use
     * {@link fun.rubicon.command2.CommandManager}.getCommandAssociations() instead.
     */
    @Deprecated
    public static HashMap<String, Command> getCommands() {
        Map<String, fun.rubicon.command2.CommandHandler> associationMap = RubiconBot.getCommandManager().getCommandAssociations();
        HashMap<String, Command> commandMap = new HashMap<>();

        for(Map.Entry<String, fun.rubicon.command2.CommandHandler> entry : associationMap.entrySet())
            if(entry.getValue() instanceof CommandHandlerAdapter // only old commands
                    && entry.getKey().equals(entry.getValue().getInvokeAliases()[0].toLowerCase())) // only main alias
                commandMap.put(entry.getKey(), ((CommandHandlerAdapter) entry.getValue()).getOldCommand());
        return commandMap;
    }
}
