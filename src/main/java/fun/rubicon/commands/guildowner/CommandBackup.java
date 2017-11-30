package fun.rubicon.commands.guildowner;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Configuration;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.guildowner
 */
public class CommandBackup extends Command {

    public CommandBackup(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        //TODO
        if(args.length != 1) {
            sendUsageMessage();
            return;
        }

        switch (args[0]) {
            case "save":
                break;
            case "load":
                break;
            case "preview":
                break;
        }
    }

    private void createBackup() {
        Guild g = e.getGuild();
        User owner = g.getOwner().getUser();
        Configuration config = getBackupConfig();
    }

    private Configuration getBackupConfig() {
        File backupFile = new File("data/guilds/" + e.getGuild().getId() + "/backup.json");
        if(!backupFile.exists()) {
            try {
                backupFile.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return new Configuration(backupFile);
    }

    @Override
    public String getDescription() {
        return "Backup your complete guild.";
    }

    @Override
    public String getUsage() {
        return "backup <save/load/preview>";
    }

    @Override
    public int getPermissionLevel() {
        return 3;
    }
}
