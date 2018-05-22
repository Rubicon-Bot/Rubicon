package fun.rubicon.entities;

public interface Joinmessage {

    String getGuildId();

    void setChannelId(String channelId);

    String getChannelId();

    String getMessage();

    void setMessage(String message);
}
