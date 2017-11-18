package fun.rubicon.commands.botowner;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.DiscordCore;
import fun.rubicon.core.Main;
import fun.rubicon.util.MySQL;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandRestart extends Command{
    public CommandRestart(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        MySQL sql = Main.getMySQL();
        sql.disconnect();
        sendEmbededMessage("Restarting :robot:");
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        DiscordCore.getJDA().shutdown();
        DiscordCore.start();
        sql.connect();
    }

    @Override
    public String getDescription() {
        return "Restarts the Bot and Reconnect the database";
    }

    @Override
    public String getUsage() {
        return "restart";
    }

    @Override
    public int getPermissionLevel() {
        return 4;
    }
}
