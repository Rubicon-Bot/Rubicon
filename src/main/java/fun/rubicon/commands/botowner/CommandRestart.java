/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.sql.MySQL;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Handles the 'restart' command.
 *
 * @author Leon Kappes / Lee
 */
public class CommandRestart extends CommandHandler {
    public CommandRestart() {
        super(new String[]{"rs", "restart", "r"}, CommandCategory.BOT_OWNER, new PermissionRequirements("command.restart", true, false), "Restart the Bot!", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        MySQL sql = RubiconBot.getMySQL();
        sql.disconnect();
        Message msg = parsedCommandInvocation.getMessage().getTextChannel().sendMessage("Restarting :robot:").complete();
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
