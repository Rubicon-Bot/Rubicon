package fun.rubicon.entities.impl;

import fun.rubicon.entities.Member;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import javax.annotation.Nullable;
import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class MemberImpl implements Member {

    private net.dv8tion.jda.core.entities.Member member;

    public MemberImpl(net.dv8tion.jda.core.entities.Member member) {
        this.member = member;
    }

    @Override
    public User getUser() {
        return member.getUser();
    }

    @Override
    public Guild getGuild() {
        return member.getGuild();
    }

    @Override
    public JDA getJDA() {
        return member.getJDA();
    }

    @Override
    public OffsetDateTime getJoinDate() {
        return member.getJoinDate();
    }

    @Override
    public GuildVoiceState getVoiceState() {
        return member.getVoiceState();
    }

    @Override
    public Game getGame() {
        return member.getGame();
    }

    @Override
    public OnlineStatus getOnlineStatus() {
        return member.getOnlineStatus();
    }

    @Override
    public String getNickname() {
        return member.getNickname();
    }

    @Override
    public String getEffectiveName() {
        return member.getEffectiveName();
    }

    @Override
    public List<Role> getRoles() {
        return member.getRoles();
    }

    @Override
    public Color getColor() {
        return member.getColor();
    }

    @Override
    public int getColorRaw() {
        return member.getColorRaw();
    }

    @Override
    public List<Permission> getPermissions(Channel channel) {
        return member.getPermissions(channel);
    }

    @Override
    public boolean canInteract(net.dv8tion.jda.core.entities.Member member) {
        return member.canInteract(member);
    }

    @Override
    public boolean canInteract(Role role) {
        return member.canInteract(role);
    }

    @Override
    public boolean canInteract(Emote emote) {
        return member.canInteract(emote);
    }

    @Override
    public boolean isOwner() {
        return member.isOwner();
    }

    @Nullable
    @Override
    public TextChannel getDefaultChannel() {
        return member.getDefaultChannel();
    }

    @Override
    public String getAsMention() {
        return member.getAsMention();
    }

    @Override
    public List<Permission> getPermissions() {
        return member.getPermissions();
    }

    @Override
    public boolean hasPermission(Permission... permissions) {
        return member.hasPermission(permissions);
    }

    @Override
    public boolean hasPermission(Collection<Permission> permissions) {
        return member.hasPermission(permissions);
    }

    @Override
    public boolean hasPermission(Channel channel, Permission... permissions) {
        return member.hasPermission(channel, permissions);
    }

    @Override
    public boolean hasPermission(Channel channel, Collection<Permission> permissions) {
        return member.hasPermission(channel, permissions);
    }
}
