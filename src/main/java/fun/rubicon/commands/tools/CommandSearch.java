package fun.rubicon.commands.tools;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class CommandSearch extends Command {
    public CommandSearch(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        TextChannel channel = e.getTextChannel();
        Guild guild = e.getGuild();

        if(args.length == 0){
            sendUsageMessage();
            return;
        }
        StringBuilder query = new StringBuilder();
        for(int i = 0; i < args.length; i++){
            query.append(args[i]);
        }

        StringBuilder textchannels = new StringBuilder();
        StringBuilder voicechannels = new StringBuilder();
        StringBuilder members = new StringBuilder();
        StringBuilder roles = new StringBuilder();

        Message mymsg = channel.sendMessage(new EmbedBuilder().setColor(Color.cyan).setDescription("Collecting textchannels ...").build()).complete();

        guild.getTextChannels().forEach(i -> {
            if(i.getName().toLowerCase().contains(query.toString().toLowerCase()))
                textchannels.append(i.getName() + "(`" + i.getId() + "`)").append("\n");
        });

        mymsg.editMessage(new EmbedBuilder().setColor(Color.cyan).setDescription("Collecting voicechannels ...").build()).queue();


        guild.getVoiceChannels().forEach(i -> {
            if(i.getName().toLowerCase().contains(query.toString().toLowerCase()))
                voicechannels.append(i.getName() + "(`" + i.getId() + "`)").append("\n");
        });

        mymsg.editMessage(new EmbedBuilder().setColor(Color.cyan).setDescription("Collecting users ...").build()).queue();


        guild.getMembers().forEach(i -> {
            if(i.getUser().getName().toLowerCase().contains(query.toString().toLowerCase()) || i.getEffectiveName().toLowerCase().contains(query.toString().toLowerCase()))
                members.append(i.getUser().getName() + "(`" + i.getUser().getId() + "`)").append("\n");
        });

        mymsg.editMessage(new EmbedBuilder().setColor(Color.cyan).setDescription("Collecting roles ...").build()).queue();

        guild.getRoles().forEach(i -> {
            if(i.getName().toLowerCase().contains(query.toString().toLowerCase()))
                roles.append(i.getName() + "(`" + i.getId() + "`)").append("\n");
        });
        try {
            EmbedBuilder results = new EmbedBuilder()
                    .setColor(Color.green)
                    .addField("**Textchannels**", textchannels.toString(), false)
                    .addField("**Voicechannles**", voicechannels.toString(), false)
                    .addField("**Members**", members.toString(), false)
                    .addField("**Roles**", roles.toString(), false);
            mymsg.editMessage(results.build()).queue();
        } catch (IllegalArgumentException ex){
            mymsg.editMessage(new EmbedBuilder().setDescription(":warning: TO MANY RESULT HEEEEEELP!").build()).queue();
        }

    }

    @Override
    public String getDescription() {
        return "Searches for users, roles and channels with a specified name.";
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
