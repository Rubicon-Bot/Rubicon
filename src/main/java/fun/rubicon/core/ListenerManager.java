package fun.rubicon.core;

<<<<<<< HEAD:src/main/java/de/rubicon/core/ListenerManager.java
import de.rubicon.listener.CommandListener;
import de.rubicon.listener.GuildJoinListener;
import de.rubicon.listener.GuildMemberJoinListener;
import de.rubicon.listener.SelfMentionListener;
=======
import fun.rubicon.listener.CommandListener;
import fun.rubicon.listener.SelfMentionListener;
>>>>>>> 37a9dbf4ca22449da21f9ea36a6084eef96903ae:src/main/java/fun/rubicon/core/ListenerManager.java
import net.dv8tion.jda.core.JDABuilder;

public class ListenerManager {

    private JDABuilder b;

    public ListenerManager(JDABuilder builder) {
        this.b = builder;
        initListener();
    }

    private void initListener() {
        b.addEventListener(new CommandListener());
        b.addEventListener(new SelfMentionListener());
        b.addEventListener(new GuildJoinListener());
        b.addEventListener(new GuildMemberJoinListener());
    }
}
