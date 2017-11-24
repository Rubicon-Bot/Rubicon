package fun.rubicon.commands.fun;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Rubicon Discord bot
 *
 * @author Michael Rittmeister / Schlaubi
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.fun
 */
public class CommandRip extends Command{

    public CommandRip(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        if(args.length > 1){
            Message message = e.getTextChannel().sendMessage(new EmbedBuilder().setColor(Colors.COLOR_SECONDARY).setDescription("Generating tombstone ...").build()).complete();
            StringBuilder query = new StringBuilder();
            for(int i = 1;i < args.length; i++){
                query.append(args[i]).append(" ");
            }
            List<String> lines = new ArrayList<>();
            int index = 0;
            while (index < query.length()) {
                lines.add(query.substring(index, Math.min(index + 25,query.length())));
                index += 25;
            }
            InputStream image = null;
            try {
                if(query.length() > 25) {
                    image = new URL("http://www.tombstonebuilder.com/generate.php?top1=R.I.P.&top2=" + args[0].replace(" ", "%20").replace("@", "") + "&top3=" + lines.get(0).replace(" ", "%20") + "&top4=" + lines.get(1).replace(" ", "%20") + "&sp=").openStream();
                } else {
                    image = new URL("http://www.tombstonebuilder.com/generate.php?top1=R.I.P.&top2=" + args[0].replace(" ", "%20").replace("@", "") + "&top3=" + lines.get(0).replace(" ", "%20") + "&top4=&sp=").openStream();
                }

            } catch (IOException e1) {
                e1.printStackTrace();
                //TODO error handling. image can not be sent if it does not exist.
            }
            message.delete().queue();
            e.getTextChannel().sendFile(image, "rip.png", null).queue();
        } else {
            sendUsageMessage();
        }
    }

    @Override
    public String getDescription() {
        return "Creates a tombstone for you";
    }

    @Override
    public String getUsage() {
        return "rip <name> <text>";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
