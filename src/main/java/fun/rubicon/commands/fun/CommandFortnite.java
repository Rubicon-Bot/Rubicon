package fun.rubicon.commands.fun;

import de.foryasee.httprequest.HttpRequest;
import de.foryasee.httprequest.RequestHeader;
import de.foryasee.httprequest.RequestResponse;
import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandFortnite extends CommandHandler {

    public CommandFortnite() {
        super(new String[]{"fortnite"}, CommandCategory.FUN, new PermissionRequirements(PermissionLevel.EVERYONE, "command.fortnite"), "Get your Fortnite statistics.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        HttpRequest httpRequest = new HttpRequest("https://api.fortnitetracker.com/v1/profile/pc/ForYaSee");
        httpRequest.setRequestHeader(new RequestHeader().addField("TRN-Api-Key", RubiconBot.getConfiguration().getString("fortnite")));
        try {
            RequestResponse response = httpRequest.sendGETRequest();
            Logger.debug(response.getResponse());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
