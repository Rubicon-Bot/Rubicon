package fun.rubicon.entities;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public interface PortalSettings {

    void setInvites(boolean state);

    void setEmbeds(boolean state);

    boolean getInvites();

    boolean getEmbeds();

    void delete();


}
