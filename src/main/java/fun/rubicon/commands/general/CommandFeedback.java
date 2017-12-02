package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import java.text.ParseException;

public class CommandFeedback extends Command{
    public CommandFeedback(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        //Check if enough args
        if (args.length<3) {
            sendUsageMessage();
            return;
        }
        //Make String out of Args
        String text = "";
        for (String arg : args) {
            text += arg + " ";
        }
        //Try to delete Message
        try {
            e.getMessage().delete().queue();
        }catch (PermissionException er){
            PrivateChannel pc = e.getGuild().getOwner().getUser().openPrivateChannel().complete();
            pc.sendMessage("Please give me MESSAGE_MANAGE permissions!").queue();
        }
        //Send Feedback to Community Server
        RubiconBot.getJDA().getTextChannelById("383324255380701194").sendMessage(
                new EmbedBuilder()
                        .setAuthor(e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator(), null, e.getAuthor().getAvatarUrl())
                        .setDescription("**New Feedback from Server: " + e.getGuild().getName() + " [" + e.getGuild().getId() +"]!**\n```fix\n" + text + "```")
                        .build()
        ).queue();
        //User Feedback
        sendEmbededMessage("Successfully send the Feedback to Community Server");
    }

    @Override
    public String getDescription() {
        return "Send feedback to the developer.";
    }

    @Override
    public String getUsage() {
        return "feedback <message>";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
