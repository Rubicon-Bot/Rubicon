package fun.rubicon.entities;

import java.util.List;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public interface Autochannel {

    List<String> getAutoChannels();

    void addChannel(String channelId);

    void removeChannel(String channelId);

    void delete();


}
