package fun.rubicon.commands.fun;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */public class CommandRainbow extends CommandHandler {

    public CommandRainbow(){
        super(new String[]{"rainbow","rainbowsixsiege"}, CommandCategory.FUN,new PermissionRequirements("rainbow",false,true),"Get Rainbow Six Siege stats","<username> <uplay/xbox/playstation>");
    }
    
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        //https://api.r6stats.com/api/v1/players/ForYaSee/?platform=uplay
        if(invocation.getArgs().length<2)
            return createHelpMessage(invocation);
        return null;
    }

}