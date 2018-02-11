package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.coinhive.CoinhiveManager;
import fun.rubicon.core.coinhive.CoinhiveUser;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.sql.UserSQL;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandMiner extends CommandHandler {

    public CommandMiner() {
        super(new String[]{"miner", "mine"}, CommandCategory.GENERAL, new PermissionRequirements("command.miner", false, true), "Get your collected hashes, top miners or let payout your hashes in rubys.",
                "| Get your hashes\n" +
                        "payout | Payout your mined hashes in rubys");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        CoinhiveUser coinhiveUser = CoinhiveManager.getCoinhiveUser(parsedCommandInvocation.getAuthor());
        UserSQL userSQL = new UserSQL(parsedCommandInvocation.getAuthor());
        if (parsedCommandInvocation.getArgs().length == 0) {
            return EmbedUtil.message(new EmbedBuilder().setDescription("You currently have `" + coinhiveUser.getBalance() + "` hashes.").setAuthor(parsedCommandInvocation.getAuthor().getName(), null, parsedCommandInvocation.getAuthor().getAvatarUrl()).setColor(Colors.COLOR_PRIMARY).setFooter("Miner more on https://miner.rubicon.fun", null));
        }
        if (parsedCommandInvocation.getArgs()[0].equalsIgnoreCase("payout")) {
            int withdrawAmount = (int) coinhiveUser.getBalance();
            if (withdrawAmount == 0) {
                return EmbedUtil.message(EmbedUtil.error("No money!", "You have to mine new rubys first."));
            }
            int oldMoney = Integer.parseInt(userSQL.get("money"));
            int newMoney = oldMoney + withdrawAmount;
            userSQL.set("money", newMoney + "");
            CoinhiveManager.withdrawUser(coinhiveUser, withdrawAmount);
            return EmbedUtil.message(EmbedUtil.success("Payout successfull!", "Transfered `" + withdrawAmount + "` rubys."));
        } else {
            return createHelpMessage();
        }
    }
}
