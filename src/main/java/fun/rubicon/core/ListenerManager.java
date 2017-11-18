package fun.rubicon.core;

<<<<<<< HEAD
=======
import de.rubicon.listener.GuildJoinListener;
import de.rubicon.listener.GuildMemberJoinListener;
import fun.rubicon.listener.AutoRoleWelcome;
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
        b.addEventListener(new SQLPreventDisconnect());
        b.addEventListener(new AutoRoleWelcome());
    }
}
