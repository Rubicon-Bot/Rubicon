package fun.rubicon.listener;

import fun.rubicon.core.Main;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.listener
 */
public class AdminListener extends ListenerAdapter{
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        if (event.getRoles().get(0).hasPermission(Permission.ADMINISTRATOR)){
            if (!event.getMember().isOwner()){
                Main.getMySQL().updateMemberValue(event.getMember(),"permissionlevel", "2");
            }
        }
    }
}
