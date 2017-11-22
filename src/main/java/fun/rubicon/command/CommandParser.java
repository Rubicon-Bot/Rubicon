package fun.rubicon.command;

import fun.rubicon.core.Main;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Parses command arguments from raw messages.
 * @author LeeDJD, ForYaSee, tr808axm
 */
public class CommandParser {

    /**
     * Parses a raw message into command components. Automatically looks up the prefix.
     * @param raw the raw message to pass.
     * @param event the event that invoked this command.
     * @return a CommandContainer with the parsed arguments.
     */
    public static CommandContainer parse(String raw, MessageReceivedEvent event) {
        return parse(raw, Main.getMySQL().getGuildValue(event.getGuild(), "prefix"), event);
    }

    /**
     * Parses a raw message into command components. Always uses the default prefix.
     * @param raw the raw message to pass.
     * @param event the event that invoked this command.
     * @return a CommandContainer with the parsed arguments.
     * @deprecated Use automatic lookup instead.
     */
    @Deprecated
    public static CommandContainer parsep(String raw, MessageReceivedEvent event) {
        return parse(raw, Info.BOT_DEFAULT_PREFIX, event);
    }

    /**
     * Parses a raw message into command components.
     * @param rawMessage the raw message to parse.
     * @param prefix the prefix used in this context.
     * @param event the event that invoked this command.
     * @return a CommandContainer with the parsed arguments.
     */
    private static CommandContainer parse(String rawMessage, String prefix, MessageReceivedEvent event) {
        // cut off command prefix
        String beheaded = rawMessage.substring(prefix.length(), rawMessage.length());
        // split arguments
        String[] allArgs = beheaded.split(" ");
        // extract first argument as invoker
        String invokerArg = allArgs[0];
        // create an array of the actual command arguments
        String[] args = new String[allArgs.length - 1];
        System.arraycopy(allArgs, 1, args, 0, args.length);

        return new CommandContainer(rawMessage, beheaded, allArgs, invokerArg, args, event);
    }

    /**
     * Contains split command arguments.
     */
    public static class CommandContainer {
        /**
         * Raw message.
         */
        public final String raw;
        /**
         * Command without the prefix.
         */
        public final String beheaded;
        /**
         * All arguments including invoke and args.
         */
        public final String[] splitBeheaded;
        /**
         * The command invoker argument ('command name'). Equal to splitBeheaded[0].
         */
        public final String invoke;
        /**
         * Actual command arguments. Equal to splitBeheaded[1 to end].
         */
        public final String[] args;
        /**
         * Event that invoked the command.
         */
        public final MessageReceivedEvent event;

        /**
         * Data container. Constructor only available for CommandParser methods.
         * @param raw Raw message.
         * @param beheaded Command without the prefix.
         * @param splitBeheaded All arguments including invoke and args.
         * @param invoke The command invoker argument ('command name'). Equal to splitBeheaded[0].
         * @param args Actual command arguments. Equal to splitBeheaded[1 to end].
         * @param event Event that invoked the command.
         */
        private CommandContainer(String raw, String beheaded, String[] splitBeheaded, String invoke, String[] args, MessageReceivedEvent event) {
            this.raw = raw;
            this.beheaded = beheaded;
            this.splitBeheaded = splitBeheaded;
            this.invoke = invoke;
            this.args = args;
            this.event = event;
        }
    }
}