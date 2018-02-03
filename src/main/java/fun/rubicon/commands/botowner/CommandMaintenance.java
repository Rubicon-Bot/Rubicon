package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.music.MusicManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import java.util.Date;
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
        manager.maintenanceSound();
        TimerTask resolveTask = new TimerTask() {
            @Override
            public void run() {
                RubiconBot.getConfiguration().set("playingStatus", "0");
                maintenance = false;
            }
        };
        int runtime =  Integer.parseInt(parsedCommandInvocation.getArgs()[0]);
        RubiconBot.getTimer().schedule(resolveTask, new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(runtime)));
        SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), new EmbedBuilder().setColor(Colors.COLOR_PRIMARY).setTitle("Activated Maintenance").setAuthor(parsedCommandInvocation.getAuthor().getName(),null,parsedCommandInvocation.getAuthor().getEffectiveAvatarUrl()).setDescription("Bot will only Respond to Owners").build());
        return null;
    }
}

