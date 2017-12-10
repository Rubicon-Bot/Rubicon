/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.fun;

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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CommandRip extends CommandHandler {

    public CommandRip() {
        super(new String[]{"rip", "tombstone"}, CommandCategory.FUN, new PermissionRequirements(0, "command.rip"), "Creates a tombstone for you", "rip <name> <text>");
    }


    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.args.length > 1) {
            Message message = parsedCommandInvocation.invocationMessage.getTextChannel().sendMessage(new EmbedBuilder().setColor(Colors.COLOR_SECONDARY).setDescription("Generating tombstone ...").build()).complete();
            StringBuilder query = new StringBuilder();
            for (int i = 1; i < parsedCommandInvocation.args.length; i++) {
                query.append(parsedCommandInvocation.args[i]).append(" ");
            }
            List<String> lines = new ArrayList<>();
            int index = 0;
            while (index < query.length()) {
                lines.add(query.substring(index, Math.min(index + 25, query.length())));
                index += 25;
            }
            InputStream image = null;
            try {
                if (query.length() > 25) {
                    image = new URL("http://www.tombstonebuilder.com/generate.php?top1=R.I.P.&top2=" + parsedCommandInvocation.args[0].replace(" ", "%20").replace("@", "") + "&top3=" + lines.get(0).replace(" ", "%20") + "&top4=" + lines.get(1).replace(" ", "%20") + "&sp=").openStream();
                } else {
                    image = new URL("http://www.tombstonebuilder.com/generate.php?top1=R.I.P.&top2=" + parsedCommandInvocation.args[0].replace(" ", "%20").replace("@", "") + "&top3=" + lines.get(0).replace(" ", "%20") + "&top4=&sp=").openStream();
                }

            } catch (IOException e1) {
                e1.printStackTrace();
                //TODO error handling. image can not be sent if it does not exist.
            }
            message.delete().queue();
            parsedCommandInvocation.invocationMessage.getTextChannel().sendFile(image, "rip.png", null).queue();
        } else {
            return new MessageBuilder().setEmbed(EmbedUtil.error("", getUsage()).build()).build();
        }
        return null;
    }
}
