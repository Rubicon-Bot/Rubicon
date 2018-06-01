package fun.rubicon.entities;

public interface Joinmessage {

    void setChannelId(String channelId);

    String getChannelId();

    String getMessage();

    void setMessage(String message);

    void delete();
}
