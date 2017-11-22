package fun.rubicon.commands.general;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.general
 */

public class CommandInvite extends Command{
    public CommandInvite(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        sendEmbededMessage(e.getTextChannel(), Info.BOT_NAME + " - Invites", Colors.COLOR_SECONDARY, "[Invite Rubicon Bot](https://discordapp.com/oauth2/authorize?client_id=380713705073147915&scope=bot&permissions=2146958591)\n" +
                "[Join Rubicon Server](https://discord.gg/UrHvXY9)");
    }

    @Override
    public String getDescription() {
        return "Gives you the invite-link of the bot.";
    }

    @Override
    public String getUsage() {
        return "invite";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
