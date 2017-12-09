/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

/**
 * Handles the 'info' command.
 * @author Yannick Seeger / ForYaSee
 */
public class CommandInfo extends CommandHandler {


    public CommandInfo(){
        super(new String[] {"Info", "inf"}, CommandCategory.GENERAL, new PermissionRequirements(0, "command.info"), "Shows some information about the bot!", "info");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        //Set some Var´s
        Message message = parsedCommandInvocation.invocationMessage;
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.COLOR_PRIMARY);
        builder.setAuthor(Info.BOT_NAME + " - Info", "https://rubicon.fun", message.getJDA().getSelfUser().getEffectiveAvatarUrl());
        builder.setThumbnail("https://cdn.discordapp.com/attachments/381176080494624768/381176148828356608/13079-thumb.jpg");
        StringBuilder authors = new StringBuilder();

        //Append Id´s on StringBuilder
        for (long authorId : Info.BOT_AUTHOR_IDS) {
            User authorUser = RubiconBot.getJDA().getUserById(authorId);
            if (authorUser == null) // TODO use alternative way that does not need to have the authors in cache.
                authors.append(authorId).append("\n");
            else
                authors.append(authorUser.getName()).append("#").append(authorUser.getDiscriminator()).append("\n");
        }
        //Set the Embed Values
        builder.addField("Bot Name", Info.BOT_NAME, true);
        builder.addField("Bot Version", Info.BOT_VERSION, true);
        builder.addField("Website", "[Link](" + Info.BOT_WEBSITE + ")", true);
        builder.addField("Bot Invite", "[Invite Rubicon](https://discordapp.com/oauth2/authorize?client_id=380713705073147915&scope=bot&permissions=1898982486)", true);
        builder.addField("Github Link", "[Github Link](" + Info.BOT_GITHUB + ")", true);
        builder.addField("Patreon Link", "[Rubicon Dev Team](https://www.patreon.com/rubiconbot)", true);
        builder.addField("Authors", authors.toString(), true);
        String dependencies = "" +
                "[json.org](http://json.org/)\n" +
                "[JDA](https://github.com/DV8FromTheWorld/JDA)\n" +
                "[mysql-connector](https://mvnrepository.com/artifact/mysql/mysql-connector-java)\n" +
                "[slf4j-simple](https://mvnrepository.com/artifact/org.slf4j/slf4j-simple)\n" +
                "[json-simple](https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple)\n" +
                "[gson](https://github.com/google/gson)\n" +
                "[jsoup](https://jsoup.org/)";
        builder.addField("Dependencies", dependencies, false);
        //Send Message and delete it after 2 Minutes
        return new MessageBuilder().setEmbed(builder.build()).build();
    }

}
