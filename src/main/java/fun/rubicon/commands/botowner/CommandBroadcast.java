package fun.rubicon.commands.botowner;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandBroadcast extends Command {
    public CommandBroadcast(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        String b_message = "";
        if(args.length<1)
        {
            sendUsageMessage();
            return;
        }

        for(int i = 0; i < args.length; i++){
            b_message += args[i].toString() + " ";
        }
        for (Guild g : e.getJDA().getGuilds() ) {
            PrivateChannel pc = g.getOwner().getUser().openPrivateChannel().complete();
            sendEmbededMessage(pc,"Message from Rubicon Dev-Team", Colors.COLOR_ERROR, b_message);
        }
    }

    @Override
    public String getDescription() {
        return "Inform all serverowner.";
    }

    @Override
    public String getUsage() {
        return "broadcast <message>";
    }

    @Override
    public int getPermissionLevel() {
        return 4;
    }
}
