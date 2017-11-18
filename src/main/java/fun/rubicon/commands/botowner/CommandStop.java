package fun.rubicon.commands.botowner;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
<<<<<<< HEAD
import fun.rubicon.core.DiscordCore;
=======
import fun.rubicon.core.Main;
>>>>>>> master
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandStop extends Command {

    public CommandStop(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        //TODO Saving Stuff?
        Main.getMySQL().disconnect();
        sendEmbededMessage(":battery: System Shutdown :battery:");
        System.exit(0);
    }

    @Override
    public String getDescription() {
        return "Stops the bot";
    }

    @Override
    public String getUsage() {
        return "stop";
    }

    @Override
    public int getPermissionLevel() {
        return 4;
    }
}
