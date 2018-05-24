package fun.rubicon.entities.impl;

import fun.rubicon.entities.VerificationSettings;
import fun.rubicon.io.db.RethinkDataset;
import lombok.Getter;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class VerificationSettingsImpl extends RethinkDataset implements VerificationSettings {

    public static transient String TABLE = "verification_settings";

    @Getter
    private String id;
    @Getter
    private String channelId;
    @Getter
    private String kickText;
    @Getter
    private String roleId;

    public VerificationSettingsImpl(){
        super(TABLE);
    }

    @Override
    public void delete() {
        deleteData();

    }
}
