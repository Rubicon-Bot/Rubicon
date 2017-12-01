package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.core.DiscordCore;
import fun.rubicon.core.Main;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.MySQL;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package commands.bowowner
 */

public class CommandRestart extends CommandHandler{
    public CommandRestart() {
        super(new String[]{"rs", "restart", "r"},CommandCategory.BOT_OWNER,new PermissionRequirements(4,"command.restart"),"Restart the Bot!","restart");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        MySQL sql = RubiconBot.getMySQL();
        sql.disconnect();
        Message msg = parsedCommandInvocation.invocationMessage.getTextChannel().sendMessage("Restarting :robot:").complete();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
                RubiconBot.getJDA().shutdown();
            }
        }, 20000);
        RubiconBot.initJDA();
        sql.connect();
        return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription(":battery: Restarted :battery:").build()).build();
    }
}
