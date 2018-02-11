package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.music.MusicManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Leon Kappes / Lee
 */
public class CommandMaintenance extends CommandHandler {

    public static boolean maintenance = false;


    public CommandMaintenance() {
        super(new String[]{"maintenance", "wartung"}, CommandCategory.BOT_OWNER, new PermissionRequirements("command.maintenance", true, false), "Starts bot maintenance.", "<message for playing status>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.getArgs().length == 1) {
            if (parsedCommandInvocation.getArgs()[0].equalsIgnoreCase("false")) {
                disable();
                return EmbedUtil.message(EmbedUtil.success("Disabled maintenance", "Successfully disabled maintenance"));
            }
        }
        if (parsedCommandInvocation.getArgs().length < 1)
            return createHelpMessage();

        //Enabling with MaintenanceCommand
        RubiconBot.getConfiguration().set("maintenance", "1");
        //Build Status message
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < parsedCommandInvocation.getArgs().length; i++) {
            msg.append(parsedCommandInvocation.getArgs()[i]).append(" ");
        }
        //Set playing status
        RubiconBot.getConfiguration().set("playingStatus", msg.toString());
        enable();
        //Play maintenance sound
        MusicManager manager = new MusicManager(parsedCommandInvocation);
        manager.maintenanceSound();
        SafeMessage.sendMessage(parsedCommandInvocation.getTextChannel(), new EmbedBuilder().setColor(Colors.COLOR_PRIMARY).setTitle("Activated Maintenance").setAuthor(parsedCommandInvocation.getAuthor().getName(), null, parsedCommandInvocation.getAuthor().getEffectiveAvatarUrl()).setDescription("Bot will only respond to owners.").build());
        return null;
    }

    public static void disable() {
        maintenance = false;
        RubiconBot.getConfiguration().set("playingStatus", "0");
        RubiconBot.getConfiguration().set("maintenance", "0");
        RubiconBot.getJDA().getPresence().setGame(Game.playing("Maintenance is over"));
        RubiconBot.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);
    }

    public static void enable() {
        maintenance = true;
        RubiconBot.getJDA().getPresence().setGame(Game.playing(RubiconBot.getConfiguration().getString("playingStatus")));
        RubiconBot.getJDA().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
    }
}

