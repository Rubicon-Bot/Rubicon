/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.entities;

import fun.rubicon.RubiconBot;
import fun.rubicon.commands.settings.CommandJoinMessage;
import fun.rubicon.commands.settings.CommandLeaveMessage;
import fun.rubicon.mysql.MySQL;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class RubiconGuild {

    private Guild guild;
    private MySQL mySQL;

    public RubiconGuild(Guild guild) {
        this.guild = guild;
        this.mySQL = RubiconBot.getMySQL();

        createIfNotExist();
    }

    public Guild getGuild() {
        return guild;
    }

    public void setPrefix(String prefix) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("UPDATE guilds SET prefix=? WHERE serverid=?");
            ps.setString(1, prefix);
            ps.setLong(2, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public String getPrefix() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT prefix FROM guilds WHERE serverid = ?");
            ps.setLong(1, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("prefix") : null;
        } catch (SQLException e) {
            Logger.error(e);
        }
        return null;
    }

    public void delete() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("DELETE FROM guilds WHERE serverid=?");
            ps.setLong(1, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    private boolean exist() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT id FROM guilds WHERE serverid = ?");
            ps.setLong(1, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            Logger.error(e);
        }
        return false;
    }

    private void createIfNotExist() {
        if (exist())
            return;
        try {
            PreparedStatement ps = mySQL.prepareStatement("INSERT INTO guilds(`serverid`, `prefix`) VALUES (?, ?)");
            ps.setLong(1, guild.getIdLong());
            ps.setString(2, Info.BOT_DEFAULT_PREFIX);
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public boolean useMuteSettings() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT * FROM mutesettings WHERE serverid = ?");
            ps.setLong(1, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            Logger.error(e);
            return false;
        }
    }

    public RubiconGuild insertMuteTable() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("INSERT INTO mutesettings(`serverid`,`mutedmsg`,`unmutemsg`,`channel`) VALUES (?, '', '', '0')");
            ps.setLong(1, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
        return this;
    }

    public TextChannel getMuteChannel() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT channel FROM mutesettings WHERE serverid =?");
            ps.setLong(1, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return guild.getTextChannelById(rs.getLong("channel"));
        } catch (SQLException e) {
            Logger.error(e);
        }
        return null;
    }

    public String getMuteMessage() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT mutedmsg FROM mutesettings WHERE serverid=?");
            ps.setLong(1, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getString("mutedmsg");
        } catch (SQLException e) {
            Logger.error(e);
            return null;
        }
        return null;
    }

    public String getUnmuteMessage() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT unmutemsg FROM mutesettings WHERE serverid=?");
            ps.setLong(1, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getString("unmutemsg");
        } catch (SQLException e) {
            Logger.error(e);
            return null;
        }
        return null;
    }

    public boolean isMutedChannel(long channelId) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT channel FROM mutesettings WHERE serverid=?");
            ps.setLong(1, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getString("channel").equals(String.valueOf(channelId));
        } catch (SQLException e) {
            Logger.error(e);
        }
        return false;
    }

    public void deleteMuteSettings() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("DELETE FROM mutesettings WHERE serverid=?");
            ps.setLong(1, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public boolean hasJoinMessagesEnabled() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT * FROM joinmessages WHERE serverid=?");
            ps.setLong(1, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            Logger.error(e);
        }
        return false;
    }

    public void setJoinMessage(String text, long channelId) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("INSERT INTO joinmessages (serverid, message, channel) VALUES (?, ?, ?)");
            ps.setLong(1, guild.getIdLong());
            ps.setString(2, text);
            ps.setLong(3, channelId);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CommandJoinMessage.JoinMessage getJoinMessage() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT channel, message FROM joinmessages WHERE serverid=?");
            ps.setLong(1, guild.getIdLong());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next())
                return new CommandJoinMessage.JoinMessage(resultSet.getLong("channel"), resultSet.getString("message"));
        } catch (SQLException e) {
            Logger.error(e);
        }
        return null;
    }

    public void setJoinMessage(String text) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("UPDATE joinmessages SET message=? WHERE serverid=?");
            ps.setString(1, text);
            ps.setLong(2, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setJoinMessage(long channel) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("UPDATE joinmessages SET channel=? WHERE serverid=?");
            ps.setLong(1, channel);
            ps.setLong(2, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteJoinMessage() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("DELETE FROM joinmessages WHERE serverid=?");
            ps.setLong(1, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
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

    // Leavemessages
    public boolean hasLeaveMessagesEnabled() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT * FROM leavemessages WHERE serverid=?");
            ps.setLong(1, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            Logger.error(e);
        }
        return false;
    }

    public void setLeaveMessage(String text, long channelId) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("INSERT INTO leavemessages (serverid, message, channel) VALUES (?, ?, ?)");
            ps.setLong(1, guild.getIdLong());
            ps.setString(2, text);
            ps.setLong(3, channelId);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CommandLeaveMessage.LeaveMessage getLeaveMessage() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT channel, message FROM leavemessages WHERE serverid=?");
            ps.setLong(1, guild.getIdLong());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next())
                return new CommandLeaveMessage.LeaveMessage(resultSet.getLong("channel"), resultSet.getString("message"));
        } catch (SQLException e) {
            Logger.error(e);
        }
        return null;
    }

    public void setLeaveMessage(String text) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("UPDATE leavemessages SET message=? WHERE serverid=?");
            ps.setString(1, text);
            ps.setLong(2, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setLeaveMessage(long channel) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("UPDATE leavemessages SET channel=? WHERE serverid=?");
            ps.setLong(1, channel);
            ps.setLong(2, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteLeaveMessage() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("DELETE FROM leavemessages WHERE serverid=?");
            ps.setLong(1, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public boolean isAutochannel(long channelId) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT id FROM autochannels WHERE channelId=?");
            ps.setLong(1, channelId);
            ResultSet rs = ps.executeQuery();
            rs.next();
        } catch (SQLException e) {
            Logger.error(e);
        }
        return false;
    }

    public void addAutochannel(long channelId) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("INSERT INTO autochannels (serverId, channelId) VALUES (?, ?)");
            ps.setLong(1, guild.getIdLong());
            ps.setLong(2, channelId);
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public void deleteAutochannel(long channelId) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("DELETE FROM autochannels WHERE channelId=?");
            ps.setLong(1, channelId);
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public List<Long> getAutochannels() {
        List<Long> channelIds = new ArrayList<>();
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT channelId FROM autochannels WHERE serverId=?");
            ps.setLong(1, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                channelIds.add(rs.getLong("channelId"));
            }
        } catch (SQLException e) {
            Logger.error(e);
        }
        return channelIds;
    }

    // Autoroles
    public void setAutorole(long roleId) {
        if(hasAutoroleEnabled())
            disableAutorole();
        try {
            PreparedStatement ps = mySQL.prepareStatement("INSERT INTO autoroles (serverId, roleId) VALUES (?, ?)");
            ps.setLong(1, guild.getIdLong());
            ps.setLong(2, roleId);
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public long getAutorole() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT roleId FROM autoroles WHERE serverId = ?");
            ps.setLong(1, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getLong("roleId");
        } catch (Exception e) {
            Logger.error(e);
        }
        return 0;
    }

    public boolean hasAutoroleEnabled() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT id FROM autoroles WHERE serverId = ?");
            ps.setLong(1, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            Logger.error(e);
        }
        return false;
    }

    public void disableAutorole() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("DELETE FROM autoroles WHERE serverId = ?");
            ps.setLong(1, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public static RubiconGuild fromGuild(Guild guild) {
        return new RubiconGuild(guild);
    }
}
