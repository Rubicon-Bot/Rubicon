package fun.rubicon.entities;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public interface VerificationSettings {

    String getChannelId();

    String getKickText();

    String getRoleId();

    void delete();

}
