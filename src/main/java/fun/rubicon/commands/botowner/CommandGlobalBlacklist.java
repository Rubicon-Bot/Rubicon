package fun.rubicon.commands.botowner;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.GlobalBlacklist;
import net.dv8tion.jda.core.entities.Message;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.botowner
 */
public class CommandGlobalBlacklist extends CommandHandler {

    public CommandGlobalBlacklist() {
        super(new String[]{"globalblacklist", "gbl"}, CommandCategory.BOT_OWNER, new PermissionRequirements(PermissionLevel.BOT_AUTHOR, "cmd.globalblacklist"), "Ban a user from RubiconBot.", "<@User>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.args.length == 2) {
            if (parsedCommandInvocation.invocationMessage.getMentionedUsers().size() == 1) {
                switch (parsedCommandInvocation.args[0]) {
                    case "add":
                        GlobalBlacklist.addToBlacklist(parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0));
                        return EmbedUtil.message(EmbedUtil.success("Success!", "Successfuly added " + parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0).getName() + " to global blacklist."));
                    case "remove":
                        GlobalBlacklist.removeFromBlacklist(parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0));
                        return EmbedUtil.message(EmbedUtil.success("Success!", "Successfuly removed " + parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0).getName() + " from the global blacklist."));
                }
            }
        }
        return createHelpMessage();
    }
}
