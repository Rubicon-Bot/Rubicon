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
    private static Timer timer = new Timer();
    private static int current = 0;

    public CommandDisco() {
        super(new String[]{"disco"}, CommandCategory.BOT_OWNER, new PermissionRequirements("disco", true, false), "Rubicon is going into a disco.", "[disable]");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if(enabled)
            enabled = false;
        else
            enabled = true;
        if (!enabled) {
            timer.cancel();
            RubiconBot.getShardManager().setStatus(OnlineStatus.ONLINE);
            return EmbedUtil.message(EmbedUtil.success("Disabled Disco!", "Successfully disabled disco."));
        }
        OnlineStatus[] statuses = {OnlineStatus.ONLINE, OnlineStatus.DO_NOT_DISTURB, OnlineStatus.IDLE};
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(current == statuses.length)
                    current = 0;
                RubiconBot.getShardManager().setStatus(statuses[current]);
                current++;
            }
        }, 0, 1000);
        return EmbedUtil.message(EmbedUtil.success("Enabled Disco!", "Successfully enabled disco."));
    }
}
