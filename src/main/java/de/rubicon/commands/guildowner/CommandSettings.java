package de.rubicon.commands.guildowner;

import de.rubicon.command.Command;
import de.rubicon.command.CommandCategory;
import de.rubicon.util.SQL;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

/**
 * Amme JDA BOT
 * <p>
 * By LordLee at 17.11.2017 20:29
 * <p>
 * Contributors for this class:
 * - github.com/zekrotja
 * - github.com/DRSchlaubi
 * <p>
 * © Coders Place 2017
 */
public class CommandSettings extends Command {
    public CommandSettings(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {

        Guild guild = e.getGuild();
        MessageChannel channel = e.getTextChannel();
        //Permission here
        if (args.length < 2) e.getChannel().sendMessage(getUsage());
        String joinmessage = SQL.getValue(guild, "joinmsg");
        switch (args[0].toLowerCase()) {
            case "prefix":
                if (args.length < 2) {
                    e.getChannel().sendMessage(getUsage());
                    return;
                }
                SQL.updateValue(guild, "prefix", args[1]);
                sendEmbededMessage(":white_check_mark: Succesfully set the Prefix!");
                break;
            case "logchannel":
                if (args.length < 2) {
                    e.getChannel().sendMessage(getUsage());
                    return;
                }
                String txt = e.getMessage().getMentionedChannels().get(0).getId();
                SQL.updateValue(guild, "logchannel", txt);
                sendEmbededMessage(":white_check_mark: Succesfully set the LogChannel!");
                break;
            case "joinmessage":
                if (args.length < 2) {
                    e.getChannel().sendMessage(getUsage() + "\n(watch for large and lower case\n)");
                    return;
                }
                String temp = "";
                for(int i = 1; i < args.length; i++){
                    temp += " " + args[i];
                }
                SQL.updateValue(guild, "joinmsg", temp.replaceFirst("null ", ""));
                String up = SQL.getValue(guild, "joinmsg");
                sendEmbededMessage(":white_check_mark:  Successfully set joinmessage to `" + up + "`!");
                break;
            case "channel":
                if (args.length < 2) {
                    e.getChannel().sendMessage(getUsage() + "\n(watch for large and lower case\n)");
                    return;
                }
                String ch = e.getMessage().getMentionedChannels().get(0).getId();
                SQL.updateValue(guild, "channel", ch);
                sendEmbededMessage(":white_check_mark: Succesfully set the Joinmessagechannel!");
                break;
        }
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
