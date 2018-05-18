package fun.rubicon.entities;

/**
 * @author ForYaSee / Yannick Seeger
 */
public interface Guild extends net.dv8tion.jda.core.entities.Guild {

    String getPrefix();

    void setPrefix(String prefix);
}
