package fun.rubicon.commands.settings;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandJoinImage extends CommandHandler {

    public CommandJoinImage() {
        super(new String[]{"joinimage", "jimage", "joinimages"}, CommandCategory.SETTINGS, new PermissionRequirements("joinimage", false, false), "Sends a nice image with the avatar and the name of a joined user.", "<disable>\n<enable> <#channel>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(invocation.getGuild());
        if (invocation.getArgs().length == 0)
            return createHelpMessage();
        String command = invocation.getArgs()[0];
        if (command.equalsIgnoreCase("disable")) {
            if (!rubiconGuild.hasJoinImagesEnabled())
                return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.ji.d.ne.t"), invocation.translate("command.ji.d.ne.d")));
            rubiconGuild.disableJoinImages();
            return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.ji.d.t"), invocation.translate("command.ji.d.d")));
        } else if (command.equalsIgnoreCase("enable")) {
            if (invocation.getArgs().length != 2)
                return createHelpMessage();
            if (invocation.getMessage().getMentionedChannels().size() == 0)
                return createHelpMessage();
            if (rubiconGuild.hasJoinImagesEnabled())
                rubiconGuild.disableJoinImages();
            TextChannel channel = invocation.getMessage().getMentionedChannels().get(0);
            rubiconGuild.enableJoinImages(channel.getId());
            return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.ji.t"), invocation.translate("command.ji.d")));
        }
        return createHelpMessage();
    }
}