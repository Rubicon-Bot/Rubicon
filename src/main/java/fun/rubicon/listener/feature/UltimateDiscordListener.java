package fun.rubicon.listener.feature;

import com.google.gson.JsonElement;
import fun.rubicon.RubiconBot;
import fun.rubicon.mysql.MySQL;
import fun.rubicon.util.Logger;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class UltimateDiscordListener extends ListenerAdapter {

    MySQL mysql = RubiconBot.getMySQL();
    JSONParser parser = new JSONParser();

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        String joinmessage = getValue("minecraft_server_settings", event.getGuild().getIdLong(), "joinmessage");
        if(joinmessage == "")
            return;
        try{
            TextChannel channel = event.getGuild().getTextChannelById(getValue("minecraft_server_settings", event.getGuild().getIdLong(), "joinmsgchannel"));
            SafeMessage.sendMessage(channel, joinmessage);
        } catch (Exception ignored){

        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String verifychannel = getValue("minecraft_server_settings", event.getGuild().getIdLong(), "verifychannel");
        if(verifychannel.equals(event.getChannel().getId())){
            try {
                if (keyIsValid(event.getMessage().getContentDisplay())) {
                    String roleConfig = getValue("minecraft_servers", event.getGuild().getIdLong(), "config");
                    JSONObject obj = (JSONObject) parser.parse(roleConfig);
                    String[] roles = getValueByCode("minecraft_users", event.getMessage().getContentDisplay(), "roles").split(",");
                    List<Role> roleList = new ArrayList<>();
                    Arrays.asList(roles).forEach(r -> {
                        if (obj.containsKey(r)){
                            Role role = event.getGuild().getRoleById(obj.get(r).toString());
                            roleList.add(role);
                        }
                    });
                    roleList.add(event.getGuild().getRoleById(obj.get("verified").toString()));
                    event.getGuild().getController().addRolesToMember(event.getMember(), roleList).queue();
                } else {
                    SafeMessage.sendMessage(event.getChannel(), "Your key is invalid");
                }
            } catch (Exception ignored){}
        }
    }

    private String getValue(String table, Long id, String key){
        try {
            PreparedStatement ps = mysql.prepareStatement("SELECT ? FROM ? WHERE discordid = ?");
            ps.setString(1, key);
            ps.setString(2, table);
            ps.setLong(3, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                return rs.getString(key);
        } catch (SQLException e) {
            Logger.error(e);
            return null;
        }
        return null;
    }

    private String getValueByCode(String table, String code, String key){
        try {
            PreparedStatement ps = mysql.prepareStatement("SELECT ? FROM ? WHERE code = ?");
            ps.setString(1, key);
            ps.setString(2, table);
            ps.setString(3, code);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                return rs.getString(key);
        } catch (SQLException e) {
            Logger.error(e);
            return null;
        }
        return null;
    }

    private boolean keyIsValid(String key){
        try {
            PreparedStatement ps = mysql.prepareStatement("SELECT * FROM minecraft_users WHERE code = ?");
            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            Logger.error(e);
            return false;
        }
    }

}
