package fun.rubicon.features.portal;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.entities.RubiconGuild;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class PortalMessageListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(event.getAuthor().isFake() || event.getAuthor().isBot() || event.getAuthor().equals(RubiconBot.getSelfUser()))
            return;
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(event.getGuild());

        if(rubiconGuild.hasPortal()) {
            PortalManager portalManager = new PortalManager();
            Portal portal = portalManager.getPortalByOwner(rubiconGuild.getPortalRoot());
            if(portal.containsChannel(event.getChannel())) {
                portal.broadcast(event.getChannel().getId(), event.getMessage().getContentDisplay(), event.getAuthor().getName(), event.getAuthor().getAvatarUrl(), event.getGuild().getName());
            }
        }
        super.onGuildMessageReceived(event);
    }
}
