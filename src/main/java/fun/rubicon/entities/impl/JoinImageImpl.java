package fun.rubicon.entities.impl;

import fun.rubicon.entities.Joinimage;
import fun.rubicon.io.db.RethinkDataset;
import lombok.Getter;

public class JoinImageImpl extends RethinkDataset implements Joinimage {

    public static final transient String TABLE = "joinimages";

    @Getter
    private String channelId;
    private String id;

    public JoinImageImpl() {
        super(TABLE);
    }

    public JoinImageImpl(String guildId, String channelId) {
        super(TABLE);
        this.channelId = channelId;
        this.id = guildId;
        saveData();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setChannelId(String channelId) {
        this.channelId = channelId;
        saveData();
    }


    @Override
    public void delete() {
        deleteData();
    }
}
