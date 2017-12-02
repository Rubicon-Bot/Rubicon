package fun.rubicon.commands.general;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.DiscordCore;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.text.ParseException;

public class CommandBug extends Command {
    public CommandBug(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        //Check if enough args
        if (args.length < 3) {
            sendUsageMessage();
            return;
        }
        //Make String out of args
        String text = "";
        for (String arg : args) {
            text += arg + " ";
        }
        //Try do delete Message
        try {
            e.getMessage().delete().queue();
        } catch (PermissionException er) {
            PrivateChannel pc = e.getGuild().getOwner().getUser().openPrivateChannel().complete();
            pc.sendMessage("Please give me MESSAGE_MANAGE permissions!").queue();
        }
        //Post Report to Dev Server
        DiscordCore.getJDA().getTextChannelById("382231366064144384").sendMessage(
                new EmbedBuilder()
                        .setAuthor(e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator(), null, e.getAuthor().getAvatarUrl())
                        .setDescription("**New Bug Detected!**\n```fix\n" + text + "```")
                        .build()
        ).queue();
        //User Feedback
        sendEmbededMessage("Successfully send the Bug to Head Developers");
    }

    @Override
    public String getDescription() {
        return "Report a bug to the developers.";
    }

    @Override
    public String getUsage() {
        return "bug [message] (min. 3 args)";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
