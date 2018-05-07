/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.entities;

import com.rethinkdb.gen.ast.Filter;
import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.commands.settings.CommandJoinMessage;
import fun.rubicon.commands.settings.CommandLeaveMessage;
import fun.rubicon.rethink.Rethink;
import fun.rubicon.rethink.RethinkHelper;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class RubiconGuild extends RethinkHelper {

    private Guild guild;
    private Rethink rethink;
    private final Filter dbGuild;

    public RubiconGuild(Guild guild) {
        this.guild = guild;
        this.rethink = RubiconBot.getRethink();
        dbGuild = rethink.db.table("guilds").filter(rethink.rethinkDB.hashMap("guildId", guild.getId()));
        createIfNotExist();
        createPortalSettingsOfNotExist();
    }

    public Guild getGuild() {
        return guild;
    }

    public void setPrefix(String prefix) {
        dbGuild.update(rethink.rethinkDB.hashMap("prefix", prefix)).run(rethink.getConnection());
    }

    public String getPrefix() {
        String prefix = getString(retrieve(), "prefix");
        return prefix == null ? Info.BOT_DEFAULT_PREFIX : prefix;
    }

    public void deleteMuteSettings() {
        rethink.db.table("mutesettings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection());
    }

    public boolean hasJoinMessagesEnabled() {
        return exist(rethink.db.table("joinmessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection()));
    }

    public void setJoinMessage(String text, String channelId) {
        if (!hasJoinMessagesEnabled())
            rethink.db.table("joinmessages").insert(
                    rethink.rethinkDB.array(
                            rethink.rethinkDB.hashMap("guildId", guild.getId())
                                    .with("message", text)
                                    .with("channel", channelId)
                    )).run(rethink.getConnection());
        else
            rethink.db.table("joinmessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(
                    rethink.rethinkDB.hashMap("message", text)
                            .with("channel", channelId)
            ).run(rethink.getConnection());
    }

    public CommandJoinMessage.JoinMessage getJoinMessage() {
        Cursor cursor = rethink.db.table("joinmessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection());
        List<Map<String, String>> list = cursor.toList();
        if (list.size() == 0)
            return null;
        Map map = list.get(0);
        String message = (String) map.get("message");
        String channel = (String) map.get("channel");
        return new CommandJoinMessage.JoinMessage(channel, message);
    }

    public void setJoinMessage(String text) {
        rethink.db.table("joinmessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(rethink.rethinkDB.hashMap("message", text)).run(rethink.getConnection());
    }

    public void setJoinMessageChannel(String channel) {
        rethink.db.table("joinmessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(rethink.rethinkDB.hashMap("channel", channel)).run(rethink.getConnection());

    }

    public void deleteJoinMessage() {
        rethink.db.table("joinmessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).delete().run(rethink.getConnection());
    }


    public void enableJoinImages(String channelId) {
        rethink.db.table("joinimages").insert(rethink.rethinkDB.array(rethink.rethinkDB.hashMap("guildId", guild.getId()).with("channel", channelId))).run(rethink.getConnection());
    }

    public void disableJoinImages() {
        rethink.db.table("joinimages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).delete().run(rethink.getConnection());
    }

    public boolean hasJoinImagesEnabled() {
        return exist(rethink.db.table("joinimages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection()));
    }

    public String getJoinImageChannel() {
        return getString(rethink.db.table("joinimages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection()), "channel");
    }

    public Role getMutedRole() {
        if (!guild.getRolesByName("rubicon-muted", false).isEmpty())
            return guild.getRolesByName("rubicon-muted", false).get(0);
        if (!guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES, Permission.MANAGE_PERMISSIONS)) {
            guild.getOwner().getUser().openPrivateChannel().complete().sendMessage("ERROR: I can't create roles so you can't use mute feature! Please give me `MANAGE_ROLES` and `MANAGE_PERMISSIONS` Permission").queue();
            return null;
        }
        Role mute = guild.getController().createRole().setName("rubicon-muted").complete();
        if (!guild.getSelfMember().hasPermission(Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS)) {
            guild.getOwner().getUser().openPrivateChannel().complete().sendMessage("ERROR: I can't manage channels so you can't use mute feature! Please give me ``MANAGE_ROLES` and `MANAGE_PERMISSIONS` Permission").queue();
            return mute;
        }
        guild.getTextChannels().forEach(tc -> {
            if (guild.getSelfMember().hasPermission(tc, Permission.MANAGE_PERMISSIONS)) {
                if (tc.getPermissionOverride(mute) != null) return;
                PermissionOverride override = tc.createPermissionOverride(mute).complete();
                override.getManager().deny(Permission.MESSAGE_WRITE).queue();
            }
        });
        return mute;
    }

    public boolean hasLeaveMessagesEnabled() {
        return exist(rethink.db.table("leavemessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection()));
    }

    public void setLeaveMessage(String text, String channelId) {
        if (!hasLeaveMessagesEnabled())
            rethink.db.table("leavemessages").insert(
                    rethink.rethinkDB.array(
                            rethink.rethinkDB.hashMap("guildId", guild.getId())
                                    .with("message", text)
                                    .with("channel", channelId)
                    )).run(rethink.getConnection());
        else
            rethink.db.table("leavemessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(
                            rethink.rethinkDB.hashMap("message", text)
                                    .with("channel", channelId)
                    ).run(rethink.getConnection());
    }

    public CommandLeaveMessage.LeaveMessage getLeaveMessage() {
        Cursor cursor = rethink.db.table("leavemessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection());
        List<Map<String, String>> list = cursor.toList();
        if (list.size() == 0)
            return null;
        Map map = list.get(0);
        String message = (String) map.get("message");
        String channel = (String) map.get("channel");
        return new CommandLeaveMessage.LeaveMessage(channel, message);
    }

    public void setLeaveMessage(String text) {
        rethink.db.table("leavemessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(rethink.rethinkDB.hashMap("message", text)).run(rethink.getConnection());
    }

    public void setLeaveMessageChannel(String channel) {
        rethink.db.table("joinmessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(rethink.rethinkDB.hashMap("channel", channel)).run(rethink.getConnection());

    }

    public void deleteLeaveMessage() {
        rethink.db.table("leavemessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).delete().run(rethink.getConnection());
    }

    public boolean isAutochannel(String channelId) {
        return getAutochannels().contains(channelId);
    }

    public void addAutochannel(String channelId) {
        List<String> oldIds = getAutochannels();
        oldIds.add(channelId);
        if (autochannelEntryExist())
            updateAutochannels(oldIds);
        else
            rethink.db.table("autochannels").insert(rethink.rethinkDB.hashMap("guildId", guild.getId()).with("channels", oldIds)).run(rethink.getConnection());
    }

    public void deleteAutochannel(String channelId) {
        List<String> list = getAutochannels();
        list.remove(channelId);
        updateAutochannels(list);
    }

    private void updateAutochannels(List<String> list) {
        rethink.db.table("autochannels").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(rethink.rethinkDB.hashMap("channels", list)).run(rethink.getConnection());

    }

    public List<String> getAutochannels() {
        Cursor cursor = rethink.db.table("autochannels").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection());
        List<?> list = cursor.toList();
        if (list.isEmpty())
            return new ArrayList<>();
        return ((HashMap<String, List<String>>) list.get(0)).get("channels");
    }

    private boolean autochannelEntryExist() {
        Cursor cursor = rethink.db.table("autochannels").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection());
        return cursor.toList().size() > 0;
    }

    // Autoroles
    public void setAutorole(String roleId) {
        if (hasAutoroleEnabled())
            disableAutorole();
        rethink.db.table("autoroles").insert(rethink.rethinkDB.array(rethink.rethinkDB.hashMap("guildId", guild.getId()).with("role", roleId))).run(rethink.getConnection());
    }

    public String getAutorole() {
        return getString(rethink.db.table("autoroles").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection()), "role");
    }

    public boolean hasAutoroleEnabled() {
        return exist(rethink.db.table("autoroles").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection()));
    }

    public void disableAutorole() {
        rethink.db.table("autoroles").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).delete().run(rethink.getConnection());
    }

    private Cursor retrieve() {
        return dbGuild.run(rethink.getConnection());
    }

    public boolean isVerificationEnabled() {
        Cursor cursor = rethink.db.table("verification_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection());
        return cursor.toList().size() >= 1;
    }

    public void disableVerification() {
        rethink.db.table("verification_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).delete().run(rethink.getConnection());
    }

    public TextChannel getVerificationChannel() {
        Cursor cursor = rethink.db.table("verification_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection());
        Map map = (Map) cursor.toList().get(0);
        String channelId = (String) map.get("channelId");
        return guild.getTextChannelById(channelId);
    }

    public Role getVerificationRole() {
        Cursor cursor = rethink.db.table("verification_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection());
        Map map = (Map) cursor.toList().get(0);
        String channelId = (String) map.get("roleId");
        return guild.getRoleById(channelId);
    }

    public String getVerificationKickText() {
        Cursor cursor = rethink.db.table("verification_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection());
        Map map = (Map) cursor.toList().get(0);
        return (String) map.get("kickText");
    }

    public boolean hasPortal() {
        return getString(retrieve(), "portal") != null;
    }

    public void setPortal(String rootGuildId) {
        dbGuild.update(rethink.rethinkDB.hashMap("portal", rootGuildId)).run(rethink.getConnection());
    }

    public String getPortalRoot() {
        return getString(retrieve(), "portal");
    }

    public void closePortal() {
        dbGuild.update(rethink.rethinkDB.hashMap("portal", null)).run(rethink.getConnection());
    }

    public void setPortalEmbeds(boolean state) {
        rethink.db.table("portal_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId()))
                .update(rethink.rethinkDB.hashMap("embeds", state)).run(rethink.getConnection());
    }

    public void setPortalInvites(boolean state) {
        rethink.db.table("portal_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId()))
                .update(rethink.rethinkDB.hashMap("invites", state)).run(rethink.getConnection());
    }

    public boolean allowsPortalInvites() {
        return getBoolean(rethink.db.table("portal_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection()), "invites");
    }

    public boolean hasPortalEmbedsEnables() {
        return getBoolean(rethink.db.table("portal_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection()), "embeds");
    }

    public void deletePortalSettings() {
        rethink.db.table("portal_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).delete().run(rethink.getConnection());
    }

    private void createPortalSettingsOfNotExist() {
        Cursor cursor = rethink.db.table("portal_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection());
        if (cursor.toList().size() == 0)
            rethink.db.table("portal_settings").insert(rethink.rethinkDB.array(rethink.rethinkDB.hashMap("guildId", guild.getId()).with("invites", true))).run(rethink.getConnection());
    }

    public void delete() {
        dbGuild.delete().run(rethink.getConnection());
        deletePortalSettings();
    }

    private boolean exist() {
        return retrieve().toList().size() != 0;
    }

    private void createIfNotExist() {
        if (exist())
            return;
        rethink.db.table("guilds").insert(rethink.rethinkDB.array(rethink.rethinkDB.hashMap("guildId", guild.getId()))).run(rethink.getConnection());
    }

    public boolean isBeta() {
        Cursor cursor = rethink.db.table("guilds").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection());
        Map map = (Map) cursor.toList().get(0);
        return map.get("beta") != null;
    }

    public void setBeta(boolean state) {
        if (state) {
            rethink.db.table("guilds").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(rethink.rethinkDB.hashMap("beta", 1)).run(rethink.getConnection());
        } else
            rethink.db.table("guilds").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(rethink.rethinkDB.hashMap("beta", null)).run(rethink.getConnection());
    }

    private List<String> getRankIDs() {
        List<String> idList = new ArrayList<>();
        Cursor cursor = rethink.db.table("guilds").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection());
        Map map = (Map) cursor.toList().get(0);
        return ((List<List<String>>) map.get("ranks")).get(0);
    }

    public List<Role> getRanks() {
        checkRanks();
        List<Role> roles = new ArrayList<>();
        getRankIDs().forEach(id -> roles.add(guild.getRoleById(id)));
        return roles;
    }

    public void checkRanks() {
        if (!useRanks()) return;
        List<String> idList = getRankIDs();
        getRankIDs().forEach(id -> {
            Role role = guild.getRoleById(id);
            if (role == null)
                idList.remove(id);
        });
        updateRanks(idList);
    }

    public boolean isRank(Role role) {
        return getRankIDs().contains(role.getId());
    }

    public boolean useRanks() {
        if (getRankIDs() == null) return false;
        return !getRankIDs().isEmpty();
    }

    public void allowRank(Role role) {
        List<String> list;
        if (useRanks())
            list = getRankIDs();
        else
            list = new ArrayList<>();
        list.add(role.getId());
        updateRanks(list);
    }

    public void disallowRank(Role role) {
        List<String> list = getRankIDs();
        list.remove(role.getId());
        updateRanks(list);
    }

    private void updateRanks(List<String> idList) {
        rethink.db.table("guilds").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(rethink.rethinkDB.hashMap("ranks", rethink.rethinkDB.array(idList))).run(rethink.getConnection());
    }

    public void deletePoll(){
        rethink.db.table("votes").filter(rethink.rethinkDB.hashMap("guild", guild.getId())).delete().run(rethink.getConnection());
    }

    public static RubiconGuild fromGuild(Guild guild) {
        return new RubiconGuild(guild);
    }
}
