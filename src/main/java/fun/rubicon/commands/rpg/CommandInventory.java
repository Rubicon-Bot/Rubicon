package fun.rubicon.commands.rpg;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.rpg.inventory.RPGInventory;
import fun.rubicon.core.rpg.inventory.RPGInventoryItem;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandInventory extends CommandHandler {

    public CommandInventory() {
        super(new String[]{"inventory", "inv"}, CommandCategory.RPG, new PermissionRequirements("inventory", false, true), "Shows your inventory.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        RPGInventory inventory = RPGInventory.fromUser(invocation.getAuthor());
        List<RPGInventoryItem> items = inventory.getItems();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Colors.COLOR_SECONDARY);
        embed.setAuthor(invocation.getAuthor().getName() + invocation.translate("command.inv.title"), null, invocation.getAuthor().getAvatarUrl());
        embed.setDescription(invocation.translate("command.inv.total") + ": " + inventory.getSlotCount() + "\n" +
                invocation.translate("command.inv.free") + ": " + inventory.getFreeSpace() + "\n" +
                invocation.translate("command.inv.used") + items.size());
        embed.addField(invocation.translate("command.inv.helmet"), inventory.getHelmet().getRpgItem().getDisplayName(), true);
        embed.addField(invocation.translate("command.inv.chest"), inventory.getChest().getRpgItem().getDisplayName(), true);
        embed.addBlankField(true);
        embed.addField(invocation.translate("command.inv.pants"), inventory.getPants().getRpgItem().getDisplayName(), true);
        embed.addField(invocation.translate("command.inv.shoes"), inventory.getShoes().getRpgItem().getDisplayName(), true);

        if(items.size() > 0) {
            StringBuilder slotString = new StringBuilder();
            for (RPGInventoryItem item : items) {
                slotString.append(item.getAmount() + " x " + item.getRpgItem().getDisplayName() + "\n");
            }
            embed.addField(invocation.translate("command.inv.items"), slotString.toString(), false);
        } else
            embed.addField(invocation.translate("command.inv.items"), "Empty", false);
        return message(embed);
    }
}
