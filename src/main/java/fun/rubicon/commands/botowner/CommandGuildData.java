package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandGuildData extends CommandHandler {

    public CommandGuildData() {
        super(new String[]{"guild-data"}, CommandCategory.BOT_OWNER, new PermissionRequirements("command.guilddata", true, false), "Saves guild data in a file. Only works on RubiconDev Server.", "");
    }

    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.getGuild().getIdLong() != 380415148545802250L)
            return null;
        new Thread(() -> {
            StringBuilder builder = new StringBuilder();
            for (Guild guild : RubiconBot.getJDA().getGuilds()) {
                builder.append(guild.getName() + "(" + guild.getId() + ") [" + guild.getMembers().size() + "]\n");
            }
            File tempFile = new File("data/temp", "guilddata.txt");
            try {
                tempFile.createNewFile();
                FileWriter writer = new FileWriter(tempFile);
                writer.write(builder.toString());
                writer.flush();
                writer.close();
                parsedCommandInvocation.getTextChannel().sendFile(tempFile).complete().delete().queueAfter(20, TimeUnit.SECONDS);
                tempFile.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        return null;
    }
}
