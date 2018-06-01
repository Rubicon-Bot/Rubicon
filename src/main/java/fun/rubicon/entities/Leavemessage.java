package fun.rubicon.entities;

public interface Leavemessage {

    void setChannelId(String channelId);

    String getChannelId();

    String getMessage();

    void setMessage(String message);

    void delete();
}
