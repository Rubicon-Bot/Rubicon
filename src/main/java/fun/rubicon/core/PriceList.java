package fun.rubicon.core;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public enum PriceList {

    PREMIUM(500000);

    private int price;

    PriceList(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

}
