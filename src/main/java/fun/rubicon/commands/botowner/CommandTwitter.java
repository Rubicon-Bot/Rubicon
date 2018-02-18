/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * Handles the 'twitter' command.
 *
 * @author Yannick Seeger / ForYaSee
 */
public class CommandTwitter extends CommandHandler {

    public CommandTwitter() {
        super(new String[]{"twitter", "tweet"}, CommandCategory.BOT_OWNER, new PermissionRequirements("command.twitter", true, false), "Tweet with with the bot.", "<message>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(RubiconBot.getConfiguration().getString("twitterConsumerKey"))
                .setOAuthConsumerSecret(RubiconBot.getConfiguration().getString("twitterConsumerSecret"))
                .setOAuthAccessToken(RubiconBot.getConfiguration().getString("twitterAccessToken"))
                .setOAuthAccessTokenSecret(RubiconBot.getConfiguration().getString("twitterAccessTokenSecret"));
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        try {
            Status status = twitter.updateStatus(String.join(" ", parsedCommandInvocation.getArgs()));
            parsedCommandInvocation.getMessage().getTextChannel().sendMessage(new EmbedBuilder()
                    .setDescription(status.getText())
                    .setColor(new Color(0, 153, 229))
                    .setAuthor("Take a look at the tweet on Twitter", "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId(), "http://icons.iconarchive.com/icons/sicons/basic-round-social/512/twitter-icon.png")
                    .setFooter(new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(status.getCreatedAt()), null)
                    .build()).queue();
        } catch (TwitterException e) {
            Logger.debug(e.getErrorMessage());
        }
        return null;
    }
}
