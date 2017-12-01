package fun.rubicon.commands.tools;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
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

public class CommandGoogle extends Command{
    private String ttemp = "";
    public CommandGoogle(String command, CommandCategory category) {
        super(command, category);
    }
    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        if (args.length < 1) {
            e.getTextChannel().sendMessage(getUsage());
            return;
        }

        if (!e.getTextChannel().isNSFW()) {
            sendErrorMessage("Sorry. Google-Search is only allowed in Channels with active NSFW filter ");
            return;
        }

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

        sendEmbededMessage("Search Results for **" + query + "**:\n" + ttemp);
    }

    @Override
    public String getDescription() {
        return "Google (useful) stuff.";
    }

    @Override
    public String getUsage() {
        return "google <Search Query>";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
