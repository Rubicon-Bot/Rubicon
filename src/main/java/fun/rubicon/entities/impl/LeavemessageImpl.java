package fun.rubicon.entities.impl;

import fun.rubicon.entities.Leavemessage;
import fun.rubicon.io.db.RethinkDataset;
import lombok.Getter;

public class LeavemessageImpl extends RethinkDataset implements Leavemessage {

    public static transient String TABLE = "leavemessages";

    @Getter
    private String id;
    @Getter
    private String channelId;
    @Getter
    private String message;

    public LeavemessageImpl() {
        super(TABLE);
    }

    public LeavemessageImpl(String guildId, String channelId, String message) {
        super(TABLE);
        this.channelId = channelId;
        this.id = guildId;
        this.message = message;
        saveData();
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
        return id;
    }

    @Override
    public void delete() {
        deleteData();
    }
}
