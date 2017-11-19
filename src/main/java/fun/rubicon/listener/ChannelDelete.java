package fun.rubicon.listener;

import fun.rubicon.util.Info;
import net.dv8tion.jda.core.events.channel.category.CategoryDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.listener
 */
public class ChannelDelete extends ListenerAdapter {

    @Override
    public void onCategoryDelete(CategoryDeleteEvent e) {
        if(e.getCategory().getName().contains(Info.BOT_NAME)) {
            e.getGuild().getOwner().getUser().openPrivateChannel().queue();
        }
    }
}
