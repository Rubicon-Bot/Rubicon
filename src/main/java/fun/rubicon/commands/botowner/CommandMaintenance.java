package fun.rubicon.commands.botowner;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.music.AudioPlayerSendHandler;
import fun.rubicon.core.music.MusicManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.FileUtil;
import fun.rubicon.util.SafeMessage;
import javafx.scene.media.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.audio.AudioConnection;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.managers.ChannelManager;
import net.dv8tion.jda.core.managers.ChannelManagerUpdatable;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.core.requests.restaction.ChannelAction;
import net.dv8tion.jda.core.requests.restaction.InviteAction;
import net.dv8tion.jda.core.requests.restaction.PermissionOverrideAction;
import sun.misc.IOUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author Leon Kappes / Lee
 */
public class CommandMaintenance extends CommandHandler {

    public static boolean maintenance = false;


    public CommandMaintenance() {
        super(new String[]{"maintenance", "wartung"}, CommandCategory.BOT_OWNER, new PermissionRequirements(PermissionLevel.BOT_AUTHOR, "command.maintenance"), "Starts bot maintenance.", "<time in minutes> <message for playing status>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.getArgs().length < 2)
            return createHelpMessage();
            maintenance = true;
        StringBuilder msg = new StringBuilder();
        for (int i = 1; i < parsedCommandInvocation.getArgs().length; i++) {
            msg.append(parsedCommandInvocation.getArgs()[i] + " ");
        }
        RubiconBot.getConfiguration().set("playingStatus", msg.toString());
        MusicManager manager = new MusicManager(parsedCommandInvocation);
        TimerTask resolveTask = new TimerTask() {
            @Override
            public void run() {
                RubiconBot.getConfiguration().set("playingStatus", "0");
                maintenance = false;
                System.out.println("Zurückgesetzt");
            }
        };
        int runtime =  Integer.parseInt(parsedCommandInvocation.getArgs()[0]);
        RubiconBot.getTimer().schedule(resolveTask, new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(runtime)));
        SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), new EmbedBuilder().setColor(Colors.COLOR_PRIMARY).setTitle("Activated Maintenance").setAuthor(parsedCommandInvocation.getAuthor().getName(),null,parsedCommandInvocation.getAuthor().getEffectiveAvatarUrl()).setDescription("Bot will only Respond to Owners").build());
        return null;
    }
}

