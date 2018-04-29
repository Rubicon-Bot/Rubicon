package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Message;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandDisco extends CommandHandler {

    private static boolean enabled = false;
    private static Timer timer;
    private static int current = 0;

    public CommandDisco() {
        super(new String[]{"disco"}, CommandCategory.BOT_OWNER, new PermissionRequirements("disco", true, false), "Rubicon is going into a disco.", "<on/off>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if(invocation.getArgs().length == 0)
            return createHelpMessage();
        String command = invocation.getArgs()[0];
        if(command.equalsIgnoreCase("on")) {
            if(enabled)
                return EmbedUtil.message(EmbedUtil.error("Already enabled!", "You cannot enable the disco twice."));
            OnlineStatus[] statuses = {OnlineStatus.ONLINE, OnlineStatus.DO_NOT_DISTURB, OnlineStatus.IDLE};
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(current == statuses.length)
                        current = 0;
                    RubiconBot.getShardManager().setStatus(statuses[current]);
                    current++;
                }
            }, 0, 1000);
            enabled = true;
            return EmbedUtil.message(EmbedUtil.success("Enabled Disco!", "Successfully enabled disco."));
        } else if(command.equalsIgnoreCase("off")) {
            if(enabled) {
                timer.cancel();
                RubiconBot.getShardManager().setStatus(OnlineStatus.ONLINE);
                enabled = false;
                return EmbedUtil.message(EmbedUtil.success("Disabled Disco!", "Successfully disabled disco."));
            } else {
                return EmbedUtil.message(EmbedUtil.error("Not enabled!", "Disco is not enabled."));
            }
        }
        return null;
    }
}
