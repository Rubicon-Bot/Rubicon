package fun.rubicon.commands.botowner;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.botowner
 */
public class CommandCreateInvite extends CommandHandler {

    public CommandCreateInvite() {
        super(new String[]{"createinvite"}, CommandCategory.BOT_OWNER, new PermissionRequirements(PermissionLevel.BOT_AUTHOR, "command.createinvite"), "Creates an invite", "<guildid>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        try {
            return new MessageBuilder().append(parsedCommandInvocation.invocationMessage.getJDA().getGuildById(parsedCommandInvocation.args[0]).getTextChannels().get(0).createInvite().complete().getURL()).build();
        } catch (Exception ex) {
            Logger.info("Create Invite: " + ex.getMessage());
            return new MessageBuilder().setEmbed(EmbedUtil.error("Error!", "An error occurred!").build()).build();
        }
    }
}