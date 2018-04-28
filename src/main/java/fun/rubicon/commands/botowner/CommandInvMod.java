package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.rpg.inventory.RPGInventory;
import fun.rubicon.core.rpg.inventory.RPGInventoryItem;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandInvMod extends CommandHandler {

    public CommandInvMod() {
        super(new String[]{"invmod"}, CommandCategory.BOT_OWNER, new PermissionRequirements("invmod", true, false), "Modify the inventory of a user.", "<@User> <add/remove> <itemId> <amount>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length != 4)
            return createHelpMessage();
        if (invocation.getMessage().getMentionedUsers().size() != 1)
            return createHelpMessage();

        User user = invocation.getMessage().getMentionedUsers().get(0);
        RPGInventory inventory = RPGInventory.fromUser(user);

        if (!StringUtil.isNumeric(invocation.getArgs()[2]))
            return createHelpMessage();
        if (!StringUtil.isNumeric(invocation.getArgs()[3]))
            return createHelpMessage();

        String command = invocation.getArgs()[1];
        int id = Integer.parseInt(invocation.getArgs()[2]);
        int amount = Integer.parseInt(invocation.getArgs()[3]);

        if (RubiconBot.getRPGItemRegistry().getItemById(id) == null)
            return message(error("Invalid ItemId!", "This item does not exits."));

        RPGInventoryItem item = new RPGInventoryItem(RubiconBot.getRPGItemRegistry().getItemById(id), amount);

        switch (command) {
            case "add":
            case "a":
                inventory.addItem(item);
                return message(success("Added Item!", String.format("Successfully added `%d x %s` to %s", amount, item.getRpgItem().getDisplayName(), user.getAsMention())));
            case "remove":
            case "r":
                inventory.removeItem(item);
                return message(success("Removed Item!", String.format("Successfully removed `%d x %s` of %s", amount, item.getRpgItem().getDisplayName(), user.getAsMention())));
        }
        return null;
    }
}
