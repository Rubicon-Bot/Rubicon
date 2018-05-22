package fun.rubicon.entities.impl;

import fun.rubicon.entities.Joinmessage;
import fun.rubicon.io.db.RethinkDataset;
import lombok.Getter;
import lombok.ToString;

@ToString
public class JoinmessageImpl extends RethinkDataset implements Joinmessage {

    public static final transient String TABLE = "joinmessages";

    @Getter private String guildId;
    @Getter private String channelId;
    @Getter private String message;

    public JoinmessageImpl(String guildId, String channelId, String message) {
        super(TABLE);
        this.guildId = guildId;
        this.channelId = channelId;
        this.message = message;
    }

    public JoinmessageImpl() {
        super(TABLE);
    }

    @Override
    public void setChannelId(String channelId) {
        this.channelId = channelId;
        saveData();
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
        saveData();
    }

    @Override
    public String getId() {
        return guildId;
    }
}
