package fun.rubicon.commands.tools;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Info;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Rubicon Discord bot
 *
 * @author Moritz Jahn / ForMoJa
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package commands.tools
 */

public class CommandASCII extends Command {

    public CommandASCII(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        if (args.length >= 2 && args[0].equalsIgnoreCase("code")) {
            StringBuilder output = new StringBuilder();
            StringBuilder asciiBuilder = new StringBuilder("ASCII-Code: `");
            StringBuilder text = new StringBuilder("Text: `");
            for (int i = 1; i < args.length; i++) {
                asciiBuilder.append(args[i]).append(" ");
                try {
                    if (Integer.valueOf(args[i]) <= 127)
                        text.append((char) ((int) Integer.valueOf(args[i])));
                    else {
                        sendErrorMessage("One of you numbers has a higher value than 127. But this is the highest ASCII-Value.");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    sendErrorMessage("You have to give me numbers!");
                    return;
                }
            }
            output.append(asciiBuilder.toString()).append("`\n");
            output.append(text.toString()).append("`");
            sendEmbededMessage(output.toString());
        } else if (args.length > 0) {
            StringBuilder output = new StringBuilder();
            StringBuilder text = new StringBuilder("Text: `");
            StringBuilder asciiBuilder = new StringBuilder("ASCII-Code: `");
            for (String arg : args) {
                text.append(arg).append(" ");
                char[] chars = arg.toCharArray();
                for (char c : chars) {
                    String ascii = Integer.toString((int) c);
                    if (ascii.length() == 1)
                        asciiBuilder.append("00");
                    else if (ascii.length() == 2)
                        asciiBuilder.append("0");
                    asciiBuilder.append(ascii).append(" ");
                }
                asciiBuilder.append("023 ");
            }
            output.append(text.toString()).append("`\n");
            output.append(asciiBuilder.toString()).append("`");
            sendEmbededMessage(output.toString());
        } else
            sendUsageMessage();
    }

    @Override
    public String getDescription() {
        return "Convert an ASCII-Code to a char and a char to an ASCII-Code.";
    }

    @Override
    public String getUsage() {
        return Info.BOT_DEFAULT_PREFIX + "ascii <string>\n" + Info.BOT_DEFAULT_PREFIX + "ascii code <ASCII-Code>";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
