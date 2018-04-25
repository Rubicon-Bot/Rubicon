package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Message;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */

public class CommandKey extends CommandHandler {

    public CommandKey() {
        super(new String[]{"key", "redeem"}, CommandCategory.GENERAL, new PermissionRequirements("key", false, true), "Redeem an Gift Code or Generate one.", "<key>\ngen");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        /*if (invocation.getArgs().length < 2)
            return createHelpMessage(invocation);

        switch (invocation.getArgs()[0]) {
            case "gen":
            case "generate":
                switch (invocation.getArgs()[1]) {
                    case "premium":
                        String token = UUID.randomUUID().toString();
                        System.out.println(token.length());
                        try {
                            PreparedStatement ps = RubiconBot.getMySQL().prepareStatement("INSERT INTO `keys` (uuid, gift) VALUES (?,?)");
                            ps.setString(1,token);
                            ps.setString(2,"premium");
                            ps.execute();
                        } catch (SQLException e) {
                            Logger.error(e);
                        }
                        invocation.getAuthor().openPrivateChannel().queue(privateChannel -> {
                            privateChannel.sendMessage(EmbedUtil.success("Token Successfully generated", String.format("Token: %d\nRedeemable with rc!key %d", token, token)).build()).queue();
                        });
                        break;
                    default:
                        return createHelpMessage(invocation);

                }

        }*/
        return null;
    }


}