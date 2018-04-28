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
        embed.setAuthor(invocation.getAuthor().getName() + "`s Inventory", null, invocation.getAuthor().getAvatarUrl());
        embed.setDescription("Total Slots: " + inventory.getSlotCount() + "\n" +
                "Free Slots: " + inventory.getFreeSpace() + "\n" +
                "Used Slots: " + items.size());
        embed.addField("Helmet", inventory.getHelmet().getRpgItem().getDisplayName(), true);
        embed.addField("Chest", inventory.getChest().getRpgItem().getDisplayName(), true);
        embed.addBlankField(true);
        embed.addField("Pants", inventory.getPants().getRpgItem().getDisplayName(), true);
        embed.addField("Shoes", inventory.getShoes().getRpgItem().getDisplayName(), true);

        StringBuilder slotString = new StringBuilder();
        for(RPGInventoryItem item : items) {
            slotString.append(item.getAmount() + " x " + item.getRpgItem().getDisplayName() + "\n");
        }
        embed.addBlankField(true);
        embed.addField("Items", slotString.toString(), false);
        return message(embed);
    }
}
