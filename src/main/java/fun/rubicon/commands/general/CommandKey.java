package fun.rubicon.commands.general;

import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */

public class CommandKey extends CommandHandler {


    public CommandKey() {
        super(new String[]{"key", "redeem", "keys"}, CommandCategory.GENERAL, new PermissionRequirements("key", false, true), "Redeem an Gift Code or Generate one.", "<key>\ngen <type>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length < 1)
            return createHelpMessage(invocation);
        long creationDate = new Date().getTime();
        switch (invocation.getArgs()[0]) {
            case "gen":
            case "generate":
                if (invocation.getArgs().length >= 2) {
                    switch (invocation.getArgs()[1]) {
                        case "premium":

                            RubiconBot.getRethink().db.table("keys").insert(RubiconBot.getRethink().rethinkDB.hashMap("type", "premium").with("date", String.valueOf(creationDate)).with("creator", invocation.getAuthor().getId())).run(RubiconBot.getRethink().connection);
                            Cursor cursor = RubiconBot.getRethink().db.table("keys").filter(RubiconBot.getRethink().rethinkDB.hashMap("date", String.valueOf(creationDate))).run(RubiconBot.getRethink().connection);
                            List l = cursor.toList();
                            Map map = (Map) l.get(0);
                            try {
                                invocation.getAuthor().openPrivateChannel().complete().sendMessage("Your Token is: `" + map.get("id") + "`").queue();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case "beta":
                            RubiconBot.getRethink().db.table("keys").insert(RubiconBot.getRethink().rethinkDB.hashMap("type", "beta").with("date", String.valueOf(creationDate)).with("creator", invocation.getAuthor().getId())).run(RubiconBot.getRethink().connection);
                            Cursor cursor1 = RubiconBot.getRethink().db.table("keys").filter(RubiconBot.getRethink().rethinkDB.hashMap("date", String.valueOf(creationDate))).run(RubiconBot.getRethink().connection);
                            List l1 = cursor1.toList();
                            Map map1 = (Map) l1.get(0);
                            try {
                                invocation.getAuthor().openPrivateChannel().complete().sendMessage("Your Token is: `" + map1.get("id") + "`").queue();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        default:
                            return createHelpMessage(invocation);
                    }
                } else
                    return createHelpMessage(invocation);
                break;
            default:
                Cursor cursor = RubiconBot.getRethink().db.table("keys").filter(RubiconBot.getRethink().rethinkDB.hashMap("id", invocation.getArgs()[0])).run(RubiconBot.getRethink().connection);
                List l = cursor.toList();
                if (l.size() == 1) {
                    Map map = (Map) l.get(0);
                    switch (String.valueOf(map.get("type"))) {
                        case "premium":
                            RubiconUser.fromUser(invocation.getAuthor()).setPremium(CommandPremium.PREMIUM_TIME);
                            RubiconBot.getRethink().db.table("keys").filter(RubiconBot.getRethink().rethinkDB.hashMap("id", invocation.getArgs()[0])).delete().run(RubiconBot.getRethink().connection);
                            return message(success("Key redeemed", "Successfully activated your Premium"));
                        case "beta":
                            RubiconGuild.fromGuild(invocation.getGuild()).setBeta(true);
                            RubiconBot.getRethink().db.table("keys").filter(RubiconBot.getRethink().rethinkDB.hashMap("id", invocation.getArgs()[0])).delete().run(RubiconBot.getRethink().connection);
                            return message(success("Key redeemed", "Successfully activated your Beta-Key"));
                        default:
                            return message(error("Invalid Type", "The Type of The Token was undefined. Please get a new Token!"));
                    }
                } else
                    return message(error("Invalid Key", "Your provided Key was Invalid!"));

        }
        return null;
    }


}