package fun.rubicon.commands.tools;


import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;

import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * Rubicon Discord bot
 *
 * @author Moritz Jahn / ForMoJa
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package commands.tools
 */

public class CommandASCII extends CommandHandler {


    public CommandASCII() {
        super(new String[]{"ascii"}, CommandCategory.TOOLS, new PermissionRequirements(0, "command.ascii"), "Convert an ASCII-Code to a char and a char to an ASCII-Code.", "ascii <string>\nascii code <ASCII-Code>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissionse) {
        String[] args = parsedCommandInvocation.args;
        Message message = parsedCommandInvocation.invocationMessage;
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
                        return new MessageBuilder().setEmbed(EmbedUtil.error("Invalid value", "One of you numbers has a higher value than 127. But this is the highest ASCII-Value.").build()).build();
                    }
                } catch (NumberFormatException ex) {
                    return new MessageBuilder().setEmbed(EmbedUtil.error("Invalid value", "You have to give me numbers").build()).build();
                }
            }
            output.append(asciiBuilder.toString()).append("`\n");
            output.append(text.toString()).append("`");
            return new MessageBuilder().setEmbed(EmbedUtil.embed("Output", output.toString()).build()).build();
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
            EmbedBuilder builder = new EmbedBuilder()
                    .setDescription(output.toString())
                    .setColor(Colors.COLOR_PRIMARY);
            return new MessageBuilder().setEmbed(builder.build()).build();
        } else
            return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "ascii <string>\nascii code <ASCII-Code>").build()).build();
    }


}
