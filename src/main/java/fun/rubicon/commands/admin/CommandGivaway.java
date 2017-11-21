package fun.rubicon.commands.admin;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CommandGivaway extends Command implements Serializable{

    private static String emote= "\\ud83c\\udfc6";
    public static ArrayList<String> idioten = new ArrayList<>();
    public CommandGivaway(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        MessageChannel channel = e.getTextChannel();
        if (args.length < 1) {
            sendUsageMessage();
            return;
        }
        switch (args[0]){
            case "create":
                String[] voteargs = e.getMessage().getContent().split("\\|");
                if(voteargs.length < 2){
                    sendUsageMessage();
                    return;
                }
                channel.sendMessage(new EmbedBuilder()
                        .setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl())
                        .setDescription(voteargs.toString())
                        .build()).queue();
                break;
            default:
        }
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }

    public static void handleReaction(MessageReactionAddEvent event){
        String react = event.getReaction().getReactionEmote().getName();
        if (react.equals(emote)){
            idioten.add(event.getMember().getUser().getId());
        }
    }



}
