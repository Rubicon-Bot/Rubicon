package fun.rubicon.commands.general;

import de.foryasee.httprequest.*;
import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

import java.util.Date;

import static fun.rubicon.util.EmbedUtil.message;

/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */
public class CommandYouTube extends CommandHandler {

    public CommandYouTube() {
        super(new String[]{"youtube"}, CommandCategory.GENERAL, new PermissionRequirements("youtube", false, true), "Search for YouTube Videos or announce your newest!", "search <Search-Term>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {

        if(invocation.getArgs().length<1)
            return message(EmbedUtil.error("Invalid parameters","Use `rc!help youtube` for more info!"));

        switch (invocation.getArgs()[0]){
            case "setmessage":
            case "message":
                if(!RubiconUser.fromUser(invocation.getAuthor()).isPremium())
                    return message(EmbedUtil.error("No Premium","Sorry, but you have no Premium."));
                if(!userPermissions.hasPermissionNode("youtube.message"))
                    return message(EmbedUtil.no_permissions("youtube.message"));
                if (RubiconBot.getMySQL() != null) {
                    if (invocation.getArgs().length<3)
                        return message(EmbedUtil.error("Invalid parameters","Use `rc!help youtube` for more info!"));
                    try {
                        String message = invocation.getArgsString().replace(invocation.getArgs()[0],"");
                        PreparedStatement ps = RubiconBot.getMySQL().prepareStatement("UPDATE `guilds` SET `youmsg`=? WHERE `serverid`=?");
                                ps.setString(1,message);
                                ps.setLong(2,invocation.getGuild().getIdLong());
                                ps.execute();
                                return message(EmbedUtil.success("Successfully set Message","Message was set to `"+message+"`"));
                    } catch (SQLException | NullPointerException e) {
                        e.printStackTrace();
                        return message(EmbedUtil.error());
                    }
                }
                break;
        }
        return message(EmbedUtil.error("Invalid parameters","Use `rc!help youtube` for more info!"));

    }

}
