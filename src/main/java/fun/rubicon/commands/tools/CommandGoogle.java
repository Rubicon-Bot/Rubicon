package fun.rubicon.commands.tools;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package commands.tools
 */

public class CommandGoogle extends CommandHandler{
    private String ttemp = "";

    public CommandGoogle() {
        super(new String[] {"google"}, CommandCategory.TOOLS, new PermissionRequirements(0, "command.google"), "Google (useful) stuff.", "<Search Query>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.args;
        Message message = parsedCommandInvocation.invocationMessage;
        //check args
        if (args.length < 1) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("Usage","google <Search Query>").build()).build();
        }
        //TODO check if Result is Nsfw
        if (!message.getTextChannel().isNSFW()) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("NSFW", "Sorry. Google-Search is only allowed in Channels with active NSFW filter.").build()).build();
        }
        //Generate query
        String query = "";
        for (String arg : args) {
            query += " " + arg;
        }
        String google = "http://www.google.com/search?q=";
        String search = query;
        String charset = "UTF-8";
        String userAgent = "Rubicon 0.1 (+http://rubicon.fun)";

        Elements links = null;
        try {
            links = Jsoup.connect(google + URLEncoder.encode(search, charset)).userAgent(userAgent).get().select(".g>.r>a");
        } catch (IOException e1) {
            e1.printStackTrace();
            //TODO error handling. links cant be processed if they dont exist.
        }
        //Get Results of Serach to String
        for (Element link : links) {
            String title = link.text();
            String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
            try {
                url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            if (!url.startsWith("http")) {
                continue; // Ads/news/etc.
            }

            ttemp = ttemp + link.text() + link.baseUri() + "\n";

        }
        //Send results
        return new MessageBuilder().setEmbed(EmbedUtil.embed("Search Results for **\" + query + \"**:", ttemp).build()).build();
    }


}
