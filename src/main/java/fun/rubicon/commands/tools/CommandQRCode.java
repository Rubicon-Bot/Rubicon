/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class CommandQRCode extends CommandHandler {
    public CommandQRCode() {
        super(new String[]{"qrcode", "qr", "code"}, CommandCategory.TOOLS, new PermissionRequirements("command.qr", false, true), "Easily generate a QR code", "<text>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.getArgs();
        Message message = parsedCommandInvocation.getMessage();
        Message mymsg = message.getTextChannel().sendMessage(EmbedUtil.info("Generating", "Generating QR cde").build()).complete();
        if (args.length > 0) {
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                text.append(args[i]).append(" ");
            }
            try {
                InputStream code = new URL("https://api.qrserver.com/v1/create-qr-code/?data=" + text.toString().replace(" ", "+") + "&size=220x220&margin=0").openStream();
                mymsg.delete().queue();
                message.getTextChannel().sendFile(code, "qrcode.png", null).queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "qr <text>").build()).build();
        }
        return null;
    }
}
