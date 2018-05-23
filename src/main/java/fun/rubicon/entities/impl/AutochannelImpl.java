package fun.rubicon.entities.impl;

import fun.rubicon.entities.Autochannel;
import fun.rubicon.io.db.RethinkDataset;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class AutochannelImpl extends RethinkDataset implements Autochannel {

    public static transient String TABLE= "autochannels";

    @Getter
    private String id;
    private List<String> channels;

    public AutochannelImpl() {
        super(TABLE);
    }

    public AutochannelImpl(String guildId, List<String> channels){
        super(TABLE);
        this.channels = channels;
        this.id = guildId;
        saveData();
    }

    public AutochannelImpl(String guildId, String channelId){
        super(TABLE);
        this.id = guildId;
        if(channels == null){
            this.channels = new ArrayList<>();
            this.channels.add(channelId);
        }else
            this.channels.add(channelId);
        saveData();
    }

    @Override
    public void addChannel(String channelId) {
        this.channels.add(channelId);
        saveData();
    }

    @Override
    public List<String> getAutoChannels() {
        return channels;
    }

    @Override
    public void delete() {
        deleteData();
    }
}
