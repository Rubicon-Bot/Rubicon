package fun.rubicon.commands.admin;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.features.portal.GuildPortalManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandPortal extends CommandHandler {

    public CommandPortal() {
        super(new String[]{"portal"}, CommandCategory.ADMIN, new PermissionRequirements("portal", false, false), "Creates a connection between two servers.", "create\n" +
                "invite <serverId>\n" +
                "accept <serverId>\n" +
                "close\n" +
                "invites <enable/disable>\n" +
                "embeds <enable/disable>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        GuildPortalManager portalManager = new GuildPortalManager(invocation.getGuild());
        if (invocation.getArgs().length == 0)
            return createHelpMessage();
        else if (invocation.getArgs().length == 1) {
            switch (invocation.getArgs()[0].toLowerCase()) {
                case "create":
                    break;
                case "info":
                    break;
                case "close":
                    break;
                default:
                    return createHelpMessage();
            }
        } else if(invocation.getArgs().length == 2) {
            String option = invocation.getArgs()[1];
            switch (invocation.getArgs()[0].toLowerCase()) {
                case "invite":
                    break;
                case "accept":
                    break;
                case "invites":
                    if(option.equalsIgnoreCase("enable")) {
                        portalManager.setInvites(true);
                        return message(success("Enabled Invites!", "You now can receive invites to join another portal."));
                    } else if(option.equalsIgnoreCase("disable")) {
                        portalManager.setInvites(false);
                        return message(success("Disabled Invites!", "You now are receiving no invites."));
                    } else
                        return createHelpMessage();
                case "embeds":
                    if(option.equalsIgnoreCase("enable")) {
                        portalManager.setEmbeds(true);
                        return message(success("Enabled Embeds!", "The messages will now be send as embeds."));
                    } else if(option.equalsIgnoreCase("disable")) {
                        portalManager.setEmbeds(false);
                        return message(success("Disabled Embeds!", "The messages will now be send as webhooks."));
                    } else
                        return createHelpMessage();
                    default:
                        return createHelpMessage();
            }
        }
        return createHelpMessage();
    }
}
