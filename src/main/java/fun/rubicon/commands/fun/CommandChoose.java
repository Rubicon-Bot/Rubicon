package fun.rubicon.commands.fun;

/**
 * Rubicon Discord bot
 *
 * @author xEiisKeksx
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.command
 */

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.ThreadLocalRandom;

public class CommandChoose extends Command {
    public CommandChoose(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        int choosed_arg = 0;
        String answer = "";
        if(args.length<2)
        {
            sendUsageMessage();
            return;
        }
        choosed_arg = ThreadLocalRandom.current().nextInt(0, args.length);
        answer = args[choosed_arg];
        sendEmbededMessage("Rubicon choosed ```" + answer + "``` for you.");

    }

    @Override
    public String getDescription() {
        return "Chooses one of your options.";
    }

    @Override
    public String getUsage() {
        return "choose <option1> <option2> [option3] [...]";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
