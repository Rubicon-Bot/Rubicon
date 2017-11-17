package de.rubicon.commands.tools;

import de.rubicon.command.Command;
import de.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandSearch extends Command {
    public CommandSearch(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        Message message = e.getMessage();
        TextChannel channel = e.getTextChannel();
        channel.sendTyping().queue();
        message.delete().queue();

        Message mymsq = channel.sendMessage(new EmbedBuilder().setDescription("Collecting users ...").build()).complete();

        StringBuilder query = new StringBuilder();
        for (int i = 0; i< args.length; i++){
            query.append(args[i]).append(" ");
        }


        StringBuilder users = new StringBuilder();
        e.getGuild().getMembers().forEach(m -> {
            if(m.getUser().getName().toLowerCase().contains(query.toString().toLowerCase()) || m.getEffectiveName().toLowerCase().contains(query.toString().toLowerCase()));
                users.append(m.getUser().getName()).append("(`").append(m.getUser().getId()).append("`) \n");
        });

    }

    @Override
    public String getDescription() {
        return "Searches for users, roles and channels with a specified name";
    }

    @Override
    public String getUsage() {
        return "search <query>";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
