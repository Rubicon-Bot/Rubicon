package fun.rubicon.commands.guildowner;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.Main;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.guildowner
 */
public class CommandRebuild extends Command {
    public CommandRebuild(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        Category category;
        TextChannel logChannel;
        TextChannel commandChannel = null; //TODO unused -> remove?
        TextChannel channel;
        try {
            category = e.getGuild().getCategoriesByName(Info.BOT_NAME, true).get(0);
            channel = e.getGuild().getTextChannelsByName("r-messages", true).get(0);
            logChannel = e.getGuild().getTextChannelsByName("r-log", true).get(0);
            commandChannel = e.getGuild().getTextChannelsByName("r-commands", true).get(0);
        } catch (Exception ex) {
            e.getGuild().getController().createCategory(Info.BOT_NAME).complete();
            category = e.getGuild().getCategoriesByName(Info.BOT_NAME, true).get(0);
            channel = (TextChannel) e.getGuild().getController().createTextChannel("r-messages").setParent(category).complete();
            logChannel = (TextChannel) e.getGuild().getController().createTextChannel("r-log").setParent(category).complete();
            commandChannel = (TextChannel) e.getGuild().getController().createTextChannel("r-commands").setParent(category).complete();
        }
        Main.getMySQL().updateGuildValue(e.getGuild(), "logchannel", logChannel.getId());
        Main.getMySQL().updateGuildValue(e.getGuild(), "channel", channel.getId());
        sendEmbededMessage("Rubicon Channels rebuilded!");
    }

    @Override
    public String getDescription() {
        return "Starts the bot on a guild, if the category gets deleted or something got fucked up!";
    }

    @Override
    public String getUsage() {
        return "startup";
    }

    @Override
    public int getPermissionLevel() {
        return 3;
    }
}
