package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Rubicon Discord bot
 *
 * @author Michael Rittmeister / Schlaubi
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.tools
 */
public class CommandQRCode extends CommandHandler{
    public CommandQRCode() {
        super(new String[] {"qrcode", "qr", "code"}, CommandCategory.TOOLS, new PermissionRequirements(0, "command.qr"), "Easily generate a QR code", "<text>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.args;
        Message message = parsedCommandInvocation.invocationMessage;
        Message mymsg = message.getTextChannel().sendMessage(EmbedUtil.info("Generating", "Generating QR cde").build()).complete();
        if(args.length > 0){
            StringBuilder text = new StringBuilder();
            for(int i = 0; i < args.length; i++){
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
