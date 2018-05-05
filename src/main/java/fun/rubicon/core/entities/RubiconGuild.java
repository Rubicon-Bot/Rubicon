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
    }

    public Guild getGuild() {
        return guild;
    }

    public void setPrefix(String prefix) {
        dbGuild.update(rethink.rethinkDB.hashMap("prefix", prefix)).run(rethink.connection);
    }

    public String getPrefix() {
        String prefix = getString(retrieve(), "prefix");
        return prefix.equals("") ? Info.BOT_DEFAULT_PREFIX : prefix;
    }

    public void deleteMuteSettings() {
        rethink.db.table("mutesettings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.connection);
    }

    public boolean hasJoinMessagesEnabled() {
        return exist(rethink.db.table("joinmessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.connection));
    }

    public void setJoinMessage(String text, long channelId) {
        rethink.db.table("joinmessages").insert(
                rethink.rethinkDB.array(
                        rethink.rethinkDB.hashMap("guildId", guild.getId())
                                .with("message", text)
                                .with("channel", channelId)
                )).run(rethink.connection);
    }

    public CommandJoinMessage.JoinMessage getJoinMessage() {
        Cursor cursor = rethink.db.table("joinmessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.connection);
        String message = getString(cursor, "message");
        long channel = getLong(cursor, "channel");
        return new CommandJoinMessage.JoinMessage(channel, message);
    }

    public void setJoinMessage(String text) {
        rethink.db.table("joinmessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(rethink.rethinkDB.hashMap("message", text)).run(rethink.connection);
    }

    public void setJoinMessage(long channel) {
        rethink.db.table("joinmessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(rethink.rethinkDB.hashMap("channel", channel)).run(rethink.connection);

    }

    public void deleteJoinMessage() {
        rethink.db.table("joinmessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).delete().run(rethink.connection);
    }


    public void enableJoinImages(String channelId) {
        rethink.db.table("joinimages").insert(rethink.rethinkDB.array(rethink.rethinkDB.hashMap("guildId", guild.getId()).with("channel", channelId))).run(rethink.connection);
    }

    public void disableJoinImages() {
        rethink.db.table("joinimages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).delete().run(rethink.connection);
    }

    public boolean hasJoinImagesEnabled() {
        return exist(rethink.db.table("joinimages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.connection));
    }

    public String getJoinImageChannel() {
        return getString(rethink.db.table("joinimages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.connection), "channel");
    }

    public Role getMutedRole() {
        if (!guild.getRolesByName("rubicon-muted", false).isEmpty())
            return guild.getRolesByName("rubicon-muted", false).get(0);
        if (!guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
            guild.getOwner().getUser().openPrivateChannel().complete().sendMessage("ERROR: I can't create roles so you can't use mute feature! Please give me `MANAGE_ROLES` Permission").queue();
            return null;
        }
        Role mute = guild.getController().createRole().setName("rubicon-muted").complete();
        if (!guild.getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            guild.getOwner().getUser().openPrivateChannel().complete().sendMessage("ERROR: I can't manage channels so you can't use mute feature! Please give me `MANAGE_CHANNELS` Permission").queue();
            return mute;
        }
        guild.getTextChannels().forEach(tc -> {
            if (tc.getPermissionOverride(mute) != null) return;
            PermissionOverride override = tc.createPermissionOverride(mute).complete();
            override.getManager().deny(Permission.MESSAGE_WRITE).queue();
        });
        return mute;
    }

    public boolean hasLeaveMessagesEnabled() {
        return exist(rethink.db.table("leavemessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.connection));
    }

    public void setLeaveMessage(String text, long channelId) {
        rethink.db.table("leavemessages").insert(
                rethink.rethinkDB.array(
                        rethink.rethinkDB.hashMap("guildId", guild.getId())
                                .with("message", text)
                                .with("channel", channelId)
                )).run(rethink.connection);
    }

    public CommandLeaveMessage.LeaveMessage getLeaveMessage() {
        Cursor cursor = rethink.db.table("leavemessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.connection);
        String message = getString(cursor, "message");
        long channel = getLong(cursor, "channel");
        return new CommandLeaveMessage.LeaveMessage(channel, message);
    }

    public void setLeaveMessage(String text) {
        rethink.db.table("leavemessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(rethink.rethinkDB.hashMap("message", text)).run(rethink.connection);
    }

    public void setLeaveMessage(long channel) {
        rethink.db.table("joinmessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(rethink.rethinkDB.hashMap("channel", channel)).run(rethink.connection);

    }

    public void deleteLeaveMessage() {
        rethink.db.table("leavemessages").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).delete().run(rethink.connection);
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
            rethink.db.table("autochannels").insert(rethink.rethinkDB.hashMap("guildId", guild.getId()).with("channels", oldIds)).run(rethink.connection);
    }

    public void deleteAutochannel(String channelId) {
        List<String> list = getAutochannels();
        list.remove(channelId);
        updateAutochannels(list);
    }

    private void updateAutochannels(List<String> list) {
        rethink.db.table("autochannels").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(rethink.rethinkDB.hashMap("channels", list)).run(rethink.connection);

    }

    public List<String> getAutochannels() {
        Cursor cursor = rethink.db.table("autochannels").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.connection);
        List<?> list = cursor.toList();
        if(list.isEmpty())
            return new ArrayList<>();
        List<String> channels = ((HashMap<String, List<String>>) list.get(0)).get("channels");
        return channels;
    }

    private boolean autochannelEntryExist() {
        Cursor cursor = rethink.db.table("autochannels").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.connection);
        return cursor.toList().size() > 0;
    }

    // Autoroles
    public void setAutorole(String roleId) {
        if (hasAutoroleEnabled())
            disableAutorole();
        rethink.db.table("autoroles").insert(rethink.rethinkDB.array(rethink.rethinkDB.hashMap("guildId", guild.getId()).with("role", roleId))).run(rethink.connection);
    }

    public String getAutorole() {
        return getString(rethink.db.table("autoroles").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.connection), "role");
    }

    public boolean hasAutoroleEnabled() {
        return exist(rethink.db.table("autoroles").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.connection));
    }

    public void disableAutorole() {
        rethink.db.table("autoroles").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).delete().run(rethink.connection);
    }

    public void delete() {
        dbGuild.delete().run(rethink.connection);
    }

    private boolean exist() {
        return retrieve().toList().size() != 0;
    }

    private void createIfNotExist() {
        if (exist())
            return;
        rethink.db.table("guilds").insert(rethink.rethinkDB.array(rethink.rethinkDB.hashMap("guildId", guild.getId()))).run(rethink.connection);
    }

    private Cursor retrieve() {
        return dbGuild.run(rethink.connection);
    }

    public boolean isVerificationEnabled() {
        Cursor cursor = rethink.db.table("verification_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.connection);
        return cursor.toList().size() >= 1;
    }

    public void disableVerification() {
        rethink.db.table("verification_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).delete().run(rethink.connection);
    }

    public TextChannel getVerificationChannel() {
        Cursor cursor = rethink.db.table("verification_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.connection);
        Map map = (Map) cursor.toList().get(0);
        String channelId = (String) map.get("channelId");
        return guild.getTextChannelById(channelId);
    }

    public Role getVerificationRole() {
        Cursor cursor = rethink.db.table("verification_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.connection);
        Map map = (Map) cursor.toList().get(0);
        String channelId = (String) map.get("roleId");
        return guild.getRoleById(channelId);
    }

    public String getVerificationKickText() {
        Cursor cursor = rethink.db.table("verification_settings").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.connection);
        Map map = (Map) cursor.toList().get(0);
        return (String) map.get("kickText");
    }

    public boolean isBeta() {
        Cursor cursor = rethink.db.table("guilds").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.connection);
        Map map = (Map) cursor.toList().get(0);
        return map.get("beta") != null;
    }

    public void setBeta(boolean state) {
        if (state) {
            rethink.db.table("guilds").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(rethink.rethinkDB.hashMap("beta", 1)).run(rethink.connection);
        } else
            rethink.db.table("guilds").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).update(rethink.rethinkDB.hashMap("beta", null)).run(rethink.connection);
    }

    public static RubiconGuild fromGuild(Guild guild) {
        return new RubiconGuild(guild);
    }
}
