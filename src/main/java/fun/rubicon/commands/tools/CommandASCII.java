/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.tools;


import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;


public class CommandASCII extends CommandHandler {

    public CommandASCII() {
        super(new String[]{"ascii"}, CommandCategory.TOOLS, new PermissionRequirements(PermissionLevel.EVERYONE, "command.ascii"), "Convert an ASCII-Code to a char and a char to an ASCII-Code.", "<string>\ncode <ASCII-Code>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.args.length >= 2 && parsedCommandInvocation.args[0].equalsIgnoreCase("code")) {
            StringBuilder output = new StringBuilder();
            StringBuilder asciiBuilder = new StringBuilder("ASCII-Code: `");
            StringBuilder text = new StringBuilder("Text: `");
            for (int i = 1; i < parsedCommandInvocation.args.length; i++) {
                asciiBuilder.append(parsedCommandInvocation.args[i]).append(" ");
                try {
                    if (Integer.valueOf(parsedCommandInvocation.args[i]) <= 127)
                        text.append((char) ((int) Integer.valueOf(parsedCommandInvocation.args[i])));
                    else {
                        return new MessageBuilder().setEmbed(EmbedUtil.error("", "One of you numbers has a higher value than 127. But this is the highest ASCII-Value.").build()).build();
                    }
                } catch (NumberFormatException ex) {
                    return new MessageBuilder().setEmbed(EmbedUtil.error("", "You have to give me numbers!").build()).build();
                }
            }
            output.append(asciiBuilder.toString()).append("`\n");
            output.append(text.toString()).append("`");
            return new MessageBuilder().setEmbed(EmbedUtil.embed("", output.toString()).build()).build();
        } else if (parsedCommandInvocation.args.length > 0) {
            StringBuilder output = new StringBuilder();
            StringBuilder text = new StringBuilder("Text: `");
            StringBuilder asciiBuilder = new StringBuilder("ASCII-Code: `");
            for (String arg : parsedCommandInvocation.args) {
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
            parsedCommandInvocation.invocationMessage.getTextChannel().sendMessage(builder.build()).queue();
        } else
            return new MessageBuilder().setEmbed(EmbedUtil.error("", getParameterUsage()).build()).build();
        return null;
    }
}
