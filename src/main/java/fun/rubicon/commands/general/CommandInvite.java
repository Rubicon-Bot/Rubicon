package fun.rubicon.commands.general;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.DiscordCore;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Amme JDA BOT
 * <p>
 * By LordLee at 18.11.2017 19:25
 * <p>
 * Contributors for this class:
 * - github.com/zekrotja
 * - github.com/DRSchlaubi
 * <p>
 * © Coders Place 2017
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
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
