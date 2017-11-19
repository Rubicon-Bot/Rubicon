package fun.rubicon.commands.guildowner;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.Main;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package commands.guildowner
 */
public class CommandRebuild extends Command{
    public CommandRebuild(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        Guild guild = e.getGuild();
        GuildController controller = guild.getController();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                controller.createCategory(Info.BOT_NAME).queue(cat -> {

                    controller.modifyCategoryPositions()
                            .selectPosition(cat.getPosition())
                            .moveTo(0).queue();

                    String[] list = {"commands", "log"};

                    Arrays.stream(list).forEach(s ->
                            controller.createTextChannel(s).queue(chan -> chan.getManager().setParent((Category) cat).queue())
                    );
                });

            }
        }, 1000);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Main.getMySQL().updateGuildValue(guild, "logchannel", e.getGuild().getTextChannelsByName("log", true).get(0).getId());
            }
        }, 3000);
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
