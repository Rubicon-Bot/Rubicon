package fun.rubicon.commands.admin;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.Main;
import fun.rubicon.core.permission.PermissionManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.MySQL;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandAutochannel extends Command {

    public CommandAutochannel(String command, CommandCategory category) {
        super(command, category);
    }

    public static HashMap<Guild, ChannelSearch> searches = new HashMap<>();

    private static final String[] EMOTI = ("\uD83C\uDF4F \uD83C\uDF4E \uD83C\uDF50 \uD83C\uDF4A \uD83C\uDF4B \uD83C\uDF4C \uD83C\uDF49 \uD83C\uDF47 \uD83C\uDF53 \uD83C\uDF48 \uD83C\uDF52 \uD83C\uDF51 \uD83C\uDF4D \uD83E\uDD5D " +
            "\uD83E\uDD51 \uD83C\uDF45 \uD83C\uDF46 \uD83E\uDD52 \uD83E\uDD55 \uD83C\uDF3D \uD83C\uDF36 \uD83E\uDD54 \uD83C\uDF60 \uD83C\uDF30 \uD83E\uDD5C \uD83C\uDF6F \uD83E\uDD50 \uD83C\uDF5E " +
            "\uD83E\uDD56 \uD83E\uDDC0 \uD83E\uDD5A \uD83C\uDF73 \uD83E\uDD53 \uD83E\uDD5E \uD83C\uDF64 \uD83C\uDF57 \uD83C\uDF56 \uD83C\uDF55 \uD83C\uDF2D \uD83C\uDF54 \uD83C\uDF5F \uD83E\uDD59 " +
            "\uD83C\uDF2E \uD83C\uDF2F \uD83E\uDD57 \uD83E\uDD58 \uD83C\uDF5D \uD83C\uDF5C \uD83C\uDF72 \uD83C\uDF65 \uD83C\uDF63 \uD83C\uDF71 \uD83C\uDF5B \uD83C\uDF5A \uD83C\uDF59 \uD83C\uDF58 " +
            "\uD83C\uDF62 \uD83C\uDF61 \uD83C\uDF67 \uD83C\uDF68 \uD83C\uDF66 \uD83C\uDF70 \uD83C\uDF82 \uD83C\uDF6E \uD83C\uDF6D \uD83C\uDF6C \uD83C\uDF6B \uD83C\uDF7F \uD83C\uDF69 \uD83C\uDF6A \uD83E\uDD5B " +
            "\uD83C\uDF75 \uD83C\uDF76 \uD83C\uDF7A \uD83C\uDF7B \uD83E\uDD42 \uD83C\uDF77 \uD83E\uDD43 \uD83C\uDF78 \uD83C\uDF79 \uD83C\uDF7E \uD83E\uDD44 \uD83C\uDF74 \uD83C\uDF7D").split(" ");


    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("list")) {
                String entry = Main.getMySQL().getGuildValue(e.getGuild(), "autochannels");
                String out = "";
                for(String s : entry.split(",")) {
                    out += e.getJDA().getVoiceChannelById(s).getName() + "\n";
                }
                sendEmbededMessage(e.getTextChannel(),  "Autochannels", Colors.COLOR_PRIMARY, out);
            } else
                sendUsageMessage();
        } else if (args.length >= 2) {
            switch (args[0].toLowerCase()) {
                case "create":
                case "c":
                    createChannel(args);
                    break;
                case "del":
                case "delete":
                    invokeDelete(args);
                    break;
                case "add":
                case "set":
                    addChannel(args);
                    break;
                default:
                    sendUsageMessage();
                    break;
            }
        } else
            sendUsageMessage();
    }

    private void createChannel(String[] args) {
        StringBuilder names = new StringBuilder();
        for(int i = 1; i < args.length; i++){
            names.append(args[i]).append(" ");
        }
        String name = names.toString();
        name = names.replace(name.lastIndexOf(" "),name.lastIndexOf(" ") + 1, "").toString();
        Channel channel = e.getGuild().getController().createVoiceChannel(name).complete();
        String oldEntry = RubiconBot.getMySQL().getGuildValue(e.getGuild(), "autochannels");
        String newEntry = oldEntry +", " + channel.getId();
        RubiconBot.getMySQL().updateGuildValue(e.getGuild(), "autochannels", newEntry);
        Message mymsg = e.getChannel().sendMessage(EmbedUtil.success("Created Autochannel", "Successfully created autochannel -> " + channel.getName() + "").build()).complete();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mymsg.delete().queue();
            }
        },5000);
    }


    private void invokeDelete(String[] args){
        StringBuilder names = new StringBuilder();
        for(int i = 1; i < args.length; i++){
            names.append(args[i]).append(" ");
        }
        String name = names.toString();
        name = names.replace(name.lastIndexOf(" "),name.lastIndexOf(" ") + 1, "").toString();
        List<VoiceChannel> channels = e.getGuild().getVoiceChannelsByName(name, false);
        String oldEntry = RubiconBot.getMySQL().getGuildValue(e.getGuild(), "autochannels");
        List<VoiceChannel> autochannels = new ArrayList<>();
        channels.forEach(c -> {
            if (oldEntry.contains(c.getId()))
                autochannels.add(c);
        });
        if(autochannels.isEmpty()){
            e.getTextChannel().sendMessage(EmbedUtil.error("Unknown Channel", ":warning: There is now channel with this name").build()).queue();
            return;
        }
        if(autochannels.size() > 1){

            ChannelSearch search = genChannelSearch(autochannels, oldEntry, true);
            searches.put(e.getGuild(), search);
        } else {
            VoiceChannel channel = channels.get(0);
            if(!oldEntry.contains(channel.getId())){
                e.getTextChannel().sendMessage(EmbedUtil.error("Unknown channel", ":warning: This channel isn't an autochannel").build()).queue();
                return;
            }
            deleteChannel(channel);
        }
    }

    private ChannelSearch genChannelSearch(List<VoiceChannel> channels, String oldEntry, boolean deleteChannel){
        HashMap<String, VoiceChannel> channellist = new HashMap<>();
        StringBuilder channelnames = new StringBuilder();
        ArrayList<String> EMOJIS = new ArrayList<>(Arrays.asList(EMOTI));
        channels.forEach(c -> {
            String category;
            if(c.getParent() != null)
                category = c.getParent().getName();
            else
                category = "NONE";
            channelnames.append(EMOJIS.get(0)).append(" - ").append(c.getName()).append("(`").append(c.getId()).append("`) (Category: ").append(category).append(")\n");
            channellist.put(EMOJIS.get(0), c);
            EMOJIS.remove(0);
        });
        if(channellist.isEmpty()){
            e.getTextChannel().sendMessage(EmbedUtil.error("Unknown Channel", ":warning: There is now channel with this name").build()).queue();
            return null;
        } else {
            Message msg = e.getTextChannel().sendMessage(new EmbedBuilder().setColor(Colors.COLOR_SECONDARY).setDescription(channelnames.toString()).build()).complete();
            channellist.keySet().forEach(e ->{
                msg.addReaction(e).queue();
            });
            ChannelSearch search = new ChannelSearch(msg, channellist, deleteChannel);
            return search;
        }
    }

    private void deleteChannel(VoiceChannel channel){
        String oldEntry = RubiconBot.getMySQL().getGuildValue(e.getGuild(), "autochannels");
        String newEntry = oldEntry.replace(channel.getId() + ",", "");
        RubiconBot.getMySQL().updateGuildValue(e.getGuild(), "autochannels", newEntry);
        Message mymsg = e.getChannel().sendMessage(EmbedUtil.success("Deleted channel", "Autochannel " + channel.getName() + "successfully removed").build()).complete();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mymsg.delete().queue();
            }
        },5000);
        channel.delete().queue();
    }

    public static void deleteChannel(VoiceChannel channel, Message message, MessageReactionAddEvent event){
        String oldEntry = RubiconBot.getMySQL().getGuildValue(event.getGuild(), "autochannels");
        String newEntry = oldEntry.replace(channel.getId() + ",", "");
        RubiconBot.getMySQL().updateGuildValue(event.getGuild(), "autochannels", newEntry);
        message.editMessage(EmbedUtil.success("Deleted channel", "Autochannel " + channel.getName() + "successfully removed").build()).complete();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                message.delete().queue();
            }
        },5000);
        channel.delete().queue();
    }

    public class ChannelSearch{
        Message message;
        HashMap<String, VoiceChannel> channels;
        boolean delete;

        private ChannelSearch(Message message, HashMap<String, VoiceChannel> channels, boolean deleteChannel){
            this.message = message;
            this.channels = channels;
            this.delete = deleteChannel;
        }
    }

    public static void handleReaction(MessageReactionAddEvent event){
        event.getReaction().removeReaction(event.getUser()).queue();
        if(!searches.containsKey(event.getGuild()))
            return;
        ChannelSearch search = searches.get(event.getGuild());
        if(!event.getMessageId().equals(search.message.getId()))
            return;
        String emote = event.getReactionEmote().getName();
        if(!search.channels.containsKey(emote))
            return;
        if(search.delete) {
            VoiceChannel channel = search.channels.get(emote);
            deleteChannel(channel, event.getTextChannel().getMessageById(event.getMessageId()).complete(), event);

        } else {
            VoiceChannel channel = search.channels.get(emote);
            String oldEntry = RubiconBot.getMySQL().getGuildValue(event.getGuild(), "autochannels");
            String newEntry = oldEntry +", " + channel.getId();
            RubiconBot.getMySQL().updateGuildValue(event.getGuild(), "autochannels", newEntry);
            Message mymsg = event.getTextChannel().getMessageById(event.getMessageId()).complete().editMessage(EmbedUtil.success("Created Autochannel", "Successfully created autochannel -> " + channel.getName() + "").build()).complete();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mymsg.delete().queue();
                }
            },5000);
        }
        event.getTextChannel().getMessageById(event.getMessageId()).complete().getReactions().forEach(r -> {
            r.removeReaction().queue();
        });
    }

    public void addChannel(String[] args){
        StringBuilder names = new StringBuilder();
        for(int i = 1; i < args.length; i++){
            names.append(args[i]).append(" ");
        }
        String name = names.toString();
        name = names.replace(name.lastIndexOf(" "),name.lastIndexOf(" ") + 1, "").toString();
        List<VoiceChannel> channels = e.getGuild().getVoiceChannelsByName(name, false);
        String oldEntry = RubiconBot.getMySQL().getGuildValue(e.getGuild(), "autochannels");
        if(channels.size() > 1){
            ChannelSearch search = genChannelSearch(channels, oldEntry, false);
            searches.put(e.getGuild(), search);
        } else {
            VoiceChannel channel = channels.get(0);
            String newEntry = oldEntry +", " + channel.getId();
            RubiconBot.getMySQL().updateGuildValue(e.getGuild(), "autochannels", newEntry);
            Message mymsg = e.getChannel().sendMessage(EmbedUtil.success("Created Autochannel", "Successfully created autochannel -> " + channel.getName() + "").build()).complete();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mymsg.delete().queue();
                }
            },5000);
        }
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUsage() {
        return "autochannel create [channelname]\n" +
                "autochannel list\n" +
                "autochannel delete [channel name]\n" +
                "autochannel add [channel name]";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }
}
