package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static fun.rubicon.util.EmbedUtil.error;
import static fun.rubicon.util.EmbedUtil.message;

/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */
public class CommandYouTube extends CommandHandler {

    private static HashMap<Long, AnnounceHolder> announceMap = new HashMap<>();
    private static Timer timer = new Timer();


    public CommandYouTube() {
        super(new String[]{"youtube"}, CommandCategory.GENERAL, new PermissionRequirements("youtube", false, false), "Announce your newest YouTube Videos!", "<#channel> <YouTube Channel ID>");

    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length < 2)
            return message(EmbedUtil.error("Invalid parameters", "Use `rc!help youtube` for more info!"));
        if (!RubiconUser.fromUser(invocation.getAuthor()).isPremium())
            return EmbedUtil.message(EmbedUtil.noPremium());
        if (invocation.getMessage().getMentionedChannels().isEmpty())
            return EmbedUtil.message(EmbedUtil.error("No channel", "You forgot to Mention an Channel"));
        try {
            PreparedStatement ps = RubiconBot.getMySQL().getConnection().prepareStatement("SELECT * from `youtube` WHERE serverid=?");
            ps.setLong(1, invocation.getGuild().getIdLong());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                PreparedStatement ps2 = RubiconBot.getMySQL().getConnection().prepareStatement("DELETE from `youtube` WHERE serverid=?");
                ps.setLong(1, invocation.getGuild().getIdLong());
                ps.execute();
                return message(error("Error", "Setup is already finished now deleting old Setup!"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        String creator = invocation.getArgs()[1].replace(" ", "");
        Request request = new Request.Builder()
                .url("https://youtube.com/channel/" + creator)
                .build();

        try {
            Response response = new OkHttpClient().newCall(request).execute();
            if (response.code() != 200)
                return message(error("Wrong ChannelID", "Your given ChannelID is not Valid.It must be something like UCgez9UZRV7E-JFbo64eCcfg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message infoMessage = SafeMessage.sendMessageBlocking(invocation.getTextChannel(), EmbedUtil.message(new EmbedBuilder().setTitle("Title....").setDescription("Please enter an Message that will be sent whenever an new Video is uploaded! Use %url% for the Video URL and %title% for the Video Title.Markdown **is supported**").setFooter("Will abort in 60 seconds.", null)));
        announceMap.put(invocation.getAuthor().getIdLong(), new AnnounceHolder(invocation.getTextChannel(), invocation.getMessage().getMentionedChannels().get(0), creator, invocation.getAuthor(), infoMessage));
        return null;
    }

    public static void handle(MessageReceivedEvent event) {
        if (!announceMap.containsKey(event.getAuthor().getIdLong()))
            return;
        AnnounceHolder holder = announceMap.get(event.getAuthor().getIdLong());
        if (!event.getTextChannel().getId().equals(holder.textChannel.getId()))
            return;
        if (event.getMessage().getContentDisplay().contains(holder.creator))
            return;
        String description = event.getMessage().getContentDisplay();
        try {
            PreparedStatement ps = RubiconBot.getMySQL().getConnection().prepareStatement("INSERT INTO `youtube` (" +
                    "`serverid`," +
                    "`youmsg`," +
                    "`youchannel`," +
                    "`youcreator`," +
                    "`lastvideo`)" +
                    "VALUES (?,?,?,?,0)");
            ps.setLong(1, holder.textChannel.getGuild().getIdLong());
            ps.setString(2, description);
            ps.setLong(3, holder.channel.getIdLong());
            ps.setString(4, holder.creator);
            ps.execute();
            holder.delete();
            event.getMessage().delete().queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static class AnnounceHolder {
        private TextChannel textChannel;
        private TextChannel channel;
        private User author;
        private Message infoMessage;
        private String creator;
        private Timer timer;

        private AnnounceHolder(TextChannel textChannel, TextChannel channel, String creator, User author, Message infoMessage) {
            this.textChannel = textChannel;
            this.channel = channel;
            this.author = author;
            this.infoMessage = infoMessage;
            this.creator = creator;

            //Abort
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    infoMessage.delete().queue();
                    SafeMessage.sendMessage(textChannel, EmbedUtil.message(EmbedUtil.error("Aborted!", "Aborted Setup.")));
                    announceMap.remove(author.getIdLong());
                }
            }, 60000);
        }

        private void delete() {
            announceMap.remove(author.getIdLong());
            timer.cancel();
            SafeMessage.sendMessage(textChannel, EmbedUtil.message(EmbedUtil.success("Success!", "Successfully finished Setup.")));
            infoMessage.delete().queue();
        }
    }
}