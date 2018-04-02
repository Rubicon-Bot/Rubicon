package fun.rubicon.listener.feature;


import fun.rubicon.RubiconBot;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.core.entities.RubiconUser;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class PunishmentListener extends ListenerAdapter {

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        if(event.getRoles().contains(RubiconGuild.fromGuild(event.getGuild()).getMutedRole())){
            RubiconMember.fromMember(event.getMember()).unmute(false);
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        RubiconMember member = RubiconMember.fromMember(event.getMember());
        if(member.isMuted()){
            Role muted = RubiconGuild.fromGuild(event.getGuild()).getMutedRole();
            if(!event.getGuild().getSelfMember().canInteract(muted) || !event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES) || !event.getGuild().getSelfMember().canInteract(member.getMember())) {
                event.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage("ERROR: Unable to interact with rubicon muted role! Please move `Rubicon` role above `rubicon-muted` role and give Rubicon the `MANAGE_ROLES` permission").queue();
                return;
            }
            event.getGuild().getController().addSingleRoleToMember(member.getMember(), muted).queue();
        }
    }

    @Override
    public void onRoleDelete(RoleDeleteEvent event) {
        if(event.getRole().getName().equals("rubicon-muted")) {
            RubiconBot.getPunishmentManager().getMuteCache().keySet().forEach(m -> {
                if(m.getGuild().equals(event.getGuild()))
                    RubiconMember.fromMember(m).unmute(false);
            });
        }
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        RubiconUser.fromUser(event.getUser()).unban(event.getGuild());
    }
}
