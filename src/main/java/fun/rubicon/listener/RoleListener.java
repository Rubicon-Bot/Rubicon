package fun.rubicon.listener;

import fun.rubicon.commands.moderation.CommandRanks;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class RoleListener extends ListenerAdapter {

    @Override
    public void onRoleUpdateName(RoleUpdateNameEvent event) {
        CommandRanks.handleRoleModification(event);
    }

    @Override
    public void onRoleDelete(RoleDeleteEvent event) {
        CommandRanks.handleRoleDeletion(event);
    }
}
