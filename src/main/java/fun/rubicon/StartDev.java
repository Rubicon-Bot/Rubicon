package fun.rubicon;

import fun.rubicon.io.Data;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class StartDev {

    public static void main(String[] args) {
        Data.init();
        new RubiconBot();
    }

}
