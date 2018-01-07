package fun.rubicon.core.music;

import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class MusicSearchResult {

    private User user;
    private Guild guild;

    private Map<String, String> linkMap;

    private String[] emotes = {
            ":one:",
            ":two_",
            ":three:",
            ":four:",
            ":five:"
    };

    public MusicSearchResult(User user, Guild guild) {
        this.user = user;
        this.guild = guild;

        linkMap = new HashMap<>();
    }

    public void addLink(String title, String link) throws Exception {
        if(linkMap.size() >= 5)
            throw new Exception("No support for more than 5 links.");
        linkMap.put(link, title);
    }

    public EmbedBuilder generateEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Top 5 search results");
        builder.setColor(Colors.COLOR_PRIMARY);
        int i = 0;
        for(Map.Entry entry : linkMap.entrySet()) {
            builder.addField(emotes[i] + " " + entry.getValue(), "[Link](" + entry.getKey() + ")", false);
            i++;
        }
        return builder;
    }

    public User getUser() {
        return user;
    }

    public Guild getGuild() {
        return guild;
    }
}
