package fun.rubicon.commands.admin;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandPortal extends CommandHandler {

    public CommandPortal() {
        super(new String[]{"portal"}, CommandCategory.ADMIN, new PermissionRequirements("portal", false, false), "Create a portal and talk with users of other servers.",
                "create [serverId] | Opens a new portal\n" +
                        "close | Closes your portal\n" +
                        "info | Shows info about the current portal\n" +
                        "settings | Customize the portal mechanic\n" +
                        "kick <serverId> | Votekicks a server out of the portal.");
    }

    private final String portalChannelName = "portal";

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        RubiconUser rubiconUser = RubiconUser.fromUser(invocation.getAuthor());
        if(invocation.getArgs().length == 0)
            return createHelpMessage();
        if(invocation.getArgs().length == 1) {
            if(invocation.getArgs()[0].equalsIgnoreCase("info")) {
                //TODO SEND INFO
            } else if (invocation.getArgs()[0].equalsIgnoreCase("create")) {
                //TODO CREATE PORTAL WITH RANDOM GUILD
            }
        }

        switch (invocation.getArgs()[0]) {
            case "create":
                break;
            case "close":
                break;
            case "settings":
                break;
            case "kick":
                break;
        }
        return null;
    }

    public void createRandomPortal(CommandManager.ParsedCommandInvocation invocation) {

    }

    public void createPortal(CommandManager.ParsedCommandInvocation invocation, long serverId) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(invocation.getGuild());
        if(rubiconGuild.portalCreated()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.info("Portal already created!", "You can't create two portals at once.").build(), 60);
            return;
        }

    }
}
