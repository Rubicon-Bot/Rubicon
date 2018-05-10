package fun.rubicon.commands.botowner;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandTest extends CommandHandler {

    public CommandTest() {
        super(new String[]{"test"}, CommandCategory.BOT_OWNER, new PermissionRequirements("test", true, false), "Test Command", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        /*RPGInventory inventory = RPGInventory.fromUser(invocation.getAuthor());
        if(invocation.getArgs().length != 3)
            return createHelpMessage();
        switch (invocation.getArgs()[0]) {
            case "add":
                inventory.addItem(new RPGInventoryItem(Integer.parseInt(invocation.getArgs()[1]), Integer.parseInt(invocation.getArgs()[2])));
                return message(success("Added", "Successfully added item."));
            case "remove":
                inventory.addItem(new RPGInventoryItem(Integer.parseInt(invocation.getArgs()[1]), Integer.parseInt(invocation.getArgs()[2])));
                return message(success("Removed", "Successfully removed item."));
        }*/
        return null;
    }
}
