package fun.rubicon.core;

<<<<<<< HEAD


=======
import de.rubicon.listener.GuildJoinListener;
import de.rubicon.listener.GuildMemberJoinListener;
>>>>>>> master
import fun.rubicon.listener.CommandListener;
import fun.rubicon.listener.SQLPreventDisconnect;
import fun.rubicon.listener.SelfMentionListener;
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
<<<<<<< HEAD

=======
        b.addEventListener(new GuildJoinListener());
        b.addEventListener(new GuildMemberJoinListener());
        b.addEventListener(new SQLPreventDisconnect());
>>>>>>> master
    }
}