package fun.rubicon.listener.role;

import fun.rubicon.core.entities.RubiconGuild;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class RoleDeleteListener extends ListenerAdapter {

    @Override
    public void onRoleDelete(RoleDeleteEvent event) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(event.getGuild());
        if(rubiconGuild.hasAutoroleEnabled()) {
            if(event.getRole().getId().equals(rubiconGuild.getAutorole()))
                rubiconGuild.disableAutorole();
        }
    }
}
