package fun.rubicon.commands.admin;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Colors;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.Serializable;
import java.text.ParseException;
import java.util.*;

public class CommandGivaway extends Command implements Serializable{
    private static boolean running = false;
    private static String emote= "\ud83c\udfc6";
    public static ArrayList<String> idioten = new ArrayList<>();
    public CommandGivaway(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        MessageChannel channel = e.getTextChannel();
        if (args.length < 3) {
            sendUsageMessage();
            return;
        }
        switch (args[0]){
            case "create":
                String voteargs = "";
                if (!StringUtil.isNumeric(args[1])) return;
                for(int i = 2; i < args.length; i++) {
                    voteargs += args[i]+" ";
                }
                Message msg = channel.sendMessage(new EmbedBuilder()
                        .setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl())
                        .setTitle("Take Part with Reaction!")
                        .setColor(Colors.COLOR_NO_PERMISSION)
                        .setDescription(voteargs)
                        .build()).complete();
                msg.addReaction("\uD83C\uDFC6").queue();
                e.getMessage().delete().queue();
                running = true;
                int min = Integer.parseInt(args[1]);
                Timer timer = new Timer();
                String finalVoteargs = voteargs;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Random rn = new Random();
                        int range = idioten.size() - 0 + 1;
                        int randomNum =  rn.nextInt(range) + 0;
                        Member memb = e.getGuild().getMemberById(idioten.get(randomNum));
                        msg.getChannel().sendMessage("@everyone").queue();
                        msg.editMessage(new EmbedBuilder().setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl()).setTitle("Giveaway Closed!").setColor(Colors.COLOR_NO_PERMISSION).setDescription("The Member " + memb.getNickname() + "won the Following: ```\n" + finalVoteargs +"```").build()).queue();
                        msg.clearReactions().queue();
                        running = false;
                    }
                }, 1000*60*min);
                break;
            default:
                sendUsageMessage();
                break;
        }
    }

    @Override
    public String getDescription() {
        return "Create a simple Giveaway.Take part with Reaction!";
    }

    @Override
    public String getUsage() {
        return "giveaway create <How long the giveaway should run in Minutes> <What you want ot Give Away>";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }

    public static void handleReaction(MessageReactionAddEvent event){
        String react = event.getReaction().getReactionEmote().getName();
        if (react.equals(emote)){
            if (running == false) return;
            if (idioten.contains(event.getMember().getUser().getId())) return;
            idioten.add(event.getMember().getUser().getId());
        }
    }



}
