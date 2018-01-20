package fun.rubicon.core.coinhive;

/**
 * @author Yannick Seeger / ForYaSee
 */
public interface CoinhiveUser {

    String getName();

    int getTotal();

    int getWithdrawn();

    long getBalance();
}
