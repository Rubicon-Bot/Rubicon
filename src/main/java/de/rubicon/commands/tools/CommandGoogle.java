package de.rubicon.commands.tools;

import de.rubicon.command.Command;
import de.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



import java.awt.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;



public class CommandGoogle extends Command{
    private String ttemp = "";
    public CommandGoogle(String command, CommandCategory category) {
        super(command, category);
    }
    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        if (args.length < 1) {e.getTextChannel().sendMessage(getUsage()); return;}
        String query = "";
        for(int i = 0; i < args.length; i++){
            query += " " + args[i];
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

            ttemp = ttemp + link.text().toString() + link.baseUri() +  "\n";

        }
        sendEmbededMessage("Search Results for **" + query + "**:\n" + ttemp);
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUsage() {
        return "google <Search Querry>";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
