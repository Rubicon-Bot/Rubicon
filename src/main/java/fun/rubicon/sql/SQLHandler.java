package fun.rubicon.sql;

/**
 * @author Yannick Seeger / ForYaSee
 */
public interface SQLHandler {

    void set(String type, String value);
    String get(String type);
    void createDefaultEntryIfNotExist();
    void createTableIfNotExist();
}
