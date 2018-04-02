package fun.rubicon.commands.botowner;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Michael Rittmeister / Schlaubi
 * @license GNU General Public License v3.0
 */
public class CommandMaintenance extends CommandHandler{
    public CommandMaintenance() {
        super(new String[] {"maintenance"}, CommandCategory.BOT_OWNER, new PermissionRequirements("maintenance", true, false), "Enable Rubicon's maintenance mode 3001", "<reason>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        boolean maintenance = RubiconBot.getCommandManager().isMaintenanceEnabled();
        String[] args = invocation.getArgs();
        if(maintenance){
            //Start game animator
            RubiconBot.getGameAnimator().start();
            //Disable maintenance
            RubiconBot.getCommandManager().setMaintenance(false);
            RubiconBot.getConfiguration().unset("maintenance");
            RubiconBot.getConfiguration().unset("playingStatus");
            //Change status
            RubiconBot.getShardManager().setStatus(OnlineStatus.ONLINE);
            RubiconBot.getGameAnimator().start();
            return new MessageBuilder().setEmbed(EmbedUtil.success("Maintenance deactivated", "Deactivated maintenance mode. Bot will response to everyone").build()).build();
        } else {
            if(args.length == 0)
                return createHelpMessage();
            //Save that maintenance is enabled
            RubiconBot.getConfiguration().set("maintenance", "true");
            //Build message
            StringBuilder statusmsg = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                statusmsg.append(args[i]).append(" ");
            }
            //Replace last " "
            statusmsg.replace(statusmsg.lastIndexOf(" "), statusmsg.lastIndexOf(" ") + 1, "");
            //Enable maintenance
            RubiconBot.getCommandManager().setMaintenance(true);
            /// /Update status & Game
            RubiconBot.getShardManager().setStatus(OnlineStatus.DO_NOT_DISTURB);
            RubiconBot.getShardManager().setGame(Game.watching(statusmsg.toString()));
            //Save playing status
            RubiconBot.getConfiguration().set("playingStatus", statusmsg.toString());
            //Stop game animator
            RubiconBot.getGameAnimator().stop();
            return new MessageBuilder().setEmbed(EmbedUtil.success("Maintenance activated", "Activated maintenance mode. Bot will only response to owners and staff team").build()).build();
        }
    }
}
