package fun.rubicon.commands.botowner;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.ImageEditor;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandTest extends CommandHandler {

    public CommandTest() {
        super(new String[]{"test"}, CommandCategory.BOT_OWNER, new PermissionRequirements("test", false, false), "Test Command", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) throws Exception {
        BufferedImage image = ImageIO.read(new URL("https://lordlee.de/pexels-photo.jpg").openStream());
        ImageEditor imageEditor = new ImageEditor(image);
        Font font = new Font("DejaVu Sans", Font.BOLD, 100);
        String name = invocation.getAuthor().getName();
        imageEditor.drawTextCentered( 525, font, ((name.length() > 10) ? name.substring(0, 10) + "\n" + name.substring(10, name.length() - 1) : name));
        URL avatarCDNUrl = new URL(invocation.getAuthor().getAvatarUrl());
        URLConnection avatarCDN = avatarCDNUrl.openConnection();
        avatarCDN.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        BufferedImage avatarImage = ImageIO.read(avatarCDN.getInputStream());
        imageEditor.drawRoundImageCentered(100, 300, 300, avatarImage);
        invocation.getTextChannel().sendFile(imageEditor.getInputStream(), "join.jpg").queue();
        return null;
    }
}
